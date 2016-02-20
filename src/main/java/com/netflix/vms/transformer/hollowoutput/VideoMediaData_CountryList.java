package com.netflix.vms.transformer.hollowoutput;


public class VideoMediaData_CountryList {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}