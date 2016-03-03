package com.netflix.vms.transformer.hollowoutput;


public class WindowPackageContractInfo implements Cloneable {

    public VideoContractInfo videoContractInfo = null;
    public VideoPackageInfo videoPackageInfo = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (videoContractInfo == null ? 1237 : videoContractInfo.hashCode());
        hashCode = hashCode * 31 + (videoPackageInfo == null ? 1237 : videoPackageInfo.hashCode());
        return hashCode;
    }

    public WindowPackageContractInfo clone() {
        try {
            WindowPackageContractInfo clone = (WindowPackageContractInfo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}