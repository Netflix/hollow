package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;

public class StreamDataDescriptor implements Cloneable {

    public int runTimeInSeconds = java.lang.Integer.MIN_VALUE;
    public int bitrate = java.lang.Integer.MIN_VALUE;
    public int peakBitrate = java.lang.Integer.MIN_VALUE;
    public PixelAspect pixelAspect = null;
    public VideoResolution videoResolution = null;
    public Set<ISOCountry> cacheDeployedCountries = null;
    public int imageCount = java.lang.Integer.MIN_VALUE;
    public TargetDimensions targetDimensions = null;

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
        if(o.imageCount != imageCount) return false;
        if(o.targetDimensions == null) {
            if(targetDimensions != null) return false;
        } else if(!o.targetDimensions.equals(targetDimensions)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + runTimeInSeconds;
        hashCode = hashCode * 31 + bitrate;
        hashCode = hashCode * 31 + (pixelAspect == null ? 1237 : pixelAspect.hashCode());
        hashCode = hashCode * 31 + (videoResolution == null ? 1237 : videoResolution.hashCode());
        hashCode = hashCode * 31 + (cacheDeployedCountries == null ? 1237 : cacheDeployedCountries.hashCode());
        hashCode = hashCode * 31 + imageCount;
        hashCode = hashCode * 31 + (targetDimensions == null ? 1237 : targetDimensions.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamDataDescriptor{");
        builder.append("runTimeInSeconds=").append(runTimeInSeconds);
        builder.append(",bitrate=").append(bitrate);
        builder.append(",pixelAspect=").append(pixelAspect);
        builder.append(",videoResolution=").append(videoResolution);
        builder.append(",cacheDeployedCountries=").append(cacheDeployedCountries);
        builder.append(",imageCount=").append(imageCount);
        builder.append(",targetDimensions=").append(targetDimensions);
        builder.append("}");
        return builder.toString();
    }

    public StreamDataDescriptor clone() {
        try {
            StreamDataDescriptor clone = (StreamDataDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}