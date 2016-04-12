package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class DefaultExtensionRecipe implements Cloneable {

    public char[] extensionStr = null;
    public char[] recipeNameStr = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DefaultExtensionRecipe))
            return false;

        DefaultExtensionRecipe o = (DefaultExtensionRecipe) other;
        if(!Arrays.equals(o.extensionStr, extensionStr)) return false;
        if(!Arrays.equals(o.recipeNameStr, recipeNameStr)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(extensionStr);
        hashCode = hashCode * 31 + Arrays.hashCode(recipeNameStr);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DefaultExtensionRecipe{");
        builder.append("extensionStr=").append(extensionStr);
        builder.append(",recipeNameStr=").append(recipeNameStr);
        builder.append("}");
        return builder.toString();
    }

    public DefaultExtensionRecipe clone() {
        try {
            DefaultExtensionRecipe clone = (DefaultExtensionRecipe)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}