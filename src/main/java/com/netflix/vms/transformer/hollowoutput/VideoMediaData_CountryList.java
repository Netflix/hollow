package com.netflix.vms.transformer.hollowoutput;


public class VideoMediaData_CountryList implements Cloneable {

    public ISOCountry country = null;
    public VideoMediaData item = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoMediaData_CountryList))
            return false;

        VideoMediaData_CountryList o = (VideoMediaData_CountryList) other;
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
        StringBuilder builder = new StringBuilder("VideoMediaData_CountryList{");
        builder.append("country=").append(country);
        builder.append(",item=").append(item);
        builder.append("}");
        return builder.toString();
    }

    public VideoMediaData_CountryList clone() {
        try {
            VideoMediaData_CountryList clone = (VideoMediaData_CountryList)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}