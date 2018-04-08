package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="IndividualSupplemental")
public class IndividualSupplemental implements Cloneable {

    public String identifier = null;
    public long movieId = java.lang.Long.MIN_VALUE;
    public long sequenceNumber = java.lang.Long.MIN_VALUE;
    public PassthroughData passthrough = null;

    public IndividualSupplemental setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }
    public IndividualSupplemental setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public IndividualSupplemental setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    public IndividualSupplemental setPassthrough(PassthroughData passthrough) {
        this.passthrough = passthrough;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof IndividualSupplemental))
            return false;

        IndividualSupplemental o = (IndividualSupplemental) other;
        if(o.identifier == null) {
            if(identifier != null) return false;
        } else if(!o.identifier.equals(identifier)) return false;
        if(o.movieId != movieId) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.passthrough == null) {
            if(passthrough != null) return false;
        } else if(!o.passthrough.equals(passthrough)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (identifier == null ? 1237 : identifier.hashCode());
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
        hashCode = hashCode * 31 + (passthrough == null ? 1237 : passthrough.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("IndividualSupplemental{");
        builder.append("identifier=").append(identifier);
        builder.append(",movieId=").append(movieId);
        builder.append(",sequenceNumber=").append(sequenceNumber);
        builder.append(",passthrough=").append(passthrough);
        builder.append("}");
        return builder.toString();
    }

    public IndividualSupplemental clone() {
        try {
            IndividualSupplemental clone = (IndividualSupplemental)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}