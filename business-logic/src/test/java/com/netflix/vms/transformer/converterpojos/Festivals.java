package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Festivals")
public class Festivals implements Cloneable {

    public long festivalId = java.lang.Long.MIN_VALUE;
    public TranslatedText copyright = null;
    public TranslatedText festivalName = null;
    public TranslatedText description = null;
    public TranslatedText shortName = null;
    public TranslatedText singularName = null;

    public Festivals setFestivalId(long festivalId) {
        this.festivalId = festivalId;
        return this;
    }
    public Festivals setCopyright(TranslatedText copyright) {
        this.copyright = copyright;
        return this;
    }
    public Festivals setFestivalName(TranslatedText festivalName) {
        this.festivalName = festivalName;
        return this;
    }
    public Festivals setDescription(TranslatedText description) {
        this.description = description;
        return this;
    }
    public Festivals setShortName(TranslatedText shortName) {
        this.shortName = shortName;
        return this;
    }
    public Festivals setSingularName(TranslatedText singularName) {
        this.singularName = singularName;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Festivals))
            return false;

        Festivals o = (Festivals) other;
        if(o.festivalId != festivalId) return false;
        if(o.copyright == null) {
            if(copyright != null) return false;
        } else if(!o.copyright.equals(copyright)) return false;
        if(o.festivalName == null) {
            if(festivalName != null) return false;
        } else if(!o.festivalName.equals(festivalName)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        if(o.shortName == null) {
            if(shortName != null) return false;
        } else if(!o.shortName.equals(shortName)) return false;
        if(o.singularName == null) {
            if(singularName != null) return false;
        } else if(!o.singularName.equals(singularName)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (festivalId ^ (festivalId >>> 32));
        hashCode = hashCode * 31 + (copyright == null ? 1237 : copyright.hashCode());
        hashCode = hashCode * 31 + (festivalName == null ? 1237 : festivalName.hashCode());
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        hashCode = hashCode * 31 + (shortName == null ? 1237 : shortName.hashCode());
        hashCode = hashCode * 31 + (singularName == null ? 1237 : singularName.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Festivals{");
        builder.append("festivalId=").append(festivalId);
        builder.append(",copyright=").append(copyright);
        builder.append(",festivalName=").append(festivalName);
        builder.append(",description=").append(description);
        builder.append(",shortName=").append(shortName);
        builder.append(",singularName=").append(singularName);
        builder.append("}");
        return builder.toString();
    }

    public Festivals clone() {
        try {
            Festivals clone = (Festivals)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}