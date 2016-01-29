package com.netflix.vms.hollowoutput.pojos;


public class AssetMetaData {

    public Strings id;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}