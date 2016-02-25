package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class MovieRatingReason implements Cloneable {

    public List<Integer> reasonIds = null;
    public boolean isDisplayImageOnly = false;
    public boolean isDisplayOrderSpecific = false;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof MovieRatingReason))
            return false;

        MovieRatingReason o = (MovieRatingReason) other;
        if(o.reasonIds == null) {
            if(reasonIds != null) return false;
        } else if(!o.reasonIds.equals(reasonIds)) return false;
        if(o.isDisplayImageOnly != isDisplayImageOnly) return false;
        if(o.isDisplayOrderSpecific != isDisplayOrderSpecific) return false;
        return true;
    }

    public MovieRatingReason clone() {
        try {
            MovieRatingReason clone = (MovieRatingReason)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}