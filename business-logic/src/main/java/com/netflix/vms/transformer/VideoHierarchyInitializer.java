package com.netflix.vms.transformer;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.IntList;
import com.netflix.vms.transformer.VideoHierarchyGrouper.VideoHierarchyGroup;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.gatekeeper2migration.GatekeeperStatusRetriever;
import com.netflix.vms.transformer.hollowinput.EpisodeHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.SeasonHollow;
import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeHollow;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.util.DVDCatalogUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to create {@link VideoHierarchy} objects.
 */
public class VideoHierarchyInitializer {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex supplementalIndex;
    private final HollowPrimaryKeyIndex videoTypeIndex;
    private final HollowPrimaryKeyIndex videoGeneralIndex;
    private final HollowHashIndex showSeasonEpisodeIndex;
    private final HollowHashIndex videoTypeCountryIndex;
    private final GatekeeperStatusRetriever statusRetriever;
    private final HollowHashIndex rolloutVideoTypeIndex;
    private final TransformerContext ctx;

    public VideoHierarchyInitializer(VMSHollowInputAPI api, VMSTransformerIndexer indexer, GatekeeperStatusRetriever statusRetriever, TransformerContext ctx) {
        this.api = api;
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
        this.videoTypeIndex = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_TYPE);
        this.videoGeneralIndex = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
        this.showSeasonEpisodeIndex = indexer.getHashIndex(IndexSpec.SHOW_SEASON_EPISODE);
        this.videoTypeCountryIndex = indexer.getHashIndex(IndexSpec.VIDEO_TYPE_COUNTRY);
        this.statusRetriever = statusRetriever;
        this.rolloutVideoTypeIndex = indexer.getHashIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);
        this.ctx = ctx;
    }


    public Map<String, Set<VideoHierarchy>> getShowHierarchiesByCountry(Set<VideoHierarchyGroup> processGroup, Set<Integer> droppedIds) {
        Map<String, Set<VideoHierarchy>> showHierarchiesByCountry = new HashMap<>();

        for (VideoHierarchyGroup videoGroup : processGroup) {
            boolean isTopNode = false;
            boolean isStandalone = false;
            long topParentId = videoGroup.getTopParentId();

            int videoGeneralOrdinal = videoGeneralIndex.getMatchingOrdinal(topParentId);
            if (videoGeneralOrdinal != -1) {
                VideoGeneralHollow videoGeneral = api.getVideoGeneralHollow(videoGeneralOrdinal);
                VideoNodeType nodeType = VideoNodeType.of(videoGeneral._getVideoType()._getValue());
                isTopNode = VideoNodeType.isTopNode(nodeType);
                isStandalone = VideoNodeType.isStandalone(nodeType);
            }

            if (!isTopNode) { // track that non-topNode video are being dropped
                addVideoAndAssociatedSupplementals(topParentId, droppedIds);
                continue;
            }

            Map<VideoHierarchy, VideoHierarchy> uniqueShowHierarchies = new HashMap<VideoHierarchy, VideoHierarchy>();

            HollowHashIndexResult matches = showSeasonEpisodeIndex.findMatches(topParentId);
            if (matches != null) {
                HollowOrdinalIterator iter = matches.iterator();
                int showSeasonEpisodeOrdinal = iter.next();
                while (showSeasonEpisodeOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    ShowSeasonEpisodeHollow showSeasonEpisode = api.getShowSeasonEpisodeHollow(showSeasonEpisodeOrdinal);

                    for (ISOCountryHollow country : showSeasonEpisode._getCountryCodes()) {
                        String countryCode = country._getValue();

                        if (!isTopNodeIncluded(topParentId, countryCode)) {
                            addShowAndAllChildren(showSeasonEpisode, droppedIds);
                            continue;
                        }

                        VideoHierarchy showHierarchy = new VideoHierarchy(ctx, (int) topParentId, isStandalone, showSeasonEpisode, countryCode, this);
                        droppedIds.addAll(showHierarchy.getDroppedIds());

                        VideoHierarchy canonicalHierarchy = uniqueShowHierarchies.get(showHierarchy);
                        if (canonicalHierarchy == null) {
                            canonicalHierarchy = showHierarchy;
                            uniqueShowHierarchies.put(showHierarchy, canonicalHierarchy);
                        }

                        Set<VideoHierarchy> hierarchies = showHierarchiesByCountry.get(countryCode);
                        if(hierarchies == null) {
                            hierarchies = new HashSet<>();
                            showHierarchiesByCountry.put(countryCode, hierarchies);
                        }
                        hierarchies.add(canonicalHierarchy);
                    }

                    showSeasonEpisodeOrdinal = iter.next();
                }
            }

            // Add standalones for countries that are not defined in hierarchy feed (Show,Movie,Supplemental)
            int videoTypeOrdinal = videoTypeIndex.getMatchingOrdinal(topParentId);
            VideoTypeHollow videoType = api.getVideoTypeHollow(videoTypeOrdinal);
            if (videoTypeOrdinal == -1 || videoType._getCountryInfos().isEmpty()) {
                addVideoAndAssociatedSupplementals(topParentId, droppedIds);
            } else {
                for (VideoTypeDescriptorHollow countryType : videoType._getCountryInfos()) {
                    String countryCode = countryType._getCountryCode()._getValue();
                    if (showHierarchiesByCountry.containsKey(countryCode)) continue;

                    if (!isTopNodeIncluded(topParentId, countryCode)) {
                        addVideoAndAssociatedSupplementals(topParentId, droppedIds);
                        continue;
                    }

                    VideoHierarchy showHierarchy = new VideoHierarchy(ctx, (int) topParentId, isStandalone, null, countryCode, this);
                    droppedIds.addAll(showHierarchy.getDroppedIds());

                    VideoHierarchy canonicalHierarchy = uniqueShowHierarchies.get(showHierarchy);
                    if(canonicalHierarchy == null) {
                        canonicalHierarchy = showHierarchy;
                        uniqueShowHierarchies.put(showHierarchy, canonicalHierarchy);
                    }

                    HashSet<VideoHierarchy> set = new HashSet<VideoHierarchy>();
                    set.add(canonicalHierarchy);
                    showHierarchiesByCountry.put(countryCode, set);
                }
            }
        }

        if(showHierarchiesByCountry.isEmpty())
            return null;

        return showHierarchiesByCountry;
    }

    private boolean isSupportedCountry(String countryCode) {
        return ctx.getOctoberSkyData().getSupportedCountries().contains(countryCode);
    }


    boolean isTopNodeIncluded(long videoId, String countryCode) {
        if (!isSupportedCountry(countryCode))
            return false;

        if (!isContentApproved(videoId, countryCode))
            return false;

        if (isGoLiveOrHasFirstDisplayDate(videoId, countryCode))
            return true;

        if (DVDCatalogUtil.isVideoInDVDCatalog(api, videoTypeCountryIndex, videoId, countryCode))
            return true;

        if (hasCurrentOrFutureRollout(videoId, "DISPLAY_PAGE", countryCode))
            return true;

        return false;
    }

    /**
     * Returns true if this child node should appears in the transformer output.
     */
    boolean isChildNodeIncluded(long videoId, String countryCode) {
        if (!isContentApproved(videoId, countryCode) || !isInVideoGeneral(videoId)) {
            return false;
        }
        if (isGoLiveOrHasFirstDisplayDate(videoId, countryCode) || hasCurrentOrFutureRollout(videoId, "DISPLAY_PAGE", countryCode))
            return true;
        return false;
    }


    boolean isContentApproved(long videoId, String countryCode) {
        HollowHashIndexResult queryResult = videoTypeCountryIndex.findMatches(videoId, countryCode);
        if (queryResult == null || queryResult.numResults() == 0)
            return false;

        int ordinal = queryResult.iterator().next();
        VideoTypeDescriptorHollow countryType = api.getVideoTypeDescriptorHollow(ordinal);
        return countryType != null;
    }

    boolean isGoLiveOrHasFirstDisplayDate(long videoId, String countryCode) {
        StatusHollow videoStatus = statusRetriever.getStatus(videoId, countryCode);
        if(videoStatus == null)
            return false;

        if (videoStatus._getFlags()._getGoLive())
            return true;

        if (videoStatus._getFlags()._getFirstDisplayDate() != null)
            return true;

        ListOfRightsWindowHollow windows = videoStatus._getRights()._getWindows();
        if (!windows.isEmpty())
            return true;

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

    // return the added supplemental IDs
    Set<Integer> addSupplementalVideos(long videoId, String countryCode, IntList toList, Set<Integer> droppedIds) {
        int supplementalsOrdinal = supplementalIndex.getMatchingOrdinal(videoId);
        if(supplementalsOrdinal == -1)
            return Collections.emptySet();

        Set<Integer> addedSupplementals = new HashSet<>();
        SupplementalsHollow supplementals = api.getSupplementalsHollow(supplementalsOrdinal);
        for (IndividualSupplementalHollow supplemental : supplementals._getSupplementals()) {
            int supplementalId = (int)supplemental._getMovieId();
            if (isChildNodeIncluded(supplementalId, countryCode)) {
                toList.add(supplementalId);
                addedSupplementals.add(supplementalId);
            } else {
                droppedIds.add(supplementalId);
            }
        }
        return addedSupplementals;
    }

    void addShowAndAllChildren(ShowSeasonEpisodeHollow showSeasonEpisode, Set<Integer> toSet) {
        addVideoAndAssociatedSupplementals((int) showSeasonEpisode._getMovieId(), toSet);

        for (SeasonHollow season : showSeasonEpisode._getSeasons()) {
            addSeasonAndAllChildren(season, toSet);
        }
    }

    void addSeasonAndAllChildren(SeasonHollow season, Set<Integer> toSet) {
        addVideoAndAssociatedSupplementals((int) season._getMovieId(), toSet);
        for (EpisodeHollow episode : season._getEpisodes()) {
            addVideoAndAssociatedSupplementals((int) episode._getMovieId(), toSet);
        }
    }

    void addVideoAndAssociatedSupplementals(long videoId, Set<Integer> toSet) {
        toSet.add((int) videoId);

        int supOrdinal = supplementalIndex.getMatchingOrdinal(videoId);
        if (supOrdinal == -1) return;

        SupplementalsHollow sups = api.getSupplementalsHollow(supOrdinal);
        for (IndividualSupplementalHollow supplemental : sups._getSupplementals()) {
            toSet.add((int) supplemental._getMovieId());
        }
    }

    /**
     * Returns true if the specified videoId is in the VideoGeneral feed.
     */
    private boolean isInVideoGeneral(long videoId) {
        return videoGeneralIndex.getMatchingOrdinal(videoId) != -1;
    }
}
