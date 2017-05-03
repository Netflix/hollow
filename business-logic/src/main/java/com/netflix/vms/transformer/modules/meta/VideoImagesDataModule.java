package com.netflix.vms.transformer.modules.meta;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.InvalidImagesTerritoryCode;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.InvalidPhaseTagForArtwork;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.MissingLocaleForArtwork;
import static com.netflix.vms.transformer.modules.countryspecific.VMSAvailabilityWindowModule.ONE_THOUSAND_YEARS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.hollowinput.AbsoluteScheduleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeSetHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.DamMerchStillsHollow;
import com.netflix.vms.transformer.hollowinput.FlagsHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.LocaleTerritoryCodeHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.PhaseTagHollow;
import com.netflix.vms.transformer.hollowinput.PhaseTagListHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkSourceFileIdHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseElementsHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowMapHollow;
import com.netflix.vms.transformer.hollowinput.SingleValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TerritoryCountriesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoArtworkHollow;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.ArtworkMerchStillPackageData;
import com.netflix.vms.transformer.hollowoutput.SchedulePhaseInfo;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.artwork.ArtWorkModule;
import com.netflix.vms.transformer.util.NFLocaleUtil;

public class VideoImagesDataModule extends ArtWorkModule implements EDAvailabilityChecker {

    private final HollowHashIndex videoArtworkIndex;
    private final HollowPrimaryKeyIndex damMerchStillsIdx;
    private final HollowPrimaryKeyIndex videoStatusIdx;
    private HollowHashIndex rolloutIndex;

    private HollowHashIndex overrideScheduleIndex;
    private HollowHashIndex masterScheduleIndex;
    private HollowHashIndex absoluteScheduleIndex;

    private final static String MERCH_STILL_TYPE = "MERCH_STILL";
    private final int MIN_ROLLUP_SIZE = 4; // 3

    public VideoImagesDataModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        super("Video", api, ctx, mapper, cycleConstants, indexer);

        this.videoArtworkIndex = indexer.getHashIndex(IndexSpec.ARTWORK_BY_VIDEO_ID);
        this.damMerchStillsIdx = indexer.getPrimaryKeyIndex(IndexSpec.DAM_MERCHSTILLS);
        this.videoStatusIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_STATUS);
        this.rolloutIndex = indexer.getHashIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);

        overrideScheduleIndex = indexer.getHashIndex(IndexSpec.OVERRIDE_SCHEDULE_BY_VIDEO_ID);
        masterScheduleIndex = indexer.getHashIndex(IndexSpec.MASTER_SCHEDULE_BY_TAG_SHOW);
        absoluteScheduleIndex = indexer.getHashIndex(IndexSpec.ABSOLUTE_SCHEDULE_BY_VIDEO_ID_TAG);

        ctx.getLogger().info(TransformerLogTag.TransformInfo, "MerchStill descSorting=" + ctx.getConfig().isMerchstillsSortedDescending() + ", edEpisode=" + ctx.getConfig().isMerchstillEpisodeLiveCheckEnabled());
    }

    // constructor only for test purposes
    VideoImagesDataModule(TransformerContext context, HollowHashIndex overrideIndex, HollowHashIndex masterIndex,
                          HollowHashIndex absoluteIndex, VMSHollowInputAPI api, HollowObjectMapper mapper, CycleConstants cycleConstants,
                          VMSTransformerIndexer indexer) {
        super("Video", api, context, mapper, cycleConstants, indexer);
        this.overrideScheduleIndex = overrideIndex;
        this.masterScheduleIndex = masterIndex;
        this.absoluteScheduleIndex = absoluteIndex;

        this.videoArtworkIndex = null;
        this.damMerchStillsIdx = null;
        this.videoStatusIdx = null;
    }

    public Map<String, Map<Integer, VideoImages>> buildVideoImagesByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry) {
        Set<Integer> ids = new HashSet<>();
        for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            for (VideoHierarchy hierarchy : entry.getValue()) {
                ids.addAll(hierarchy.getAllIds());
            }
        }

        Set<Integer> rollupMerchstillVideoIds = new HashSet<>();
        Set<String> rollupSourceFieldIds = new HashSet<>();
        Set<String> merchstillSourceFieldIds = new HashSet<>();

        Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap = new HashMap<>();
        Map<String, Map<Integer, Set<SchedulePhaseInfo>>> countrySchedulePhaseMap = new HashMap<>();

        for (Integer videoId : ids) {
            HollowHashIndexResult matches = videoArtworkIndex.findMatches((long) videoId);
            //HollowHashIndexResult rolloutInput = getRolloutInput(videoId);
            if (matches != null) {
                Map<String, Set<String>> rolloutImagesByCountry = getRolloutImagesByCountry(showHierarchiesByCountry);
                HollowOrdinalIterator iter = matches.iterator();
                int videoArtworkOrdinal = iter.next();
                while (videoArtworkOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    VideoArtworkHollow artworkHollowInput = api.getVideoArtworkHollow(videoArtworkOrdinal);
                    String rollupSourceFileId = processArtwork(showHierarchiesByCountry.keySet(), artworkHollowInput, countryArtworkMap, countrySchedulePhaseMap, merchstillSourceFieldIds, rolloutImagesByCountry, showHierarchiesByCountry);
                    if (rollupSourceFileId != null) {
                        rollupMerchstillVideoIds.add(videoId);
                        rollupSourceFieldIds.add(rollupSourceFileId);
                    }
                    videoArtworkOrdinal = iter.next();
                }
            }
        }

        if (ctx.getConfig().isMerchstillsSortedDescending()) {
            rollupMerchstillsDescOrder(rollupSourceFieldIds, showHierarchiesByCountry, merchstillSourceFieldIds, countryArtworkMap, this, MIN_ROLLUP_SIZE);
        } else {
            rollupMerchstills(rollupMerchstillVideoIds, rollupSourceFieldIds, showHierarchiesByCountry, merchstillSourceFieldIds, countryArtworkMap);
        }

        // Create VideoImages
        Map<String, Map<Integer, VideoImages>> countryImagesMap = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Set<Artwork>>> countryEntry : countryArtworkMap.entrySet()) {
            String countryCode = countryEntry.getKey();
            Map<Integer, Set<Artwork>> artMap = countryEntry.getValue();

            Map<Integer, VideoImages> imagesMap = new HashMap<>();
            countryImagesMap.put(countryCode, imagesMap);

            for (Map.Entry<Integer, Set<Artwork>> entry : artMap.entrySet()) {
                VideoImages images = new VideoImages();
                Integer id = entry.getKey();

                Set<Artwork> artworkSet = entry.getValue();
                images.artworks = createArtworkByTypeMap(artworkSet);
                images.artworkFormatsByType = createFormatByTypeMap(artworkSet);

                imagesMap.put(id, images);
            }
        }
        
        /**
         * Iterate over what is left in countrySchedulePhaseMap. 
         * Iterating over countryArtworkMap and  countrySchedulePhaseMap is done separately to handle cases where some videos might not have
         * images but still have schedule phase info. Ex: child nodes like season. The schedule phase windows including ones at child level are used by Asset Validator.
         */
        for (Map.Entry<String, Map<Integer, Set<SchedulePhaseInfo>>> countrySchedulePhaseEntry : countrySchedulePhaseMap.entrySet()) {
            String countryCode = countrySchedulePhaseEntry.getKey();
            Map<Integer, Set<SchedulePhaseInfo>> videoSchedulePhaseMap = countrySchedulePhaseEntry.getValue();
            Map<Integer, VideoImages> videoImagesMap = countryImagesMap.get(countryCode);
            for(Entry<Integer, Set<SchedulePhaseInfo>> entry: videoSchedulePhaseMap.entrySet()){
            	Integer id = entry.getKey();
                // get schedule phase for artworks for the given video Id above
                if (videoSchedulePhaseMap != null) {
                	VideoImages images = videoImagesMap.get(id);
                	if(images == null){
                		images = new VideoImages();
                		videoImagesMap.put(id, images);
                	}
                    Set<SchedulePhaseInfo> schedulePhaseInfoList = videoSchedulePhaseMap.get(id);
                    if (schedulePhaseInfoList != null) images.imageAvailabilityWindows = schedulePhaseInfoList;
                }
            }

        }

        return countryImagesMap;
    }

    private Map<String, Set<String>> getRolloutImagesByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry) {

        Map<String, Set<String>> countryToListOfSourceFileIds = new HashMap<String, Set<String>>();
        for (Entry<String, Set<VideoHierarchy>> countryVideoHierarchyEntry : showHierarchiesByCountry.entrySet()) {

            Set<String> setOfSourceFileIdsForCountry = new HashSet<>();
            String country = countryVideoHierarchyEntry.getKey();
            countryToListOfSourceFileIds.put(country, setOfSourceFileIdsForCountry);

            for (VideoHierarchy vh : countryVideoHierarchyEntry.getValue()) {
                long topNodeId = vh.getTopNodeId();
                HollowHashIndexResult rolloutMatches = rolloutIndex.findMatches(topNodeId, "DISPLAY_PAGE");

                if (rolloutMatches == null)
                    continue;

                HollowOrdinalIterator iter = rolloutMatches.iterator();
                int rolloutOrdinal = iter.next();
                while (rolloutOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {

                    RolloutHollow rollout = api.getRolloutHollow(rolloutOrdinal);

                    for (RolloutPhaseHollow phase : rollout._getPhases()) {

                        if (!isRolloutForCountryAtHand(country, phase))
                            continue;

                        RolloutPhaseElementsHollow elements = phase._getElements();
                        if (elements == null)
                            continue;

                        RolloutPhaseArtworkHollow artwork = elements._getArtwork();
                        if (artwork == null)
                            continue;
                        for (RolloutPhaseArtworkSourceFileIdHollow sourcefileIdHollow : artwork._getSourceFileIds()) {
                            String sourceFileId = sourcefileIdHollow._getValue()._getValue();
                            setOfSourceFileIdsForCountry.add(sourceFileId);
                        }
                    }
                    rolloutOrdinal = iter.next();
                }
            }
        }
        return countryToListOfSourceFileIds;
    }

    private boolean isRolloutForCountryAtHand(String country, RolloutPhaseHollow phase) {

        boolean rolloutAppliesToCountryAtHand = false;
        RolloutPhaseWindowMapHollow windows = phase._getWindows();

        if (windows != null) {
            for (ISOCountryHollow countryHollow : windows.keySet()) {
                if (country.equals(countryHollow._getValue())) {
                    rolloutAppliesToCountryAtHand = true;
                    break;
                }
            }
        }

        return rolloutAppliesToCountryAtHand;
    }

    private void rollupMerchstills(Set<Integer> rollupMerchstillVideoIds /* in */, Set<String> rollupSourceFieldIds /* in */, Map<String, Set<VideoHierarchy>> showHierarchiesByCountry/* in */,
                                   Set<String> merchstillSourceFieldIds /* in */, Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap /* out */) {

        for (String countryCode : showHierarchiesByCountry.keySet()) {
            Map<Integer, Set<Artwork>> artworkMap = countryArtworkMap.get(countryCode);
            if (artworkMap == null) {
                continue;
            }

            for (VideoHierarchy hierarchy : showHierarchiesByCountry.get(countryCode)) {
                int topNodeId = hierarchy.getTopNodeId();
                Set<Artwork> showArtwork = artworkMap.get(topNodeId);
                boolean showAttached = true;
                if (showArtwork == null) {
                    showArtwork = new LinkedHashSet<>();
                    showAttached = false;
                }
                List<Artwork> showBackFillArtwork = new ArrayList<>();
                int showMerchstillsCount = 0;

                int episodeSeqNum = 0;
                for (int iseason = 0; iseason < hierarchy.getSeasonIds().length; iseason++) {
                    int seasonId = hierarchy.getSeasonIds()[iseason];
                    Set<Artwork> seasonArtwork = artworkMap.get(seasonId);
                    boolean seasonAttached = true;
                    if (seasonArtwork == null) {
                        seasonArtwork = new LinkedHashSet<>();
                        seasonAttached = false;
                    }

                    List<Artwork> seasonBackFillArtwork = new ArrayList<>();
                    int seasonMerchstillsCount = 0;

                    for (int iepisode = 0; iepisode < hierarchy.getEpisodeIds()[iseason].length; iepisode++) {
                        episodeSeqNum++;
                        int episodeId = hierarchy.getEpisodeIds()[iseason][iepisode];
                        if (isAvailableForED(episodeId, countryCode)) {
                            Set<Artwork> episodeArtwork = artworkMap.get(episodeId);

                            if (episodeArtwork != null && !episodeArtwork.isEmpty()) {
                                for (Artwork artwork : episodeArtwork) {
                                    String sourceFieldId = artwork.sourceFileId == null ? null : new String(artwork.sourceFileId.value);
                                    if (rollupMerchstillVideoIds.contains(episodeId) && artwork.sourceFileId != null && rollupSourceFieldIds.contains(sourceFieldId)) {
                                        Artwork seasonArt = artwork.clone();
                                        Artwork showArt = artwork.clone();
                                        seasonArt.seqNum = episodeSeqNum;
                                        showArt.seqNum = episodeSeqNum;
                                        seasonArtwork.add(seasonArt);
                                        showArtwork.add(showArt);
                                        seasonMerchstillsCount++;
                                        showMerchstillsCount++;
                                    } else {  // artwork is not "rollup", potential backfill
                                        if (artwork.sourceFileId != null && merchstillSourceFieldIds.contains(sourceFieldId)) {
                                            if (seasonBackFillArtwork.size() < MIN_ROLLUP_SIZE) {
                                                seasonBackFillArtwork.add(artwork.clone());
                                            }
                                            if (showBackFillArtwork.size() < MIN_ROLLUP_SIZE) {
                                                showBackFillArtwork.add(artwork.clone());
                                            }
                                        }
                                    }// backfill
                                }
                            }
                        }
                    }

                    if (seasonArtwork.isEmpty() && seasonBackFillArtwork.isEmpty()) {
                        // don't attach an empty map
                    } else { // add backfill, if needed
                        int max_seqNum = 0;
                        for (Artwork seasonArt : seasonArtwork) {
                            if (seasonArt.seqNum > max_seqNum) max_seqNum = seasonArt.seqNum;
                        }
                        int num_add = MIN_ROLLUP_SIZE - seasonMerchstillsCount;
                        for (int iadd = 0; iadd < num_add && iadd < seasonBackFillArtwork.size(); iadd++) {
                            Artwork seasonFallback = seasonBackFillArtwork.get(iadd);
                            seasonFallback.seqNum = ++max_seqNum;
                            seasonArtwork.add(seasonFallback);
                        }
                    }

                    if (!seasonAttached && !seasonArtwork.isEmpty()) {
                        artworkMap.put(seasonId, seasonArtwork);
                    }
                } // all seasons within a hierarchy

                if (showArtwork.isEmpty() && showBackFillArtwork.isEmpty()) {
                    // don't attach
                } else { // add backfill, if needed
                    int max_seqNum = 0;
                    for (Artwork showArt : showArtwork) {
                        if (showArt.seqNum > max_seqNum) max_seqNum = showArt.seqNum;
                    }

                    int num_add = MIN_ROLLUP_SIZE - showMerchstillsCount;
                    for (int iadd = 0; iadd < num_add && iadd < showBackFillArtwork.size(); iadd++) {
                        Artwork fallback = showBackFillArtwork.get(iadd);
                        fallback.seqNum = ++max_seqNum;
                        showArtwork.add(fallback);
                    }
                }

                if (!showAttached && !showArtwork.isEmpty()) {
                    artworkMap.put(topNodeId, showArtwork);
                }
            } // for all video-hierarchies
        } // accross all countries

    }

    // Ref: https://docs.google.com/document/d/1paFkF8WyWJ6dB1Rh7lWZ_y8hk1WwjPoXt-TD0hzc-z0
    static void rollupMerchstillsDescOrder(
            Set<String> rollupSourceFieldIds /* in */,
            Map<String, Set<VideoHierarchy>> showHierarchiesByCountry/* in */,
            Set<String> merchstillSourceFieldIds /* in */,
            Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap /* in-out */,
            EDAvailabilityChecker availabilityChecker,
            int minRollupSize) {

        for (String countryCode : showHierarchiesByCountry.keySet()) {
            Map<Integer, Set<Artwork>> artworkMap = countryArtworkMap.get(countryCode);
            if (artworkMap == null) {
                continue;
            }

            for (VideoHierarchy hierarchy : showHierarchiesByCountry.get(countryCode)) {
                int topNodeId = hierarchy.getTopNodeId();
                Set<Artwork> showArtwork = artworkMap.get(topNodeId);
                boolean showAttached = true;
                if (showArtwork == null) {
                    showArtwork = new LinkedHashSet<>();
                    showAttached = false;
                }
                List<Artwork> showBackFillArtwork = new ArrayList<>();
                int showMerchstillsCount = 0;

                int episodeSeqNum = 0;
                for (int iseason = hierarchy.getSeasonIds().length - 1; iseason >= 0; iseason--) {
                    int seasonId = hierarchy.getSeasonIds()[iseason];
                    Set<Artwork> seasonArtwork = artworkMap.get(seasonId);
                    boolean seasonAttached = true;
                    if (seasonArtwork == null) {
                        seasonArtwork = new LinkedHashSet<>();
                        seasonAttached = false;
                    }

                    List<Artwork> seasonBackFillArtwork = new ArrayList<>();
                    int seasonMerchstillsCount = 0;

                    for (int iepisode = 0; iepisode < hierarchy.getEpisodeIds()[iseason].length; iepisode++) {
                        episodeSeqNum++;
                        int episodeId = hierarchy.getEpisodeIds()[iseason][iepisode];
                        if (!availabilityChecker.isAvailableForED(episodeId, countryCode)) {
                            continue;
                        }

                        Set<Artwork> episodeArtworkSet = artworkMap.get(episodeId);
                        List<Artwork> episodeArtwork = sortViewableArtworkByFile_Seq(episodeArtworkSet);

                        for (Artwork artwork : episodeArtwork) {
                            String sourceFieldId = artwork.sourceFileId == null ? null : new String(artwork.sourceFileId.value);
                            if (artwork.sourceFileId != null && rollupSourceFieldIds.contains(sourceFieldId)) {
                                Artwork seasonArt = artwork.clone();
                                Artwork showArt = artwork.clone();
                                seasonArt.seqNum = episodeSeqNum;
                                showArt.seqNum = episodeSeqNum;
                                seasonArtwork.add(seasonArt);
                                showArtwork.add(showArt);
                                seasonMerchstillsCount++;
                                showMerchstillsCount++;
                            } else {  // artwork is not "rollup", potential backfill
                                if (artwork.sourceFileId != null && merchstillSourceFieldIds.contains(sourceFieldId)) {
                                    if (seasonBackFillArtwork.size() < minRollupSize) {
                                        seasonBackFillArtwork.add(artwork.clone());
                                    }
                                    if (showBackFillArtwork.size() < minRollupSize) {
                                        showBackFillArtwork.add(artwork.clone());
                                    }
                                }
                            }// backfill
                        }
                    }

                    if (seasonArtwork.isEmpty() && seasonBackFillArtwork.isEmpty()) {
                        // don't attach an empty map
                    } else { // add backfill, if needed
                        int max_seqNum = 0;
                        for (Artwork seasonArt : seasonArtwork) {
                            if (seasonArt.seqNum > max_seqNum) max_seqNum = seasonArt.seqNum;
                        }
                        int num_add = minRollupSize - seasonMerchstillsCount;
                        for (int iadd = 0; iadd < num_add && iadd < seasonBackFillArtwork.size(); iadd++) {
                            Artwork seasonFallback = seasonBackFillArtwork.get(iadd);
                            seasonFallback.seqNum = ++max_seqNum;
                            seasonArtwork.add(seasonFallback);
                        }
                    }

                    if (!seasonAttached && !seasonArtwork.isEmpty()) {
                        artworkMap.put(seasonId, seasonArtwork);
                    }
                } // all seasons within a hierarchy

                if (showArtwork.isEmpty() && showBackFillArtwork.isEmpty()) {
                    // don't attach
                } else { // add backfill, if needed
                    int max_seqNum = 0;
                    for (Artwork showArt : showArtwork) {
                        if (showArt.seqNum > max_seqNum) max_seqNum = showArt.seqNum;
                    }

                    int num_add = minRollupSize - showMerchstillsCount;
                    for (int iadd = 0; iadd < num_add && iadd < showBackFillArtwork.size(); iadd++) {
                        Artwork fallback = showBackFillArtwork.get(iadd);
                        fallback.seqNum = ++max_seqNum;
                        showArtwork.add(fallback);
                    }
                }

                if (!showAttached && !showArtwork.isEmpty()) {
                    artworkMap.put(topNodeId, showArtwork);
                }
            } // for all video-hierarchies
        } // accross all countries

    }

    static List<Artwork> sortViewableArtworkByFile_Seq(Set<Artwork> set) {
        if (set == null || set.isEmpty()) {
            return new ArrayList<>();
        }

        List<Artwork> artworkForViewable = new ArrayList<>(set.size());

        for (Artwork art : set) {
            artworkForViewable.add(art);
        }

        Collections.sort(artworkForViewable, new Comparator<Artwork>() {
            @Override
            public int compare(Artwork o1, Artwork o2) {
                return Integer.compare(o1.file_seq, o2.file_seq);
            }
        });

        return artworkForViewable;
    }

    public boolean isAvailableForED(int videoId, String countryCode) {
        int statusOrdinal = videoStatusIdx.getMatchingOrdinal((long) videoId, countryCode);
        StatusHollow status = null;
        if (statusOrdinal != -1) {
            status = api.getStatusHollow(statusOrdinal);
        }

        boolean isGoLive = false;
        boolean isInWindow = false;

        if (status != null) {
            FlagsHollow flags = status._getFlags();
            if (flags != null) {
                isGoLive = flags._getGoLive();
            }

            ListOfRightsWindowHollow windows = status._getRights()._getWindows();
            for (RightsWindowHollow window : windows) {
                long windowStart = window._getStartDate();
                long windowEnd = window._getEndDate();
                if (window._getOnHold()) {
                    windowStart += ONE_THOUSAND_YEARS;
                    windowEnd += ONE_THOUSAND_YEARS;
                }

                if (windowStart < ctx.getNowMillis() && windowEnd > ctx.getNowMillis()) {
                    isInWindow = true;
                    break;
                }
            }
        }

        return (isGoLive && isInWindow);
    }

    @Override
    public void transform() {
        throw new UnsupportedOperationException("Use buildVideoImagesByCountry");
    }

    private String processArtwork(Set<String> countrySet, VideoArtworkHollow artworkHollowInput,
                                  Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap,
                                  Map<String, Map<Integer, Set<SchedulePhaseInfo>>> countrySchedulePhaseMap,
                                  Set<String> merchstillSourceFieldIds,
                                  Map<String, Set<String>> rolloutImagesByCountry, Map<String,
            Set<VideoHierarchy>> showHierarchiesByCountry) {
        ArtworkLocaleListHollow locales = artworkHollowInput._getLocales();
        int entityId = (int) artworkHollowInput._getMovieId();
        int videoId = entityId;

        Set<ArtworkLocaleHollow> localeSet = getLocalTerritories(locales);
        if (localeSet.isEmpty()) {
            ctx.getLogger().error(MissingLocaleForArtwork, "Missing artwork locale for {} with id={}; data will be dropped.", entityType, entityId);
            return null;
        }
        String sourceFileId = artworkHollowInput._getSourceFileId()._getValue();
        int ordinalPriority = (int) artworkHollowInput._getOrdinalPriority();
        int seqNum = (int) artworkHollowInput._getSeqNum();

        // get earliest schedule phase information
        // get all schedule phase list for asset validation
        Set<SchedulePhaseInfo> schedulePhaseInfoSet = getAllScheduleInfo(artworkHollowInput, videoId);
        SchedulePhaseInfo window = getEarliestScheduleInfo(schedulePhaseInfoSet, videoId);
        if (window == null) {
            ctx.getLogger().warn(InvalidPhaseTagForArtwork, "Undefined phaseTagList in VideoArtwork for videoId={} sourceFileId={}. dData will be dropped", videoId, sourceFileId);
            if (ctx.getConfig().isFilterImagesForArtworkScheduling())
                return null;
            window = getFarInFutureSchedulePhaseInfo(videoId);
        }

        ArtworkAttributesHollow attributes = artworkHollowInput._getAttributes();
        ArtworkDerivativeSetHollow inputDerivatives = artworkHollowInput._getDerivatives();

        boolean showLevel = false;
        SingleValuePassthroughMapHollow map = attributes._getPassthrough()._getSingleValues();
        for (MapKeyHollow key_ : map.keySet()) {
            if (key_._getValue().equals("SHOW_LEVEL")) {
                StringHollow val_ = map.get(key_);
                if (val_ != null) {
                    if (val_._getValue().equals("true")) {
                        showLevel = true;
                    }
                }
                break;
            }
        }

        boolean isMerchstillRollup = false;
        boolean isMerchStill = false;
        if (artworkHollowInput._getFileImageType() != null) {
            isMerchStill = MERCH_STILL_TYPE.equals(artworkHollowInput._getFileImageType()._getValue());
            isMerchstillRollup = (isMerchStill && showLevel == true);
        }
        if (isMerchStill) {
            merchstillSourceFieldIds.add(sourceFileId);
        }
        Artwork artwork = new Artwork();
        artwork.sourceVideoId = videoId;
        artwork.hasShowLevelTag = showLevel;

        // Process list of derivatives
        processDerivativesAndCdnList(entityId, sourceFileId, inputDerivatives, artwork);

        artwork.sourceFileId = new Strings(sourceFileId);
        artwork.seqNum = seqNum;
        artwork.ordinalPriority = ordinalPriority;
        fillPassThroughData(artwork, attributes);
        artwork.schedulePhaseInfo = window;
        artwork.isRolloutExclusive = artworkHollowInput._getRolloutExclusive();

        int ordinal = damMerchStillsIdx.getMatchingOrdinal(sourceFileId);
        if (ordinal != -1) {
            DamMerchStillsHollow damMerchstill = api.getDamMerchStillsHollow(ordinal);
            ArtworkMerchStillPackageData packageData = new ArtworkMerchStillPackageData();
            if (damMerchstill._getMoment() != null) {
                try {
                    packageData.packageId = java.lang.Integer.valueOf(damMerchstill._getMoment()._getPackageId()._getValue());
                    packageData.offsetMillis = java.lang.Long.valueOf(damMerchstill._getMoment()._getStillTS()._getValue());
                    artwork.merchstillsPackageData = packageData;
                } catch (Exception e) {
                    ctx.getLogger().error(TransformerLogTag.UnexpectedError, "malformeddamfile=" + sourceFileId, e);
                }
            }
        }

        // upstream to populate locales in Q1 2017, till then bypass the ArtworkLocale
        if (isMerchStill) {
            Artwork localeArtwork = artwork.clone();
            localeArtwork.locale = NFLocaleUtil.createNFLocale("en");
            localeArtwork.effectiveDate = 0L;

            for (String countryCode : countrySet) {
                boolean episodeAvailable = true;
                if (ctx.getConfig().isMerchstillEpisodeLiveCheckEnabled())
                    episodeAvailable = isAvailableForED(videoId, countryCode);

                if (episodeAvailable) {
                    Map<Integer, Set<Artwork>> artMap = getArtworkMap(countryCode, countryArtworkMap);
                    Set<Artwork> artworkSet = getArtworkSet(entityId, artMap);
                    artworkSet.add(localeArtwork);
                }
            }
        } else {
            // Support Country based data
            for (ArtworkLocaleHollow localeHollow : localeSet) {
                Artwork localeArtworkIsRolloutAsInput = artwork.clone();
                localeArtworkIsRolloutAsInput.locale = NFLocaleUtil.createNFLocale(localeHollow._getBcp47Code()._getValue());
                localeArtworkIsRolloutAsInput.effectiveDate = localeHollow._getEffectiveDate()._getValue();

                Artwork localeArtworkIsRolloutOppositeToInput = localeArtworkIsRolloutAsInput.clone();
                localeArtworkIsRolloutOppositeToInput.isRolloutExclusive = !localeArtworkIsRolloutAsInput.isRolloutExclusive;

                for (String countryCode : getCountryCodes(localeHollow)) {
                    Map<Integer, Set<Artwork>> artMap = getArtworkMap(countryCode, countryArtworkMap);

                    // fill up schedule phase information along with locale artworks
                    if (!countrySchedulePhaseMap.containsKey(countryCode)) {
                        countrySchedulePhaseMap.put(countryCode, new HashMap<>());
                    }
                    Map<Integer, Set<SchedulePhaseInfo>> videoSchedulePhaseMap = countrySchedulePhaseMap.get(countryCode);
                    if (!videoSchedulePhaseMap.containsKey(videoId)) {
                        videoSchedulePhaseMap.put(videoId, new HashSet<>());
                    }
                    Set<SchedulePhaseInfo> scheduleSet = videoSchedulePhaseMap.get(videoId);
                    scheduleSet.addAll(schedulePhaseInfoSet);

                    // For non-merch still images do the following
                    // 1) Filter any rollout images without rollout
                    // 2) Mark all rollout images as rollout
                    // 3) Roll-up all images is to topNode with source video to indicate to which video the image was associated to in input.
                    Set<Integer> topNodes = getTopNodes(showHierarchiesByCountry, countryCode, entityId);
                    Artwork updatedArtwork = pickArtworkBasedOnRolloutInfo(localeArtworkIsRolloutAsInput, localeArtworkIsRolloutOppositeToInput, rolloutImagesByCountry.get(countryCode), sourceFileId);

                    if (!ctx.getConfig().isRollupImagesForArtworkScheduling() || topNodes == null || topNodes.isEmpty()) {
                        Set<Artwork> artworkSet = getArtworkSet(entityId, artMap);
                        if (updatedArtwork != null)
                            artworkSet.add(updatedArtwork);
                        continue;
                    }

                    for (int topNode : topNodes) {
                        Set<Artwork> artworkSet = getArtworkSet(topNode, artMap);
                        // If pickArtworkBasedOnRolloutInfo return null based on roll-out: we drop the image for the country.
                        // Look at pickArtworkBasedOnRolloutInfo method for details. Logging of dropped IDs is in pickArtworkBasedOnRolloutInfo.
                        if (updatedArtwork != null)
                            artworkSet.add(updatedArtwork);

                        // roll up schedule windows in top nodes
                        if (!videoSchedulePhaseMap.containsKey(topNode)) {
                            videoSchedulePhaseMap.put(topNode, new HashSet<>());
                        }
                        scheduleSet = videoSchedulePhaseMap.get(topNode);
                        scheduleSet.addAll(schedulePhaseInfoSet);
                    }
                }
            }
        }

        return isMerchstillRollup ? sourceFileId : null;
    }

    private Set<Integer> getTopNodes(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, String countryCode, int entityId) {
        Set<Integer> topNodes = new HashSet<>();
        // More than one hierarchy is possible for a video has different hierarchies in different countries
        Set<VideoHierarchy> hierarchies = showHierarchiesByCountry.get(countryCode);
        if (hierarchies != null) {
            for (VideoHierarchy vh : hierarchies) {
                topNodes.add(vh.getTopNodeId());
            }
        }
        return topNodes;
    }

    @VisibleForTesting
    Artwork pickArtworkBasedOnRolloutInfo(Artwork localeArtworkIsRolloutAsInput, Artwork localeArtworkIsRolloutOppositeToInput, Set<String> rolloutSourceFileIds, String sourceFileId) {
        if (localeArtworkIsRolloutAsInput.isRolloutExclusive) {
            // Upstream says this is a rollout image
            if (ctx.getConfig().isFilterImagesForArtworkScheduling() && (rolloutSourceFileIds == null || !rolloutSourceFileIds.contains(sourceFileId))) {
                // But no corresponding rollout for the image in this country.
                // To err on side of not leaking a rollout image, drop this image for the country.
                // not logging for now since there are way too many log messages.
                // ctx.getLogger().warn(MissingRolloutForArtwork, "Rollout exclusive image has no valid rollout with id={}; data will be dropped.", sourceFileId);
                return null;
            }
            return localeArtworkIsRolloutAsInput;
        }
        // localeArtworkIsRolloutAsInput.isRolloutExclusive == false
        if (rolloutSourceFileIds != null && rolloutSourceFileIds.contains(sourceFileId)) {
            // Upstream did not tell us that this source file id was rollout exclusive but the image is in roll-out.
            // To err on side of caution mark it rollout exclusive to ensure this image is not returned without proper rollout window.
            return (localeArtworkIsRolloutOppositeToInput);
        }
        return localeArtworkIsRolloutAsInput;
    }

    /**
     * Get all schedule phase information for the given video Id.
     *
     * @param videoArtworkHollow
     * @param videoId
     * @return Null if phase tags list is null or no matching schedule is present for given phase tag.
     * Else a set of schedule information. Note empty set is never returned.
     */
    Set<SchedulePhaseInfo> getAllScheduleInfo(VideoArtworkHollow videoArtworkHollow, int videoId) {
        Set<SchedulePhaseInfo> schedulePhaseInfos = null;
        boolean isSmoky = videoArtworkHollow._getIsSmoky();

        PhaseTagListHollow phaseTagListHollow = videoArtworkHollow._getPhaseTags();
        checkPhaseTagList(phaseTagListHollow, videoArtworkHollow, videoId);
        Iterator<PhaseTagHollow> iterator = null;
        if (phaseTagListHollow != null)
            iterator = phaseTagListHollow.iterator();

        if (phaseTagListHollow == null || !iterator.hasNext()) {
            // adding a default window for no phase tags.
            SchedulePhaseInfo defaultWindow = new SchedulePhaseInfo(isSmoky, videoId);
            schedulePhaseInfos = new HashSet<>();
            schedulePhaseInfos.add(defaultWindow);
            return schedulePhaseInfos;
        }

        while (iterator.hasNext()) {

            PhaseTagHollow phaseTagHollow = iterator.next();
            String tag = phaseTagHollow._getPhaseTag()._getValue();
            String scheduleId = phaseTagHollow._getScheduleId()._getValue();

            if (tag != null && scheduleId != null) {

                // check absolute schedule
                HollowHashIndexResult indexResult = absoluteScheduleIndex.findMatches((long) videoId, tag);
                if (indexResult != null && indexResult.numResults() >= 1) {
                    if (schedulePhaseInfos == null) schedulePhaseInfos = new HashSet<>();
                    schedulePhaseInfos.add(getAbsoluteSchedule(indexResult.iterator().next(), api, isSmoky, videoId));
                }

                // check override schedule
                indexResult = overrideScheduleIndex.findMatches((long) videoId, tag);
                if (indexResult != null && indexResult.numResults() >= 1) {
                    if (schedulePhaseInfos == null) schedulePhaseInfos = new HashSet<>();
                    schedulePhaseInfos.add(getSchedule(indexResult.iterator().next(), api, isSmoky, true, videoId));
                } else {
                    // get master schedule if override is not present
                    indexResult = masterScheduleIndex.findMatches(tag, scheduleId);
                    if (indexResult != null && indexResult.numResults() >= 1) {
                        if (schedulePhaseInfos == null) schedulePhaseInfos = new HashSet<>();
                        schedulePhaseInfos.add(getSchedule(indexResult.iterator().next(), api, isSmoky, false, videoId));
                    }
                }

            }
        }

        // Returning null will result in dropping the image. To enable gradual roll out of artwork scheduling, dropping of 
        // images will be enabled only when a property is turned on. To achieve this, bad phase tag images will not be dropped
        // but will be tagged with far in the future start and end dates. This was for old path image exits.
        // For new path, image will not be considered. 
        if (schedulePhaseInfos == null && !ctx.getConfig().isFilterImagesForArtworkScheduling()) {
            SchedulePhaseInfo phaseWithFarFutureStartAndEnd = getFarInFutureSchedulePhaseInfo(videoId);
            return Collections.singleton(phaseWithFarFutureStartAndEnd);
        }

        return schedulePhaseInfos;
    }

    private SchedulePhaseInfo getFarInFutureSchedulePhaseInfo(int videoId) {
        SchedulePhaseInfo phaseWithFarFutureStartAndEnd = new SchedulePhaseInfo(videoId);
        phaseWithFarFutureStartAndEnd.start = phaseWithFarFutureStartAndEnd.end = SchedulePhaseInfo.FAR_FUTURE_DATE;
        return phaseWithFarFutureStartAndEnd;
    }

    private SchedulePhaseInfo getAbsoluteSchedule(int absoluteScheduleOrdinal, VMSHollowInputAPI api, boolean isSmoky, int videoId) {
        SchedulePhaseInfo window = new SchedulePhaseInfo(isSmoky, videoId);
        AbsoluteScheduleHollow absoluteScheduleHollow = api.getAbsoluteScheduleHollow(absoluteScheduleOrdinal);
        // check for null values (null values are Long.MIN_VALUE in hollow)
        // assign dates only if not Long.MIN_VALUE else default is used FAR_FUTURE_DATE for end date and 0L for start date
        if (absoluteScheduleHollow._getStartDate() != Long.MIN_VALUE)
            window.start = absoluteScheduleHollow._getStartDate();
        if (absoluteScheduleHollow._getEndDate() != Long.MIN_VALUE)
            window.end = absoluteScheduleHollow._getEndDate();
        window.isAbsolute = true;
        return window;
    }

    private SchedulePhaseInfo getSchedule(int ordinal, VMSHollowInputAPI api, boolean isSmoky, boolean isOverride, int videoId) {
        Long startOffset;
        if (isOverride) {
            startOffset = api.getOverrideScheduleHollow(ordinal)._getAvailabilityOffset();
        } else {
            startOffset = api.getMasterScheduleHollow(ordinal)._getAvailabilityOffset();
        }
        SchedulePhaseInfo window = new SchedulePhaseInfo(isSmoky, videoId);
        // assign dates only if not Long.MIN_VALUE else default is used 0L for start offset
        if (startOffset != Long.MIN_VALUE)
            window.start = startOffset;
        return window;
    }

    /**
     * Check phase tag list, if null then return null, else return the list itself.
     */
    private PhaseTagListHollow checkPhaseTagList(PhaseTagListHollow phaseTagListHollow, VideoArtworkHollow videoArtworkHollow, int videoId) {
        if (phaseTagListHollow == null) {
            String sourceFileId = videoArtworkHollow._getSourceFileId()._getValue();
            ctx.getLogger().info(InvalidPhaseTagForArtwork, "PhaseTagList is null in VideoArtwork for videoId={} sourceFileId={} " +
                    "returning null, data will be dropped", videoId, sourceFileId);
        }
        return phaseTagListHollow;
    }

    /**
     * This method retrieves the earliest scheduling information for the artwork. It looks up the schedule in images schedules feed.
     * Check method getAllScheduleInfo.
     *
     * @param schedulePhaseInfoSet
     * @return
     */
    SchedulePhaseInfo getEarliestScheduleInfo(Set<SchedulePhaseInfo> schedulePhaseInfoSet, int videoId) {
        // this set is empty if phase tag list is null. Check method getAllScheduleInfo
        if (schedulePhaseInfoSet == null) return null;

        SchedulePhaseInfo window = null;
        for (SchedulePhaseInfo schedulePhaseInfo : schedulePhaseInfoSet) {
            if (schedulePhaseInfo.isAbsolute) {
                return schedulePhaseInfo;
            }
            if (window == null) {
                window = schedulePhaseInfo;
            }
            if (window.start > schedulePhaseInfo.start) {
                window = schedulePhaseInfo;
            }
        }
        return window;
    }


    protected Map<Integer, Set<Artwork>> getArtworkMap(String countryCode, Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap) {
        Map<Integer, Set<Artwork>> artMap = countryArtworkMap.get(countryCode);
        if (artMap == null) {
            artMap = new HashMap<>();
            countryArtworkMap.put(countryCode, artMap);
        }

        return artMap;
    }

    protected Set<String> getCountryCodes(ArtworkLocaleHollow artworkLocaleHollow) {
        Set<String> countrySet = new HashSet<>();
        for (LocaleTerritoryCodeHollow ltCodeHollow : artworkLocaleHollow._getTerritoryCodes()) {
            StringHollow codeHollow = ltCodeHollow._getValue();
            int ordinal = territoryIdx.getMatchingOrdinal(codeHollow._getValue());
            if (ordinal != -1) {
                TerritoryCountriesHollow territoryCountryHollow = api.getTerritoryCountriesHollow(ordinal);
                for (ISOCountryHollow countryHollow : territoryCountryHollow._getCountryCodes()) {
                    countrySet.add(countryHollow._getValue());
                }
            } else {
                ctx.getLogger().error(InvalidImagesTerritoryCode, "Invalid TerritoryCode={} in entityType={}", codeHollow._getValue(), entityType);
                continue;
            }
        }
        return countrySet;
    }
}
