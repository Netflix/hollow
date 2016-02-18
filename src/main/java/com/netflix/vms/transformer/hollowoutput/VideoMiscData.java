package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoMiscData {

    public List<VideoAward> videoAwards;
    public ICSMReview cSMReview;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}