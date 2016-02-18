package com.netflix.vms.transformer.hollowoutput;


public class NamedCollectionHolder_CountryList {

    public ISOCountry country;
    public NamedCollectionHolder item;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof NamedCollectionHolder_CountryList))
            return false;

        NamedCollectionHolder_CountryList o = (NamedCollectionHolder_CountryList) other;
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