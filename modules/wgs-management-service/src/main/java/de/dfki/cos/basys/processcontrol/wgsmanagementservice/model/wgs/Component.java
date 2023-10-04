package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.wgs;

public class Component {
    private String name;
    private boolean checked;
    private String url;

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public boolean getChecked() { return checked; }
    public void setChecked(boolean value) { this.checked = value; }

    public String getURL() { return url; }
    public void setURL(String value) { this.url = value; }
}
