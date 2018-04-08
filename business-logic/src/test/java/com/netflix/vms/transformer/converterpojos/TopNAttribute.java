package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="TopNAttribute")
public class TopNAttribute implements Cloneable {

    public String country = null;
    public String viewShare = null;
    public String countryViewHrs = null;

    public TopNAttribute setCountry(String country) {
        this.country = country;
        return this;
    }
    public TopNAttribute setViewShare(String viewShare) {
        this.viewShare = viewShare;
        return this;
    }
    public TopNAttribute setCountryViewHrs(String countryViewHrs) {
        this.countryViewHrs = countryViewHrs;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TopNAttribute))
            return false;

        TopNAttribute o = (TopNAttribute) other;
        if(o.country == null) {
            if(country != null) return false;
        } else if(!o.country.equals(country)) return false;
        if(o.viewShare == null) {
            if(viewShare != null) return false;
        } else if(!o.viewShare.equals(viewShare)) return false;
        if(o.countryViewHrs == null) {
            if(countryViewHrs != null) return false;
        } else if(!o.countryViewHrs.equals(countryViewHrs)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (country == null ? 1237 : country.hashCode());
        hashCode = hashCode * 31 + (viewShare == null ? 1237 : viewShare.hashCode());
        hashCode = hashCode * 31 + (countryViewHrs == null ? 1237 : countryViewHrs.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TopNAttribute{");
        builder.append("country=").append(country);
        builder.append(",viewShare=").append(viewShare);
        builder.append(",countryViewHrs=").append(countryViewHrs);
        builder.append("}");
        return builder.toString();
    }

    public TopNAttribute clone() {
        try {
            TopNAttribute clone = (TopNAttribute)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}