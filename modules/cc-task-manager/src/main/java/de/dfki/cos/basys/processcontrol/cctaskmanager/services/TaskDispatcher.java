package de.dfki.cos.basys.processcontrol.cctaskmanager.services;

import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.basyx.aas.registry.events.RegistryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class TaskDispatcher {

    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Consumer<ControlComponentRequest> controlComponentRequests() {
        return this::handleComponentRequest;
    }

    @Bean
    public Consumer<RegistryEvent> aasRegistryUpdates() {
        return this::handleAasRegistryUpdates;
    }

    private void handleAasRegistryUpdates(RegistryEvent registryEvent) {
        log.info("received registry event");
        log.debug(registryEvent.toString());
    }

    private void handleComponentRequest(ControlComponentRequest controlComponentRequest) {
        log.info("received {} for {}", controlComponentRequest.getRequestType(), controlComponentRequest.getComponentId());
        log.debug(controlComponentRequest.toString());
    }

}
