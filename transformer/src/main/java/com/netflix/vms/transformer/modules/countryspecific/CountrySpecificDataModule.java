package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MapOfFirstDisplayDatesHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
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

    private final HollowPrimaryKeyIndex videoRightsIdx;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowHashIndex rolloutVideoTypeIndex;

    private final CertificationListsModule certificationListsModule;
    private final VMSAvailabilityWindowModule availabilityWindowModule;

    public CountrySpecificDataModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer) {
        this.api = api;
        this.ctx = ctx;
        this.constants = constants;
        this.indexer = indexer;
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_RIGHTS);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
        this.rolloutVideoTypeIndex = indexer.getHashIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);

        this.certificationListsModule = new CertificationListsModule(api, indexer);
        this.availabilityWindowModule = new VMSAvailabilityWindowModule(api, ctx, indexer);
    }

    public Map<String, Map<Integer, CompleteVideoCountrySpecificData>> buildCountrySpecificDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry, Map<Integer, VideoPackageData> transformedPackageData) {
        this.availabilityWindowModule.setTransformedPackageData(transformedPackageData);

        Map<String, Map<Integer, CompleteVideoCountrySpecificData>> allCountrySpecificDataMap = new HashMap<String, Map<Integer,CompleteVideoCountrySpecificData>>();
        CountrySpecificRollupValues rollup = new CountrySpecificRollupValues();

        for(Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();

            Map<Integer, CompleteVideoCountrySpecificData> countryMap = new HashMap<Integer, CompleteVideoCountrySpecificData>();
            allCountrySpecificDataMap.put(entry.getKey(), countryMap);

            ShowHierarchy hierarchy = entry.getValue();

            for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                    int videoId = hierarchy.getEpisodeIds()[i][j];
                    convert(videoId, countryCode, countryMap, rollup);
                    rollup.episodeFound();
                }

                rollup.setDoSeason(true);
                rollup.setSeasonSequenceNumber(hierarchy.getSeasonSequenceNumbers()[i]);
                convert(hierarchy.getSeasonIds()[i], countryCode, countryMap, rollup);
                rollup.setDoSeason(false);
                rollup.resetSeason();
            }

            rollup.setDoShow(true);
            convert(hierarchy.getTopNodeId(), countryCode, countryMap, rollup);
            rollup.setDoShow(false);
            rollup.resetShow();

            for(int i=0;i<hierarchy.getSupplementalIds().length;i++) {
                convert(hierarchy.getSupplementalIds()[i], countryCode, countryMap, rollup);
            }

            rollup.reset();
        }

        certificationListsModule.reset();
        availabilityWindowModule.reset();

        return allCountrySpecificDataMap;
    }

    private void convert(Integer videoId, String countryCode, Map<Integer, CompleteVideoCountrySpecificData> countryMap, CountrySpecificRollupValues rollup) {
        CompleteVideoCountrySpecificData data = new CompleteVideoCountrySpecificData();

        populateDatesAndWindowData(videoId, countryCode, data, rollup);
        certificationListsModule.populateCertificationLists(videoId, countryCode, data);

        if(rollup.doShow())
            data.dateWindowWiseSeasonSequenceNumberMap = new SortedMapOfDateWindowToListOfInteger(rollup.getDateWindowWiseSeasonSequenceNumbers()); // VideoCollectionsShowDataHolder.computeEpisodeSeasonSequenceNumberMap(showVideoEpisodeList)

        countryMap.put(videoId, data);
    }

    private void populateDatesAndWindowData(Integer videoId, String countryCode, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup) {
        Long firstDisplayDate = null;
        List<VMSAvailabilityWindow> availabilityWindowList = null;

        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal(videoId.longValue(), countryCode);
        if(rightsOrdinal != -1) {
            VideoRightsHollow rights = api.getVideoRightsHollow(rightsOrdinal);

            availabilityWindowList = availabilityWindowModule.populateWindowData(videoId, countryCode, data, rights, rollup);
            firstDisplayDate = populateFirstDisplayDateData(data, rights, availabilityWindowList);
        }

        // Use Rights Data to populate MetaDataAvailabilityDate
        populateMetaDataAvailabilityDate(videoId, countryCode, firstDisplayDate, availabilityWindowList, data);
    }


    // Return firstDisplayDate as long
    private Long populateFirstDisplayDateData(CompleteVideoCountrySpecificData data, VideoRightsHollow rights, List<VMSAvailabilityWindow> availabilityWindowList) {
        VMSAvailabilityWindow activeWindow = getActiveWindow(availabilityWindowList, ctx.getNowMillis());
        Date availabilityDate = activeWindow == null ? null : activeWindow.startDate;

        VideoRightsFlagsHollow flags = rights._getFlags();
        DateHollow firstDisplayDate = flags._getFirstDisplayDate();
        if (firstDisplayDate != null) {
            data.firstDisplayDate = VideoDateUtil.roundToHour(new Date(firstDisplayDate._getValue()), availabilityDate);
        }

        MapOfFirstDisplayDatesHollow firstDisplayDatesByLocale = flags._getFirstDisplayDates();
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

        Integer metadataReleaseDays = getMeataDataReleaseDays(videoId);
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

    private Integer getMeataDataReleaseDays(long videoId) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal(videoId);
        VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
        if (general != null)
            return general._getMetadataReleaseDaysBoxed();
        return null;
    }

}
