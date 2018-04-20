package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="StreamDeployment")
public class StreamDeployment implements Cloneable {

    public StreamDeploymentInfo deploymentInfo = null;
    @HollowTypeName(name="StreamDeploymentLabelSet")
    public Set<StreamDeploymentLabel> deploymentLabel = null;
    public int deploymentPriority = java.lang.Integer.MIN_VALUE;
    public String s3PathComponent = null;
    public String s3FullPath = null;

    public StreamDeployment setDeploymentInfo(StreamDeploymentInfo deploymentInfo) {
        this.deploymentInfo = deploymentInfo;
        return this;
    }
    public StreamDeployment setDeploymentLabel(Set<StreamDeploymentLabel> deploymentLabel) {
        this.deploymentLabel = deploymentLabel;
        return this;
    }
    public StreamDeployment setDeploymentPriority(int deploymentPriority) {
        this.deploymentPriority = deploymentPriority;
        return this;
    }
    public StreamDeployment setS3PathComponent(String s3PathComponent) {
        this.s3PathComponent = s3PathComponent;
        return this;
    }
    public StreamDeployment setS3FullPath(String s3FullPath) {
        this.s3FullPath = s3FullPath;
        return this;
    }
    public StreamDeployment addToDeploymentLabel(StreamDeploymentLabel streamDeploymentLabel) {
        if (this.deploymentLabel == null) {
            this.deploymentLabel = new HashSet<StreamDeploymentLabel>();
        }
        this.deploymentLabel.add(streamDeploymentLabel);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDeployment))
            return false;

        StreamDeployment o = (StreamDeployment) other;
        if(o.deploymentInfo == null) {
            if(deploymentInfo != null) return false;
        } else if(!o.deploymentInfo.equals(deploymentInfo)) return false;
        if(o.deploymentLabel == null) {
            if(deploymentLabel != null) return false;
        } else if(!o.deploymentLabel.equals(deploymentLabel)) return false;
        if(o.deploymentPriority != deploymentPriority) return false;
        if(o.s3PathComponent == null) {
            if(s3PathComponent != null) return false;
        } else if(!o.s3PathComponent.equals(s3PathComponent)) return false;
        if(o.s3FullPath == null) {
            if(s3FullPath != null) return false;
        } else if(!o.s3FullPath.equals(s3FullPath)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (deploymentInfo == null ? 1237 : deploymentInfo.hashCode());
        hashCode = hashCode * 31 + (deploymentLabel == null ? 1237 : deploymentLabel.hashCode());
        hashCode = hashCode * 31 + deploymentPriority;
        hashCode = hashCode * 31 + (s3PathComponent == null ? 1237 : s3PathComponent.hashCode());
        hashCode = hashCode * 31 + (s3FullPath == null ? 1237 : s3FullPath.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamDeployment{");
        builder.append("deploymentInfo=").append(deploymentInfo);
        builder.append(",deploymentLabel=").append(deploymentLabel);
        builder.append(",deploymentPriority=").append(deploymentPriority);
        builder.append(",s3PathComponent=").append(s3PathComponent);
        builder.append(",s3FullPath=").append(s3FullPath);
        builder.append("}");
        return builder.toString();
    }

    public StreamDeployment clone() {
        try {
            StreamDeployment clone = (StreamDeployment)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}