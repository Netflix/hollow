package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ExplicitDate")
public class ExplicitDate implements Cloneable {

    public int monthOfYear = java.lang.Integer.MIN_VALUE;
    public int year = java.lang.Integer.MIN_VALUE;
    public int dayOfMonth = java.lang.Integer.MIN_VALUE;

    public ExplicitDate setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
        return this;
    }
    public ExplicitDate setYear(int year) {
        this.year = year;
        return this;
    }
    public ExplicitDate setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ExplicitDate))
            return false;

        ExplicitDate o = (ExplicitDate) other;
        if(o.monthOfYear != monthOfYear) return false;
        if(o.year != year) return false;
        if(o.dayOfMonth != dayOfMonth) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + monthOfYear;
        hashCode = hashCode * 31 + year;
        hashCode = hashCode * 31 + dayOfMonth;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ExplicitDate{");
        builder.append("monthOfYear=").append(monthOfYear);
        builder.append(",year=").append(year);
        builder.append(",dayOfMonth=").append(dayOfMonth);
        builder.append("}");
        return builder.toString();
    }

    public ExplicitDate clone() {
        try {
            ExplicitDate clone = (ExplicitDate)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}