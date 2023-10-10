package de.dfki.cos.basys.processcontrol.wgsmanagementservice.services;

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

    public WGSManager(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostConstruct
    public void initialize() {
        log.info("Started ScaleManagementService");

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

        this.sendStep();
    }

    public void sendStep() {
        //TODO: Retrieve info from AAS
        String url = "http://localhost:3000/events/step-change";

        Step s = new Step();
        s.setName("Arbeitsschritt 1");
        s.setDescription("Beschreibung des Arbeitsschritts");
        s.setActive(0);
        s.setStationID("827");
        s.setVariantID("911");
        s.setCurrentAmount(7);
        s.setAmountTotal(8);
        s.setFinished(false);

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
        s.setComponents(new Component[]{c1, c2});

        Tool t1 = Tool.builder()
                .name("Schrauber")
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        s.setTools(new Tool[] {t1});

        Image i1 = Image.builder()
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        Image i2 = Image.builder()
                .url("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png")
                .build();
        s.setImages(new Image[] {i1, i2});

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
        s.setStepHints(new StepHint[] {sh1, sh2, sh3});

        Notification n1 = Notification.builder()
                .title("Test")
                .description("Show that")
                .build();
        s.setNotifications(new Notification[]{n1});

        Step stepResult = null;
        try {
            stepResult = this.restTemplate.postForObject(url, s, Step.class);
        }
        catch (Exception ex){
            log.error("Error: {}", ex.getMessage());
        }

        if (stepResult == null) return;

        log.info(stepResult.toString());
    }
}
