package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoPackageInfo implements Cloneable {

    public int packageId = java.lang.Integer.MIN_VALUE;
    public Set<VideoFormatDescriptor> formats = null;
    public boolean isAvailableIn3D = false;
    public boolean isDefaultPackage = true;
    public int runtimeInSeconds = java.lang.Integer.MIN_VALUE;
    public List<Strings> soundTypes = null;
    public List<Strings> screenFormats = null;
    public Map<TrickPlayType, TrickPlayItem> trickPlayMap = null;
    public long startMomentOffsetInMillis = -1;
    public long endMomentOffsetInMillis = -1;

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
        if(o.trickPlayMap == null) {
            if(trickPlayMap != null) return false;
        } else if(!o.trickPlayMap.equals(trickPlayMap)) return false;
        if (o.startMomentOffsetInMillis!=this.startMomentOffsetInMillis) return false;
        if (o.endMomentOffsetInMillis!=endMomentOffsetInMillis) return false;
        if (o.isDefaultPackage!=this.isDefaultPackage) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + packageId;
        hashCode = hashCode * 31 + (formats == null ? 1237 : formats.hashCode());
        hashCode = hashCode * 31 + (isAvailableIn3D ? 1231 : 1237);
        hashCode = hashCode * 31 + runtimeInSeconds;
        hashCode = hashCode * 31 + (soundTypes == null ? 1237 : soundTypes.hashCode());
        hashCode = hashCode * 31 + (screenFormats == null ? 1237 : screenFormats.hashCode());
        hashCode = hashCode * 31 + (trickPlayMap == null ? 1237 : trickPlayMap.hashCode());
        hashCode = hashCode * 31 + (isDefaultPackage ? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoPackageInfo{");
        builder.append("packageId=").append(packageId);
        builder.append(",formats=").append(formats);
        builder.append(",isAvailableIn3D=").append(isAvailableIn3D);
        builder.append(",isDefaultPackage=").append(isDefaultPackage);
        builder.append(",runtimeInSeconds=").append(runtimeInSeconds);
        builder.append(",soundTypes=").append(soundTypes);
        builder.append(",screenFormats=").append(screenFormats);
        builder.append(",trickPlayMap=").append(trickPlayMap);
        builder.append(",startOffsetInMillis=").append(startMomentOffsetInMillis);
        builder.append(",endOffsetInMillis=").append(endMomentOffsetInMillis);
        builder.append("}");
        return builder.toString();
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