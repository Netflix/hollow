package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoMoment implements Cloneable {

    public Strings videoMomentTypeName = null;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;
    public long msOffset = java.lang.Long.MIN_VALUE;
    public long runtimeMs = java.lang.Long.MIN_VALUE;
    public List<Strings> momentTags = null;
    public int packageId = java.lang.Integer.MIN_VALUE;
    public long bifIndex = java.lang.Long.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoMoment))
            return false;

        VideoMoment o = (VideoMoment) other;
        if(o.videoMomentTypeName == null) {
            if(videoMomentTypeName != null) return false;
        } else if(!o.videoMomentTypeName.equals(videoMomentTypeName)) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.msOffset != msOffset) return false;
        if(o.runtimeMs != runtimeMs) return false;
        if(o.momentTags == null) {
            if(momentTags != null) return false;
        } else if(!o.momentTags.equals(momentTags)) return false;
        if(o.packageId != packageId) return false;
        if(o.bifIndex != bifIndex) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (videoMomentTypeName == null ? 1237 : videoMomentTypeName.hashCode());
        hashCode = hashCode * 31 + sequenceNumber;
        hashCode = hashCode * 31 + (int) (msOffset ^ (msOffset >>> 32));
        hashCode = hashCode * 31 + (int) (runtimeMs ^ (runtimeMs >>> 32));
        hashCode = hashCode * 31 + (momentTags == null ? 1237 : momentTags.hashCode());
        hashCode = hashCode * 31 + packageId;
        hashCode = hashCode * 31 + (int) (bifIndex ^ (bifIndex >>> 32));
        return hashCode;
    }

    public VideoMoment clone() {
        try {
            VideoMoment clone = (VideoMoment)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}