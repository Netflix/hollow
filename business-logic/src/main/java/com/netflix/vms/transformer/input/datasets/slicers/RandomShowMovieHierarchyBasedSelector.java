package com.netflix.vms.transformer.input.datasets.slicers;

//TODO: enable me once we can turn on the new data set including follow vip functionality
//import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.DatasetIdentifier.OSCAR;

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
import com.netflix.vms.transformer.input.api.gen.oscar.Movie;
import com.netflix.vms.transformer.input.datasets.OscarDataset;
import com.netflix.vms.transformer.modules.ModuleDataSourceTransitionUtil;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


//
// This class can find videoIds belonging to topNodeIds in inputs for input slicing. This is an alternative
// to the existing implementation using GlobalVideoBasedSelector which uses the output blob to find videoIds that
// belong to a topNodeId. Depending on what is being tested, one might be more suitable over the other.
//
public class RandomShowMovieHierarchyBasedSelector {
    private HollowReadStateEngine stateEngine;
    private VMSHollowInputAPI api;
    private HollowPrimaryKeyIndex videoGeneralIdx;
    private HollowPrimaryKeyIndex supplementalIdx;
    private HollowHashIndex showSeasonEpisodeHashIdx;
//    private final OscarDataset oscarDataset;

    public RandomShowMovieHierarchyBasedSelector(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        api = new VMSHollowInputAPI(stateEngine);
        videoGeneralIdx = new HollowPrimaryKeyIndex(stateEngine, "VideoGeneral", "videoId");
        supplementalIdx = new HollowPrimaryKeyIndex(stateEngine, "Supplementals", "movieId");
        showSeasonEpisodeHashIdx = new HollowHashIndex(stateEngine, "ShowSeasonEpisode", "", "movieId");
        // TODO:  the below won't work-- the slicer that calls this is not compatible with the need for multiple stateEngines as input, will probably back out of converting this class and leave it as-is
//        this.oscarDataset = indexer.getHashIndex(OSCAR);
    }

    public Set<Integer> findRandomVideoIds(int numberOfRandomTopNodesToInclude, int[] specificIdsToInclude) {
//        if (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral()) {
//            return findRandomVideoIdsOscar(numberOfRandomTopNodesToInclude,specificIdsToInclude);
//        }
        return findRandomVideoIdsVideoGeneral(numberOfRandomTopNodesToInclude,specificIdsToInclude);
    }

    public Set<Integer> findRandomVideoIdsVideoGeneral(int numberOfRandomTopNodesToInclude, int[] specificIdsToInclude) {
        Random rand = new Random(1000);
        Set<Long> topNodeVideoIds = new HashSet<Long>();
        Set<Integer> allVideoIds = new HashSet<Integer>();

        int vGeneralMaxOrdinal = stateEngine.getTypeState("VideoGeneral").maxOrdinal();
        while (topNodeVideoIds.size() < numberOfRandomTopNodesToInclude) {
            int randomOrdinal = rand.nextInt(vGeneralMaxOrdinal + 1);
            if (ordinalIsPopulated("VideoGeneral", randomOrdinal)) {
                VideoGeneralHollow vid = api.getVideoGeneralHollow(randomOrdinal);
                addIdsBasedOnVideoGeneral(topNodeVideoIds, allVideoIds, vid._getVideoId(), VideoNodeType.of(vid._getVideoType()._getValue()), true);
            }
        }


        for (int videoId : specificIdsToInclude) {
            allVideoIds.add(videoId);

            int vOrdinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
            if (vOrdinal == -1) continue;

            VideoGeneralHollow vid = api.getVideoGeneralHollow(vOrdinal);
            addIdsBasedOnVideoGeneral(topNodeVideoIds, allVideoIds, vid._getVideoId(), VideoNodeType.of(vid._getVideoType()._getValue()), false);
        }

        return allVideoIds;
    }

//    public Set<Integer> findRandomVideoIdsOscar(int numberOfRandomTopNodesToInclude, int[] specificIdsToInclude) {
//        Random rand = new Random(1000);
//        Set<Long> topNodeVideoIds = new HashSet<Long>();
//        Set<Integer> allVideoIds = new HashSet<Integer>();
//
//        int maxMovieOrdinal = oscarDataset.maxMovieOrdinal();
//        while (topNodeVideoIds.size() < numberOfRandomTopNodesToInclude) {
//            int randomOrdinal = rand.nextInt(maxMovieOrdinal + 1);
//            Movie movie = oscarDataset.getAPI().getMovie(randomOrdinal);
//            if (movie != null) {
//                addIdsBasedOnVideoGeneral(topNodeVideoIds, allVideoIds, movie.getMovieId(), VideoNodeType.of(movie.getType().get_name()), true);
//            }
//        }
//
//
//        for (int videoId : specificIdsToInclude) {
//            allVideoIds.add(videoId);
//
//            Movie movie = oscarDataset.getAPI().getMovie(videoId);
//            if (movie == null) continue;
//            addIdsBasedOnVideoGeneral(topNodeVideoIds, allVideoIds, movie.getMovieId(), VideoNodeType.of(movie.getType().get_name()), true);
//        }
//
//        return allVideoIds;
//    }

    private void addIdsBasedOnVideoGeneral(Set<Long> topNodeVideoIds, Set<Integer> allVideoIds, long videoId, VideoNodeType nodeType, boolean isEnforceTopNode) {
        boolean isTopNode = VideoNodeType.isTopNode(nodeType);
        if (isEnforceTopNode && !isTopNode) return;

        if (isTopNode) topNodeVideoIds.add(videoId);
        addVideoAndAssociatedSupplementals(videoId, allVideoIds);
        if (!VideoNodeType.isStandalone(nodeType)) {
            // need to add show members
            HollowHashIndexResult matches = showSeasonEpisodeHashIdx.findMatches(videoId);
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

    private void addVideoAndAssociatedSupplementals(long videoId, Set<Integer> allVideoIds) {
        allVideoIds.add((int)videoId);
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