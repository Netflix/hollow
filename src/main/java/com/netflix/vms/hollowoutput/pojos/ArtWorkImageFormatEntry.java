package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class ArtWorkImageFormatEntry {

    public char[] nameStr;
    public int width;
    public int height;

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