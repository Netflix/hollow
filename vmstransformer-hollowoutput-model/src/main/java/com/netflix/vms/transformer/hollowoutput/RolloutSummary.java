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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(type);
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        hashCode = hashCode * 31 + (rolloutInfoMap == null ? 1237 : rolloutInfoMap.hashCode());
        hashCode = hashCode * 31 + (phaseWindowMap == null ? 1237 : phaseWindowMap.hashCode());
        hashCode = hashCode * 31 + (allPhases == null ? 1237 : allPhases.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutSummary{");
        builder.append("type=").append(type);
        builder.append(",video=").append(video);
        builder.append(",rolloutInfoMap=").append(rolloutInfoMap);
        builder.append(",phaseWindowMap=").append(phaseWindowMap);
        builder.append(",allPhases=").append(allPhases);
        builder.append("}");
        return builder.toString();
    }

    public RolloutSummary clone() {
        try {
            RolloutSummary clone = (RolloutSummary)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
