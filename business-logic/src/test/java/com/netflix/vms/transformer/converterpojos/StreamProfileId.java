package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamProfileId")
public class StreamProfileId implements Cloneable {

    public long value = java.lang.Long.MIN_VALUE;

    public StreamProfileId() { }

    public StreamProfileId(long value) {
        this.value = value;
    }

    public StreamProfileId setValue(long value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamProfileId))
            return false;

        StreamProfileId o = (StreamProfileId) other;
        if(o.value != value) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (value ^ (value >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamProfileId{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public StreamProfileId clone() {
        try {
            StreamProfileId clone = (StreamProfileId)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}