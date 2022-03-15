package de.dfki.cos.basys.processcontrol.cctaskmanager.services;

import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.util.Utf8;
import org.eclipse.basyx.aas.registry.events.RegistryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
public class TaskDispatcher {

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private AasRegistryQueries aasRegistryQueries;

    @Bean
    public Function<ControlComponentRequest,ControlComponentRequest> controlComponentRequests() {
        return this::handleComponentRequest;
    }


    private ControlComponentRequest handleComponentRequest(ControlComponentRequest controlComponentRequest) {
        log.info("received {} for {}", controlComponentRequest.getRequestType(), controlComponentRequest.getComponentId());
        log.debug(controlComponentRequest.toString());
        if (controlComponentRequest.getAasId() == null) {
            if (controlComponentRequest.getComponentId() == null) {
                // TODO: find appropriate and available cc
                //controlComponentRequest.setAasId("null");
            } else {
                String aasId = aasRegistryQueries.searchAasIdentifierByAssetName(controlComponentRequest.getComponentId().toString());
                controlComponentRequest.setAasId(aasId);
            }
        } else {
            // TODO: check if cc is available, find replacement otherwise
        }


        return  controlComponentRequest;
    }

//    @Bean
//    public Consumer<RegistryEvent> aasRegistryUpdates() {
//        return this::handleAasRegistryUpdates;
//    }
//
//    private void handleAasRegistryUpdates(RegistryEvent registryEvent) {
//        log.info("received registry event");
//        log.debug(registryEvent.toString());
//    }
//
//    @Bean
//    public Consumer<ControlComponentRequest> controlComponentOrders() {
//        return this::handleComponentOrder;
//    }
//
//    private void handleComponentOrder(ControlComponentRequest controlComponentRequest) {
//        log.info("received {} for {}", controlComponentRequest.getRequestType(), controlComponentRequest.getComponentId());
//        log.debug(controlComponentRequest.toString());
//    }


}
