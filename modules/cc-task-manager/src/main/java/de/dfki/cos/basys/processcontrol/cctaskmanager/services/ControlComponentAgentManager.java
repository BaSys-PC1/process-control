package de.dfki.cos.basys.processcontrol.cctaskmanager.services;

import de.dfki.cos.basys.processcontrol.cctaskmanager.util.ControlComponentAgent;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.network.Mode;
import org.eclipse.basyx.aas.registry.events.RegistryEvent;
import org.eclipse.basyx.aas.registry.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class ControlComponentAgentManager implements ControlComponentAgentCallback {

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Value("basys.ccagent-manager.ccinstance-submodel-semantic-id:https://wiki.eclipse.org/BaSyx_/_Submodels#Control_Component_Instance")
    private String ccInstanceSubmodelSemanticId;

    private Map<String, ControlComponentAgent> agents = new HashMap<>();
    private Map<String, SubmodelDescriptor> smDescriptors = new HashMap<>();

    @Override
    public void onControlComponentResponse(ControlComponentResponse response) {
        streamBridge.send("controlComponentResponses", response);
    }

    private void handleAasRegistryUpdates(RegistryEvent event) {
        log.info("new AAS registry event: " + event.getType());
        switch (event.getType()) {
            case AAS_REGISTERED:
                //TODO: ignore for now
                break;
            case AAS_UNREGISTERED:
                //TODO: ignore for now
                break;
            case SUBMODEL_REGISTERED:
                if (event.getSubmodelDescriptor() != null) {
                    Reference ref = event.getSubmodelDescriptor().getSemanticId();
                    if (ref instanceof ModelReference) {
                        ModelReference modelReference = (ModelReference) ref;
                        Key key = modelReference.getKeys().get(0);
                        if (key.getType() == KeyElements.CONCEPTDESCRIPTION && ccInstanceSubmodelSemanticId.equals(key.getValue())) {
                            registerControlComponentAgent(event.getSubmodelDescriptor());
                        }
                    }
                }
                break;
            case SUBMODEL_UNREGISTERED:
                unregisterControlComponentAgent(event.getSubmodelId());
                break;
            default:
                break;
        }
    }

    private void registerControlComponentAgent(SubmodelDescriptor smDescriptor) {
        if (agents.containsKey(smDescriptor.getIdentification())) {
            // TODO: probably, we should check for different endpoint information and unregister the agent. For now, we do nothing.
            log.info("ControlComponentAgent for submodelId {} (IdShort: {}) already registered", smDescriptor.getIdentification(), smDescriptor.getIdShort());
            return;
        }

        if (agents.containsKey(smDescriptor.getIdentification())) {
            log.info("register ControlComponentAgent for submodelId {} (IdShort: {})", smDescriptor.getIdentification(), smDescriptor.getIdShort());
            smDescriptors.put(smDescriptor.getIdentification(), smDescriptor);
            ControlComponentAgent agent = new ControlComponentAgent(null);
            agent.activate();
            agents.put(smDescriptor.getIdentification(), agent);
            //TODO: create, host and register agent submodel
        }

    }

    private void unregisterControlComponentAgent(String smIdentifier) {
        if (agents.containsKey(smIdentifier)) {
            SubmodelDescriptor smDescriptor = smDescriptors.remove(smIdentifier);
            ControlComponentAgent agent = agents.remove(smIdentifier);
            log.info("unregister ControlComponentAgent for submodelId {} (IdShort: {})", smDescriptor.getIdentification(), smDescriptor.getIdShort());
            //TODO: delete and unregister agent submodel
            agent.deactivate();
        }
    }


    @Bean
    public Consumer<RegistryEvent> aasRegistryUpdates() {
        return this::handleAasRegistryUpdates;
    }



}
