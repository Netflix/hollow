package com.netflix.vms.transformer.hollowoutput;


public class WmDrmKey {

    public long downloadableId = java.lang.Long.MIN_VALUE;
    public DrmKeyString contentPackagerPublicKey = null;
    public DrmKeyString encryptedContentKey = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof WmDrmKey))
            return false;

        WmDrmKey o = (WmDrmKey) other;
        if(o.downloadableId != downloadableId) return false;
        if(o.contentPackagerPublicKey == null) {
            if(contentPackagerPublicKey != null) return false;
        } else if(!o.contentPackagerPublicKey.equals(contentPackagerPublicKey)) return false;
        if(o.encryptedContentKey == null) {
            if(encryptedContentKey != null) return false;
        } else if(!o.encryptedContentKey.equals(encryptedContentKey)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}