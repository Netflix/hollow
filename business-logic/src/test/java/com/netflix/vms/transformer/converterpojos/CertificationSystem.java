package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="CertificationSystem")
public class CertificationSystem implements Cloneable {

    public long certificationSystemId = java.lang.Long.MIN_VALUE;
    public String countryCode = null;
    @HollowTypeName(name="CertificationSystemRatingList")
    public List<CertificationSystemRating> rating = null;
    public String officialURL = null;

    public CertificationSystem setCertificationSystemId(long certificationSystemId) {
        this.certificationSystemId = certificationSystemId;
        return this;
    }
    public CertificationSystem setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public CertificationSystem setRating(List<CertificationSystemRating> rating) {
        this.rating = rating;
        return this;
    }
    public CertificationSystem setOfficialURL(String officialURL) {
        this.officialURL = officialURL;
        return this;
    }
    public CertificationSystem addToRating(CertificationSystemRating certificationSystemRating) {
        if (this.rating == null) {
            this.rating = new ArrayList<CertificationSystemRating>();
        }
        this.rating.add(certificationSystemRating);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CertificationSystem))
            return false;

        CertificationSystem o = (CertificationSystem) other;
        if(o.certificationSystemId != certificationSystemId) return false;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.rating == null) {
            if(rating != null) return false;
        } else if(!o.rating.equals(rating)) return false;
        if(o.officialURL == null) {
            if(officialURL != null) return false;
        } else if(!o.officialURL.equals(officialURL)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (certificationSystemId ^ (certificationSystemId >>> 32));
        hashCode = hashCode * 31 + (countryCode == null ? 1237 : countryCode.hashCode());
        hashCode = hashCode * 31 + (rating == null ? 1237 : rating.hashCode());
        hashCode = hashCode * 31 + (officialURL == null ? 1237 : officialURL.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CertificationSystem{");
        builder.append("certificationSystemId=").append(certificationSystemId);
        builder.append(",countryCode=").append(countryCode);
        builder.append(",rating=").append(rating);
        builder.append(",officialURL=").append(officialURL);
        builder.append("}");
        return builder.toString();
    }

    public CertificationSystem clone() {
        try {
            CertificationSystem clone = (CertificationSystem)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}