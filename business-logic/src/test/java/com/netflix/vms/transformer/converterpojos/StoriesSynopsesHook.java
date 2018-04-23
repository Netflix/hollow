package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Map;


@SuppressWarnings("all")
@HollowTypeName(name="StoriesSynopsesHook")
public class StoriesSynopsesHook implements Cloneable {

    public String type = null;
    public String rank = null;
    @HollowTypeName(name="MapOfTranslatedText")
    public Map<MapKey, TranslatedTextValue> translatedTexts = null;

    public StoriesSynopsesHook setType(String type) {
        this.type = type;
        return this;
    }
    public StoriesSynopsesHook setRank(String rank) {
        this.rank = rank;
        return this;
    }
    public StoriesSynopsesHook setTranslatedTexts(Map<MapKey, TranslatedTextValue> translatedTexts) {
        this.translatedTexts = translatedTexts;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StoriesSynopsesHook))
            return false;

        StoriesSynopsesHook o = (StoriesSynopsesHook) other;
        if(o.type == null) {
            if(type != null) return false;
        } else if(!o.type.equals(type)) return false;
        if(o.rank == null) {
            if(rank != null) return false;
        } else if(!o.rank.equals(rank)) return false;
        if(o.translatedTexts == null) {
            if(translatedTexts != null) return false;
        } else if(!o.translatedTexts.equals(translatedTexts)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (type == null ? 1237 : type.hashCode());
        hashCode = hashCode * 31 + (rank == null ? 1237 : rank.hashCode());
        hashCode = hashCode * 31 + (translatedTexts == null ? 1237 : translatedTexts.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StoriesSynopsesHook{");
        builder.append("type=").append(type);
        builder.append(",rank=").append(rank);
        builder.append(",translatedTexts=").append(translatedTexts);
        builder.append("}");
        return builder.toString();
    }

    public StoriesSynopsesHook clone() {
        try {
            StoriesSynopsesHook clone = (StoriesSynopsesHook)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}