package de.dfki.cos.basys.processcontrol.cctaskmanager;

import de.dfki.cos.basys.processcontrol.cctaskmanager.util.AasManager;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import de.dfki.cos.basys.aas.registry.client.api.AasRegistryPaths;
import de.dfki.cos.basys.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;
import de.dfki.cos.basys.aas.registry.compatibility.DotAASRegistryProxy;
import de.dfki.cos.basys.aas.registry.model.*;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.springframework.web.util.UriUtils;
import springfox.documentation.schema.ModelReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
public class TestMain {

    public static void main(String[] args) throws UnsupportedEncodingException {

        //String aasRegistryEndpoint = "http://lns-90200.sb.dfki.de:8020";
        String aasRegistryEndpoint = "http://aasregistry.dockerhost";
        //String aasRegistryEndpoint = "http://localhost:8080";
        String aasId = "https://dfki.de/ids/aas/mir100_1";

        String idSubmodelSemanticId = "https://www.hsu-hh.de/aut/aas/identification";
        String ccInterfaceSubmodelSemanticId = "https://wiki.eclipse.org/BaSyx_/_Submodels#Control_Component_Interface";
        String ccInstanceSubmodelSemanticId = "https://wiki.eclipse.org/BaSyx_/_Submodels#Control_Component_Instance";

        //dotaas registry client
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        apiInstance.getApiClient().setBasePath(aasRegistryEndpoint);

        //basyx registy client with dotaas compatibility impl for use in aas manager
        IAASRegistry aasRegistry = new DotAASRegistryProxy(aasRegistryEndpoint);
        IAssetAdministrationShellManager aasManager = new AasManager(aasRegistry);

        try {
            log.info("retrieving all aas descriptors via dotaas registry client");
            log.info("---------------------------------------------");
            var result = apiInstance.getAllAssetAdministrationShellDescriptors();
            log.info(result.toString());
            log.info("---------------------------------------------");
        } catch (Exception e) {
            log.error("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllAssetAdministrationShellDescriptors", e);
        }

        try {
            log.info("retrieving the mir100_1 aas descriptor via dotaas registry client");
            log.info("---------------------------------------------");
            var result = apiInstance.getAssetAdministrationShellDescriptorById(aasId);
            log.info(result.toString());
            log.info("---------------------------------------------");
        } catch (Exception e) {
            log.error("Exception when calling RegistryAndDiscoveryInterfaceApi#getAssetAdministrationShellDescriptorById", e);
        }

        try {
            log.info("retrieving the mir100_1 aas via basyx aas manager, identifier type can be ignored");
            log.info("---------------------------------------------");

            var result = aasManager.retrieveAAS(new Identifier(IdentifierType.CUSTOM, aasId));
            log.info(result.toString());
            log.info("----------------------");
            result.getSubmodels().values().stream().forEach(sm -> {
                log.info("Submodel {} (Id = {})", sm.getIdShort(), sm.getIdentification().getId());
                if (sm.getSemanticId().getKeys().size() > 0) {
                    log.info("  SemanticId = " + sm.getSemanticId().getKeys().get(0).getValue());
                }
                sm.getProperties().values().stream().forEach(p -> {
                    log.info("  Property {} (Value = {})", p.getIdShort(), p.getValue());
                    if (p.getSemanticId().getKeys().size() > 0) {
                        log.info("    SemanticId = " + p.getSemanticId().getKeys().get(0).getValue());
                    }
                });
            });
            log.info("---------------------------------------------");
        } catch (Exception e) {
            log.error("Exception when calling IAssetAdministrationShellManager#retrieveAAS", e);
        }



        try {
            log.info("search for aas that contain a identification submodel");
            log.info("---------------------------------------------");
            ShellDescriptorQuery query = new ShellDescriptorQuery();
            query.setQueryType(ShellDescriptorQuery.QueryTypeEnum.MATCH);
            query.setPath(AasRegistryPaths.submodelDescriptors().semanticId().keys().value());
            query.setValue(idSubmodelSemanticId);

            var searchResult = apiInstance.searchShellDescriptors(new ShellDescriptorSearchRequest().query(query));
            log.info("Result size: " + searchResult.getTotal());

            Key instanceKey = new Key().type(KeyTypes.CONCEPTDESCRIPTION).value(idSubmodelSemanticId);

            for (var aasDescriptor: searchResult.getHits()) {
                var opt = aasDescriptor.getSubmodelDescriptors().stream().filter(smd -> smd.getSemanticId().getKeys().contains(instanceKey)).findFirst();
                opt.ifPresent(smDescriptor -> {
                    ISubmodel sm = aasManager.retrieveSubmodel(new Identifier(IdentifierType.CUSTOM, aasDescriptor.getIdentification()),new Identifier(IdentifierType.CUSTOM, smDescriptor.getIdentification()));
                    log.info(sm.getIdShort());
                });
            }
            log.info("---------------------------------------------");
        } catch (Exception e) {
            log.error("Exception when searching for aas", e);
        }

        try {
            log.info("search for aas whose assetId ends with mir100_1");
            log.info("---------------------------------------------");
            ShellDescriptorQuery query = new ShellDescriptorQuery();
            query.setQueryType(ShellDescriptorQuery.QueryTypeEnum.REGEX);
            query.setPath(AasRegistryPaths.globalAssetId().keys().value());
            query.setValue("mir100_1");

            var searchResult = apiInstance.searchShellDescriptors(new ShellDescriptorSearchRequest().query(query));
            log.info("Result size: " + searchResult.getTotal());


            for (var aasDescriptor: searchResult.getHits()) {
                log.info("IdShort: {}", aasDescriptor.getIdShort());
            }
            log.info("---------------------------------------------");
        } catch (Exception e) {
            log.error("Exception when searching for aas", e);
        }


    }
}
