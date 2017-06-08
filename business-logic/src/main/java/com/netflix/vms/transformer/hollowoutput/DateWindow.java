package com.netflix.vms.transformer.hollowoutput;


public class DateWindow implements Cloneable {

    public long startDateTimestamp = java.lang.Long.MIN_VALUE;
    public long endDateTimestamp = java.lang.Long.MIN_VALUE;
    public boolean onHold = false;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DateWindow))
            return false;

        DateWindow o = (DateWindow) other;
        if(o.startDateTimestamp != startDateTimestamp) return false;
        if(o.endDateTimestamp != endDateTimestamp) return false;
        if(onHold != o.onHold) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (startDateTimestamp ^ (startDateTimestamp >>> 32));
        hashCode = hashCode * 31 + (int) (endDateTimestamp ^ (endDateTimestamp >>> 32));
        hashCode = hashCode * 31 + (onHold ? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DateWindow{");
        builder.append("startDateTimestamp=").append(startDateTimestamp);
        builder.append(",endDateTimestamp=").append(endDateTimestamp);
        builder.append(",onHold=").append(onHold);
        builder.append("}");
        return builder.toString();
    }

    public DateWindow clone() {
        try {
            DateWindow clone = (DateWindow)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
