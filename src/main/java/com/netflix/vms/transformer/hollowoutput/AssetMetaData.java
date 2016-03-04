package com.netflix.vms.transformer.hollowoutput;


public class AssetMetaData implements Cloneable {

    public Strings id = null;

    public AssetMetaData() { }

    public AssetMetaData(Strings value) {
        this.id = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AssetMetaData))
            return false;

        AssetMetaData o = (AssetMetaData) other;
        if(o.id == null) {
            if(id != null) return false;
        } else if(!o.id.equals(id)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (id == null ? 1237 : id.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AssetMetaData{");
        builder.append("id=").append(id);
        builder.append("}");
        return builder.toString();
    }

    public AssetMetaData clone() {
        try {
            AssetMetaData clone = (AssetMetaData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}