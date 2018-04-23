package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="PersonCharacterResource")
public class PersonCharacterResource implements Cloneable {

    public long id = java.lang.Long.MIN_VALUE;
    public String prefix = null;
    public TranslatedText cn = null;

    public PersonCharacterResource setId(long id) {
        this.id = id;
        return this;
    }
    public PersonCharacterResource setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
    public PersonCharacterResource setCn(TranslatedText cn) {
        this.cn = cn;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonCharacterResource))
            return false;

        PersonCharacterResource o = (PersonCharacterResource) other;
        if(o.id != id) return false;
        if(o.prefix == null) {
            if(prefix != null) return false;
        } else if(!o.prefix.equals(prefix)) return false;
        if(o.cn == null) {
            if(cn != null) return false;
        } else if(!o.cn.equals(cn)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (id ^ (id >>> 32));
        hashCode = hashCode * 31 + (prefix == null ? 1237 : prefix.hashCode());
        hashCode = hashCode * 31 + (cn == null ? 1237 : cn.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonCharacterResource{");
        builder.append("id=").append(id);
        builder.append(",prefix=").append(prefix);
        builder.append(",cn=").append(cn);
        builder.append("}");
        return builder.toString();
    }

    public PersonCharacterResource clone() {
        try {
            PersonCharacterResource clone = (PersonCharacterResource)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}