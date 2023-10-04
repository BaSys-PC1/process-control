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

        Component c1 = new Component();
        c1.setName("Name 1");
        c1.setChecked(true);
        c1.setURL("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png");
        Component c2 = new Component();
        c2.setName("Name 2");
        c2.setChecked(true);
        c2.setURL("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png");
        s.setComponents(new Component[]{c1, c2});

        Tool t1 = new Tool();
        t1.setName("Schrauber");
        t1.setURL("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png");
        s.setTools(new Tool[] {t1});

        Image i1 = new Image();
        i1.setURL("https://www.dfki.de/fileadmin/user_upload/DFKI/Medien/Logos/Logos_DFKI/DFKI_Logo.png");
        Image i2 = new Image();
        i2.setURL("/extern/img/steps/step.jpg");
        s.setImages(new Image[] {i1, i2});

        StepHint sh1 = new StepHint();
        sh1.setName("Arbeitsschritt 1");
        sh1.setDescriptionShort("Kurze Beschreibung 123");
        StepHint sh2 = new StepHint();
        sh2.setName("Arbeitsschritt 2");
        sh2.setDescriptionShort("Kurze Beschreibung 456");
        StepHint sh3 = new StepHint();
        sh3.setName("Arbeitsschritt 3");
        sh3.setDescriptionShort("Kurze Beschreibung 789");
        s.setStepHints(new StepHint[] {sh1, sh2, sh3});

        this.restTemplate.postForObject(url, s, Step.class);
    }


}
