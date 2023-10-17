package de.dfki.cos.basys.processcontrol.wgsmanagementservice.services;

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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;

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
        currentStep.setStepHints(new StepHint[] {sh1, sh2, sh3});

        this.sendCurrentStep();
    }

    public void sendNotification(NotificationType type, Boolean show){

        Notification n1 = Notification.builder()
                .build();
        switch (type) {
            case WRONG_QUANTITY_TAKEN:
                n1.setTitle("Wrong quantity");
                n1.setDescription("A wrong quantity was taken out of the box.");
                break;
            case WRONG_DIRECTION_REACHED:
                n1.setTitle("Wrong direction reached");
                n1.setDescription("You reached a wrong direction with one of your hands.");
                break;
            case GRASPED_AT_WRONG_LOCATION:
                n1.setTitle("Grasped at wrong location");
                n1.setDescription("You grasped at a wrong box.");
            case LEADING_INTO_WRONG_DIRECTION:
                n1.setTitle("Leading into wrong direction");
                n1.setDescription("One of your hands was leading into a wrong direction.");
        }

        // REST API return null if no notification is set
        Notification[] currentNotifications = currentStep.getNotifications() != null ? currentStep.getNotifications() : new Notification[]{};
        // Extend existing notifications
        Notification[] updatedNotifications = addElement(currentNotifications, n1);
        currentStep.setNotifications(updatedNotifications);

        this.sendCurrentStep();
    }

    private Step getCurrentStep() {
        return this.restTemplate.getForObject(baseUrl + "active-step/current", Step.class);
    }

    private void sendCurrentStep(){
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
