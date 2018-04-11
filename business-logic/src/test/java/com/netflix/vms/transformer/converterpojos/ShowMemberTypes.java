package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ShowMemberTypes")
public class ShowMemberTypes implements Cloneable {

    public long showMemberTypeId = java.lang.Long.MIN_VALUE;
    public TranslatedText displayName = null;

    public ShowMemberTypes setShowMemberTypeId(long showMemberTypeId) {
        this.showMemberTypeId = showMemberTypeId;
        return this;
    }
    public ShowMemberTypes setDisplayName(TranslatedText displayName) {
        this.displayName = displayName;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ShowMemberTypes))
            return false;

        ShowMemberTypes o = (ShowMemberTypes) other;
        if(o.showMemberTypeId != showMemberTypeId) return false;
        if(o.displayName == null) {
            if(displayName != null) return false;
        } else if(!o.displayName.equals(displayName)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (showMemberTypeId ^ (showMemberTypeId >>> 32));
        hashCode = hashCode * 31 + (displayName == null ? 1237 : displayName.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ShowMemberTypes{");
        builder.append("showMemberTypeId=").append(showMemberTypeId);
        builder.append(",displayName=").append(displayName);
        builder.append("}");
        return builder.toString();
    }

    public ShowMemberTypes clone() {
        try {
            ShowMemberTypes clone = (ShowMemberTypes)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}