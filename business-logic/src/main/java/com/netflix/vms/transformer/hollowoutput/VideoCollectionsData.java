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

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoCollectionsData))
            return false;

        VideoCollectionsData o = (VideoCollectionsData) other;
        if(o.nodeType == null) {
            if(nodeType != null) return false;
        } else if(!o.nodeType.equals(nodeType)) return false;
        if(o.topNodeType == null) {
            if(topNodeType != null) return false;
        } else if(!o.topNodeType.equals(topNodeType)) return false;
        if(o.videoEpisodes == null) {
            if(videoEpisodes != null) return false;
        } else if(!o.videoEpisodes.equals(videoEpisodes)) return false;
        if(o.showChildren == null) {
            if(showChildren != null) return false;
        } else if(!o.showChildren.equals(showChildren)) return false;
        if(o.seasonChildren == null) {
            if(seasonChildren != null) return false;
        } else if(!o.seasonChildren.equals(seasonChildren)) return false;
        if(o.supplementalVideoParents == null) {
            if(supplementalVideoParents != null) return false;
        } else if(!o.supplementalVideoParents.equals(supplementalVideoParents)) return false;
        if(o.supplementalVideos == null) {
            if(supplementalVideos != null) return false;
        } else if(!o.supplementalVideos.equals(supplementalVideos)) return false;
        if(o.showParent == null) {
            if(showParent != null) return false;
        } else if(!o.showParent.equals(showParent)) return false;
        if(o.seasonParent == null) {
            if(seasonParent != null) return false;
        } else if(!o.seasonParent.equals(seasonParent)) return false;
        if(o.topNode == null) {
            if(topNode != null) return false;
        } else if(!o.topNode.equals(topNode)) return false;
        if(o.episodesForSeasonSequenceNumberMap == null) {
            if(episodesForSeasonSequenceNumberMap != null) return false;
        } else if(!o.episodesForSeasonSequenceNumberMap.equals(episodesForSeasonSequenceNumberMap)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (nodeType == null ? 1237 : nodeType.hashCode());
        hashCode = hashCode * 31 + (topNodeType == null ? 1237 : topNodeType.hashCode());
        hashCode = hashCode * 31 + (videoEpisodes == null ? 1237 : videoEpisodes.hashCode());
        hashCode = hashCode * 31 + (showChildren == null ? 1237 : showChildren.hashCode());
        hashCode = hashCode * 31 + (seasonChildren == null ? 1237 : seasonChildren.hashCode());
        hashCode = hashCode * 31 + (supplementalVideoParents == null ? 1237 : supplementalVideoParents.hashCode());
        hashCode = hashCode * 31 + (supplementalVideos == null ? 1237 : supplementalVideos.hashCode());
        hashCode = hashCode * 31 + (showParent == null ? 1237 : showParent.hashCode());
        hashCode = hashCode * 31 + (seasonParent == null ? 1237 : seasonParent.hashCode());
        hashCode = hashCode * 31 + (topNode == null ? 1237 : topNode.hashCode());
        hashCode = hashCode * 31 + (episodesForSeasonSequenceNumberMap == null ? 1237 : episodesForSeasonSequenceNumberMap.hashCode());
        return hashCode;
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
    private int __assigned_ordinal = -1;
}