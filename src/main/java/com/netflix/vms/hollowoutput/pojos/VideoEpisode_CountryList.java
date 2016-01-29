package com.netflix.vms.hollowoutput.pojos;


public class VideoEpisode_CountryList {

    public ISOCountry country;
    public VideoEpisode item;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoEpisode_CountryList))
            return false;

        VideoEpisode_CountryList o = (VideoEpisode_CountryList) other;
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