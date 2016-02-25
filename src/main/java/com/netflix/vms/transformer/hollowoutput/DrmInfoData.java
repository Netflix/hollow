package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class DrmInfoData implements Cloneable {

    public int packageId = java.lang.Integer.MIN_VALUE;
    public Map<Long, DrmInfo> downloadableIdToDrmInfoMap = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmInfoData))
            return false;

        DrmInfoData o = (DrmInfoData) other;
        if(o.packageId != packageId) return false;
        if(o.downloadableIdToDrmInfoMap == null) {
            if(downloadableIdToDrmInfoMap != null) return false;
        } else if(!o.downloadableIdToDrmInfoMap.equals(downloadableIdToDrmInfoMap)) return false;
        return true;
    }

    public DrmInfoData clone() {
        try {
            DrmInfoData clone = (DrmInfoData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}