package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamDrmInfo")
public class StreamDrmInfo implements Cloneable {

    public DrmInfoString keyId = null;
    public DrmInfoString key = null;
    public DrmInfoString contentPackagerPublicKey = null;
    public DrmInfoString keySeed = null;
    public String type = null;

    public StreamDrmInfo setKeyId(DrmInfoString keyId) {
        this.keyId = keyId;
        return this;
    }
    public StreamDrmInfo setKey(DrmInfoString key) {
        this.key = key;
        return this;
    }
    public StreamDrmInfo setContentPackagerPublicKey(DrmInfoString contentPackagerPublicKey) {
        this.contentPackagerPublicKey = contentPackagerPublicKey;
        return this;
    }
    public StreamDrmInfo setKeySeed(DrmInfoString keySeed) {
        this.keySeed = keySeed;
        return this;
    }
    public StreamDrmInfo setType(String type) {
        this.type = type;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDrmInfo))
            return false;

        StreamDrmInfo o = (StreamDrmInfo) other;
        if(o.keyId == null) {
            if(keyId != null) return false;
        } else if(!o.keyId.equals(keyId)) return false;
        if(o.key == null) {
            if(key != null) return false;
        } else if(!o.key.equals(key)) return false;
        if(o.contentPackagerPublicKey == null) {
            if(contentPackagerPublicKey != null) return false;
        } else if(!o.contentPackagerPublicKey.equals(contentPackagerPublicKey)) return false;
        if(o.keySeed == null) {
            if(keySeed != null) return false;
        } else if(!o.keySeed.equals(keySeed)) return false;
        if(o.type == null) {
            if(type != null) return false;
        } else if(!o.type.equals(type)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (keyId == null ? 1237 : keyId.hashCode());
        hashCode = hashCode * 31 + (key == null ? 1237 : key.hashCode());
        hashCode = hashCode * 31 + (contentPackagerPublicKey == null ? 1237 : contentPackagerPublicKey.hashCode());
        hashCode = hashCode * 31 + (keySeed == null ? 1237 : keySeed.hashCode());
        hashCode = hashCode * 31 + (type == null ? 1237 : type.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamDrmInfo{");
        builder.append("keyId=").append(keyId);
        builder.append(",key=").append(key);
        builder.append(",contentPackagerPublicKey=").append(contentPackagerPublicKey);
        builder.append(",keySeed=").append(keySeed);
        builder.append(",type=").append(type);
        builder.append("}");
        return builder.toString();
    }

    public StreamDrmInfo clone() {
        try {
            StreamDrmInfo clone = (StreamDrmInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}