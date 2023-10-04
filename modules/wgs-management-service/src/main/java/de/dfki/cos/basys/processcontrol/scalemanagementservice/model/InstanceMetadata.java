package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model;

public class InstanceMetadata {
    private String ec2InstanceID;
    private String imageID;

    public String getEc2InstanceID() { return ec2InstanceID; }
    public void setEc2InstanceID(String value) { this.ec2InstanceID = value; }

    public String getImageID() { return imageID; }
    public void setImageID(String value) { this.imageID = value; }
}