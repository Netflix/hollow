package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VideoRatingRatingReasonIds")
public class VideoRatingRatingReasonIds implements Cloneable {

    public long value = java.lang.Long.MIN_VALUE;

    public VideoRatingRatingReasonIds() { }

    public VideoRatingRatingReasonIds(long value) {
        this.value = value;
    }

    public VideoRatingRatingReasonIds setValue(long value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoRatingRatingReasonIds))
            return false;

        VideoRatingRatingReasonIds o = (VideoRatingRatingReasonIds) other;
        if(o.value != value) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (value ^ (value >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoRatingRatingReasonIds{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public VideoRatingRatingReasonIds clone() {
        try {
            VideoRatingRatingReasonIds clone = (VideoRatingRatingReasonIds)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}