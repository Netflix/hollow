package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="IPLArtworkDerivativeSet")
public class IPLArtworkDerivativeSet implements Cloneable {

    public String derivativeSetId = null;
    @HollowTypeName(name="IPLDerivativeGroupSet")
    public Set<IPLDerivativeGroup> derivativesGroupBySource = null;

    public IPLArtworkDerivativeSet setDerivativeSetId(String derivativeSetId) {
        this.derivativeSetId = derivativeSetId;
        return this;
    }
    public IPLArtworkDerivativeSet setDerivativesGroupBySource(Set<IPLDerivativeGroup> derivativesGroupBySource) {
        this.derivativesGroupBySource = derivativesGroupBySource;
        return this;
    }
    public IPLArtworkDerivativeSet addToDerivativesGroupBySource(IPLDerivativeGroup iPLDerivativeGroup) {
        if (this.derivativesGroupBySource == null) {
            this.derivativesGroupBySource = new HashSet<IPLDerivativeGroup>();
        }
        this.derivativesGroupBySource.add(iPLDerivativeGroup);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof IPLArtworkDerivativeSet))
            return false;

        IPLArtworkDerivativeSet o = (IPLArtworkDerivativeSet) other;
        if(o.derivativeSetId == null) {
            if(derivativeSetId != null) return false;
        } else if(!o.derivativeSetId.equals(derivativeSetId)) return false;
        if(o.derivativesGroupBySource == null) {
            if(derivativesGroupBySource != null) return false;
        } else if(!o.derivativesGroupBySource.equals(derivativesGroupBySource)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (derivativeSetId == null ? 1237 : derivativeSetId.hashCode());
        hashCode = hashCode * 31 + (derivativesGroupBySource == null ? 1237 : derivativesGroupBySource.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("IPLArtworkDerivativeSet{");
        builder.append("derivativeSetId=").append(derivativeSetId);
        builder.append(",derivativesGroupBySource=").append(derivativesGroupBySource);
        builder.append("}");
        return builder.toString();
    }

    public IPLArtworkDerivativeSet clone() {
        try {
            IPLArtworkDerivativeSet clone = (IPLArtworkDerivativeSet)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}