package com.netflix.vms.transformer.hollowoutput;


public class CompleteVideo implements Cloneable {

    public Video id = null;
    public ISOCountry country = null;
    public CompleteVideoFacetData facetData = null;
    public CompleteVideoCountrySpecificData countrySpecificData = null;

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

    public CompleteVideo clone() {
        try {
            CompleteVideo clone = (CompleteVideo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}