package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Characters")
public class Characters implements Cloneable {

    public long id = java.lang.Long.MIN_VALUE;
    public String prefix = null;
    public TranslatedText b = null;
    public TranslatedText cn = null;

    public Characters setId(long id) {
        this.id = id;
        return this;
    }
    public Characters setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
    public Characters setB(TranslatedText b) {
        this.b = b;
        return this;
    }
    public Characters setCn(TranslatedText cn) {
        this.cn = cn;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Characters))
            return false;

        Characters o = (Characters) other;
        if(o.id != id) return false;
        if(o.prefix == null) {
            if(prefix != null) return false;
        } else if(!o.prefix.equals(prefix)) return false;
        if(o.b == null) {
            if(b != null) return false;
        } else if(!o.b.equals(b)) return false;
        if(o.cn == null) {
            if(cn != null) return false;
        } else if(!o.cn.equals(cn)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (id ^ (id >>> 32));
        hashCode = hashCode * 31 + (prefix == null ? 1237 : prefix.hashCode());
        hashCode = hashCode * 31 + (b == null ? 1237 : b.hashCode());
        hashCode = hashCode * 31 + (cn == null ? 1237 : cn.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Characters{");
        builder.append("id=").append(id);
        builder.append(",prefix=").append(prefix);
        builder.append(",b=").append(b);
        builder.append(",cn=").append(cn);
        builder.append("}");
        return builder.toString();
    }

    public Characters clone() {
        try {
            Characters clone = (Characters)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}