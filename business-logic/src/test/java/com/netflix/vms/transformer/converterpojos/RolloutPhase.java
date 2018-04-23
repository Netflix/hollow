package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Map;


@SuppressWarnings("all")
@HollowTypeName(name="RolloutPhase")
public class RolloutPhase implements Cloneable {

    public long seasonMovieId = java.lang.Long.MIN_VALUE;
    public RolloutPhaseElements elements = null;
    public String name = null;
    public boolean showCoreMetadata = false;
    @HollowTypeName(name="RolloutPhaseWindowMap")
    public Map<ISOCountry, RolloutPhaseWindow> windows = null;
    public String phaseType = null;
    public boolean onHold = false;

    public RolloutPhase setSeasonMovieId(long seasonMovieId) {
        this.seasonMovieId = seasonMovieId;
        return this;
    }
    public RolloutPhase setElements(RolloutPhaseElements elements) {
        this.elements = elements;
        return this;
    }
    public RolloutPhase setName(String name) {
        this.name = name;
        return this;
    }
    public RolloutPhase setShowCoreMetadata(boolean showCoreMetadata) {
        this.showCoreMetadata = showCoreMetadata;
        return this;
    }
    public RolloutPhase setWindows(Map<ISOCountry, RolloutPhaseWindow> windows) {
        this.windows = windows;
        return this;
    }
    public RolloutPhase setPhaseType(String phaseType) {
        this.phaseType = phaseType;
        return this;
    }
    public RolloutPhase setOnHold(boolean onHold) {
        this.onHold = onHold;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutPhase))
            return false;

        RolloutPhase o = (RolloutPhase) other;
        if(o.seasonMovieId != seasonMovieId) return false;
        if(o.elements == null) {
            if(elements != null) return false;
        } else if(!o.elements.equals(elements)) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.showCoreMetadata != showCoreMetadata) return false;
        if(o.windows == null) {
            if(windows != null) return false;
        } else if(!o.windows.equals(windows)) return false;
        if(o.phaseType == null) {
            if(phaseType != null) return false;
        } else if(!o.phaseType.equals(phaseType)) return false;
        if(o.onHold != onHold) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (seasonMovieId ^ (seasonMovieId >>> 32));
        hashCode = hashCode * 31 + (elements == null ? 1237 : elements.hashCode());
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (showCoreMetadata? 1231 : 1237);
        hashCode = hashCode * 31 + (windows == null ? 1237 : windows.hashCode());
        hashCode = hashCode * 31 + (phaseType == null ? 1237 : phaseType.hashCode());
        hashCode = hashCode * 31 + (onHold? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutPhase{");
        builder.append("seasonMovieId=").append(seasonMovieId);
        builder.append(",elements=").append(elements);
        builder.append(",name=").append(name);
        builder.append(",showCoreMetadata=").append(showCoreMetadata);
        builder.append(",windows=").append(windows);
        builder.append(",phaseType=").append(phaseType);
        builder.append(",onHold=").append(onHold);
        builder.append("}");
        return builder.toString();
    }

    public RolloutPhase clone() {
        try {
            RolloutPhase clone = (RolloutPhase)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}