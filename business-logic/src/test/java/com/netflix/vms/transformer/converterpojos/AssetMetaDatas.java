package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="AssetMetaDatas")
public class AssetMetaDatas implements Cloneable {

    public String assetId = null;
    public TranslatedText trackLabels = null;

    public AssetMetaDatas setAssetId(String assetId) {
        this.assetId = assetId;
        return this;
    }
    public AssetMetaDatas setTrackLabels(TranslatedText trackLabels) {
        this.trackLabels = trackLabels;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AssetMetaDatas))
            return false;

        AssetMetaDatas o = (AssetMetaDatas) other;
        if(o.assetId == null) {
            if(assetId != null) return false;
        } else if(!o.assetId.equals(assetId)) return false;
        if(o.trackLabels == null) {
            if(trackLabels != null) return false;
        } else if(!o.trackLabels.equals(trackLabels)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (assetId == null ? 1237 : assetId.hashCode());
        hashCode = hashCode * 31 + (trackLabels == null ? 1237 : trackLabels.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AssetMetaDatas{");
        builder.append("assetId=").append(assetId);
        builder.append(",trackLabels=").append(trackLabels);
        builder.append("}");
        return builder.toString();
    }

    public AssetMetaDatas clone() {
        try {
            AssetMetaDatas clone = (AssetMetaDatas)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}