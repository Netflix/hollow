package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ArtWorkImageFormatEntry {

    public char[] nameStr = null;
    public int width = java.lang.Integer.MIN_VALUE;
    public int height = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtWorkImageFormatEntry))
            return false;

        ArtWorkImageFormatEntry o = (ArtWorkImageFormatEntry) other;
        if(!Arrays.equals(o.nameStr, nameStr)) return false;
        if(o.width != width) return false;
        if(o.height != height) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}