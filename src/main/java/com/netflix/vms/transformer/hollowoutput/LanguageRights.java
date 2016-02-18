package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class LanguageRights {

    public int contractId;
    public Video videoId;
    public Map<ISOCountry, Map<Integer, LanguageRestrictions>> languageRestrictionsMap;
    public Map<Strings, Map<Integer, LanguageRestrictions>> fallbackRestrictionsMap;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof LanguageRights))
            return false;

        LanguageRights o = (LanguageRights) other;
        if(o.contractId != contractId) return false;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
        if(o.languageRestrictionsMap == null) {
            if(languageRestrictionsMap != null) return false;
        } else if(!o.languageRestrictionsMap.equals(languageRestrictionsMap)) return false;
        if(o.fallbackRestrictionsMap == null) {
            if(fallbackRestrictionsMap != null) return false;
        } else if(!o.fallbackRestrictionsMap.equals(fallbackRestrictionsMap)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}