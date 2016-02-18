package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;
import java.util.Arrays;
import java.util.List;

public class RolloutSummary {

    public char[] type;
    public Video video;
    public Map<Integer, RolloutInfo> rolloutInfoMap;
    public Map<ISOCountry, List<RolloutPhaseWindow>> phaseWindowMap;
    public List<Phase> allPhases;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}