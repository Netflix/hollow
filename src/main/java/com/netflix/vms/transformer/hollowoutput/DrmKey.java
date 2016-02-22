package com.netflix.vms.transformer.hollowoutput;


public class DrmKey implements Cloneable {

    public long keyId = java.lang.Long.MIN_VALUE;
    public Video videoId = null;
    public DrmKeyString encryptedContentKey = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmKey))
            return false;

        DrmKey o = (DrmKey) other;
        if(o.keyId != keyId) return false;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
        if(o.encryptedContentKey == null) {
            if(encryptedContentKey != null) return false;
        } else if(!o.encryptedContentKey.equals(encryptedContentKey)) return false;
        return true;
    }

    public DrmKey clone() {
        try {
            return (DrmKey)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}