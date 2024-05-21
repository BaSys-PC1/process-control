package de.dfki.cos.basys.processcontrol.scalemanagementservice.services;

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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class ScaleManager {

    @Autowired
    private AasRegistryQueries aasRegistryServices;

    @Autowired
    private IAssetAdministrationShellManager aasManager;

    @Value("${basys.semanticIds.processSubmodel}")
    private String processSubmodelSemanticId;

    @Value("${basys.semanticIds.idShort}")
    private String idShort;

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

    }


}
