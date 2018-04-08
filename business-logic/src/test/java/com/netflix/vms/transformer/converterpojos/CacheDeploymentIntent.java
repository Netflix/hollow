package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CacheDeploymentIntent")
public class CacheDeploymentIntent implements Cloneable {

    public long streamProfileId = java.lang.Long.MIN_VALUE;
    public String isoCountryCode = null;
    public long bitrateKBPS = java.lang.Long.MIN_VALUE;

    public CacheDeploymentIntent setStreamProfileId(long streamProfileId) {
        this.streamProfileId = streamProfileId;
        return this;
    }
    public CacheDeploymentIntent setIsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
        return this;
    }
    public CacheDeploymentIntent setBitrateKBPS(long bitrateKBPS) {
        this.bitrateKBPS = bitrateKBPS;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CacheDeploymentIntent))
            return false;

        CacheDeploymentIntent o = (CacheDeploymentIntent) other;
        if(o.streamProfileId != streamProfileId) return false;
        if(o.isoCountryCode == null) {
            if(isoCountryCode != null) return false;
        } else if(!o.isoCountryCode.equals(isoCountryCode)) return false;
        if(o.bitrateKBPS != bitrateKBPS) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (streamProfileId ^ (streamProfileId >>> 32));
        hashCode = hashCode * 31 + (isoCountryCode == null ? 1237 : isoCountryCode.hashCode());
        hashCode = hashCode * 31 + (int) (bitrateKBPS ^ (bitrateKBPS >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CacheDeploymentIntent{");
        builder.append("streamProfileId=").append(streamProfileId);
        builder.append(",isoCountryCode=").append(isoCountryCode);
        builder.append(",bitrateKBPS=").append(bitrateKBPS);
        builder.append("}");
        return builder.toString();
    }

    public CacheDeploymentIntent clone() {
        try {
            CacheDeploymentIntent clone = (CacheDeploymentIntent)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}