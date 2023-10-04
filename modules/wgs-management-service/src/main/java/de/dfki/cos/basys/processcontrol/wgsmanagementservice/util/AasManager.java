package de.dfki.cos.basys.processcontrol.wgsmanagementservice.util;

import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.exception.FeatureNotImplementedException;
import org.eclipse.basyx.vab.factory.java.ModelProxyFactory;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AasManager implements IAssetAdministrationShellManager {
    protected IAASRegistry aasDirectory;
    protected IConnectorFactory connectorFactory;
    protected ModelProxyFactory proxyFactory;

    public AasManager(IAASRegistry directory) {
        this(directory, new HTTPConnectorFactory());
    }

    public AasManager(IAASRegistry directory, IConnectorFactory provider) {
        this.aasDirectory = directory;
        this.connectorFactory = provider;
        this.proxyFactory = new ModelProxyFactory(provider);
    }

    public ConnectedAssetAdministrationShell retrieveAAS(IIdentifier aasId) {
        AASDescriptor aasDescriptor = this.aasDirectory.lookupAAS(aasId);
        String addr = aasDescriptor.getFirstEndpoint();
        VABElementProxy proxy = this.proxyFactory.createProxy(addr);
        return new ConnectedAssetAdministrationShell(proxy);
    }

    public Map<String, ISubmodel> retrieveSubmodels(IIdentifier aasId) {
        AASDescriptor aasDesc = this.aasDirectory.lookupAAS(aasId);
        Collection<SubmodelDescriptor> smDescriptors = aasDesc.getSubmodelDescriptors();
        Map<String, ISubmodel> submodels = new HashMap();
        Iterator var5 = smDescriptors.iterator();

        while(var5.hasNext()) {
            SubmodelDescriptor smDesc = (SubmodelDescriptor)var5.next();
            String smEndpoint = smDesc.getFirstEndpoint();
            String smIdShort = smDesc.getIdShort();
            VABElementProxy smProxy = this.proxyFactory.createProxy(smEndpoint);
            ConnectedSubmodel connectedSM = new ConnectedSubmodel(smProxy);
            submodels.put(smIdShort, connectedSM);
        }

        return submodels;
    }

    public ISubmodel retrieveSubmodel(IIdentifier aasId, IIdentifier smId) {
        SubmodelDescriptor smDescriptor = this.aasDirectory.lookupSubmodel(aasId, smId);
        String addr = smDescriptor.getFirstEndpoint();
        return new ConnectedSubmodel(this.proxyFactory.createProxy(addr));
    }

    public Collection<IAssetAdministrationShell> retrieveAASAll() {
        throw new FeatureNotImplementedException();
    }

    public void deleteAAS(IIdentifier id) {
        AASDescriptor aasDescriptor = this.aasDirectory.lookupAAS(id);
        String addr = aasDescriptor.getFirstEndpoint();
        addr = VABPathTools.stripSlashes(addr);
        addr = addr.substring(0, addr.length() - "/aas".length());
        this.proxyFactory.createProxy(addr).deleteValue("");
        this.aasDirectory.delete(id);
    }

    public void createSubmodel(IIdentifier aasId, Submodel submodel) {
        this.retrieveAAS(aasId).addSubmodel(submodel);
        AASDescriptor aasDescriptor = this.aasDirectory.lookupAAS(aasId);
        String addr = aasDescriptor.getFirstEndpoint();
        String smEndpoint = VABPathTools.concatenatePaths(new String[]{addr, "submodels", submodel.getIdShort(), "submodel"});
        this.aasDirectory.register(aasId, new SubmodelDescriptor(submodel, smEndpoint));
    }

    public void deleteSubmodel(IIdentifier aasId, IIdentifier submodelId) {
        IAssetAdministrationShell shell = this.retrieveAAS(aasId);
        shell.removeSubmodel(submodelId);
        this.aasDirectory.delete(aasId, submodelId);
    }

    public void createAAS(AssetAdministrationShell aas, String endpoint) {
        endpoint = VABPathTools.stripSlashes(endpoint);
        if (!endpoint.endsWith("shells")) {
            endpoint = endpoint + "/shells";
        }

        IModelProvider provider = this.connectorFactory.getConnector(endpoint);
        AASAggregatorProxy proxy = new AASAggregatorProxy(provider);
        proxy.createAAS(aas);

        try {
            String combinedEndpoint = VABPathTools.concatenatePaths(new String[]{endpoint, URLEncoder.encode(aas.getIdentification().getId(), "UTF-8"), "aas"});
            this.aasDirectory.register(new AASDescriptor(aas, combinedEndpoint));
        } catch (UnsupportedEncodingException var6) {
            throw new RuntimeException("Encoding failed. This should never happen");
        }
    }

}
