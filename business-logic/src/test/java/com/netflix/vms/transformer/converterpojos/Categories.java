package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Categories")
public class Categories implements Cloneable {

    public long categoryId = java.lang.Long.MIN_VALUE;
    public TranslatedText displayName = null;
    public TranslatedText shortName = null;

    public Categories setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        return this;
    }
    public Categories setDisplayName(TranslatedText displayName) {
        this.displayName = displayName;
        return this;
    }
    public Categories setShortName(TranslatedText shortName) {
        this.shortName = shortName;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Categories))
            return false;

        Categories o = (Categories) other;
        if(o.categoryId != categoryId) return false;
        if(o.displayName == null) {
            if(displayName != null) return false;
        } else if(!o.displayName.equals(displayName)) return false;
        if(o.shortName == null) {
            if(shortName != null) return false;
        } else if(!o.shortName.equals(shortName)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (categoryId ^ (categoryId >>> 32));
        hashCode = hashCode * 31 + (displayName == null ? 1237 : displayName.hashCode());
        hashCode = hashCode * 31 + (shortName == null ? 1237 : shortName.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Categories{");
        builder.append("categoryId=").append(categoryId);
        builder.append(",displayName=").append(displayName);
        builder.append(",shortName=").append(shortName);
        builder.append("}");
        return builder.toString();
    }

    public Categories clone() {
        try {
            Categories clone = (Categories)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}