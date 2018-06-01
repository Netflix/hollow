package com.netflix.vms.transformer.hollowoutput;


public class StreamDrmData implements Cloneable {

    public DrmKey drmKey = null;
    public WmDrmKey wmDrmKey = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDrmData))
            return false;

        StreamDrmData o = (StreamDrmData) other;
        if(o.drmKey == null) {
            if(drmKey != null) return false;
        } else if(!o.drmKey.equals(drmKey)) return false;
        if(o.wmDrmKey == null) {
            if(wmDrmKey != null) return false;
        } else if(!o.wmDrmKey.equals(wmDrmKey)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (drmKey == null ? 1237 : drmKey.hashCode());
        hashCode = hashCode * 31 + (wmDrmKey == null ? 1237 : wmDrmKey.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamDrmData{");
        builder.append("drmKey=").append(drmKey);
        builder.append(",wmDrmKey=").append(wmDrmKey);
        builder.append("}");
        return builder.toString();
    }

    public StreamDrmData clone() {
        try {
            StreamDrmData clone = (StreamDrmData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
