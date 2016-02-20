package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoMoment {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}