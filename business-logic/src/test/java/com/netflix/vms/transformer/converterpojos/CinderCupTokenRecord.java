package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CinderCupTokenRecord")
public class CinderCupTokenRecord implements Cloneable {

    public Long movieId = null;
    public Long contractId = null;
    public String cupTokenId = null;

    public CinderCupTokenRecord setMovieId(Long movieId) {
        this.movieId = movieId;
        return this;
    }
    public CinderCupTokenRecord setContractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }
    public CinderCupTokenRecord setCupTokenId(String cupTokenId) {
        this.cupTokenId = cupTokenId;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CinderCupTokenRecord))
            return false;

        CinderCupTokenRecord o = (CinderCupTokenRecord) other;
        if(o.movieId == null) {
            if(movieId != null) return false;
        } else if(!o.movieId.equals(movieId)) return false;
        if(o.contractId == null) {
            if(contractId != null) return false;
        } else if(!o.contractId.equals(contractId)) return false;
        if(o.cupTokenId == null) {
            if(cupTokenId != null) return false;
        } else if(!o.cupTokenId.equals(cupTokenId)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (movieId == null ? 1237 : movieId.hashCode());
        hashCode = hashCode * 31 + (contractId == null ? 1237 : contractId.hashCode());
        hashCode = hashCode * 31 + (cupTokenId == null ? 1237 : cupTokenId.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CinderCupTokenRecord{");
        builder.append("movieId=").append(movieId);
        builder.append(",contractId=").append(contractId);
        builder.append(",cupTokenId=").append(cupTokenId);
        builder.append("}");
        return builder.toString();
    }

    public CinderCupTokenRecord clone() {
        try {
            CinderCupTokenRecord clone = (CinderCupTokenRecord)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}