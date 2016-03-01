package com.netflix.vms.transformer.modules.packages;


public class ContractAssets {

    private final String assetType;
    private final String lang;
    public ContractAssets(String assetType, String lang) {
        this.assetType = assetType;
        this.lang = lang;
    }
    public String getAssetType() {
        return assetType;
    }
    public String getLang() {
        return lang;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assetType == null) ? 0 : assetType.hashCode());
        result = prime * result + ((lang == null) ? 0 : lang.hashCode());
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
        ContractAssets other = (ContractAssets) obj;
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
