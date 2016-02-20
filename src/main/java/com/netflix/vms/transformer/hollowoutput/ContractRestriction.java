package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractRestriction {

    public Set<Long> excludedDownloadables = null;
    public AvailabilityWindow availabilityWindow = null;
    public List<CupKey> cupKeys = null;
    public int prePromotionDays = java.lang.Integer.MIN_VALUE;
    public int postPromotionDays = java.lang.Integer.MIN_VALUE;
    public Map<Strings, LanguageRestrictions> languageBcp47RestrictionsMap = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ContractRestriction))
            return false;

        ContractRestriction o = (ContractRestriction) other;
        if(o.excludedDownloadables == null) {
            if(excludedDownloadables != null) return false;
        } else if(!o.excludedDownloadables.equals(excludedDownloadables)) return false;
        if(o.availabilityWindow == null) {
            if(availabilityWindow != null) return false;
        } else if(!o.availabilityWindow.equals(availabilityWindow)) return false;
        if(o.cupKeys == null) {
            if(cupKeys != null) return false;
        } else if(!o.cupKeys.equals(cupKeys)) return false;
        if(o.prePromotionDays != prePromotionDays) return false;
        if(o.postPromotionDays != postPromotionDays) return false;
        if(o.languageBcp47RestrictionsMap == null) {
            if(languageBcp47RestrictionsMap != null) return false;
        } else if(!o.languageBcp47RestrictionsMap.equals(languageBcp47RestrictionsMap)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}