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

    public ArtworkSourceString clone() {
        try {
            return (ArtworkSourceString)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}