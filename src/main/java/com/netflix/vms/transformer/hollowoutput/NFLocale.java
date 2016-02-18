package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class NFLocale {

    public char[] value;

    public NFLocale() { }

    public NFLocale(char[] value) {
        this.value = value;
    }

    public NFLocale(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof NFLocale))
            return false;

        NFLocale o = (NFLocale) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}