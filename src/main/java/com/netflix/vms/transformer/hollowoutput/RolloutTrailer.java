package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class RolloutTrailer implements Cloneable {

    public Video video = null;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;
    public Map<SupplementalInfoType, TrailerInfo> supplementalInfos = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutTrailer))
            return false;

        RolloutTrailer o = (RolloutTrailer) other;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.supplementalInfos == null) {
            if(supplementalInfos != null) return false;
        } else if(!o.supplementalInfos.equals(supplementalInfos)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        hashCode = hashCode * 31 + sequenceNumber;
        hashCode = hashCode * 31 + (supplementalInfos == null ? 1237 : supplementalInfos.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutTrailer{");
        builder.append("video=").append(video);
        builder.append(",sequenceNumber=").append(sequenceNumber);
        builder.append(",supplementalInfos=").append(supplementalInfos);
        builder.append("}");
        return builder.toString();
    }

    public RolloutTrailer clone() {
        try {
            RolloutTrailer clone = (RolloutTrailer)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}