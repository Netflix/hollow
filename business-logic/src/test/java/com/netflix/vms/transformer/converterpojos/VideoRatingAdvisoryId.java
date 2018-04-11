package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VideoRatingAdvisoryId")
public class VideoRatingAdvisoryId implements Cloneable {

    public long value = java.lang.Long.MIN_VALUE;

    public VideoRatingAdvisoryId() { }

    public VideoRatingAdvisoryId(long value) {
        this.value = value;
    }

    public VideoRatingAdvisoryId setValue(long value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoRatingAdvisoryId))
            return false;

        VideoRatingAdvisoryId o = (VideoRatingAdvisoryId) other;
        if(o.value != value) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (value ^ (value >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoRatingAdvisoryId{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public VideoRatingAdvisoryId clone() {
        try {
            VideoRatingAdvisoryId clone = (VideoRatingAdvisoryId)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}