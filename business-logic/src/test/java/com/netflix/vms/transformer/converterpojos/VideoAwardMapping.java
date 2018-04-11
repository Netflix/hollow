package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VideoAwardMapping")
public class VideoAwardMapping implements Cloneable {

    public long awardId = java.lang.Long.MIN_VALUE;
    public long sequenceNumber = java.lang.Long.MIN_VALUE;
    public long year = java.lang.Long.MIN_VALUE;
    public long personId = java.lang.Long.MIN_VALUE;
    public boolean winner = false;

    public VideoAwardMapping setAwardId(long awardId) {
        this.awardId = awardId;
        return this;
    }
    public VideoAwardMapping setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    public VideoAwardMapping setYear(long year) {
        this.year = year;
        return this;
    }
    public VideoAwardMapping setPersonId(long personId) {
        this.personId = personId;
        return this;
    }
    public VideoAwardMapping setWinner(boolean winner) {
        this.winner = winner;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoAwardMapping))
            return false;

        VideoAwardMapping o = (VideoAwardMapping) other;
        if(o.awardId != awardId) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.year != year) return false;
        if(o.personId != personId) return false;
        if(o.winner != winner) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (awardId ^ (awardId >>> 32));
        hashCode = hashCode * 31 + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
        hashCode = hashCode * 31 + (int) (year ^ (year >>> 32));
        hashCode = hashCode * 31 + (int) (personId ^ (personId >>> 32));
        hashCode = hashCode * 31 + (winner? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoAwardMapping{");
        builder.append("awardId=").append(awardId);
        builder.append(",sequenceNumber=").append(sequenceNumber);
        builder.append(",year=").append(year);
        builder.append(",personId=").append(personId);
        builder.append(",winner=").append(winner);
        builder.append("}");
        return builder.toString();
    }

    public VideoAwardMapping clone() {
        try {
            VideoAwardMapping clone = (VideoAwardMapping)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}