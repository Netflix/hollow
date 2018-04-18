package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="Season")
public class Season implements Cloneable {

    public long sequenceNumber = java.lang.Long.MIN_VALUE;
    public long movieId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="EpisodeList")
    public List<Episode> episodes = null;
    public boolean hideEpisodeNumbers = false;
    public boolean episodicNewBadge = false;
    public int episodeSkipping = java.lang.Integer.MIN_VALUE;
    public boolean filterUnavailableEpisodes = false;
    public boolean useLatestEpisodeAsDefault = false;
    public String merchOrder = null;

    public Season setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    public Season setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public Season setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
        return this;
    }
    public Season setHideEpisodeNumbers(boolean hideEpisodeNumbers) {
        this.hideEpisodeNumbers = hideEpisodeNumbers;
        return this;
    }
    public Season setEpisodicNewBadge(boolean episodicNewBadge) {
        this.episodicNewBadge = episodicNewBadge;
        return this;
    }
    public Season setEpisodeSkipping(int episodeSkipping) {
        this.episodeSkipping = episodeSkipping;
        return this;
    }
    public Season setFilterUnavailableEpisodes(boolean filterUnavailableEpisodes) {
        this.filterUnavailableEpisodes = filterUnavailableEpisodes;
        return this;
    }
    public Season setUseLatestEpisodeAsDefault(boolean useLatestEpisodeAsDefault) {
        this.useLatestEpisodeAsDefault = useLatestEpisodeAsDefault;
        return this;
    }
    public Season setMerchOrder(String merchOrder) {
        this.merchOrder = merchOrder;
        return this;
    }
    public Season addToEpisodes(Episode episode) {
        if (this.episodes == null) {
            this.episodes = new ArrayList<Episode>();
        }
        this.episodes.add(episode);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Season))
            return false;

        Season o = (Season) other;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.movieId != movieId) return false;
        if(o.episodes == null) {
            if(episodes != null) return false;
        } else if(!o.episodes.equals(episodes)) return false;
        if(o.hideEpisodeNumbers != hideEpisodeNumbers) return false;
        if(o.episodicNewBadge != episodicNewBadge) return false;
        if(o.episodeSkipping != episodeSkipping) return false;
        if(o.filterUnavailableEpisodes != filterUnavailableEpisodes) return false;
        if(o.useLatestEpisodeAsDefault != useLatestEpisodeAsDefault) return false;
        if(o.merchOrder == null) {
            if(merchOrder != null) return false;
        } else if(!o.merchOrder.equals(merchOrder)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (episodes == null ? 1237 : episodes.hashCode());
        hashCode = hashCode * 31 + (hideEpisodeNumbers? 1231 : 1237);
        hashCode = hashCode * 31 + (episodicNewBadge? 1231 : 1237);
        hashCode = hashCode * 31 + episodeSkipping;
        hashCode = hashCode * 31 + (filterUnavailableEpisodes? 1231 : 1237);
        hashCode = hashCode * 31 + (useLatestEpisodeAsDefault? 1231 : 1237);
        hashCode = hashCode * 31 + (merchOrder == null ? 1237 : merchOrder.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Season{");
        builder.append("sequenceNumber=").append(sequenceNumber);
        builder.append(",movieId=").append(movieId);
        builder.append(",episodes=").append(episodes);
        builder.append(",hideEpisodeNumbers=").append(hideEpisodeNumbers);
        builder.append(",episodicNewBadge=").append(episodicNewBadge);
        builder.append(",episodeSkipping=").append(episodeSkipping);
        builder.append(",filterUnavailableEpisodes=").append(filterUnavailableEpisodes);
        builder.append(",useLatestEpisodeAsDefault=").append(useLatestEpisodeAsDefault);
        builder.append(",merchOrder=").append(merchOrder);
        builder.append("}");
        return builder.toString();
    }

    public Season clone() {
        try {
            Season clone = (Season)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}