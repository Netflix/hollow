package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.util.Map;

@HollowPrimaryKey(fields="video")
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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        hashCode = hashCode * 31 + (summaryMap == null ? 1237 : summaryMap.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutVideo{");
        builder.append("video=").append(video);
        builder.append(",summaryMap=").append(summaryMap);
        builder.append("}");
        return builder.toString();
    }

    public RolloutVideo clone() {
        try {
            RolloutVideo clone = (RolloutVideo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}