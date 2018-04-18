package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Episode")
public class Episode implements Cloneable {

    public long sequenceNumber = java.lang.Long.MIN_VALUE;
    public long movieId = java.lang.Long.MIN_VALUE;
    public boolean midSeason = false;
    public boolean seasonFinale = false;
    public boolean showFinale = false;

    public Episode setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    public Episode setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Episode setMidSeason(boolean midSeason) {
        this.midSeason = midSeason;
        return this;
    }
    public Episode setSeasonFinale(boolean seasonFinale) {
        this.seasonFinale = seasonFinale;
        return this;
    }
    public Episode setShowFinale(boolean showFinale) {
        this.showFinale = showFinale;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Episode))
            return false;

        Episode o = (Episode) other;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.movieId != movieId) return false;
        if(o.midSeason != midSeason) return false;
        if(o.seasonFinale != seasonFinale) return false;
        if(o.showFinale != showFinale) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (midSeason? 1231 : 1237);
        hashCode = hashCode * 31 + (seasonFinale? 1231 : 1237);
        hashCode = hashCode * 31 + (showFinale? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Episode{");
        builder.append("sequenceNumber=").append(sequenceNumber);
        builder.append(",movieId=").append(movieId);
        builder.append(",midSeason=").append(midSeason);
        builder.append(",seasonFinale=").append(seasonFinale);
        builder.append(",showFinale=").append(showFinale);
        builder.append("}");
        return builder.toString();
    }

    public Episode clone() {
        try {
            Episode clone = (Episode)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}