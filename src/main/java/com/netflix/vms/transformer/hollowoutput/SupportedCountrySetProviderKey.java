package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class SupportedCountrySetProviderKey implements Cloneable {

    public char[] value = null;

    public SupportedCountrySetProviderKey() { }

    public SupportedCountrySetProviderKey(char[] value) {
        this.value = value;
    }

    public SupportedCountrySetProviderKey(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof SupportedCountrySetProviderKey))
            return false;

        SupportedCountrySetProviderKey o = (SupportedCountrySetProviderKey) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public SupportedCountrySetProviderKey clone() {
        try {
            return (SupportedCountrySetProviderKey)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}