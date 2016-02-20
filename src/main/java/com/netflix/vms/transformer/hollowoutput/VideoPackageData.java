package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;

public class VideoPackageData {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}