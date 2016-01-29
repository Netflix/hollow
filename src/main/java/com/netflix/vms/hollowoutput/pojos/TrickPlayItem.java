package com.netflix.vms.hollowoutput.pojos;


public class TrickPlayItem {

    public Video videoId;
    public int imageCount;
    public TrickPlayDownloadable trickPlayDownloadable;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TrickPlayItem))
            return false;

        TrickPlayItem o = (TrickPlayItem) other;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
        if(o.imageCount != imageCount) return false;
        if(o.trickPlayDownloadable == null) {
            if(trickPlayDownloadable != null) return false;
        } else if(!o.trickPlayDownloadable.equals(trickPlayDownloadable)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}