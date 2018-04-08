package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="RightsContractPackage")
public class RightsContractPackage implements Cloneable {

    public long packageId = java.lang.Long.MIN_VALUE;
    public boolean primary = false;

    public RightsContractPackage setPackageId(long packageId) {
        this.packageId = packageId;
        return this;
    }
    public RightsContractPackage setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RightsContractPackage))
            return false;

        RightsContractPackage o = (RightsContractPackage) other;
        if(o.packageId != packageId) return false;
        if(o.primary != primary) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (packageId ^ (packageId >>> 32));
        hashCode = hashCode * 31 + (primary? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RightsContractPackage{");
        builder.append("packageId=").append(packageId);
        builder.append(",primary=").append(primary);
        builder.append("}");
        return builder.toString();
    }

    public RightsContractPackage clone() {
        try {
            RightsContractPackage clone = (RightsContractPackage)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}