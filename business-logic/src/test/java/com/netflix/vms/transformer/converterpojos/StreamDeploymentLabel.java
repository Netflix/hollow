package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamDeploymentLabel")
public class StreamDeploymentLabel implements Cloneable {

    public String value = null;

    public StreamDeploymentLabel() { }

    public StreamDeploymentLabel(String value) {
        this.value = value;
    }

    public StreamDeploymentLabel setValue(String value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDeploymentLabel))
            return false;

        StreamDeploymentLabel o = (StreamDeploymentLabel) other;
        if(o.value == null) {
            if(value != null) return false;
        } else if(!o.value.equals(value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (value == null ? 1237 : value.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamDeploymentLabel{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public StreamDeploymentLabel clone() {
        try {
            StreamDeploymentLabel clone = (StreamDeploymentLabel)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}