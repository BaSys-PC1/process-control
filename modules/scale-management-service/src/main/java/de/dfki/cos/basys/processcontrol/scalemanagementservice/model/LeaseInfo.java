package de.dfki.cos.basys.processcontrol.scalemanagementservice.model;

public class LeaseInfo {
    private long renewalIntervalInSecs;
    private long durationInSecs;
    private long registrationTimestamp;
    private long lastRenewalTimestamp;
    private long evictionTimestamp;
    private long serviceUpTimestamp;

    public long getRenewalIntervalInSecs() { return renewalIntervalInSecs; }
    public void setRenewalIntervalInSecs(long value) { this.renewalIntervalInSecs = value; }

    public long getDurationInSecs() { return durationInSecs; }
    public void setDurationInSecs(long value) { this.durationInSecs = value; }

    public long getRegistrationTimestamp() { return registrationTimestamp; }
    public void setRegistrationTimestamp(long value) { this.registrationTimestamp = value; }

    public long getLastRenewalTimestamp() { return lastRenewalTimestamp; }
    public void setLastRenewalTimestamp(long value) { this.lastRenewalTimestamp = value; }

    public long getEvictionTimestamp() { return evictionTimestamp; }
    public void setEvictionTimestamp(long value) { this.evictionTimestamp = value; }

    public long getServiceUpTimestamp() { return serviceUpTimestamp; }
    public void setServiceUpTimestamp(long value) { this.serviceUpTimestamp = value; }
}
