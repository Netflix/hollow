package com.netflix.vms.transformer.hollowoutput;


public class CompleteVideoFacetData {

    public VideoMetaData videoMetaData;
    public VideoCollectionsData videoCollectionsData;
    public VideoMiscData videoMiscData;
    public VideoImages videoImages;
    public VideoMediaData videoMediaData;

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