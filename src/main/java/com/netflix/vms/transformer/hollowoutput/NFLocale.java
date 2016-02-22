package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class NFLocale implements Cloneable {

    public char[] value = null;

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

    public NFLocale clone() {
        try {
            return (NFLocale)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}