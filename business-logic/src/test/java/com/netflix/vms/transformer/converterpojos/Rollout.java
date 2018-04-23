package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="Rollout")
public class Rollout implements Cloneable {

    public long rolloutId = java.lang.Long.MIN_VALUE;
    public long movieId = java.lang.Long.MIN_VALUE;
    public String rolloutName = null;
    public String rolloutType = null;
    @HollowTypeName(name="RolloutPhaseList")
    public List<RolloutPhase> phases = null;

    public Rollout setRolloutId(long rolloutId) {
        this.rolloutId = rolloutId;
        return this;
    }
    public Rollout setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Rollout setRolloutName(String rolloutName) {
        this.rolloutName = rolloutName;
        return this;
    }
    public Rollout setRolloutType(String rolloutType) {
        this.rolloutType = rolloutType;
        return this;
    }
    public Rollout setPhases(List<RolloutPhase> phases) {
        this.phases = phases;
        return this;
    }
    public Rollout addToPhases(RolloutPhase rolloutPhase) {
        if (this.phases == null) {
            this.phases = new ArrayList<RolloutPhase>();
        }
        this.phases.add(rolloutPhase);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Rollout))
            return false;

        Rollout o = (Rollout) other;
        if(o.rolloutId != rolloutId) return false;
        if(o.movieId != movieId) return false;
        if(o.rolloutName == null) {
            if(rolloutName != null) return false;
        } else if(!o.rolloutName.equals(rolloutName)) return false;
        if(o.rolloutType == null) {
            if(rolloutType != null) return false;
        } else if(!o.rolloutType.equals(rolloutType)) return false;
        if(o.phases == null) {
            if(phases != null) return false;
        } else if(!o.phases.equals(phases)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (rolloutId ^ (rolloutId >>> 32));
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (rolloutName == null ? 1237 : rolloutName.hashCode());
        hashCode = hashCode * 31 + (rolloutType == null ? 1237 : rolloutType.hashCode());
        hashCode = hashCode * 31 + (phases == null ? 1237 : phases.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Rollout{");
        builder.append("rolloutId=").append(rolloutId);
        builder.append(",movieId=").append(movieId);
        builder.append(",rolloutName=").append(rolloutName);
        builder.append(",rolloutType=").append(rolloutType);
        builder.append(",phases=").append(phases);
        builder.append("}");
        return builder.toString();
    }

    public Rollout clone() {
        try {
            Rollout clone = (Rollout)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}