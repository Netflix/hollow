package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

public class ConverterDataSlicerImpl extends DataSlicer implements InputDataSlicer {

    public ConverterDataSlicerImpl(int... specificNodeIdsToInclude) {
        super(specificNodeIdsToInclude);
    }

    @Override
    public HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine stateEngine) {

        clearOrdinalsToInclude();

        final VMSHollowInputAPI inputAPI = new VMSHollowInputAPI(stateEngine);

        //
        // Below code snippet can find videoIds belonging to topNodeIds in inputs for input slicing. This is an alternative
        // to the existing implementation using GlobalVideoBasedSelector which uses the output blob to find videoIds that
        // belong to a topNodeId. Depending on what is being tested, one might be more suitable over the other.
        //
        // Below code snippet worked when all types were coming via the Converter API, but has not been migrated to n-inputs
        // to Transformer, in favor using output-based GlobalVideoBasedSelector instead if it meets the requirements, or
        // using other testing options eg. followVIP, staticInputVersions, and remote debugging.
        //
        // if (videoIdsToInclude.isEmpty()) {
        //    RandomShowMovieHierarchyBasedSelector selector = new RandomShowMovieHierarchyBasedSelector(stateEngine);
        //    videoIdsToInclude.addAll(selector.findRandomVideoIds(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude));
        //}
        //

        findIncludedOrdinals(stateEngine, "ShowSeasonEpisode", ordinal ->
                Integer.valueOf((int)inputAPI.getShowSeasonEpisodeHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "Package", ordinal ->
                Integer.valueOf((int) inputAPI.getPackageHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "PackageMovieDealCountryGroup", ordinal ->
                Integer.valueOf((int)inputAPI.getPackageMovieDealCountryGroupHollow(ordinal)._getMovieId()._getValue()));
        findIncludedOrdinals(stateEngine, "Episodes", ordinal ->
                Integer.valueOf((int)inputAPI.getEpisodesHollow(ordinal)._getEpisodeId()));
        findIncludedOrdinals(stateEngine, "LocalizedMetadata", ordinal ->
                Integer.valueOf((int)inputAPI.getLocalizedMetadataHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "MovieRatings", ordinal ->
                Integer.valueOf((int)inputAPI.getMovieRatingsHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "Movies", ordinal ->
                Integer.valueOf((int)inputAPI.getMoviesHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "Rollout", ordinal ->
                Integer.valueOf((int)inputAPI.getRolloutHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "StoriesSynopses", ordinal ->
                Integer.valueOf((int)inputAPI.getStoriesSynopsesHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "Supplementals", ordinal ->
                Integer.valueOf((int)inputAPI.getSupplementalsHollow(ordinal)._getMovieId()));
        findIncludedOrdinals(stateEngine, "VideoAward", ordinal ->
                Integer.valueOf((int)inputAPI.getVideoAwardHollow(ordinal)._getVideoId()));
        findIncludedOrdinals(stateEngine, "VideoDate", ordinal ->
                Integer.valueOf((int)inputAPI.getVideoDateHollow(ordinal)._getVideoId()));
        findIncludedOrdinals(stateEngine, "VideoGeneral", ordinal ->
                Integer.valueOf((int)inputAPI.getVideoGeneralHollow(ordinal)._getVideoId()));
        findIncludedOrdinals(stateEngine, "VideoRating", ordinal ->
                Integer.valueOf((int)inputAPI.getVideoRatingHollow(ordinal)._getVideoId()));
        findIncludedOrdinals(stateEngine, "VideoType", ordinal ->
                Integer.valueOf((int)inputAPI.getVideoTypeHollow(ordinal)._getVideoId()));
        findIncludedOrdinals(stateEngine, "ShowCountryLabel", ordinal ->
                Integer.valueOf((int) inputAPI.getShowCountryLabelHollow(ordinal)._getVideoId()));
        findIncludedOrdinals(stateEngine, "VideoArtworkSource", ordinal ->
                Integer.valueOf((int) inputAPI.getVideoArtworkSourceHollow(ordinal)._getMovieId()));

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

        return populateFilteredBlob(stateEngine);
    }


}
