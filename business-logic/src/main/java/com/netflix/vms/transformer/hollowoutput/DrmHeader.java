package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Map;

public class DrmHeader implements Cloneable {

    public int drmSystemId = java.lang.Integer.MIN_VALUE;
    public byte[] keyId = null;
    public byte[] checksum = null;
    public Map<Strings, Strings> attributes = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmHeader))
            return false;

        DrmHeader o = (DrmHeader) other;
        if(o.drmSystemId != drmSystemId) return false;
        if(!Arrays.equals(o.keyId, keyId)) return false;
        if(!Arrays.equals(o.checksum, checksum)) return false;
        if(o.attributes == null) {
            if(attributes != null) return false;
        } else if(!o.attributes.equals(attributes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + drmSystemId;
        hashCode = hashCode * 31 + Arrays.hashCode(keyId);
        hashCode = hashCode * 31 + Arrays.hashCode(checksum);
        hashCode = hashCode * 31 + (attributes == null ? 1237 : attributes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DrmHeader{");
        builder.append("drmSystemId=").append(drmSystemId);
        builder.append(",keyId=").append(keyId);
        builder.append(",checksum=").append(checksum);
        builder.append(",attributes=").append(attributes);
        builder.append("}");
        return builder.toString();
    }

    public DrmHeader clone() {
        try {
            DrmHeader clone = (DrmHeader)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
