package com.netflix.vms.transformer.hollowoutput;


public class Date implements Cloneable {

    public long val = java.lang.Long.MIN_VALUE;

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

    public Date clone() {
        try {
            return (Date)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}