package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ArtWorkImageType")
public class ArtWorkImageType implements Cloneable {

    public String imageType = null;
    public String extension = null;
    public String recipe = null;

    public ArtWorkImageType setImageType(String imageType) {
        this.imageType = imageType;
        return this;
    }
    public ArtWorkImageType setExtension(String extension) {
        this.extension = extension;
        return this;
    }
    public ArtWorkImageType setRecipe(String recipe) {
        this.recipe = recipe;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtWorkImageType))
            return false;

        ArtWorkImageType o = (ArtWorkImageType) other;
        if(o.imageType == null) {
            if(imageType != null) return false;
        } else if(!o.imageType.equals(imageType)) return false;
        if(o.extension == null) {
            if(extension != null) return false;
        } else if(!o.extension.equals(extension)) return false;
        if(o.recipe == null) {
            if(recipe != null) return false;
        } else if(!o.recipe.equals(recipe)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (imageType == null ? 1237 : imageType.hashCode());
        hashCode = hashCode * 31 + (extension == null ? 1237 : extension.hashCode());
        hashCode = hashCode * 31 + (recipe == null ? 1237 : recipe.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtWorkImageType{");
        builder.append("imageType=").append(imageType);
        builder.append(",extension=").append(extension);
        builder.append(",recipe=").append(recipe);
        builder.append("}");
        return builder.toString();
    }

    public ArtWorkImageType clone() {
        try {
            ArtWorkImageType clone = (ArtWorkImageType)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}