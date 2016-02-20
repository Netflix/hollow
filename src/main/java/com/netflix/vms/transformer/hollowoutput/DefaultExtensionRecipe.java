package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class DefaultExtensionRecipe {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}