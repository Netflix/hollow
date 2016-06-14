package com.netflix.vms.transformer.hollowoutput;


public class DeprecatedImageId implements Cloneable {

    public long val = java.lang.Long.MIN_VALUE;

    public DeprecatedImageId() { }

    public DeprecatedImageId(long value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DeprecatedImageId))
            return false;

        DeprecatedImageId o = (DeprecatedImageId) other;
        if(o.val != val) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (val ^ (val >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DeprecatedImageId{");
        builder.append("val=").append(val);
        builder.append("}");
        return builder.toString();
    }

    public DeprecatedImageId clone() {
        try {
            DeprecatedImageId clone = (DeprecatedImageId)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}