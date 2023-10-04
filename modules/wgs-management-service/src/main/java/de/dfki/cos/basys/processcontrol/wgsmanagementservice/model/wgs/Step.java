package de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.wgs;

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

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }

    public long getActive() { return active; }
    public void setActive(long value) { this.active = value; }

    public String getStationID() { return stationID; }
    public void setStationID(String value) { this.stationID = value; }

    public String getVariantID() { return variantID; }
    public void setVariantID(String value) { this.variantID = value; }

    public long getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(long value) { this.currentAmount = value; }

    public long getAmountTotal() { return amountTotal; }
    public void setAmountTotal(long value) { this.amountTotal = value; }

    public boolean getFinished() { return finished; }
    public void setFinished(boolean value) { this.finished = value; }

    public Component[] getComponents() { return components; }
    public void setComponents(Component[] value) { this.components = value; }

    public Tool[] getTools() { return tools; }
    public void setTools(Tool[] value) { this.tools = value; }

    public Image[] getImages() { return images; }
    public void setImages(Image[] value) { this.images = value; }

    public StepHint[] getStepHints() { return stepHints; }
    public void setStepHints(StepHint[] value) { this.stepHints = value; }
}
