package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CertificationSystemRating")
public class CertificationSystemRating implements Cloneable {

    public String ratingCode = null;
    public long ratingId = java.lang.Long.MIN_VALUE;
    public long maturityLevel = java.lang.Long.MIN_VALUE;

    public CertificationSystemRating setRatingCode(String ratingCode) {
        this.ratingCode = ratingCode;
        return this;
    }
    public CertificationSystemRating setRatingId(long ratingId) {
        this.ratingId = ratingId;
        return this;
    }
    public CertificationSystemRating setMaturityLevel(long maturityLevel) {
        this.maturityLevel = maturityLevel;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CertificationSystemRating))
            return false;

        CertificationSystemRating o = (CertificationSystemRating) other;
        if(o.ratingCode == null) {
            if(ratingCode != null) return false;
        } else if(!o.ratingCode.equals(ratingCode)) return false;
        if(o.ratingId != ratingId) return false;
        if(o.maturityLevel != maturityLevel) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (ratingCode == null ? 1237 : ratingCode.hashCode());
        hashCode = hashCode * 31 + (int) (ratingId ^ (ratingId >>> 32));
        hashCode = hashCode * 31 + (int) (maturityLevel ^ (maturityLevel >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CertificationSystemRating{");
        builder.append("ratingCode=").append(ratingCode);
        builder.append(",ratingId=").append(ratingId);
        builder.append(",maturityLevel=").append(maturityLevel);
        builder.append("}");
        return builder.toString();
    }

    public CertificationSystemRating clone() {
        try {
            CertificationSystemRating clone = (CertificationSystemRating)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}