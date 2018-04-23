package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="DownloadableId")
public class DownloadableId implements Cloneable {

    public long value = java.lang.Long.MIN_VALUE;

    public DownloadableId() { }

    public DownloadableId(long value) {
        this.value = value;
    }

    public DownloadableId setValue(long value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DownloadableId))
            return false;

        DownloadableId o = (DownloadableId) other;
        if(o.value != value) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (value ^ (value >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DownloadableId{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public DownloadableId clone() {
        try {
            DownloadableId clone = (DownloadableId)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}