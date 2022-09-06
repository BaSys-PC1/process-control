package de.dfki.cos.basys.processcontrol.taskchannel.camunda.wallet.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.processcontrol.model.*;
import de.wallet.model.Lift;
import de.wallet.model.Plan;
import de.wallet.model.Request;
import de.wallet.model.custom.*;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class WalletExternalTaskWorker implements ExternalTaskHandler {

	protected int maxRetryCount = 0;
	protected int retryTimeout = 1000;

	@Autowired
	private StreamBridge streamBridge;

	@Autowired
	private ObjectMapper mapper;

	private ExternalTaskService externalTaskService;

	private Map<String, ExternalTask> externalTasks = new HashMap<>();

	protected Plan plan;

	public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
		// the externalTaskService is only created once for all externalTasks. So we store it here the first time.		
		if (this.externalTaskService == null) {
			this.externalTaskService = externalTaskService;
		}
			
		ControlComponentRequest request = createControlComponentRequest(externalTask);
		if (request != null) {
			externalTasks.put(externalTask.getId(), externalTask);
			streamBridge.send("controlComponentRequests", request);
		}
	}

	public ControlComponentRequest createControlComponentRequest(ExternalTask externalTask) {
		ControlComponentRequest ccRequest = null;
		Double liftHeight = null;

		ObjectValue planObjVal = externalTask.getVariableTyped("plan", false);
		String value = "{\"Plan\" : " + planObjVal.getValueSerialized() + "}";
		plan = (Plan) Utils.makeJsonToObject(value, Plan.class);

		Request request = plan.getRequests().get(0);
		Command command = null;
		if(request.getCommand().contains(LiftGroundLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftGroundLevelCommand.class);
		} else if(request.getCommand().contains(LiftFirstLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftFirstLevelCommand.class);
		} else if (request.getCommand().contains(LiftSecondLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftSecondLevelCommand.class);
		} else if (request.getCommand().contains(LiftThirdLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftThirdLevelCommand.class);
		} /* else if (request.getCommand().contains(LiftHeightCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftHeightCommand.class);
		}*/

		command.execute();
		if(command.getReceiver() instanceof Lift) {
			Lift receiver = (Lift)command.getReceiver();
			//liftHeight = receiver.getHeight();
			plan.setResult(Utils.getJsonFormObject(receiver));
		}

		Variable levelVar = Variable.newBuilder().setName("height").setType(VariableType.DOUBLE).setValue(liftHeight).build();

		ccRequest = new ControlComponentRequest();
		ccRequest.setRequestType(ControlComponentRequestType.OPERATION_MODE_REQUEST);
		OperationMode opMode = new OperationMode();
		opMode.setInputParameters(Collections.singletonList(levelVar));
		opMode.setName("LIFT");
		ccRequest.setOperationMode(opMode);
		ccRequest.setComponentId("wallet-1");
		ccRequest.setCorrelationId(externalTask.getId());
		ccRequest.setOccupierId(externalTask.getProcessInstanceId());
		return ccRequest;
	}

	protected ControlComponentRequest _createControlComponentRequest(ExternalTask externalTask) {
		ControlComponentRequest r = new ControlComponentRequest();

		String requestType = externalTask.getVariable("requestType");
		if (requestType == null) {
			externalTaskService.handleFailure(externalTask, "No requestType", "ExternalTask does not contain a requestType", maxRetryCount, retryTimeout);
			return null;
		}

		String componentId = externalTask.getVariable("componentId");
		if (componentId == null) {
			externalTaskService.handleFailure(externalTask, "No componentId", "ExternalTask does not contain a componentId", maxRetryCount, retryTimeout);
			return null;
		}

		String token = externalTask.getVariable("token");
		if (token == null) {
			externalTaskService.handleFailure(externalTask, "No token", "ExternalTask does not contain a token", maxRetryCount, retryTimeout);
			return null;
		}

		switch (requestType) {
			case "ExecutionCommandRequest":
				r.setRequestType(ControlComponentRequestType.EXECUTION_COMMAND_REQUEST);
				r.setExecutionCommand(ExecutionCommand.valueOf(token));
				break;
			case "ExecutionModeRequest":
				r.setRequestType(ControlComponentRequestType.EXECUTION_MODE_REQUEST);
				r.setExecutionMode(ExecutionMode.valueOf(token));
				break;
			case "OccupationCommandRequest":
				r.setRequestType(ControlComponentRequestType.OCCUPATION_COMMAND_REQUEST);
				r.setOccupationCommand(OccupationCommand.valueOf(token));
				break;
			case "OperationModeRequest":
				r.setRequestType(ControlComponentRequestType.OPERATION_MODE_REQUEST);
				OperationMode opMode = new OperationMode();
				r.setOperationMode(opMode);

				opMode.setName(token);
				try {

					String input = externalTask.getVariable("inputParameters");
					String output = externalTask.getVariable("outputParameters");

					if (input != null) {
						List<Variable> inputParameters = mapper.readValue(input,new TypeReference<>(){});
						log.debug(inputParameters.toString());
						opMode.setInputParameters(inputParameters);
					}
					if (output != null) {
						List<Variable> outputParameters = mapper.readValue(output,new TypeReference<>(){});
						log.debug(outputParameters.toString());
						opMode.setOutputParameters(outputParameters);
					}

				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
		}

		r.setCorrelationId(externalTask.getId());
		r.setOccupierId(externalTask.getProcessInstanceId());
		r.setComponentId(componentId);

		return r;
	}

	private void handleComponentResponse(ControlComponentResponse response) {
		ExternalTask externalTask = externalTasks.remove(response.getRequest().getCorrelationId());
		if (externalTask != null) {
			if (response.getStatus() == RequestStatus.OK) {
				if (response.getOutputParameters().size() > 0) {
					Map<String, Object> variables = new HashMap<>();
					for (Variable var : response.getOutputParameters()) {
						variables.put(var.getName().toString(), var.getValue());
					}
					externalTaskService.complete(externalTask, variables);
				} else {

					externalTaskService.complete(externalTask);
				}
			} else {
				externalTaskService.handleFailure(externalTask, response.getMessage().toString(), "", maxRetryCount, retryTimeout);
			}
		} else {
			//do nothing; this worker instance has not issued that control component request
		}
	}

	@Bean
	public Consumer<ControlComponentResponse> controlComponentResponse() {
		return this::handleComponentResponse;
	}

}
