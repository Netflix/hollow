package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VideoRatingRating")
public class VideoRatingRating implements Cloneable {

    public VideoRatingRatingReason reason = null;
    public long ratingId = java.lang.Long.MIN_VALUE;
    public long certificationSystemId = java.lang.Long.MIN_VALUE;

    public VideoRatingRating setReason(VideoRatingRatingReason reason) {
        this.reason = reason;
        return this;
    }
    public VideoRatingRating setRatingId(long ratingId) {
        this.ratingId = ratingId;
        return this;
    }
    public VideoRatingRating setCertificationSystemId(long certificationSystemId) {
        this.certificationSystemId = certificationSystemId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoRatingRating))
            return false;

        VideoRatingRating o = (VideoRatingRating) other;
        if(o.reason == null) {
            if(reason != null) return false;
        } else if(!o.reason.equals(reason)) return false;
        if(o.ratingId != ratingId) return false;
        if(o.certificationSystemId != certificationSystemId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (reason == null ? 1237 : reason.hashCode());
        hashCode = hashCode * 31 + (int) (ratingId ^ (ratingId >>> 32));
        hashCode = hashCode * 31 + (int) (certificationSystemId ^ (certificationSystemId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoRatingRating{");
        builder.append("reason=").append(reason);
        builder.append(",ratingId=").append(ratingId);
        builder.append(",certificationSystemId=").append(certificationSystemId);
        builder.append("}");
        return builder.toString();
    }

    public VideoRatingRating clone() {
        try {
            VideoRatingRating clone = (VideoRatingRating)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}