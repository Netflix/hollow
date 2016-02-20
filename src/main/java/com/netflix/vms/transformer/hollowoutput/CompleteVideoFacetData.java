package com.netflix.vms.transformer.hollowoutput;


public class CompleteVideoFacetData {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}