package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="DrmHeaderInfo")
public class DrmHeaderInfo implements Cloneable {

    public String keyId = null;
    public long drmSystemId = java.lang.Long.MIN_VALUE;
    public String checksum = null;

    public DrmHeaderInfo setKeyId(String keyId) {
        this.keyId = keyId;
        return this;
    }
    public DrmHeaderInfo setDrmSystemId(long drmSystemId) {
        this.drmSystemId = drmSystemId;
        return this;
    }
    public DrmHeaderInfo setChecksum(String checksum) {
        this.checksum = checksum;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmHeaderInfo))
            return false;

        DrmHeaderInfo o = (DrmHeaderInfo) other;
        if(o.keyId == null) {
            if(keyId != null) return false;
        } else if(!o.keyId.equals(keyId)) return false;
        if(o.drmSystemId != drmSystemId) return false;
        if(o.checksum == null) {
            if(checksum != null) return false;
        } else if(!o.checksum.equals(checksum)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (keyId == null ? 1237 : keyId.hashCode());
        hashCode = hashCode * 31 + (int) (drmSystemId ^ (drmSystemId >>> 32));
        hashCode = hashCode * 31 + (checksum == null ? 1237 : checksum.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DrmHeaderInfo{");
        builder.append("keyId=").append(keyId);
        builder.append(",drmSystemId=").append(drmSystemId);
        builder.append(",checksum=").append(checksum);
        builder.append("}");
        return builder.toString();
    }

    public DrmHeaderInfo clone() {
        try {
            DrmHeaderInfo clone = (DrmHeaderInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}