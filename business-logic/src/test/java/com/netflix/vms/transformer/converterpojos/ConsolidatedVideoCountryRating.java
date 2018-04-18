package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ConsolidatedVideoCountryRating")
public class ConsolidatedVideoCountryRating implements Cloneable {

    public VideoRatingAdvisories advisories = null;
    public TranslatedText reasons = null;
    public long ratingId = java.lang.Long.MIN_VALUE;
    public long certificationSystemId = java.lang.Long.MIN_VALUE;

    public ConsolidatedVideoCountryRating setAdvisories(VideoRatingAdvisories advisories) {
        this.advisories = advisories;
        return this;
    }
    public ConsolidatedVideoCountryRating setReasons(TranslatedText reasons) {
        this.reasons = reasons;
        return this;
    }
    public ConsolidatedVideoCountryRating setRatingId(long ratingId) {
        this.ratingId = ratingId;
        return this;
    }
    public ConsolidatedVideoCountryRating setCertificationSystemId(long certificationSystemId) {
        this.certificationSystemId = certificationSystemId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ConsolidatedVideoCountryRating))
            return false;

        ConsolidatedVideoCountryRating o = (ConsolidatedVideoCountryRating) other;
        if(o.advisories == null) {
            if(advisories != null) return false;
        } else if(!o.advisories.equals(advisories)) return false;
        if(o.reasons == null) {
            if(reasons != null) return false;
        } else if(!o.reasons.equals(reasons)) return false;
        if(o.ratingId != ratingId) return false;
        if(o.certificationSystemId != certificationSystemId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (advisories == null ? 1237 : advisories.hashCode());
        hashCode = hashCode * 31 + (reasons == null ? 1237 : reasons.hashCode());
        hashCode = hashCode * 31 + (int) (ratingId ^ (ratingId >>> 32));
        hashCode = hashCode * 31 + (int) (certificationSystemId ^ (certificationSystemId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ConsolidatedVideoCountryRating{");
        builder.append("advisories=").append(advisories);
        builder.append(",reasons=").append(reasons);
        builder.append(",ratingId=").append(ratingId);
        builder.append(",certificationSystemId=").append(certificationSystemId);
        builder.append("}");
        return builder.toString();
    }

    public ConsolidatedVideoCountryRating clone() {
        try {
            ConsolidatedVideoCountryRating clone = (ConsolidatedVideoCountryRating)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}