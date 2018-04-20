package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="RolloutPhaseWindow")
public class RolloutPhaseWindow implements Cloneable {

    public long endDate = java.lang.Long.MIN_VALUE;
    public long startDate = java.lang.Long.MIN_VALUE;

    public RolloutPhaseWindow setEndDate(long endDate) {
        this.endDate = endDate;
        return this;
    }
    public RolloutPhaseWindow setStartDate(long startDate) {
        this.startDate = startDate;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutPhaseWindow))
            return false;

        RolloutPhaseWindow o = (RolloutPhaseWindow) other;
        if(o.endDate != endDate) return false;
        if(o.startDate != startDate) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (endDate ^ (endDate >>> 32));
        hashCode = hashCode * 31 + (int) (startDate ^ (startDate >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutPhaseWindow{");
        builder.append("endDate=").append(endDate);
        builder.append(",startDate=").append(startDate);
        builder.append("}");
        return builder.toString();
    }

    public RolloutPhaseWindow clone() {
        try {
            RolloutPhaseWindow clone = (RolloutPhaseWindow)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}