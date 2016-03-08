package com.netflix.vms.transformer.hollowoutput;


public class VideoMetaData_CountryList implements Cloneable {

    public ISOCountry country = null;
    public VideoMetaData item = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoMetaData_CountryList))
            return false;

        VideoMetaData_CountryList o = (VideoMetaData_CountryList) other;
        if(o.country == null) {
            if(country != null) return false;
        } else if(!o.country.equals(country)) return false;
        if(o.item == null) {
            if(item != null) return false;
        } else if(!o.item.equals(item)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (country == null ? 1237 : country.hashCode());
        hashCode = hashCode * 31 + (item == null ? 1237 : item.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoMetaData_CountryList{");
        builder.append("country=").append(country);
        builder.append(",item=").append(item);
        builder.append("}");
        return builder.toString();
    }

    public VideoMetaData_CountryList clone() {
        try {
            VideoMetaData_CountryList clone = (VideoMetaData_CountryList)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}