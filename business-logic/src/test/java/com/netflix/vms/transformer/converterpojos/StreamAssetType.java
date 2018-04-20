package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamAssetType")
public class StreamAssetType implements Cloneable {

    public long assetTypeId = java.lang.Long.MIN_VALUE;
    public String assetType = null;

    public StreamAssetType setAssetTypeId(long assetTypeId) {
        this.assetTypeId = assetTypeId;
        return this;
    }
    public StreamAssetType setAssetType(String assetType) {
        this.assetType = assetType;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamAssetType))
            return false;

        StreamAssetType o = (StreamAssetType) other;
        if(o.assetTypeId != assetTypeId) return false;
        if(o.assetType == null) {
            if(assetType != null) return false;
        } else if(!o.assetType.equals(assetType)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (assetTypeId ^ (assetTypeId >>> 32));
        hashCode = hashCode * 31 + (assetType == null ? 1237 : assetType.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamAssetType{");
        builder.append("assetTypeId=").append(assetTypeId);
        builder.append(",assetType=").append(assetType);
        builder.append("}");
        return builder.toString();
    }

    public StreamAssetType clone() {
        try {
            StreamAssetType clone = (StreamAssetType)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}