package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class DrmKeyString implements Cloneable {

    public char[] value = null;

    public DrmKeyString() { }

    public DrmKeyString(char[] value) {
        this.value = value;
    }

    public DrmKeyString(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmKeyString))
            return false;

        DrmKeyString o = (DrmKeyString) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public DrmKeyString clone() {
        try {
            return (DrmKeyString)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}