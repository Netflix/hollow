package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="ShowSeasonEpisode")
public class ShowSeasonEpisode implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    public long displaySetId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="ISOCountryList")
    public List<ISOCountry> countryCodes = null;
    @HollowTypeName(name="SeasonList")
    public List<Season> seasons = null;
    public boolean hideSeasonNumbers = false;
    public boolean episodicNewBadge = false;
    public String merchOrder = null;

    public ShowSeasonEpisode setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public ShowSeasonEpisode setDisplaySetId(long displaySetId) {
        this.displaySetId = displaySetId;
        return this;
    }
    public ShowSeasonEpisode setCountryCodes(List<ISOCountry> countryCodes) {
        this.countryCodes = countryCodes;
        return this;
    }
    public ShowSeasonEpisode setSeasons(List<Season> seasons) {
        this.seasons = seasons;
        return this;
    }
    public ShowSeasonEpisode setHideSeasonNumbers(boolean hideSeasonNumbers) {
        this.hideSeasonNumbers = hideSeasonNumbers;
        return this;
    }
    public ShowSeasonEpisode setEpisodicNewBadge(boolean episodicNewBadge) {
        this.episodicNewBadge = episodicNewBadge;
        return this;
    }
    public ShowSeasonEpisode setMerchOrder(String merchOrder) {
        this.merchOrder = merchOrder;
        return this;
    }
    public ShowSeasonEpisode addToCountryCodes(ISOCountry iSOCountry) {
        if (this.countryCodes == null) {
            this.countryCodes = new ArrayList<ISOCountry>();
        }
        this.countryCodes.add(iSOCountry);
        return this;
    }
    public ShowSeasonEpisode addToSeasons(Season season) {
        if (this.seasons == null) {
            this.seasons = new ArrayList<Season>();
        }
        this.seasons.add(season);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ShowSeasonEpisode))
            return false;

        ShowSeasonEpisode o = (ShowSeasonEpisode) other;
        if(o.movieId != movieId) return false;
        if(o.displaySetId != displaySetId) return false;
        if(o.countryCodes == null) {
            if(countryCodes != null) return false;
        } else if(!o.countryCodes.equals(countryCodes)) return false;
        if(o.seasons == null) {
            if(seasons != null) return false;
        } else if(!o.seasons.equals(seasons)) return false;
        if(o.hideSeasonNumbers != hideSeasonNumbers) return false;
        if(o.episodicNewBadge != episodicNewBadge) return false;
        if(o.merchOrder == null) {
            if(merchOrder != null) return false;
        } else if(!o.merchOrder.equals(merchOrder)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (int) (displaySetId ^ (displaySetId >>> 32));
        hashCode = hashCode * 31 + (countryCodes == null ? 1237 : countryCodes.hashCode());
        hashCode = hashCode * 31 + (seasons == null ? 1237 : seasons.hashCode());
        hashCode = hashCode * 31 + (hideSeasonNumbers? 1231 : 1237);
        hashCode = hashCode * 31 + (episodicNewBadge? 1231 : 1237);
        hashCode = hashCode * 31 + (merchOrder == null ? 1237 : merchOrder.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ShowSeasonEpisode{");
        builder.append("movieId=").append(movieId);
        builder.append(",displaySetId=").append(displaySetId);
        builder.append(",countryCodes=").append(countryCodes);
        builder.append(",seasons=").append(seasons);
        builder.append(",hideSeasonNumbers=").append(hideSeasonNumbers);
        builder.append(",episodicNewBadge=").append(episodicNewBadge);
        builder.append(",merchOrder=").append(merchOrder);
        builder.append("}");
        return builder.toString();
    }

    public ShowSeasonEpisode clone() {
        try {
            ShowSeasonEpisode clone = (ShowSeasonEpisode)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}