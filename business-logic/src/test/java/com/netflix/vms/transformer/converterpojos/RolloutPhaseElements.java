package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="RolloutPhaseElements")
public class RolloutPhaseElements implements Cloneable {

    public RolloutPhaseLocalizedMetadata localized_metadata = null;
    public RolloutPhaseArtwork artwork = null;

    public RolloutPhaseElements setLocalized_metadata(RolloutPhaseLocalizedMetadata localized_metadata) {
        this.localized_metadata = localized_metadata;
        return this;
    }
    public RolloutPhaseElements setArtwork(RolloutPhaseArtwork artwork) {
        this.artwork = artwork;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutPhaseElements))
            return false;

        RolloutPhaseElements o = (RolloutPhaseElements) other;
        if(o.localized_metadata == null) {
            if(localized_metadata != null) return false;
        } else if(!o.localized_metadata.equals(localized_metadata)) return false;
        if(o.artwork == null) {
            if(artwork != null) return false;
        } else if(!o.artwork.equals(artwork)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (localized_metadata == null ? 1237 : localized_metadata.hashCode());
        hashCode = hashCode * 31 + (artwork == null ? 1237 : artwork.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutPhaseElements{");
        builder.append("localized_metadata=").append(localized_metadata);
        builder.append(",artwork=").append(artwork);
        builder.append("}");
        return builder.toString();
    }

    public RolloutPhaseElements clone() {
        try {
            RolloutPhaseElements clone = (RolloutPhaseElements)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}