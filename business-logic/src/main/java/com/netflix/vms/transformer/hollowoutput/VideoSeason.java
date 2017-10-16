package com.netflix.vms.transformer.hollowoutput;

/**
 * Adding attributes specific for a video type that is a season. An instance of this class is used in VideoCollectionsData.
 */
public class VideoSeason {

    public Boolean hideEpisodeNumbers = false;
    public Boolean episodicNewBadge = false;
    public int episodeSkipping = 0;
    public Boolean filterUnavailableEpisodes = true;
    public Boolean useLatestEpisodeAsDefault = false;
    public String merchOrder = "regular";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoSeason)) return false;

        VideoSeason that = (VideoSeason) o;

        if (episodeSkipping != that.episodeSkipping) return false;
        if (!hideEpisodeNumbers.equals(that.hideEpisodeNumbers)) return false;
        if (!episodicNewBadge.equals(that.episodicNewBadge)) return false;
        if (!filterUnavailableEpisodes.equals(that.filterUnavailableEpisodes)) return false;
        if (!useLatestEpisodeAsDefault.equals(that.useLatestEpisodeAsDefault)) return false;
        return merchOrder.equals(that.merchOrder);
    }

    @Override
    public int hashCode() {
        int result = hideEpisodeNumbers.hashCode();
        result = 31 * result + episodicNewBadge.hashCode();
        result = 31 * result + episodeSkipping;
        result = 31 * result + filterUnavailableEpisodes.hashCode();
        result = 31 * result + useLatestEpisodeAsDefault.hashCode();
        result = 31 * result + merchOrder.hashCode();
        return result;
    }
}
