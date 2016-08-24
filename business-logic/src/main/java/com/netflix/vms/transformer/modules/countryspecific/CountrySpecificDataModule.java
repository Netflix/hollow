package com.netflix.vms.transformer.modules.countryspecific;

import java.util.Collections;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.FlagsHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MapOfFlagsFirstDisplayDatesHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfDateWindowToListOfInteger;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.util.SensitiveVideoServerSideUtil;
import com.netflix.vms.transformer.util.VideoDateUtil;
import com.netflix.vms.transformer.util.VideoSetTypeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CountrySpecificDataModule {

    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;
    private final CycleConstants constants;
    private final VMSTransformerIndexer indexer;

    private final HollowPrimaryKeyIndex videoStatusIdx;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowHashIndex rolloutVideoTypeIndex;

    private final CertificationListsModule certificationListsModule;
    private final VMSAvailabilityWindowModule availabilityWindowModule;

    public CountrySpecificDataModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer) {
        this.api = api;
        this.ctx = ctx;
        this.constants = constants;
        this.indexer = indexer;
        this.videoStatusIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_STATUS);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
        this.rolloutVideoTypeIndex = indexer.getHashIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);

        this.certificationListsModule = new CertificationListsModule(api, indexer);
        this.availabilityWindowModule = new VMSAvailabilityWindowModule(api, ctx, constants, indexer);
    }

    public Map<String, Map<Integer, CompleteVideoCountrySpecificData>> buildCountrySpecificDataByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, Map<Integer, VideoPackageData> transformedPackageData) {
        this.availabilityWindowModule.setTransformedPackageData(transformedPackageData);

        Map<String, Map<Integer, CompleteVideoCountrySpecificData>> allCountrySpecificDataMap = new HashMap<String, Map<Integer,CompleteVideoCountrySpecificData>>();
        CountrySpecificRollupValues rollup = new CountrySpecificRollupValues();

        for(Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            
            String locale = null;
            switch(countryCode) {
            case "BE":
            case "CH":
            case "LU":
                locale = "fr";
                break;
            default:
                locale = null;
            }

            Map<Integer, CompleteVideoCountrySpecificData> countryMap = new HashMap<Integer, CompleteVideoCountrySpecificData>();
            allCountrySpecificDataMap.put(entry.getKey(), countryMap);

            for(VideoHierarchy hierarchy : entry.getValue()) {

                for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                    rollup.setSeasonSequenceNumber(hierarchy.getSeasonSequenceNumbers()[i]);

                    for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                        int videoId = hierarchy.getEpisodeIds()[i][j];
                        rollup.setDoEpisode(true);
                        convert(videoId, countryCode, locale, countryMap, rollup);
                        rollup.setDoEpisode(false);
                        rollup.episodeFound();
                    }

                    rollup.setDoSeason(true);
                    convert(hierarchy.getSeasonIds()[i], countryCode, locale, countryMap, rollup);
                    rollup.setDoSeason(false);
                    rollup.resetSeason();
                }

                rollup.setDoShow(true);
                convert(hierarchy.getTopNodeId(), countryCode, locale, countryMap, rollup);
                rollup.setDoShow(false);
                rollup.resetShow();

                for(int i=0;i<hierarchy.getSupplementalIds().length;i++) {
                    convert(hierarchy.getSupplementalIds()[i], countryCode, locale, countryMap, rollup);
                }

                rollup.reset();
            }
        }

        certificationListsModule.reset();
        availabilityWindowModule.reset();

        return allCountrySpecificDataMap;
    }

    private void convert(Integer videoId, String countryCode, String locale, Map<Integer, CompleteVideoCountrySpecificData> countryMap, CountrySpecificRollupValues rollup) {
        CompleteVideoCountrySpecificData data = new CompleteVideoCountrySpecificData();

        populateDatesAndWindowData(videoId, countryCode, locale, data, rollup);
        certificationListsModule.populateCertificationLists(videoId, countryCode, data);

        if(rollup.doShow() && isTopNodeGoLive(videoId, countryCode))
            data.dateWindowWiseSeasonSequenceNumberMap = new SortedMapOfDateWindowToListOfInteger(rollup.getDateWindowWiseSeasonSequenceNumbers()); // VideoCollectionsShowDataHolder.computeEpisodeSeasonSequenceNumberMap(showVideoEpisodeList)
        else
            data.dateWindowWiseSeasonSequenceNumberMap = constants.EMPTY_DATE_WINDOW_SEASON_SEQ_MAP;

        countryMap.put(videoId, data);
    }

    private void populateDatesAndWindowData(Integer videoId, String countryCode, String locale, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup) {
        Long firstDisplayDate = null;
        List<VMSAvailabilityWindow> availabilityWindowList = null;

        int statusOrdinal = videoStatusIdx.getMatchingOrdinal(videoId.longValue(), countryCode);
        if (statusOrdinal != -1) {
            StatusHollow status = api.getStatusHollow(statusOrdinal);

            availabilityWindowList = availabilityWindowModule.populateWindowData(videoId, countryCode, locale, data, status, rollup);
            firstDisplayDate = populateFirstDisplayDateData(data, status, availabilityWindowList);
        }

        // Use Status Data to populate MetaDataAvailabilityDate
        populateMetaDataAvailabilityDate(videoId, countryCode, firstDisplayDate, availabilityWindowList, data);

        if(data.firstDisplayDateByLocale == null) data.firstDisplayDateByLocale = Collections.emptyMap();
        if(data.imagesAvailabilityWindows == null) data.imagesAvailabilityWindows = Collections.emptyList();
        if(data.mediaAvailabilityWindows == null) data.mediaAvailabilityWindows = Collections.emptyList();
    }

    // Return firstDisplayDate as long
    private Long populateFirstDisplayDateData(CompleteVideoCountrySpecificData data, StatusHollow status, List<VMSAvailabilityWindow> availabilityWindowList) {
        VMSAvailabilityWindow activeWindow = getActiveWindow(availabilityWindowList, ctx.getNowMillis());
        Date availabilityDate = activeWindow == null ? null : activeWindow.startDate;

        FlagsHollow flags = status._getFlags();
        DateHollow firstDisplayDate = flags._getFirstDisplayDate();
        if (firstDisplayDate != null) {
            data.firstDisplayDate = VideoDateUtil.roundToHour(new Date(firstDisplayDate._getValue()), availabilityDate);
        }

        MapOfFlagsFirstDisplayDatesHollow firstDisplayDatesByLocale = flags._getFirstDisplayDates();
        if(firstDisplayDatesByLocale != null) {
            data.firstDisplayDateByLocale = new HashMap<NFLocale, Date>();
            for(Map.Entry<MapKeyHollow, DateHollow> entry : firstDisplayDatesByLocale.entrySet()) {
                data.firstDisplayDateByLocale.put(new NFLocale(entry.getKey()._getValue().replace('-', '_')), VideoDateUtil.roundToHour(new Date(entry.getValue()._getValue()), availabilityDate));
            }
        }

        return data.firstDisplayDate == null ? null : data.firstDisplayDate.val;
    }

    private void populateMetaDataAvailabilityDate(long videoId, String countryCode, Long firstDisplayDate, List<VMSAvailabilityWindow> availabilityWindowList, CompleteVideoCountrySpecificData data) {
        VMSAvailabilityWindow firstWindow = getEarlierstWindow(availabilityWindowList);
        WindowPackageContractInfo packageContractInfo = getWindowPackageContractInfo(firstWindow);
        Integer prePromoDays = packageContractInfo == null ? null : packageContractInfo.videoContractInfo.prePromotionDays;
        Long availabilityDate = firstWindow != null ? firstWindow.startDate.val : null;

        Integer metadataReleaseDays = getMetaDataReleaseDays(videoId);
        Long firstPhaseStartDate = getFirstPhaseStartDate(videoId, countryCode);

        Set<VideoSetType> videoSetTypes = VideoSetTypeUtil.computeSetTypes(videoId, countryCode, api, ctx, constants, indexer);
        data.metadataAvailabilityDate = SensitiveVideoServerSideUtil.getMetadataAvailabilityDate(videoSetTypes, firstDisplayDate, firstPhaseStartDate, availabilityDate, prePromoDays, metadataReleaseDays, constants);
    }

    private WindowPackageContractInfo getWindowPackageContractInfo(VMSAvailabilityWindow window) {
        if (window == null || window.windowInfosByPackageId == null) return null;

        WindowPackageContractInfo info = null;
        com.netflix.vms.transformer.hollowoutput.Integer packageId = null;
        for (Map.Entry<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo> entry : window.windowInfosByPackageId.entrySet()) {
            com.netflix.vms.transformer.hollowoutput.Integer key = entry.getKey();
            if (packageId == null || packageId.val < key.val) {
                packageId = key;
                info = entry.getValue();
            }
        }

        return info;
    }

    private Long getFirstPhaseStartDate(long videoId, String country) {
        HollowHashIndexResult result = rolloutVideoTypeIndex.findMatches(videoId, "DISPLAY_PAGE");
        if(result == null) return null;

        Long firstStartDate = null;
        HollowOrdinalIterator iter = result.iterator();
        int rolloutOrdinal = iter.next();
        while(rolloutOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            RolloutHollow rollout = api.getRolloutHollow(rolloutOrdinal);

            for(RolloutPhaseHollow phase : rollout._getPhases()) {
                for(Map.Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phase._getWindows().entrySet()) {
                    if(entry.getKey()._isValueEqual(country)) {
                        long curStartDate = entry.getValue()._getStartDate();
                        if (firstStartDate==null ||  firstStartDate > curStartDate) {
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
        VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
        if (general != null)
            return general._getMetadataReleaseDaysBoxed();
        return null;
    }

    private boolean isTopNodeGoLive(int topNodeId, String countryCode) {
        int statusOrdinal = videoStatusIdx.getMatchingOrdinal(Long.valueOf(topNodeId), countryCode);
        if (statusOrdinal != -1) {
            StatusHollow status = api.getStatusHollow(statusOrdinal);
            FlagsHollow flags = status._getFlags();
            if(flags != null)
                return flags._getGoLive();
        }
        return false;
    }

}
