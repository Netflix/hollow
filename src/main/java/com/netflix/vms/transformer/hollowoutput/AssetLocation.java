package com.netflix.vms.transformer.hollowoutput;


public class AssetLocation implements Cloneable {

    public Strings recipeDescriptor = null;
    public int cdnId = java.lang.Integer.MIN_VALUE;
    public Strings cdnDirectory = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AssetLocation))
            return false;

        AssetLocation o = (AssetLocation) other;
        if(o.recipeDescriptor == null) {
            if(recipeDescriptor != null) return false;
        } else if(!o.recipeDescriptor.equals(recipeDescriptor)) return false;
        if(o.cdnId != cdnId) return false;
        if(o.cdnDirectory == null) {
            if(cdnDirectory != null) return false;
        } else if(!o.cdnDirectory.equals(cdnDirectory)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (recipeDescriptor == null ? 1237 : recipeDescriptor.hashCode());
        hashCode = hashCode * 31 + cdnId;
        hashCode = hashCode * 31 + (cdnDirectory == null ? 1237 : cdnDirectory.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AssetLocation{");
        builder.append("recipeDescriptor=").append(recipeDescriptor);
        builder.append(",cdnId=").append(cdnId);
        builder.append(",cdnDirectory=").append(cdnDirectory);
        builder.append("}");
        return builder.toString();
    }

    public AssetLocation clone() {
        try {
            AssetLocation clone = (AssetLocation)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}