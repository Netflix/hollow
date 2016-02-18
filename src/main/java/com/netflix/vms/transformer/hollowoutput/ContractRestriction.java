package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;
import java.util.Map;
import java.util.List;

public class ContractRestriction {

    public Set<Long> excludedDownloadables;
    public AvailabilityWindow availabilityWindow;
    public List<CupKey> cupKeys;
    public int prePromotionDays;
    public int postPromotionDays;
    public Map<Strings, LanguageRestrictions> languageBcp47RestrictionsMap;

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