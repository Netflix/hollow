package com.netflix.vms.transformer.hollowoutput;


public class AvailabilityWindow {

    public Date startDate;
    public Date endDate;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AvailabilityWindow))
            return false;

        AvailabilityWindow o = (AvailabilityWindow) other;
        if(o.startDate == null) {
            if(startDate != null) return false;
        } else if(!o.startDate.equals(startDate)) return false;
        if(o.endDate == null) {
            if(endDate != null) return false;
        } else if(!o.endDate.equals(endDate)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}