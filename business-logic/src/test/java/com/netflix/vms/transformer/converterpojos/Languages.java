package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Languages")
public class Languages implements Cloneable {

    public long languageId = java.lang.Long.MIN_VALUE;
    public TranslatedText name = null;

    public Languages setLanguageId(long languageId) {
        this.languageId = languageId;
        return this;
    }
    public Languages setName(TranslatedText name) {
        this.name = name;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Languages))
            return false;

        Languages o = (Languages) other;
        if(o.languageId != languageId) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (languageId ^ (languageId >>> 32));
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Languages{");
        builder.append("languageId=").append(languageId);
        builder.append(",name=").append(name);
        builder.append("}");
        return builder.toString();
    }

    public Languages clone() {
        try {
            Languages clone = (Languages)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}