package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoImage implements Cloneable {

    public Video videoId = null;
    public VideoMoment videoMoment = null;
    public List<ImageDownloadable> downloadableList = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoImage))
            return false;

        VideoImage o = (VideoImage) other;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
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
        hashCode = hashCode * 31 + (videoMoment == null ? 1237 : videoMoment.hashCode());
        hashCode = hashCode * 31 + (downloadableList == null ? 1237 : downloadableList.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoImage{");
        builder.append("videoId=").append(videoId);
        builder.append(",videoMoment=").append(videoMoment);
        builder.append(",downloadableList=").append(downloadableList);
        builder.append("}");
        return builder.toString();
    }

    public VideoImage clone() {
        try {
            VideoImage clone = (VideoImage)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}