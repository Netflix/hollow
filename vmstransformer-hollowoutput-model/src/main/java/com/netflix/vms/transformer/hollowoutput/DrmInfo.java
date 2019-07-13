package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class DrmInfo implements Cloneable {

    public int drmKeyGroup = java.lang.Integer.MIN_VALUE;
    public DrmKey drmKey = null;
    // TODO(timt): make `java.lang.Integer` do the right thing here
    public Map<com.netflix.vms.transformer.hollowoutput.Integer, DrmHeader> drmHeaders = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmInfo))
            return false;

        DrmInfo o = (DrmInfo) other;
        if(o.drmKeyGroup != drmKeyGroup) return false;
        if(o.drmKey == null) {
            if(drmKey != null) return false;
        } else if(!o.drmKey.equals(drmKey)) return false;
        if(o.drmHeaders == null) {
            if(drmHeaders != null) return false;
        } else if(!o.drmHeaders.equals(drmHeaders)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + drmKeyGroup;
        hashCode = hashCode * 31 + (drmKey == null ? 1237 : drmKey.hashCode());
        hashCode = hashCode * 31 + (drmHeaders == null ? 1237 : drmHeaders.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DrmInfo{");
        builder.append("drmKeyGroup=").append(drmKeyGroup);
        builder.append(",drmKey=").append(drmKey);
        builder.append(",drmHeaders=").append(drmHeaders);
        builder.append("}");
        return builder.toString();
    }

    public DrmInfo clone() {
        try {
            DrmInfo clone = (DrmInfo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
