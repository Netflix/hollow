package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class DrmKeyString {

    public char[] value;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}