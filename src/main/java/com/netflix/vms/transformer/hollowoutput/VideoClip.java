package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoClip implements Cloneable {

    public Video videoId = null;
    public long msDuration = java.lang.Long.MIN_VALUE;
    public VideoMoment videoMoment = null;
    public List<StreamDownloadable> downloadableList = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoClip))
            return false;

        VideoClip o = (VideoClip) other;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
        if(o.msDuration != msDuration) return false;
        if(o.videoMoment == null) {
            if(videoMoment != null) return false;
        } else if(!o.videoMoment.equals(videoMoment)) return false;
        if(o.downloadableList == null) {
            if(downloadableList != null) return false;
        } else if(!o.downloadableList.equals(downloadableList)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (videoId == null ? 1237 : videoId.hashCode());
        hashCode = hashCode * 31 + (int) (msDuration ^ (msDuration >>> 32));
        hashCode = hashCode * 31 + (videoMoment == null ? 1237 : videoMoment.hashCode());
        hashCode = hashCode * 31 + (downloadableList == null ? 1237 : downloadableList.hashCode());
        return hashCode;
    }

    public VideoClip clone() {
        try {
            VideoClip clone = (VideoClip)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}