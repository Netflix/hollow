package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VideoId")
public class VideoId implements Cloneable {

    public long value = java.lang.Long.MIN_VALUE;

    public VideoId() { }

    public VideoId(long value) {
        this.value = value;
    }

    public VideoId setValue(long value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoId))
            return false;

        VideoId o = (VideoId) other;
        if(o.value != value) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (value ^ (value >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoId{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public VideoId clone() {
        try {
            VideoId clone = (VideoId)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}