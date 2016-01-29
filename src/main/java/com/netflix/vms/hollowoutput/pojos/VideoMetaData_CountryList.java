package com.netflix.vms.hollowoutput.pojos;


public class VideoMetaData_CountryList {

    public ISOCountry country;
    public VideoMetaData item;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}