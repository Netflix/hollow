package com.netflix.vms.hollowoutput.pojos;

import java.util.Set;

public class StreamDataDescriptor {

    public int runTimeInSeconds;
    public int bitrate;
    public PixelAspect pixelAspect;
    public VideoResolution videoResolution;
    public Set<ISOCountry> cacheDeployedCountries;
    public VideoMoment videoMoment;
    public int imageCount;
    public TargetDimensions targetDimensions;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDataDescriptor))
            return false;

        StreamDataDescriptor o = (StreamDataDescriptor) other;
        if(o.runTimeInSeconds != runTimeInSeconds) return false;
        if(o.bitrate != bitrate) return false;
        if(o.pixelAspect == null) {
            if(pixelAspect != null) return false;
        } else if(!o.pixelAspect.equals(pixelAspect)) return false;
        if(o.videoResolution == null) {
            if(videoResolution != null) return false;
        } else if(!o.videoResolution.equals(videoResolution)) return false;
        if(o.cacheDeployedCountries == null) {
            if(cacheDeployedCountries != null) return false;
        } else if(!o.cacheDeployedCountries.equals(cacheDeployedCountries)) return false;
        if(o.videoMoment == null) {
            if(videoMoment != null) return false;
        } else if(!o.videoMoment.equals(videoMoment)) return false;
        if(o.imageCount != imageCount) return false;
        if(o.targetDimensions == null) {
            if(targetDimensions != null) return false;
        } else if(!o.targetDimensions.equals(targetDimensions)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}