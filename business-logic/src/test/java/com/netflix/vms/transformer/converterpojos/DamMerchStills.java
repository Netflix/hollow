package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="DamMerchStills")
public class DamMerchStills implements Cloneable {

    public String assetId = null;
    public DamMerchStillsMoment moment = null;

    public DamMerchStills setAssetId(String assetId) {
        this.assetId = assetId;
        return this;
    }
    public DamMerchStills setMoment(DamMerchStillsMoment moment) {
        this.moment = moment;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DamMerchStills))
            return false;

        DamMerchStills o = (DamMerchStills) other;
        if(o.assetId == null) {
            if(assetId != null) return false;
        } else if(!o.assetId.equals(assetId)) return false;
        if(o.moment == null) {
            if(moment != null) return false;
        } else if(!o.moment.equals(moment)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (assetId == null ? 1237 : assetId.hashCode());
        hashCode = hashCode * 31 + (moment == null ? 1237 : moment.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DamMerchStills{");
        builder.append("assetId=").append(assetId);
        builder.append(",moment=").append(moment);
        builder.append("}");
        return builder.toString();
    }

    public DamMerchStills clone() {
        try {
            DamMerchStills clone = (DamMerchStills)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}