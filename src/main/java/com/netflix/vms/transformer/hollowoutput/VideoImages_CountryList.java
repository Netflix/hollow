package com.netflix.vms.transformer.hollowoutput;


public class VideoImages_CountryList {

    public ISOCountry country = null;
    public VideoImages item = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoImages_CountryList))
            return false;

        VideoImages_CountryList o = (VideoImages_CountryList) other;
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