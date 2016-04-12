package com.netflix.vms.transformer.modules.packages.contracts;



public class ContractAssetType {

    private final String assetType;
    private final String lang;
    private final int hashCode;

    public ContractAssetType(String assetType, String lang) {
        this.assetType = assetType;
        this.lang = lang;
        this.hashCode = assetType.hashCode() + 997 * lang.hashCode();
    }
    public String getAssetType() {
        return assetType;
    }
    public String getLang() {
        return lang;
    }
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ContractAssetType) {
            return ((ContractAssetType)obj).lang.equals(lang)
                    && ((ContractAssetType)obj).assetType.equals(assetType);
        }
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContractAssetType other = (ContractAssetType) obj;
        if (assetType == null) {
            if (other.assetType != null)
                return false;
        } else if (!assetType.equals(other.assetType))
            return false;
        if (lang == null) {
            if (other.lang != null)
                return false;
        } else if (!lang.equals(other.lang))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + assetType + ": " + lang + "]";
    }


}
