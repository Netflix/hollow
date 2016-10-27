package com.netflix.vms.transformer.hollowoutput;


public class ExplicitDate implements Cloneable {

    public Integer year = null;
    public Integer month = null;
    public Integer day = null;

    @Override
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ExplicitDate))
            return false;

        ExplicitDate o = (ExplicitDate) other;
        if(o.year == null) {
            if(year != null) return false;
        } else if(!o.year.equals(year)) return false;
        if(o.month == null) {
            if(month != null) return false;
        } else if(!o.month.equals(month)) return false;
        if(o.day == null) {
            if(day != null) return false;
        } else if(!o.day.equals(day)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (year == null ? 1237 : year.hashCode());
        hashCode = hashCode * 31 + (month == null ? 1237 : month.hashCode());
        hashCode = hashCode * 31 + (day == null ? 1237 : day.hashCode());
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExplicitDate [year=");
        builder.append(year);
        builder.append(", month=");
        builder.append(month);
        builder.append(", day=");
        builder.append(day);
        builder.append(", __assigned_ordinal=");
        builder.append(__assigned_ordinal);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public ExplicitDate clone() {
        try {
            ExplicitDate clone = (ExplicitDate)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}