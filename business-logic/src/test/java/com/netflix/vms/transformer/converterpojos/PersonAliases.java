package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="PersonAliases")
public class PersonAliases implements Cloneable {

    public long aliasId = java.lang.Long.MIN_VALUE;
    public TranslatedText name = null;

    public PersonAliases setAliasId(long aliasId) {
        this.aliasId = aliasId;
        return this;
    }
    public PersonAliases setName(TranslatedText name) {
        this.name = name;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonAliases))
            return false;

        PersonAliases o = (PersonAliases) other;
        if(o.aliasId != aliasId) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (aliasId ^ (aliasId >>> 32));
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonAliases{");
        builder.append("aliasId=").append(aliasId);
        builder.append(",name=").append(name);
        builder.append("}");
        return builder.toString();
    }

    public PersonAliases clone() {
        try {
            PersonAliases clone = (PersonAliases)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}