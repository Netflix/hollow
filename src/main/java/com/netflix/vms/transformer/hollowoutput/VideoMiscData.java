package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoMiscData implements Cloneable {

    public List<VideoAward> videoAwards = null;
    public ICSMReview cSMReview = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoMiscData))
            return false;

        VideoMiscData o = (VideoMiscData) other;
        if(o.videoAwards == null) {
            if(videoAwards != null) return false;
        } else if(!o.videoAwards.equals(videoAwards)) return false;
        if(o.cSMReview == null) {
            if(cSMReview != null) return false;
        } else if(!o.cSMReview.equals(cSMReview)) return false;
        return true;
    }

    public VideoMiscData clone() {
        try {
            VideoMiscData clone = (VideoMiscData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}