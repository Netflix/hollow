package com.netflix.vms.transformer.hollowoutput;


public class Date {

    public long val;

    public Date() { }

    public Date(long value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Date))
            return false;

        Date o = (Date) other;
        if(o.val != val) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}