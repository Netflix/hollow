package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.data.DeployablePackagesFetcher;
import com.netflix.vms.transformer.hollowinput.EpisodeHollow;
import com.netflix.vms.transformer.hollowinput.EpisodesHollow;
import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;
import com.netflix.vms.transformer.hollowinput.MovieRatingsHollow;
import com.netflix.vms.transformer.hollowinput.SeasonHollow;
import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoHierarchyGrouper {

    private final VMSHollowInputAPI api;
    private final DeployablePackagesFetcher deployablePackagesFetcher;
    private final TransformerContext ctx;
    private final Map<Integer, Set<Integer>> topParentMap = new HashMap<>();
    private final Map<Integer, Set<ShowSeasonEpisodeHollow>> displaySetsByVideoId = new HashMap<>();
    private final List<Set<VideoHierarchyGroup>> processGroups = new ArrayList<>();

    public VideoHierarchyGrouper(VMSHollowInputAPI api,
            DeployablePackagesFetcher deployablePackagesFetcher, TransformerContext ctx) {
        this.api = api;
        this.ctx = ctx;
        this.deployablePackagesFetcher = deployablePackagesFetcher;
        group();
    }

    public Map<Integer, Set<ShowSeasonEpisodeHollow>> getGroupedShowSeasonEpisodes() {
        return displaySetsByVideoId;
    }

    public List<Set<VideoHierarchyGroup>> getProcessGroups() {
        return processGroups;
    }

    private void group() {
        processVideoRelationships();
        expandFastlaneIds();
        findProcessGroups();
        Collections.shuffle(processGroups);
    }

    private void processVideoRelationships() {
        for(ShowSeasonEpisodeHollow showSeasonEpisode : api.getAllShowSeasonEpisodeHollow()) {
            int topParentId = (int) showSeasonEpisode._getMovieId();
            Set<ShowSeasonEpisodeHollow> groupOfSets = new HashSet<>();
            groupOfSets.add(showSeasonEpisode);

            for(Integer videoId : getBagOfVideoIds(groupOfSets)) {
                Set<ShowSeasonEpisodeHollow> existingSet = displaySetsByVideoId.get(videoId);
                if(existingSet != null && existingSet != groupOfSets) {
                    groupOfSets.addAll(existingSet);

                    for(Integer replaceForVideoId : getBagOfVideoIds(existingSet)) {
                        displaySetsByVideoId.put(replaceForVideoId, groupOfSets);
                    }
                }

                displaySetsByVideoId.put(videoId, groupOfSets);
                trackTopParent(videoId, topParentId);
            }
        }

        for (SupplementalsHollow sups : api.getAllSupplementalsHollow()) {
            int supParentId = (int) sups._getMovieId();
            Set<Integer> topParents = topParentMap.get(supParentId);

            for (IndividualSupplementalHollow sup : sups._getSupplementals()) {
                int supId = (int) sup._getMovieId();
                if (topParents == null) {
                    trackTopParent(supId, supParentId);
                } else {
                    trackTopParents(supId, topParents);
                }
            }
        }
    }

    private void expandFastlaneIds() {
        if(ctx.getFastlaneIds() != null) {
            Set<Integer> expandedFastlaneIds = new HashSet<>();

            for(Integer i : ctx.getFastlaneIds()) {
                Set<ShowSeasonEpisodeHollow> displaySets = displaySetsByVideoId.get(i);
                if(displaySets != null && !displaySets.isEmpty())
                    expandedFastlaneIds.addAll(getBagOfVideoIds(displaySets));
                else
                    expandedFastlaneIds.add(i);
            }

            ctx.setFastlaneIds(expandedFastlaneIds);
        }
    }

    private void findProcessGroups() {
        Set<Integer> fastlaneIds = ctx.getFastlaneIds();
        Set<Integer> potentialOrphans = new HashSet<Integer>();
        Set<Integer> alreadyAddedTopNodes = new HashSet<Integer>();
        Set<Integer> topNodesToDropOnFloor = getTopNodesToDropOnFloor();

        for(VideoGeneralHollow videoGeneral : api.getAllVideoGeneralHollow()) {
            Integer videoId = Integer.valueOf((int)videoGeneral._getVideoId());

            if(fastlaneIds != null && !fastlaneIds.contains(videoId))
                continue;

            if (alreadyAddedTopNodes.contains(videoId)) continue;

            if (VideoNodeType.isStandaloneOrTopNode(VideoNodeType.of(videoGeneral._getVideoType()._getValue()))) {
                Set<ShowSeasonEpisodeHollow> displaySets = displaySetsByVideoId.get(videoId);

                if(displaySets == null) {
                    if(!topNodesToDropOnFloor.contains(videoId))
                        processGroups.add(Collections.singleton(new VideoHierarchyGroup(videoId)));
                    else
                        ctx.getLogger().warn(TransformerLogTag.DroppedTopNodeOnFloor, "Dropped hierarchy for {} on floor", videoId);
                } else {
                    Map<Integer, VideoHierarchyGroup> theseGroupsByTopNode = new HashMap<>();

                    theseGroupsByTopNode.put(videoId, new VideoHierarchyGroup(videoId));

                    for(ShowSeasonEpisodeHollow displaySet : displaySets) {
                        int thisDisplaySetTopNode = (int)displaySet._getMovieId();
                        VideoHierarchyGroup topNodeProcessGroup = theseGroupsByTopNode.get(thisDisplaySetTopNode);
                        if(topNodeProcessGroup == null) {
                            topNodeProcessGroup = new VideoHierarchyGroup(thisDisplaySetTopNode);
                            theseGroupsByTopNode.put(thisDisplaySetTopNode, topNodeProcessGroup);
                            alreadyAddedTopNodes.add(thisDisplaySetTopNode);
                        }
                        topNodeProcessGroup.addShowSeasonEpisodeHollow(displaySet);
                    }

                    Set<VideoHierarchyGroup> groups = new HashSet<VideoHierarchyGroup>();

                    for (Map.Entry<Integer, VideoHierarchyGroup> entry : theseGroupsByTopNode.entrySet()) {
                        if(!topNodesToDropOnFloor.contains(entry.getKey()))
                            groups.add(entry.getValue());
                        else
                            ctx.getLogger().warn(TransformerLogTag.DroppedTopNodeOnFloor, "Dropped hierarchy for {} on floor", entry.getKey());
                    }

                    if(!groups.isEmpty())
                        processGroups.add(groups);
                }

                alreadyAddedTopNodes.add(videoId);
            } else {
                // NOTE: track potential orphans - Needed for Data Parity with legacy pipeline such as PackageData, etc
                potentialOrphans.add(videoId);
            }
        }

        // If this is the fastlane, don't worry about orphans.
        if(fastlaneIds != null)
            return;

        // NOTE: TODO need to follow up with beehive to make sure this are part of VideoGeneral
        if (ctx.getConfig().shouldProcessExtraNonVideoGeneralVideoIds()) {
            // Make sure to include videoIds from DeployablePackage feed - for PackageData parity
            potentialOrphans.addAll(deployablePackagesFetcher.getAllMovieIds());

            // Make sure to include videoIds from l10n feeds - for L10n Parity
            for (EpisodesHollow item : api.getAllEpisodesHollow()) {
                potentialOrphans.add((int) item._getMovieId());
            }
            for (MovieRatingsHollow item : api.getAllMovieRatingsHollow()) {
                potentialOrphans.add((int) item._getMovieId());
            }
            for (StoriesSynopsesHollow item : api.getAllStoriesSynopsesHollow()) {
                potentialOrphans.add((int) item._getMovieId());
            }
        }

        // Make sure orphans don't get dropped from grouping - Needed for Data Parity with legacy pipeline such as PackageData, etc
        for (int videoId : potentialOrphans) {
            if (alreadyAddedTopNodes.contains(videoId)) continue;

            Set<Integer> topParents = topParentMap.get(videoId);
            if (topParents == null) {
                if(!topNodesToDropOnFloor.contains(videoId))
                    processGroups.add(Collections.singleton(new VideoHierarchyGroup(videoId)));
                else
                    ctx.getLogger().warn(TransformerLogTag.DroppedTopNodeOnFloor, "Dropped hierarchy for {} on floor", videoId);
            } else {
                for (int parentId : topParents) {
                    if (alreadyAddedTopNodes.contains(parentId)) continue;

                    if(!topNodesToDropOnFloor.contains(parentId))
                        processGroups.add(Collections.singleton(new VideoHierarchyGroup(parentId)));
                    else
                        ctx.getLogger().warn(TransformerLogTag.DroppedTopNodeOnFloor, "Dropped hierarchy for {} on floor", parentId);
                }
            }
        }
    }

    private void trackTopParent(int videoId, int parentId) {
        Set<Integer> parents = topParentMap.get(videoId);
        if (parents == null) {
            parents = new HashSet<>();
            topParentMap.put(videoId, parents);
        }

        parents.add(parentId);
    }

    private void trackTopParents(int videoId, Set<Integer> newParents) {
        Set<Integer> parents = topParentMap.get(videoId);
        if (parents == null) {
            parents = new HashSet<>();
            topParentMap.put(videoId, parents);
        }

        parents.addAll(newParents);
    }


    private Set<Integer> getBagOfVideoIds(Set<ShowSeasonEpisodeHollow> showSeasonEpisodes) {
        Set<Integer> setOfIds = new HashSet<>();

        for(ShowSeasonEpisodeHollow showSeasonEpisode : showSeasonEpisodes) {
            setOfIds.add((int)showSeasonEpisode._getMovieId());

            for(SeasonHollow season : showSeasonEpisode._getSeasons()) {
                setOfIds.add((int)season._getMovieId());

                for(EpisodeHollow episode : season._getEpisodes()) {
                    setOfIds.add((int)episode._getMovieId());
                }
            }
        }

        return setOfIds;
    }

    private Set<Integer> getTopNodesToDropOnFloor() {
        String csv = ctx.getConfig().getDropTopNodesOnFloor();
        if (csv == null || csv.trim().isEmpty())
            return Collections.emptySet();

        Set<Integer> set = new HashSet<Integer>();
        for(String id : csv.split(",")) {
            try {
                set.add(Integer.parseInt(id.trim()));
            } catch(Exception e) {
                ctx.getLogger().error(TransformerLogTag.DroppedTopNodeOnFloor, "Unable to parse top node ID: {}", id);
            }
        }

        return set;
    }

    public static class VideoHierarchyGroup {
        private final int topParentId;
        private final Set<ShowSeasonEpisodeHollow> showSeasonEpisodes;

        public VideoHierarchyGroup(int topNodeId) {
            this.topParentId = topNodeId;
            this.showSeasonEpisodes = new HashSet<ShowSeasonEpisodeHollow>();
        }

        private void addShowSeasonEpisodeHollow(ShowSeasonEpisodeHollow hierarchy) {
            this.showSeasonEpisodes.add(hierarchy);
        }

        public int getTopParentId() {
            return topParentId;
        }

        public Set<ShowSeasonEpisodeHollow> getShowSeasonEpisodes() {
            return showSeasonEpisodes;
        }

        @Override
        public String toString() {
            return "VideoHierarchyGroup: topParentId=" + topParentId;
        }
    }
}
