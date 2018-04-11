package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Map;


@SuppressWarnings("all")
@HollowTypeName(name="LocalizedMetadata")
public class LocalizedMetadata implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public String attributeName = null;
    public String label = null;
    @HollowTypeName(name="MapOfTranslatedText")
    public Map<MapKey, TranslatedTextValue> translatedTexts = null;

    public LocalizedMetadata setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public LocalizedMetadata setAttributeName(String attributeName) {
        this.attributeName = attributeName;
        return this;
    }
    public LocalizedMetadata setLabel(String label) {
        this.label = label;
        return this;
    }
    public LocalizedMetadata setTranslatedTexts(Map<MapKey, TranslatedTextValue> translatedTexts) {
        this.translatedTexts = translatedTexts;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof LocalizedMetadata))
            return false;

        LocalizedMetadata o = (LocalizedMetadata) other;
        if(o.movieId != movieId) return false;
        if(o.attributeName == null) {
            if(attributeName != null) return false;
        } else if(!o.attributeName.equals(attributeName)) return false;
        if(o.label == null) {
            if(label != null) return false;
        } else if(!o.label.equals(label)) return false;
        if(o.translatedTexts == null) {
            if(translatedTexts != null) return false;
        } else if(!o.translatedTexts.equals(translatedTexts)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (attributeName == null ? 1237 : attributeName.hashCode());
        hashCode = hashCode * 31 + (label == null ? 1237 : label.hashCode());
        hashCode = hashCode * 31 + (translatedTexts == null ? 1237 : translatedTexts.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("LocalizedMetadata{");
        builder.append("movieId=").append(movieId);
        builder.append(",attributeName=").append(attributeName);
        builder.append(",label=").append(label);
        builder.append(",translatedTexts=").append(translatedTexts);
        builder.append("}");
        return builder.toString();
    }

    public LocalizedMetadata clone() {
        try {
            LocalizedMetadata clone = (LocalizedMetadata)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}