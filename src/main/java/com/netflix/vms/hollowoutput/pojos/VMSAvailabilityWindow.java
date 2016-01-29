package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;

public class VMSAvailabilityWindow {

    public Date startDate;
    public Date endDate;
    public int bundledAssetsGroupId;
    public Map<Integer, WindowPackageContractInfo> windowInfosByPackageId;

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
        if(o.bundledAssetsGroupId != bundledAssetsGroupId) return false;
        if(o.windowInfosByPackageId == null) {
            if(windowInfosByPackageId != null) return false;
        } else if(!o.windowInfosByPackageId.equals(windowInfosByPackageId)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}