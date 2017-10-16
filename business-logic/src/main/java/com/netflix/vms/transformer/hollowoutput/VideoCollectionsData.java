package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoCollectionsData implements Cloneable {

    public VideoNodeType nodeType = null;
    public VideoNodeType topNodeType = null;
    public List<VideoEpisode> videoEpisodes = null;
    public List<Video> showChildren = null;
    public List<Video> seasonChildren = null;
    public List<Video> supplementalVideoParents = null;
    public List<SupplementalVideo> supplementalVideos = null;
    public Video showParent = null;
    public Video seasonParent = null;
    public Video topNode = null;
    public SortedMapOfIntegerToListOfVideoEpisode episodesForSeasonSequenceNumberMap = null;
    public int seasonNumber = -1;
    public VideoEpisode videoEpisode = null;
    public VideoSeason videoSeason = null;
    public VideoShow videoShow = null;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoCollectionsData)) return false;

        VideoCollectionsData that = (VideoCollectionsData) o;

        if (seasonNumber != that.seasonNumber) return false;
        if (!nodeType.equals(that.nodeType)) return false;
        if (!topNodeType.equals(that.topNodeType)) return false;
        if (!videoEpisodes.equals(that.videoEpisodes)) return false;
        if (!showChildren.equals(that.showChildren)) return false;
        if (!seasonChildren.equals(that.seasonChildren)) return false;
        if (!supplementalVideoParents.equals(that.supplementalVideoParents)) return false;
        if (!supplementalVideos.equals(that.supplementalVideos)) return false;
        if (!showParent.equals(that.showParent)) return false;
        if (!seasonParent.equals(that.seasonParent)) return false;
        if (!topNode.equals(that.topNode)) return false;
        if (!episodesForSeasonSequenceNumberMap.equals(that.episodesForSeasonSequenceNumberMap)) return false;
        if (!videoEpisode.equals(that.videoEpisode)) return false;
        if (!videoSeason.equals(that.videoSeason)) return false;
        return videoShow.equals(that.videoShow);
    }

    @Override
    public int hashCode() {
        int result = nodeType.hashCode();
        result = 31 * result + topNodeType.hashCode();
        result = 31 * result + videoEpisodes.hashCode();
        result = 31 * result + showChildren.hashCode();
        result = 31 * result + seasonChildren.hashCode();
        result = 31 * result + supplementalVideoParents.hashCode();
        result = 31 * result + supplementalVideos.hashCode();
        result = 31 * result + showParent.hashCode();
        result = 31 * result + seasonParent.hashCode();
        result = 31 * result + topNode.hashCode();
        result = 31 * result + episodesForSeasonSequenceNumberMap.hashCode();
        result = 31 * result + seasonNumber;
        result = 31 * result + videoEpisode.hashCode();
        result = 31 * result + videoSeason.hashCode();
        result = 31 * result + videoShow.hashCode();
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoCollectionsData{");
        builder.append("nodeType=").append(nodeType);

        builder.append(",topNodeType=").append(topNodeType);
        builder.append(",videoEpisodes=").append(videoEpisodes);
        builder.append(",showChildren=").append(showChildren);
        builder.append(",seasonChildren=").append(seasonChildren);
        builder.append(",supplementalVideoParents=").append(supplementalVideoParents);
        builder.append(",supplementalVideos=").append(supplementalVideos);
        builder.append(",showParent=").append(showParent);
        builder.append(",seasonParent=").append(seasonParent);
        builder.append(",topNode=").append(topNode);
        builder.append(",episodesForSeasonSequenceNumberMap=").append(episodesForSeasonSequenceNumberMap);
        builder.append(",seasonNumber=").append(seasonNumber);
        builder.append(",videoEpisode=").append(videoEpisode);
        builder.append(",videoSeason=").append(videoSeason);
        builder.append(",videoShow=").append(videoShow);
        builder.append("}");
        return builder.toString();
    }

    public VideoCollectionsData clone() {
        try {
            VideoCollectionsData clone = (VideoCollectionsData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
