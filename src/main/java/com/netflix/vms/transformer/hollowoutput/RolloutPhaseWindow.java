package com.netflix.vms.transformer.hollowoutput;


public class RolloutPhaseWindow {

    public int phaseOrdinal = java.lang.Integer.MIN_VALUE;
    public AvailabilityWindow phaseWindow = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutPhaseWindow))
            return false;

        RolloutPhaseWindow o = (RolloutPhaseWindow) other;
        if(o.phaseOrdinal != phaseOrdinal) return false;
        if(o.phaseWindow == null) {
            if(phaseWindow != null) return false;
        } else if(!o.phaseWindow.equals(phaseWindow)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}