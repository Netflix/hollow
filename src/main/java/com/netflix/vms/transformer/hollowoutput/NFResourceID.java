package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class NFResourceID {

    public char[] value;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}