package com.netflix.vms.transformer.modules.media;

import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_DATE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.CONVERTER;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.GATEKEEPER2;
//TODO: enable me once we can turn on the new data set including follow vip functionality
//import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.OSCAR;

import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.data.VideoDataCollection;
import com.netflix.vms.transformer.hollowinput.ReleaseDateHollow;
import com.netflix.vms.transformer.hollowinput.SetOfStringHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDateWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Status;
import com.netflix.vms.transformer.input.api.gen.oscar.Movie;
import com.netflix.vms.transformer.input.datasets.ConverterDataset;
import com.netflix.vms.transformer.input.datasets.Gatekeeper2Dataset;
import com.netflix.vms.transformer.input.datasets.OscarDataset;
import com.netflix.vms.transformer.modules.ModuleDataSourceTransitionUtil;
import com.netflix.vms.transformer.util.VideoDateUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VideoMediaDataModule {

    private final VMSHollowInputAPI api;
    private final Gatekeeper2Dataset gk2Dataset;
    private final HollowHashIndex videoTypeCountryIdx;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowHashIndex videoDateIdx;
    //private final OscarDataset oscarDataset;

    private VideoMediaData showData;
    private VideoMediaData seasonData;

    private static enum HierarchyLeveL {
        SHOW, SEASON, EPISODE, SUPPLEMENTAL
    }

    private static final int SECONDS_IN_MIN = 60;

    public VideoMediaDataModule(UpstreamDatasetHolder upstream, VMSTransformerIndexer indexer) {
        ConverterDataset converterDataset = upstream.getDataset(CONVERTER);
        this.api = converterDataset.getAPI();
        this.gk2Dataset = upstream.getDataset(GATEKEEPER2);
        this.videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.videoDateIdx = indexer.getHashIndex(VIDEO_DATE);
        //this.oscarDataset = upstream.getDataset(OSCAR);
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

//        if (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral()) {
//            populateGeneralOscar(videoId, vmd);
//        } else {
//            populateGeneral(videoId, vmd);
//        }

        populateGeneral(videoId, vmd);
        if (!populateDate(videoId, countryCode, vmd)) {
            if (!countryCode.equals("US")) { // #cleanup: For backwards compatibility
                populateDate(videoId, "US", vmd);
            }
        }
        setReferencesForRollupDown(vmd, level);
    }

    private boolean populateRights(Integer videoId, String countryCode, VideoMediaData vmd) {
        Status status = gk2Dataset.getStatus(videoId.longValue(), countryCode);

        if (status != null) {
            vmd.isGoLive = status.getFlags().getGoLive();
            vmd.isAutoPlayEnabled = status.getFlags().getAutoPlay();
            vmd.isLanguageOverride = status.getFlags().getLanguageOverride();
            vmd.hasLocalText = status.getFlags().getLocalText();
            vmd.hasLocalAudio = status.getFlags().getLocalAudio();

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

//    private void populateGeneralOscar(long videoId, VideoMediaData vmd) {
//        oscarDataset.execWithMovieIfExists(videoId,(movie)->{
//            vmd.approximateRuntimeInSeconds = movie.getRunLenth()*SECONDS_IN_MIN;
//            vmd.regulatoryAdvisories = oscarDataset.getSetStringsFromMovieExtensions(videoId,OscarDataset.MovieExtensionAttributeName.REGULATORY_ADVISORY);
//        });
//    }

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
