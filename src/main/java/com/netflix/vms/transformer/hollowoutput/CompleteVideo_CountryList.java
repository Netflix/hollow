package com.netflix.vms.transformer.hollowoutput;


public class CompleteVideo_CountryList implements Cloneable {

    public ISOCountry country = null;
    public CompleteVideo item = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CompleteVideo_CountryList))
            return false;

        CompleteVideo_CountryList o = (CompleteVideo_CountryList) other;
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

    public CompleteVideo_CountryList clone() {
        try {
            CompleteVideo_CountryList clone = (CompleteVideo_CountryList)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}