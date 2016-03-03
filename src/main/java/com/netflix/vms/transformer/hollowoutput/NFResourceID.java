package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class NFResourceID implements Cloneable {

    public char[] value = null;

    public NFResourceID() { }

    public NFResourceID(char[] value) {
        this.value = value;
    }

    public NFResourceID(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof NFResourceID))
            return false;

        NFResourceID o = (NFResourceID) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (value == null ? 1237 : value.hashCode());
        return hashCode;
    }

    public NFResourceID clone() {
        try {
            NFResourceID clone = (NFResourceID)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}