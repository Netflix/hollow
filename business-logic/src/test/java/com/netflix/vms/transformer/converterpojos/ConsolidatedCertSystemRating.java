package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ConsolidatedCertSystemRating")
public class ConsolidatedCertSystemRating implements Cloneable {

    public long ratingId = java.lang.Long.MIN_VALUE;
    public long maturityLevel = java.lang.Long.MIN_VALUE;
    public String ratingCode = null;
    public TranslatedText ratingCodes = null;
    public TranslatedText descriptions = null;

    public ConsolidatedCertSystemRating setRatingId(long ratingId) {
        this.ratingId = ratingId;
        return this;
    }
    public ConsolidatedCertSystemRating setMaturityLevel(long maturityLevel) {
        this.maturityLevel = maturityLevel;
        return this;
    }
    public ConsolidatedCertSystemRating setRatingCode(String ratingCode) {
        this.ratingCode = ratingCode;
        return this;
    }
    public ConsolidatedCertSystemRating setRatingCodes(TranslatedText ratingCodes) {
        this.ratingCodes = ratingCodes;
        return this;
    }
    public ConsolidatedCertSystemRating setDescriptions(TranslatedText descriptions) {
        this.descriptions = descriptions;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ConsolidatedCertSystemRating))
            return false;

        ConsolidatedCertSystemRating o = (ConsolidatedCertSystemRating) other;
        if(o.ratingId != ratingId) return false;
        if(o.maturityLevel != maturityLevel) return false;
        if(o.ratingCode == null) {
            if(ratingCode != null) return false;
        } else if(!o.ratingCode.equals(ratingCode)) return false;
        if(o.ratingCodes == null) {
            if(ratingCodes != null) return false;
        } else if(!o.ratingCodes.equals(ratingCodes)) return false;
        if(o.descriptions == null) {
            if(descriptions != null) return false;
        } else if(!o.descriptions.equals(descriptions)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (ratingId ^ (ratingId >>> 32));
        hashCode = hashCode * 31 + (int) (maturityLevel ^ (maturityLevel >>> 32));
        hashCode = hashCode * 31 + (ratingCode == null ? 1237 : ratingCode.hashCode());
        hashCode = hashCode * 31 + (ratingCodes == null ? 1237 : ratingCodes.hashCode());
        hashCode = hashCode * 31 + (descriptions == null ? 1237 : descriptions.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ConsolidatedCertSystemRating{");
        builder.append("ratingId=").append(ratingId);
        builder.append(",maturityLevel=").append(maturityLevel);
        builder.append(",ratingCode=").append(ratingCode);
        builder.append(",ratingCodes=").append(ratingCodes);
        builder.append(",descriptions=").append(descriptions);
        builder.append("}");
        return builder.toString();
    }

    public ConsolidatedCertSystemRating clone() {
        try {
            ConsolidatedCertSystemRating clone = (ConsolidatedCertSystemRating)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}