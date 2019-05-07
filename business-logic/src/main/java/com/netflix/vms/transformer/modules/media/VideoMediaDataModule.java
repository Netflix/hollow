package com.netflix.vms.transformer.modules.media;

import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_DATE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.data.VideoDataCollection;
import com.netflix.vms.transformer.gatekeeper2migration.GatekeeperStatusRetriever;
import com.netflix.vms.transformer.hollowinput.ReleaseDateHollow;
import com.netflix.vms.transformer.hollowinput.SetOfStringHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDateWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.util.VideoDateUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VideoMediaDataModule {

    private final VMSHollowInputAPI api;
    private final GatekeeperStatusRetriever statusRetriever;
    private final HollowHashIndex videoTypeCountryIdx;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowHashIndex videoDateIdx;

    private VideoMediaData showData;
    private VideoMediaData seasonData;

    private static enum HierarchyLeveL {
        SHOW, SEASON, EPISODE, SUPPLEMENTAL
    }

    public VideoMediaDataModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer, GatekeeperStatusRetriever statusRetriever) {
        this.api = api;
        this.statusRetriever = statusRetriever;
        this.videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.videoDateIdx = indexer.getHashIndex(VIDEO_DATE);
    }

    public void buildVideoMediaDataByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, TransformedVideoData transformedVideoData) {
        for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            VideoDataCollection videoDataCollection = transformedVideoData.getVideoDataCollection(countryCode);

            for (VideoHierarchy hierarchy : entry.getValue()) {
                addToResult(hierarchy.getTopNodeId(), countryCode, hierarchy, HierarchyLeveL.SHOW, videoDataCollection);

                for (int iSeason = 0; iSeason < hierarchy.getSeasonIds().length; iSeason++) {
                    int seasonId = hierarchy.getSeasonIds()[iSeason];
                    addToResult(seasonId, countryCode, hierarchy, HierarchyLeveL.SEASON, videoDataCollection);

                    for (int j = 0; j < hierarchy.getEpisodeIds()[iSeason].length; j++) {
                        Integer episodeId = hierarchy.getEpisodeIds()[iSeason][j];
                        addToResult(episodeId, countryCode, hierarchy, HierarchyLeveL.EPISODE, videoDataCollection);
                    }
                }

                for (int i = 0; i < hierarchy.getSupplementalIds().length; i++) {
                    addToResult(hierarchy.getSupplementalIds()[i], countryCode, hierarchy, HierarchyLeveL.SUPPLEMENTAL, videoDataCollection);
                }
            }
        }
    }

    private void addToResult(Integer videoId, String countryCode, VideoHierarchy hierarchy, HierarchyLeveL level, VideoDataCollection videoDataCollection) {
        VideoMediaData vmd = getVideoMediaDataInstance();

        if (populateRights(videoId, countryCode, vmd)) {
            if (level == HierarchyLeveL.EPISODE) {
                vmd.isGoLive = vmd.isGoLive && showData.isGoLive && seasonData.isGoLive;
            } else if (level == HierarchyLeveL.SEASON) {
                vmd.isGoLive = vmd.isGoLive && showData.isGoLive;
            }
            videoDataCollection.addVideoMediaData(videoId, vmd);
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
        StatusHollow status = statusRetriever.getStatus(videoId.longValue(), countryCode);

        if (status != null) {
            vmd.isGoLive = status._getFlags()._getGoLive();
            vmd.isAutoPlayEnabled = status._getFlags()._getAutoPlay();
            vmd.isLanguageOverride = status._getFlags()._getLanguageOverride();
            vmd.hasLocalText = status._getFlags()._getLocalText();
            vmd.hasLocalAudio = status._getFlags()._getLocalAudio();

            HollowHashIndexResult videoTypeMatches = videoTypeCountryIdx.findMatches(videoId.longValue(), countryCode);
            VideoTypeDescriptorHollow typeDescriptor = null;
            if (videoTypeMatches != null) {
                typeDescriptor = api.getVideoTypeDescriptorHollow(videoTypeMatches.iterator().next());
                vmd.isOriginal = typeDescriptor._getOriginal();
            }
        }
        return (status != null);
    }

    private void populateGeneral(Integer videoId, VideoMediaData vmd) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
        if (ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
            vmd.approximateRuntimeInSeconds = (int) general._getRuntime();
            SetOfStringHollow inputRegAdvisories = general._getRegulatoryAdvisories();
            if (inputRegAdvisories != null) {
                Set<Strings> outputRegAdv = new HashSet<>();
                for (StringHollow regAdv : inputRegAdvisories) {
                    outputRegAdv.add(new Strings(regAdv._getValue()));
                }
                vmd.regulatoryAdvisories = outputRegAdv;
            }
        }

        if (vmd.regulatoryAdvisories == null) vmd.regulatoryAdvisories = Collections.emptySet();
    }

    private boolean populateDate(Integer videoId, String countryCode, VideoMediaData vmd) {
        HollowHashIndexResult dateResult = videoDateIdx.findMatches((long) videoId, countryCode);

        if (dateResult != null) {
            int ordinal = dateResult.iterator().next();
            VideoDateWindowHollow dateWindow = api.getVideoDateWindowHollow(ordinal);
            ReleaseDateHollow date_ = VideoDateUtil.getReleaseDateType(VideoDateUtil.ReleaseDateType.DVDStreet, dateWindow);
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
