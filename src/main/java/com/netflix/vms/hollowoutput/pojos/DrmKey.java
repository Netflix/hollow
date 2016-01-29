package com.netflix.vms.hollowoutput.pojos;


public class DrmKey {

    public long keyId;
    public Video videoId;
    public DrmKeyString encryptedContentKey;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}