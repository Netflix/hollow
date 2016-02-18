package com.netflix.vms.transformer.hollowoutput;


public class WindowPackageContractInfo {

    public VideoContractInfo videoContractInfo;
    public VideoPackageInfo videoPackageInfo;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof WindowPackageContractInfo))
            return false;

        WindowPackageContractInfo o = (WindowPackageContractInfo) other;
        if(o.videoContractInfo == null) {
            if(videoContractInfo != null) return false;
        } else if(!o.videoContractInfo.equals(videoContractInfo)) return false;
        if(o.videoPackageInfo == null) {
            if(videoPackageInfo != null) return false;
        } else if(!o.videoPackageInfo.equals(videoPackageInfo)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}