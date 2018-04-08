package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Map;


@SuppressWarnings("all")
@HollowTypeName(name="TranslatedText")
public class TranslatedText implements Cloneable {

    @HollowTypeName(name="MapOfTranslatedText")
    public Map<MapKey, TranslatedTextValue> translatedTexts = null;

    public TranslatedText() { }

    public TranslatedText(Map<MapKey, TranslatedTextValue> value) {
        this.translatedTexts = value;
    }

    public TranslatedText setTranslatedTexts(Map<MapKey, TranslatedTextValue> translatedTexts) {
        this.translatedTexts = translatedTexts;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TranslatedText))
            return false;

        TranslatedText o = (TranslatedText) other;
        if(o.translatedTexts == null) {
            if(translatedTexts != null) return false;
        } else if(!o.translatedTexts.equals(translatedTexts)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (translatedTexts == null ? 1237 : translatedTexts.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TranslatedText{");
        builder.append("translatedTexts=").append(translatedTexts);
        builder.append("}");
        return builder.toString();
    }

    public TranslatedText clone() {
        try {
            TranslatedText clone = (TranslatedText)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}