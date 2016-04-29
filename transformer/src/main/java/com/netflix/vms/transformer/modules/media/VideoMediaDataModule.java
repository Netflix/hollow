package com.netflix.vms.transformer.modules.media;

import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_DATE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_RIGHTS;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.ReleaseDateHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDateWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.util.VideoDateUtil;
import java.util.HashMap;
import java.util.Map;

public class VideoMediaDataModule {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex videoRightsIdx;
    private final HollowHashIndex videoTypeCountryIdx;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowHashIndex videoDateIdx;

    private VideoMediaData showData;
    private VideoMediaData seasonData;

    private static enum HierarchyLeveL {
        SHOW, SEASON, EPISODE, SUPPLEMENTAL
    }

    public VideoMediaDataModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(VIDEO_RIGHTS);
        this.videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.videoDateIdx = indexer.getHashIndex(VIDEO_DATE);
    }

    public Map<String, Map<Integer, VideoMediaData>> buildVideoMediaDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        Map<String, Map<Integer, VideoMediaData>> allVideoMediaDataMap = new HashMap<String, Map<Integer, VideoMediaData>>();

        for (Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            Map<Integer, VideoMediaData> countryMap = new HashMap<Integer, VideoMediaData>();
            allVideoMediaDataMap.put(countryCode, countryMap);

            ShowHierarchy hierarchy = entry.getValue();
            addToResult(hierarchy.getTopNodeId(), countryCode, hierarchy, HierarchyLeveL.SHOW, countryMap);

            for (int iSeason = 0; iSeason < hierarchy.getSeasonIds().length; iSeason++) {
                int seasonId = hierarchy.getSeasonIds()[iSeason];
                addToResult(seasonId, countryCode, hierarchy, HierarchyLeveL.SEASON, countryMap);

                for (int j = 0; j < hierarchy.getEpisodeIds()[iSeason].length; j++) {
                    Integer episodeId = hierarchy.getEpisodeIds()[iSeason][j];
                    addToResult(episodeId, countryCode, hierarchy, HierarchyLeveL.EPISODE, countryMap);
                }
            }

            for (int i = 0; i < hierarchy.getSupplementalIds().length; i++) {
                addToResult(hierarchy.getSupplementalIds()[i], countryCode, hierarchy, HierarchyLeveL.SUPPLEMENTAL, countryMap);
            }
        }

        return allVideoMediaDataMap;
    }

    private void addToResult(Integer videoId, String countryCode, ShowHierarchy hierarchy, HierarchyLeveL level, Map<Integer, VideoMediaData> result) {
        // Integer showId = hierarchy.getTopNodeId();
        VideoMediaData vmd = getVideoMediaDataInstance();

        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal(videoId.longValue(), countryCode);
        VideoRightsHollow rights = null;

        if (populateRights(videoId, countryCode, vmd)) {
            result.put(videoId, vmd);
            // NOTE: Previous version below. Apparently logic was not changed, but removing roll-down produces no diffs.
            // keeping this note just in case.

            // #cleanup: why is vms.isGoLive not in the condition?
            // if (level == HierarchyLeveL.EPISODE) {
            //    vmd.isGoLive = showData.isGoLive && seasonData.isGoLive;
            // }
        }

        populateGeneral(videoId, vmd);
        if (!populateDate(videoId, countryCode, vmd)) {
            if (!countryCode.equals("US")) { // #cleanup: For backwards compatibility
                populateDate(videoId, "US", vmd);
            }
        }
        setReferencesForRollupDown(vmd, level);
    }

    private boolean populateRights(Integer videoId, String countryCode, VideoMediaData vmd) {
        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal(videoId.longValue(), countryCode);
        VideoRightsHollow rights = null;

        if (rightsOrdinal != -1) {
            rights = api.getVideoRightsHollow(rightsOrdinal);
            vmd.isGoLive = rights._getFlags()._getGoLive();
            vmd.hasLocalText = rights._getFlags()._getLocalText();
            vmd.isAutoPlayEnabled = rights._getFlags()._getAutoPlay();
            vmd.isLanguageOverride = rights._getFlags()._getLanguageOverride();
            vmd.hasLocalText = rights._getFlags()._getLocalText();
            vmd.hasLocalAudio = rights._getFlags()._getLocalAudio();

            HollowHashIndexResult videoTypeMatches = videoTypeCountryIdx.findMatches(videoId.longValue(), countryCode);
            VideoTypeDescriptorHollow typeDescriptor = null;
            if (videoTypeMatches != null) {
                typeDescriptor = api.getVideoTypeDescriptorHollow(videoTypeMatches.iterator().next());
                vmd.isOriginal = typeDescriptor._getOriginal();
            }
        }
        return (rightsOrdinal != -1);
    }

    private boolean populateGeneral(Integer videoId, VideoMediaData vmd) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
        if (ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
            vmd.approximateRuntimeInSeconds = (int) general._getRuntime();
        }
        return (ordinal != -1);
    }

    private boolean populateDate(Integer videoId, String countryCode, VideoMediaData vmd) {
        HollowHashIndexResult dateResult = videoDateIdx.findMatches((long) videoId, countryCode);

        if (dateResult != null) {
            int ordinal = dateResult.iterator().next();
            VideoDateWindowHollow dateWindow = api.getVideoDateWindowHollow(ordinal);
            ReleaseDateHollow date_ = VideoDateUtil.getReleaseDateType("DVDStreet", dateWindow);
            vmd.dvdReleaseDate = VideoDateUtil.convertToHollowOutputDate(date_);
        }
        return (vmd.dvdReleaseDate != null);
    }

    private void setReferencesForRollupDown(VideoMediaData vmd, HierarchyLeveL level) {
        switch (level) {
            case SHOW:
                showData = vmd;
                break;
            case SEASON:
                seasonData = vmd;
                break;
            default:
                break;
        }
    }


    private VideoMediaData getVideoMediaDataInstance() {
        VideoMediaData vmd = new VideoMediaData();
        vmd.approximateRuntimeInSeconds = 0;
        vmd.dvdReleaseDate = null;
        return vmd;
    }
}
