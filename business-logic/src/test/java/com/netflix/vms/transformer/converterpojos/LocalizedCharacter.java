package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Map;


@SuppressWarnings("all")
@HollowTypeName(name="LocalizedCharacter")
public class LocalizedCharacter implements Cloneable {

    public long characterId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="MapOfTranslatedText")
    public Map<MapKey, TranslatedTextValue> translatedTexts = null;
    public String label = null;
    public String attributeName = null;
    public Date lastUpdated = null;

    public LocalizedCharacter setCharacterId(long characterId) {
        this.characterId = characterId;
        return this;
    }
    public LocalizedCharacter setTranslatedTexts(Map<MapKey, TranslatedTextValue> translatedTexts) {
        this.translatedTexts = translatedTexts;
        return this;
    }
    public LocalizedCharacter setLabel(String label) {
        this.label = label;
        return this;
    }
    public LocalizedCharacter setAttributeName(String attributeName) {
        this.attributeName = attributeName;
        return this;
    }
    public LocalizedCharacter setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof LocalizedCharacter))
            return false;

        LocalizedCharacter o = (LocalizedCharacter) other;
        if(o.characterId != characterId) return false;
        if(o.translatedTexts == null) {
            if(translatedTexts != null) return false;
        } else if(!o.translatedTexts.equals(translatedTexts)) return false;
        if(o.label == null) {
            if(label != null) return false;
        } else if(!o.label.equals(label)) return false;
        if(o.attributeName == null) {
            if(attributeName != null) return false;
        } else if(!o.attributeName.equals(attributeName)) return false;
        if(o.lastUpdated == null) {
            if(lastUpdated != null) return false;
        } else if(!o.lastUpdated.equals(lastUpdated)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (characterId ^ (characterId >>> 32));
        hashCode = hashCode * 31 + (translatedTexts == null ? 1237 : translatedTexts.hashCode());
        hashCode = hashCode * 31 + (label == null ? 1237 : label.hashCode());
        hashCode = hashCode * 31 + (attributeName == null ? 1237 : attributeName.hashCode());
        hashCode = hashCode * 31 + (lastUpdated == null ? 1237 : lastUpdated.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("LocalizedCharacter{");
        builder.append("characterId=").append(characterId);
        builder.append(",translatedTexts=").append(translatedTexts);
        builder.append(",label=").append(label);
        builder.append(",attributeName=").append(attributeName);
        builder.append(",lastUpdated=").append(lastUpdated);
        builder.append("}");
        return builder.toString();
    }

    public LocalizedCharacter clone() {
        try {
            LocalizedCharacter clone = (LocalizedCharacter)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}