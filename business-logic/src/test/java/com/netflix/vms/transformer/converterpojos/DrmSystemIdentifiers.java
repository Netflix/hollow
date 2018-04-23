package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="DrmSystemIdentifiers")
public class DrmSystemIdentifiers implements Cloneable {

    public long id = java.lang.Long.MIN_VALUE;
    public String guid = null;
    public String name = null;
    public boolean headerDataAvailable = false;

    public DrmSystemIdentifiers setId(long id) {
        this.id = id;
        return this;
    }
    public DrmSystemIdentifiers setGuid(String guid) {
        this.guid = guid;
        return this;
    }
    public DrmSystemIdentifiers setName(String name) {
        this.name = name;
        return this;
    }
    public DrmSystemIdentifiers setHeaderDataAvailable(boolean headerDataAvailable) {
        this.headerDataAvailable = headerDataAvailable;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmSystemIdentifiers))
            return false;

        DrmSystemIdentifiers o = (DrmSystemIdentifiers) other;
        if(o.id != id) return false;
        if(o.guid == null) {
            if(guid != null) return false;
        } else if(!o.guid.equals(guid)) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.headerDataAvailable != headerDataAvailable) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (id ^ (id >>> 32));
        hashCode = hashCode * 31 + (guid == null ? 1237 : guid.hashCode());
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (headerDataAvailable? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DrmSystemIdentifiers{");
        builder.append("id=").append(id);
        builder.append(",guid=").append(guid);
        builder.append(",name=").append(name);
        builder.append(",headerDataAvailable=").append(headerDataAvailable);
        builder.append("}");
        return builder.toString();
    }

    public DrmSystemIdentifiers clone() {
        try {
            DrmSystemIdentifiers clone = (DrmSystemIdentifiers)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}