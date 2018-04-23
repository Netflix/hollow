package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="FeedMovieCountryLanguages")
public class FeedMovieCountryLanguages implements Cloneable {

    public Long movieId = null;
    public String countryCode = null;
    public String languageCode = null;
    public Long earliestWindowStartDate = null;

    public FeedMovieCountryLanguages setMovieId(Long movieId) {
        this.movieId = movieId;
        return this;
    }
    public FeedMovieCountryLanguages setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public FeedMovieCountryLanguages setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }
    public FeedMovieCountryLanguages setEarliestWindowStartDate(Long earliestWindowStartDate) {
        this.earliestWindowStartDate = earliestWindowStartDate;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof FeedMovieCountryLanguages))
            return false;

        FeedMovieCountryLanguages o = (FeedMovieCountryLanguages) other;
        if(o.movieId == null) {
            if(movieId != null) return false;
        } else if(!o.movieId.equals(movieId)) return false;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.languageCode == null) {
            if(languageCode != null) return false;
        } else if(!o.languageCode.equals(languageCode)) return false;
        if(o.earliestWindowStartDate == null) {
            if(earliestWindowStartDate != null) return false;
        } else if(!o.earliestWindowStartDate.equals(earliestWindowStartDate)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (movieId == null ? 1237 : movieId.hashCode());
        hashCode = hashCode * 31 + (countryCode == null ? 1237 : countryCode.hashCode());
        hashCode = hashCode * 31 + (languageCode == null ? 1237 : languageCode.hashCode());
        hashCode = hashCode * 31 + (earliestWindowStartDate == null ? 1237 : earliestWindowStartDate.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("FeedMovieCountryLanguages{");
        builder.append("movieId=").append(movieId);
        builder.append(",countryCode=").append(countryCode);
        builder.append(",languageCode=").append(languageCode);
        builder.append(",earliestWindowStartDate=").append(earliestWindowStartDate);
        builder.append("}");
        return builder.toString();
    }

    public FeedMovieCountryLanguages clone() {
        try {
            FeedMovieCountryLanguages clone = (FeedMovieCountryLanguages)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}