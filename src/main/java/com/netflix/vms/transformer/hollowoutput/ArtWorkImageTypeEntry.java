package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ArtWorkImageTypeEntry implements Cloneable {

    public char[] nameStr = null;
    public char[] unavailableFileNameStr = null;
    public boolean allowMultiples = false;
    public char[] recipeNameStr = null;

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

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (nameStr == null ? 1237 : nameStr.hashCode());
        hashCode = hashCode * 31 + (unavailableFileNameStr == null ? 1237 : unavailableFileNameStr.hashCode());
        hashCode = hashCode * 31 + (allowMultiples? 1231 : 1237);
        hashCode = hashCode * 31 + (recipeNameStr == null ? 1237 : recipeNameStr.hashCode());
        return hashCode;
    }

    public ArtWorkImageTypeEntry clone() {
        try {
            ArtWorkImageTypeEntry clone = (ArtWorkImageTypeEntry)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}