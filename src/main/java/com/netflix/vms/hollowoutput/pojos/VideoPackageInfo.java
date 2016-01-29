package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;
import java.util.Set;
import java.util.List;

public class VideoPackageInfo {

    public int packageId;
    public Set<VideoFormatDescriptor> formats;
    public boolean isAvailableIn3D;
    public int runtimeInSeconds;
    public List<Strings> soundTypes;
    public List<Strings> screenFormats;
    public List<VideoMoment> phoneSnacks;
    public Map<Strings, List<VideoImage>> stillImagesMap;
    public Map<Strings, List<VideoClip>> videoClipMap;
    public Map<TrickPlayType, TrickPlayItem> trickPlayMap;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}