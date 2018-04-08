package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="MasterSchedule")
public class MasterSchedule implements Cloneable {

    public String movieType = null;
    public long versionId = java.lang.Long.MIN_VALUE;
    public String scheduleId = null;
    public String phaseTag = null;
    public long availabilityOffset = java.lang.Long.MIN_VALUE;

    public MasterSchedule setMovieType(String movieType) {
        this.movieType = movieType;
        return this;
    }
    public MasterSchedule setVersionId(long versionId) {
        this.versionId = versionId;
        return this;
    }
    public MasterSchedule setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
        return this;
    }
    public MasterSchedule setPhaseTag(String phaseTag) {
        this.phaseTag = phaseTag;
        return this;
    }
    public MasterSchedule setAvailabilityOffset(long availabilityOffset) {
        this.availabilityOffset = availabilityOffset;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof MasterSchedule))
            return false;

        MasterSchedule o = (MasterSchedule) other;
        if(o.movieType == null) {
            if(movieType != null) return false;
        } else if(!o.movieType.equals(movieType)) return false;
        if(o.versionId != versionId) return false;
        if(o.scheduleId == null) {
            if(scheduleId != null) return false;
        } else if(!o.scheduleId.equals(scheduleId)) return false;
        if(o.phaseTag == null) {
            if(phaseTag != null) return false;
        } else if(!o.phaseTag.equals(phaseTag)) return false;
        if(o.availabilityOffset != availabilityOffset) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (movieType == null ? 1237 : movieType.hashCode());
        hashCode = hashCode * 31 + (int) (versionId ^ (versionId >>> 32));
        hashCode = hashCode * 31 + (scheduleId == null ? 1237 : scheduleId.hashCode());
        hashCode = hashCode * 31 + (phaseTag == null ? 1237 : phaseTag.hashCode());
        hashCode = hashCode * 31 + (int) (availabilityOffset ^ (availabilityOffset >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("MasterSchedule{");
        builder.append("movieType=").append(movieType);
        builder.append(",versionId=").append(versionId);
        builder.append(",scheduleId=").append(scheduleId);
        builder.append(",phaseTag=").append(phaseTag);
        builder.append(",availabilityOffset=").append(availabilityOffset);
        builder.append("}");
        return builder.toString();
    }

    public MasterSchedule clone() {
        try {
            MasterSchedule clone = (MasterSchedule)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}