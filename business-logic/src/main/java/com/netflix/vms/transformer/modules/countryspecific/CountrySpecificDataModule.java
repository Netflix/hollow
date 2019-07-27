package com.netflix.vms.transformer.modules.countryspecific;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.GATEKEEPER2;

import com.google.common.annotations.VisibleForTesting;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.CycleDataAggregator;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.data.CupTokenFetcher;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.data.VideoDataCollection;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.MulticatalogCountryData;
import com.netflix.vms.transformer.hollowoutput.MulticatalogCountryLocaleData;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.SchedulePhaseInfo;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfDateWindowToListOfInteger;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Flags;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.MapKey;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.MapOfFlagsFirstDisplayDates;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Status;
import com.netflix.vms.transformer.input.datasets.Gatekeeper2Dataset;
import com.netflix.vms.transformer.util.DVDCatalogUtil;
import com.netflix.vms.transformer.util.SensitiveVideoServerSideUtil;
import com.netflix.vms.transformer.util.VideoDateUtil;
import com.netflix.vms.transformer.util.VideoSetTypeUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//TODO: enable me once we can turn on the new data set including follow vip functionality
//import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.DatasetIdentifier.OSCAR;

public class CountrySpecificDataModule {

    private static final long THIRTY_DAYS = 30L * 24L * 60L * 60L * 1000L;

    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;
    private final HollowObjectMapper mapper;
    private final CycleConstants constants;
    private final VMSTransformerIndexer indexer;

    private final Gatekeeper2Dataset gk2Dataset;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowHashIndex rolloutVideoTypeIndex;
    private final HollowHashIndex videoTypeCountryIndex;
    //private final OscarDataset oscarDataset;

    private final CertificationListsModule certificationListsModule;
    private final VMSAvailabilityWindowModule availabilityWindowModule;

    private VideoDataCollection videoDataCollection;

    public CountrySpecificDataModule(VMSHollowInputAPI api, UpstreamDatasetHolder upstream,
            TransformerContext ctx, HollowObjectMapper mapper,
            CycleConstants constants, VMSTransformerIndexer indexer,
            CycleDataAggregator cycleDataAggregator, CupTokenFetcher cupTokenFetcher) {
        this.api = api;
        this.ctx = ctx;
        this.mapper = mapper;
        this.constants = constants;
        this.indexer = indexer;
        this.gk2Dataset = upstream.getDataset(GATEKEEPER2);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
        this.rolloutVideoTypeIndex = indexer.getHashIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);
        this.videoTypeCountryIndex = indexer.getHashIndex(IndexSpec.VIDEO_TYPE_COUNTRY);
        //this.oscarDataset = upstream.getDataset(OSCAR);

        this.certificationListsModule = new CertificationListsModule(api, constants, indexer);
        this.availabilityWindowModule = new VMSAvailabilityWindowModule(api, ctx, constants, indexer,
                cycleDataAggregator, cupTokenFetcher, upstream);
    }

    @VisibleForTesting
    CountrySpecificDataModule(TransformerContext ctx) {
        this.api = null;
        this.ctx = ctx;
        this.mapper = null;
        this.constants = null;
        this.indexer = null;
        this.gk2Dataset = null;
        this.videoGeneralIdx = null;
        this.rolloutVideoTypeIndex = null;
        this.videoTypeCountryIndex = null;
//        this.oscarDataset = null;

        this.certificationListsModule = null;
        this.availabilityWindowModule = null;
    }

    public void buildCountrySpecificDataByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, TransformedVideoData transformedVideoData) {
        this.availabilityWindowModule.setTransformedVideoData(transformedVideoData);
        CountrySpecificRollupValues rollup = new CountrySpecificRollupValues();

        for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            this.videoDataCollection = transformedVideoData.getVideoDataCollection(countryCode);
            processCountrySpecificData(rollup, entry.getValue(), countryCode, videoDataCollection);
            Set<String> catalogLanguages = ctx.getOctoberSkyData().getCatalogLanguages(countryCode);
            if (catalogLanguages != null) {
                processLocaleSpecificData(rollup, entry.getValue(), countryCode, catalogLanguages);
            }
        }

        certificationListsModule.reset();
        availabilityWindowModule.reset();
    }

    private void processCountrySpecificData(CountrySpecificRollupValues rollup, Set<VideoHierarchy> hierarchies, String countryCode, VideoDataCollection videoDataCollection) {
        for (VideoHierarchy hierarchy : hierarchies) {

            for (int i = 0; i < hierarchy.getSeasonIds().length; i++) {
                rollup.setSeasonSequenceNumber(hierarchy.getSeasonSequenceNumbers()[i]);

                for (int j = 0; j < hierarchy.getEpisodeIds()[i].length; j++) {
                    int videoId = hierarchy.getEpisodeIds()[i][j];
                    rollup.setDoEpisode(true);
                    convert(videoId, countryCode, videoDataCollection, rollup);
                    rollup.setDoEpisode(false);
                    rollup.episodeFound();
                }

                rollup.setDoSeason(true);
                convert(hierarchy.getSeasonIds()[i], countryCode, videoDataCollection, rollup);
                rollup.setDoSeason(false);
                rollup.resetSeason();
            }

            rollup.setDoShow(true);
            convert(hierarchy.getTopNodeId(), countryCode, videoDataCollection, rollup);
            rollup.setDoShow(false);
            rollup.resetShow();

            for (int i = 0; i < hierarchy.getSupplementalIds().length; i++) {
                convert(hierarchy.getSupplementalIds()[i], countryCode, videoDataCollection, rollup);
            }

            rollup.reset();
        }
    }

    private void processLocaleSpecificData(CountrySpecificRollupValues rollup, Set<VideoHierarchy> hierarchies, String countryCode, Set<String> locales) {
        Map<Integer, MulticatalogCountryData> map = new HashMap<>();

        for (String locale : locales) {

            for (VideoHierarchy hierarchy : hierarchies) {
                for (int i = 0; i < hierarchy.getSeasonIds().length; i++) {
                    rollup.setSeasonSequenceNumber(hierarchy.getSeasonSequenceNumbers()[i]);

                    for (int j = 0; j < hierarchy.getEpisodeIds()[i].length; j++) {
                        int videoId = hierarchy.getEpisodeIds()[i][j];
                        rollup.resetViewable();
                        rollup.setDoEpisode(true);
                        convertLocale(videoId, false, countryCode, locale, rollup, map);
                        rollup.setDoEpisode(false);
                        rollup.episodeFound();
                    }

                    rollup.setDoSeason(true);
                    convertLocale(hierarchy.getSeasonIds()[i], false, countryCode, locale, rollup, map);
                    rollup.setDoSeason(false);
                    rollup.resetSeason();
                }

                rollup.setDoShow(true);
                convertLocale(hierarchy.getTopNodeId(), hierarchy.isStandalone(), countryCode, locale, rollup, map);
                rollup.setDoShow(false);
                rollup.resetShow();

                for (int i = 0; i < hierarchy.getSupplementalIds().length; i++) {
                    rollup.resetViewable();
                    convertLocale(hierarchy.getSupplementalIds()[i], false, countryCode, locale, rollup, map);
                }

                rollup.reset();
            }
        }

        boolean addToCountrySpecificData = ctx.getConfig().isCountrySpecificLanguageDataMapEnabled();
        for (Map.Entry<Integer, MulticatalogCountryData> entry : map.entrySet()) {

            // add languageDataMap to CompleteVideoCountrySpecificData based on the property.
            if (addToCountrySpecificData && entry.getValue() != null) {
                CompleteVideoCountrySpecificData countrySpecificData = videoDataCollection.getCompleteVideoCountrySpecificData(entry.getKey());
                countrySpecificData.languageData = entry.getValue().languageData;
            }

            // continue adding this same data to Hollow for backwards compatibility.
            mapper.add(entry.getValue());
        }
    }

    private void convert(Integer videoId, String countryCode, VideoDataCollection videoDataCollection, CountrySpecificRollupValues rollup) {
        CompleteVideoCountrySpecificData data = new CompleteVideoCountrySpecificData();

        populateDatesAndWindowData(videoId, countryCode, data, rollup);
        certificationListsModule.populateCertificationLists(videoId, countryCode, data);

        if (rollup.doShow() && isTopNodeGoLive(videoId, countryCode))
            data.dateWindowWiseSeasonSequenceNumberMap = new SortedMapOfDateWindowToListOfInteger(rollup.getDateWindowWiseSeasonSequenceNumbers());
        else
            data.dateWindowWiseSeasonSequenceNumberMap = constants.EMPTY_DATE_WINDOW_SEASON_SEQ_MAP;
        videoDataCollection.addCompleteVideoCountrySpecificData(videoId, data);
    }

    private void convertLocale(Integer videoId, boolean isStandalone, String countryCode, String language, CountrySpecificRollupValues rollup, Map<Integer, MulticatalogCountryData> data) {
        Status status = gk2Dataset.getStatus(videoId.longValue(), countryCode);
        if (status != null) {
            /// in order to calculate the hasNewContent flag, we need to know the latest in-window episode launch date for this hierarchy
            if (rollup.doEpisode()) {
                Long launchDate = getLaunchDateForLanguage(status, language);
                if (launchDate != null)
                    rollup.newEpisodeLaunchDate(launchDate);
            }

            List<VMSAvailabilityWindow> availabilityWindows = availabilityWindowModule.calculateWindowData(videoId, countryCode, language, status, rollup, availabilityWindowModule.isGoLive(status));

            /// Check the generated windows against the main country window -- if not different, exclude the record.
            ///if(!availabilityWindows.equals(baseData.mediaAvailabilityWindows)) {
            MulticatalogCountryData countryData = data.get(videoId);
            if (countryData == null) {
                countryData = new MulticatalogCountryData();
                countryData.country = constants.getISOCountry(countryCode);
                countryData.videoId = new Video(videoId);
                countryData.languageData = new HashMap<>();
                data.put(videoId, countryData);
            }

            MulticatalogCountryLocaleData result = new MulticatalogCountryLocaleData();
            result.availabilityWindows = availabilityWindows;
            result.hasNewContent = calculateHasNewContent(rollup.getMaxInWindowLaunchDate(), availabilityWindows);
            result.hasLocalAudio = rollup.isFoundLocalAudio();
            result.hasLocalText = rollup.isFoundLocalText();
            if (rollup.doShow() && !isStandalone)
                result.isSearchOnly = calculateSearchOnly(availabilityWindows);

            if (rollup.doShow() && isTopNodeGoLive(videoId, countryCode))
                result.dateWindowWiseSeasonSequenceNumberMap = new SortedMapOfDateWindowToListOfInteger(rollup.getDateWindowWiseSeasonSequenceNumbers());
            else
                result.dateWindowWiseSeasonSequenceNumberMap = constants.EMPTY_DATE_WINDOW_SEASON_SEQ_MAP;

            countryData.languageData.put(new NFLocale(language), result);
            ///}
        }
    }

    private boolean calculateSearchOnly(List<VMSAvailabilityWindow> windows) {
        if (windows == null)
            return false;

        for (VMSAvailabilityWindow window : windows) {
            if (window.startDate.val <= ctx.getNowMillis() && window.endDate.val > ctx.getNowMillis()) {
                return ((window.endDate.val - ctx.getNowMillis()) < THIRTY_DAYS);
            }
        }
        return false;
    }

    private boolean calculateHasNewContent(long maxInWindowLaunchDate, List<VMSAvailabilityWindow> windows) {
        for (VMSAvailabilityWindow window : windows) {
            if (window.startDate.val <= ctx.getNowMillis()) {
                return (ctx.getNowMillis() - window.startDate.val > THIRTY_DAYS) && (ctx.getNowMillis() - maxInWindowLaunchDate < THIRTY_DAYS);
            }
        }
        return false;
    }

    private void populateDatesAndWindowData(Integer videoId, String countryCode, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup) {
        Long firstDisplayDate = null;
        List<VMSAvailabilityWindow> availabilityWindowList = null;

        Status status = gk2Dataset.getStatus(videoId.longValue(), countryCode);
        if (status != null) {
            availabilityWindowList = availabilityWindowModule.populateWindowData(videoId, countryCode, data, status, rollup);
            firstDisplayDate = populateFirstDisplayDateData(data, status, availabilityWindowList);
        }

        // Use Status Data to populate MetaDataAvailabilityDate
        populateMetaDataAvailabilityDate(videoId, countryCode, firstDisplayDate, availabilityWindowList, data, rollup);

        if (data.firstDisplayDateByLocale == null) data.firstDisplayDateByLocale = Collections.emptyMap();
        if (data.availabilityWindows == null) data.availabilityWindows = Collections.emptyList();
    }

    // Return firstDisplayDate as long
    private Long populateFirstDisplayDateData(CompleteVideoCountrySpecificData data, Status status, List<VMSAvailabilityWindow> availabilityWindowList) {
        VMSAvailabilityWindow activeWindow = getActiveWindow(availabilityWindowList, ctx.getNowMillis());
        Date availabilityDate = activeWindow == null ? null : activeWindow.startDate;

        Flags flags = status.getFlags();
        long firstDisplayDate = flags.getFirstDisplayDate();
        if (firstDisplayDate != Long.MIN_VALUE) {
            data.firstDisplayDate = VideoDateUtil.roundToHour(new Date(firstDisplayDate), availabilityDate);
        }

        MapOfFlagsFirstDisplayDates firstDisplayDatesByLocale = flags.getFirstDisplayDates();
        if (firstDisplayDatesByLocale != null) {
            data.firstDisplayDateByLocale = new HashMap<NFLocale, Date>();
            for (Map.Entry<MapKey, com.netflix.vms.transformer.input.api.gen.gatekeeper2.Date> entry : firstDisplayDatesByLocale.entrySet()) {
                data.firstDisplayDateByLocale.put(new NFLocale(entry.getKey().getValue().replace('-', '_')), VideoDateUtil.roundToHour(new Date(entry.getValue().getValue()), availabilityDate));
            }
        }

        return data.firstDisplayDate == null ? null : data.firstDisplayDate.val;
    }

    private Long getLaunchDateForLanguage(Status status, String locale) {
        Flags flags = status.getFlags();
        MapOfFlagsFirstDisplayDates firstDisplayDatesByLocale = flags.getFirstDisplayDates();
        if (firstDisplayDatesByLocale != null && !firstDisplayDatesByLocale.isEmpty()) {
            for (Map.Entry<MapKey, com.netflix.vms.transformer.input.api.gen.gatekeeper2.Date> entry : firstDisplayDatesByLocale.entrySet()) {
                String loc = entry.getKey().getValue();
                if (loc.length() > 2)
                    loc = loc.substring(0, 2);
                if (loc.equals(locale))
                    return entry.getValue().getValue();
            }
        } else {
            long firstDisplayDate = flags.getFirstDisplayDate();
            if (firstDisplayDate != Long.MIN_VALUE)
                return firstDisplayDate;
        }

        return null;
    }

    private VideoTypeDescriptorHollow getVideoTypeDescriptor(long videoId, String countryCode) {
        HollowHashIndexResult queryResult = videoTypeCountryIndex.findMatches(videoId, countryCode);
        if (queryResult == null)
            return null;

        int ordinal = queryResult.iterator().next();
        VideoTypeDescriptorHollow countryType = api.getVideoTypeDescriptorHollow(ordinal);
        return countryType;
    }

    /**
     * This method calculates metadata availability date for a video. This date determines when the search team will start promoting the titles correctly.
     * <br>
     * For the actual logic see {@link SensitiveVideoServerSideUtil#getMetadataAvailabilityDate(boolean, boolean, Set, Long, Long, Long, Integer, Integer, CycleConstants, Long)}
     *
     * @param videoId
     * @param countryCode
     * @param firstDisplayDate
     * @param availabilityWindowList - Availability windows in country
     * @param data
     * @param rollup
     */
    private void populateMetaDataAvailabilityDate(long videoId, String countryCode, Long firstDisplayDate, List<VMSAvailabilityWindow> availabilityWindowList, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup) {
        VMSAvailabilityWindow firstWindow = getEarlierstWindow(availabilityWindowList);
        WindowPackageContractInfo packageContractInfo = getWindowPackageContractInfo(firstWindow);
        Integer prePromoDays = packageContractInfo == null ? null : packageContractInfo.videoContractInfo.prePromotionDays;
        Long availabilityDate = firstWindow != null ? firstWindow.startDate.val : null;
        VideoImages videoImages = videoDataCollection.getVideoImages((int) videoId);
        Long earliestPhaseDate = getEarliestSchedulePhaseDate(videoId, videoImages, availabilityDate, rollup);

        Integer metadataReleaseDays = getMetaDataReleaseDays(videoId);

//        Integer metadataReleaseDays =  (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral())?
//                getMetaDataReleaseDays(videoId)
//                : getMetaDataReleaseDaysOscar(videoId);


        Long firstPhaseStartDate = getFirstPhaseStartDate(videoId, countryCode);

        Set<VideoSetType> videoSetTypes = VideoSetTypeUtil.computeSetTypes(videoId, countryCode, api, ctx, constants, indexer, gk2Dataset);
        VideoTypeDescriptorHollow videoType = getVideoTypeDescriptor(videoId, countryCode);
        boolean isOriginal = videoType == null ? false : videoType._getOriginal();
        boolean inDVDCatalog = DVDCatalogUtil.isVideoInDVDCatalog(api, videoType, videoId, countryCode);
        data.metadataAvailabilityDate = SensitiveVideoServerSideUtil.getMetadataAvailabilityDate(inDVDCatalog, isOriginal, videoSetTypes, firstDisplayDate, firstPhaseStartDate,
                availabilityDate, prePromoDays, metadataReleaseDays, constants, earliestPhaseDate);
        data.isSensitiveMetaData = SensitiveVideoServerSideUtil.isSensitiveMetaData(data.metadataAvailabilityDate, ctx);
    }

    @VisibleForTesting
    Long getEarliestSchedulePhaseDate(long videoId, VideoImages videoImages, Long availabilityDate, CountrySpecificRollupValues rollup) {
        Long earliestStart = null;

        // Check if the feature is turned on.
        if (!ctx.getConfig().isUseSchedulePhasesInAvailabilityDateCalc())
            return earliestStart;

        if (videoImages == null)
            return earliestStart;

        // TODO: videoImages data uses video id as int but this code uses long and hence the conversion.
        // Needs a better fix.
        int intVideoId = (int) videoId;
        Set<SchedulePhaseInfo> schedulePhaseInfoWindows = videoImages.imageAvailabilityWindows;
        if (schedulePhaseInfoWindows == null)
            return earliestStart;

        for (SchedulePhaseInfo info : schedulePhaseInfoWindows) {
            // Only offsets from images associated to current video should count for earliest offset.
            // In some cases (topNodes) image windows from child video are rolled up. In that case,
            // source video will be child video which should be ignored.
            if (info.sourceVideoId != intVideoId)
                continue;

            // If phase has offset and availability date is null, cannot calculate a date.
            // So needs to be ignored.
            if (!info.isAbsolute && availabilityDate == null)
                continue;

            //If absolute, use start as is. Else add it to availability date.
            Long currentOffsetDate = (info.isAbsolute) ? info.start : info.start + availabilityDate;

            if (earliestStart == null || earliestStart > currentOffsetDate)
                earliestStart = currentOffsetDate;
        }

        if(earliestStart != null)
            rollup.newEarliestScheduledPhaseDate(earliestStart);

        if(rollup.getRolledUpEarliestScheduledPhaseDate() != Long.MAX_VALUE)
            earliestStart = rollup.getRolledUpEarliestScheduledPhaseDate();

        return earliestStart;
    }

    private WindowPackageContractInfo getWindowPackageContractInfo(VMSAvailabilityWindow window) {
        if (window == null || window.windowInfosByPackageId == null) return null;

        WindowPackageContractInfo info = null;
        com.netflix.vms.transformer.hollowoutput.Integer packageId = null;
        for (Map.Entry<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo> entry : window.windowInfosByPackageId.entrySet()) {
            com.netflix.vms.transformer.hollowoutput.Integer key = entry.getKey();
            VideoPackageInfo packageInfo = entry.getValue().videoPackageInfo;
            boolean considerPackageForSectction = packageInfo == null ? true : packageInfo.isDefaultPackage;
            if (window.windowInfosByPackageId.size() == 1) {
                considerPackageForSectction = true;
            }
            if (considerPackageForSectction) {
                if (packageId == null || packageId.val < key.val) {
                    packageId = key;
                    info = entry.getValue();
                }
            }
        }

        return info;
    }

    private Long getFirstPhaseStartDate(long videoId, String country) {
        HollowHashIndexResult result = rolloutVideoTypeIndex.findMatches(videoId, "DISPLAY_PAGE");
        if (result == null) return null;

        Long firstStartDate = null;
        HollowOrdinalIterator iter = result.iterator();
        int rolloutOrdinal = iter.next();
        while (rolloutOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            RolloutHollow rollout = api.getRolloutHollow(rolloutOrdinal);

            for (RolloutPhaseHollow phase : rollout._getPhases()) {
                for (Map.Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phase._getWindows().entrySet()) {
                    if (entry.getKey()._isValueEqual(country)) {
                        long curStartDate = entry.getValue()._getStartDate();
                        if (firstStartDate == null || firstStartDate > curStartDate) {
                            firstStartDate = curStartDate;
                        }
                    }
                }
            }

            rolloutOrdinal = iter.next();
        }

        return firstStartDate;
    }

    private VMSAvailabilityWindow getEarlierstWindow(List<VMSAvailabilityWindow> availabilityWindowList) {
        if (availabilityWindowList == null) return null;

        VMSAvailabilityWindow first = null;
        for (VMSAvailabilityWindow item : availabilityWindowList) {
            if (first == null) {
                first = item;
            } else if (item.startDate.val < first.startDate.val) {
                first = item;
            }
        }

        return first;
    }

    private VMSAvailabilityWindow getActiveWindow(List<VMSAvailabilityWindow> availabilityWindowList, long time) {
        if (availabilityWindowList == null) return null;

        for (VMSAvailabilityWindow item : availabilityWindowList) {
            if (item.startDate.val <= time || item.endDate.val >= time) {
                return item;
            }
        }

        return null;
    }

    private Integer getMetaDataReleaseDays(long videoId) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal(videoId);
        if (ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
            return general._getMetadataReleaseDaysBoxed();
        }
        return null;
    }

//    private Integer getMetaDataReleaseDaysOscar(long videoId) {
//        return oscarDataset.mapWithMovieIfExists(videoId,(movie)-> movie.getMetadataReleaseDaysBoxed()).orElse(null);
//    }

    private boolean isTopNodeGoLive(int topNodeId, String countryCode) {
        Status status = gk2Dataset.getStatus(Long.valueOf(topNodeId), countryCode);
        if (status != null) {
            Flags flags = status.getFlags();
            if (flags != null)
                return flags.getGoLive();
        }
        return false;
    }

}
