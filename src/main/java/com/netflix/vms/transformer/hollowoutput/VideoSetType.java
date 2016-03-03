package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class VideoSetType implements Cloneable {

    public char[] value = null;

    public VideoSetType() { }

    public VideoSetType(char[] value) {
        this.value = value;
    }

    public VideoSetType(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoSetType))
            return false;

        VideoSetType o = (VideoSetType) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (value == null ? 1237 : value.hashCode());
        return hashCode;
    }

    public VideoSetType clone() {
        try {
            VideoSetType clone = (VideoSetType)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}