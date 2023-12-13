package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.wgs;

import lombok.Data;

@Data
public class Step {
    private String name;
    private String description;
    private long active;
    private String stationID;
    private String variantID;
    private long currentAmount;
    private long amountTotal;
    private boolean finished;
    private Component[] components;
    private Tool[] tools;
    private Image[] images;
    private StepHint[] stepHints;
    private Notification[] notifications;
}
