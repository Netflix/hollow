package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class VMSAvailabilityWindow implements Cloneable {

    public Date startDate = null;
    public Date endDate = null;
    public boolean onHold = false;
    public int bundledAssetsGroupId = java.lang.Integer.MIN_VALUE;
    public Map<Integer, WindowPackageContractInfo> windowInfosByPackageId = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VMSAvailabilityWindow))
            return false;

        VMSAvailabilityWindow o = (VMSAvailabilityWindow) other;
        if(o.startDate == null) {
            if(startDate != null) return false;
        } else if(!o.startDate.equals(startDate)) return false;
        if(o.endDate == null) {
            if(endDate != null) return false;
        } else if(!o.endDate.equals(endDate)) return false;
        if(onHold != o.onHold) return false;
        if(o.bundledAssetsGroupId != bundledAssetsGroupId) return false;
        if(o.windowInfosByPackageId == null) {
            if(windowInfosByPackageId != null) return false;
        } else if(!o.windowInfosByPackageId.equals(windowInfosByPackageId)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (startDate == null ? 1237 : startDate.hashCode());
        hashCode = hashCode * 31 + (endDate == null ? 1237 : endDate.hashCode());
        hashCode = hashCode * 31 + (onHold ? 1231 : 1237);
        hashCode = hashCode * 31 + bundledAssetsGroupId;
        hashCode = hashCode * 31 + (windowInfosByPackageId == null ? 1237 : windowInfosByPackageId.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VMSAvailabilityWindow{");
        builder.append("startDate=").append(startDate);
        builder.append(",endDate=").append(endDate);
        builder.append(",onHold=").append(onHold);
        builder.append(",bundledAssetsGroupId=").append(bundledAssetsGroupId);
        builder.append(",windowInfosByPackageId=").append(windowInfosByPackageId);
        builder.append("}");
        return builder.toString();
    }

    public VMSAvailabilityWindow clone() {
        try {
            VMSAvailabilityWindow clone = (VMSAvailabilityWindow)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
