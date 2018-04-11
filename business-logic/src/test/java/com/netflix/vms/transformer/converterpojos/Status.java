package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="Status")
public class Status implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public String countryCode = null;
    public Rights rights = null;
    public Flags flags = null;

    public Status setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Status setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public Status setRights(Rights rights) {
        this.rights = rights;
        return this;
    }
    public Status setFlags(Flags flags) {
        this.flags = flags;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Status))
            return false;

        Status o = (Status) other;
        if(o.movieId != movieId) return false;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.rights == null) {
            if(rights != null) return false;
        } else if(!o.rights.equals(rights)) return false;
        if(o.flags == null) {
            if(flags != null) return false;
        } else if(!o.flags.equals(flags)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (countryCode == null ? 1237 : countryCode.hashCode());
        hashCode = hashCode * 31 + (rights == null ? 1237 : rights.hashCode());
        hashCode = hashCode * 31 + (flags == null ? 1237 : flags.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Status{");
        builder.append("movieId=").append(movieId);
        builder.append(",countryCode=").append(countryCode);
        builder.append(",rights=").append(rights);
        builder.append(",flags=").append(flags);
        builder.append("}");
        return builder.toString();
    }

    public Status clone() {
        try {
            Status clone = (Status)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}