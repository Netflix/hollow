package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;

public class VideoPackageData implements Cloneable {

    public Video videoId = null;
    public Set<PackageData> packages = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoPackageData))
            return false;

        VideoPackageData o = (VideoPackageData) other;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
        if(o.packages == null) {
            if(packages != null) return false;
        } else if(!o.packages.equals(packages)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (videoId == null ? 1237 : videoId.hashCode());
        hashCode = hashCode * 31 + (packages == null ? 1237 : packages.hashCode());
        return hashCode;
    }

    public VideoPackageData clone() {
        try {
            VideoPackageData clone = (VideoPackageData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}