package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.write.objectmapper.NullablePrimitiveBoolean;


public class VideoMediaData implements Cloneable {

    public NullablePrimitiveBoolean isAvailableForED = null;
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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (isAvailableForED == null ? 1237 : isAvailableForED.hashCode());
        hashCode = hashCode * 31 + (isGoLive? 1231 : 1237);
        hashCode = hashCode * 31 + (isOriginal? 1231 : 1237);
        hashCode = hashCode * 31 + (isAutoPlayEnabled? 1231 : 1237);
        hashCode = hashCode * 31 + (dvdReleaseDate == null ? 1237 : dvdReleaseDate.hashCode());
        hashCode = hashCode * 31 + (hasLocalAudio? 1231 : 1237);
        hashCode = hashCode * 31 + (hasLocalText? 1231 : 1237);
        hashCode = hashCode * 31 + approximateRuntimeInSeconds;
        hashCode = hashCode * 31 + (isLanguageOverride? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoMediaData{");
        builder.append("isAvailableForED=").append(isAvailableForED);
        builder.append(",isGoLive=").append(isGoLive);
        builder.append(",isOriginal=").append(isOriginal);
        builder.append(",isAutoPlayEnabled=").append(isAutoPlayEnabled);
        builder.append(",dvdReleaseDate=").append(dvdReleaseDate);
        builder.append(",hasLocalAudio=").append(hasLocalAudio);
        builder.append(",hasLocalText=").append(hasLocalText);
        builder.append(",approximateRuntimeInSeconds=").append(approximateRuntimeInSeconds);
        builder.append(",isLanguageOverride=").append(isLanguageOverride);
        builder.append("}");
        return builder.toString();
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