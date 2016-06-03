package com.netflix.vms.transformer;

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
import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeMediaHollow;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.HashMap;
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
    }

    public Map<String, ShowHierarchy> getShowHierarchiesByCountry(VideoGeneralHollow videoGeneral) {
        VideoNodeType nodeType = VideoNodeType.of(videoGeneral._getVideoType()._getValue());
        if (!VideoNodeType.isStandaloneOrTopNode(nodeType)) return null;

        long topNodeId = videoGeneral._getVideoId();
        boolean isStandalone = VideoNodeType.isStandalone(nodeType);
        Map<String, ShowHierarchy> showHierarchiesByCountry = new HashMap<String, ShowHierarchy>();
        Map<ShowHierarchy, ShowHierarchy> uniqueShowHierarchies = new HashMap<ShowHierarchy, ShowHierarchy>();

        HollowHashIndexResult matches = showSeasonEpisodeIndex.findMatches(topNodeId);
        if (matches != null) {
            HollowOrdinalIterator iter = matches.iterator();
            int showSeasonEpisodeOrdinal = iter.next();
            while (showSeasonEpisodeOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                ShowSeasonEpisodeHollow showSeasonEpisode = api.getShowSeasonEpisodeHollow(showSeasonEpisodeOrdinal);

                for (ISOCountryHollow country : showSeasonEpisode._getCountryCodes()) {
                    String countryCode = country._getValue();
                    if (!isTopNodeIncluded(topNodeId, countryCode))
                        continue;

                    ShowHierarchy showHierarchy = new ShowHierarchy((int) topNodeId, isStandalone, showSeasonEpisode, countryCode, this);
                    ShowHierarchy canonicalHierarchy = uniqueShowHierarchies.get(showHierarchy);
                    if (canonicalHierarchy == null) {
                        canonicalHierarchy = showHierarchy;
                        uniqueShowHierarchies.put(showHierarchy, canonicalHierarchy);
                    }

                    showHierarchiesByCountry.put(countryCode, canonicalHierarchy);
                }

                showSeasonEpisodeOrdinal = iter.next();
            }
        }

        // Add standalones for countries that are not defined in hierarchy feed (Show,Movie,Supplemental)
        int videoTypeOrdinal = videoTypeIndex.getMatchingOrdinal(topNodeId);
        VideoTypeHollow videoType = api.getVideoTypeHollow(videoTypeOrdinal);
        for (VideoTypeDescriptorHollow countryType : videoType._getCountryInfos()) {
            String countryCode = countryType._getCountryCode()._getValue();
            if (showHierarchiesByCountry.containsKey(countryCode)) continue;

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

        if(showHierarchiesByCountry.isEmpty())
            return null;

        return showHierarchiesByCountry;
    }

	private boolean isSupportedCountry(String countryCode) {
		return ctx.getOctoberSkyData().getSupportedCountries().contains(countryCode);
	}



    boolean isTopNodeIncluded(long videoId, String countryCode) {
    	if(!isSupportedCountry(countryCode))
    		return false;
    	
        if(!isContentApproved(videoId, countryCode))
            return false;

        if(isGoLiveOrHasFirstDisplayDate(videoId, countryCode))
            return true;

        if (isDVDData(videoId, countryCode))
            return true;

        if (hasCurrentOrFutureRollout(videoId, "DISPLAY_PAGE", countryCode))
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
                        if (entry.getValue()._getEndDate() >= ctx.getNowMillis()) {
                            return true;
                        }
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
