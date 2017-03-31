package com.netflix.vms.transformer.hollowoutput;

public class ArtworkMerchStillPackageData {
    public int packageId = -1;
    public long offsetMillis = -1;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (offsetMillis ^ (offsetMillis >>> 32));
        result = prime * result + packageId;
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
        ArtworkMerchStillPackageData other = (ArtworkMerchStillPackageData) obj;
        if (offsetMillis != other.offsetMillis)
            return false;
        if (packageId != other.packageId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ArtworkMerchStillPackageData [packageId=" + packageId + ", offsetMillis=" + offsetMillis + "]";
    }
    
    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}
