package de.dfki.cos.basys.processcontrol.cctaskmanager.services;

import de.dfki.cos.basys.processcontrol.cctaskmanager.util.ControlComponentAgent;
import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import de.dfki.cos.basys.processcontrol.model.RequestStatus;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import de.dfki.cos.basys.aas.registry.events.RegistryEvent;
import de.dfki.cos.basys.aas.registry.model.*;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import springfox.documentation.schema.ModelReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
@Slf4j
public class ControlComponentAgentManager implements ControlComponentAgentCallback {

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private AasRegistryQueries aasRegistryServices;

    @Autowired
    private IAssetAdministrationShellManager aasManager;

    @Value("${basys.semanticIds.ccinstanceSubmodel}")
    private String ccInstanceSubmodelSemanticId;

    private Map<String, ControlComponentAgent> agents = new HashMap<>();
    private Map<String, SubmodelDescriptor> smDescriptors = new HashMap<>();

    @PostConstruct
    public void initialize() {
        List<AssetAdministrationShellDescriptor> result = aasRegistryServices.searchAasDescriptorsWithSubmodel(ccInstanceSubmodelSemanticId);
        Key instanceKey = new Key().type(KeyTypes.CONCEPTDESCRIPTION).value(ccInstanceSubmodelSemanticId);
        for (var aasDescriptor: result) {
            var opt = aasDescriptor.getSubmodelDescriptors().stream().filter(smd -> smd.getSemanticId().getKeys().contains(instanceKey)).findFirst();
            opt.ifPresent(smDescriptor -> {
                registerControlComponentAgent(aasDescriptor.getIdentification(), smDescriptor);
            });
        }
    }

    @PreDestroy
    public void dispose() {
        agents.values().parallelStream().forEach(ControlComponentAgent::deactivate);
    }

    @Override
    public void onControlComponentResponse(ControlComponentResponse response) {
        streamBridge.send("controlComponentResponses", response);
    }

    @Bean
    public Consumer<RegistryEvent> aasRegistryUpdates() {
        return this::handleAasRegistryUpdates;
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
                    Key key = ref.getKeys().get(0);
                    if (key.getType() == KeyTypes.CONCEPTDESCRIPTION && ccInstanceSubmodelSemanticId.equals(key.getValue())) {
                        registerControlComponentAgent(event.getId(), event.getSubmodelDescriptor());
                    }
                }
                break;
            case SUBMODEL_UNREGISTERED:
                unregisterControlComponentAgent(event.getId(), event.getSubmodelId());
                break;
            default:
                break;
        }
    }

    private void registerControlComponentAgent(String aasId, SubmodelDescriptor smDescriptor) {
        if (agents.containsKey(aasId)) {
            // TODO: probably, we should check for different endpoint information and unregister the agent. For now, we do nothing.
            log.info("ControlComponentAgent for submodelId {} (IdShort: {}) already registered", smDescriptor.getIdentification(), smDescriptor.getIdShort());
            return;
        }

        if (!agents.containsKey(aasId)) {
            log.info("register ControlComponentAgent for submodelId {} (IdShort: {})", smDescriptor.getIdentification(), smDescriptor.getIdShort());

            ISubmodel instanceSubmodel = aasManager.retrieveSubmodel(new Identifier(IdentifierType.CUSTOM, aasId), new Identifier(IdentifierType.CUSTOM, smDescriptor.getIdentification()));
            ISubmodelElementCollection endpointCollection = (ISubmodelElementCollection) instanceSubmodel.getSubmodelElement("EndpointDescriptions");
            Map<String, ISubmodelElement> endpoints =  endpointCollection.getSubmodelElements();

            var transportProfileValue = "http://opcfoundation.org/UA-Profile/Transport/uatcp-uasc-uabinary";
            var securityPolicyValue = "http://opcfoundation.org/UA/SecurityPolicy#Basic256Sha256";
            var profileValue = 4;
            AtomicReference<String> connectionStringValue = new AtomicReference<>("");
            AtomicReference<String> nodeIdValue = new AtomicReference<>("");;

            endpoints.values().stream().forEach(iSubmodelElement -> {
                var endpoint = (ISubmodelElementCollection)iSubmodelElement;
                var properties = endpoint.getProperties();
                IProperty transportProfile = properties.get("TransportProfile");
                IProperty securityPolicy = properties.get("SecurityPolicy");

                if (transportProfile.getValue().equals(transportProfileValue)
                        && securityPolicy.getValue().equals(securityPolicyValue)) {
                    connectionStringValue.set((String) properties.get("Endpoint").getValue());
                    nodeIdValue.set((String) properties.get("NodeId").getValue());
                }

//                properties.values().stream().forEach(iProperty -> {
//                    log.info("Name: {}, Value: {}", iProperty.getIdShort(), iProperty.getValue());
//                });
            });

            Properties config = new Properties();
            config.setProperty("nodeId",nodeIdValue.get());
            config.setProperty("connectionString",connectionStringValue.get());
            config.setProperty("username","ccAgent");
            config.setProperty("password","");

            ControlComponentAgent agent = new ControlComponentAgent(config, this);
            if (agent.activate()) {
                agents.put(aasId, agent);
                smDescriptors.put(aasId, smDescriptor);
                //TODO: create, host and register agent submodel
            }
        }

    }

    private void unregisterControlComponentAgent(String aasId, String smIdentifier) {
        if (agents.containsKey(aasId)) {
            SubmodelDescriptor smDescriptor = smDescriptors.remove(aasId);
            ControlComponentAgent agent = agents.remove(aasId);
            log.info("unregister ControlComponentAgent for submodelId {} (IdShort: {})", smDescriptor.getIdentification(), smDescriptor.getIdShort());
            //TODO: delete and unregister agent submodel
            agent.deactivate();
        }
    }

    @Bean
    public Consumer<ControlComponentRequest> controlComponentOrders() {
        return this::handleComponentOrder;
    }

    private void handleComponentOrder(ControlComponentRequest controlComponentRequest) {
        log.info("received {} for {}", controlComponentRequest.getRequestType(), controlComponentRequest.getComponentId());
        log.debug(controlComponentRequest.toString());
        threadPoolTaskScheduler.execute(new Runnable() {
            @Override
            public void run() {
                ControlComponentResponse response = null;
                if (controlComponentRequest.getAasId() == null) {
                    response = ControlComponentResponse.newBuilder()
                            .setRequest(controlComponentRequest)
                            .setComponentId(controlComponentRequest.getComponentId())
                            .setAasId(controlComponentRequest.getAasId())
                            .setCorrelationId(controlComponentRequest.getCorrelationId())
                            .setMessage("no control component found for task")
                            .setStatus(RequestStatus.REJECTED)
                            .setStatusCode(-1)
                            .setOutputParameters(Collections.emptyList())
                            .build();
                } else {
                    ControlComponentAgent agent = agents.get(controlComponentRequest.getAasId());
                    if (agent == null) {
                        response = ControlComponentResponse.newBuilder()
                                .setRequest(controlComponentRequest)
                                .setComponentId(controlComponentRequest.getComponentId())
                                .setAasId(controlComponentRequest.getAasId())
                                .setCorrelationId(controlComponentRequest.getCorrelationId())
                                .setMessage("control component agent not available for aasId " + controlComponentRequest.getAasId())
                                .setStatus(RequestStatus.REJECTED)
                                .setStatusCode(-2)
                                .setOutputParameters(Collections.emptyList())
                                .build();
                    } else {
                        ControlComponentRequestStatus status = agent.handleControlComponentRequest(controlComponentRequest);
                        if (status.getStatus() != RequestStatus.ACCEPTED /* && status.getStatus() != RequestStatus.QUEUED*/) {
                            response = ControlComponentResponse.newBuilder()
                                    .setRequest(controlComponentRequest)
                                    .setComponentId(controlComponentRequest.getComponentId())
                                    .setAasId(controlComponentRequest.getAasId())
                                    .setCorrelationId(controlComponentRequest.getCorrelationId())
                                    .setMessage(status.getMessage())
                                    .setStatus(status.getStatus() == RequestStatus.DONE ? RequestStatus.OK : status.getStatus())
                                    .setStatusCode(0)
                                    .setOutputParameters(Collections.emptyList())
                                    .build();
                        } else {
                            // TODO: implement timer to check if execution is stalled and report back if so
                        }
                    }
                }
                if (response != null) {
                    streamBridge.send("controlComponentResponses", response);
                }
            }
        });


    }



}
