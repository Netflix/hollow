package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MapOfFirstDisplayDatesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfDateWindowToListOfInteger;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountrySpecificDataModule {


    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex videoRightsIdx;

    private final CertificationListsModule certificationListsModule;
    private final VMSAvailabilityWindowModule availabilityWindowModule;



    public CountrySpecificDataModule(VMSHollowInputAPI api, TransformerContext ctx, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_RIGHTS);

        this.certificationListsModule = new CertificationListsModule(api, indexer);
        this.availabilityWindowModule = new VMSAvailabilityWindowModule(api, ctx, indexer);
    }

    public Map<String, Map<Integer, CompleteVideoCountrySpecificData>> buildCountrySpecificDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry, Map<Integer, List<PackageData>> transformedPackageData) {
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

        populateRightsData(videoId, countryCode, data, rollup);
        certificationListsModule.populateCertificationLists(videoId, countryCode, data);

        if(rollup.doShow())
            data.dateWindowWiseSeasonSequenceNumberMap = new SortedMapOfDateWindowToListOfInteger(rollup.getDateWindowWiseSeasonSequenceNumbers()); // VideoCollectionsShowDataHolder.computeEpisodeSeasonSequenceNumberMap(showVideoEpisodeList)

        countryMap.put(videoId, data);
    }

    private void populateRightsData(Integer videoId, String countryCode, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup) {
        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal(videoId.longValue(), countryCode);
        if(rightsOrdinal != -1) {
            VideoRightsHollow rights = api.getVideoRightsHollow(rightsOrdinal);

            availabilityWindowModule.populateWindowData(videoId, countryCode, data, rights, rollup);
            populateFirstDisplayDateData(data, rights);
        }
    }


    private void populateFirstDisplayDateData(CompleteVideoCountrySpecificData data, VideoRightsHollow rights) {
        VideoRightsFlagsHollow flags = rights._getFlags();

        DateHollow firstDisplayDate = flags._getFirstDisplayDate();
        if(firstDisplayDate != null)
            data.firstDisplayDate = new Date(firstDisplayDate._getValue() / 5000 * 5000);

        MapOfFirstDisplayDatesHollow firstDisplayDatesByLocale = flags._getFirstDisplayDates();

        if(firstDisplayDatesByLocale != null) {
            data.firstDisplayDateByLocale = new HashMap<NFLocale, Date>();
            for(Map.Entry<MapKeyHollow, DateHollow> entry : firstDisplayDatesByLocale.entrySet()) {
                data.firstDisplayDateByLocale.put(new NFLocale(entry.getKey()._getValue().replace('-', '_')), new Date(entry.getValue()._getValue() / 5000 * 5000));
            }
        }
    }

}
