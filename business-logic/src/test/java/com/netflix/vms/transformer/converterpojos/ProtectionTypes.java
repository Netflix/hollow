package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ProtectionTypes")
public class ProtectionTypes implements Cloneable {

    public String name = null;
    public long id = java.lang.Long.MIN_VALUE;

    public ProtectionTypes setName(String name) {
        this.name = name;
        return this;
    }
    public ProtectionTypes setId(long id) {
        this.id = id;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ProtectionTypes))
            return false;

        ProtectionTypes o = (ProtectionTypes) other;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.id != id) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (int) (id ^ (id >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ProtectionTypes{");
        builder.append("name=").append(name);
        builder.append(",id=").append(id);
        builder.append("}");
        return builder.toString();
    }

    public ProtectionTypes clone() {
        try {
            ProtectionTypes clone = (ProtectionTypes)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}