package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="IPLDerivativeGroup")
public class IPLDerivativeGroup implements Cloneable {

    public String externalId = null;
    public int submission = java.lang.Integer.MIN_VALUE;
    public String imageType = null;
    @HollowTypeName(name="IPLDerivativeSet")
    public Set<IPLArtworkDerivative> derivatives = null;

    public IPLDerivativeGroup setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }
    public IPLDerivativeGroup setSubmission(int submission) {
        this.submission = submission;
        return this;
    }
    public IPLDerivativeGroup setImageType(String imageType) {
        this.imageType = imageType;
        return this;
    }
    public IPLDerivativeGroup setDerivatives(Set<IPLArtworkDerivative> derivatives) {
        this.derivatives = derivatives;
        return this;
    }
    public IPLDerivativeGroup addToDerivatives(IPLArtworkDerivative iPLArtworkDerivative) {
        if (this.derivatives == null) {
            this.derivatives = new HashSet<IPLArtworkDerivative>();
        }
        this.derivatives.add(iPLArtworkDerivative);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof IPLDerivativeGroup))
            return false;

        IPLDerivativeGroup o = (IPLDerivativeGroup) other;
        if(o.externalId == null) {
            if(externalId != null) return false;
        } else if(!o.externalId.equals(externalId)) return false;
        if(o.submission != submission) return false;
        if(o.imageType == null) {
            if(imageType != null) return false;
        } else if(!o.imageType.equals(imageType)) return false;
        if(o.derivatives == null) {
            if(derivatives != null) return false;
        } else if(!o.derivatives.equals(derivatives)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (externalId == null ? 1237 : externalId.hashCode());
        hashCode = hashCode * 31 + submission;
        hashCode = hashCode * 31 + (imageType == null ? 1237 : imageType.hashCode());
        hashCode = hashCode * 31 + (derivatives == null ? 1237 : derivatives.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("IPLDerivativeGroup{");
        builder.append("externalId=").append(externalId);
        builder.append(",submission=").append(submission);
        builder.append(",imageType=").append(imageType);
        builder.append(",derivatives=").append(derivatives);
        builder.append("}");
        return builder.toString();
    }

    public IPLDerivativeGroup clone() {
        try {
            IPLDerivativeGroup clone = (IPLDerivativeGroup)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}