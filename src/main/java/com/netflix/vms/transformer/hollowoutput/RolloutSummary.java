package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RolloutSummary implements Cloneable {

    public char[] type = null;
    public Video video = null;
    public Map<Integer, RolloutInfo> rolloutInfoMap = null;
    public Map<ISOCountry, List<RolloutPhaseWindow>> phaseWindowMap = null;
    public List<Phase> allPhases = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutSummary))
            return false;

        RolloutSummary o = (RolloutSummary) other;
        if(!Arrays.equals(o.type, type)) return false;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(o.rolloutInfoMap == null) {
            if(rolloutInfoMap != null) return false;
        } else if(!o.rolloutInfoMap.equals(rolloutInfoMap)) return false;
        if(o.phaseWindowMap == null) {
            if(phaseWindowMap != null) return false;
        } else if(!o.phaseWindowMap.equals(phaseWindowMap)) return false;
        if(o.allPhases == null) {
            if(allPhases != null) return false;
        } else if(!o.allPhases.equals(allPhases)) return false;
        return true;
    }

    public RolloutSummary clone() {
        try {
            return (RolloutSummary)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}