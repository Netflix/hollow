package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ArtworkSourceString {

    public char[] value;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}