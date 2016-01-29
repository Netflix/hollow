package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;

public class DrmInfoData {

    public int packageId;
    public Map<Long, DrmInfo> downloadableIdToDrmInfoMap;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}