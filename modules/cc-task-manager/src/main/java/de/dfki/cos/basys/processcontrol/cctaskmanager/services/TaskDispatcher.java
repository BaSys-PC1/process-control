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
import java.util.function.Function;

@Service
@Slf4j
public class TaskDispatcher {

    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Function<ControlComponentRequest,ControlComponentRequest> controlComponentRequests() {
        return this::handleComponentRequest;
    }

    @Bean
    public Consumer<RegistryEvent> aasRegistryUpdates() {
        return this::handleAasRegistryUpdates;
    }

    @Bean
    public Consumer<ControlComponentRequest> controlComponentOrders() {
        return this::handleComponentOrder;
    }

    private void handleAasRegistryUpdates(RegistryEvent registryEvent) {
        log.info("received registry event");
        log.debug(registryEvent.toString());
    }

    private ControlComponentRequest handleComponentRequest(ControlComponentRequest controlComponentRequest) {
        log.info("received {} for {}", controlComponentRequest.getRequestType(), controlComponentRequest.getComponentId());
        log.debug(controlComponentRequest.toString());
        if (controlComponentRequest.getComponentId() == null) {
            // TODO: find appropriate cc
            controlComponentRequest.setComponentId("null");
        } else {
            // TODO: check if cc is available, find replacement otherwise
        }
        return  controlComponentRequest;
    }

    private void handleComponentOrder(ControlComponentRequest controlComponentRequest) {
        log.info("received {} for {}", controlComponentRequest.getRequestType(), controlComponentRequest.getComponentId());
        log.debug(controlComponentRequest.toString());
    }


}
