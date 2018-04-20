package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="RolloutPhaseArtwork")
public class RolloutPhaseArtwork implements Cloneable {

    @HollowTypeName(name="RolloutPhaseArtworkSourceFileIdList")
    public List<RolloutPhaseArtworkSourceFileId> sourceFileIds = null;

    public RolloutPhaseArtwork() { }

    public RolloutPhaseArtwork(List<RolloutPhaseArtworkSourceFileId> value) {
        this.sourceFileIds = value;
    }

    public RolloutPhaseArtwork setSourceFileIds(List<RolloutPhaseArtworkSourceFileId> sourceFileIds) {
        this.sourceFileIds = sourceFileIds;
        return this;
    }
    public RolloutPhaseArtwork addToSourceFileIds(RolloutPhaseArtworkSourceFileId rolloutPhaseArtworkSourceFileId) {
        if (this.sourceFileIds == null) {
            this.sourceFileIds = new ArrayList<RolloutPhaseArtworkSourceFileId>();
        }
        this.sourceFileIds.add(rolloutPhaseArtworkSourceFileId);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutPhaseArtwork))
            return false;

        RolloutPhaseArtwork o = (RolloutPhaseArtwork) other;
        if(o.sourceFileIds == null) {
            if(sourceFileIds != null) return false;
        } else if(!o.sourceFileIds.equals(sourceFileIds)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (sourceFileIds == null ? 1237 : sourceFileIds.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutPhaseArtwork{");
        builder.append("sourceFileIds=").append(sourceFileIds);
        builder.append("}");
        return builder.toString();
    }

    public RolloutPhaseArtwork clone() {
        try {
            RolloutPhaseArtwork clone = (RolloutPhaseArtwork)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}