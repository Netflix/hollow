package com.netflix.vms.hollowoutput.pojos;


public class CompleteVideo {

    public Video id;
    public ISOCountry country;
    public CompleteVideoFacetData facetData;
    public CompleteVideoCountrySpecificData countrySpecificData;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CompleteVideo))
            return false;

        CompleteVideo o = (CompleteVideo) other;
        if(o.id == null) {
            if(id != null) return false;
        } else if(!o.id.equals(id)) return false;
        if(o.country == null) {
            if(country != null) return false;
        } else if(!o.country.equals(country)) return false;
        if(o.facetData == null) {
            if(facetData != null) return false;
        } else if(!o.facetData.equals(facetData)) return false;
        if(o.countrySpecificData == null) {
            if(countrySpecificData != null) return false;
        } else if(!o.countrySpecificData.equals(countrySpecificData)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}