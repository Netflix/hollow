package com.netflix.vms.transformer;

import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;

import com.netflix.vms.transformer.hollowinput.VideoTypeHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.util.IntList;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
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

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex supplementalIndex;
    private final HollowPrimaryKeyIndex videoTypeIndex;
    private final HollowHashIndex showSeasonEpisodeIndex;
    private final HollowHashIndex videoTypeCountryIndex;
    private final HollowPrimaryKeyIndex videoRightsIndex;
    private final HollowHashIndex rolloutVideoTypeIndex;
    private final Set<Integer> supplementalIds;
    private final TransformerContext ctx;

    public ShowHierarchyInitializer(VMSHollowInputAPI api, VMSTransformerIndexer indexer, TransformerContext ctx) {
        this.api = api;
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
        this.videoTypeIndex = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_TYPE);
        this.showSeasonEpisodeIndex = indexer.getHashIndex(IndexSpec.SHOW_SEASON_EPISODE);
        this.videoTypeCountryIndex = indexer.getHashIndex(IndexSpec.VIDEO_TYPE_COUNTRY);
        this.videoRightsIndex = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_RIGHTS);
        this.rolloutVideoTypeIndex = indexer.getHashIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);
        this.ctx = ctx;

        this.supplementalIds = findAllSupplementalVideoIds(api);
    }

    private Set<Integer> findAllSupplementalVideoIds(VMSHollowInputAPI api) {
        Set<Integer> ids = new HashSet<Integer>();

        for (IndividualSupplementalHollow supplemental : api.getAllIndividualSupplementalHollow())
            ids.add((int)supplemental._getMovieId());

        return ids;
    }

    public Map<String, ShowHierarchy> getShowHierarchiesByCountry(VideoGeneralHollow videoGeneral) {
        long topNodeId = videoGeneral._getVideoId();

        if(supplementalIds.contains((int)topNodeId))
            return null;

        boolean isStandalone = videoGeneral._getVideoType()._isValueEqual("Standalone");
        boolean isShow = videoGeneral._getVideoType()._isValueEqual("Show");
        if(!isStandalone && !isShow)
            return null;

        int videoTypeOrdinal = videoTypeIndex.getMatchingOrdinal(topNodeId);
        VideoTypeHollow videoType = api.getVideoTypeHollow(videoTypeOrdinal);

        Map<String, ShowHierarchy> showHierarchiesByCountry = new HashMap<String, ShowHierarchy>();
        Map<ShowHierarchy, ShowHierarchy> uniqueShowHierarchies = new HashMap<ShowHierarchy, ShowHierarchy>();

        if(isStandalone) {
            for(VideoTypeDescriptorHollow countryType : videoType._getCountryInfos()) {
                String countryCode = countryType._getCountryCode()._getValue();

                if(!isTopNodeIncluded(topNodeId, countryCode))
                    continue;

                ShowHierarchy showHierarchy = new ShowHierarchy((int)topNodeId, isStandalone, null, countryCode, this);
                ShowHierarchy canonicalHierarchy = uniqueShowHierarchies.get(showHierarchy);
                if(canonicalHierarchy == null) {
                    canonicalHierarchy = showHierarchy;
                    uniqueShowHierarchies.put(showHierarchy, canonicalHierarchy);
                }

                showHierarchiesByCountry.put(countryCode, canonicalHierarchy);
            }
        } else {
            HollowHashIndexResult matches = showSeasonEpisodeIndex.findMatches(topNodeId);
            if(matches != null) {
                HollowOrdinalIterator iter = matches.iterator();
                int showSeasonEpisodeOrdinal = iter.next();
                while(showSeasonEpisodeOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    ShowSeasonEpisodeHollow showSeasonEpisode = api.getShowSeasonEpisodeHollow(showSeasonEpisodeOrdinal);

                    for(ISOCountryHollow country : showSeasonEpisode._getCountryCodes()) {
                        String countryCode = country._getValue();

                        if(!isTopNodeIncluded(topNodeId, countryCode))
                            continue;

                        ShowHierarchy showHierarchy = new ShowHierarchy((int)topNodeId, isStandalone, showSeasonEpisode, countryCode, this);
                        ShowHierarchy canonicalHierarchy = uniqueShowHierarchies.get(showHierarchy);
                        if(canonicalHierarchy == null) {
                            canonicalHierarchy = showHierarchy;
                            uniqueShowHierarchies.put(showHierarchy, canonicalHierarchy);
                        }

                        showHierarchiesByCountry.put(countryCode, canonicalHierarchy);
                    }

                    showSeasonEpisodeOrdinal = iter.next();
                }
            }

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

        VideoTypeDescriptorHollow countryType = api.getVideoTypeDescriptorHollow(ordinal);
        if ("US".equals(countryCode) && countryType._getExtended())
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
        VideoTypeDescriptorHollow countryType = api.getVideoTypeDescriptorHollow(ordinal);
        return countryType != null;
    }

    boolean isGoLiveOrHasFirstDisplayDate(long videoId, String countryCode) {
        int rightsOrdinal = videoRightsIndex.getMatchingOrdinal(videoId, countryCode);

        if(rightsOrdinal == -1)
            return false;


        VideoRightsHollow videoRights = api.getVideoRightsHollow(rightsOrdinal);
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
            RolloutHollow rollout = api.getRolloutHollow(rolloutOrdinal);

            for(RolloutPhaseHollow phase : rollout._getPhases()) {
                for(Map.Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phase._getWindows().entrySet()) {
                    if(entry.getKey()._isValueEqual(country)) {
                        return entry.getValue()._getEndDate() >= ctx.getNowMillis();
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

        SupplementalsHollow supplementals = api.getSupplementalsHollow(supplementalsOrdinal);
        for (IndividualSupplementalHollow supplemental : supplementals._getSupplementals()) {
            int supplementalId = (int)supplemental._getMovieId();
            if(isChildNodeIncluded(supplementalId, countryCode))
                toList.add(supplementalId);
        }
    }

}
