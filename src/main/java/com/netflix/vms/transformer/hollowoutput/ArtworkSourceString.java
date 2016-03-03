package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ArtworkSourceString implements Cloneable {

    public char[] value = null;

    public ArtworkSourceString() { }

    public ArtworkSourceString(char[] value) {
        this.value = value;
    }

    public ArtworkSourceString(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkSourceString))
            return false;

        ArtworkSourceString o = (ArtworkSourceString) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public ArtworkSourceString clone() {
        try {
            ArtworkSourceString clone = (ArtworkSourceString)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}