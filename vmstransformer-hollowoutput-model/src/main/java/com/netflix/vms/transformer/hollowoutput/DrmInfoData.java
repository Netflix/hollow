package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Map;

@HollowPrimaryKey(fields="packageId")
public class DrmInfoData implements Cloneable {

    public int packageId = java.lang.Integer.MIN_VALUE;
    public Map<DownloadableId, DrmInfo> downloadableIdToDrmInfoMap = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + packageId;
        hashCode = hashCode * 31 + (downloadableIdToDrmInfoMap == null ? 1237 : downloadableIdToDrmInfoMap.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DrmInfoData{");
        builder.append("packageId=").append(packageId);
        builder.append(",downloadableIdToDrmInfoMap=").append(downloadableIdToDrmInfoMap);
        builder.append("}");
        return builder.toString();
    }

    public DrmInfoData clone() {
        try {
            DrmInfoData clone = (DrmInfoData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
