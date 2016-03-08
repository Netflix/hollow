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
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(nameStr);
        hashCode = hashCode * 31 + Arrays.hashCode(unavailableFileNameStr);
        hashCode = hashCode * 31 + (allowMultiples? 1231 : 1237);
        hashCode = hashCode * 31 + Arrays.hashCode(recipeNameStr);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtWorkImageTypeEntry{");
        builder.append("nameStr=").append(nameStr);
        builder.append(",unavailableFileNameStr=").append(unavailableFileNameStr);
        builder.append(",allowMultiples=").append(allowMultiples);
        builder.append(",recipeNameStr=").append(recipeNameStr);
        builder.append("}");
        return builder.toString();
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