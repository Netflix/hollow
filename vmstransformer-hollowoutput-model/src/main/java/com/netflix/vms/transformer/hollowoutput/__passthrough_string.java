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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("__passthrough_string{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public __passthrough_string clone() {
        try {
            __passthrough_string clone = (__passthrough_string)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
