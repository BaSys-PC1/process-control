package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model;

public class Instance {
    private String instanceID;
    private String app;
    private String ipAddr;
    private String hostName;
    private String status;
    private String overriddenStatus;
    private Port port;
    private Port securePort;
    private long countryID;
    private DataCenterInfo dataCenterInfo;
    private LeaseInfo leaseInfo;
    private InstanceMetadata metadata;
    private String appGroupName;
    private String homePageURL;
    private String statusPageURL;
    private String healthCheckURL;
    private String secureHealthCheckURL;
    private String secureVipAddress;
    private String vipAddress;
    private String isCoordinatingDiscoveryServer;
    private String lastUpdatedTimestamp;
    private String lastDirtyTimestamp;
    private String asgName;

    public String getInstanceID() { return instanceID; }
    public void setInstanceID(String value) { this.instanceID = value; }

    public String getApp() { return app; }
    public void setApp(String value) { this.app = value; }

    public String getIPAddr() { return ipAddr; }
    public void setIPAddr(String value) { this.ipAddr = value; }

    public String getHostName() { return hostName; }
    public void setHostName(String value) { this.hostName = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

    public String getOverriddenStatus() { return overriddenStatus; }
    public void setOverriddenStatus(String value) { this.overriddenStatus = value; }

    public Port getPort() { return port; }
    public void setPort(Port value) { this.port = value; }

    public Port getSecurePort() { return securePort; }
    public void setSecurePort(Port value) { this.securePort = value; }

    public long getCountryID() { return countryID; }
    public void setCountryID(long value) { this.countryID = value; }

    public DataCenterInfo getDataCenterInfo() { return dataCenterInfo; }
    public void setDataCenterInfo(DataCenterInfo value) { this.dataCenterInfo = value; }

    public LeaseInfo getLeaseInfo() { return leaseInfo; }
    public void setLeaseInfo(LeaseInfo value) { this.leaseInfo = value; }

    public InstanceMetadata getMetadata() { return metadata; }
    public void setMetadata(InstanceMetadata value) { this.metadata = value; }

    public String getAppGroupName() { return appGroupName; }
    public void setAppGroupName(String value) { this.appGroupName = value; }

    public String getHomePageURL() { return homePageURL; }
    public void setHomePageURL(String value) { this.homePageURL = value; }

    public String getStatusPageURL() { return statusPageURL; }
    public void setStatusPageURL(String value) { this.statusPageURL = value; }

    public String getHealthCheckURL() { return healthCheckURL; }
    public void setHealthCheckURL(String value) { this.healthCheckURL = value; }

    public String getSecureHealthCheckURL() { return secureHealthCheckURL; }
    public void setSecureHealthCheckURL(String value) { this.secureHealthCheckURL = value; }

    public String getSecureVipAddress() { return secureVipAddress; }
    public void setSecureVipAddress(String value) { this.secureVipAddress = value; }

    public String getVipAddress() { return vipAddress; }
    public void setVipAddress(String value) { this.vipAddress = value; }

    public String getIsCoordinatingDiscoveryServer() { return isCoordinatingDiscoveryServer; }
    public void setIsCoordinatingDiscoveryServer(String value) { this.isCoordinatingDiscoveryServer = value; }

    public String getLastUpdatedTimestamp() { return lastUpdatedTimestamp; }
    public void setLastUpdatedTimestamp(String value) { this.lastUpdatedTimestamp = value; }

    public String getLastDirtyTimestamp() { return lastDirtyTimestamp; }
    public void setLastDirtyTimestamp(String value) { this.lastDirtyTimestamp = value; }

    public String getASGName() { return asgName; }
    public void setASGName(String value) { this.asgName = value; }
}