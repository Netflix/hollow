package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Awards")
public class Awards implements Cloneable {

    public long awardId = java.lang.Long.MIN_VALUE;
    public TranslatedText awardName = null;
    public TranslatedText alternateName = null;
    public TranslatedText description = null;

    public Awards setAwardId(long awardId) {
        this.awardId = awardId;
        return this;
    }
    public Awards setAwardName(TranslatedText awardName) {
        this.awardName = awardName;
        return this;
    }
    public Awards setAlternateName(TranslatedText alternateName) {
        this.alternateName = alternateName;
        return this;
    }
    public Awards setDescription(TranslatedText description) {
        this.description = description;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Awards))
            return false;

        Awards o = (Awards) other;
        if(o.awardId != awardId) return false;
        if(o.awardName == null) {
            if(awardName != null) return false;
        } else if(!o.awardName.equals(awardName)) return false;
        if(o.alternateName == null) {
            if(alternateName != null) return false;
        } else if(!o.alternateName.equals(alternateName)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (awardId ^ (awardId >>> 32));
        hashCode = hashCode * 31 + (awardName == null ? 1237 : awardName.hashCode());
        hashCode = hashCode * 31 + (alternateName == null ? 1237 : alternateName.hashCode());
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Awards{");
        builder.append("awardId=").append(awardId);
        builder.append(",awardName=").append(awardName);
        builder.append(",alternateName=").append(alternateName);
        builder.append(",description=").append(description);
        builder.append("}");
        return builder.toString();
    }

    public Awards clone() {
        try {
            Awards clone = (Awards)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}