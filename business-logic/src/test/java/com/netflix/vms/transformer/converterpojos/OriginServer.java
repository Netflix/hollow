package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="OriginServer")
public class OriginServer implements Cloneable {

    public long id = java.lang.Long.MIN_VALUE;
    public String name = null;
    public String storageGroupId = null;

    public OriginServer setId(long id) {
        this.id = id;
        return this;
    }
    public OriginServer setName(String name) {
        this.name = name;
        return this;
    }
    public OriginServer setStorageGroupId(String storageGroupId) {
        this.storageGroupId = storageGroupId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof OriginServer))
            return false;

        OriginServer o = (OriginServer) other;
        if(o.id != id) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.storageGroupId == null) {
            if(storageGroupId != null) return false;
        } else if(!o.storageGroupId.equals(storageGroupId)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (id ^ (id >>> 32));
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (storageGroupId == null ? 1237 : storageGroupId.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("OriginServer{");
        builder.append("id=").append(id);
        builder.append(",name=").append(name);
        builder.append(",storageGroupId=").append(storageGroupId);
        builder.append("}");
        return builder.toString();
    }

    public OriginServer clone() {
        try {
            OriginServer clone = (OriginServer)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}