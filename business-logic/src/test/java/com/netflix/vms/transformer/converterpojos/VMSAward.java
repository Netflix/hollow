package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VMSAward")
public class VMSAward implements Cloneable {

    public long awardId = java.lang.Long.MIN_VALUE;
    public long sequenceNumber = java.lang.Long.MIN_VALUE;
    public long festivalId = java.lang.Long.MIN_VALUE;
    public String countryCode = null;
    public boolean isMovieAward = false;

    public VMSAward setAwardId(long awardId) {
        this.awardId = awardId;
        return this;
    }
    public VMSAward setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    public VMSAward setFestivalId(long festivalId) {
        this.festivalId = festivalId;
        return this;
    }
    public VMSAward setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public VMSAward setIsMovieAward(boolean isMovieAward) {
        this.isMovieAward = isMovieAward;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VMSAward))
            return false;

        VMSAward o = (VMSAward) other;
        if(o.awardId != awardId) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.festivalId != festivalId) return false;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.isMovieAward != isMovieAward) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (awardId ^ (awardId >>> 32));
        hashCode = hashCode * 31 + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
        hashCode = hashCode * 31 + (int) (festivalId ^ (festivalId >>> 32));
        hashCode = hashCode * 31 + (countryCode == null ? 1237 : countryCode.hashCode());
        hashCode = hashCode * 31 + (isMovieAward? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VMSAward{");
        builder.append("awardId=").append(awardId);
        builder.append(",sequenceNumber=").append(sequenceNumber);
        builder.append(",festivalId=").append(festivalId);
        builder.append(",countryCode=").append(countryCode);
        builder.append(",isMovieAward=").append(isMovieAward);
        builder.append("}");
        return builder.toString();
    }

    public VMSAward clone() {
        try {
            VMSAward clone = (VMSAward)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}