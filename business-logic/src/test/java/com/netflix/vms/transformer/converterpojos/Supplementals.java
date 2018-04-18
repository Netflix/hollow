package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="Supplementals")
public class Supplementals implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="SupplementalsList")
    public List<IndividualSupplemental> supplementals = null;

    public Supplementals setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Supplementals setSupplementals(List<IndividualSupplemental> supplementals) {
        this.supplementals = supplementals;
        return this;
    }
    public Supplementals addToSupplementals(IndividualSupplemental individualSupplemental) {
        if (this.supplementals == null) {
            this.supplementals = new ArrayList<IndividualSupplemental>();
        }
        this.supplementals.add(individualSupplemental);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Supplementals))
            return false;

        Supplementals o = (Supplementals) other;
        if(o.movieId != movieId) return false;
        if(o.supplementals == null) {
            if(supplementals != null) return false;
        } else if(!o.supplementals.equals(supplementals)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (supplementals == null ? 1237 : supplementals.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Supplementals{");
        builder.append("movieId=").append(movieId);
        builder.append(",supplementals=").append(supplementals);
        builder.append("}");
        return builder.toString();
    }

    public Supplementals clone() {
        try {
            Supplementals clone = (Supplementals)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}