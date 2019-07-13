package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

public class ConverterDataSlicerImpl extends DataSlicer implements InputDataSlicer {

    public ConverterDataSlicerImpl(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        super(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }

    @Override
    public HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine stateEngine) {

        ordinalsToInclude.clear();

        final VMSHollowInputAPI inputAPI = new VMSHollowInputAPI(stateEngine);

        if (videoIdsToInclude.isEmpty()) {
            RandomShowMovieHierarchyBasedSelector selector = new RandomShowMovieHierarchyBasedSelector(stateEngine);
            videoIdsToInclude.addAll(selector.findRandomVideoIds(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude));
        }

        findIncludedOrdinals(stateEngine, "ShowSeasonEpisode", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getShowSeasonEpisodeHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "Package", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int) inputAPI.getPackageHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "PackageMovieDealCountryGroup", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getPackageMovieDealCountryGroupHollow(ordinal)._getMovieId()._getValue());
            }
        });
        findIncludedOrdinals(stateEngine, "Episodes", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getEpisodesHollow(ordinal)._getEpisodeId());
            }
        });
        findIncludedOrdinals(stateEngine, "LocalizedMetadata", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getLocalizedMetadataHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "MovieRatings", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getMovieRatingsHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "Movies", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getMoviesHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "Rollout", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getRolloutHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "StoriesSynopses", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getStoriesSynopsesHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "Supplementals", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getSupplementalsHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals(stateEngine, "VideoAward", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoAwardHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals(stateEngine, "VideoDate", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoDateHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals(stateEngine, "VideoGeneral", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoGeneralHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals(stateEngine, "VideoRating", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoRatingHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals(stateEngine, "VideoType", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoTypeHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals(stateEngine, "ShowCountryLabel", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int) inputAPI.getShowCountryLabelHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals(stateEngine, "VideoArtworkSource", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int) inputAPI.getVideoArtworkSourceHollow(ordinal)._getMovieId());
            }
        });

        includeAll(stateEngine, "DamMerchStills");
        includeAll(stateEngine, "TopN");
        includeAll(stateEngine, "AltGenres");
        includeAll(stateEngine, "ArtWorkImageType");
        includeAll(stateEngine, "ArtworkRecipe");
        includeAll(stateEngine, "AssetMetaDatas");
        includeAll(stateEngine, "Awards");
        includeAll(stateEngine, "CacheDeploymentIntent");
        includeAll(stateEngine, "Categories");
        includeAll(stateEngine, "CategoryGroups");
        includeAll(stateEngine, "Cdn");
        includeAll(stateEngine, "Certifications");
        includeAll(stateEngine, "CertificationSystem");
        includeAll(stateEngine, "Character");
        includeAll(stateEngine, "CharacterArtworkSource");
        includeAll(stateEngine, "Characters");
        includeAll(stateEngine, "ConsolidatedCertificationSystems");
        includeAll(stateEngine, "ConsolidatedVideoRatings");
        includeAll(stateEngine, "DrmSystemIdentifiers");
        includeAll(stateEngine, "Festivals");
        includeAll(stateEngine, "Languages");
        includeAll(stateEngine, "LocalizedCharacter");
        includeAll(stateEngine, "OriginServer");
        includeAll(stateEngine, "PersonAliases");
        includeAll(stateEngine, "PersonArtworkSource");
        includeAll(stateEngine, "Persons");
        includeAll(stateEngine, "ProtectionTypes");
        includeAll(stateEngine, "ShowMemberTypes");
        includeAll(stateEngine, "StorageGroups");
        includeAll(stateEngine, "StreamProfileGroups");
        includeAll(stateEngine, "StreamProfiles");
        includeAll(stateEngine, "TerritoryCountries");
        includeAll(stateEngine, "TurboCollections");
        includeAll(stateEngine, "PersonVideo");
        includeAll(stateEngine, "PersonBio");
        includeAll(stateEngine, "MovieCharacterPerson");
        includeAll(stateEngine, "VMSAward");
        includeAll(stateEngine, "IPLArtworkDerivativeSet");
        includeAll(stateEngine, "AbsoluteSchedule");
        includeAll(stateEngine, "MasterSchedule");
        includeAll(stateEngine, "OverrideSchedule");

        return populateFilteredBlob(stateEngine, ordinalsToInclude);
    }


}
