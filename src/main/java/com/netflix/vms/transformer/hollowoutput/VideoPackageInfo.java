package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoPackageInfo implements Cloneable {

    public int packageId = java.lang.Integer.MIN_VALUE;
    public Set<VideoFormatDescriptor> formats = null;
    public boolean isAvailableIn3D = false;
    public int runtimeInSeconds = java.lang.Integer.MIN_VALUE;
    public List<Strings> soundTypes = null;
    public List<Strings> screenFormats = null;
    public List<VideoMoment> phoneSnacks = null;
    public Map<Strings, List<VideoImage>> stillImagesMap = null;
    public Map<Strings, List<VideoClip>> videoClipMap = null;
    public Map<TrickPlayType, TrickPlayItem> trickPlayMap = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoPackageInfo))
            return false;

        VideoPackageInfo o = (VideoPackageInfo) other;
        if(o.packageId != packageId) return false;
        if(o.formats == null) {
            if(formats != null) return false;
        } else if(!o.formats.equals(formats)) return false;
        if(o.isAvailableIn3D != isAvailableIn3D) return false;
        if(o.runtimeInSeconds != runtimeInSeconds) return false;
        if(o.soundTypes == null) {
            if(soundTypes != null) return false;
        } else if(!o.soundTypes.equals(soundTypes)) return false;
        if(o.screenFormats == null) {
            if(screenFormats != null) return false;
        } else if(!o.screenFormats.equals(screenFormats)) return false;
        if(o.phoneSnacks == null) {
            if(phoneSnacks != null) return false;
        } else if(!o.phoneSnacks.equals(phoneSnacks)) return false;
        if(o.stillImagesMap == null) {
            if(stillImagesMap != null) return false;
        } else if(!o.stillImagesMap.equals(stillImagesMap)) return false;
        if(o.videoClipMap == null) {
            if(videoClipMap != null) return false;
        } else if(!o.videoClipMap.equals(videoClipMap)) return false;
        if(o.trickPlayMap == null) {
            if(trickPlayMap != null) return false;
        } else if(!o.trickPlayMap.equals(trickPlayMap)) return false;
        return true;
    }

    public VideoPackageInfo clone() {
        try {
            VideoPackageInfo clone = (VideoPackageInfo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}