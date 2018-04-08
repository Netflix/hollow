package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CategoryGroups")
public class CategoryGroups implements Cloneable {

    public long categoryGroupId = java.lang.Long.MIN_VALUE;
    public TranslatedText categoryGroupName = null;

    public CategoryGroups setCategoryGroupId(long categoryGroupId) {
        this.categoryGroupId = categoryGroupId;
        return this;
    }
    public CategoryGroups setCategoryGroupName(TranslatedText categoryGroupName) {
        this.categoryGroupName = categoryGroupName;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CategoryGroups))
            return false;

        CategoryGroups o = (CategoryGroups) other;
        if(o.categoryGroupId != categoryGroupId) return false;
        if(o.categoryGroupName == null) {
            if(categoryGroupName != null) return false;
        } else if(!o.categoryGroupName.equals(categoryGroupName)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (categoryGroupId ^ (categoryGroupId >>> 32));
        hashCode = hashCode * 31 + (categoryGroupName == null ? 1237 : categoryGroupName.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CategoryGroups{");
        builder.append("categoryGroupId=").append(categoryGroupId);
        builder.append(",categoryGroupName=").append(categoryGroupName);
        builder.append("}");
        return builder.toString();
    }

    public CategoryGroups clone() {
        try {
            CategoryGroups clone = (CategoryGroups)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}