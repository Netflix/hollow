package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ArtworkRecipe")
public class ArtworkRecipe implements Cloneable {

    public String recipeName = null;
    public String cdnFolder = null;
    public String extension = null;
    public String hostName = null;

    public ArtworkRecipe setRecipeName(String recipeName) {
        this.recipeName = recipeName;
        return this;
    }
    public ArtworkRecipe setCdnFolder(String cdnFolder) {
        this.cdnFolder = cdnFolder;
        return this;
    }
    public ArtworkRecipe setExtension(String extension) {
        this.extension = extension;
        return this;
    }
    public ArtworkRecipe setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkRecipe))
            return false;

        ArtworkRecipe o = (ArtworkRecipe) other;
        if(o.recipeName == null) {
            if(recipeName != null) return false;
        } else if(!o.recipeName.equals(recipeName)) return false;
        if(o.cdnFolder == null) {
            if(cdnFolder != null) return false;
        } else if(!o.cdnFolder.equals(cdnFolder)) return false;
        if(o.extension == null) {
            if(extension != null) return false;
        } else if(!o.extension.equals(extension)) return false;
        if(o.hostName == null) {
            if(hostName != null) return false;
        } else if(!o.hostName.equals(hostName)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (recipeName == null ? 1237 : recipeName.hashCode());
        hashCode = hashCode * 31 + (cdnFolder == null ? 1237 : cdnFolder.hashCode());
        hashCode = hashCode * 31 + (extension == null ? 1237 : extension.hashCode());
        hashCode = hashCode * 31 + (hostName == null ? 1237 : hostName.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkRecipe{");
        builder.append("recipeName=").append(recipeName);
        builder.append(",cdnFolder=").append(cdnFolder);
        builder.append(",extension=").append(extension);
        builder.append(",hostName=").append(hostName);
        builder.append("}");
        return builder.toString();
    }

    public ArtworkRecipe clone() {
        try {
            ArtworkRecipe clone = (ArtworkRecipe)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}