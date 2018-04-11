package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="OverrideSchedule")
public class OverrideSchedule implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public String phaseTag = null;
    public long availabilityOffset = java.lang.Long.MIN_VALUE;

    public OverrideSchedule setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public OverrideSchedule setPhaseTag(String phaseTag) {
        this.phaseTag = phaseTag;
        return this;
    }
    public OverrideSchedule setAvailabilityOffset(long availabilityOffset) {
        this.availabilityOffset = availabilityOffset;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof OverrideSchedule))
            return false;

        OverrideSchedule o = (OverrideSchedule) other;
        if(o.movieId != movieId) return false;
        if(o.phaseTag == null) {
            if(phaseTag != null) return false;
        } else if(!o.phaseTag.equals(phaseTag)) return false;
        if(o.availabilityOffset != availabilityOffset) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (phaseTag == null ? 1237 : phaseTag.hashCode());
        hashCode = hashCode * 31 + (int) (availabilityOffset ^ (availabilityOffset >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("OverrideSchedule{");
        builder.append("movieId=").append(movieId);
        builder.append(",phaseTag=").append(phaseTag);
        builder.append(",availabilityOffset=").append(availabilityOffset);
        builder.append("}");
        return builder.toString();
    }

    public OverrideSchedule clone() {
        try {
            OverrideSchedule clone = (OverrideSchedule)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}