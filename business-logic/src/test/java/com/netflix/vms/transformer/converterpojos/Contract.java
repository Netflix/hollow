package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="Contract")
public class Contract implements Cloneable {

    public long contractId = java.lang.Long.MIN_VALUE;
    public boolean original = false;
    public String cupToken = null;
    public boolean dayOfBroadcast = false;
    public long prePromotionDays = java.lang.Long.MIN_VALUE;
    public boolean dayAfterBroadcast = false;
    @HollowTypeName(name="DisallowedAssetBundlesList")
    public List<DisallowedAssetBundle> disallowedAssetBundles = null;

    public Contract setContractId(long contractId) {
        this.contractId = contractId;
        return this;
    }
    public Contract setOriginal(boolean original) {
        this.original = original;
        return this;
    }
    public Contract setCupToken(String cupToken) {
        this.cupToken = cupToken;
        return this;
    }
    public Contract setDayOfBroadcast(boolean dayOfBroadcast) {
        this.dayOfBroadcast = dayOfBroadcast;
        return this;
    }
    public Contract setPrePromotionDays(long prePromotionDays) {
        this.prePromotionDays = prePromotionDays;
        return this;
    }
    public Contract setDayAfterBroadcast(boolean dayAfterBroadcast) {
        this.dayAfterBroadcast = dayAfterBroadcast;
        return this;
    }
    public Contract setDisallowedAssetBundles(List<DisallowedAssetBundle> disallowedAssetBundles) {
        this.disallowedAssetBundles = disallowedAssetBundles;
        return this;
    }
    public Contract addToDisallowedAssetBundles(DisallowedAssetBundle disallowedAssetBundle) {
        if (this.disallowedAssetBundles == null) {
            this.disallowedAssetBundles = new ArrayList<DisallowedAssetBundle>();
        }
        this.disallowedAssetBundles.add(disallowedAssetBundle);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Contract))
            return false;

        Contract o = (Contract) other;
        if(o.contractId != contractId) return false;
        if(o.original != original) return false;
        if(o.cupToken == null) {
            if(cupToken != null) return false;
        } else if(!o.cupToken.equals(cupToken)) return false;
        if(o.dayOfBroadcast != dayOfBroadcast) return false;
        if(o.prePromotionDays != prePromotionDays) return false;
        if(o.dayAfterBroadcast != dayAfterBroadcast) return false;
        if(o.disallowedAssetBundles == null) {
            if(disallowedAssetBundles != null) return false;
        } else if(!o.disallowedAssetBundles.equals(disallowedAssetBundles)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (contractId ^ (contractId >>> 32));
        hashCode = hashCode * 31 + (original? 1231 : 1237);
        hashCode = hashCode * 31 + (cupToken == null ? 1237 : cupToken.hashCode());
        hashCode = hashCode * 31 + (dayOfBroadcast? 1231 : 1237);
        hashCode = hashCode * 31 + (int) (prePromotionDays ^ (prePromotionDays >>> 32));
        hashCode = hashCode * 31 + (dayAfterBroadcast? 1231 : 1237);
        hashCode = hashCode * 31 + (disallowedAssetBundles == null ? 1237 : disallowedAssetBundles.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Contract{");
        builder.append("contractId=").append(contractId);
        builder.append(",original=").append(original);
        builder.append(",cupToken=").append(cupToken);
        builder.append(",dayOfBroadcast=").append(dayOfBroadcast);
        builder.append(",prePromotionDays=").append(prePromotionDays);
        builder.append(",dayAfterBroadcast=").append(dayAfterBroadcast);
        builder.append(",disallowedAssetBundles=").append(disallowedAssetBundles);
        builder.append("}");
        return builder.toString();
    }

    public Contract clone() {
        try {
            Contract clone = (Contract)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}