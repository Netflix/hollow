package com.netflix.vms.hollowoutput.pojos;


public class MovieCertification {

    public Video videoId;
    public int certificationSystemId;
    public int ratingId;
    public int maturityLevel;
    public MovieRatingReason ratingReason;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof MovieCertification))
            return false;

        MovieCertification o = (MovieCertification) other;
        if(o.videoId == null) {
            if(videoId != null) return false;
        } else if(!o.videoId.equals(videoId)) return false;
        if(o.certificationSystemId != certificationSystemId) return false;
        if(o.ratingId != ratingId) return false;
        if(o.maturityLevel != maturityLevel) return false;
        if(o.ratingReason == null) {
            if(ratingReason != null) return false;
        } else if(!o.ratingReason.equals(ratingReason)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}