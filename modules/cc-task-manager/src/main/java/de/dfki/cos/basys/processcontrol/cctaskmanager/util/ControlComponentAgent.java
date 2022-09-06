package de.dfki.cos.basys.processcontrol.cctaskmanager.util;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.common.component.StringConstants;
import de.dfki.cos.basys.controlcomponent.*;
import de.dfki.cos.basys.controlcomponent.client.ControlComponentClient;
import de.dfki.cos.basys.controlcomponent.client.ControlComponentClientImpl;
import de.dfki.cos.basys.controlcomponent.packml.PackMLWaitStatesHandler;
import de.dfki.cos.basys.processcontrol.cctaskmanager.services.ControlComponentAgentCallback;
import de.dfki.cos.basys.processcontrol.model.*;
import de.dfki.cos.basys.processcontrol.model.ExecutionCommand;
import de.dfki.cos.basys.processcontrol.model.ExecutionMode;
import de.dfki.cos.basys.processcontrol.model.OccupationCommand;
import de.dfki.cos.basys.processcontrol.model.OperationMode;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ControlComponentAgent implements PackMLWaitStatesHandler /*, StatusInterface*/ {

    protected ControlComponentClientImpl controlComponentClient = null;
    protected ControlComponentAgentCallback callback = null;
    private ControlComponentRequest currentRequest = null;

    private Properties config = null;

    private boolean isActive = false;

    public ControlComponentAgent(Properties config, ControlComponentAgentCallback callback) {
        this.config = config;
        this.controlComponentClient = new ControlComponentClientImpl(config, this);
        this.callback = callback;
    }

    public boolean activate() {
        String connectionString = config.getProperty("connectionString");
        isActive = controlComponentClient.connect(ComponentContext.getStaticContext(), connectionString);
        return isActive();
    }

    public void deactivate() {
        if (controlComponentClient.isConnected()) {
            isActive = false; // prevents calls from outside when deactivation is ongoing
            //TODO: think of more graceful way for shutdown; check cc state; if not stopped/complete -> wait
            if (controlComponentClient.getExecutionState() != ExecutionState.COMPLETE
                    || controlComponentClient.getExecutionState() != ExecutionState.STOPPED) {
                controlComponentClient.stop();
            }
            controlComponentClient.disconnect();
        }
    }

    public boolean isActive() {
        return isActive && controlComponentClient.isConnected();
    }

    public ControlComponentRequestStatus handleControlComponentRequest(ControlComponentRequest request) {
        ControlComponentRequestStatus status = null;

        if (!isActive()) {
            status = new ControlComponentRequestStatus();
            status.setStatus(RequestStatus.REJECTED);
            status.setMessage("agent not active");
            return status;
        }

        switch (request.getRequestType()) {
            case OCCUPATION_COMMAND_REQUEST:
                status = handleOccupationCommandRequest(request);
                break;
            case EXECUTION_MODE_REQUEST:
                status = handleExecutionModeRequest(request);
                break;
            case EXECUTION_COMMAND_REQUEST:
                status = handleExecutionCommandRequest(request);
                break;
            case OPERATION_MODE_REQUEST:
                status = handleOperationModeRequest(request);
                break;
            default:
                break;
        }
        return status;
    }

    protected ControlComponentRequestStatus handleExecutionCommandRequest(ControlComponentRequest req) {
        log.info("handleExecutionCommandRequest '{}' (businessKey = {})", req.getExecutionCommand(), req.getOccupierId());

        ComponentOrderStatus order = controlComponentClient.raiseExecutionCommand(de.dfki.cos.basys.controlcomponent.ExecutionCommand.get(req.getExecutionCommand().name()));

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info("handleExecutionCommandRequest '{}' - finished", req.getExecutionCommand());
        return status;
    }

    protected ControlComponentRequestStatus handleExecutionModeRequest(ControlComponentRequest req) {
        log.info("handleExecutionModeRequest '{}' (businessKey = {})", req.getExecutionMode(), req.getOccupierId());

        ControlComponentClient client = controlComponentClient;
        ComponentOrderStatus order = client.setExecutionMode(de.dfki.cos.basys.controlcomponent.ExecutionMode.get(req.getExecutionMode().name()));

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info("handleExecutionModeRequest '{}' - finished", req.getExecutionMode());
        return status;
    }

    protected ControlComponentRequestStatus handleOccupationCommandRequest(ControlComponentRequest req) {
        log.info("handleOccupationCommandRequest '{}' (businessKey = {})", req.getOccupationCommand(), req.getOccupierId());

        ComponentOrderStatus order = controlComponentClient.occupy(de.dfki.cos.basys.controlcomponent.OccupationCommand.get(req.getOccupationCommand().name()));

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info("handleOccupationCommandRequest '{}' - finished", req.getOccupationCommand());
        return status;
    }

    protected ControlComponentRequestStatus handleOperationModeRequest(ControlComponentRequest req) {
        log.info("handleOperationModeRequest '{}' (businessKey = {})", req.getOperationMode().getName(), req.getOccupierId());

        /*
         * Prerequisites:
         *  - free
         *  - stopped or completed execution state
         *
         * Steps to perform:
         *  - occupy
         *  - reset
         *  - [wait for IDLE state]
         *  - set operation mode
         *  - set input parameters
         *  - start
         *  - [wait for COMPLETED or STOPPED state]
         *  - read output parameters
         *  - send response to process
         *  - free
         */

        ComponentOrderStatus order = controlComponentClient.occupy();
        if (order.getStatus() == OrderStatus.DONE) {
            log.info("caching current request");
            currentRequest = req;
            log.debug(currentRequest.toString());
            order = controlComponentClient.reset();
        }

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info("handleOperationModeRequest '{}' - finished", (req.getOperationMode().getName()));
        return status;
    }

    @Override
    public void onIdle() {
        log.info("onIdle - start");
        if (currentRequest != null) {
            OperationMode opMode = currentRequest.getOperationMode();
            try {
                ComponentOrderStatus status = ComponentContext.getStaticContext().getScheduledExecutorService().submit(() -> {
                    ComponentOrderStatus status1 = controlComponentClient.setOperationMode((String) opMode.getName());
                    if (status1.getStatus() == OrderStatus.DONE) {
                        log.debug("set operation mode to {}", opMode.getName());
                        for (Variable var : opMode.getInputParameters()) {
                            //TODO: put switch block into Variable class, test date parsing and setting via opcua
                            controlComponentClient.setParameterValue((String) var.getName(), var.getValue());
                        }
                        status1 = controlComponentClient.start();
                    }
                    return status1;

                }).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(),e);
            }
        } else {
            log.warn("currentRequest is null");
        }
        log.info("onIdle - finished");
    }

    @Override
    public void onComplete() {
        log.info("onComplete - start");
        if (currentRequest != null) {
            OperationMode opMode = currentRequest.getOperationMode();
            try {
                ComponentOrderStatus status = ComponentContext.getStaticContext().getScheduledExecutorService().submit(() -> {
                    ComponentOrderStatus status1 = controlComponentClient.free();

                    ControlComponentResponse response = new ControlComponentResponse();
                    response.setRequest(currentRequest);
                    response.setComponentId(currentRequest.getComponentId());
                    response.setAasId(currentRequest.getAasId());
                    response.setCorrelationId(currentRequest.getCorrelationId());
                    response.setStatus(RequestStatus.OK);
                    response.setStatusCode(controlComponentClient.getErrorCode());
                    response.setOutputParameters(new ArrayList<>(opMode.getOutputParameters().size()));

                    for (Variable var : opMode.getOutputParameters()) {
                        ParameterInfo p = controlComponentClient.getParameter(var.getName().toString());
                        Variable outVar = new Variable(var.getName(), p.getValue(), var.getType());
                        response.getOutputParameters().add(outVar);
                        if (var.getType() != VariableTypeHelper.fromOpcUa(p.getType())) {
                            log.warn("output parameter {} : retrieved type {} does not match expected type {}!", var.getName(), VariableTypeHelper.fromOpcUa(p.getType()), p.getType());
                        }
                    }

                    callback.onControlComponentResponse(response);
                    log.info("nulling current request");
                    currentRequest = null;
                    return status1;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(),e);
            }
        } else {
            log.warn("currentRequest is null");
        }
        log.info("onComplete - finished");
    }

    @Override
    public void onStopped() {
        log.info("onStopped - start");
        if (currentRequest != null) {
            OperationMode opMode = currentRequest.getOperationMode();
            try {
                ComponentOrderStatus status = ComponentContext.getStaticContext().getScheduledExecutorService().submit(() -> {
                    ComponentOrderStatus status1 = controlComponentClient.free();

                    ControlComponentResponse response = new ControlComponentResponse();
                    response.setRequest(currentRequest);
                    response.setComponentId(currentRequest.getComponentId());
                    response.setAasId(currentRequest.getAasId());
                    response.setCorrelationId(currentRequest.getCorrelationId());
                    response.setStatus(RequestStatus.NOT_OK);
                    response.setStatusCode(controlComponentClient.getErrorCode());
                    response.setOutputParameters(new ArrayList<>(opMode.getOutputParameters().size()));

                    for (Variable var : opMode.getOutputParameters()) {
                        ParameterInfo p = controlComponentClient.getParameter(var.getName().toString());
                        Variable outVar = new Variable(var.getName(), p.getValue(), var.getType());
                        response.getOutputParameters().add(outVar);
                        if (var.getType() != VariableTypeHelper.fromOpcUa(p.getType())) {
                            log.warn("output parameter {} : retrieved type {} does not match expected type {}!", var.getName(), VariableTypeHelper.fromOpcUa(p.getType()), p.getType());
                        }
                    }

                    callback.onControlComponentResponse(response);

                    log.info("nulling current request");
                    currentRequest = null;
                    return status1;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(),e);
            }
        } else {
            log.warn("currentRequest is null");
        }
        log.info("onStopped - finished");
    }

    @Override
    public void onHeld() {
        if (currentRequest != null) {

        }
    }

    @Override
    public void onSuspended() {
        if (currentRequest != null) {

        }
    }

    @Override
    public void onAborted() {
        if (currentRequest != null) {

        }
    }
/*
    @Override
    public OccupationStatus getOccupationStatus() {
        return controlComponentClient.getOccupationStatus();
    }

    @Override
    public OccupationState getOccupationState() {
        return controlComponentClient.getOccupationState();
    }

    @Override
    public String getOccupierId() {
        return controlComponentClient.getOccupierId();
    }

    @Override
    public List<OperationModeInfo> getOperationModes() {
        return controlComponentClient.getOperationModes();
    }

    @Override
    public OperationModeInfo getOperationMode() {
        return controlComponentClient.getOperationMode();
    }

    @Override
    public String getWorkState() {
        return controlComponentClient.getWorkState();
    }

    @Override
    public ErrorStatus getErrorStatus() {
        return controlComponentClient.getErrorStatus();
    }

    @Override
    public String getErrorMessage() {
        return controlComponentClient.getErrorMessage();
    }

    @Override
    public int getErrorCode() {
        return controlComponentClient.getErrorCode();
    }

    @Override
    public ExecutionState getExecutionState() {
        return controlComponentClient.getExecutionState();
    }

    @Override
    public de.dfki.cos.basys.controlcomponent.ExecutionMode getExecutionMode() {
        return controlComponentClient.getExecutionMode();
    }
 */
}