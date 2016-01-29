package com.netflix.vmsserver.videocollectionsdata;

import java.util.Map;

import java.util.Arrays;
import com.netflix.vms.hollowoutput.pojos.SupplementalVideo;
import com.netflix.vms.hollowoutput.pojos.SortedMapOfIntegerToListOfVideoEpisode;
import com.netflix.vms.hollowoutput.pojos.Video;
import com.netflix.vms.hollowoutput.pojos.VideoCollectionsData;
import com.netflix.vms.hollowoutput.pojos.VideoEpisode;
import com.netflix.vms.hollowoutput.pojos.VideoNodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class VideoCollectionsDataHierarchy {

    public static final SortedMapOfIntegerToListOfVideoEpisode EMPTY_EPISODE_SEQUENCE_NUMBER_MAP = new SortedMapOfIntegerToListOfVideoEpisode(Collections.<com.netflix.vms.hollowoutput.pojos.Integer, List<VideoEpisode>>emptyMap());

    public static final VideoNodeType MOVIE = new VideoNodeType("MOVIE");
    public static final VideoNodeType SHOW = new VideoNodeType("SHOW");
    public static final VideoNodeType SEASON = new VideoNodeType("SEASON");
    public static final VideoNodeType EPISODE = new VideoNodeType("EPISODE");
    public static final VideoNodeType SUPPLEMENTAL = new VideoNodeType("SUPPLEMENTAL");


    private VideoCollectionsData topNodeVideoCollectionsData;
    private final Video topNode;
    private final LinkedHashMap<Integer, VideoCollectionsData> orderedSeasons;
    private final List<LinkedHashMap<Integer, VideoCollectionsData>> orderedSeasonEpisodes;
    private final Map<Integer, VideoCollectionsData> supplementalVideosCollectionsData;

    private VideoCollectionsData currentSeason;
    private Video currentSeasonVideo;
    private LinkedHashMap<Integer, VideoCollectionsData> currentSeasonEpisodes;
    private List<VideoEpisode> currentSeasonVideoEpisodesList;
    private int totalAddedEpisodes = 0;

    public VideoCollectionsDataHierarchy(int videoId, boolean isStandalone, List<SupplementalVideo> supplementalVideos) {
        this.topNodeVideoCollectionsData = new VideoCollectionsData();
        this.orderedSeasons = new LinkedHashMap<Integer, VideoCollectionsData>();
        this.orderedSeasonEpisodes = new ArrayList<LinkedHashMap<Integer,VideoCollectionsData>>();
        this.topNode = new Video(videoId);
        this.supplementalVideosCollectionsData = new HashMap<Integer, VideoCollectionsData>();

        if(isStandalone) {
            topNodeVideoCollectionsData.nodeType = MOVIE;
            topNodeVideoCollectionsData.topNodeType = MOVIE;
            topNodeVideoCollectionsData.videoEpisodes = Collections.emptyList();
            topNodeVideoCollectionsData.showChildren = Collections.emptyList();
            topNodeVideoCollectionsData.seasonChildren = Collections.emptyList();
            topNodeVideoCollectionsData.topNode = topNode;
            topNodeVideoCollectionsData.episodesForSeasonSequenceNumberMap = EMPTY_EPISODE_SEQUENCE_NUMBER_MAP;
            topNodeVideoCollectionsData.supplementalVideos = supplementalVideos;
        } else {
            topNodeVideoCollectionsData.nodeType = SHOW;
            topNodeVideoCollectionsData.topNodeType = SHOW;
            topNodeVideoCollectionsData.videoEpisodes = new ArrayList<VideoEpisode>();
            topNodeVideoCollectionsData.showChildren = new ArrayList<Video>();
            topNodeVideoCollectionsData.seasonChildren = new ArrayList<Video>();
            topNodeVideoCollectionsData.topNode = topNode;
            topNodeVideoCollectionsData.episodesForSeasonSequenceNumberMap = new SortedMapOfIntegerToListOfVideoEpisode(new HashMap<com.netflix.vms.hollowoutput.pojos.Integer, List<VideoEpisode>>());
            topNodeVideoCollectionsData.supplementalVideos = supplementalVideos;
        }

        addSupplementalVideoCollectionsData(supplementalVideos);
    }

    public void addSeason(Integer videoId, List<SupplementalVideo> supplementalVideos) {
        this.currentSeasonVideo = new Video(videoId.intValue());
        this.currentSeasonEpisodes = new LinkedHashMap<Integer, VideoCollectionsData>();
        this.currentSeasonVideoEpisodesList = new ArrayList<VideoEpisode>();
        this.currentSeason = new VideoCollectionsData();
        currentSeason.seasonChildren = new ArrayList<Video>();
        this.orderedSeasons.put(videoId, currentSeason);
        this.orderedSeasonEpisodes.add(currentSeasonEpisodes);

        topNodeVideoCollectionsData.showChildren.add(currentSeasonVideo);
        topNodeVideoCollectionsData.episodesForSeasonSequenceNumberMap.map.put(new com.netflix.vms.hollowoutput.pojos.Integer(orderedSeasons.size()), this.currentSeasonVideoEpisodesList);
        topNodeVideoCollectionsData.supplementalVideos.addAll(supplementalVideos);

        currentSeason.nodeType = SEASON;
        currentSeason.topNodeType = SHOW;
        currentSeason.videoEpisodes = new ArrayList<VideoEpisode>();
        currentSeason.showChildren = Collections.emptyList();
        currentSeason.seasonChildren = new ArrayList<Video>();
        currentSeason.topNode = topNode;
        currentSeason.showParent = topNode;
        currentSeason.supplementalVideos = supplementalVideos;
        currentSeason.episodesForSeasonSequenceNumberMap = EMPTY_EPISODE_SEQUENCE_NUMBER_MAP;

        addSupplementalVideoCollectionsData(supplementalVideos);
    }

    public void addEpisode(int videoId) {
        VideoCollectionsData episode = new VideoCollectionsData();
        Video v = new Video(videoId);
        currentSeason.seasonChildren.add(v);
        topNodeVideoCollectionsData.seasonChildren.add(v);
        currentSeasonEpisodes.put(videoId, episode);

        VideoEpisode videoEpisode = new VideoEpisode();
        videoEpisode.seriesParent = currentSeasonVideo;
        videoEpisode.deliverableVideo = v;
        videoEpisode.sequenceNumber = currentSeasonEpisodes.size();
        videoEpisode.showSequenceNumber = ++totalAddedEpisodes;
        videoEpisode.seasonSequenceNumber = orderedSeasons.size();
        videoEpisode.episodeSequenceNumber = currentSeasonEpisodes.size(); ///TODO: This is just a duplicate of the sequenceNumber field?
        topNodeVideoCollectionsData.videoEpisodes.add(videoEpisode);
        currentSeason.videoEpisodes.add(videoEpisode);
        currentSeasonVideoEpisodesList.add(videoEpisode);


        episode.nodeType = EPISODE;
        episode.topNodeType = SHOW;
        episode.videoEpisodes = Collections.singletonList(videoEpisode);
        episode.showChildren = Collections.emptyList();
        episode.seasonChildren = Collections.emptyList();
        episode.showParent = topNode;
        episode.seasonParent = currentSeasonVideo;
        episode.topNode = topNode;
        episode.episodesForSeasonSequenceNumberMap = EMPTY_EPISODE_SEQUENCE_NUMBER_MAP;
    }

    private void addSupplementalVideoCollectionsData(List<SupplementalVideo> supplementalVideos) {
        for(SupplementalVideo suppVideo : supplementalVideos) {
            VideoCollectionsData supplementalVideoCollectionsData = new VideoCollectionsData();
            supplementalVideoCollectionsData.nodeType = SUPPLEMENTAL;
            supplementalVideoCollectionsData.topNodeType = topNodeVideoCollectionsData.nodeType;
            supplementalVideoCollectionsData.videoEpisodes = Collections.emptyList();
            supplementalVideoCollectionsData.showChildren = Collections.emptyList();
            supplementalVideoCollectionsData.supplementalVideoParents = currentSeasonVideo == null ? Collections.singletonList(topNode) : Arrays.asList(topNode, currentSeasonVideo);
            supplementalVideoCollectionsData.topNode = topNode;

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
}
