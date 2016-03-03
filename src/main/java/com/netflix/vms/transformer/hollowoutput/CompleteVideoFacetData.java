package com.netflix.vms.transformer.hollowoutput;


public class CompleteVideoFacetData implements Cloneable {

    public VideoMetaData videoMetaData = null;
    public VideoCollectionsData videoCollectionsData = null;
    public VideoMiscData videoMiscData = null;
    public VideoImages videoImages = null;
    public VideoMediaData videoMediaData = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CompleteVideoFacetData))
            return false;

        CompleteVideoFacetData o = (CompleteVideoFacetData) other;
        if(o.videoMetaData == null) {
            if(videoMetaData != null) return false;
        } else if(!o.videoMetaData.equals(videoMetaData)) return false;
        if(o.videoCollectionsData == null) {
            if(videoCollectionsData != null) return false;
        } else if(!o.videoCollectionsData.equals(videoCollectionsData)) return false;
        if(o.videoMiscData == null) {
            if(videoMiscData != null) return false;
        } else if(!o.videoMiscData.equals(videoMiscData)) return false;
        if(o.videoImages == null) {
            if(videoImages != null) return false;
        } else if(!o.videoImages.equals(videoImages)) return false;
        if(o.videoMediaData == null) {
            if(videoMediaData != null) return false;
        } else if(!o.videoMediaData.equals(videoMediaData)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (videoMetaData == null ? 1237 : videoMetaData.hashCode());
        hashCode = hashCode * 31 + (videoCollectionsData == null ? 1237 : videoCollectionsData.hashCode());
        hashCode = hashCode * 31 + (videoMiscData == null ? 1237 : videoMiscData.hashCode());
        hashCode = hashCode * 31 + (videoImages == null ? 1237 : videoImages.hashCode());
        hashCode = hashCode * 31 + (videoMediaData == null ? 1237 : videoMediaData.hashCode());
        return hashCode;
    }

    public CompleteVideoFacetData clone() {
        try {
            CompleteVideoFacetData clone = (CompleteVideoFacetData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}