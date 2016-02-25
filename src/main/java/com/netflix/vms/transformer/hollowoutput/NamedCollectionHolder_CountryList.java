package com.netflix.vms.transformer.hollowoutput;


public class NamedCollectionHolder_CountryList implements Cloneable {

    public ISOCountry country = null;
    public NamedCollectionHolder item = null;

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

    public NamedCollectionHolder_CountryList clone() {
        try {
            NamedCollectionHolder_CountryList clone = (NamedCollectionHolder_CountryList)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}