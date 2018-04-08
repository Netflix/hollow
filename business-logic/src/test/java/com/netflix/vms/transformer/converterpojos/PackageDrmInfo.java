package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="PackageDrmInfo")
public class PackageDrmInfo implements Cloneable {

    public String contentPackagerPublicKey = null;
    public String keySeed = null;
    public long keyId = java.lang.Long.MIN_VALUE;
    public long drmKeyGroup = java.lang.Long.MIN_VALUE;
    public String key = null;
    @HollowTypeName(name="DrmHeaderInfoList")
    public List<DrmHeaderInfo> drmHeaderInfo = null;
    public boolean keyDecrypted = false;

    public PackageDrmInfo setContentPackagerPublicKey(String contentPackagerPublicKey) {
        this.contentPackagerPublicKey = contentPackagerPublicKey;
        return this;
    }
    public PackageDrmInfo setKeySeed(String keySeed) {
        this.keySeed = keySeed;
        return this;
    }
    public PackageDrmInfo setKeyId(long keyId) {
        this.keyId = keyId;
        return this;
    }
    public PackageDrmInfo setDrmKeyGroup(long drmKeyGroup) {
        this.drmKeyGroup = drmKeyGroup;
        return this;
    }
    public PackageDrmInfo setKey(String key) {
        this.key = key;
        return this;
    }
    public PackageDrmInfo setDrmHeaderInfo(List<DrmHeaderInfo> drmHeaderInfo) {
        this.drmHeaderInfo = drmHeaderInfo;
        return this;
    }
    public PackageDrmInfo setKeyDecrypted(boolean keyDecrypted) {
        this.keyDecrypted = keyDecrypted;
        return this;
    }
    public PackageDrmInfo addToDrmHeaderInfo(DrmHeaderInfo drmHeaderInfo) {
        if (this.drmHeaderInfo == null) {
            this.drmHeaderInfo = new ArrayList<DrmHeaderInfo>();
        }
        this.drmHeaderInfo.add(drmHeaderInfo);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PackageDrmInfo))
            return false;

        PackageDrmInfo o = (PackageDrmInfo) other;
        if(o.contentPackagerPublicKey == null) {
            if(contentPackagerPublicKey != null) return false;
        } else if(!o.contentPackagerPublicKey.equals(contentPackagerPublicKey)) return false;
        if(o.keySeed == null) {
            if(keySeed != null) return false;
        } else if(!o.keySeed.equals(keySeed)) return false;
        if(o.keyId != keyId) return false;
        if(o.drmKeyGroup != drmKeyGroup) return false;
        if(o.key == null) {
            if(key != null) return false;
        } else if(!o.key.equals(key)) return false;
        if(o.drmHeaderInfo == null) {
            if(drmHeaderInfo != null) return false;
        } else if(!o.drmHeaderInfo.equals(drmHeaderInfo)) return false;
        if(o.keyDecrypted != keyDecrypted) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (contentPackagerPublicKey == null ? 1237 : contentPackagerPublicKey.hashCode());
        hashCode = hashCode * 31 + (keySeed == null ? 1237 : keySeed.hashCode());
        hashCode = hashCode * 31 + (int) (keyId ^ (keyId >>> 32));
        hashCode = hashCode * 31 + (int) (drmKeyGroup ^ (drmKeyGroup >>> 32));
        hashCode = hashCode * 31 + (key == null ? 1237 : key.hashCode());
        hashCode = hashCode * 31 + (drmHeaderInfo == null ? 1237 : drmHeaderInfo.hashCode());
        hashCode = hashCode * 31 + (keyDecrypted? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PackageDrmInfo{");
        builder.append("contentPackagerPublicKey=").append(contentPackagerPublicKey);
        builder.append(",keySeed=").append(keySeed);
        builder.append(",keyId=").append(keyId);
        builder.append(",drmKeyGroup=").append(drmKeyGroup);
        builder.append(",key=").append(key);
        builder.append(",drmHeaderInfo=").append(drmHeaderInfo);
        builder.append(",keyDecrypted=").append(keyDecrypted);
        builder.append("}");
        return builder.toString();
    }

    public PackageDrmInfo clone() {
        try {
            PackageDrmInfo clone = (PackageDrmInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}