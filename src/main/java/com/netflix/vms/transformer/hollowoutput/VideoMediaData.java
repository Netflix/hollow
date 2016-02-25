package com.netflix.vms.transformer.hollowoutput;


public class VideoMediaData implements Cloneable {

    public boolean isAvailableForED = false;
    public boolean isGoLive = false;
    public boolean isOriginal = false;
    public boolean isAutoPlayEnabled = false;
    public Date dvdReleaseDate = null;
    public boolean hasLocalAudio = false;
    public boolean hasLocalText = false;
    public int approximateRuntimeInSeconds = java.lang.Integer.MIN_VALUE;
    public boolean isLanguageOverride = false;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoMediaData))
            return false;

        VideoMediaData o = (VideoMediaData) other;
        if(o.isAvailableForED != isAvailableForED) return false;
        if(o.isGoLive != isGoLive) return false;
        if(o.isOriginal != isOriginal) return false;
        if(o.isAutoPlayEnabled != isAutoPlayEnabled) return false;
        if(o.dvdReleaseDate == null) {
            if(dvdReleaseDate != null) return false;
        } else if(!o.dvdReleaseDate.equals(dvdReleaseDate)) return false;
        if(o.hasLocalAudio != hasLocalAudio) return false;
        if(o.hasLocalText != hasLocalText) return false;
        if(o.approximateRuntimeInSeconds != approximateRuntimeInSeconds) return false;
        if(o.isLanguageOverride != isLanguageOverride) return false;
        return true;
    }

    public VideoMediaData clone() {
        try {
            VideoMediaData clone = (VideoMediaData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}