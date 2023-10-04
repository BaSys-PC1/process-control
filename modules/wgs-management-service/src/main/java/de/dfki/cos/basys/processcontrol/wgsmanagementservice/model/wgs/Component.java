package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.wgs;

import lombok.Data;

@Data
public class Component {
    private String name;
    private boolean checked;
    private String url;
}
