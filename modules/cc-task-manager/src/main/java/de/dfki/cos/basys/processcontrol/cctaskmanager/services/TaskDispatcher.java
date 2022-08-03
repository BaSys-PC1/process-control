package de.dfki.cos.basys.processcontrol.cctaskmanager.services;

import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.model.ControlComponentRequestType;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import de.dfki.cos.basys.processcontrol.model.OperationMode;
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

        checkInputParameterTypes(controlComponentRequest);

        return  controlComponentRequest;
    }

    private void checkInputParameterTypes(ControlComponentRequest controlComponentRequest) {
        if (controlComponentRequest.getRequestType() == ControlComponentRequestType.OPERATION_MODE_REQUEST) {
            log.info("checkVariableTypes of operation mode request");
            OperationMode op = (OperationMode) controlComponentRequest.getCommand();
            op.getInputParameters().forEach(variable -> {
                switch (variable.getType()) {
                    case DOUBLE:
                        if (variable.getValue() instanceof Double) {
                            log.debug("Input parameter '{}': specified type '{}' consistent with value '{}' ", variable.getName(), variable.getType(), variable.getValue());
                        } else if (variable.getValue() instanceof String) {
                            log.warn("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected, try to parse... ", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                            variable.setValue(Double.parseDouble(variable.getValue().toString()));
                        } else {
                            log.error("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected!", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                        }
                        break;
                    case INTEGER:
                        if (variable.getValue() instanceof Integer) {
                            log.debug("Input parameter '{}': specified type '{}' consistent with value '{}' ", variable.getName(), variable.getType(), variable.getValue());
                        } else if (variable.getValue() instanceof String) {
                            log.warn("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected, try to parse... ", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                            variable.setValue(Integer.parseInt(variable.getValue().toString()));
                        } else {
                            log.error("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected!", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                        }
                        break;
                    case LONG:
                        if (variable.getValue() instanceof Long) {
                            log.debug("Input parameter '{}': specified type '{}' consistent with value '{}' ", variable.getName(), variable.getType(), variable.getValue());
                        } else if (variable.getValue() instanceof String) {
                            log.warn("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected, try to parse... ", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                            variable.setValue(Long.parseLong(variable.getValue().toString()));
                        } else {
                            log.error("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected!", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                        }
                        break;
                    case BOOLEAN:
                        if (variable.getValue() instanceof Boolean) {
                            log.debug("Input parameter '{}': specified type '{}' consistent with value '{}' ", variable.getName(), variable.getType(), variable.getValue());
                        } else if (variable.getValue() instanceof String) {
                            log.warn("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected, try to parse... ", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                            variable.setValue(Boolean.parseBoolean(variable.getValue().toString()));
                        } else {
                            log.error("Input parameter '{}': specified type '{}' NOT consistent with value '{}', {} detected!", variable.getName(), variable.getType(), variable.getValue(), variable.getValue().getClass());
                        }
                        break;
                    case STRING:
                    case DATE:
                    case NULL:
                    default:
                        break;
                }
            });
        }

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
