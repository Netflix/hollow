package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Ratings")
public class Ratings implements Cloneable {

    public long ratingId = java.lang.Long.MIN_VALUE;
    public TranslatedText ratingCode = null;
    public TranslatedText description = null;

    public Ratings setRatingId(long ratingId) {
        this.ratingId = ratingId;
        return this;
    }
    public Ratings setRatingCode(TranslatedText ratingCode) {
        this.ratingCode = ratingCode;
        return this;
    }
    public Ratings setDescription(TranslatedText description) {
        this.description = description;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Ratings))
            return false;

        Ratings o = (Ratings) other;
        if(o.ratingId != ratingId) return false;
        if(o.ratingCode == null) {
            if(ratingCode != null) return false;
        } else if(!o.ratingCode.equals(ratingCode)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (ratingId ^ (ratingId >>> 32));
        hashCode = hashCode * 31 + (ratingCode == null ? 1237 : ratingCode.hashCode());
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Ratings{");
        builder.append("ratingId=").append(ratingId);
        builder.append(",ratingCode=").append(ratingCode);
        builder.append(",description=").append(description);
        builder.append("}");
        return builder.toString();
    }

    public Ratings clone() {
        try {
            Ratings clone = (Ratings)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}