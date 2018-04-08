package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="DamMerchStillsMoment")
public class DamMerchStillsMoment implements Cloneable {

    public String packageId = null;
    public String stillTS = null;

    public DamMerchStillsMoment setPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }
    public DamMerchStillsMoment setStillTS(String stillTS) {
        this.stillTS = stillTS;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DamMerchStillsMoment))
            return false;

        DamMerchStillsMoment o = (DamMerchStillsMoment) other;
        if(o.packageId == null) {
            if(packageId != null) return false;
        } else if(!o.packageId.equals(packageId)) return false;
        if(o.stillTS == null) {
            if(stillTS != null) return false;
        } else if(!o.stillTS.equals(stillTS)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (packageId == null ? 1237 : packageId.hashCode());
        hashCode = hashCode * 31 + (stillTS == null ? 1237 : stillTS.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DamMerchStillsMoment{");
        builder.append("packageId=").append(packageId);
        builder.append(",stillTS=").append(stillTS);
        builder.append("}");
        return builder.toString();
    }

    public DamMerchStillsMoment clone() {
        try {
            DamMerchStillsMoment clone = (DamMerchStillsMoment)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}