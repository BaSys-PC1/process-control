package de.dfki.cos.basys.processcontrol.scalemanagementservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import de.dfki.cos.basys.aas.registry.client.api.AasRegistryPaths;
import de.dfki.cos.basys.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;
import de.dfki.cos.basys.aas.registry.model.*;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AasRegistryQueries {

    @Autowired
    private RegistryAndDiscoveryInterfaceApi registryApi;

    public List<AssetAdministrationShellDescriptor> searchAasDescriptorsWithSubmodel(String semanticId) {
        log.info("search for aas that contain a submodel with semanticId {}", semanticId);
        log.info("---------------------------------------------");
        ShellDescriptorQuery query = new ShellDescriptorQuery();
        query.setQueryType(ShellDescriptorQuery.QueryTypeEnum.MATCH);
        query.setPath(AasRegistryPaths.submodelDescriptors().semanticId().keys().value());
        query.setValue(semanticId);

        var searchResult = registryApi.searchShellDescriptors(new ShellDescriptorSearchRequest().query(query).page(new Page().size(100).index(0)));
        log.info("Result size: {}", searchResult.getTotal());

        //Key instanceKey = new Key().type(KeyElements.CONCEPTDESCRIPTION).value(semanticId);

        for (var aasDescriptor: searchResult.getHits()) {
            log.info("Result hit: {}", aasDescriptor.getIdShort());
//            var opt = aasDescriptor.getSubmodelDescriptors().stream().filter(smd -> ((ModelReference)smd.getSemanticId()).getKeys().contains(instanceKey)).findFirst();
//            opt.ifPresent(smDescriptor -> {
//                ISubmodel sm = aasManager.retrieveSubmodel(new Identifier(IdentifierType.CUSTOM, aasDescriptor.getIdentification()),new Identifier(IdentifierType.CUSTOM, smDescriptor.getIdentification()));
//                log.info(sm.getIdShort());
//            });
        }

        return searchResult.getHits();
    }

    public AssetAdministrationShellDescriptor searchAasDescriptorByAssetName(String assetName) {
        log.info("search for aas whose assetId ends with {}", assetName);
        ShellDescriptorQuery query = new ShellDescriptorQuery();
        query.setQueryType(ShellDescriptorQuery.QueryTypeEnum.REGEX);
        query.setPath(AasRegistryPaths.globalAssetId().keys().value());
        query.setValue(assetName);

        var searchResult = registryApi.searchShellDescriptors(new ShellDescriptorSearchRequest().query(query).page(new Page().size(1).index(0)));
        log.info("Result size: " + searchResult.getTotal());

        for (var aasDescriptor: searchResult.getHits()) {
            log.info("IdShort: {}", aasDescriptor.getIdShort());
        }

        if (searchResult.getTotal() == 0) {
            log.warn("no hit found");
            return null;
        } else if (searchResult.getTotal() == 1) {
            return searchResult.getHits().get(0);
        } else {
            log.warn("more than 1 hit found");
            return null;
        }
    }

    public AssetAdministrationShellDescriptor searchAasDescriptorByIdShort(String idShort) {
        log.info("search for aas whose idShort ends with {}", idShort);
        ShellDescriptorQuery query = new ShellDescriptorQuery();
        query.setQueryType(ShellDescriptorQuery.QueryTypeEnum.REGEX);
        query.setPath(AasRegistryPaths.idShort());
        query.setValue(idShort);

        var searchResult = registryApi.searchShellDescriptors(new ShellDescriptorSearchRequest().query(query).page(new Page().size(1).index(0)));
        log.info("Result size: " + searchResult.getTotal());

        for (var aasDescriptor: searchResult.getHits()) {
            log.info("IdShort: {}", aasDescriptor.getIdShort());
        }

        if (searchResult.getTotal() == 0) {
            log.warn("no hit found");
            return null;
        } else if (searchResult.getTotal() == 1) {
            return searchResult.getHits().get(0);
        } else {
            log.warn("more than 1 hit found");
            return null;
        }
    }

    public String searchAasIdentifierByAssetName(String assetName) {
        log.info("search for aas whose assetId ends with {}", assetName);
        ShellDescriptorQuery query = new ShellDescriptorQuery();
        query.setQueryType(ShellDescriptorQuery.QueryTypeEnum.REGEX);
        query.setPath(AasRegistryPaths.globalAssetId().keys().value());
        //FIXME: this pattern is probably not strict enough
        query.setValue(".*" + assetName);

        var searchResult = registryApi.searchShellDescriptors(new ShellDescriptorSearchRequest().query(query).page(new Page().size(1).index(0)));
        log.info("Result size: " + searchResult.getTotal());

        for (var aasDescriptor: searchResult.getHits()) {
            log.info("IdShort: {}", aasDescriptor.getIdShort());
        }

        if (searchResult.getTotal() == 0) {
            log.warn("no hit found");
            return null;
        } else if (searchResult.getTotal() == 1) {
            return searchResult.getHits().get(0).getIdentification();
        } else {
            log.warn("more than 1 hit found");
            return null;
        }
    }
}
