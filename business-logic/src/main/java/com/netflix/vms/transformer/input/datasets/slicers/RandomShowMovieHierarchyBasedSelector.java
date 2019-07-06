package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.VideoNodeType;
import com.netflix.vms.transformer.hollowinput.EpisodeHollow;
import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;
import com.netflix.vms.transformer.hollowinput.SeasonHollow;
import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomShowMovieHierarchyBasedSelector {
    private HollowReadStateEngine stateEngine;
    private VMSHollowInputAPI api;
    private HollowPrimaryKeyIndex videoGeneralIdx;
    private HollowPrimaryKeyIndex supplementalIdx;
    private HollowHashIndex showSeasonEpisodeHashIdx;

    public RandomShowMovieHierarchyBasedSelector(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        api = new VMSHollowInputAPI(stateEngine);
        videoGeneralIdx = new HollowPrimaryKeyIndex(stateEngine, "VideoGeneral", "videoId");
        supplementalIdx = new HollowPrimaryKeyIndex(stateEngine, "Supplementals", "movieId");
        showSeasonEpisodeHashIdx = new HollowHashIndex(stateEngine, "ShowSeasonEpisode", "", "movieId");
    }

    public Set<Integer> findRandomVideoIds(int numberOfRandomTopNodesToInclude, int[] specificIdsToInclude) {
        Random rand = new Random(1000);
        Set<Integer> topNodeVideoIds = new HashSet<Integer>();
        Set<Integer> allVideoIds = new HashSet<Integer>();

        int vGeneralMaxOrdinal = stateEngine.getTypeState("VideoGeneral").maxOrdinal();
        while (topNodeVideoIds.size() < numberOfRandomTopNodesToInclude) {
            int randomOrdinal = rand.nextInt(vGeneralMaxOrdinal + 1);
            if (ordinalIsPopulated("VideoGeneral", randomOrdinal)) {
                VideoGeneralHollow vid = api.getVideoGeneralHollow(randomOrdinal);
                addIdsBasedOnVideoGeneral(topNodeVideoIds, allVideoIds, vid, true);
            }
        }


        for (int videoId : specificIdsToInclude) {
            allVideoIds.add(videoId);

            int vOrdinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
            if (vOrdinal == -1) continue;

            VideoGeneralHollow vid = api.getVideoGeneralHollow(vOrdinal);
            addIdsBasedOnVideoGeneral(topNodeVideoIds, allVideoIds, vid, false);
        }

        return allVideoIds;
    }

    private void addIdsBasedOnVideoGeneral(Set<Integer> topNodeVideoIds, Set<Integer> allVideoIds, VideoGeneralHollow vid, boolean isEnforceTopNode) {
        VideoNodeType nodeType = VideoNodeType.of(vid._getVideoType()._getValue());
        boolean isTopNode = VideoNodeType.isTopNode(nodeType);
        if (isEnforceTopNode && !isTopNode) return;

        int videoId = (int) vid._getVideoId();
        if (isTopNode) topNodeVideoIds.add(videoId);
        addVideoAndAssociatedSupplementals(videoId, allVideoIds);
        if (!VideoNodeType.isStandalone(nodeType)) {
            // need to add show members
            HollowHashIndexResult matches = showSeasonEpisodeHashIdx.findMatches(vid._getVideoId());
            if (matches == null) return;

            HollowOrdinalIterator videoIterator = matches.iterator();
            int videoOrdinal = videoIterator.next();
            while (videoOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                ShowSeasonEpisodeHollow showSeasonEpisode = api.getShowSeasonEpisodeHollow(videoOrdinal);
                for (SeasonHollow season : showSeasonEpisode._getSeasons()) {
                    addVideoAndAssociatedSupplementals((int) season._getMovieId(), allVideoIds);
                    for (EpisodeHollow episode : season._getEpisodes()) {
                        addVideoAndAssociatedSupplementals((int) episode._getMovieId(), allVideoIds);
                    }
                }

                videoOrdinal = videoIterator.next();
            }
        }
    }

    private void addVideoAndAssociatedSupplementals(int videoId, Set<Integer> allVideoIds) {
        allVideoIds.add(videoId);
        addAssociatedSupplementals(videoId, allVideoIds);
    }

    private void addAssociatedSupplementals(long videoId, Set<Integer> toSet) {
        int supOrdinal = supplementalIdx.getMatchingOrdinal(videoId);
        if (supOrdinal == -1) return;

        SupplementalsHollow sups = api.getSupplementalsHollow(supOrdinal);
        for (IndividualSupplementalHollow supplemental : sups._getSupplementals()) {
            toSet.add((int) supplemental._getMovieId());
        }
    }

    private boolean ordinalIsPopulated(String type, int ordinal) {
        return populatedOrdinals(type).get(ordinal);
    }

    private BitSet populatedOrdinals(String type) {
        return stateEngine.getTypeState(type).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }
}