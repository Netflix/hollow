package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CdnDeployment")
public class CdnDeployment implements Cloneable {

    public long originServerId = java.lang.Long.MIN_VALUE;
    public String directory = null;
    public String originServer = null;

    public CdnDeployment setOriginServerId(long originServerId) {
        this.originServerId = originServerId;
        return this;
    }
    public CdnDeployment setDirectory(String directory) {
        this.directory = directory;
        return this;
    }
    public CdnDeployment setOriginServer(String originServer) {
        this.originServer = originServer;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CdnDeployment))
            return false;

        CdnDeployment o = (CdnDeployment) other;
        if(o.originServerId != originServerId) return false;
        if(o.directory == null) {
            if(directory != null) return false;
        } else if(!o.directory.equals(directory)) return false;
        if(o.originServer == null) {
            if(originServer != null) return false;
        } else if(!o.originServer.equals(originServer)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (originServerId ^ (originServerId >>> 32));
        hashCode = hashCode * 31 + (directory == null ? 1237 : directory.hashCode());
        hashCode = hashCode * 31 + (originServer == null ? 1237 : originServer.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CdnDeployment{");
        builder.append("originServerId=").append(originServerId);
        builder.append(",directory=").append(directory);
        builder.append(",originServer=").append(originServer);
        builder.append("}");
        return builder.toString();
    }

    public CdnDeployment clone() {
        try {
            CdnDeployment clone = (CdnDeployment)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}