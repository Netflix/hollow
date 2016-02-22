package com.netflix.vms.transformer.hollowoutput;


public class TrickPlayItem implements Cloneable {

    public Video videoId = null;
    public int imageCount = java.lang.Integer.MIN_VALUE;
    public TrickPlayDownloadable trickPlayDownloadable = null;

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

    public TrickPlayItem clone() {
        try {
            return (TrickPlayItem)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}