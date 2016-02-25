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