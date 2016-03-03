package com.netflix.vms.transformer.hollowoutput;


public class AssetTypeDescriptor implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public Strings name = null;
    public Strings description = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AssetTypeDescriptor))
            return false;

        AssetTypeDescriptor o = (AssetTypeDescriptor) other;
        if(o.id != id) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        return hashCode;
    }

    public AssetTypeDescriptor clone() {
        try {
            AssetTypeDescriptor clone = (AssetTypeDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}