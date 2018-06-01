package com.netflix.vms.transformer.hollowoutput;

public class CompleteVideoData implements Cloneable {

    public CompleteVideoFacetData facetData = null;
    public CompleteVideoCountrySpecificData countrySpecificData = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CompleteVideoData))
            return false;

        CompleteVideoData o = (CompleteVideoData) other;
        if(o.facetData == null) {
            if(facetData != null) return false;
        } else if(!o.facetData.equals(facetData)) return false;
        if(o.countrySpecificData == null) {
            if(countrySpecificData != null) return false;
        } else if(!o.countrySpecificData.equals(countrySpecificData)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (facetData == null ? 1237 : facetData.hashCode());
        hashCode = hashCode * 31 + (countrySpecificData == null ? 1237 : countrySpecificData.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CompleteVideoData{");
        builder.append("facetData=").append(facetData);
        builder.append(",countrySpecificData=").append(countrySpecificData);
        builder.append("}");
        return builder.toString();
    }

    public CompleteVideoData clone() {
        try {
            CompleteVideoData clone = (CompleteVideoData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;

}
