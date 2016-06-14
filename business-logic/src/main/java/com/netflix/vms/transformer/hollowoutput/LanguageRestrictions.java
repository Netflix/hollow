package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;

public class LanguageRestrictions implements Cloneable {

    public int audioLanguageId = java.lang.Integer.MIN_VALUE;
    public Strings audioLanguage = null;
    public Set<Integer> disallowedTimedText = null;
    public Set<Strings> disallowedTimedTextBcp47codes = null;
    public boolean requiresForcedSubtitles = false;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + audioLanguageId;
        hashCode = hashCode * 31 + (audioLanguage == null ? 1237 : audioLanguage.hashCode());
        hashCode = hashCode * 31 + (disallowedTimedText == null ? 1237 : disallowedTimedText.hashCode());
        hashCode = hashCode * 31 + (disallowedTimedTextBcp47codes == null ? 1237 : disallowedTimedTextBcp47codes.hashCode());
        hashCode = hashCode * 31 + (requiresForcedSubtitles? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("LanguageRestrictions{");
        builder.append("audioLanguageId=").append(audioLanguageId);
        builder.append(",audioLanguage=").append(audioLanguage);
        builder.append(",disallowedTimedText=").append(disallowedTimedText);
        builder.append(",disallowedTimedTextBcp47codes=").append(disallowedTimedTextBcp47codes);
        builder.append(",requiresForcedSubtitles=").append(requiresForcedSubtitles);
        builder.append("}");
        return builder.toString();
    }

    public LanguageRestrictions clone() {
        try {
            LanguageRestrictions clone = (LanguageRestrictions)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}