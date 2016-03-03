package com.netflix.vms.transformer.hollowoutput;


public class CertificationSystem implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public Strings officialURL = null;
    public ISOCountry country = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CertificationSystem))
            return false;

        CertificationSystem o = (CertificationSystem) other;
        if(o.id != id) return false;
        if(o.officialURL == null) {
            if(officialURL != null) return false;
        } else if(!o.officialURL.equals(officialURL)) return false;
        if(o.country == null) {
            if(country != null) return false;
        } else if(!o.country.equals(country)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (officialURL == null ? 1237 : officialURL.hashCode());
        hashCode = hashCode * 31 + (country == null ? 1237 : country.hashCode());
        return hashCode;
    }

    public CertificationSystem clone() {
        try {
            CertificationSystem clone = (CertificationSystem)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}