package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Map;


@SuppressWarnings("all")
@HollowTypeName(name="AltGenresAlternateNames")
public class AltGenresAlternateNames implements Cloneable {

    public long typeId = java.lang.Long.MIN_VALUE;
    public String type = null;
    @HollowTypeName(name="MapOfTranslatedText")
    public Map<MapKey, TranslatedTextValue> translatedTexts = null;

    public AltGenresAlternateNames setTypeId(long typeId) {
        this.typeId = typeId;
        return this;
    }
    public AltGenresAlternateNames setType(String type) {
        this.type = type;
        return this;
    }
    public AltGenresAlternateNames setTranslatedTexts(Map<MapKey, TranslatedTextValue> translatedTexts) {
        this.translatedTexts = translatedTexts;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AltGenresAlternateNames))
            return false;

        AltGenresAlternateNames o = (AltGenresAlternateNames) other;
        if(o.typeId != typeId) return false;
        if(o.type == null) {
            if(type != null) return false;
        } else if(!o.type.equals(type)) return false;
        if(o.translatedTexts == null) {
            if(translatedTexts != null) return false;
        } else if(!o.translatedTexts.equals(translatedTexts)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (typeId ^ (typeId >>> 32));
        hashCode = hashCode * 31 + (type == null ? 1237 : type.hashCode());
        hashCode = hashCode * 31 + (translatedTexts == null ? 1237 : translatedTexts.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AltGenresAlternateNames{");
        builder.append("typeId=").append(typeId);
        builder.append(",type=").append(type);
        builder.append(",translatedTexts=").append(translatedTexts);
        builder.append("}");
        return builder.toString();
    }

    public AltGenresAlternateNames clone() {
        try {
            AltGenresAlternateNames clone = (AltGenresAlternateNames)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}