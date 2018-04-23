package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Cdn")
public class Cdn implements Cloneable {

    public long id = java.lang.Long.MIN_VALUE;
    public String name = null;

    public Cdn setId(long id) {
        this.id = id;
        return this;
    }
    public Cdn setName(String name) {
        this.name = name;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Cdn))
            return false;

        Cdn o = (Cdn) other;
        if(o.id != id) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (id ^ (id >>> 32));
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Cdn{");
        builder.append("id=").append(id);
        builder.append(",name=").append(name);
        builder.append("}");
        return builder.toString();
    }

    public Cdn clone() {
        try {
            Cdn clone = (Cdn)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}