package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.registry;

import com.google.gson.annotations.SerializedName;

public class DataCenterInfo {
    @SerializedName(value = "@class")
    private String dataCenterInfoClass;
    private String name;
    private DataCenterInfoMetadata metadata;

    public String getDataCenterInfoClass() { return dataCenterInfoClass; }
    public void setDataCenterInfoClass(String value) { this.dataCenterInfoClass = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public DataCenterInfoMetadata getMetadata() { return metadata; }
    public void setMetadata(DataCenterInfoMetadata value) { this.metadata = value; }
}
