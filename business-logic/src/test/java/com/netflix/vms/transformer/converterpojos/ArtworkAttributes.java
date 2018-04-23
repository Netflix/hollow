package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ArtworkAttributes")
public class ArtworkAttributes implements Cloneable {

    public PassthroughData passthrough = null;

    public ArtworkAttributes() { }

    public ArtworkAttributes(PassthroughData value) {
        this.passthrough = value;
    }

    public ArtworkAttributes setPassthrough(PassthroughData passthrough) {
        this.passthrough = passthrough;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkAttributes))
            return false;

        ArtworkAttributes o = (ArtworkAttributes) other;
        if(o.passthrough == null) {
            if(passthrough != null) return false;
        } else if(!o.passthrough.equals(passthrough)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (passthrough == null ? 1237 : passthrough.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkAttributes{");
        builder.append("passthrough=").append(passthrough);
        builder.append("}");
        return builder.toString();
    }

    public ArtworkAttributes clone() {
        try {
            ArtworkAttributes clone = (ArtworkAttributes)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}