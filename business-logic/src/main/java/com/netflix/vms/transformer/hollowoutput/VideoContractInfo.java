package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;

public class VideoContractInfo implements Cloneable {

    public int contractId = java.lang.Integer.MIN_VALUE;
    public boolean isDownloadable = false;
    public int primaryPackageId = java.lang.Integer.MIN_VALUE;
    public int prePromotionDays = java.lang.Integer.MIN_VALUE;
    public int postPromotionDays = java.lang.Integer.MIN_VALUE;
    public boolean isDayAfterBroadcast = false;
    public boolean hasRollingEpisodes = false;
    public LinkedHashSetOfStrings cupTokens = null;
    public Set<Strings> assetBcp47Codes = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoContractInfo))
            return false;

        VideoContractInfo o = (VideoContractInfo) other;
        if(o.contractId != contractId) return false;
        if(o.isDownloadable != isDownloadable) return false;
        if(o.primaryPackageId != primaryPackageId) return false;
        if(o.prePromotionDays != prePromotionDays) return false;
        if(o.postPromotionDays != postPromotionDays) return false;
        if(o.isDayAfterBroadcast != isDayAfterBroadcast) return false;
        if(o.hasRollingEpisodes != hasRollingEpisodes) return false;
        if(o.cupTokens == null) {
            if(cupTokens != null) return false;
        } else if(!o.cupTokens.equals(cupTokens)) return false;
        if(o.assetBcp47Codes == null) {
            if(assetBcp47Codes != null) return false;
        } else if(!o.assetBcp47Codes.equals(assetBcp47Codes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + contractId;
        hashCode = hashCode * 31 + (isDownloadable ? 1231 : 1237);
        hashCode = hashCode * 31 + primaryPackageId;
        hashCode = hashCode * 31 + prePromotionDays;
        hashCode = hashCode * 31 + postPromotionDays;
        hashCode = hashCode * 31 + (isDayAfterBroadcast? 1231 : 1237);
        hashCode = hashCode * 31 + (hasRollingEpisodes? 1231 : 1237);
        hashCode = hashCode * 31 + (cupTokens == null ? 1237 : cupTokens.hashCode());
        hashCode = hashCode * 31 + (assetBcp47Codes == null ? 1237 : assetBcp47Codes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoContractInfo{");
        builder.append("contractId=").append(contractId);
        builder.append(",isDownloadable=").append(isDownloadable);
        builder.append(",primaryPackageId=").append(primaryPackageId);
        builder.append(",prePromotionDays=").append(prePromotionDays);
        builder.append(",postPromotionDays=").append(postPromotionDays);
        builder.append(",isDayAfterBroadcast=").append(isDayAfterBroadcast);
        builder.append(",hasRollingEpisodes=").append(hasRollingEpisodes);
        builder.append(",cupTokens=").append(cupTokens);
        builder.append(",assetBcp47Codes=").append(assetBcp47Codes);
        builder.append("}");
        return builder.toString();
    }

    public VideoContractInfo clone() {
        try {
            VideoContractInfo clone = (VideoContractInfo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}