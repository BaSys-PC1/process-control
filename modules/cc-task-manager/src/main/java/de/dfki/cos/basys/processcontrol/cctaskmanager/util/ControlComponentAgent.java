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
        log.info(String.format("handleExecutionCommandRequest '%s' (businessKey = %s)", req.getCommand(), req.getOccupierId()));

        ComponentOrderStatus order = controlComponentClient.raiseExecutionCommand(de.dfki.cos.basys.controlcomponent.ExecutionCommand.get(((ExecutionCommand) req.getCommand()).name()));

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info(String.format("handleExecutionCommandRequest '%s' - finished", req.getCommand()));
        return status;
    }

    protected ControlComponentRequestStatus handleExecutionModeRequest(ControlComponentRequest req) {
        log.info(String.format("handleExecutionModeRequest '%s' (businessKey = %s)", req.getCommand(), req.getOccupierId()));

        ControlComponentClient client = controlComponentClient;
        ComponentOrderStatus order = client.setExecutionMode(de.dfki.cos.basys.controlcomponent.ExecutionMode.get(((ExecutionMode) req.getCommand()).name()));

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info(String.format("handleExecutionModeRequest '%s' - finished", req.getCommand()));
        return status;
    }

    protected ControlComponentRequestStatus handleOccupationCommandRequest(ControlComponentRequest req) {
        log.info(String.format("handleOccupationCommandRequest '%s' (businessKey = %s)", req.getCommand(), req.getOccupierId()));

        ComponentOrderStatus order = controlComponentClient.occupy(de.dfki.cos.basys.controlcomponent.OccupationCommand.get(((OccupationCommand) req.getCommand()).name()));

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info(String.format("handleOccupationCommandRequest '%s' - finished", req.getCommand()));
        return status;
    }

    protected ControlComponentRequestStatus handleOperationModeRequest(ControlComponentRequest req) {
        log.info(String.format("handleOperationModeRequest '%s' (businessKey = %s)", ((OperationMode) req.getCommand()).getName(), req.getOccupierId()));

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
            currentRequest = req;
            order = controlComponentClient.reset();
        }

        ControlComponentRequestStatus status = new ControlComponentRequestStatus();
        status.setStatus(RequestStatus.valueOf(order.getStatus().getLiteral()));
        status.setMessage(order.getMessage());

        log.info(String.format("handleOperationModeRequest '%s' - finished", ((OperationMode) req.getCommand()).getName()));
        return status;
    }

    @Override
    public void onIdle() {
        if (currentRequest != null) {
            OperationMode opMode = (OperationMode) currentRequest.getCommand();
            try {
                ComponentOrderStatus status = ComponentContext.getStaticContext().getScheduledExecutorService().submit(new Callable<ComponentOrderStatus>() {
                    @Override
                    public ComponentOrderStatus call() throws Exception {
                        ComponentOrderStatus status = controlComponentClient.setOperationMode(opMode.getName().toString());
                        if (status.getStatus() == OrderStatus.DONE) {
                            for (Variable var : opMode.getInputParameters()) {
                                //TODO: put switch block into Variable class, test date parsing and setting via opcua
                                controlComponentClient.setParameterValue(var.getName().toString(), var.getValue());
                            }
                            status = controlComponentClient.start();
                        }
                        return status;

                    }
                }).get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onComplete() {
        if (currentRequest != null) {
            OperationMode opMode = (OperationMode) currentRequest.getCommand();
            try {
                ComponentOrderStatus status = ComponentContext.getStaticContext().getScheduledExecutorService().submit(new Callable<ComponentOrderStatus>() {
                    @Override
                    public ComponentOrderStatus call() throws Exception {
                        ComponentOrderStatus status = controlComponentClient.free();

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

                        currentRequest = null;
                        return status;
                    }
                }).get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onStopped() {
        if (currentRequest != null) {
            OperationMode opMode = (OperationMode) currentRequest.getCommand();
            try {
                ComponentOrderStatus status = ComponentContext.getStaticContext().getScheduledExecutorService().submit(new Callable<ComponentOrderStatus>() {
                    @Override
                    public ComponentOrderStatus call() throws Exception {
                        ComponentOrderStatus status = controlComponentClient.free();

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

                        currentRequest = null;
                        return status;
                    }
                }).get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
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