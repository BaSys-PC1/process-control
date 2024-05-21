package de.dfki.cos.basys.processcontrol.wgsmanagementservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.processcontrol.model.NotificationType;
import de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.wgs.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import de.dfki.cos.basys.aas.registry.model.*;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class WGSManager {

    @Autowired
    private AasRegistryQueries aasRegistryServices;

    @Autowired
    private IAssetAdministrationShellManager aasManager;

    @Value("${basys.semanticIds.processSubmodel}")
    private String processSubmodelSemanticId;

    @Value("${basys.semanticIds.idShort}")
    private String idShort;

    private final RestTemplate restTemplate;

    private Step currentStep;

    @Value("${basys.wgsDashboard.connectionString:http://localhost:3000/}")
    private String baseUrl;

    @Autowired
    private ObjectMapper objectMapper;

    public WGSManager(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostConstruct
    public void initialize() {
        log.info("Started ScaleManagementService");
        currentStep = getCurrentStep();

        AssetAdministrationShellDescriptor aasDescriptor = aasRegistryServices.searchAasDescriptorByIdShort(idShort);
        Key instanceKey = new Key().type(KeyTypes.CONCEPTDESCRIPTION).value(processSubmodelSemanticId);

        var opt = aasDescriptor.getSubmodelDescriptors().stream().filter(smd -> smd.getSemanticId().getKeys().contains(instanceKey)).findFirst();
        opt.ifPresent(smDescriptor -> {
            //Retrieve Submodel
            ISubmodel instanceSubmodel = aasManager.retrieveSubmodel(new Identifier(IdentifierType.CUSTOM, aasDescriptor.getIdentification()), new Identifier(IdentifierType.CUSTOM, smDescriptor.getIdentification()));
            ISubmodelElementCollection processesCollection = (ISubmodelElementCollection) instanceSubmodel.getSubmodelElement("Processes");
            ISubmodelElementCollection manufacturingProcess = (ISubmodelElementCollection) processesCollection.getSubmodelElement("ManufacturingBoxedRaspberryPi");
            IProperty maxDuration = manufacturingProcess.getProperties().get("MaxDuration");
            log.info("MaxDuration (via AAS SDK): {}", maxDuration.getValue());
        });
    }

    public void sendStep(String workstepId) {
        //TODO: Retrieve info from AAS based on workstepId
        try {
            File jsonFile = new ClassPathResource("data/" + workstepId + ".json").getFile();
            currentStep = objectMapper.readValue(jsonFile, Step.class);
            this.sendCurrentStep();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
/*
        currentStep.setName("Arbeitsschritt 1");
        currentStep.setDescription("Beschreibung des Arbeitsschritts");
        currentStep.setActive(0);
        currentStep.setStationID("827");
        currentStep.setVariantID(workstepId);
        currentStep.setCurrentAmount(7);
        currentStep.setAmountTotal(8);
        currentStep.setFinished(false);

        Component c1 = Component.builder()
                .name("Name 1")
                .checked(true)
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        Component c2 = Component.builder()
                .name("Name 2")
                .checked(false)
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        currentStep.setComponents(new Component[]{c1, c2});

        Tool t1 = Tool.builder()
                .name("Schrauber")
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        currentStep.setTools(new Tool[] {t1});

        Image i1 = Image.builder()
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        Image i2 = Image.builder()
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        currentStep.setImages(new Image[] {i1, i2});

        StepHint sh1 = StepHint.builder()
                .name("Arbeitsschritt 1")
                .descriptionShort("Kurze Beschreibung 123")
                .build();
        StepHint sh2 = StepHint.builder()
                .name("Arbeitsschritt 2")
                .descriptionShort("Kurze Beschreibung 456")
                .build();
        StepHint sh3 = StepHint.builder()
                .name("Arbeitsschritt 3")
                .descriptionShort("Kurze Beschreibung 789")
                .build();
        currentStep.setStepHints(new StepHint[] {sh1, sh2, sh3});*/
    }

    public void sendMaterialCheckedUpdate() {
        currentStep.getComponents()[0].setChecked(true);
        this.sendCurrentStep();
    }

    public void sendNotification(NotificationType type, Boolean show){
        // REST API return null if no notification is set
        Notification[] currentNotifications = currentStep.getNotifications() != null ? currentStep.getNotifications() : new Notification[]{};
        String title = "";
        String description = "";
        String icon = "";
        String x = "";
        String y = "";

        try {
            File jsonFile = new ClassPathResource("data/notifications.json").getFile();
            JsonNode node = objectMapper.readValue(jsonFile, JsonNode.class);
            title = node.get(type.toString()).get("title").asText();
            description = node.get(type.toString()).get("description").asText();
            icon = node.get(type.toString()).get("icon").asText();
            x = node.get(type.toString()).get("x").asText();
            y = node.get(type.toString()).get("y").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String finalTitle = title;
        // See if notification is already shown on server side
        Optional<Notification> notification = Arrays.stream(currentNotifications).filter(n -> n.getTitle().equals(finalTitle)).findFirst();

        if (!show) {
            // Notification shall be removed
            if (notification.isEmpty()){
                // nothing to do here
                return;
            }
            else {
                // Remove notification from array
                currentNotifications = Arrays.stream(currentNotifications).filter(n -> !Objects.equals(n.getTitle(), finalTitle)).toArray(Notification[]::new);
            }

        }
        else {
            // Notification shall be added
            if (notification.isPresent()){
                // nothing to do here
                return;
            }
            else {
                Notification n1 = Notification.builder()
                        .title(title)
                        .description(description)
                        .icon(icon)
                        .x(x)
                        .y(y)
                        .build();

                // Extend existing notifications
                currentNotifications = addElement(currentNotifications, n1);
            }
        }

        currentStep.setNotifications(currentNotifications);
        this.sendCurrentStep();
    }

    private Step getCurrentStep() {
        Step stepResult = null;
        try {
            stepResult = this.restTemplate.getForObject(baseUrl + "active-step/current", Step.class);
        }
        catch (Exception ex){
            log.error("Error: {}", ex.getMessage());
        }

        if (stepResult == null) return new Step();
        return stepResult;
    }

    private void sendCurrentStep(){
        //send material box on Kafka topic for Logitech program
        Step stepResult = null;
        try {
            stepResult = this.restTemplate.postForObject(baseUrl + "events/step-change", currentStep, Step.class);
        }
        catch (Exception ex){
            log.error("Error: {}", ex.getMessage());
        }

        if (stepResult == null) return;

        log.info(stepResult.toString());
    }

    private static <X> X[] addElement(X[] myArray, X element) {
        X[] array = Arrays.copyOf(myArray, myArray.length + 1);
        array[myArray.length] = element;
        return array;
    }
}
