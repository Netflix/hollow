package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="Contracts")
public class Contracts implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public String countryCode = null;
    public List<Contract> contracts = null;

    public Contracts setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Contracts setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public Contracts setContracts(List<Contract> contracts) {
        this.contracts = contracts;
        return this;
    }
    public Contracts addToContracts(Contract contract) {
        if (this.contracts == null) {
            this.contracts = new ArrayList<Contract>();
        }
        this.contracts.add(contract);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Contracts))
            return false;

        Contracts o = (Contracts) other;
        if(o.movieId != movieId) return false;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.contracts == null) {
            if(contracts != null) return false;
        } else if(!o.contracts.equals(contracts)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (countryCode == null ? 1237 : countryCode.hashCode());
        hashCode = hashCode * 31 + (contracts == null ? 1237 : contracts.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Contracts{");
        builder.append("movieId=").append(movieId);
        builder.append(",countryCode=").append(countryCode);
        builder.append(",contracts=").append(contracts);
        builder.append("}");
        return builder.toString();
    }

    public Contracts clone() {
        try {
            Contracts clone = (Contracts)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}