package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;

public class VideoContractInfo {

    public int contractId = java.lang.Integer.MIN_VALUE;
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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}