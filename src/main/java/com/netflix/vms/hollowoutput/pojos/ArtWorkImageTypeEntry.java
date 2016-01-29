package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class ArtWorkImageTypeEntry {

    public char[] nameStr;
    public char[] unavailableFileNameStr;
    public boolean allowMultiples;
    public char[] recipeNameStr;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtWorkImageTypeEntry))
            return false;

        ArtWorkImageTypeEntry o = (ArtWorkImageTypeEntry) other;
        if(!Arrays.equals(o.nameStr, nameStr)) return false;
        if(!Arrays.equals(o.unavailableFileNameStr, unavailableFileNameStr)) return false;
        if(o.allowMultiples != allowMultiples) return false;
        if(!Arrays.equals(o.recipeNameStr, recipeNameStr)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}