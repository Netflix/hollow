package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="MovieRatings")
public class MovieRatings implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public String media = null;
    public long certificationTypeId = java.lang.Long.MIN_VALUE;
    public TranslatedText ratingReason = null;

    public MovieRatings setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public MovieRatings setMedia(String media) {
        this.media = media;
        return this;
    }
    public MovieRatings setCertificationTypeId(long certificationTypeId) {
        this.certificationTypeId = certificationTypeId;
        return this;
    }
    public MovieRatings setRatingReason(TranslatedText ratingReason) {
        this.ratingReason = ratingReason;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof MovieRatings))
            return false;

        MovieRatings o = (MovieRatings) other;
        if(o.movieId != movieId) return false;
        if(o.media == null) {
            if(media != null) return false;
        } else if(!o.media.equals(media)) return false;
        if(o.certificationTypeId != certificationTypeId) return false;
        if(o.ratingReason == null) {
            if(ratingReason != null) return false;
        } else if(!o.ratingReason.equals(ratingReason)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (media == null ? 1237 : media.hashCode());
        hashCode = hashCode * 31 + (int) (certificationTypeId ^ (certificationTypeId >>> 32));
        hashCode = hashCode * 31 + (ratingReason == null ? 1237 : ratingReason.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("MovieRatings{");
        builder.append("movieId=").append(movieId);
        builder.append(",media=").append(media);
        builder.append(",certificationTypeId=").append(certificationTypeId);
        builder.append(",ratingReason=").append(ratingReason);
        builder.append("}");
        return builder.toString();
    }

    public MovieRatings clone() {
        try {
            MovieRatings clone = (MovieRatings)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}