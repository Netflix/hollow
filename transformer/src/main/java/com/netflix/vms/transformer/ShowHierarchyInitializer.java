package com.netflix.vms.transformer;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.util.IntList;
import com.netflix.vms.transformer.hollowinput.CountryVideoDisplaySetHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.IndividualTrailerHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseTrailerHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.TrailerHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDisplaySetHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeMediaHollow;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShowHierarchyInitializer {

    private final VMSHollowInputAPI videoAPI;
    private final HollowPrimaryKeyIndex supplementalIndex;
    private final HollowHashIndex videoTypeCountryIndex;
    private final HollowPrimaryKeyIndex videoRightsIndex;
    private final HollowHashIndex rolloutVideoTypeIndex;
    private final Set<Integer> supplementalIds;
    private final TransformerContext ctx;

    public ShowHierarchyInitializer(VMSHollowInputAPI videoAPI, VMSTransformerIndexer indexer, TransformerContext ctx) {
        this.videoAPI = videoAPI;
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
        this.videoTypeCountryIndex = indexer.getHashIndex(IndexSpec.VIDEO_TYPE_COUNTRY);
        this.videoRightsIndex = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_RIGHTS);
        this.rolloutVideoTypeIndex = indexer.getHashIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);
        this.ctx = ctx;

        this.supplementalIds = findAllSupplementalVideoIds(videoAPI);
    }

    private Set<Integer> findAllSupplementalVideoIds(VMSHollowInputAPI videoAPI) {
        Set<Integer> ids = new HashSet<Integer>();

        for(IndividualTrailerHollow supplemental : videoAPI.getAllIndividualTrailerHollow())
            ids.add((int)supplemental._getMovieId());

        for(RolloutPhaseTrailerHollow rolloutTrailer : videoAPI.getAllRolloutPhaseTrailerHollow())
            ids.add((int)rolloutTrailer._getTrailerMovieId());

        return ids;
    }

    public Map<String, ShowHierarchy> getShowHierarchiesByCountry(VideoDisplaySetHollow displaySet) {
        long topNodeId = displaySet._getTopNodeId();
        
        if(supplementalIds.contains((int)topNodeId))
            return null;

        Map<String, ShowHierarchy> showHierarchiesByCountry = new HashMap<String, ShowHierarchy>();
        Map<ShowHierarchy, ShowHierarchy> uniqueShowHierarchies = new HashMap<ShowHierarchy, ShowHierarchy>();

        for(CountryVideoDisplaySetHollow set : displaySet._getSets()) {
            String countryCode = set._getCountryCode()._getValue();

            if(!isTopNodeIncluded(topNodeId, countryCode))
                continue;

            boolean isStandalone = set._getSetType()._isValueEqual("Standalone");
            boolean isShow = set._getSetType()._isValueEqual("std_show");
            if(!isStandalone && !isShow)
                continue;

            ShowHierarchy showHierarchy = new ShowHierarchy((int)topNodeId, isStandalone, set, countryCode, this);
            ShowHierarchy canonicalHierarchy = uniqueShowHierarchies.get(showHierarchy);
            if(canonicalHierarchy == null) {
                canonicalHierarchy = showHierarchy;
                uniqueShowHierarchies.put(showHierarchy, canonicalHierarchy);
            }

            showHierarchiesByCountry.put(countryCode, canonicalHierarchy);
        }

        if(showHierarchiesByCountry.isEmpty())
            return null;

        return showHierarchiesByCountry;
    }



    boolean isTopNodeIncluded(long videoId, String countryCode) {
        if(!isContentApproved(videoId, countryCode))
            return false;

        if(isGoLiveOrHasFirstDisplayDate(videoId, countryCode) || isDVDData(videoId, countryCode) || hasCurrentOrFutureRollout(videoId, "DISPLAY_PAGE", countryCode))
            return true;

        return false;
    }

    boolean isChildNodeIncluded(long videoId, String countryCode) {
        if(!isContentApproved(videoId, countryCode))
            return false;

        if(isGoLiveOrHasFirstDisplayDate(videoId, countryCode) || hasCurrentOrFutureRollout(videoId, "DISPLAY_PAGE", countryCode))
            return true;

        return false;
    }

    boolean isDVDData(long videoId, String countryCode) {
        HollowHashIndexResult queryResult = videoTypeCountryIndex.findMatches(videoId, countryCode);

        int ordinal = queryResult.iterator().next();

        VideoTypeDescriptorHollow countryType = videoAPI.getVideoTypeDescriptorHollow(ordinal);
        if(countryType._getIsCanon() || countryType._getIsExtended())
            return true;

        for(VideoTypeMediaHollow media : countryType._getMedia()) {
            if(media._getValue()._isValueEqual("Plastic"))
                return true;
        }

        return false;
    }

    boolean isContentApproved(long videoId, String countryCode) {
        HollowHashIndexResult queryResult = videoTypeCountryIndex.findMatches(videoId, countryCode);

        if(queryResult == null || queryResult.numResults() == 0)
            return false;

        int ordinal = queryResult.iterator().next();

        VideoTypeDescriptorHollow countryType = videoAPI.getVideoTypeDescriptorHollow(ordinal);
        return countryType._getIsContentApproved();
    }

    boolean isGoLiveOrHasFirstDisplayDate(long videoId, String countryCode) {
        int rightsOrdinal = videoRightsIndex.getMatchingOrdinal(videoId, countryCode);

        if(rightsOrdinal == -1)
            return false;


        VideoRightsHollow videoRights = videoAPI.getVideoRightsHollow(rightsOrdinal);
        if(videoRights._getFlags()._getGoLive())
            return true;

        if(videoRights._getFlags()._getFirstDisplayDate() != null)
            return true;

        Set<VideoRightsWindowHollow> windowSet = videoRights._getRights()._getWindows();

        if(!windowSet.isEmpty())
            return true;

        videoRights._getRights()._getContracts();

        return false;
    }

    boolean hasCurrentOrFutureRollout(long videoId, String rolloutType, String country) {
        HollowHashIndexResult result = rolloutVideoTypeIndex.findMatches(videoId, rolloutType);

        if(result == null)
            return false;

        HollowOrdinalIterator iter = result.iterator();

        int rolloutOrdinal = iter.next();
        while(rolloutOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            RolloutHollow rollout = videoAPI.getRolloutHollow(rolloutOrdinal);
    
            for(RolloutPhaseHollow phase : rollout._getPhases()) {
                for(Map.Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phase._getWindows().entrySet()) {
                    if(entry.getKey()._isValueEqual(country)) {
                        return entry.getValue()._getEndDate()._getValue() >= ctx.getNowMillis();
                    }
                }
            }
            
            rolloutOrdinal = iter.next();
        }

        return false;
    }

    void addSupplementalVideos(long videoId, String countryCode, IntList toList) {
        int supplementalsOrdinal = supplementalIndex.getMatchingOrdinal(videoId);

        if(supplementalsOrdinal == -1)
            return;

        TrailerHollow supplementals = videoAPI.getTrailerHollow(supplementalsOrdinal);
        for(IndividualTrailerHollow supplemental : supplementals._getTrailers()) {
            int supplementalId = (int)supplemental._getMovieId();
            if(isChildNodeIncluded(supplementalId, countryCode))
                toList.add(supplementalId);
        }
    }

}
