package com.netflix.vms.transformer.hollowoutput;


public class RolloutPhaseWindow implements Cloneable {

    public AvailabilityWindow phaseWindow = null;

    public RolloutPhaseWindow() { }

    public RolloutPhaseWindow(AvailabilityWindow value) {
        this.phaseWindow = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutPhaseWindow))
            return false;

        RolloutPhaseWindow o = (RolloutPhaseWindow) other;
        if(o.phaseWindow == null) {
            if(phaseWindow != null) return false;
        } else if(!o.phaseWindow.equals(phaseWindow)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (phaseWindow == null ? 1237 : phaseWindow.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutPhaseWindow{");
        builder.append("phaseWindow=").append(phaseWindow);
        builder.append("}");
        return builder.toString();
    }

    public RolloutPhaseWindow clone() {
        try {
            RolloutPhaseWindow clone = (RolloutPhaseWindow)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
