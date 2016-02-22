package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class __passthrough_string implements Cloneable {

    public char[] value = null;

    public __passthrough_string() { }

    public __passthrough_string(char[] value) {
        this.value = value;
    }

    public __passthrough_string(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof __passthrough_string))
            return false;

        __passthrough_string o = (__passthrough_string) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public __passthrough_string clone() {
        try {
            return (__passthrough_string)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}