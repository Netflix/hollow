package com.netflix.vms.transformer.modules.collections;

import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfIntegerToListOfVideoEpisode;
import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoCollectionsData;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VideoCollectionsDataHierarchy {

    private VideoCollectionsData topNodeVideoCollectionsData;
    private final Video topNode;
    private final LinkedHashMap<Integer, VideoCollectionsData> orderedSeasons;
    private final List<LinkedHashMap<Integer, VideoCollectionsData>> orderedSeasonEpisodes;
    private final Map<Integer, VideoCollectionsData> supplementalVideosCollectionsData;
    private final CycleConstants constants;

    private VideoCollectionsData currentSeason;
    private int currentSeasonSequenceNumber;
    private Video currentSeasonVideo;
    private LinkedHashMap<Integer, VideoCollectionsData> currentSeasonEpisodes;
    private List<VideoEpisode> currentSeasonVideoEpisodesList;
    private int totalAddedEpisodes = 0;

    public VideoCollectionsDataHierarchy(int videoId, boolean isStandalone, List<SupplementalVideo> supplementalVideos, CycleConstants constants) {
        this.topNodeVideoCollectionsData = new VideoCollectionsData();
        this.orderedSeasons = new LinkedHashMap<Integer, VideoCollectionsData>();
        this.orderedSeasonEpisodes = new ArrayList<LinkedHashMap<Integer,VideoCollectionsData>>();
        this.topNode = new Video(videoId);
        this.supplementalVideosCollectionsData = new HashMap<Integer, VideoCollectionsData>();
        this.constants = constants;

        if(isStandalone) {
            topNodeVideoCollectionsData.nodeType = constants.MOVIE;
            topNodeVideoCollectionsData.topNodeType = constants.MOVIE;
            topNodeVideoCollectionsData.videoEpisodes = Collections.emptyList();
            topNodeVideoCollectionsData.showChildren = Collections.emptyList();
            topNodeVideoCollectionsData.seasonChildren = Collections.emptyList();
            topNodeVideoCollectionsData.supplementalVideoParents = Collections.emptyList();
            topNodeVideoCollectionsData.topNode = topNode;
            topNodeVideoCollectionsData.episodesForSeasonSequenceNumberMap = constants.EMPTY_EPISODE_SEQUENCE_NUMBER_MAP;
            topNodeVideoCollectionsData.supplementalVideos = supplementalVideos;
        } else {
            topNodeVideoCollectionsData.nodeType = constants.SHOW;
            topNodeVideoCollectionsData.topNodeType = constants.SHOW;
            topNodeVideoCollectionsData.videoEpisodes = new ArrayList<VideoEpisode>();
            topNodeVideoCollectionsData.showChildren = new ArrayList<Video>();
            topNodeVideoCollectionsData.seasonChildren = new ArrayList<Video>();
            topNodeVideoCollectionsData.supplementalVideoParents = Collections.emptyList();
            topNodeVideoCollectionsData.topNode = topNode;
            topNodeVideoCollectionsData.episodesForSeasonSequenceNumberMap = new SortedMapOfIntegerToListOfVideoEpisode(new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, List<VideoEpisode>>());
            topNodeVideoCollectionsData.supplementalVideos = supplementalVideos;
        }

        addSupplementalVideoCollectionsData(supplementalVideos, null);
    }

    public void addSeason(Integer videoId, int sequenceNumber, List<SupplementalVideo> supplementalVideos) {
        this.currentSeasonVideo = new Video(videoId.intValue());
        this.currentSeasonEpisodes = new LinkedHashMap<Integer, VideoCollectionsData>();
        this.currentSeasonVideoEpisodesList = new ArrayList<VideoEpisode>();
        this.currentSeason = new VideoCollectionsData();
        this.currentSeasonSequenceNumber = sequenceNumber;
        currentSeason.seasonChildren = new ArrayList<Video>();
        this.orderedSeasons.put(videoId, currentSeason);
        this.orderedSeasonEpisodes.add(currentSeasonEpisodes);

        topNodeVideoCollectionsData.showChildren.add(currentSeasonVideo);
        addSupplementalsToTopNode(supplementalVideos);

        currentSeason.nodeType = constants.SEASON;
        currentSeason.topNodeType = constants.SHOW;
        currentSeason.videoEpisodes = new ArrayList<VideoEpisode>();
        currentSeason.showChildren = Collections.emptyList();
        currentSeason.seasonChildren = new ArrayList<Video>();
        currentSeason.supplementalVideoParents = Collections.emptyList();
        currentSeason.topNode = topNode;
        currentSeason.showParent = topNode;
        currentSeason.supplementalVideos = supplementalVideos;
        currentSeason.episodesForSeasonSequenceNumberMap = constants.EMPTY_EPISODE_SEQUENCE_NUMBER_MAP;
        currentSeason.seasonNumber = sequenceNumber;

        addSupplementalVideoCollectionsData(supplementalVideos, null);
    }

    private void addSupplementalsToTopNode(List<SupplementalVideo> originalList) {
        for(SupplementalVideo vid : originalList) {
            SupplementalVideo clone = vid.clone();
            clone.parent = topNode;
            topNodeVideoCollectionsData.supplementalVideos.add(clone);
        }
    }

    public void addEpisode(int videoId, int sequenceNumber, List<SupplementalVideo> supplementalVideos) {
        VideoCollectionsData episode = new VideoCollectionsData();
        Video v = new Video(videoId);
        currentSeason.seasonChildren.add(v);
        topNodeVideoCollectionsData.seasonChildren.add(v);
        currentSeasonEpisodes.put(videoId, episode);

        VideoEpisode videoEpisode = new VideoEpisode();
        videoEpisode.seriesParent = currentSeasonVideo;
        videoEpisode.deliverableVideo = v;
        videoEpisode.sequenceNumber = sequenceNumber;
        videoEpisode.showSequenceNumber = ++totalAddedEpisodes;
        videoEpisode.seasonSequenceNumber = currentSeasonSequenceNumber;
        videoEpisode.episodeSequenceNumber = sequenceNumber; ///TODO: This is just a duplicate of the sequenceNumber field?
        topNodeVideoCollectionsData.videoEpisodes.add(videoEpisode);
        currentSeason.videoEpisodes.add(videoEpisode);
        currentSeasonVideoEpisodesList.add(videoEpisode);

        if(currentSeasonVideoEpisodesList.size() == 1)
            topNodeVideoCollectionsData.episodesForSeasonSequenceNumberMap.map.put(new com.netflix.vms.transformer.hollowoutput.Integer(this.currentSeasonSequenceNumber), this.currentSeasonVideoEpisodesList);

        episode.nodeType = constants.EPISODE;
        episode.topNodeType = constants.SHOW;
        episode.videoEpisodes = Collections.singletonList(videoEpisode);
        episode.showChildren = Collections.emptyList();
        episode.seasonChildren = Collections.emptyList();
        episode.supplementalVideoParents = Collections.emptyList();
        episode.showParent = topNode;
        episode.seasonParent = currentSeasonVideo;
        episode.supplementalVideos = supplementalVideos;
        episode.topNode = topNode;
        episode.episodesForSeasonSequenceNumberMap = constants.EMPTY_EPISODE_SEQUENCE_NUMBER_MAP;

        addSupplementalVideoCollectionsData(supplementalVideos, v);
    }

    private void addSupplementalVideoCollectionsData(List<SupplementalVideo> supplementalVideos, Video supplementalVideoParent) {
        for(SupplementalVideo suppVideo : supplementalVideos) {
            VideoCollectionsData supplementalVideoCollectionsData = new VideoCollectionsData();
            supplementalVideoCollectionsData.nodeType = constants.SUPPLEMENTAL;
            supplementalVideoCollectionsData.topNodeType = topNodeVideoCollectionsData.nodeType;
            supplementalVideoCollectionsData.videoEpisodes = Collections.emptyList();
            supplementalVideoCollectionsData.showChildren = Collections.emptyList();
            supplementalVideoCollectionsData.seasonChildren = Collections.emptyList();
            supplementalVideoCollectionsData.supplementalVideos = Collections.emptyList();
            if(supplementalVideoParent == null)
                supplementalVideoCollectionsData.supplementalVideoParents = currentSeasonVideo == null ? Collections.singletonList(topNode) : Arrays.asList(topNode, currentSeasonVideo);
            else
                supplementalVideoCollectionsData.supplementalVideoParents = Collections.singletonList(supplementalVideoParent);

            supplementalVideoCollectionsData.topNode = topNode;

            if(supplementalVideoCollectionsData.topNodeType == constants.SHOW)
                supplementalVideoCollectionsData.showParent = topNode;

            supplementalVideoCollectionsData.episodesForSeasonSequenceNumberMap = constants.EMPTY_EPISODE_SEQUENCE_NUMBER_MAP;

            supplementalVideosCollectionsData.put(suppVideo.id.value, supplementalVideoCollectionsData);
        }
    }

    public VideoCollectionsData getTopNode() {
        return topNodeVideoCollectionsData;
    }

    public void setTopNode(VideoCollectionsData vcd) {
        this.topNodeVideoCollectionsData = vcd;
    }

    public Video getTopNodeId() {
        return topNode;
    }

    public LinkedHashMap<Integer, VideoCollectionsData> getOrderedSeasons() {
        return orderedSeasons;
    }

    public LinkedHashMap<Integer, VideoCollectionsData> getOrderedSeasonEpisodes(int seasonSequenceNumber) {
        return orderedSeasonEpisodes.get(seasonSequenceNumber - 1);
    }

    public Map<Integer, VideoCollectionsData> getSupplementalVideosCollectionsData() {
        return supplementalVideosCollectionsData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("topNodeId=").append(topNode.value);
        int i = 0;
        for (int seasonId : orderedSeasons.keySet()) {
            sb.append("\n\tseasonId=").append(seasonId);
            for (int episodeId : orderedSeasonEpisodes.get(i++).keySet()) {
                sb.append("\n\t\tepisodeIds=").append(episodeId);
            }
        }

        sb.append("\nsupplementalIds=");
        for (int supId : supplementalVideosCollectionsData.keySet()) {
            sb.append(supId).append(" ");
        }

        return sb.toString();
    }
}
