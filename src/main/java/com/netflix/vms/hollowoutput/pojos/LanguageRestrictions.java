package com.netflix.vms.hollowoutput.pojos;

import java.util.Set;

public class LanguageRestrictions {

    public int audioLanguageId;
    public Strings audioLanguage;
    public Set<Integer> disallowedTimedText;
    public Set<Strings> disallowedTimedTextBcp47codes;
    public boolean requiresForcedSubtitles;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof LanguageRestrictions))
            return false;

        LanguageRestrictions o = (LanguageRestrictions) other;
        if(o.audioLanguageId != audioLanguageId) return false;
        if(o.audioLanguage == null) {
            if(audioLanguage != null) return false;
        } else if(!o.audioLanguage.equals(audioLanguage)) return false;
        if(o.disallowedTimedText == null) {
            if(disallowedTimedText != null) return false;
        } else if(!o.disallowedTimedText.equals(disallowedTimedText)) return false;
        if(o.disallowedTimedTextBcp47codes == null) {
            if(disallowedTimedTextBcp47codes != null) return false;
        } else if(!o.disallowedTimedTextBcp47codes.equals(disallowedTimedTextBcp47codes)) return false;
        if(o.requiresForcedSubtitles != requiresForcedSubtitles) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}