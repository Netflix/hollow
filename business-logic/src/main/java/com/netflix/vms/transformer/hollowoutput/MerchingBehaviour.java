package com.netflix.vms.transformer.hollowoutput;

/**
 * This class contains flags that contain information on merching/display order for a video.
 */
public class MerchingBehaviour {

    public String merchOrder = "regular";
    public Boolean episodicNewBadge = false;

    // specific to show
    public Boolean hideSeasonNumbers = false;

    // specific to season
    public Boolean hideEpisodeNumbers = false;
    public int episodeSkipping = 0;
    public Boolean filterUnavailableEpisodes = true;
    public Boolean useLatestEpisodeAsDefault = false;

    // specific to episode
    public Boolean midSeason = false;
    public Boolean seasonFinale = false;
    public Boolean showFinale = false;

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MerchingBehaviour)) return false;

        MerchingBehaviour that = (MerchingBehaviour) o;

        if (episodeSkipping != that.episodeSkipping) return false;
        if (merchOrder != null ? !merchOrder.equals(that.merchOrder) : that.merchOrder != null) return false;
        if (episodicNewBadge != null ? !episodicNewBadge.equals(that.episodicNewBadge) : that.episodicNewBadge != null)
            return false;
        if (hideSeasonNumbers != null ? !hideSeasonNumbers.equals(that.hideSeasonNumbers) : that.hideSeasonNumbers != null)
            return false;
        if (hideEpisodeNumbers != null ? !hideEpisodeNumbers.equals(that.hideEpisodeNumbers) : that.hideEpisodeNumbers != null)
            return false;
        if (filterUnavailableEpisodes != null ? !filterUnavailableEpisodes.equals(that.filterUnavailableEpisodes) : that.filterUnavailableEpisodes != null)
            return false;
        if (useLatestEpisodeAsDefault != null ? !useLatestEpisodeAsDefault.equals(that.useLatestEpisodeAsDefault) : that.useLatestEpisodeAsDefault != null)
            return false;
        if (midSeason != null ? !midSeason.equals(that.midSeason) : that.midSeason != null) return false;
        if (seasonFinale != null ? !seasonFinale.equals(that.seasonFinale) : that.seasonFinale != null) return false;
        return showFinale != null ? showFinale.equals(that.showFinale) : that.showFinale == null;
    }

    @Override
    public int hashCode() {
        int result = merchOrder != null ? merchOrder.hashCode() : 0;
        result = 31 * result + (episodicNewBadge != null ? episodicNewBadge.hashCode() : 0);
        result = 31 * result + (hideSeasonNumbers != null ? hideSeasonNumbers.hashCode() : 0);
        result = 31 * result + (hideEpisodeNumbers != null ? hideEpisodeNumbers.hashCode() : 0);
        result = 31 * result + episodeSkipping;
        result = 31 * result + (filterUnavailableEpisodes != null ? filterUnavailableEpisodes.hashCode() : 0);
        result = 31 * result + (useLatestEpisodeAsDefault != null ? useLatestEpisodeAsDefault.hashCode() : 0);
        result = 31 * result + (midSeason != null ? midSeason.hashCode() : 0);
        result = 31 * result + (seasonFinale != null ? seasonFinale.hashCode() : 0);
        result = 31 * result + (showFinale != null ? showFinale.hashCode() : 0);
        return result;
    }

    public String toString() {

        StringBuilder builder = new StringBuilder("MerchingBehaviour{");
        builder.append("merchOrder=").append(merchOrder);
        builder.append(",episodicNewBadge=").append(episodicNewBadge);
        builder.append(",hideSeasonNumbers=").append(hideSeasonNumbers);
        builder.append(",hideEpisodeNumbers=").append(hideEpisodeNumbers);
        builder.append(",episodeSkipping=").append(episodeSkipping);
        builder.append(",filterUnavailableEpisodes=").append(filterUnavailableEpisodes);
        builder.append(",useLatestEpisodeAsDefault=").append(useLatestEpisodeAsDefault);
        builder.append(",midSeason=").append(midSeason);
        builder.append(",seasonFinale=").append(seasonFinale);
        builder.append(",showFinale=").append(showFinale);
        builder.append("}");
        return builder.toString();
    }
}
