package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.GlobalVideoHollow;
import com.netflix.vms.generated.notemplate.SupplementalVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoCollectionsDataHollow;
import com.netflix.vms.generated.notemplate.VideoEpisodeHollow;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.generated.notemplate.VideoNodeTypeHollow;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GlobalVideoBasedSelector {
    private HollowReadStateEngine stateEngine;
    private VMSRawHollowAPI api;
    private HollowPrimaryKeyIndex globalVideoIdx;
    private HollowPrimaryKeyIndex completeVideoPrimaryKeyIdx;
    private HollowHashIndex completeVideoHashIdx;

    public GlobalVideoBasedSelector(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        api = new VMSRawHollowAPI(stateEngine);
        globalVideoIdx = new HollowPrimaryKeyIndex(stateEngine, "GlobalVideo", "completeVideo.id.value");
        completeVideoHashIdx = new HollowHashIndex(stateEngine, "CompleteVideo", "", "id.value");
        completeVideoPrimaryKeyIdx = new HollowPrimaryKeyIndex(stateEngine, "CompleteVideo", "id.value", "country.id");
    }

    public Set<Integer> findVideosForTopNodes(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {

        return findRandomVideoIds(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }

    public Set<Integer> findRandomVideoIds(int numberOfRandomTopNodesToInclude, int[] specificTopNodeIdsToInclude) {
        Random rand = new Random(1000);
        Set<Integer> topNodeVideoIds = new HashSet<>();
        Set<Integer> allVideoIds = new HashSet<>();

        int maxGlobalVideoOrdinal = stateEngine.getTypeState("GlobalVideo").maxOrdinal();
        while (topNodeVideoIds.size() < numberOfRandomTopNodesToInclude) {
            int randomOrdinal = rand.nextInt(maxGlobalVideoOrdinal + 1);

            if (ordinalIsPopulated("GlobalVideo", randomOrdinal)) {
                GlobalVideoHollow vid = api.getGlobalVideoHollow(randomOrdinal);

                addIdsBasedOnGlobalVideo(topNodeVideoIds, allVideoIds, vid);
            }
        }

        for (int videoId : specificTopNodeIdsToInclude) {
            int gvOrdinal = globalVideoIdx.getMatchingOrdinal(videoId);
            if (gvOrdinal == -1) {
                throw new RuntimeException("Could not find GlobalVideo for id " + videoId);
            }

            GlobalVideoHollow vid = api.getGlobalVideoHollow(gvOrdinal);
            addIdsBasedOnGlobalVideo(topNodeVideoIds, allVideoIds, vid);
        }

        return allVideoIds;
    }

    private void addIdsBasedOnGlobalVideo(Set<Integer> topNodeVideoIds, Set<Integer> allVideoIds, GlobalVideoHollow vid) {
        HollowHashIndexResult completeVideos = completeVideoHashIdx.findMatches(vid._getCompleteVideo()._getId()._getValueBoxed());

        HollowOrdinalIterator completeVideoIterator = completeVideos.iterator();
        int completeVideoOrdinal = completeVideoIterator.next();

        while (completeVideoOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            CompleteVideoHollow completeVideo = api.getCompleteVideoHollow(completeVideoOrdinal);
            String countryCode = completeVideo._getCountry()._getId();
            VideoCollectionsDataHollow videoCollectionsData = completeVideo._getData()._getFacetData()._getVideoCollectionsData();
            VideoNodeTypeHollow nodeType = videoCollectionsData._getNodeType();
            if (nodeType._isValueEqual("SHOW") || nodeType._isValueEqual("MOVIE")) {
                Integer videoId = vid._getCompleteVideo()._getId()._getValueBoxed();
                topNodeVideoIds.add(videoId);
                allVideoIds.add(videoId);

                for (VideoEpisodeHollow episode : videoCollectionsData._getVideoEpisodes()) {
                    int episodeId = episode._getDeliverableVideo()._getValueBoxed();
                    allVideoIds.add(episodeId);
                    addAllSupplementalVideoIds(episodeId, countryCode, allVideoIds);
                }

                for (VideoHollow season : videoCollectionsData._getShowChildren()) {
                    int seasonId = season._getValueBoxed();
                    allVideoIds.add(seasonId);
                    addAllSupplementalVideoIds(seasonId, countryCode, allVideoIds);
                }

                addAllSupplementalVideoIds(videoId, countryCode, allVideoIds);
            }

            completeVideoOrdinal = completeVideoIterator.next();
        }
    }

    private void addAllSupplementalVideoIds(int videoId, String countryCode, Set<Integer> toSet) {
        int completeVideoOrdinal = completeVideoPrimaryKeyIdx.getMatchingOrdinal(videoId, countryCode);
        CompleteVideoHollow vid = api.getCompleteVideoHollow(completeVideoOrdinal);

        VideoCollectionsDataHollow videoCollectionsData = vid._getData()._getFacetData()._getVideoCollectionsData();

        for (SupplementalVideoHollow supplemental : videoCollectionsData._getSupplementalVideos()) {
            toSet.add(supplemental._getId()._getValueBoxed());
        }
    }

    private boolean ordinalIsPopulated(String type, int ordinal) {
        return populatedOrdinals(type).get(ordinal);
    }

    private BitSet populatedOrdinals(String type) {
        return stateEngine.getTypeState(type).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }
}