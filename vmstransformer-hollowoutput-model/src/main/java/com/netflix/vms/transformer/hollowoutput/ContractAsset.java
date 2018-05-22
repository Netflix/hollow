package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

public class ContractAsset implements Cloneable {

    @HollowTypeName(name="ContractAssetType")
    public String contractAssetType;
    public Strings locale;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((contractAssetType == null) ? 0 : contractAssetType
                        .hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContractAsset other = (ContractAsset) obj;
        if (contractAssetType == null) {
            if (other.contractAssetType != null)
                return false;
        } else if (!contractAssetType.equals(other.contractAssetType))
            return false;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ContractAsset{");
        builder.append("contractAssetType=").append(contractAssetType);
        builder.append(",locale=").append(locale);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public ContractAsset clone() {
        try {
            ContractAsset clone = (ContractAsset)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
    
}
