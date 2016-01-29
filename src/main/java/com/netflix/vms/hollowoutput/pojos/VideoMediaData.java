package com.netflix.vms.hollowoutput.pojos;


public class VideoMediaData {

    public boolean isAvailableForED;
    public boolean isGoLive;
    public boolean isOriginal;
    public boolean isAutoPlayEnabled;
    public Date dvdReleaseDate;
    public boolean hasLocalAudio;
    public boolean hasLocalText;
    public int approximateRuntimeInSeconds;
    public boolean isLanguageOverride;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}