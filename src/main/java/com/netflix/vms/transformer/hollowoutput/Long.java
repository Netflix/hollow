package com.netflix.vms.transformer.hollowoutput;


public class Long {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}