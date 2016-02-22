package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class RolloutVideo implements Cloneable {

    public Video video = null;
    public Map<Strings, RolloutSummary> summaryMap = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutVideo))
            return false;

        RolloutVideo o = (RolloutVideo) other;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(o.summaryMap == null) {
            if(summaryMap != null) return false;
        } else if(!o.summaryMap.equals(summaryMap)) return false;
        return true;
    }

    public RolloutVideo clone() {
        try {
            return (RolloutVideo)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}