package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Arrays;

@HollowPrimaryKey(fields="recipeNameStr")
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
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(recipeNameStr);
        hashCode = hashCode * 31 + Arrays.hashCode(extensionStr);
        hashCode = hashCode * 31 + Arrays.hashCode(cdnFolderStr);
        hashCode = hashCode * 31 + Arrays.hashCode(hostNameStr);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtWorkImageRecipe{");
        builder.append("recipeNameStr=").append(recipeNameStr);
        builder.append(",extensionStr=").append(extensionStr);
        builder.append(",cdnFolderStr=").append(cdnFolderStr);
        builder.append(",hostNameStr=").append(hostNameStr);
        builder.append("}");
        return builder.toString();
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