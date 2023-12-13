package de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.processcontrol.model.*;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Service
public class WGSExternalTaskWorker implements ExternalTaskHandler {

	protected int maxRetryCount = 0;
	protected int retryTimeout = 1000;

	protected int secondsToSleep = 3;

	@Autowired
	private StreamBridge streamBridge;

	@Autowired
	private ObjectMapper mapper;

	private ExternalTaskService externalTaskService;

	private Map<CharSequence, ExternalTask> externalTasks = new HashMap<>();

	public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
		// the externalTaskService is only created once for all externalTasks. So we store it here the first time.		
		if (this.externalTaskService == null) {
			this.externalTaskService = externalTaskService;
		}

		StepChange request = createStepChangeRequest(externalTask);

		if (request != null) {
			externalTasks.put(request.getWorkstepId(), externalTask);
			log.info("new request arrived for WGS Dashboard, workstepId: {}",  request.getWorkstepId());
			if (log.isDebugEnabled()) {
				log.debug(externalTask.toString());
				log.debug(request.toString());
			}
			streamBridge.send("stepChange", request);

			//TODO: Wait for response instead (see CamundaExternalTaskWorker)
			try {
				TimeUnit.SECONDS.sleep(secondsToSleep);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			externalTasks.remove(request.getWorkstepId());
			externalTaskService.complete(externalTask);
		}
	}

	protected StepChange createStepChangeRequest(ExternalTask externalTask) {
		StepChange sc = new StepChange();

		String workstepId = externalTask.getVariable("workstepId");
		if (workstepId == null) {
			externalTaskService.handleFailure(externalTask, "No workstepId", "ExternalTask does not contain a workstepId", maxRetryCount, retryTimeout);
			return null;
		}

		String token = externalTask.getVariable("componentId");
		if (token == null) {
			externalTaskService.handleFailure(externalTask, "No token", "ExternalTask does not contain a componentId", maxRetryCount, retryTimeout);
			return null;
		}

		sc.setWorkstepId(externalTask.getVariable("workstepId"));

		return sc;
	}

}
