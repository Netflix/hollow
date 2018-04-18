package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="PhaseTag")
public class PhaseTag implements Cloneable {

    public String phaseTag = null;
    public String scheduleId = null;

    public PhaseTag setPhaseTag(String phaseTag) {
        this.phaseTag = phaseTag;
        return this;
    }
    public PhaseTag setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PhaseTag))
            return false;

        PhaseTag o = (PhaseTag) other;
        if(o.phaseTag == null) {
            if(phaseTag != null) return false;
        } else if(!o.phaseTag.equals(phaseTag)) return false;
        if(o.scheduleId == null) {
            if(scheduleId != null) return false;
        } else if(!o.scheduleId.equals(scheduleId)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (phaseTag == null ? 1237 : phaseTag.hashCode());
        hashCode = hashCode * 31 + (scheduleId == null ? 1237 : scheduleId.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PhaseTag{");
        builder.append("phaseTag=").append(phaseTag);
        builder.append(",scheduleId=").append(scheduleId);
        builder.append("}");
        return builder.toString();
    }

    public PhaseTag clone() {
        try {
            PhaseTag clone = (PhaseTag)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}