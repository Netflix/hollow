package com.netflix.vms.hollowoutput.pojos;


public class RolloutPhaseWindow {

    public int phaseOrdinal;
    public AvailabilityWindow phaseWindow;

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