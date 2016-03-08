package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoCountryRatingHollow;

import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingsHollow;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MapOfFirstDisplayDatesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashMap;
import java.util.Map;

public class CountrySpecificDataModule {

    private final VMSHollowVideoInputAPI api;
    private final HollowPrimaryKeyIndex videoRightsIdx;
    private final HollowPrimaryKeyIndex videoRatingsIdx;

    public CountrySpecificDataModule(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_RIGHTS);
        this.videoRatingsIdx = indexer.getPrimaryKeyIndex(IndexSpec.CONSOLIDATED_VIDEO_RATINGS);
    }

    public Map<String, Map<Integer, CompleteVideoCountrySpecificData>> buildCountrySpecificDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        Map<String, Map<Integer, CompleteVideoCountrySpecificData>> allCountrySpecificDataMap = new HashMap<String, Map<Integer,CompleteVideoCountrySpecificData>>();

        for(Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            Map<Integer, CompleteVideoCountrySpecificData> countryMap = new HashMap<Integer, CompleteVideoCountrySpecificData>();
            allCountrySpecificDataMap.put(entry.getKey(), countryMap);

            ShowHierarchy hierarchy = entry.getValue();

            for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                    convert(hierarchy.getEpisodeIds()[i][j], countryCode, countryMap);
                }

                convert(hierarchy.getSeasonIds()[i], countryCode, countryMap);
            }

            convert(hierarchy.getTopNodeId(), countryCode, countryMap);

            for(int i=0;i<hierarchy.getSupplementalIds().length;i++) {
                convert(hierarchy.getSupplementalIds()[i], countryCode, countryMap);
            }

        }

        return allCountrySpecificDataMap;
    }

    private void convert(Integer videoId, String countryCode, Map<Integer, CompleteVideoCountrySpecificData> countryMap) {
        CompleteVideoCountrySpecificData data = new CompleteVideoCountrySpecificData();

        populateDates(videoId, countryCode, countryMap, data);

        int ratingsOrdinal = videoRatingsIdx.getMatchingOrdinal(videoId.longValue());
        if(ratingsOrdinal != -1) {
            ConsolidatedVideoRatingsHollow videoRatings = api.getConsolidatedVideoRatingsHollow(ratingsOrdinal);

            for(ConsolidatedVideoRatingHollow rating : videoRatings._getRatings()) {
                for(ConsolidatedVideoCountryRatingHollow countryRating : rating._getCountryRatings()) {

                }
            }
        }


        countryMap.put(videoId, data);

    }

    private void populateDates(Integer videoId, String countryCode, Map<Integer, CompleteVideoCountrySpecificData> countryMap, CompleteVideoCountrySpecificData data) {
        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal(videoId.longValue(), countryCode);
        if(rightsOrdinal != -1) {
            VideoRightsHollow rights = api.getVideoRightsHollow(rightsOrdinal);

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

}
