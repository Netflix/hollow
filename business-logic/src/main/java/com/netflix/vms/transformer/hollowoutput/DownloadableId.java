package com.netflix.vms.transformer.hollowoutput;


public class DownloadableId implements Cloneable {

    public long val = java.lang.Long.MIN_VALUE;

    public DownloadableId() { }

    public DownloadableId(long value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DownloadableId))
            return false;

        DownloadableId o = (DownloadableId) other;
        if(o.val != val) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (val ^ (val >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Long{");
        builder.append("val=").append(val);
        builder.append("}");
        return builder.toString();
    }

    public DownloadableId clone() {
        try {
            DownloadableId clone = (DownloadableId)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}