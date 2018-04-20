package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="PersonVideoAliasId")
public class PersonVideoAliasId implements Cloneable {

    public int value = java.lang.Integer.MIN_VALUE;

    public PersonVideoAliasId() { }

    public PersonVideoAliasId(int value) {
        this.value = value;
    }

    public PersonVideoAliasId setValue(int value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonVideoAliasId))
            return false;

        PersonVideoAliasId o = (PersonVideoAliasId) other;
        if(o.value != value) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + value;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonVideoAliasId{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public PersonVideoAliasId clone() {
        try {
            PersonVideoAliasId clone = (PersonVideoAliasId)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}