package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="AbsoluteSchedule")
public class AbsoluteSchedule implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public String phaseTag = null;
    public long startDate = java.lang.Long.MIN_VALUE;
    public long endDate = java.lang.Long.MIN_VALUE;

    public AbsoluteSchedule setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public AbsoluteSchedule setPhaseTag(String phaseTag) {
        this.phaseTag = phaseTag;
        return this;
    }
    public AbsoluteSchedule setStartDate(long startDate) {
        this.startDate = startDate;
        return this;
    }
    public AbsoluteSchedule setEndDate(long endDate) {
        this.endDate = endDate;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AbsoluteSchedule))
            return false;

        AbsoluteSchedule o = (AbsoluteSchedule) other;
        if(o.movieId != movieId) return false;
        if(o.phaseTag == null) {
            if(phaseTag != null) return false;
        } else if(!o.phaseTag.equals(phaseTag)) return false;
        if(o.startDate != startDate) return false;
        if(o.endDate != endDate) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (phaseTag == null ? 1237 : phaseTag.hashCode());
        hashCode = hashCode * 31 + (int) (startDate ^ (startDate >>> 32));
        hashCode = hashCode * 31 + (int) (endDate ^ (endDate >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AbsoluteSchedule{");
        builder.append("movieId=").append(movieId);
        builder.append(",phaseTag=").append(phaseTag);
        builder.append(",startDate=").append(startDate);
        builder.append(",endDate=").append(endDate);
        builder.append("}");
        return builder.toString();
    }

    public AbsoluteSchedule clone() {
        try {
            AbsoluteSchedule clone = (AbsoluteSchedule)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}