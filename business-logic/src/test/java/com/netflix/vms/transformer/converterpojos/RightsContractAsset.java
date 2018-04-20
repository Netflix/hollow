package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="RightsContractAsset")
public class RightsContractAsset implements Cloneable {

    public String bcp47Code = null;
    public String assetType = null;

    public RightsContractAsset setBcp47Code(String bcp47Code) {
        this.bcp47Code = bcp47Code;
        return this;
    }
    public RightsContractAsset setAssetType(String assetType) {
        this.assetType = assetType;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RightsContractAsset))
            return false;

        RightsContractAsset o = (RightsContractAsset) other;
        if(o.bcp47Code == null) {
            if(bcp47Code != null) return false;
        } else if(!o.bcp47Code.equals(bcp47Code)) return false;
        if(o.assetType == null) {
            if(assetType != null) return false;
        } else if(!o.assetType.equals(assetType)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (bcp47Code == null ? 1237 : bcp47Code.hashCode());
        hashCode = hashCode * 31 + (assetType == null ? 1237 : assetType.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RightsContractAsset{");
        builder.append("bcp47Code=").append(bcp47Code);
        builder.append(",assetType=").append(assetType);
        builder.append("}");
        return builder.toString();
    }

    public RightsContractAsset clone() {
        try {
            RightsContractAsset clone = (RightsContractAsset)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}