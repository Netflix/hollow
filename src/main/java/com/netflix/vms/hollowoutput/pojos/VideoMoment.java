package com.netflix.vms.hollowoutput.pojos;

import java.util.List;

public class VideoMoment {

    public Strings videoMomentTypeName;
    public int sequenceNumber;
    public long msOffset;
    public long runtimeMs;
    public List<Strings> momentTags;
    public int packageId;
    public long bifIndex;

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