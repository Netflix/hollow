package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ReleaseDate")
public class ReleaseDate implements Cloneable {

    public String releaseDateType = null;
    public String distributorName = null;
    public int month = java.lang.Integer.MIN_VALUE;
    public int year = java.lang.Integer.MIN_VALUE;
    public int day = java.lang.Integer.MIN_VALUE;
    public String bcp47code = null;

    public ReleaseDate setReleaseDateType(String releaseDateType) {
        this.releaseDateType = releaseDateType;
        return this;
    }
    public ReleaseDate setDistributorName(String distributorName) {
        this.distributorName = distributorName;
        return this;
    }
    public ReleaseDate setMonth(int month) {
        this.month = month;
        return this;
    }
    public ReleaseDate setYear(int year) {
        this.year = year;
        return this;
    }
    public ReleaseDate setDay(int day) {
        this.day = day;
        return this;
    }
    public ReleaseDate setBcp47code(String bcp47code) {
        this.bcp47code = bcp47code;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ReleaseDate))
            return false;

        ReleaseDate o = (ReleaseDate) other;
        if(o.releaseDateType == null) {
            if(releaseDateType != null) return false;
        } else if(!o.releaseDateType.equals(releaseDateType)) return false;
        if(o.distributorName == null) {
            if(distributorName != null) return false;
        } else if(!o.distributorName.equals(distributorName)) return false;
        if(o.month != month) return false;
        if(o.year != year) return false;
        if(o.day != day) return false;
        if(o.bcp47code == null) {
            if(bcp47code != null) return false;
        } else if(!o.bcp47code.equals(bcp47code)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (releaseDateType == null ? 1237 : releaseDateType.hashCode());
        hashCode = hashCode * 31 + (distributorName == null ? 1237 : distributorName.hashCode());
        hashCode = hashCode * 31 + month;
        hashCode = hashCode * 31 + year;
        hashCode = hashCode * 31 + day;
        hashCode = hashCode * 31 + (bcp47code == null ? 1237 : bcp47code.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ReleaseDate{");
        builder.append("releaseDateType=").append(releaseDateType);
        builder.append(",distributorName=").append(distributorName);
        builder.append(",month=").append(month);
        builder.append(",year=").append(year);
        builder.append(",day=").append(day);
        builder.append(",bcp47code=").append(bcp47code);
        builder.append("}");
        return builder.toString();
    }

    public ReleaseDate clone() {
        try {
            ReleaseDate clone = (ReleaseDate)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}