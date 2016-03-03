package com.netflix.vms.transformer.hollowoutput;


public class DrmKey implements Cloneable {

    public long keyId = java.lang.Long.MIN_VALUE;
    public Video videoId = null;
    public DrmKeyString encryptedContentKey = null;
    public boolean keyDecrypted = false;

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
        if(o.keyDecrypted != keyDecrypted) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (int) (keyId ^ (keyId >>> 32));
        hashCode = hashCode * 31 + (videoId == null ? 1237 : videoId.hashCode());
        hashCode = hashCode * 31 + (encryptedContentKey == null ? 1237 : encryptedContentKey.hashCode());
        hashCode = hashCode * 31 + (keyDecrypted? 1231 : 1237);
        return hashCode;
    }

    public DrmKey clone() {
        try {
            DrmKey clone = (DrmKey)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}