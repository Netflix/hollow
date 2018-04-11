package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="RightsWindowContract")
public class RightsWindowContract implements Cloneable {

    public long contractId = java.lang.Long.MIN_VALUE;
    public boolean download = false;
    public long packageId = java.lang.Long.MIN_VALUE;
    public List<RightsContractAsset> assets = null;
    public List<RightsContractPackage> packages = null;

    public RightsWindowContract setContractId(long contractId) {
        this.contractId = contractId;
        return this;
    }
    public RightsWindowContract setDownload(boolean download) {
        this.download = download;
        return this;
    }
    public RightsWindowContract setPackageId(long packageId) {
        this.packageId = packageId;
        return this;
    }
    public RightsWindowContract setAssets(List<RightsContractAsset> assets) {
        this.assets = assets;
        return this;
    }
    public RightsWindowContract setPackages(List<RightsContractPackage> packages) {
        this.packages = packages;
        return this;
    }
    public RightsWindowContract addToAssets(RightsContractAsset rightsContractAsset) {
        if (this.assets == null) {
            this.assets = new ArrayList<RightsContractAsset>();
        }
        this.assets.add(rightsContractAsset);
        return this;
    }
    public RightsWindowContract addToPackages(RightsContractPackage rightsContractPackage) {
        if (this.packages == null) {
            this.packages = new ArrayList<RightsContractPackage>();
        }
        this.packages.add(rightsContractPackage);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RightsWindowContract))
            return false;

        RightsWindowContract o = (RightsWindowContract) other;
        if(o.contractId != contractId) return false;
        if(o.download != download) return false;
        if(o.packageId != packageId) return false;
        if(o.assets == null) {
            if(assets != null) return false;
        } else if(!o.assets.equals(assets)) return false;
        if(o.packages == null) {
            if(packages != null) return false;
        } else if(!o.packages.equals(packages)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (contractId ^ (contractId >>> 32));
        hashCode = hashCode * 31 + (download? 1231 : 1237);
        hashCode = hashCode * 31 + (int) (packageId ^ (packageId >>> 32));
        hashCode = hashCode * 31 + (assets == null ? 1237 : assets.hashCode());
        hashCode = hashCode * 31 + (packages == null ? 1237 : packages.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RightsWindowContract{");
        builder.append("contractId=").append(contractId);
        builder.append(",download=").append(download);
        builder.append(",packageId=").append(packageId);
        builder.append(",assets=").append(assets);
        builder.append(",packages=").append(packages);
        builder.append("}");
        return builder.toString();
    }

    public RightsWindowContract clone() {
        try {
            RightsWindowContract clone = (RightsWindowContract)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}