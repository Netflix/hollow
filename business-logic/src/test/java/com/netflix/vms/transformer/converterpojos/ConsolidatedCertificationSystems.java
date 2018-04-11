package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="ConsolidatedCertificationSystems")
public class ConsolidatedCertificationSystems implements Cloneable {

    public long certificationSystemId = java.lang.Long.MIN_VALUE;
    public String countryCode = null;
    @HollowTypeName(name="ConsolidatedCertSystemRatingList")
    public List<ConsolidatedCertSystemRating> rating = null;
    public TranslatedText name = null;
    public TranslatedText description = null;
    public String officialURL = null;

    public ConsolidatedCertificationSystems setCertificationSystemId(long certificationSystemId) {
        this.certificationSystemId = certificationSystemId;
        return this;
    }
    public ConsolidatedCertificationSystems setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public ConsolidatedCertificationSystems setRating(List<ConsolidatedCertSystemRating> rating) {
        this.rating = rating;
        return this;
    }
    public ConsolidatedCertificationSystems setName(TranslatedText name) {
        this.name = name;
        return this;
    }
    public ConsolidatedCertificationSystems setDescription(TranslatedText description) {
        this.description = description;
        return this;
    }
    public ConsolidatedCertificationSystems setOfficialURL(String officialURL) {
        this.officialURL = officialURL;
        return this;
    }
    public ConsolidatedCertificationSystems addToRating(ConsolidatedCertSystemRating consolidatedCertSystemRating) {
        if (this.rating == null) {
            this.rating = new ArrayList<ConsolidatedCertSystemRating>();
        }
        this.rating.add(consolidatedCertSystemRating);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ConsolidatedCertificationSystems))
            return false;

        ConsolidatedCertificationSystems o = (ConsolidatedCertificationSystems) other;
        if(o.certificationSystemId != certificationSystemId) return false;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.rating == null) {
            if(rating != null) return false;
        } else if(!o.rating.equals(rating)) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
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
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        hashCode = hashCode * 31 + (officialURL == null ? 1237 : officialURL.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ConsolidatedCertificationSystems{");
        builder.append("certificationSystemId=").append(certificationSystemId);
        builder.append(",countryCode=").append(countryCode);
        builder.append(",rating=").append(rating);
        builder.append(",name=").append(name);
        builder.append(",description=").append(description);
        builder.append(",officialURL=").append(officialURL);
        builder.append("}");
        return builder.toString();
    }

    public ConsolidatedCertificationSystems clone() {
        try {
            ConsolidatedCertificationSystems clone = (ConsolidatedCertificationSystems)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}