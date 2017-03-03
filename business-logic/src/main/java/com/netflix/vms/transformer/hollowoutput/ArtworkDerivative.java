package com.netflix.vms.transformer.hollowoutput;


public class ArtworkDerivative implements Cloneable {

    public ArtWorkImageFormatEntry format = null;
    public ArtWorkImageTypeEntry type = null;
    public ArtWorkImageRecipe recipe = null;
    public Strings recipeDesc = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkDerivative))
            return false;

        ArtworkDerivative o = (ArtworkDerivative) other;
        if(o.format == null) {
            if(format != null) return false;
        } else if(!o.format.equals(format)) return false;
        if(o.type == null) {
            if(type != null) return false;
        } else if(!o.type.equals(type)) return false;
        if(o.recipe == null) {
            if(recipe != null) return false;
        } else if(!o.recipe.equals(recipe)) return false;
        if(o.recipeDesc == null) {
            if(recipeDesc != null) return false;
        } else if(!o.recipeDesc.equals(recipeDesc)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (format == null ? 1237 : format.hashCode());
        hashCode = hashCode * 31 + (type == null ? 1237 : type.hashCode());
        hashCode = hashCode * 31 + (recipe == null ? 1237 : recipe.hashCode());
        hashCode = hashCode * 31 + (recipeDesc == null ? 1237 : recipeDesc.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkDerivative{");
        builder.append("format=").append(format);
        builder.append(",type=").append(type);
        builder.append(",recipe=").append(recipe);
        builder.append(",recipeDesc=").append(recipeDesc);
        builder.append("}");
        return builder.toString();
    }

    public ArtworkDerivative clone() {
        try {
            ArtworkDerivative clone = (ArtworkDerivative)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}