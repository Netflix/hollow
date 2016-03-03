package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ArtWorkImageRecipe implements Cloneable {

    public char[] recipeNameStr = null;
    public char[] extensionStr = null;
    public char[] cdnFolderStr = null;
    public char[] hostNameStr = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtWorkImageRecipe))
            return false;

        ArtWorkImageRecipe o = (ArtWorkImageRecipe) other;
        if(!Arrays.equals(o.recipeNameStr, recipeNameStr)) return false;
        if(!Arrays.equals(o.extensionStr, extensionStr)) return false;
        if(!Arrays.equals(o.cdnFolderStr, cdnFolderStr)) return false;
        if(!Arrays.equals(o.hostNameStr, hostNameStr)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (recipeNameStr == null ? 1237 : recipeNameStr.hashCode());
        hashCode = hashCode * 31 + (extensionStr == null ? 1237 : extensionStr.hashCode());
        hashCode = hashCode * 31 + (cdnFolderStr == null ? 1237 : cdnFolderStr.hashCode());
        hashCode = hashCode * 31 + (hostNameStr == null ? 1237 : hostNameStr.hashCode());
        return hashCode;
    }

    public ArtWorkImageRecipe clone() {
        try {
            ArtWorkImageRecipe clone = (ArtWorkImageRecipe)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}