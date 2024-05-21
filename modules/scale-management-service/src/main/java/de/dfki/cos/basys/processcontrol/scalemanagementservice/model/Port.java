package de.dfki.cos.basys.processcontrol.scalemanagementservice.model;

import com.google.gson.annotations.SerializedName;

public class Port {
    @SerializedName(value = "$")
    private long dollar;
    @SerializedName(value = "@enabled")
    private String enabled;

    public long getDollar() { return dollar; }
    public void setDollar(long value) { this.dollar = value; }

    public String getEnabled() { return enabled; }
    public void setEnabled(String value) { this.enabled = value; }
}
