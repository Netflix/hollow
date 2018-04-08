package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="PersonVideoRole")
public class PersonVideoRole implements Cloneable {

    public int sequenceNumber = java.lang.Integer.MIN_VALUE;
    public int roleTypeId = java.lang.Integer.MIN_VALUE;
    public long videoId = java.lang.Long.MIN_VALUE;

    public PersonVideoRole setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    public PersonVideoRole setRoleTypeId(int roleTypeId) {
        this.roleTypeId = roleTypeId;
        return this;
    }
    public PersonVideoRole setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonVideoRole))
            return false;

        PersonVideoRole o = (PersonVideoRole) other;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.roleTypeId != roleTypeId) return false;
        if(o.videoId != videoId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + sequenceNumber;
        hashCode = hashCode * 31 + roleTypeId;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonVideoRole{");
        builder.append("sequenceNumber=").append(sequenceNumber);
        builder.append(",roleTypeId=").append(roleTypeId);
        builder.append(",videoId=").append(videoId);
        builder.append("}");
        return builder.toString();
    }

    public PersonVideoRole clone() {
        try {
            PersonVideoRole clone = (PersonVideoRole)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}