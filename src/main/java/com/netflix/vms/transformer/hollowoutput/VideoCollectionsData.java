package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoCollectionsData {

    public VideoNodeType nodeType;
    public VideoNodeType topNodeType;
    public List<VideoEpisode> videoEpisodes;
    public List<Video> showChildren;
    public List<Video> seasonChildren;
    public List<Video> supplementalVideoParents;
    public List<SupplementalVideo> supplementalVideos;
    public Video showParent;
    public Video seasonParent;
    public Video topNode;
    public SortedMapOfIntegerToListOfVideoEpisode episodesForSeasonSequenceNumberMap;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}