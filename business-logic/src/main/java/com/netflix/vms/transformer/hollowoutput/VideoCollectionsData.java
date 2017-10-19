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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoCollectionsData)) return false;

        VideoCollectionsData that = (VideoCollectionsData) o;

        if (seasonNumber != that.seasonNumber) return false;
        if (nodeType != null ? !nodeType.equals(that.nodeType) : that.nodeType != null) return false;
        if (topNodeType != null ? !topNodeType.equals(that.topNodeType) : that.topNodeType != null) return false;
        if (videoEpisodes != null ? !videoEpisodes.equals(that.videoEpisodes) : that.videoEpisodes != null)
            return false;
        if (showChildren != null ? !showChildren.equals(that.showChildren) : that.showChildren != null) return false;
        if (seasonChildren != null ? !seasonChildren.equals(that.seasonChildren) : that.seasonChildren != null)
            return false;
        if (supplementalVideoParents != null ? !supplementalVideoParents.equals(that.supplementalVideoParents) : that.supplementalVideoParents != null)
            return false;
        if (supplementalVideos != null ? !supplementalVideos.equals(that.supplementalVideos) : that.supplementalVideos != null)
            return false;
        if (showParent != null ? !showParent.equals(that.showParent) : that.showParent != null) return false;
        if (seasonParent != null ? !seasonParent.equals(that.seasonParent) : that.seasonParent != null) return false;
        if (topNode != null ? !topNode.equals(that.topNode) : that.topNode != null) return false;
        if (episodesForSeasonSequenceNumberMap != null ? !episodesForSeasonSequenceNumberMap.equals(that.episodesForSeasonSequenceNumberMap) : that.episodesForSeasonSequenceNumberMap != null)
            return false;
        return videoEpisode != null ? videoEpisode.equals(that.videoEpisode) : that.videoEpisode == null;
    }

    @Override
    public int hashCode() {
        int result = nodeType != null ? nodeType.hashCode() : 0;
        result = 31 * result + (topNodeType != null ? topNodeType.hashCode() : 0);
        result = 31 * result + (videoEpisodes != null ? videoEpisodes.hashCode() : 0);
        result = 31 * result + (showChildren != null ? showChildren.hashCode() : 0);
        result = 31 * result + (seasonChildren != null ? seasonChildren.hashCode() : 0);
        result = 31 * result + (supplementalVideoParents != null ? supplementalVideoParents.hashCode() : 0);
        result = 31 * result + (supplementalVideos != null ? supplementalVideos.hashCode() : 0);
        result = 31 * result + (showParent != null ? showParent.hashCode() : 0);
        result = 31 * result + (seasonParent != null ? seasonParent.hashCode() : 0);
        result = 31 * result + (topNode != null ? topNode.hashCode() : 0);
        result = 31 * result + (episodesForSeasonSequenceNumberMap != null ? episodesForSeasonSequenceNumberMap.hashCode() : 0);
        result = 31 * result + seasonNumber;
        result = 31 * result + (videoEpisode != null ? videoEpisode.hashCode() : 0);
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
