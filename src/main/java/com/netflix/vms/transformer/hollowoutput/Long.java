package com.netflix.vms.transformer.hollowoutput;


public class Long implements Cloneable {

    public long val = java.lang.Long.MIN_VALUE;

    public Long() { }

    public Long(long value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Long))
            return false;

        Long o = (Long) other;
        if(o.val != val) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (int) (val ^ (val >>> 32));
        return hashCode;
    }

    public Long clone() {
        try {
            Long clone = (Long)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}