package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class L10NStrings {

    public char[] value = null;

    public L10NStrings() { }

    public L10NStrings(char[] value) {
        this.value = value;
    }

    public L10NStrings(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof L10NStrings))
            return false;

        L10NStrings o = (L10NStrings) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}