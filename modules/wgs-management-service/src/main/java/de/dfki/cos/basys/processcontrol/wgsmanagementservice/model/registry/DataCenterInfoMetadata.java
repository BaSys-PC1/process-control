package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.registry;

import com.google.gson.annotations.SerializedName;

public class DataCenterInfoMetadata {
    @SerializedName(value = "vpc-id")
    private String vpcID;
    @SerializedName(value = "ami-id")
    private String amiID;
    private String mac;
    private String accountID;
    @SerializedName(value = "instance-id")
    private String instanceID;
    @SerializedName(value = "instance-type")
    private String instanceType;
    @SerializedName(value = "local-hostname")
    private String localHostname;
    @SerializedName(value = "local-ipv4")
    private String localIpv4;
    @SerializedName(value = "availability-zone")
    private String availabilityZone;

    public String getVpcID() { return vpcID; }
    public void setVpcID(String value) { this.vpcID = value; }

    public String getAmiID() { return amiID; }
    public void setAmiID(String value) { this.amiID = value; }

    public String getMAC() { return mac; }
    public void setMAC(String value) { this.mac = value; }

    public String getAccountID() { return accountID; }
    public void setAccountID(String value) { this.accountID = value; }

    public String getInstanceID() { return instanceID; }
    public void setInstanceID(String value) { this.instanceID = value; }

    public String getInstanceType() { return instanceType; }
    public void setInstanceType(String value) { this.instanceType = value; }

    public String getLocalHostname() { return localHostname; }
    public void setLocalHostname(String value) { this.localHostname = value; }

    public String getLocalIpv4() { return localIpv4; }
    public void setLocalIpv4(String value) { this.localIpv4 = value; }

    public String getAvailabilityZone() { return availabilityZone; }
    public void setAvailabilityZone(String value) { this.availabilityZone = value; }
}