package com.netflix.vms.transformer.hollowoutput;


public class AvailabilityWindow implements Cloneable {

    public Date startDate = null;
    public Date endDate = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (startDate == null ? 1237 : startDate.hashCode());
        hashCode = hashCode * 31 + (endDate == null ? 1237 : endDate.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AvailabilityWindow{");
        builder.append("startDate=").append(startDate);
        builder.append(",endDate=").append(endDate);
        builder.append("}");
        return builder.toString();
    }

    public AvailabilityWindow clone() {
        try {
            AvailabilityWindow clone = (AvailabilityWindow)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}