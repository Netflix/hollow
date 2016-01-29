package com.netflix.vms.videos.hollowinput;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import com.netflix.hollow.read.customapi.HollowAPI;
import com.netflix.hollow.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.objects.provider.HollowObjectProvider;
import com.netflix.hollow.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.sampling.HollowSamplingDirector;
import com.netflix.hollow.sampling.SampleResult;
import com.netflix.hollow.util.AllHollowRecordCollection;

public class VMSHollowVideoInputAPI extends HollowAPI {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final AwardsDescriptionTranslatedTextsTypeAPI awardsDescriptionTranslatedTextsTypeAPI;
    private final AwardsDescriptionTypeAPI awardsDescriptionTypeAPI;
    private final CharacterQuotesTypeAPI characterQuotesTypeAPI;
    private final CharacterArrayOfQuotesTypeAPI characterArrayOfQuotesTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI;
    private final MapKeyTypeAPI mapKeyTypeAPI;
    private final RolloutLaunchDatesTypeAPI rolloutLaunchDatesTypeAPI;
    private final RolloutMapOfLaunchDatesTypeAPI rolloutMapOfLaunchDatesTypeAPI;
    private final RolloutPhasesElementsArtworkTypeAPI rolloutPhasesElementsArtworkTypeAPI;
    private final RolloutPhasesElementsArrayOfArtworkTypeAPI rolloutPhasesElementsArrayOfArtworkTypeAPI;
    private final RolloutPhasesElementsCastTypeAPI rolloutPhasesElementsCastTypeAPI;
    private final RolloutPhasesElementsArrayOfCastTypeAPI rolloutPhasesElementsArrayOfCastTypeAPI;
    private final RolloutPhasesElementsCharactersTypeAPI rolloutPhasesElementsCharactersTypeAPI;
    private final RolloutPhasesElementsArrayOfCharactersTypeAPI rolloutPhasesElementsArrayOfCharactersTypeAPI;
    private final RolloutPhasesWindowsTypeAPI rolloutPhasesWindowsTypeAPI;
    private final RolloutPhasesMapOfWindowsTypeAPI rolloutPhasesMapOfWindowsTypeAPI;
    private final StreamProfileGroupsStreamProfileIdsTypeAPI streamProfileGroupsStreamProfileIdsTypeAPI;
    private final StreamProfileGroupsArrayOfStreamProfileIdsTypeAPI streamProfileGroupsArrayOfStreamProfileIdsTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final AltGenresAlternateNamesTranslatedTextsTypeAPI altGenresAlternateNamesTranslatedTextsTypeAPI;
    private final AltGenresAlternateNamesMapOfTranslatedTextsTypeAPI altGenresAlternateNamesMapOfTranslatedTextsTypeAPI;
    private final AltGenresAlternateNamesTypeAPI altGenresAlternateNamesTypeAPI;
    private final AltGenresArrayOfAlternateNamesTypeAPI altGenresArrayOfAlternateNamesTypeAPI;
    private final AltGenresDisplayNameTranslatedTextsTypeAPI altGenresDisplayNameTranslatedTextsTypeAPI;
    private final AltGenresDisplayNameMapOfTranslatedTextsTypeAPI altGenresDisplayNameMapOfTranslatedTextsTypeAPI;
    private final AltGenresDisplayNameTypeAPI altGenresDisplayNameTypeAPI;
    private final AltGenresShortNameTranslatedTextsTypeAPI altGenresShortNameTranslatedTextsTypeAPI;
    private final AltGenresShortNameMapOfTranslatedTextsTypeAPI altGenresShortNameMapOfTranslatedTextsTypeAPI;
    private final AltGenresShortNameTypeAPI altGenresShortNameTypeAPI;
    private final AltGenresTypeAPI altGenresTypeAPI;
    private final ArtWorkImageFormatTypeAPI artWorkImageFormatTypeAPI;
    private final ArtWorkImageTypeTypeAPI artWorkImageTypeTypeAPI;
    private final ArtworkRecipeTypeAPI artworkRecipeTypeAPI;
    private final AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI assetMetaDatasTrackLabelsTranslatedTextsTypeAPI;
    private final AssetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI assetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI;
    private final AssetMetaDatasTrackLabelsTypeAPI assetMetaDatasTrackLabelsTypeAPI;
    private final AssetMetaDatasTypeAPI assetMetaDatasTypeAPI;
    private final AwardsAlternateNameTranslatedTextsTypeAPI awardsAlternateNameTranslatedTextsTypeAPI;
    private final AwardsAlternateNameMapOfTranslatedTextsTypeAPI awardsAlternateNameMapOfTranslatedTextsTypeAPI;
    private final AwardsAlternateNameTypeAPI awardsAlternateNameTypeAPI;
    private final AwardsAwardNameTranslatedTextsTypeAPI awardsAwardNameTranslatedTextsTypeAPI;
    private final AwardsAwardNameMapOfTranslatedTextsTypeAPI awardsAwardNameMapOfTranslatedTextsTypeAPI;
    private final AwardsAwardNameTypeAPI awardsAwardNameTypeAPI;
    private final AwardsTypeAPI awardsTypeAPI;
    private final Bcp47CodeTypeAPI bcp47CodeTypeAPI;
    private final CSMReviewTypeAPI cSMReviewTypeAPI;
    private final CacheDeploymentIntentTypeAPI cacheDeploymentIntentTypeAPI;
    private final CategoriesDisplayNameTranslatedTextsTypeAPI categoriesDisplayNameTranslatedTextsTypeAPI;
    private final CategoriesDisplayNameMapOfTranslatedTextsTypeAPI categoriesDisplayNameMapOfTranslatedTextsTypeAPI;
    private final CategoriesDisplayNameTypeAPI categoriesDisplayNameTypeAPI;
    private final CategoriesShortNameTranslatedTextsTypeAPI categoriesShortNameTranslatedTextsTypeAPI;
    private final CategoriesShortNameMapOfTranslatedTextsTypeAPI categoriesShortNameMapOfTranslatedTextsTypeAPI;
    private final CategoriesShortNameTypeAPI categoriesShortNameTypeAPI;
    private final CategoriesTypeAPI categoriesTypeAPI;
    private final CategoryGroupsCategoryGroupNameTranslatedTextsTypeAPI categoryGroupsCategoryGroupNameTranslatedTextsTypeAPI;
    private final CategoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI categoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI;
    private final CategoryGroupsCategoryGroupNameTypeAPI categoryGroupsCategoryGroupNameTypeAPI;
    private final CategoryGroupsTypeAPI categoryGroupsTypeAPI;
    private final CdnsTypeAPI cdnsTypeAPI;
    private final CertificationSystemRatingTypeAPI certificationSystemRatingTypeAPI;
    private final CertificationSystemArrayOfRatingTypeAPI certificationSystemArrayOfRatingTypeAPI;
    private final CertificationSystemTypeAPI certificationSystemTypeAPI;
    private final CertificationsDescriptionTranslatedTextsTypeAPI certificationsDescriptionTranslatedTextsTypeAPI;
    private final CertificationsDescriptionMapOfTranslatedTextsTypeAPI certificationsDescriptionMapOfTranslatedTextsTypeAPI;
    private final CertificationsDescriptionTypeAPI certificationsDescriptionTypeAPI;
    private final CertificationsNameTranslatedTextsTypeAPI certificationsNameTranslatedTextsTypeAPI;
    private final CertificationsNameMapOfTranslatedTextsTypeAPI certificationsNameMapOfTranslatedTextsTypeAPI;
    private final CertificationsNameTypeAPI certificationsNameTypeAPI;
    private final CertificationsTypeAPI certificationsTypeAPI;
    private final CharacterArtworkAttributesTypeAPI characterArtworkAttributesTypeAPI;
    private final CharacterArtworkDerivativesTypeAPI characterArtworkDerivativesTypeAPI;
    private final CharacterArtworkArrayOfDerivativesTypeAPI characterArtworkArrayOfDerivativesTypeAPI;
    private final CharacterArtworkLocalesTerritoryCodesTypeAPI characterArtworkLocalesTerritoryCodesTypeAPI;
    private final CharacterArtworkLocalesArrayOfTerritoryCodesTypeAPI characterArtworkLocalesArrayOfTerritoryCodesTypeAPI;
    private final CharacterArtworkLocalesTypeAPI characterArtworkLocalesTypeAPI;
    private final CharacterArtworkArrayOfLocalesTypeAPI characterArtworkArrayOfLocalesTypeAPI;
    private final CharacterArtworkTypeAPI characterArtworkTypeAPI;
    private final CharacterElementsTypeAPI characterElementsTypeAPI;
    private final CharacterTypeAPI characterTypeAPI;
    private final CharactersBTranslatedTextsTypeAPI charactersBTranslatedTextsTypeAPI;
    private final CharactersBMapOfTranslatedTextsTypeAPI charactersBMapOfTranslatedTextsTypeAPI;
    private final CharactersBTypeAPI charactersBTypeAPI;
    private final CharactersCnTranslatedTextsTypeAPI charactersCnTranslatedTextsTypeAPI;
    private final CharactersCnMapOfTranslatedTextsTypeAPI charactersCnMapOfTranslatedTextsTypeAPI;
    private final CharactersCnTypeAPI charactersCnTypeAPI;
    private final CharactersTypeAPI charactersTypeAPI;
    private final ConsolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI consolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsDescriptionTypeAPI consolidatedCertificationSystemsDescriptionTypeAPI;
    private final ConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI consolidatedCertificationSystemsNameTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI consolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsNameTypeAPI consolidatedCertificationSystemsNameTypeAPI;
    private final ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI consolidatedCertificationSystemsRatingDescriptionsTypeAPI;
    private final ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI;
    private final ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI consolidatedCertificationSystemsRatingRatingCodesTypeAPI;
    private final ConsolidatedCertificationSystemsRatingTypeAPI consolidatedCertificationSystemsRatingTypeAPI;
    private final ConsolidatedCertificationSystemsArrayOfRatingTypeAPI consolidatedCertificationSystemsArrayOfRatingTypeAPI;
    private final ConsolidatedCertificationSystemsTypeAPI consolidatedCertificationSystemsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryListTypeAPI consolidatedVideoRatingsRatingsCountryListTypeAPI;
    private final ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI consolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI consolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI consolidatedVideoRatingsRatingsCountryRatingsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI consolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI;
    private final ConsolidatedVideoRatingsRatingsTypeAPI consolidatedVideoRatingsRatingsTypeAPI;
    private final ConsolidatedVideoRatingsArrayOfRatingsTypeAPI consolidatedVideoRatingsArrayOfRatingsTypeAPI;
    private final ConsolidatedVideoRatingsTypeAPI consolidatedVideoRatingsTypeAPI;
    private final DefaultExtensionRecipeTypeAPI defaultExtensionRecipeTypeAPI;
    private final DeployablePackagesCountryCodesTypeAPI deployablePackagesCountryCodesTypeAPI;
    private final DeployablePackagesArrayOfCountryCodesTypeAPI deployablePackagesArrayOfCountryCodesTypeAPI;
    private final DeployablePackagesTypeAPI deployablePackagesTypeAPI;
    private final DrmSystemIdentifiersTypeAPI drmSystemIdentifiersTypeAPI;
    private final EpisodesEpisodeNameTranslatedTextsTypeAPI episodesEpisodeNameTranslatedTextsTypeAPI;
    private final EpisodesEpisodeNameMapOfTranslatedTextsTypeAPI episodesEpisodeNameMapOfTranslatedTextsTypeAPI;
    private final EpisodesEpisodeNameTypeAPI episodesEpisodeNameTypeAPI;
    private final EpisodesTypeAPI episodesTypeAPI;
    private final FestivalsCopyrightTranslatedTextsTypeAPI festivalsCopyrightTranslatedTextsTypeAPI;
    private final FestivalsCopyrightMapOfTranslatedTextsTypeAPI festivalsCopyrightMapOfTranslatedTextsTypeAPI;
    private final FestivalsCopyrightTypeAPI festivalsCopyrightTypeAPI;
    private final FestivalsDescriptionTranslatedTextsTypeAPI festivalsDescriptionTranslatedTextsTypeAPI;
    private final FestivalsDescriptionMapOfTranslatedTextsTypeAPI festivalsDescriptionMapOfTranslatedTextsTypeAPI;
    private final FestivalsDescriptionTypeAPI festivalsDescriptionTypeAPI;
    private final FestivalsFestivalNameTranslatedTextsTypeAPI festivalsFestivalNameTranslatedTextsTypeAPI;
    private final FestivalsFestivalNameMapOfTranslatedTextsTypeAPI festivalsFestivalNameMapOfTranslatedTextsTypeAPI;
    private final FestivalsFestivalNameTypeAPI festivalsFestivalNameTypeAPI;
    private final FestivalsShortNameTranslatedTextsTypeAPI festivalsShortNameTranslatedTextsTypeAPI;
    private final FestivalsShortNameMapOfTranslatedTextsTypeAPI festivalsShortNameMapOfTranslatedTextsTypeAPI;
    private final FestivalsShortNameTypeAPI festivalsShortNameTypeAPI;
    private final FestivalsSingularNameTranslatedTextsTypeAPI festivalsSingularNameTranslatedTextsTypeAPI;
    private final FestivalsSingularNameMapOfTranslatedTextsTypeAPI festivalsSingularNameMapOfTranslatedTextsTypeAPI;
    private final FestivalsSingularNameTypeAPI festivalsSingularNameTypeAPI;
    private final FestivalsTypeAPI festivalsTypeAPI;
    private final LanguagesNameTranslatedTextsTypeAPI languagesNameTranslatedTextsTypeAPI;
    private final LanguagesNameMapOfTranslatedTextsTypeAPI languagesNameMapOfTranslatedTextsTypeAPI;
    private final LanguagesNameTypeAPI languagesNameTypeAPI;
    private final LanguagesTypeAPI languagesTypeAPI;
    private final LocalizedCharacterTranslatedTextsTypeAPI localizedCharacterTranslatedTextsTypeAPI;
    private final LocalizedCharacterMapOfTranslatedTextsTypeAPI localizedCharacterMapOfTranslatedTextsTypeAPI;
    private final LocalizedCharacterTypeAPI localizedCharacterTypeAPI;
    private final LocalizedMetadataTranslatedTextsTypeAPI localizedMetadataTranslatedTextsTypeAPI;
    private final LocalizedMetadataMapOfTranslatedTextsTypeAPI localizedMetadataMapOfTranslatedTextsTypeAPI;
    private final LocalizedMetadataTypeAPI localizedMetadataTypeAPI;
    private final MovieRatingsRatingReasonTranslatedTextsTypeAPI movieRatingsRatingReasonTranslatedTextsTypeAPI;
    private final MovieRatingsRatingReasonMapOfTranslatedTextsTypeAPI movieRatingsRatingReasonMapOfTranslatedTextsTypeAPI;
    private final MovieRatingsRatingReasonTypeAPI movieRatingsRatingReasonTypeAPI;
    private final MovieRatingsTypeAPI movieRatingsTypeAPI;
    private final MoviesAkaTranslatedTextsTypeAPI moviesAkaTranslatedTextsTypeAPI;
    private final MoviesAkaMapOfTranslatedTextsTypeAPI moviesAkaMapOfTranslatedTextsTypeAPI;
    private final MoviesAkaTypeAPI moviesAkaTypeAPI;
    private final MoviesDisplayNameTranslatedTextsTypeAPI moviesDisplayNameTranslatedTextsTypeAPI;
    private final MoviesDisplayNameMapOfTranslatedTextsTypeAPI moviesDisplayNameMapOfTranslatedTextsTypeAPI;
    private final MoviesDisplayNameTypeAPI moviesDisplayNameTypeAPI;
    private final MoviesOriginalTitleTranslatedTextsTypeAPI moviesOriginalTitleTranslatedTextsTypeAPI;
    private final MoviesOriginalTitleMapOfTranslatedTextsTypeAPI moviesOriginalTitleMapOfTranslatedTextsTypeAPI;
    private final MoviesOriginalTitleTypeAPI moviesOriginalTitleTypeAPI;
    private final MoviesShortDisplayNameTranslatedTextsTypeAPI moviesShortDisplayNameTranslatedTextsTypeAPI;
    private final MoviesShortDisplayNameMapOfTranslatedTextsTypeAPI moviesShortDisplayNameMapOfTranslatedTextsTypeAPI;
    private final MoviesShortDisplayNameTypeAPI moviesShortDisplayNameTypeAPI;
    private final MoviesSiteSynopsisTranslatedTextsTypeAPI moviesSiteSynopsisTranslatedTextsTypeAPI;
    private final MoviesSiteSynopsisMapOfTranslatedTextsTypeAPI moviesSiteSynopsisMapOfTranslatedTextsTypeAPI;
    private final MoviesSiteSynopsisTypeAPI moviesSiteSynopsisTypeAPI;
    private final MoviesTransliteratedTranslatedTextsTypeAPI moviesTransliteratedTranslatedTextsTypeAPI;
    private final MoviesTransliteratedMapOfTranslatedTextsTypeAPI moviesTransliteratedMapOfTranslatedTextsTypeAPI;
    private final MoviesTransliteratedTypeAPI moviesTransliteratedTypeAPI;
    private final MoviesTvSynopsisTranslatedTextsTypeAPI moviesTvSynopsisTranslatedTextsTypeAPI;
    private final MoviesTvSynopsisMapOfTranslatedTextsTypeAPI moviesTvSynopsisMapOfTranslatedTextsTypeAPI;
    private final MoviesTvSynopsisTypeAPI moviesTvSynopsisTypeAPI;
    private final MoviesTypeAPI moviesTypeAPI;
    private final OriginServersTypeAPI originServersTypeAPI;
    private final PersonAliasesNameTranslatedTextsTypeAPI personAliasesNameTranslatedTextsTypeAPI;
    private final PersonAliasesNameMapOfTranslatedTextsTypeAPI personAliasesNameMapOfTranslatedTextsTypeAPI;
    private final PersonAliasesNameTypeAPI personAliasesNameTypeAPI;
    private final PersonAliasesTypeAPI personAliasesTypeAPI;
    private final PersonArtworkAttributesTypeAPI personArtworkAttributesTypeAPI;
    private final PersonArtworkDerivativesTypeAPI personArtworkDerivativesTypeAPI;
    private final PersonArtworkArrayOfDerivativesTypeAPI personArtworkArrayOfDerivativesTypeAPI;
    private final PersonArtworkLocalesTerritoryCodesTypeAPI personArtworkLocalesTerritoryCodesTypeAPI;
    private final PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI personArtworkLocalesArrayOfTerritoryCodesTypeAPI;
    private final PersonArtworkLocalesTypeAPI personArtworkLocalesTypeAPI;
    private final PersonArtworkArrayOfLocalesTypeAPI personArtworkArrayOfLocalesTypeAPI;
    private final PersonArtworkTypeAPI personArtworkTypeAPI;
    private final PersonsBioTranslatedTextsTypeAPI personsBioTranslatedTextsTypeAPI;
    private final PersonsBioMapOfTranslatedTextsTypeAPI personsBioMapOfTranslatedTextsTypeAPI;
    private final PersonsBioTypeAPI personsBioTypeAPI;
    private final PersonsNameTranslatedTextsTypeAPI personsNameTranslatedTextsTypeAPI;
    private final PersonsNameMapOfTranslatedTextsTypeAPI personsNameMapOfTranslatedTextsTypeAPI;
    private final PersonsNameTypeAPI personsNameTypeAPI;
    private final PersonsTypeAPI personsTypeAPI;
    private final ProtectionTypesTypeAPI protectionTypesTypeAPI;
    private final RatingsDescriptionTranslatedTextsTypeAPI ratingsDescriptionTranslatedTextsTypeAPI;
    private final RatingsDescriptionMapOfTranslatedTextsTypeAPI ratingsDescriptionMapOfTranslatedTextsTypeAPI;
    private final RatingsDescriptionTypeAPI ratingsDescriptionTypeAPI;
    private final RatingsRatingCodeTranslatedTextsTypeAPI ratingsRatingCodeTranslatedTextsTypeAPI;
    private final RatingsRatingCodeMapOfTranslatedTextsTypeAPI ratingsRatingCodeMapOfTranslatedTextsTypeAPI;
    private final RatingsRatingCodeTypeAPI ratingsRatingCodeTypeAPI;
    private final RatingsTypeAPI ratingsTypeAPI;
    private final RolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI rolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI;
    private final RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI;
    private final RolloutPhasesElementsArtwork_newTypeAPI rolloutPhasesElementsArtwork_newTypeAPI;
    private final RolloutPhasesElementsLocalized_metadataTypeAPI rolloutPhasesElementsLocalized_metadataTypeAPI;
    private final RolloutPhasesElementsTrailersSupplementalInfoTypeAPI rolloutPhasesElementsTrailersSupplementalInfoTypeAPI;
    private final RolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI rolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI;
    private final RolloutPhasesElementsTrailersTypeAPI rolloutPhasesElementsTrailersTypeAPI;
    private final RolloutPhasesElementsArrayOfTrailersTypeAPI rolloutPhasesElementsArrayOfTrailersTypeAPI;
    private final RolloutPhasesElementsTypeAPI rolloutPhasesElementsTypeAPI;
    private final RolloutPhasesTypeAPI rolloutPhasesTypeAPI;
    private final RolloutArrayOfPhasesTypeAPI rolloutArrayOfPhasesTypeAPI;
    private final RolloutTypeAPI rolloutTypeAPI;
    private final ShowMemberTypesDisplayNameTranslatedTextsTypeAPI showMemberTypesDisplayNameTranslatedTextsTypeAPI;
    private final ShowMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI showMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI;
    private final ShowMemberTypesDisplayNameTypeAPI showMemberTypesDisplayNameTypeAPI;
    private final ShowMemberTypesTypeAPI showMemberTypesTypeAPI;
    private final StorageGroupsCountriesTypeAPI storageGroupsCountriesTypeAPI;
    private final StorageGroupsArrayOfCountriesTypeAPI storageGroupsArrayOfCountriesTypeAPI;
    private final StorageGroupsTypeAPI storageGroupsTypeAPI;
    private final Stories_SynopsesHooksTranslatedTextsTypeAPI stories_SynopsesHooksTranslatedTextsTypeAPI;
    private final Stories_SynopsesHooksMapOfTranslatedTextsTypeAPI stories_SynopsesHooksMapOfTranslatedTextsTypeAPI;
    private final Stories_SynopsesHooksTypeAPI stories_SynopsesHooksTypeAPI;
    private final Stories_SynopsesArrayOfHooksTypeAPI stories_SynopsesArrayOfHooksTypeAPI;
    private final Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI stories_SynopsesNarrativeTextTranslatedTextsTypeAPI;
    private final Stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI;
    private final Stories_SynopsesNarrativeTextTypeAPI stories_SynopsesNarrativeTextTypeAPI;
    private final Stories_SynopsesTypeAPI stories_SynopsesTypeAPI;
    private final StreamProfileGroupsTypeAPI streamProfileGroupsTypeAPI;
    private final StreamProfilesTypeAPI streamProfilesTypeAPI;
    private final TerritoryCountriesCountryCodesTypeAPI territoryCountriesCountryCodesTypeAPI;
    private final TerritoryCountriesArrayOfCountryCodesTypeAPI territoryCountriesArrayOfCountryCodesTypeAPI;
    private final TerritoryCountriesTypeAPI territoryCountriesTypeAPI;
    private final TopNAttributesTypeAPI topNAttributesTypeAPI;
    private final TopNArrayOfAttributesTypeAPI topNArrayOfAttributesTypeAPI;
    private final TopNTypeAPI topNTypeAPI;
    private final TrailerTrailersThemesTypeAPI trailerTrailersThemesTypeAPI;
    private final TrailerTrailersArrayOfThemesTypeAPI trailerTrailersArrayOfThemesTypeAPI;
    private final TrailerTrailersTypeAPI trailerTrailersTypeAPI;
    private final TrailerArrayOfTrailersTypeAPI trailerArrayOfTrailersTypeAPI;
    private final TrailerTypeAPI trailerTypeAPI;
    private final VMSAwardTypeAPI vMSAwardTypeAPI;
    private final VideoArtWorkAttributesTypeAPI videoArtWorkAttributesTypeAPI;
    private final VideoArtWorkArrayOfAttributesTypeAPI videoArtWorkArrayOfAttributesTypeAPI;
    private final VideoArtWorkExtensionsTypeAPI videoArtWorkExtensionsTypeAPI;
    private final VideoArtWorkArrayOfExtensionsTypeAPI videoArtWorkArrayOfExtensionsTypeAPI;
    private final VideoArtWorkLocalesTerritoryCodesTypeAPI videoArtWorkLocalesTerritoryCodesTypeAPI;
    private final VideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI videoArtWorkLocalesArrayOfTerritoryCodesTypeAPI;
    private final VideoArtWorkLocalesTypeAPI videoArtWorkLocalesTypeAPI;
    private final VideoArtWorkArrayOfLocalesTypeAPI videoArtWorkArrayOfLocalesTypeAPI;
    private final VideoArtWorkRecipesTypeAPI videoArtWorkRecipesTypeAPI;
    private final VideoArtWorkArrayOfRecipesTypeAPI videoArtWorkArrayOfRecipesTypeAPI;
    private final VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI videoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI;
    private final VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI;
    private final VideoArtWorkSourceAttributesIDENTIFIERSTypeAPI videoArtWorkSourceAttributesIDENTIFIERSTypeAPI;
    private final VideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI videoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI;
    private final VideoArtWorkSourceAttributesPERSON_IDSTypeAPI videoArtWorkSourceAttributesPERSON_IDSTypeAPI;
    private final VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI videoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI;
    private final VideoArtWorkSourceAttributesThemesTypeAPI videoArtWorkSourceAttributesThemesTypeAPI;
    private final VideoArtWorkSourceAttributesArrayOfThemesTypeAPI videoArtWorkSourceAttributesArrayOfThemesTypeAPI;
    private final VideoArtWorkSourceAttributesTypeAPI videoArtWorkSourceAttributesTypeAPI;
    private final VideoArtWorkTypeAPI videoArtWorkTypeAPI;
    private final VideoAwardAwardTypeAPI videoAwardAwardTypeAPI;
    private final VideoAwardArrayOfAwardTypeAPI videoAwardArrayOfAwardTypeAPI;
    private final VideoAwardTypeAPI videoAwardTypeAPI;
    private final VideoDateWindowTypeAPI videoDateWindowTypeAPI;
    private final VideoDateArrayOfWindowTypeAPI videoDateArrayOfWindowTypeAPI;
    private final VideoDateTypeAPI videoDateTypeAPI;
    private final VideoDisplaySetSetsChildrenChildrenTypeAPI videoDisplaySetSetsChildrenChildrenTypeAPI;
    private final VideoDisplaySetSetsChildrenArrayOfChildrenTypeAPI videoDisplaySetSetsChildrenArrayOfChildrenTypeAPI;
    private final VideoDisplaySetSetsChildrenTypeAPI videoDisplaySetSetsChildrenTypeAPI;
    private final VideoDisplaySetSetsArrayOfChildrenTypeAPI videoDisplaySetSetsArrayOfChildrenTypeAPI;
    private final VideoDisplaySetSetsTypeAPI videoDisplaySetSetsTypeAPI;
    private final VideoDisplaySetArrayOfSetsTypeAPI videoDisplaySetArrayOfSetsTypeAPI;
    private final VideoDisplaySetTypeAPI videoDisplaySetTypeAPI;
    private final VideoGeneralAliasesTypeAPI videoGeneralAliasesTypeAPI;
    private final VideoGeneralArrayOfAliasesTypeAPI videoGeneralArrayOfAliasesTypeAPI;
    private final VideoGeneralEpisodeTypesTypeAPI videoGeneralEpisodeTypesTypeAPI;
    private final VideoGeneralArrayOfEpisodeTypesTypeAPI videoGeneralArrayOfEpisodeTypesTypeAPI;
    private final VideoGeneralTitleTypesTypeAPI videoGeneralTitleTypesTypeAPI;
    private final VideoGeneralArrayOfTitleTypesTypeAPI videoGeneralArrayOfTitleTypesTypeAPI;
    private final VideoGeneralTypeAPI videoGeneralTypeAPI;
    private final VideoPersonAliasTypeAPI videoPersonAliasTypeAPI;
    private final VideoPersonArrayOfAliasTypeAPI videoPersonArrayOfAliasTypeAPI;
    private final VideoPersonCastTypeAPI videoPersonCastTypeAPI;
    private final VideoPersonArrayOfCastTypeAPI videoPersonArrayOfCastTypeAPI;
    private final VideoPersonTypeAPI videoPersonTypeAPI;
    private final VideoRatingRatingReasonIdsTypeAPI videoRatingRatingReasonIdsTypeAPI;
    private final VideoRatingRatingReasonArrayOfIdsTypeAPI videoRatingRatingReasonArrayOfIdsTypeAPI;
    private final VideoRatingRatingReasonTypeAPI videoRatingRatingReasonTypeAPI;
    private final VideoRatingRatingTypeAPI videoRatingRatingTypeAPI;
    private final VideoRatingArrayOfRatingTypeAPI videoRatingArrayOfRatingTypeAPI;
    private final VideoRatingTypeAPI videoRatingTypeAPI;
    private final VideoRightsFlagsFirstDisplayDatesTypeAPI videoRightsFlagsFirstDisplayDatesTypeAPI;
    private final VideoRightsFlagsMapOfFirstDisplayDatesTypeAPI videoRightsFlagsMapOfFirstDisplayDatesTypeAPI;
    private final VideoRightsFlagsTypeAPI videoRightsFlagsTypeAPI;
    private final VideoRightsRightsContractsAssetsTypeAPI videoRightsRightsContractsAssetsTypeAPI;
    private final VideoRightsRightsContractsArrayOfAssetsTypeAPI videoRightsRightsContractsArrayOfAssetsTypeAPI;
    private final VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI;
    private final VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI;
    private final VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI videoRightsRightsContractsDisallowedAssetBundlesTypeAPI;
    private final VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI videoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI;
    private final VideoRightsRightsContractsPackagesTypeAPI videoRightsRightsContractsPackagesTypeAPI;
    private final VideoRightsRightsContractsArrayOfPackagesTypeAPI videoRightsRightsContractsArrayOfPackagesTypeAPI;
    private final VideoRightsRightsContractsTypeAPI videoRightsRightsContractsTypeAPI;
    private final VideoRightsRightsArrayOfContractsTypeAPI videoRightsRightsArrayOfContractsTypeAPI;
    private final VideoRightsRightsWindowsContractIdsTypeAPI videoRightsRightsWindowsContractIdsTypeAPI;
    private final VideoRightsRightsWindowsArrayOfContractIdsTypeAPI videoRightsRightsWindowsArrayOfContractIdsTypeAPI;
    private final VideoRightsRightsWindowsTypeAPI videoRightsRightsWindowsTypeAPI;
    private final VideoRightsRightsArrayOfWindowsTypeAPI videoRightsRightsArrayOfWindowsTypeAPI;
    private final VideoRightsRightsTypeAPI videoRightsRightsTypeAPI;
    private final VideoRightsTypeAPI videoRightsTypeAPI;
    private final VideoTypeTypeMediaTypeAPI videoTypeTypeMediaTypeAPI;
    private final VideoTypeTypeArrayOfMediaTypeAPI videoTypeTypeArrayOfMediaTypeAPI;
    private final VideoTypeTypeTypeAPI videoTypeTypeTypeAPI;
    private final VideoTypeArrayOfTypeTypeAPI videoTypeArrayOfTypeTypeAPI;
    private final VideoTypeTypeAPI videoTypeTypeAPI;

    private final HollowObjectProvider awardsDescriptionTranslatedTextsProvider;
    private final HollowObjectProvider awardsDescriptionProvider;
    private final HollowObjectProvider characterQuotesProvider;
    private final HollowObjectProvider characterArrayOfQuotesProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider;
    private final HollowObjectProvider mapKeyProvider;
    private final HollowObjectProvider rolloutLaunchDatesProvider;
    private final HollowObjectProvider rolloutMapOfLaunchDatesProvider;
    private final HollowObjectProvider rolloutPhasesElementsArtworkProvider;
    private final HollowObjectProvider rolloutPhasesElementsArrayOfArtworkProvider;
    private final HollowObjectProvider rolloutPhasesElementsCastProvider;
    private final HollowObjectProvider rolloutPhasesElementsArrayOfCastProvider;
    private final HollowObjectProvider rolloutPhasesElementsCharactersProvider;
    private final HollowObjectProvider rolloutPhasesElementsArrayOfCharactersProvider;
    private final HollowObjectProvider rolloutPhasesWindowsProvider;
    private final HollowObjectProvider rolloutPhasesMapOfWindowsProvider;
    private final HollowObjectProvider streamProfileGroupsStreamProfileIdsProvider;
    private final HollowObjectProvider streamProfileGroupsArrayOfStreamProfileIdsProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider altGenresAlternateNamesTranslatedTextsProvider;
    private final HollowObjectProvider altGenresAlternateNamesMapOfTranslatedTextsProvider;
    private final HollowObjectProvider altGenresAlternateNamesProvider;
    private final HollowObjectProvider altGenresArrayOfAlternateNamesProvider;
    private final HollowObjectProvider altGenresDisplayNameTranslatedTextsProvider;
    private final HollowObjectProvider altGenresDisplayNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider altGenresDisplayNameProvider;
    private final HollowObjectProvider altGenresShortNameTranslatedTextsProvider;
    private final HollowObjectProvider altGenresShortNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider altGenresShortNameProvider;
    private final HollowObjectProvider altGenresProvider;
    private final HollowObjectProvider artWorkImageFormatProvider;
    private final HollowObjectProvider artWorkImageTypeProvider;
    private final HollowObjectProvider artworkRecipeProvider;
    private final HollowObjectProvider assetMetaDatasTrackLabelsTranslatedTextsProvider;
    private final HollowObjectProvider assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider;
    private final HollowObjectProvider assetMetaDatasTrackLabelsProvider;
    private final HollowObjectProvider assetMetaDatasProvider;
    private final HollowObjectProvider awardsAlternateNameTranslatedTextsProvider;
    private final HollowObjectProvider awardsAlternateNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider awardsAlternateNameProvider;
    private final HollowObjectProvider awardsAwardNameTranslatedTextsProvider;
    private final HollowObjectProvider awardsAwardNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider awardsAwardNameProvider;
    private final HollowObjectProvider awardsProvider;
    private final HollowObjectProvider bcp47CodeProvider;
    private final HollowObjectProvider cSMReviewProvider;
    private final HollowObjectProvider cacheDeploymentIntentProvider;
    private final HollowObjectProvider categoriesDisplayNameTranslatedTextsProvider;
    private final HollowObjectProvider categoriesDisplayNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider categoriesDisplayNameProvider;
    private final HollowObjectProvider categoriesShortNameTranslatedTextsProvider;
    private final HollowObjectProvider categoriesShortNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider categoriesShortNameProvider;
    private final HollowObjectProvider categoriesProvider;
    private final HollowObjectProvider categoryGroupsCategoryGroupNameTranslatedTextsProvider;
    private final HollowObjectProvider categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider categoryGroupsCategoryGroupNameProvider;
    private final HollowObjectProvider categoryGroupsProvider;
    private final HollowObjectProvider cdnsProvider;
    private final HollowObjectProvider certificationSystemRatingProvider;
    private final HollowObjectProvider certificationSystemArrayOfRatingProvider;
    private final HollowObjectProvider certificationSystemProvider;
    private final HollowObjectProvider certificationsDescriptionTranslatedTextsProvider;
    private final HollowObjectProvider certificationsDescriptionMapOfTranslatedTextsProvider;
    private final HollowObjectProvider certificationsDescriptionProvider;
    private final HollowObjectProvider certificationsNameTranslatedTextsProvider;
    private final HollowObjectProvider certificationsNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider certificationsNameProvider;
    private final HollowObjectProvider certificationsProvider;
    private final HollowObjectProvider characterArtworkAttributesProvider;
    private final HollowObjectProvider characterArtworkDerivativesProvider;
    private final HollowObjectProvider characterArtworkArrayOfDerivativesProvider;
    private final HollowObjectProvider characterArtworkLocalesTerritoryCodesProvider;
    private final HollowObjectProvider characterArtworkLocalesArrayOfTerritoryCodesProvider;
    private final HollowObjectProvider characterArtworkLocalesProvider;
    private final HollowObjectProvider characterArtworkArrayOfLocalesProvider;
    private final HollowObjectProvider characterArtworkProvider;
    private final HollowObjectProvider characterElementsProvider;
    private final HollowObjectProvider characterProvider;
    private final HollowObjectProvider charactersBTranslatedTextsProvider;
    private final HollowObjectProvider charactersBMapOfTranslatedTextsProvider;
    private final HollowObjectProvider charactersBProvider;
    private final HollowObjectProvider charactersCnTranslatedTextsProvider;
    private final HollowObjectProvider charactersCnMapOfTranslatedTextsProvider;
    private final HollowObjectProvider charactersCnProvider;
    private final HollowObjectProvider charactersProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsDescriptionTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsDescriptionProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsNameTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsNameProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsRatingDescriptionsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsRatingRatingCodesProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsRatingProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsArrayOfRatingProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryListProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsArrayOfCountryListProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsCountryRatingsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsRatingsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsArrayOfRatingsProvider;
    private final HollowObjectProvider consolidatedVideoRatingsProvider;
    private final HollowObjectProvider defaultExtensionRecipeProvider;
    private final HollowObjectProvider deployablePackagesCountryCodesProvider;
    private final HollowObjectProvider deployablePackagesArrayOfCountryCodesProvider;
    private final HollowObjectProvider deployablePackagesProvider;
    private final HollowObjectProvider drmSystemIdentifiersProvider;
    private final HollowObjectProvider episodesEpisodeNameTranslatedTextsProvider;
    private final HollowObjectProvider episodesEpisodeNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider episodesEpisodeNameProvider;
    private final HollowObjectProvider episodesProvider;
    private final HollowObjectProvider festivalsCopyrightTranslatedTextsProvider;
    private final HollowObjectProvider festivalsCopyrightMapOfTranslatedTextsProvider;
    private final HollowObjectProvider festivalsCopyrightProvider;
    private final HollowObjectProvider festivalsDescriptionTranslatedTextsProvider;
    private final HollowObjectProvider festivalsDescriptionMapOfTranslatedTextsProvider;
    private final HollowObjectProvider festivalsDescriptionProvider;
    private final HollowObjectProvider festivalsFestivalNameTranslatedTextsProvider;
    private final HollowObjectProvider festivalsFestivalNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider festivalsFestivalNameProvider;
    private final HollowObjectProvider festivalsShortNameTranslatedTextsProvider;
    private final HollowObjectProvider festivalsShortNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider festivalsShortNameProvider;
    private final HollowObjectProvider festivalsSingularNameTranslatedTextsProvider;
    private final HollowObjectProvider festivalsSingularNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider festivalsSingularNameProvider;
    private final HollowObjectProvider festivalsProvider;
    private final HollowObjectProvider languagesNameTranslatedTextsProvider;
    private final HollowObjectProvider languagesNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider languagesNameProvider;
    private final HollowObjectProvider languagesProvider;
    private final HollowObjectProvider localizedCharacterTranslatedTextsProvider;
    private final HollowObjectProvider localizedCharacterMapOfTranslatedTextsProvider;
    private final HollowObjectProvider localizedCharacterProvider;
    private final HollowObjectProvider localizedMetadataTranslatedTextsProvider;
    private final HollowObjectProvider localizedMetadataMapOfTranslatedTextsProvider;
    private final HollowObjectProvider localizedMetadataProvider;
    private final HollowObjectProvider movieRatingsRatingReasonTranslatedTextsProvider;
    private final HollowObjectProvider movieRatingsRatingReasonMapOfTranslatedTextsProvider;
    private final HollowObjectProvider movieRatingsRatingReasonProvider;
    private final HollowObjectProvider movieRatingsProvider;
    private final HollowObjectProvider moviesAkaTranslatedTextsProvider;
    private final HollowObjectProvider moviesAkaMapOfTranslatedTextsProvider;
    private final HollowObjectProvider moviesAkaProvider;
    private final HollowObjectProvider moviesDisplayNameTranslatedTextsProvider;
    private final HollowObjectProvider moviesDisplayNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider moviesDisplayNameProvider;
    private final HollowObjectProvider moviesOriginalTitleTranslatedTextsProvider;
    private final HollowObjectProvider moviesOriginalTitleMapOfTranslatedTextsProvider;
    private final HollowObjectProvider moviesOriginalTitleProvider;
    private final HollowObjectProvider moviesShortDisplayNameTranslatedTextsProvider;
    private final HollowObjectProvider moviesShortDisplayNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider moviesShortDisplayNameProvider;
    private final HollowObjectProvider moviesSiteSynopsisTranslatedTextsProvider;
    private final HollowObjectProvider moviesSiteSynopsisMapOfTranslatedTextsProvider;
    private final HollowObjectProvider moviesSiteSynopsisProvider;
    private final HollowObjectProvider moviesTransliteratedTranslatedTextsProvider;
    private final HollowObjectProvider moviesTransliteratedMapOfTranslatedTextsProvider;
    private final HollowObjectProvider moviesTransliteratedProvider;
    private final HollowObjectProvider moviesTvSynopsisTranslatedTextsProvider;
    private final HollowObjectProvider moviesTvSynopsisMapOfTranslatedTextsProvider;
    private final HollowObjectProvider moviesTvSynopsisProvider;
    private final HollowObjectProvider moviesProvider;
    private final HollowObjectProvider originServersProvider;
    private final HollowObjectProvider personAliasesNameTranslatedTextsProvider;
    private final HollowObjectProvider personAliasesNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider personAliasesNameProvider;
    private final HollowObjectProvider personAliasesProvider;
    private final HollowObjectProvider personArtworkAttributesProvider;
    private final HollowObjectProvider personArtworkDerivativesProvider;
    private final HollowObjectProvider personArtworkArrayOfDerivativesProvider;
    private final HollowObjectProvider personArtworkLocalesTerritoryCodesProvider;
    private final HollowObjectProvider personArtworkLocalesArrayOfTerritoryCodesProvider;
    private final HollowObjectProvider personArtworkLocalesProvider;
    private final HollowObjectProvider personArtworkArrayOfLocalesProvider;
    private final HollowObjectProvider personArtworkProvider;
    private final HollowObjectProvider personsBioTranslatedTextsProvider;
    private final HollowObjectProvider personsBioMapOfTranslatedTextsProvider;
    private final HollowObjectProvider personsBioProvider;
    private final HollowObjectProvider personsNameTranslatedTextsProvider;
    private final HollowObjectProvider personsNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider personsNameProvider;
    private final HollowObjectProvider personsProvider;
    private final HollowObjectProvider protectionTypesProvider;
    private final HollowObjectProvider ratingsDescriptionTranslatedTextsProvider;
    private final HollowObjectProvider ratingsDescriptionMapOfTranslatedTextsProvider;
    private final HollowObjectProvider ratingsDescriptionProvider;
    private final HollowObjectProvider ratingsRatingCodeTranslatedTextsProvider;
    private final HollowObjectProvider ratingsRatingCodeMapOfTranslatedTextsProvider;
    private final HollowObjectProvider ratingsRatingCodeProvider;
    private final HollowObjectProvider ratingsProvider;
    private final HollowObjectProvider rolloutPhasesElementsArtwork_newSourceFileIdsProvider;
    private final HollowObjectProvider rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider;
    private final HollowObjectProvider rolloutPhasesElementsArtwork_newProvider;
    private final HollowObjectProvider rolloutPhasesElementsLocalized_metadataProvider;
    private final HollowObjectProvider rolloutPhasesElementsTrailersSupplementalInfoProvider;
    private final HollowObjectProvider rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider;
    private final HollowObjectProvider rolloutPhasesElementsTrailersProvider;
    private final HollowObjectProvider rolloutPhasesElementsArrayOfTrailersProvider;
    private final HollowObjectProvider rolloutPhasesElementsProvider;
    private final HollowObjectProvider rolloutPhasesProvider;
    private final HollowObjectProvider rolloutArrayOfPhasesProvider;
    private final HollowObjectProvider rolloutProvider;
    private final HollowObjectProvider showMemberTypesDisplayNameTranslatedTextsProvider;
    private final HollowObjectProvider showMemberTypesDisplayNameMapOfTranslatedTextsProvider;
    private final HollowObjectProvider showMemberTypesDisplayNameProvider;
    private final HollowObjectProvider showMemberTypesProvider;
    private final HollowObjectProvider storageGroupsCountriesProvider;
    private final HollowObjectProvider storageGroupsArrayOfCountriesProvider;
    private final HollowObjectProvider storageGroupsProvider;
    private final HollowObjectProvider stories_SynopsesHooksTranslatedTextsProvider;
    private final HollowObjectProvider stories_SynopsesHooksMapOfTranslatedTextsProvider;
    private final HollowObjectProvider stories_SynopsesHooksProvider;
    private final HollowObjectProvider stories_SynopsesArrayOfHooksProvider;
    private final HollowObjectProvider stories_SynopsesNarrativeTextTranslatedTextsProvider;
    private final HollowObjectProvider stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider;
    private final HollowObjectProvider stories_SynopsesNarrativeTextProvider;
    private final HollowObjectProvider stories_SynopsesProvider;
    private final HollowObjectProvider streamProfileGroupsProvider;
    private final HollowObjectProvider streamProfilesProvider;
    private final HollowObjectProvider territoryCountriesCountryCodesProvider;
    private final HollowObjectProvider territoryCountriesArrayOfCountryCodesProvider;
    private final HollowObjectProvider territoryCountriesProvider;
    private final HollowObjectProvider topNAttributesProvider;
    private final HollowObjectProvider topNArrayOfAttributesProvider;
    private final HollowObjectProvider topNProvider;
    private final HollowObjectProvider trailerTrailersThemesProvider;
    private final HollowObjectProvider trailerTrailersArrayOfThemesProvider;
    private final HollowObjectProvider trailerTrailersProvider;
    private final HollowObjectProvider trailerArrayOfTrailersProvider;
    private final HollowObjectProvider trailerProvider;
    private final HollowObjectProvider vMSAwardProvider;
    private final HollowObjectProvider videoArtWorkAttributesProvider;
    private final HollowObjectProvider videoArtWorkArrayOfAttributesProvider;
    private final HollowObjectProvider videoArtWorkExtensionsProvider;
    private final HollowObjectProvider videoArtWorkArrayOfExtensionsProvider;
    private final HollowObjectProvider videoArtWorkLocalesTerritoryCodesProvider;
    private final HollowObjectProvider videoArtWorkLocalesArrayOfTerritoryCodesProvider;
    private final HollowObjectProvider videoArtWorkLocalesProvider;
    private final HollowObjectProvider videoArtWorkArrayOfLocalesProvider;
    private final HollowObjectProvider videoArtWorkRecipesProvider;
    private final HollowObjectProvider videoArtWorkArrayOfRecipesProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesIDENTIFIERSProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesPERSON_IDSProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesThemesProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesArrayOfThemesProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesProvider;
    private final HollowObjectProvider videoArtWorkProvider;
    private final HollowObjectProvider videoAwardAwardProvider;
    private final HollowObjectProvider videoAwardArrayOfAwardProvider;
    private final HollowObjectProvider videoAwardProvider;
    private final HollowObjectProvider videoDateWindowProvider;
    private final HollowObjectProvider videoDateArrayOfWindowProvider;
    private final HollowObjectProvider videoDateProvider;
    private final HollowObjectProvider videoDisplaySetSetsChildrenChildrenProvider;
    private final HollowObjectProvider videoDisplaySetSetsChildrenArrayOfChildrenProvider;
    private final HollowObjectProvider videoDisplaySetSetsChildrenProvider;
    private final HollowObjectProvider videoDisplaySetSetsArrayOfChildrenProvider;
    private final HollowObjectProvider videoDisplaySetSetsProvider;
    private final HollowObjectProvider videoDisplaySetArrayOfSetsProvider;
    private final HollowObjectProvider videoDisplaySetProvider;
    private final HollowObjectProvider videoGeneralAliasesProvider;
    private final HollowObjectProvider videoGeneralArrayOfAliasesProvider;
    private final HollowObjectProvider videoGeneralEpisodeTypesProvider;
    private final HollowObjectProvider videoGeneralArrayOfEpisodeTypesProvider;
    private final HollowObjectProvider videoGeneralTitleTypesProvider;
    private final HollowObjectProvider videoGeneralArrayOfTitleTypesProvider;
    private final HollowObjectProvider videoGeneralProvider;
    private final HollowObjectProvider videoPersonAliasProvider;
    private final HollowObjectProvider videoPersonArrayOfAliasProvider;
    private final HollowObjectProvider videoPersonCastProvider;
    private final HollowObjectProvider videoPersonArrayOfCastProvider;
    private final HollowObjectProvider videoPersonProvider;
    private final HollowObjectProvider videoRatingRatingReasonIdsProvider;
    private final HollowObjectProvider videoRatingRatingReasonArrayOfIdsProvider;
    private final HollowObjectProvider videoRatingRatingReasonProvider;
    private final HollowObjectProvider videoRatingRatingProvider;
    private final HollowObjectProvider videoRatingArrayOfRatingProvider;
    private final HollowObjectProvider videoRatingProvider;
    private final HollowObjectProvider videoRightsFlagsFirstDisplayDatesProvider;
    private final HollowObjectProvider videoRightsFlagsMapOfFirstDisplayDatesProvider;
    private final HollowObjectProvider videoRightsFlagsProvider;
    private final HollowObjectProvider videoRightsRightsContractsAssetsProvider;
    private final HollowObjectProvider videoRightsRightsContractsArrayOfAssetsProvider;
    private final HollowObjectProvider videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider;
    private final HollowObjectProvider videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider;
    private final HollowObjectProvider videoRightsRightsContractsDisallowedAssetBundlesProvider;
    private final HollowObjectProvider videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider;
    private final HollowObjectProvider videoRightsRightsContractsPackagesProvider;
    private final HollowObjectProvider videoRightsRightsContractsArrayOfPackagesProvider;
    private final HollowObjectProvider videoRightsRightsContractsProvider;
    private final HollowObjectProvider videoRightsRightsArrayOfContractsProvider;
    private final HollowObjectProvider videoRightsRightsWindowsContractIdsProvider;
    private final HollowObjectProvider videoRightsRightsWindowsArrayOfContractIdsProvider;
    private final HollowObjectProvider videoRightsRightsWindowsProvider;
    private final HollowObjectProvider videoRightsRightsArrayOfWindowsProvider;
    private final HollowObjectProvider videoRightsRightsProvider;
    private final HollowObjectProvider videoRightsProvider;
    private final HollowObjectProvider videoTypeTypeMediaProvider;
    private final HollowObjectProvider videoTypeTypeArrayOfMediaProvider;
    private final HollowObjectProvider videoTypeTypeProvider;
    private final HollowObjectProvider videoTypeArrayOfTypeProvider;
    private final HollowObjectProvider videoTypeProvider;

    public VMSHollowVideoInputAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public VMSHollowVideoInputAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public VMSHollowVideoInputAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public VMSHollowVideoInputAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, VMSHollowVideoInputAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("AwardsDescriptionTranslatedTexts","AwardsDescription","CharacterQuotes","CharacterArrayOfQuotes","ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIds","ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIds","ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories","MapKey","RolloutLaunchDates","RolloutMapOfLaunchDates","RolloutPhasesElementsArtwork","RolloutPhasesElementsArrayOfArtwork","RolloutPhasesElementsCast","RolloutPhasesElementsArrayOfCast","RolloutPhasesElementsCharacters","RolloutPhasesElementsArrayOfCharacters","RolloutPhasesWindows","RolloutPhasesMapOfWindows","StreamProfileGroupsStreamProfileIds","StreamProfileGroupsArrayOfStreamProfileIds","String","AltGenresAlternateNamesTranslatedTexts","AltGenresAlternateNamesMapOfTranslatedTexts","AltGenresAlternateNames","AltGenresArrayOfAlternateNames","AltGenresDisplayNameTranslatedTexts","AltGenresDisplayNameMapOfTranslatedTexts","AltGenresDisplayName","AltGenresShortNameTranslatedTexts","AltGenresShortNameMapOfTranslatedTexts","AltGenresShortName","AltGenres","ArtWorkImageFormat","ArtWorkImageType","ArtworkRecipe","AssetMetaDatasTrackLabelsTranslatedTexts","AssetMetaDatasTrackLabelsMapOfTranslatedTexts","AssetMetaDatasTrackLabels","AssetMetaDatas","AwardsAlternateNameTranslatedTexts","AwardsAlternateNameMapOfTranslatedTexts","AwardsAlternateName","AwardsAwardNameTranslatedTexts","AwardsAwardNameMapOfTranslatedTexts","AwardsAwardName","Awards","Bcp47Code","CSMReview","CacheDeploymentIntent","CategoriesDisplayNameTranslatedTexts","CategoriesDisplayNameMapOfTranslatedTexts","CategoriesDisplayName","CategoriesShortNameTranslatedTexts","CategoriesShortNameMapOfTranslatedTexts","CategoriesShortName","Categories","CategoryGroupsCategoryGroupNameTranslatedTexts","CategoryGroupsCategoryGroupNameMapOfTranslatedTexts","CategoryGroupsCategoryGroupName","CategoryGroups","Cdns","CertificationSystemRating","CertificationSystemArrayOfRating","CertificationSystem","CertificationsDescriptionTranslatedTexts","CertificationsDescriptionMapOfTranslatedTexts","CertificationsDescription","CertificationsNameTranslatedTexts","CertificationsNameMapOfTranslatedTexts","CertificationsName","Certifications","CharacterArtworkAttributes","CharacterArtworkDerivatives","CharacterArtworkArrayOfDerivatives","CharacterArtworkLocalesTerritoryCodes","CharacterArtworkLocalesArrayOfTerritoryCodes","CharacterArtworkLocales","CharacterArtworkArrayOfLocales","CharacterArtwork","CharacterElements","Character","CharactersBTranslatedTexts","CharactersBMapOfTranslatedTexts","CharactersB","CharactersCnTranslatedTexts","CharactersCnMapOfTranslatedTexts","CharactersCn","Characters","ConsolidatedCertificationSystemsDescriptionTranslatedTexts","ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTexts","ConsolidatedCertificationSystemsDescription","ConsolidatedCertificationSystemsNameTranslatedTexts","ConsolidatedCertificationSystemsNameMapOfTranslatedTexts","ConsolidatedCertificationSystemsName","ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTexts","ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTexts","ConsolidatedCertificationSystemsRatingDescriptions","ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTexts","ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTexts","ConsolidatedCertificationSystemsRatingRatingCodes","ConsolidatedCertificationSystemsRating","ConsolidatedCertificationSystemsArrayOfRating","ConsolidatedCertificationSystems","ConsolidatedVideoRatingsRatingsCountryList","ConsolidatedVideoRatingsRatingsArrayOfCountryList","ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTexts","ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTexts","ConsolidatedVideoRatingsRatingsCountryRatingsReasons","ConsolidatedVideoRatingsRatingsCountryRatings","ConsolidatedVideoRatingsRatingsArrayOfCountryRatings","ConsolidatedVideoRatingsRatings","ConsolidatedVideoRatingsArrayOfRatings","ConsolidatedVideoRatings","DefaultExtensionRecipe","DeployablePackagesCountryCodes","DeployablePackagesArrayOfCountryCodes","DeployablePackages","DrmSystemIdentifiers","EpisodesEpisodeNameTranslatedTexts","EpisodesEpisodeNameMapOfTranslatedTexts","EpisodesEpisodeName","Episodes","FestivalsCopyrightTranslatedTexts","FestivalsCopyrightMapOfTranslatedTexts","FestivalsCopyright","FestivalsDescriptionTranslatedTexts","FestivalsDescriptionMapOfTranslatedTexts","FestivalsDescription","FestivalsFestivalNameTranslatedTexts","FestivalsFestivalNameMapOfTranslatedTexts","FestivalsFestivalName","FestivalsShortNameTranslatedTexts","FestivalsShortNameMapOfTranslatedTexts","FestivalsShortName","FestivalsSingularNameTranslatedTexts","FestivalsSingularNameMapOfTranslatedTexts","FestivalsSingularName","Festivals","LanguagesNameTranslatedTexts","LanguagesNameMapOfTranslatedTexts","LanguagesName","Languages","LocalizedCharacterTranslatedTexts","LocalizedCharacterMapOfTranslatedTexts","LocalizedCharacter","LocalizedMetadataTranslatedTexts","LocalizedMetadataMapOfTranslatedTexts","LocalizedMetadata","MovieRatingsRatingReasonTranslatedTexts","MovieRatingsRatingReasonMapOfTranslatedTexts","MovieRatingsRatingReason","MovieRatings","MoviesAkaTranslatedTexts","MoviesAkaMapOfTranslatedTexts","MoviesAka","MoviesDisplayNameTranslatedTexts","MoviesDisplayNameMapOfTranslatedTexts","MoviesDisplayName","MoviesOriginalTitleTranslatedTexts","MoviesOriginalTitleMapOfTranslatedTexts","MoviesOriginalTitle","MoviesShortDisplayNameTranslatedTexts","MoviesShortDisplayNameMapOfTranslatedTexts","MoviesShortDisplayName","MoviesSiteSynopsisTranslatedTexts","MoviesSiteSynopsisMapOfTranslatedTexts","MoviesSiteSynopsis","MoviesTransliteratedTranslatedTexts","MoviesTransliteratedMapOfTranslatedTexts","MoviesTransliterated","MoviesTvSynopsisTranslatedTexts","MoviesTvSynopsisMapOfTranslatedTexts","MoviesTvSynopsis","Movies","OriginServers","PersonAliasesNameTranslatedTexts","PersonAliasesNameMapOfTranslatedTexts","PersonAliasesName","PersonAliases","PersonArtworkAttributes","PersonArtworkDerivatives","PersonArtworkArrayOfDerivatives","PersonArtworkLocalesTerritoryCodes","PersonArtworkLocalesArrayOfTerritoryCodes","PersonArtworkLocales","PersonArtworkArrayOfLocales","PersonArtwork","PersonsBioTranslatedTexts","PersonsBioMapOfTranslatedTexts","PersonsBio","PersonsNameTranslatedTexts","PersonsNameMapOfTranslatedTexts","PersonsName","Persons","ProtectionTypes","RatingsDescriptionTranslatedTexts","RatingsDescriptionMapOfTranslatedTexts","RatingsDescription","RatingsRatingCodeTranslatedTexts","RatingsRatingCodeMapOfTranslatedTexts","RatingsRatingCode","Ratings","RolloutPhasesElementsArtwork_newSourceFileIds","RolloutPhasesElementsArtwork_newArrayOfSourceFileIds","RolloutPhasesElementsArtwork_new","RolloutPhasesElementsLocalized_metadata","RolloutPhasesElementsTrailersSupplementalInfo","RolloutPhasesElementsTrailersMapOfSupplementalInfo","RolloutPhasesElementsTrailers","RolloutPhasesElementsArrayOfTrailers","RolloutPhasesElements","RolloutPhases","RolloutArrayOfPhases","Rollout","ShowMemberTypesDisplayNameTranslatedTexts","ShowMemberTypesDisplayNameMapOfTranslatedTexts","ShowMemberTypesDisplayName","ShowMemberTypes","StorageGroupsCountries","StorageGroupsArrayOfCountries","StorageGroups","Stories_SynopsesHooksTranslatedTexts","Stories_SynopsesHooksMapOfTranslatedTexts","Stories_SynopsesHooks","Stories_SynopsesArrayOfHooks","Stories_SynopsesNarrativeTextTranslatedTexts","Stories_SynopsesNarrativeTextMapOfTranslatedTexts","Stories_SynopsesNarrativeText","Stories_Synopses","StreamProfileGroups","StreamProfiles","TerritoryCountriesCountryCodes","TerritoryCountriesArrayOfCountryCodes","TerritoryCountries","TopNAttributes","TopNArrayOfAttributes","TopN","TrailerTrailersThemes","TrailerTrailersArrayOfThemes","TrailerTrailers","TrailerArrayOfTrailers","Trailer","VMSAward","VideoArtWorkAttributes","VideoArtWorkArrayOfAttributes","VideoArtWorkExtensions","VideoArtWorkArrayOfExtensions","VideoArtWorkLocalesTerritoryCodes","VideoArtWorkLocalesArrayOfTerritoryCodes","VideoArtWorkLocales","VideoArtWorkArrayOfLocales","VideoArtWorkRecipes","VideoArtWorkArrayOfRecipes","VideoArtWorkSourceAttributesAWARD_CAMPAIGNS","VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNS","VideoArtWorkSourceAttributesIDENTIFIERS","VideoArtWorkSourceAttributesArrayOfIDENTIFIERS","VideoArtWorkSourceAttributesPERSON_IDS","VideoArtWorkSourceAttributesArrayOfPERSON_IDS","VideoArtWorkSourceAttributesThemes","VideoArtWorkSourceAttributesArrayOfThemes","VideoArtWorkSourceAttributes","VideoArtWork","VideoAwardAward","VideoAwardArrayOfAward","VideoAward","VideoDateWindow","VideoDateArrayOfWindow","VideoDate","VideoDisplaySetSetsChildrenChildren","VideoDisplaySetSetsChildrenArrayOfChildren","VideoDisplaySetSetsChildren","VideoDisplaySetSetsArrayOfChildren","VideoDisplaySetSets","VideoDisplaySetArrayOfSets","VideoDisplaySet","VideoGeneralAliases","VideoGeneralArrayOfAliases","VideoGeneralEpisodeTypes","VideoGeneralArrayOfEpisodeTypes","VideoGeneralTitleTypes","VideoGeneralArrayOfTitleTypes","VideoGeneral","VideoPersonAlias","VideoPersonArrayOfAlias","VideoPersonCast","VideoPersonArrayOfCast","VideoPerson","VideoRatingRatingReasonIds","VideoRatingRatingReasonArrayOfIds","VideoRatingRatingReason","VideoRatingRating","VideoRatingArrayOfRating","VideoRating","VideoRightsFlagsFirstDisplayDates","VideoRightsFlagsMapOfFirstDisplayDates","VideoRightsFlags","VideoRightsRightsContractsAssets","VideoRightsRightsContractsArrayOfAssets","VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodes","VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodes","VideoRightsRightsContractsDisallowedAssetBundles","VideoRightsRightsContractsArrayOfDisallowedAssetBundles","VideoRightsRightsContractsPackages","VideoRightsRightsContractsArrayOfPackages","VideoRightsRightsContracts","VideoRightsRightsArrayOfContracts","VideoRightsRightsWindowsContractIds","VideoRightsRightsWindowsArrayOfContractIds","VideoRightsRightsWindows","VideoRightsRightsArrayOfWindows","VideoRightsRights","VideoRights","VideoTypeTypeMedia","VideoTypeTypeArrayOfMedia","VideoTypeType","VideoTypeArrayOfType","VideoType");

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsDescriptionTranslatedTexts");
        if(typeDataAccess != null) {
            awardsDescriptionTranslatedTextsTypeAPI = new AwardsDescriptionTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardsDescriptionTranslatedTextsTypeAPI = new AwardsDescriptionTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AwardsDescriptionTranslatedTexts"));
        }
        addTypeAPI(awardsDescriptionTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AwardsDescriptionTranslatedTexts");
        if(factory == null)
            factory = new AwardsDescriptionTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AwardsDescriptionTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsDescriptionTranslatedTextsProvider;
            awardsDescriptionTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, awardsDescriptionTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            awardsDescriptionTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsDescriptionTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsDescription");
        if(typeDataAccess != null) {
            awardsDescriptionTypeAPI = new AwardsDescriptionTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardsDescriptionTypeAPI = new AwardsDescriptionTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AwardsDescription"));
        }
        addTypeAPI(awardsDescriptionTypeAPI);
        factory = factoryOverrides.get("AwardsDescription");
        if(factory == null)
            factory = new AwardsDescriptionHollowFactory();
        if(cachedTypes.contains("AwardsDescription")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsDescriptionProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsDescriptionProvider;
            awardsDescriptionProvider = new HollowObjectCacheProvider(typeDataAccess, awardsDescriptionTypeAPI, factory, previousCacheProvider);
        } else {
            awardsDescriptionProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsDescriptionTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterQuotes");
        if(typeDataAccess != null) {
            characterQuotesTypeAPI = new CharacterQuotesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterQuotesTypeAPI = new CharacterQuotesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterQuotes"));
        }
        addTypeAPI(characterQuotesTypeAPI);
        factory = factoryOverrides.get("CharacterQuotes");
        if(factory == null)
            factory = new CharacterQuotesHollowFactory();
        if(cachedTypes.contains("CharacterQuotes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterQuotesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterQuotesProvider;
            characterQuotesProvider = new HollowObjectCacheProvider(typeDataAccess, characterQuotesTypeAPI, factory, previousCacheProvider);
        } else {
            characterQuotesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterQuotesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArrayOfQuotes");
        if(typeDataAccess != null) {
            characterArrayOfQuotesTypeAPI = new CharacterArrayOfQuotesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterArrayOfQuotesTypeAPI = new CharacterArrayOfQuotesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterArrayOfQuotes"));
        }
        addTypeAPI(characterArrayOfQuotesTypeAPI);
        factory = factoryOverrides.get("CharacterArrayOfQuotes");
        if(factory == null)
            factory = new CharacterArrayOfQuotesHollowFactory();
        if(cachedTypes.contains("CharacterArrayOfQuotes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArrayOfQuotesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArrayOfQuotesProvider;
            characterArrayOfQuotesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArrayOfQuotesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArrayOfQuotesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArrayOfQuotesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIds");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIds"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIds");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider;
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIds");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIds"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIds");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider;
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider;
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MapKey");
        if(typeDataAccess != null) {
            mapKeyTypeAPI = new MapKeyTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            mapKeyTypeAPI = new MapKeyTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MapKey"));
        }
        addTypeAPI(mapKeyTypeAPI);
        factory = factoryOverrides.get("MapKey");
        if(factory == null)
            factory = new MapKeyHollowFactory();
        if(cachedTypes.contains("MapKey")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapKeyProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapKeyProvider;
            mapKeyProvider = new HollowObjectCacheProvider(typeDataAccess, mapKeyTypeAPI, factory, previousCacheProvider);
        } else {
            mapKeyProvider = new HollowObjectFactoryProvider(typeDataAccess, mapKeyTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutLaunchDates");
        if(typeDataAccess != null) {
            rolloutLaunchDatesTypeAPI = new RolloutLaunchDatesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutLaunchDatesTypeAPI = new RolloutLaunchDatesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutLaunchDates"));
        }
        addTypeAPI(rolloutLaunchDatesTypeAPI);
        factory = factoryOverrides.get("RolloutLaunchDates");
        if(factory == null)
            factory = new RolloutLaunchDatesHollowFactory();
        if(cachedTypes.contains("RolloutLaunchDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutLaunchDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutLaunchDatesProvider;
            rolloutLaunchDatesProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutLaunchDatesTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutLaunchDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutLaunchDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutMapOfLaunchDates");
        if(typeDataAccess != null) {
            rolloutMapOfLaunchDatesTypeAPI = new RolloutMapOfLaunchDatesTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            rolloutMapOfLaunchDatesTypeAPI = new RolloutMapOfLaunchDatesTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RolloutMapOfLaunchDates"));
        }
        addTypeAPI(rolloutMapOfLaunchDatesTypeAPI);
        factory = factoryOverrides.get("RolloutMapOfLaunchDates");
        if(factory == null)
            factory = new RolloutMapOfLaunchDatesHollowFactory();
        if(cachedTypes.contains("RolloutMapOfLaunchDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutMapOfLaunchDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutMapOfLaunchDatesProvider;
            rolloutMapOfLaunchDatesProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutMapOfLaunchDatesTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutMapOfLaunchDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutMapOfLaunchDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArtwork");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArtworkTypeAPI = new RolloutPhasesElementsArtworkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArtworkTypeAPI = new RolloutPhasesElementsArtworkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsArtwork"));
        }
        addTypeAPI(rolloutPhasesElementsArtworkTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArtwork");
        if(factory == null)
            factory = new RolloutPhasesElementsArtworkHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArtworkProvider;
            rolloutPhasesElementsArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArrayOfArtwork");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArrayOfArtworkTypeAPI = new RolloutPhasesElementsArrayOfArtworkTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArrayOfArtworkTypeAPI = new RolloutPhasesElementsArrayOfArtworkTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhasesElementsArrayOfArtwork"));
        }
        addTypeAPI(rolloutPhasesElementsArrayOfArtworkTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArrayOfArtwork");
        if(factory == null)
            factory = new RolloutPhasesElementsArrayOfArtworkHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArrayOfArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArrayOfArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArrayOfArtworkProvider;
            rolloutPhasesElementsArrayOfArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArrayOfArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArrayOfArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArrayOfArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsCast");
        if(typeDataAccess != null) {
            rolloutPhasesElementsCastTypeAPI = new RolloutPhasesElementsCastTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsCastTypeAPI = new RolloutPhasesElementsCastTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsCast"));
        }
        addTypeAPI(rolloutPhasesElementsCastTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsCast");
        if(factory == null)
            factory = new RolloutPhasesElementsCastHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsCast")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsCastProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsCastProvider;
            rolloutPhasesElementsCastProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsCastTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsCastProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsCastTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArrayOfCast");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArrayOfCastTypeAPI = new RolloutPhasesElementsArrayOfCastTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArrayOfCastTypeAPI = new RolloutPhasesElementsArrayOfCastTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhasesElementsArrayOfCast"));
        }
        addTypeAPI(rolloutPhasesElementsArrayOfCastTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArrayOfCast");
        if(factory == null)
            factory = new RolloutPhasesElementsArrayOfCastHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArrayOfCast")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArrayOfCastProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArrayOfCastProvider;
            rolloutPhasesElementsArrayOfCastProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArrayOfCastTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArrayOfCastProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArrayOfCastTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsCharacters");
        if(typeDataAccess != null) {
            rolloutPhasesElementsCharactersTypeAPI = new RolloutPhasesElementsCharactersTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsCharactersTypeAPI = new RolloutPhasesElementsCharactersTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsCharacters"));
        }
        addTypeAPI(rolloutPhasesElementsCharactersTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsCharacters");
        if(factory == null)
            factory = new RolloutPhasesElementsCharactersHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsCharacters")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsCharactersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsCharactersProvider;
            rolloutPhasesElementsCharactersProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsCharactersTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsCharactersProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsCharactersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArrayOfCharacters");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArrayOfCharactersTypeAPI = new RolloutPhasesElementsArrayOfCharactersTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArrayOfCharactersTypeAPI = new RolloutPhasesElementsArrayOfCharactersTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhasesElementsArrayOfCharacters"));
        }
        addTypeAPI(rolloutPhasesElementsArrayOfCharactersTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArrayOfCharacters");
        if(factory == null)
            factory = new RolloutPhasesElementsArrayOfCharactersHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArrayOfCharacters")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArrayOfCharactersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArrayOfCharactersProvider;
            rolloutPhasesElementsArrayOfCharactersProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArrayOfCharactersTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArrayOfCharactersProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArrayOfCharactersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesWindows");
        if(typeDataAccess != null) {
            rolloutPhasesWindowsTypeAPI = new RolloutPhasesWindowsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesWindowsTypeAPI = new RolloutPhasesWindowsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesWindows"));
        }
        addTypeAPI(rolloutPhasesWindowsTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesWindows");
        if(factory == null)
            factory = new RolloutPhasesWindowsHollowFactory();
        if(cachedTypes.contains("RolloutPhasesWindows")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesWindowsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesWindowsProvider;
            rolloutPhasesWindowsProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesWindowsTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesWindowsProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesWindowsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesMapOfWindows");
        if(typeDataAccess != null) {
            rolloutPhasesMapOfWindowsTypeAPI = new RolloutPhasesMapOfWindowsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesMapOfWindowsTypeAPI = new RolloutPhasesMapOfWindowsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RolloutPhasesMapOfWindows"));
        }
        addTypeAPI(rolloutPhasesMapOfWindowsTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesMapOfWindows");
        if(factory == null)
            factory = new RolloutPhasesMapOfWindowsHollowFactory();
        if(cachedTypes.contains("RolloutPhasesMapOfWindows")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesMapOfWindowsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesMapOfWindowsProvider;
            rolloutPhasesMapOfWindowsProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesMapOfWindowsTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesMapOfWindowsProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesMapOfWindowsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamProfileGroupsStreamProfileIds");
        if(typeDataAccess != null) {
            streamProfileGroupsStreamProfileIdsTypeAPI = new StreamProfileGroupsStreamProfileIdsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamProfileGroupsStreamProfileIdsTypeAPI = new StreamProfileGroupsStreamProfileIdsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamProfileGroupsStreamProfileIds"));
        }
        addTypeAPI(streamProfileGroupsStreamProfileIdsTypeAPI);
        factory = factoryOverrides.get("StreamProfileGroupsStreamProfileIds");
        if(factory == null)
            factory = new StreamProfileGroupsStreamProfileIdsHollowFactory();
        if(cachedTypes.contains("StreamProfileGroupsStreamProfileIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamProfileGroupsStreamProfileIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamProfileGroupsStreamProfileIdsProvider;
            streamProfileGroupsStreamProfileIdsProvider = new HollowObjectCacheProvider(typeDataAccess, streamProfileGroupsStreamProfileIdsTypeAPI, factory, previousCacheProvider);
        } else {
            streamProfileGroupsStreamProfileIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, streamProfileGroupsStreamProfileIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamProfileGroupsArrayOfStreamProfileIds");
        if(typeDataAccess != null) {
            streamProfileGroupsArrayOfStreamProfileIdsTypeAPI = new StreamProfileGroupsArrayOfStreamProfileIdsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            streamProfileGroupsArrayOfStreamProfileIdsTypeAPI = new StreamProfileGroupsArrayOfStreamProfileIdsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "StreamProfileGroupsArrayOfStreamProfileIds"));
        }
        addTypeAPI(streamProfileGroupsArrayOfStreamProfileIdsTypeAPI);
        factory = factoryOverrides.get("StreamProfileGroupsArrayOfStreamProfileIds");
        if(factory == null)
            factory = new StreamProfileGroupsArrayOfStreamProfileIdsHollowFactory();
        if(cachedTypes.contains("StreamProfileGroupsArrayOfStreamProfileIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamProfileGroupsArrayOfStreamProfileIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamProfileGroupsArrayOfStreamProfileIdsProvider;
            streamProfileGroupsArrayOfStreamProfileIdsProvider = new HollowObjectCacheProvider(typeDataAccess, streamProfileGroupsArrayOfStreamProfileIdsTypeAPI, factory, previousCacheProvider);
        } else {
            streamProfileGroupsArrayOfStreamProfileIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, streamProfileGroupsArrayOfStreamProfileIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("String");
        if(typeDataAccess != null) {
            stringTypeAPI = new StringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stringTypeAPI = new StringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "String"));
        }
        addTypeAPI(stringTypeAPI);
        factory = factoryOverrides.get("String");
        if(factory == null)
            factory = new StringHollowFactory();
        if(cachedTypes.contains("String")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stringProvider;
            stringProvider = new HollowObjectCacheProvider(typeDataAccess, stringTypeAPI, factory, previousCacheProvider);
        } else {
            stringProvider = new HollowObjectFactoryProvider(typeDataAccess, stringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresAlternateNamesTranslatedTexts");
        if(typeDataAccess != null) {
            altGenresAlternateNamesTranslatedTextsTypeAPI = new AltGenresAlternateNamesTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            altGenresAlternateNamesTranslatedTextsTypeAPI = new AltGenresAlternateNamesTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AltGenresAlternateNamesTranslatedTexts"));
        }
        addTypeAPI(altGenresAlternateNamesTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AltGenresAlternateNamesTranslatedTexts");
        if(factory == null)
            factory = new AltGenresAlternateNamesTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AltGenresAlternateNamesTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresAlternateNamesTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresAlternateNamesTranslatedTextsProvider;
            altGenresAlternateNamesTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresAlternateNamesTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresAlternateNamesTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresAlternateNamesTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresAlternateNamesMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            altGenresAlternateNamesMapOfTranslatedTextsTypeAPI = new AltGenresAlternateNamesMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            altGenresAlternateNamesMapOfTranslatedTextsTypeAPI = new AltGenresAlternateNamesMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "AltGenresAlternateNamesMapOfTranslatedTexts"));
        }
        addTypeAPI(altGenresAlternateNamesMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AltGenresAlternateNamesMapOfTranslatedTexts");
        if(factory == null)
            factory = new AltGenresAlternateNamesMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AltGenresAlternateNamesMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresAlternateNamesMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresAlternateNamesMapOfTranslatedTextsProvider;
            altGenresAlternateNamesMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresAlternateNamesMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresAlternateNamesMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresAlternateNamesMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresAlternateNames");
        if(typeDataAccess != null) {
            altGenresAlternateNamesTypeAPI = new AltGenresAlternateNamesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            altGenresAlternateNamesTypeAPI = new AltGenresAlternateNamesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AltGenresAlternateNames"));
        }
        addTypeAPI(altGenresAlternateNamesTypeAPI);
        factory = factoryOverrides.get("AltGenresAlternateNames");
        if(factory == null)
            factory = new AltGenresAlternateNamesHollowFactory();
        if(cachedTypes.contains("AltGenresAlternateNames")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresAlternateNamesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresAlternateNamesProvider;
            altGenresAlternateNamesProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresAlternateNamesTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresAlternateNamesProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresAlternateNamesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresArrayOfAlternateNames");
        if(typeDataAccess != null) {
            altGenresArrayOfAlternateNamesTypeAPI = new AltGenresArrayOfAlternateNamesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            altGenresArrayOfAlternateNamesTypeAPI = new AltGenresArrayOfAlternateNamesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "AltGenresArrayOfAlternateNames"));
        }
        addTypeAPI(altGenresArrayOfAlternateNamesTypeAPI);
        factory = factoryOverrides.get("AltGenresArrayOfAlternateNames");
        if(factory == null)
            factory = new AltGenresArrayOfAlternateNamesHollowFactory();
        if(cachedTypes.contains("AltGenresArrayOfAlternateNames")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresArrayOfAlternateNamesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresArrayOfAlternateNamesProvider;
            altGenresArrayOfAlternateNamesProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresArrayOfAlternateNamesTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresArrayOfAlternateNamesProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresArrayOfAlternateNamesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresDisplayNameTranslatedTexts");
        if(typeDataAccess != null) {
            altGenresDisplayNameTranslatedTextsTypeAPI = new AltGenresDisplayNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            altGenresDisplayNameTranslatedTextsTypeAPI = new AltGenresDisplayNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AltGenresDisplayNameTranslatedTexts"));
        }
        addTypeAPI(altGenresDisplayNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AltGenresDisplayNameTranslatedTexts");
        if(factory == null)
            factory = new AltGenresDisplayNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AltGenresDisplayNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresDisplayNameTranslatedTextsProvider;
            altGenresDisplayNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresDisplayNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresDisplayNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresDisplayNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresDisplayNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            altGenresDisplayNameMapOfTranslatedTextsTypeAPI = new AltGenresDisplayNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            altGenresDisplayNameMapOfTranslatedTextsTypeAPI = new AltGenresDisplayNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "AltGenresDisplayNameMapOfTranslatedTexts"));
        }
        addTypeAPI(altGenresDisplayNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AltGenresDisplayNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new AltGenresDisplayNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AltGenresDisplayNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresDisplayNameMapOfTranslatedTextsProvider;
            altGenresDisplayNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresDisplayNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresDisplayNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresDisplayNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresDisplayName");
        if(typeDataAccess != null) {
            altGenresDisplayNameTypeAPI = new AltGenresDisplayNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            altGenresDisplayNameTypeAPI = new AltGenresDisplayNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AltGenresDisplayName"));
        }
        addTypeAPI(altGenresDisplayNameTypeAPI);
        factory = factoryOverrides.get("AltGenresDisplayName");
        if(factory == null)
            factory = new AltGenresDisplayNameHollowFactory();
        if(cachedTypes.contains("AltGenresDisplayName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresDisplayNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresDisplayNameProvider;
            altGenresDisplayNameProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresDisplayNameTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresDisplayNameProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresDisplayNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresShortNameTranslatedTexts");
        if(typeDataAccess != null) {
            altGenresShortNameTranslatedTextsTypeAPI = new AltGenresShortNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            altGenresShortNameTranslatedTextsTypeAPI = new AltGenresShortNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AltGenresShortNameTranslatedTexts"));
        }
        addTypeAPI(altGenresShortNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AltGenresShortNameTranslatedTexts");
        if(factory == null)
            factory = new AltGenresShortNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AltGenresShortNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresShortNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresShortNameTranslatedTextsProvider;
            altGenresShortNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresShortNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresShortNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresShortNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresShortNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            altGenresShortNameMapOfTranslatedTextsTypeAPI = new AltGenresShortNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            altGenresShortNameMapOfTranslatedTextsTypeAPI = new AltGenresShortNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "AltGenresShortNameMapOfTranslatedTexts"));
        }
        addTypeAPI(altGenresShortNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AltGenresShortNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new AltGenresShortNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AltGenresShortNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresShortNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresShortNameMapOfTranslatedTextsProvider;
            altGenresShortNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresShortNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresShortNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresShortNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresShortName");
        if(typeDataAccess != null) {
            altGenresShortNameTypeAPI = new AltGenresShortNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            altGenresShortNameTypeAPI = new AltGenresShortNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AltGenresShortName"));
        }
        addTypeAPI(altGenresShortNameTypeAPI);
        factory = factoryOverrides.get("AltGenresShortName");
        if(factory == null)
            factory = new AltGenresShortNameHollowFactory();
        if(cachedTypes.contains("AltGenresShortName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresShortNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresShortNameProvider;
            altGenresShortNameProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresShortNameTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresShortNameProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresShortNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenres");
        if(typeDataAccess != null) {
            altGenresTypeAPI = new AltGenresTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            altGenresTypeAPI = new AltGenresTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AltGenres"));
        }
        addTypeAPI(altGenresTypeAPI);
        factory = factoryOverrides.get("AltGenres");
        if(factory == null)
            factory = new AltGenresHollowFactory();
        if(cachedTypes.contains("AltGenres")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresProvider;
            altGenresProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ArtWorkImageFormat");
        if(typeDataAccess != null) {
            artWorkImageFormatTypeAPI = new ArtWorkImageFormatTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            artWorkImageFormatTypeAPI = new ArtWorkImageFormatTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ArtWorkImageFormat"));
        }
        addTypeAPI(artWorkImageFormatTypeAPI);
        factory = factoryOverrides.get("ArtWorkImageFormat");
        if(factory == null)
            factory = new ArtWorkImageFormatHollowFactory();
        if(cachedTypes.contains("ArtWorkImageFormat")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.artWorkImageFormatProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.artWorkImageFormatProvider;
            artWorkImageFormatProvider = new HollowObjectCacheProvider(typeDataAccess, artWorkImageFormatTypeAPI, factory, previousCacheProvider);
        } else {
            artWorkImageFormatProvider = new HollowObjectFactoryProvider(typeDataAccess, artWorkImageFormatTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ArtWorkImageType");
        if(typeDataAccess != null) {
            artWorkImageTypeTypeAPI = new ArtWorkImageTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            artWorkImageTypeTypeAPI = new ArtWorkImageTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ArtWorkImageType"));
        }
        addTypeAPI(artWorkImageTypeTypeAPI);
        factory = factoryOverrides.get("ArtWorkImageType");
        if(factory == null)
            factory = new ArtWorkImageTypeHollowFactory();
        if(cachedTypes.contains("ArtWorkImageType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.artWorkImageTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.artWorkImageTypeProvider;
            artWorkImageTypeProvider = new HollowObjectCacheProvider(typeDataAccess, artWorkImageTypeTypeAPI, factory, previousCacheProvider);
        } else {
            artWorkImageTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, artWorkImageTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ArtworkRecipe");
        if(typeDataAccess != null) {
            artworkRecipeTypeAPI = new ArtworkRecipeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            artworkRecipeTypeAPI = new ArtworkRecipeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ArtworkRecipe"));
        }
        addTypeAPI(artworkRecipeTypeAPI);
        factory = factoryOverrides.get("ArtworkRecipe");
        if(factory == null)
            factory = new ArtworkRecipeHollowFactory();
        if(cachedTypes.contains("ArtworkRecipe")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.artworkRecipeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.artworkRecipeProvider;
            artworkRecipeProvider = new HollowObjectCacheProvider(typeDataAccess, artworkRecipeTypeAPI, factory, previousCacheProvider);
        } else {
            artworkRecipeProvider = new HollowObjectFactoryProvider(typeDataAccess, artworkRecipeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AssetMetaDatasTrackLabelsTranslatedTexts");
        if(typeDataAccess != null) {
            assetMetaDatasTrackLabelsTranslatedTextsTypeAPI = new AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            assetMetaDatasTrackLabelsTranslatedTextsTypeAPI = new AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AssetMetaDatasTrackLabelsTranslatedTexts"));
        }
        addTypeAPI(assetMetaDatasTrackLabelsTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AssetMetaDatasTrackLabelsTranslatedTexts");
        if(factory == null)
            factory = new AssetMetaDatasTrackLabelsTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AssetMetaDatasTrackLabelsTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.assetMetaDatasTrackLabelsTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.assetMetaDatasTrackLabelsTranslatedTextsProvider;
            assetMetaDatasTrackLabelsTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, assetMetaDatasTrackLabelsTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            assetMetaDatasTrackLabelsTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, assetMetaDatasTrackLabelsTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AssetMetaDatasTrackLabelsMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            assetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI = new AssetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            assetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI = new AssetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "AssetMetaDatasTrackLabelsMapOfTranslatedTexts"));
        }
        addTypeAPI(assetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AssetMetaDatasTrackLabelsMapOfTranslatedTexts");
        if(factory == null)
            factory = new AssetMetaDatasTrackLabelsMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AssetMetaDatasTrackLabelsMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider;
            assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, assetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, assetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AssetMetaDatasTrackLabels");
        if(typeDataAccess != null) {
            assetMetaDatasTrackLabelsTypeAPI = new AssetMetaDatasTrackLabelsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            assetMetaDatasTrackLabelsTypeAPI = new AssetMetaDatasTrackLabelsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AssetMetaDatasTrackLabels"));
        }
        addTypeAPI(assetMetaDatasTrackLabelsTypeAPI);
        factory = factoryOverrides.get("AssetMetaDatasTrackLabels");
        if(factory == null)
            factory = new AssetMetaDatasTrackLabelsHollowFactory();
        if(cachedTypes.contains("AssetMetaDatasTrackLabels")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.assetMetaDatasTrackLabelsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.assetMetaDatasTrackLabelsProvider;
            assetMetaDatasTrackLabelsProvider = new HollowObjectCacheProvider(typeDataAccess, assetMetaDatasTrackLabelsTypeAPI, factory, previousCacheProvider);
        } else {
            assetMetaDatasTrackLabelsProvider = new HollowObjectFactoryProvider(typeDataAccess, assetMetaDatasTrackLabelsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AssetMetaDatas");
        if(typeDataAccess != null) {
            assetMetaDatasTypeAPI = new AssetMetaDatasTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            assetMetaDatasTypeAPI = new AssetMetaDatasTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AssetMetaDatas"));
        }
        addTypeAPI(assetMetaDatasTypeAPI);
        factory = factoryOverrides.get("AssetMetaDatas");
        if(factory == null)
            factory = new AssetMetaDatasHollowFactory();
        if(cachedTypes.contains("AssetMetaDatas")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.assetMetaDatasProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.assetMetaDatasProvider;
            assetMetaDatasProvider = new HollowObjectCacheProvider(typeDataAccess, assetMetaDatasTypeAPI, factory, previousCacheProvider);
        } else {
            assetMetaDatasProvider = new HollowObjectFactoryProvider(typeDataAccess, assetMetaDatasTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsAlternateNameTranslatedTexts");
        if(typeDataAccess != null) {
            awardsAlternateNameTranslatedTextsTypeAPI = new AwardsAlternateNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardsAlternateNameTranslatedTextsTypeAPI = new AwardsAlternateNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AwardsAlternateNameTranslatedTexts"));
        }
        addTypeAPI(awardsAlternateNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AwardsAlternateNameTranslatedTexts");
        if(factory == null)
            factory = new AwardsAlternateNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AwardsAlternateNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsAlternateNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsAlternateNameTranslatedTextsProvider;
            awardsAlternateNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, awardsAlternateNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            awardsAlternateNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsAlternateNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsAlternateNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            awardsAlternateNameMapOfTranslatedTextsTypeAPI = new AwardsAlternateNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            awardsAlternateNameMapOfTranslatedTextsTypeAPI = new AwardsAlternateNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "AwardsAlternateNameMapOfTranslatedTexts"));
        }
        addTypeAPI(awardsAlternateNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AwardsAlternateNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new AwardsAlternateNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AwardsAlternateNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsAlternateNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsAlternateNameMapOfTranslatedTextsProvider;
            awardsAlternateNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, awardsAlternateNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            awardsAlternateNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsAlternateNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsAlternateName");
        if(typeDataAccess != null) {
            awardsAlternateNameTypeAPI = new AwardsAlternateNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardsAlternateNameTypeAPI = new AwardsAlternateNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AwardsAlternateName"));
        }
        addTypeAPI(awardsAlternateNameTypeAPI);
        factory = factoryOverrides.get("AwardsAlternateName");
        if(factory == null)
            factory = new AwardsAlternateNameHollowFactory();
        if(cachedTypes.contains("AwardsAlternateName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsAlternateNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsAlternateNameProvider;
            awardsAlternateNameProvider = new HollowObjectCacheProvider(typeDataAccess, awardsAlternateNameTypeAPI, factory, previousCacheProvider);
        } else {
            awardsAlternateNameProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsAlternateNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsAwardNameTranslatedTexts");
        if(typeDataAccess != null) {
            awardsAwardNameTranslatedTextsTypeAPI = new AwardsAwardNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardsAwardNameTranslatedTextsTypeAPI = new AwardsAwardNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AwardsAwardNameTranslatedTexts"));
        }
        addTypeAPI(awardsAwardNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AwardsAwardNameTranslatedTexts");
        if(factory == null)
            factory = new AwardsAwardNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AwardsAwardNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsAwardNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsAwardNameTranslatedTextsProvider;
            awardsAwardNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, awardsAwardNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            awardsAwardNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsAwardNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsAwardNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            awardsAwardNameMapOfTranslatedTextsTypeAPI = new AwardsAwardNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            awardsAwardNameMapOfTranslatedTextsTypeAPI = new AwardsAwardNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "AwardsAwardNameMapOfTranslatedTexts"));
        }
        addTypeAPI(awardsAwardNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("AwardsAwardNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new AwardsAwardNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("AwardsAwardNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsAwardNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsAwardNameMapOfTranslatedTextsProvider;
            awardsAwardNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, awardsAwardNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            awardsAwardNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsAwardNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AwardsAwardName");
        if(typeDataAccess != null) {
            awardsAwardNameTypeAPI = new AwardsAwardNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardsAwardNameTypeAPI = new AwardsAwardNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AwardsAwardName"));
        }
        addTypeAPI(awardsAwardNameTypeAPI);
        factory = factoryOverrides.get("AwardsAwardName");
        if(factory == null)
            factory = new AwardsAwardNameHollowFactory();
        if(cachedTypes.contains("AwardsAwardName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsAwardNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsAwardNameProvider;
            awardsAwardNameProvider = new HollowObjectCacheProvider(typeDataAccess, awardsAwardNameTypeAPI, factory, previousCacheProvider);
        } else {
            awardsAwardNameProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsAwardNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Awards");
        if(typeDataAccess != null) {
            awardsTypeAPI = new AwardsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            awardsTypeAPI = new AwardsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Awards"));
        }
        addTypeAPI(awardsTypeAPI);
        factory = factoryOverrides.get("Awards");
        if(factory == null)
            factory = new AwardsHollowFactory();
        if(cachedTypes.contains("Awards")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.awardsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.awardsProvider;
            awardsProvider = new HollowObjectCacheProvider(typeDataAccess, awardsTypeAPI, factory, previousCacheProvider);
        } else {
            awardsProvider = new HollowObjectFactoryProvider(typeDataAccess, awardsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Bcp47Code");
        if(typeDataAccess != null) {
            bcp47CodeTypeAPI = new Bcp47CodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            bcp47CodeTypeAPI = new Bcp47CodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Bcp47Code"));
        }
        addTypeAPI(bcp47CodeTypeAPI);
        factory = factoryOverrides.get("Bcp47Code");
        if(factory == null)
            factory = new Bcp47CodeHollowFactory();
        if(cachedTypes.contains("Bcp47Code")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.bcp47CodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.bcp47CodeProvider;
            bcp47CodeProvider = new HollowObjectCacheProvider(typeDataAccess, bcp47CodeTypeAPI, factory, previousCacheProvider);
        } else {
            bcp47CodeProvider = new HollowObjectFactoryProvider(typeDataAccess, bcp47CodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CSMReview");
        if(typeDataAccess != null) {
            cSMReviewTypeAPI = new CSMReviewTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            cSMReviewTypeAPI = new CSMReviewTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CSMReview"));
        }
        addTypeAPI(cSMReviewTypeAPI);
        factory = factoryOverrides.get("CSMReview");
        if(factory == null)
            factory = new CSMReviewHollowFactory();
        if(cachedTypes.contains("CSMReview")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.cSMReviewProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.cSMReviewProvider;
            cSMReviewProvider = new HollowObjectCacheProvider(typeDataAccess, cSMReviewTypeAPI, factory, previousCacheProvider);
        } else {
            cSMReviewProvider = new HollowObjectFactoryProvider(typeDataAccess, cSMReviewTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CacheDeploymentIntent");
        if(typeDataAccess != null) {
            cacheDeploymentIntentTypeAPI = new CacheDeploymentIntentTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            cacheDeploymentIntentTypeAPI = new CacheDeploymentIntentTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CacheDeploymentIntent"));
        }
        addTypeAPI(cacheDeploymentIntentTypeAPI);
        factory = factoryOverrides.get("CacheDeploymentIntent");
        if(factory == null)
            factory = new CacheDeploymentIntentHollowFactory();
        if(cachedTypes.contains("CacheDeploymentIntent")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.cacheDeploymentIntentProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.cacheDeploymentIntentProvider;
            cacheDeploymentIntentProvider = new HollowObjectCacheProvider(typeDataAccess, cacheDeploymentIntentTypeAPI, factory, previousCacheProvider);
        } else {
            cacheDeploymentIntentProvider = new HollowObjectFactoryProvider(typeDataAccess, cacheDeploymentIntentTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoriesDisplayNameTranslatedTexts");
        if(typeDataAccess != null) {
            categoriesDisplayNameTranslatedTextsTypeAPI = new CategoriesDisplayNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoriesDisplayNameTranslatedTextsTypeAPI = new CategoriesDisplayNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CategoriesDisplayNameTranslatedTexts"));
        }
        addTypeAPI(categoriesDisplayNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CategoriesDisplayNameTranslatedTexts");
        if(factory == null)
            factory = new CategoriesDisplayNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CategoriesDisplayNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoriesDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoriesDisplayNameTranslatedTextsProvider;
            categoriesDisplayNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, categoriesDisplayNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            categoriesDisplayNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, categoriesDisplayNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoriesDisplayNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            categoriesDisplayNameMapOfTranslatedTextsTypeAPI = new CategoriesDisplayNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            categoriesDisplayNameMapOfTranslatedTextsTypeAPI = new CategoriesDisplayNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "CategoriesDisplayNameMapOfTranslatedTexts"));
        }
        addTypeAPI(categoriesDisplayNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CategoriesDisplayNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new CategoriesDisplayNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CategoriesDisplayNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoriesDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoriesDisplayNameMapOfTranslatedTextsProvider;
            categoriesDisplayNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, categoriesDisplayNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            categoriesDisplayNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, categoriesDisplayNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoriesDisplayName");
        if(typeDataAccess != null) {
            categoriesDisplayNameTypeAPI = new CategoriesDisplayNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoriesDisplayNameTypeAPI = new CategoriesDisplayNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CategoriesDisplayName"));
        }
        addTypeAPI(categoriesDisplayNameTypeAPI);
        factory = factoryOverrides.get("CategoriesDisplayName");
        if(factory == null)
            factory = new CategoriesDisplayNameHollowFactory();
        if(cachedTypes.contains("CategoriesDisplayName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoriesDisplayNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoriesDisplayNameProvider;
            categoriesDisplayNameProvider = new HollowObjectCacheProvider(typeDataAccess, categoriesDisplayNameTypeAPI, factory, previousCacheProvider);
        } else {
            categoriesDisplayNameProvider = new HollowObjectFactoryProvider(typeDataAccess, categoriesDisplayNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoriesShortNameTranslatedTexts");
        if(typeDataAccess != null) {
            categoriesShortNameTranslatedTextsTypeAPI = new CategoriesShortNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoriesShortNameTranslatedTextsTypeAPI = new CategoriesShortNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CategoriesShortNameTranslatedTexts"));
        }
        addTypeAPI(categoriesShortNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CategoriesShortNameTranslatedTexts");
        if(factory == null)
            factory = new CategoriesShortNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CategoriesShortNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoriesShortNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoriesShortNameTranslatedTextsProvider;
            categoriesShortNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, categoriesShortNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            categoriesShortNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, categoriesShortNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoriesShortNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            categoriesShortNameMapOfTranslatedTextsTypeAPI = new CategoriesShortNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            categoriesShortNameMapOfTranslatedTextsTypeAPI = new CategoriesShortNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "CategoriesShortNameMapOfTranslatedTexts"));
        }
        addTypeAPI(categoriesShortNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CategoriesShortNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new CategoriesShortNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CategoriesShortNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoriesShortNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoriesShortNameMapOfTranslatedTextsProvider;
            categoriesShortNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, categoriesShortNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            categoriesShortNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, categoriesShortNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoriesShortName");
        if(typeDataAccess != null) {
            categoriesShortNameTypeAPI = new CategoriesShortNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoriesShortNameTypeAPI = new CategoriesShortNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CategoriesShortName"));
        }
        addTypeAPI(categoriesShortNameTypeAPI);
        factory = factoryOverrides.get("CategoriesShortName");
        if(factory == null)
            factory = new CategoriesShortNameHollowFactory();
        if(cachedTypes.contains("CategoriesShortName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoriesShortNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoriesShortNameProvider;
            categoriesShortNameProvider = new HollowObjectCacheProvider(typeDataAccess, categoriesShortNameTypeAPI, factory, previousCacheProvider);
        } else {
            categoriesShortNameProvider = new HollowObjectFactoryProvider(typeDataAccess, categoriesShortNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Categories");
        if(typeDataAccess != null) {
            categoriesTypeAPI = new CategoriesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoriesTypeAPI = new CategoriesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Categories"));
        }
        addTypeAPI(categoriesTypeAPI);
        factory = factoryOverrides.get("Categories");
        if(factory == null)
            factory = new CategoriesHollowFactory();
        if(cachedTypes.contains("Categories")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoriesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoriesProvider;
            categoriesProvider = new HollowObjectCacheProvider(typeDataAccess, categoriesTypeAPI, factory, previousCacheProvider);
        } else {
            categoriesProvider = new HollowObjectFactoryProvider(typeDataAccess, categoriesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoryGroupsCategoryGroupNameTranslatedTexts");
        if(typeDataAccess != null) {
            categoryGroupsCategoryGroupNameTranslatedTextsTypeAPI = new CategoryGroupsCategoryGroupNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoryGroupsCategoryGroupNameTranslatedTextsTypeAPI = new CategoryGroupsCategoryGroupNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CategoryGroupsCategoryGroupNameTranslatedTexts"));
        }
        addTypeAPI(categoryGroupsCategoryGroupNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CategoryGroupsCategoryGroupNameTranslatedTexts");
        if(factory == null)
            factory = new CategoryGroupsCategoryGroupNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CategoryGroupsCategoryGroupNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoryGroupsCategoryGroupNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoryGroupsCategoryGroupNameTranslatedTextsProvider;
            categoryGroupsCategoryGroupNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, categoryGroupsCategoryGroupNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            categoryGroupsCategoryGroupNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, categoryGroupsCategoryGroupNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoryGroupsCategoryGroupNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            categoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI = new CategoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            categoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI = new CategoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "CategoryGroupsCategoryGroupNameMapOfTranslatedTexts"));
        }
        addTypeAPI(categoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CategoryGroupsCategoryGroupNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new CategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CategoryGroupsCategoryGroupNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider;
            categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, categoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, categoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoryGroupsCategoryGroupName");
        if(typeDataAccess != null) {
            categoryGroupsCategoryGroupNameTypeAPI = new CategoryGroupsCategoryGroupNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoryGroupsCategoryGroupNameTypeAPI = new CategoryGroupsCategoryGroupNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CategoryGroupsCategoryGroupName"));
        }
        addTypeAPI(categoryGroupsCategoryGroupNameTypeAPI);
        factory = factoryOverrides.get("CategoryGroupsCategoryGroupName");
        if(factory == null)
            factory = new CategoryGroupsCategoryGroupNameHollowFactory();
        if(cachedTypes.contains("CategoryGroupsCategoryGroupName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoryGroupsCategoryGroupNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoryGroupsCategoryGroupNameProvider;
            categoryGroupsCategoryGroupNameProvider = new HollowObjectCacheProvider(typeDataAccess, categoryGroupsCategoryGroupNameTypeAPI, factory, previousCacheProvider);
        } else {
            categoryGroupsCategoryGroupNameProvider = new HollowObjectFactoryProvider(typeDataAccess, categoryGroupsCategoryGroupNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CategoryGroups");
        if(typeDataAccess != null) {
            categoryGroupsTypeAPI = new CategoryGroupsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            categoryGroupsTypeAPI = new CategoryGroupsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CategoryGroups"));
        }
        addTypeAPI(categoryGroupsTypeAPI);
        factory = factoryOverrides.get("CategoryGroups");
        if(factory == null)
            factory = new CategoryGroupsHollowFactory();
        if(cachedTypes.contains("CategoryGroups")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.categoryGroupsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.categoryGroupsProvider;
            categoryGroupsProvider = new HollowObjectCacheProvider(typeDataAccess, categoryGroupsTypeAPI, factory, previousCacheProvider);
        } else {
            categoryGroupsProvider = new HollowObjectFactoryProvider(typeDataAccess, categoryGroupsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Cdns");
        if(typeDataAccess != null) {
            cdnsTypeAPI = new CdnsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            cdnsTypeAPI = new CdnsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Cdns"));
        }
        addTypeAPI(cdnsTypeAPI);
        factory = factoryOverrides.get("Cdns");
        if(factory == null)
            factory = new CdnsHollowFactory();
        if(cachedTypes.contains("Cdns")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.cdnsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.cdnsProvider;
            cdnsProvider = new HollowObjectCacheProvider(typeDataAccess, cdnsTypeAPI, factory, previousCacheProvider);
        } else {
            cdnsProvider = new HollowObjectFactoryProvider(typeDataAccess, cdnsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationSystemRating");
        if(typeDataAccess != null) {
            certificationSystemRatingTypeAPI = new CertificationSystemRatingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            certificationSystemRatingTypeAPI = new CertificationSystemRatingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CertificationSystemRating"));
        }
        addTypeAPI(certificationSystemRatingTypeAPI);
        factory = factoryOverrides.get("CertificationSystemRating");
        if(factory == null)
            factory = new CertificationSystemRatingHollowFactory();
        if(cachedTypes.contains("CertificationSystemRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationSystemRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationSystemRatingProvider;
            certificationSystemRatingProvider = new HollowObjectCacheProvider(typeDataAccess, certificationSystemRatingTypeAPI, factory, previousCacheProvider);
        } else {
            certificationSystemRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationSystemRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationSystemArrayOfRating");
        if(typeDataAccess != null) {
            certificationSystemArrayOfRatingTypeAPI = new CertificationSystemArrayOfRatingTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            certificationSystemArrayOfRatingTypeAPI = new CertificationSystemArrayOfRatingTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CertificationSystemArrayOfRating"));
        }
        addTypeAPI(certificationSystemArrayOfRatingTypeAPI);
        factory = factoryOverrides.get("CertificationSystemArrayOfRating");
        if(factory == null)
            factory = new CertificationSystemArrayOfRatingHollowFactory();
        if(cachedTypes.contains("CertificationSystemArrayOfRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationSystemArrayOfRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationSystemArrayOfRatingProvider;
            certificationSystemArrayOfRatingProvider = new HollowObjectCacheProvider(typeDataAccess, certificationSystemArrayOfRatingTypeAPI, factory, previousCacheProvider);
        } else {
            certificationSystemArrayOfRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationSystemArrayOfRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationSystem");
        if(typeDataAccess != null) {
            certificationSystemTypeAPI = new CertificationSystemTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            certificationSystemTypeAPI = new CertificationSystemTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CertificationSystem"));
        }
        addTypeAPI(certificationSystemTypeAPI);
        factory = factoryOverrides.get("CertificationSystem");
        if(factory == null)
            factory = new CertificationSystemHollowFactory();
        if(cachedTypes.contains("CertificationSystem")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationSystemProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationSystemProvider;
            certificationSystemProvider = new HollowObjectCacheProvider(typeDataAccess, certificationSystemTypeAPI, factory, previousCacheProvider);
        } else {
            certificationSystemProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationSystemTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationsDescriptionTranslatedTexts");
        if(typeDataAccess != null) {
            certificationsDescriptionTranslatedTextsTypeAPI = new CertificationsDescriptionTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            certificationsDescriptionTranslatedTextsTypeAPI = new CertificationsDescriptionTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CertificationsDescriptionTranslatedTexts"));
        }
        addTypeAPI(certificationsDescriptionTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CertificationsDescriptionTranslatedTexts");
        if(factory == null)
            factory = new CertificationsDescriptionTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CertificationsDescriptionTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationsDescriptionTranslatedTextsProvider;
            certificationsDescriptionTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, certificationsDescriptionTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            certificationsDescriptionTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationsDescriptionTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationsDescriptionMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            certificationsDescriptionMapOfTranslatedTextsTypeAPI = new CertificationsDescriptionMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            certificationsDescriptionMapOfTranslatedTextsTypeAPI = new CertificationsDescriptionMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "CertificationsDescriptionMapOfTranslatedTexts"));
        }
        addTypeAPI(certificationsDescriptionMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CertificationsDescriptionMapOfTranslatedTexts");
        if(factory == null)
            factory = new CertificationsDescriptionMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CertificationsDescriptionMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationsDescriptionMapOfTranslatedTextsProvider;
            certificationsDescriptionMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, certificationsDescriptionMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            certificationsDescriptionMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationsDescriptionMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationsDescription");
        if(typeDataAccess != null) {
            certificationsDescriptionTypeAPI = new CertificationsDescriptionTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            certificationsDescriptionTypeAPI = new CertificationsDescriptionTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CertificationsDescription"));
        }
        addTypeAPI(certificationsDescriptionTypeAPI);
        factory = factoryOverrides.get("CertificationsDescription");
        if(factory == null)
            factory = new CertificationsDescriptionHollowFactory();
        if(cachedTypes.contains("CertificationsDescription")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationsDescriptionProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationsDescriptionProvider;
            certificationsDescriptionProvider = new HollowObjectCacheProvider(typeDataAccess, certificationsDescriptionTypeAPI, factory, previousCacheProvider);
        } else {
            certificationsDescriptionProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationsDescriptionTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationsNameTranslatedTexts");
        if(typeDataAccess != null) {
            certificationsNameTranslatedTextsTypeAPI = new CertificationsNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            certificationsNameTranslatedTextsTypeAPI = new CertificationsNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CertificationsNameTranslatedTexts"));
        }
        addTypeAPI(certificationsNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CertificationsNameTranslatedTexts");
        if(factory == null)
            factory = new CertificationsNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CertificationsNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationsNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationsNameTranslatedTextsProvider;
            certificationsNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, certificationsNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            certificationsNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationsNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationsNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            certificationsNameMapOfTranslatedTextsTypeAPI = new CertificationsNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            certificationsNameMapOfTranslatedTextsTypeAPI = new CertificationsNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "CertificationsNameMapOfTranslatedTexts"));
        }
        addTypeAPI(certificationsNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CertificationsNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new CertificationsNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CertificationsNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationsNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationsNameMapOfTranslatedTextsProvider;
            certificationsNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, certificationsNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            certificationsNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationsNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationsName");
        if(typeDataAccess != null) {
            certificationsNameTypeAPI = new CertificationsNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            certificationsNameTypeAPI = new CertificationsNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CertificationsName"));
        }
        addTypeAPI(certificationsNameTypeAPI);
        factory = factoryOverrides.get("CertificationsName");
        if(factory == null)
            factory = new CertificationsNameHollowFactory();
        if(cachedTypes.contains("CertificationsName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationsNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationsNameProvider;
            certificationsNameProvider = new HollowObjectCacheProvider(typeDataAccess, certificationsNameTypeAPI, factory, previousCacheProvider);
        } else {
            certificationsNameProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationsNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Certifications");
        if(typeDataAccess != null) {
            certificationsTypeAPI = new CertificationsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            certificationsTypeAPI = new CertificationsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Certifications"));
        }
        addTypeAPI(certificationsTypeAPI);
        factory = factoryOverrides.get("Certifications");
        if(factory == null)
            factory = new CertificationsHollowFactory();
        if(cachedTypes.contains("Certifications")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationsProvider;
            certificationsProvider = new HollowObjectCacheProvider(typeDataAccess, certificationsTypeAPI, factory, previousCacheProvider);
        } else {
            certificationsProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkAttributes");
        if(typeDataAccess != null) {
            characterArtworkAttributesTypeAPI = new CharacterArtworkAttributesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkAttributesTypeAPI = new CharacterArtworkAttributesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtworkAttributes"));
        }
        addTypeAPI(characterArtworkAttributesTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkAttributes");
        if(factory == null)
            factory = new CharacterArtworkAttributesHollowFactory();
        if(cachedTypes.contains("CharacterArtworkAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkAttributesProvider;
            characterArtworkAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkDerivatives");
        if(typeDataAccess != null) {
            characterArtworkDerivativesTypeAPI = new CharacterArtworkDerivativesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkDerivativesTypeAPI = new CharacterArtworkDerivativesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtworkDerivatives"));
        }
        addTypeAPI(characterArtworkDerivativesTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkDerivatives");
        if(factory == null)
            factory = new CharacterArtworkDerivativesHollowFactory();
        if(cachedTypes.contains("CharacterArtworkDerivatives")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkDerivativesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkDerivativesProvider;
            characterArtworkDerivativesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkDerivativesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkDerivativesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkDerivativesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkArrayOfDerivatives");
        if(typeDataAccess != null) {
            characterArtworkArrayOfDerivativesTypeAPI = new CharacterArtworkArrayOfDerivativesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkArrayOfDerivativesTypeAPI = new CharacterArtworkArrayOfDerivativesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterArtworkArrayOfDerivatives"));
        }
        addTypeAPI(characterArtworkArrayOfDerivativesTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkArrayOfDerivatives");
        if(factory == null)
            factory = new CharacterArtworkArrayOfDerivativesHollowFactory();
        if(cachedTypes.contains("CharacterArtworkArrayOfDerivatives")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkArrayOfDerivativesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkArrayOfDerivativesProvider;
            characterArtworkArrayOfDerivativesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkArrayOfDerivativesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkArrayOfDerivativesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkArrayOfDerivativesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkLocalesTerritoryCodes");
        if(typeDataAccess != null) {
            characterArtworkLocalesTerritoryCodesTypeAPI = new CharacterArtworkLocalesTerritoryCodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkLocalesTerritoryCodesTypeAPI = new CharacterArtworkLocalesTerritoryCodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtworkLocalesTerritoryCodes"));
        }
        addTypeAPI(characterArtworkLocalesTerritoryCodesTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkLocalesTerritoryCodes");
        if(factory == null)
            factory = new CharacterArtworkLocalesTerritoryCodesHollowFactory();
        if(cachedTypes.contains("CharacterArtworkLocalesTerritoryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkLocalesTerritoryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkLocalesTerritoryCodesProvider;
            characterArtworkLocalesTerritoryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkLocalesTerritoryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkLocalesTerritoryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkLocalesTerritoryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkLocalesArrayOfTerritoryCodes");
        if(typeDataAccess != null) {
            characterArtworkLocalesArrayOfTerritoryCodesTypeAPI = new CharacterArtworkLocalesArrayOfTerritoryCodesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkLocalesArrayOfTerritoryCodesTypeAPI = new CharacterArtworkLocalesArrayOfTerritoryCodesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterArtworkLocalesArrayOfTerritoryCodes"));
        }
        addTypeAPI(characterArtworkLocalesArrayOfTerritoryCodesTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkLocalesArrayOfTerritoryCodes");
        if(factory == null)
            factory = new CharacterArtworkLocalesArrayOfTerritoryCodesHollowFactory();
        if(cachedTypes.contains("CharacterArtworkLocalesArrayOfTerritoryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkLocalesArrayOfTerritoryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkLocalesArrayOfTerritoryCodesProvider;
            characterArtworkLocalesArrayOfTerritoryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkLocalesArrayOfTerritoryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkLocalesArrayOfTerritoryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkLocalesArrayOfTerritoryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkLocales");
        if(typeDataAccess != null) {
            characterArtworkLocalesTypeAPI = new CharacterArtworkLocalesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkLocalesTypeAPI = new CharacterArtworkLocalesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtworkLocales"));
        }
        addTypeAPI(characterArtworkLocalesTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkLocales");
        if(factory == null)
            factory = new CharacterArtworkLocalesHollowFactory();
        if(cachedTypes.contains("CharacterArtworkLocales")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkLocalesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkLocalesProvider;
            characterArtworkLocalesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkLocalesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkLocalesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkLocalesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkArrayOfLocales");
        if(typeDataAccess != null) {
            characterArtworkArrayOfLocalesTypeAPI = new CharacterArtworkArrayOfLocalesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkArrayOfLocalesTypeAPI = new CharacterArtworkArrayOfLocalesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterArtworkArrayOfLocales"));
        }
        addTypeAPI(characterArtworkArrayOfLocalesTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkArrayOfLocales");
        if(factory == null)
            factory = new CharacterArtworkArrayOfLocalesHollowFactory();
        if(cachedTypes.contains("CharacterArtworkArrayOfLocales")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkArrayOfLocalesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkArrayOfLocalesProvider;
            characterArtworkArrayOfLocalesProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkArrayOfLocalesTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkArrayOfLocalesProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkArrayOfLocalesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtwork");
        if(typeDataAccess != null) {
            characterArtworkTypeAPI = new CharacterArtworkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkTypeAPI = new CharacterArtworkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtwork"));
        }
        addTypeAPI(characterArtworkTypeAPI);
        factory = factoryOverrides.get("CharacterArtwork");
        if(factory == null)
            factory = new CharacterArtworkHollowFactory();
        if(cachedTypes.contains("CharacterArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkProvider;
            characterArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterElements");
        if(typeDataAccess != null) {
            characterElementsTypeAPI = new CharacterElementsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterElementsTypeAPI = new CharacterElementsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterElements"));
        }
        addTypeAPI(characterElementsTypeAPI);
        factory = factoryOverrides.get("CharacterElements");
        if(factory == null)
            factory = new CharacterElementsHollowFactory();
        if(cachedTypes.contains("CharacterElements")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterElementsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterElementsProvider;
            characterElementsProvider = new HollowObjectCacheProvider(typeDataAccess, characterElementsTypeAPI, factory, previousCacheProvider);
        } else {
            characterElementsProvider = new HollowObjectFactoryProvider(typeDataAccess, characterElementsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Character");
        if(typeDataAccess != null) {
            characterTypeAPI = new CharacterTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterTypeAPI = new CharacterTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Character"));
        }
        addTypeAPI(characterTypeAPI);
        factory = factoryOverrides.get("Character");
        if(factory == null)
            factory = new CharacterHollowFactory();
        if(cachedTypes.contains("Character")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterProvider;
            characterProvider = new HollowObjectCacheProvider(typeDataAccess, characterTypeAPI, factory, previousCacheProvider);
        } else {
            characterProvider = new HollowObjectFactoryProvider(typeDataAccess, characterTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharactersBTranslatedTexts");
        if(typeDataAccess != null) {
            charactersBTranslatedTextsTypeAPI = new CharactersBTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            charactersBTranslatedTextsTypeAPI = new CharactersBTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharactersBTranslatedTexts"));
        }
        addTypeAPI(charactersBTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CharactersBTranslatedTexts");
        if(factory == null)
            factory = new CharactersBTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CharactersBTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.charactersBTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.charactersBTranslatedTextsProvider;
            charactersBTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, charactersBTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            charactersBTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, charactersBTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharactersBMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            charactersBMapOfTranslatedTextsTypeAPI = new CharactersBMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            charactersBMapOfTranslatedTextsTypeAPI = new CharactersBMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "CharactersBMapOfTranslatedTexts"));
        }
        addTypeAPI(charactersBMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CharactersBMapOfTranslatedTexts");
        if(factory == null)
            factory = new CharactersBMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CharactersBMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.charactersBMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.charactersBMapOfTranslatedTextsProvider;
            charactersBMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, charactersBMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            charactersBMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, charactersBMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharactersB");
        if(typeDataAccess != null) {
            charactersBTypeAPI = new CharactersBTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            charactersBTypeAPI = new CharactersBTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharactersB"));
        }
        addTypeAPI(charactersBTypeAPI);
        factory = factoryOverrides.get("CharactersB");
        if(factory == null)
            factory = new CharactersBHollowFactory();
        if(cachedTypes.contains("CharactersB")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.charactersBProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.charactersBProvider;
            charactersBProvider = new HollowObjectCacheProvider(typeDataAccess, charactersBTypeAPI, factory, previousCacheProvider);
        } else {
            charactersBProvider = new HollowObjectFactoryProvider(typeDataAccess, charactersBTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharactersCnTranslatedTexts");
        if(typeDataAccess != null) {
            charactersCnTranslatedTextsTypeAPI = new CharactersCnTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            charactersCnTranslatedTextsTypeAPI = new CharactersCnTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharactersCnTranslatedTexts"));
        }
        addTypeAPI(charactersCnTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CharactersCnTranslatedTexts");
        if(factory == null)
            factory = new CharactersCnTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CharactersCnTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.charactersCnTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.charactersCnTranslatedTextsProvider;
            charactersCnTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, charactersCnTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            charactersCnTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, charactersCnTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharactersCnMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            charactersCnMapOfTranslatedTextsTypeAPI = new CharactersCnMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            charactersCnMapOfTranslatedTextsTypeAPI = new CharactersCnMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "CharactersCnMapOfTranslatedTexts"));
        }
        addTypeAPI(charactersCnMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("CharactersCnMapOfTranslatedTexts");
        if(factory == null)
            factory = new CharactersCnMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("CharactersCnMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.charactersCnMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.charactersCnMapOfTranslatedTextsProvider;
            charactersCnMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, charactersCnMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            charactersCnMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, charactersCnMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharactersCn");
        if(typeDataAccess != null) {
            charactersCnTypeAPI = new CharactersCnTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            charactersCnTypeAPI = new CharactersCnTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharactersCn"));
        }
        addTypeAPI(charactersCnTypeAPI);
        factory = factoryOverrides.get("CharactersCn");
        if(factory == null)
            factory = new CharactersCnHollowFactory();
        if(cachedTypes.contains("CharactersCn")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.charactersCnProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.charactersCnProvider;
            charactersCnProvider = new HollowObjectCacheProvider(typeDataAccess, charactersCnTypeAPI, factory, previousCacheProvider);
        } else {
            charactersCnProvider = new HollowObjectFactoryProvider(typeDataAccess, charactersCnTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Characters");
        if(typeDataAccess != null) {
            charactersTypeAPI = new CharactersTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            charactersTypeAPI = new CharactersTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Characters"));
        }
        addTypeAPI(charactersTypeAPI);
        factory = factoryOverrides.get("Characters");
        if(factory == null)
            factory = new CharactersHollowFactory();
        if(cachedTypes.contains("Characters")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.charactersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.charactersProvider;
            charactersProvider = new HollowObjectCacheProvider(typeDataAccess, charactersTypeAPI, factory, previousCacheProvider);
        } else {
            charactersProvider = new HollowObjectFactoryProvider(typeDataAccess, charactersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsDescriptionTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsDescriptionTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsDescriptionTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsDescriptionTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsDescriptionTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsDescriptionTranslatedTextsProvider;
            consolidatedCertificationSystemsDescriptionTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsDescriptionTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider;
            consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsDescription");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsDescriptionTypeAPI = new ConsolidatedCertificationSystemsDescriptionTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsDescriptionTypeAPI = new ConsolidatedCertificationSystemsDescriptionTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsDescription"));
        }
        addTypeAPI(consolidatedCertificationSystemsDescriptionTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsDescription");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsDescriptionHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsDescription")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsDescriptionProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsDescriptionProvider;
            consolidatedCertificationSystemsDescriptionProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsDescriptionTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsDescriptionProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsDescriptionTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsNameTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsNameTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsNameTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsNameTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsNameTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsNameTranslatedTextsProvider;
            consolidatedCertificationSystemsNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsNameMapOfTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider;
            consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsName");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsNameTypeAPI = new ConsolidatedCertificationSystemsNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsNameTypeAPI = new ConsolidatedCertificationSystemsNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsName"));
        }
        addTypeAPI(consolidatedCertificationSystemsNameTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsName");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsNameHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsNameProvider;
            consolidatedCertificationSystemsNameProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsNameTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsNameProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider;
            consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider;
            consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsRatingDescriptions");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsRatingDescriptionsTypeAPI = new ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsRatingDescriptionsTypeAPI = new ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsRatingDescriptions"));
        }
        addTypeAPI(consolidatedCertificationSystemsRatingDescriptionsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsRatingDescriptions");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsRatingDescriptionsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsRatingDescriptions")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsRatingDescriptionsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsRatingDescriptionsProvider;
            consolidatedCertificationSystemsRatingDescriptionsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsRatingDescriptionsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsRatingDescriptionsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsRatingDescriptionsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider;
            consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI = new ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTexts"));
        }
        addTypeAPI(consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider;
            consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsRatingRatingCodes");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsRatingRatingCodesTypeAPI = new ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsRatingRatingCodesTypeAPI = new ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsRatingRatingCodes"));
        }
        addTypeAPI(consolidatedCertificationSystemsRatingRatingCodesTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsRatingRatingCodes");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsRatingRatingCodesHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsRatingRatingCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsRatingRatingCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsRatingRatingCodesProvider;
            consolidatedCertificationSystemsRatingRatingCodesProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsRatingRatingCodesTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsRatingRatingCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsRatingRatingCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsRating");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsRatingTypeAPI = new ConsolidatedCertificationSystemsRatingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsRatingTypeAPI = new ConsolidatedCertificationSystemsRatingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsRating"));
        }
        addTypeAPI(consolidatedCertificationSystemsRatingTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsRating");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsRatingHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsRatingProvider;
            consolidatedCertificationSystemsRatingProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsRatingTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystemsArrayOfRating");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsArrayOfRatingTypeAPI = new ConsolidatedCertificationSystemsArrayOfRatingTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsArrayOfRatingTypeAPI = new ConsolidatedCertificationSystemsArrayOfRatingTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedCertificationSystemsArrayOfRating"));
        }
        addTypeAPI(consolidatedCertificationSystemsArrayOfRatingTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystemsArrayOfRating");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsArrayOfRatingHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystemsArrayOfRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsArrayOfRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsArrayOfRatingProvider;
            consolidatedCertificationSystemsArrayOfRatingProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsArrayOfRatingTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsArrayOfRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsArrayOfRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertificationSystems");
        if(typeDataAccess != null) {
            consolidatedCertificationSystemsTypeAPI = new ConsolidatedCertificationSystemsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertificationSystemsTypeAPI = new ConsolidatedCertificationSystemsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertificationSystems"));
        }
        addTypeAPI(consolidatedCertificationSystemsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertificationSystems");
        if(factory == null)
            factory = new ConsolidatedCertificationSystemsHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertificationSystems")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertificationSystemsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertificationSystemsProvider;
            consolidatedCertificationSystemsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertificationSystemsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertificationSystemsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertificationSystemsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryList");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryListTypeAPI = new ConsolidatedVideoRatingsRatingsCountryListTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryListTypeAPI = new ConsolidatedVideoRatingsRatingsCountryListTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryList"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryListTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryList");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryListHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryListProvider;
            consolidatedVideoRatingsRatingsCountryListProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryListTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryListProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsArrayOfCountryList");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI = new ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI = new ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsArrayOfCountryList"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsArrayOfCountryList");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsArrayOfCountryListHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsArrayOfCountryList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsArrayOfCountryListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsArrayOfCountryListProvider;
            consolidatedVideoRatingsRatingsArrayOfCountryListProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsArrayOfCountryListProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTexts"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider;
            consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTexts"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTexts");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider;
            consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsReasons");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryRatingsReasons"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryRatingsReasons");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryRatingsReasons")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider;
            consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatings");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsCountryRatingsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsTypeAPI = new ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsCountryRatings"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsCountryRatingsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsCountryRatings");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsCountryRatingsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsCountryRatings")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsCountryRatingsProvider;
            consolidatedVideoRatingsRatingsCountryRatingsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsCountryRatingsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsCountryRatingsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatingsArrayOfCountryRatings");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI = new ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI = new ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatingsArrayOfCountryRatings"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatingsArrayOfCountryRatings");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatingsArrayOfCountryRatings")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider;
            consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsRatings");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsRatingsTypeAPI = new ConsolidatedVideoRatingsRatingsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsRatingsTypeAPI = new ConsolidatedVideoRatingsRatingsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsRatings"));
        }
        addTypeAPI(consolidatedVideoRatingsRatingsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsRatings");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsRatingsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsRatings")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsRatingsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsRatingsProvider;
            consolidatedVideoRatingsRatingsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsRatingsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsRatingsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsRatingsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingsArrayOfRatings");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsArrayOfRatingsTypeAPI = new ConsolidatedVideoRatingsArrayOfRatingsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsArrayOfRatingsTypeAPI = new ConsolidatedVideoRatingsArrayOfRatingsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedVideoRatingsArrayOfRatings"));
        }
        addTypeAPI(consolidatedVideoRatingsArrayOfRatingsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingsArrayOfRatings");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsArrayOfRatingsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingsArrayOfRatings")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsArrayOfRatingsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsArrayOfRatingsProvider;
            consolidatedVideoRatingsArrayOfRatingsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsArrayOfRatingsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsArrayOfRatingsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsArrayOfRatingsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatings");
        if(typeDataAccess != null) {
            consolidatedVideoRatingsTypeAPI = new ConsolidatedVideoRatingsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingsTypeAPI = new ConsolidatedVideoRatingsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRatings"));
        }
        addTypeAPI(consolidatedVideoRatingsTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatings");
        if(factory == null)
            factory = new ConsolidatedVideoRatingsHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatings")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingsProvider;
            consolidatedVideoRatingsProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingsTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingsProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DefaultExtensionRecipe");
        if(typeDataAccess != null) {
            defaultExtensionRecipeTypeAPI = new DefaultExtensionRecipeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            defaultExtensionRecipeTypeAPI = new DefaultExtensionRecipeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DefaultExtensionRecipe"));
        }
        addTypeAPI(defaultExtensionRecipeTypeAPI);
        factory = factoryOverrides.get("DefaultExtensionRecipe");
        if(factory == null)
            factory = new DefaultExtensionRecipeHollowFactory();
        if(cachedTypes.contains("DefaultExtensionRecipe")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.defaultExtensionRecipeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.defaultExtensionRecipeProvider;
            defaultExtensionRecipeProvider = new HollowObjectCacheProvider(typeDataAccess, defaultExtensionRecipeTypeAPI, factory, previousCacheProvider);
        } else {
            defaultExtensionRecipeProvider = new HollowObjectFactoryProvider(typeDataAccess, defaultExtensionRecipeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DeployablePackagesCountryCodes");
        if(typeDataAccess != null) {
            deployablePackagesCountryCodesTypeAPI = new DeployablePackagesCountryCodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            deployablePackagesCountryCodesTypeAPI = new DeployablePackagesCountryCodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DeployablePackagesCountryCodes"));
        }
        addTypeAPI(deployablePackagesCountryCodesTypeAPI);
        factory = factoryOverrides.get("DeployablePackagesCountryCodes");
        if(factory == null)
            factory = new DeployablePackagesCountryCodesHollowFactory();
        if(cachedTypes.contains("DeployablePackagesCountryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.deployablePackagesCountryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.deployablePackagesCountryCodesProvider;
            deployablePackagesCountryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, deployablePackagesCountryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            deployablePackagesCountryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, deployablePackagesCountryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DeployablePackagesArrayOfCountryCodes");
        if(typeDataAccess != null) {
            deployablePackagesArrayOfCountryCodesTypeAPI = new DeployablePackagesArrayOfCountryCodesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            deployablePackagesArrayOfCountryCodesTypeAPI = new DeployablePackagesArrayOfCountryCodesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "DeployablePackagesArrayOfCountryCodes"));
        }
        addTypeAPI(deployablePackagesArrayOfCountryCodesTypeAPI);
        factory = factoryOverrides.get("DeployablePackagesArrayOfCountryCodes");
        if(factory == null)
            factory = new DeployablePackagesArrayOfCountryCodesHollowFactory();
        if(cachedTypes.contains("DeployablePackagesArrayOfCountryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.deployablePackagesArrayOfCountryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.deployablePackagesArrayOfCountryCodesProvider;
            deployablePackagesArrayOfCountryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, deployablePackagesArrayOfCountryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            deployablePackagesArrayOfCountryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, deployablePackagesArrayOfCountryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DeployablePackages");
        if(typeDataAccess != null) {
            deployablePackagesTypeAPI = new DeployablePackagesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            deployablePackagesTypeAPI = new DeployablePackagesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DeployablePackages"));
        }
        addTypeAPI(deployablePackagesTypeAPI);
        factory = factoryOverrides.get("DeployablePackages");
        if(factory == null)
            factory = new DeployablePackagesHollowFactory();
        if(cachedTypes.contains("DeployablePackages")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.deployablePackagesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.deployablePackagesProvider;
            deployablePackagesProvider = new HollowObjectCacheProvider(typeDataAccess, deployablePackagesTypeAPI, factory, previousCacheProvider);
        } else {
            deployablePackagesProvider = new HollowObjectFactoryProvider(typeDataAccess, deployablePackagesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DrmSystemIdentifiers");
        if(typeDataAccess != null) {
            drmSystemIdentifiersTypeAPI = new DrmSystemIdentifiersTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            drmSystemIdentifiersTypeAPI = new DrmSystemIdentifiersTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DrmSystemIdentifiers"));
        }
        addTypeAPI(drmSystemIdentifiersTypeAPI);
        factory = factoryOverrides.get("DrmSystemIdentifiers");
        if(factory == null)
            factory = new DrmSystemIdentifiersHollowFactory();
        if(cachedTypes.contains("DrmSystemIdentifiers")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.drmSystemIdentifiersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.drmSystemIdentifiersProvider;
            drmSystemIdentifiersProvider = new HollowObjectCacheProvider(typeDataAccess, drmSystemIdentifiersTypeAPI, factory, previousCacheProvider);
        } else {
            drmSystemIdentifiersProvider = new HollowObjectFactoryProvider(typeDataAccess, drmSystemIdentifiersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("EpisodesEpisodeNameTranslatedTexts");
        if(typeDataAccess != null) {
            episodesEpisodeNameTranslatedTextsTypeAPI = new EpisodesEpisodeNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            episodesEpisodeNameTranslatedTextsTypeAPI = new EpisodesEpisodeNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "EpisodesEpisodeNameTranslatedTexts"));
        }
        addTypeAPI(episodesEpisodeNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("EpisodesEpisodeNameTranslatedTexts");
        if(factory == null)
            factory = new EpisodesEpisodeNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("EpisodesEpisodeNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodesEpisodeNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodesEpisodeNameTranslatedTextsProvider;
            episodesEpisodeNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, episodesEpisodeNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            episodesEpisodeNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, episodesEpisodeNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("EpisodesEpisodeNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            episodesEpisodeNameMapOfTranslatedTextsTypeAPI = new EpisodesEpisodeNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            episodesEpisodeNameMapOfTranslatedTextsTypeAPI = new EpisodesEpisodeNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "EpisodesEpisodeNameMapOfTranslatedTexts"));
        }
        addTypeAPI(episodesEpisodeNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("EpisodesEpisodeNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new EpisodesEpisodeNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("EpisodesEpisodeNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodesEpisodeNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodesEpisodeNameMapOfTranslatedTextsProvider;
            episodesEpisodeNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, episodesEpisodeNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            episodesEpisodeNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, episodesEpisodeNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("EpisodesEpisodeName");
        if(typeDataAccess != null) {
            episodesEpisodeNameTypeAPI = new EpisodesEpisodeNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            episodesEpisodeNameTypeAPI = new EpisodesEpisodeNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "EpisodesEpisodeName"));
        }
        addTypeAPI(episodesEpisodeNameTypeAPI);
        factory = factoryOverrides.get("EpisodesEpisodeName");
        if(factory == null)
            factory = new EpisodesEpisodeNameHollowFactory();
        if(cachedTypes.contains("EpisodesEpisodeName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodesEpisodeNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodesEpisodeNameProvider;
            episodesEpisodeNameProvider = new HollowObjectCacheProvider(typeDataAccess, episodesEpisodeNameTypeAPI, factory, previousCacheProvider);
        } else {
            episodesEpisodeNameProvider = new HollowObjectFactoryProvider(typeDataAccess, episodesEpisodeNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Episodes");
        if(typeDataAccess != null) {
            episodesTypeAPI = new EpisodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            episodesTypeAPI = new EpisodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Episodes"));
        }
        addTypeAPI(episodesTypeAPI);
        factory = factoryOverrides.get("Episodes");
        if(factory == null)
            factory = new EpisodesHollowFactory();
        if(cachedTypes.contains("Episodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodesProvider;
            episodesProvider = new HollowObjectCacheProvider(typeDataAccess, episodesTypeAPI, factory, previousCacheProvider);
        } else {
            episodesProvider = new HollowObjectFactoryProvider(typeDataAccess, episodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsCopyrightTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsCopyrightTranslatedTextsTypeAPI = new FestivalsCopyrightTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsCopyrightTranslatedTextsTypeAPI = new FestivalsCopyrightTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsCopyrightTranslatedTexts"));
        }
        addTypeAPI(festivalsCopyrightTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsCopyrightTranslatedTexts");
        if(factory == null)
            factory = new FestivalsCopyrightTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsCopyrightTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsCopyrightTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsCopyrightTranslatedTextsProvider;
            festivalsCopyrightTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsCopyrightTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsCopyrightTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsCopyrightTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsCopyrightMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsCopyrightMapOfTranslatedTextsTypeAPI = new FestivalsCopyrightMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            festivalsCopyrightMapOfTranslatedTextsTypeAPI = new FestivalsCopyrightMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "FestivalsCopyrightMapOfTranslatedTexts"));
        }
        addTypeAPI(festivalsCopyrightMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsCopyrightMapOfTranslatedTexts");
        if(factory == null)
            factory = new FestivalsCopyrightMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsCopyrightMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsCopyrightMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsCopyrightMapOfTranslatedTextsProvider;
            festivalsCopyrightMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsCopyrightMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsCopyrightMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsCopyrightMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsCopyright");
        if(typeDataAccess != null) {
            festivalsCopyrightTypeAPI = new FestivalsCopyrightTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsCopyrightTypeAPI = new FestivalsCopyrightTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsCopyright"));
        }
        addTypeAPI(festivalsCopyrightTypeAPI);
        factory = factoryOverrides.get("FestivalsCopyright");
        if(factory == null)
            factory = new FestivalsCopyrightHollowFactory();
        if(cachedTypes.contains("FestivalsCopyright")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsCopyrightProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsCopyrightProvider;
            festivalsCopyrightProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsCopyrightTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsCopyrightProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsCopyrightTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsDescriptionTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsDescriptionTranslatedTextsTypeAPI = new FestivalsDescriptionTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsDescriptionTranslatedTextsTypeAPI = new FestivalsDescriptionTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsDescriptionTranslatedTexts"));
        }
        addTypeAPI(festivalsDescriptionTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsDescriptionTranslatedTexts");
        if(factory == null)
            factory = new FestivalsDescriptionTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsDescriptionTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsDescriptionTranslatedTextsProvider;
            festivalsDescriptionTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsDescriptionTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsDescriptionTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsDescriptionTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsDescriptionMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsDescriptionMapOfTranslatedTextsTypeAPI = new FestivalsDescriptionMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            festivalsDescriptionMapOfTranslatedTextsTypeAPI = new FestivalsDescriptionMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "FestivalsDescriptionMapOfTranslatedTexts"));
        }
        addTypeAPI(festivalsDescriptionMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsDescriptionMapOfTranslatedTexts");
        if(factory == null)
            factory = new FestivalsDescriptionMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsDescriptionMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsDescriptionMapOfTranslatedTextsProvider;
            festivalsDescriptionMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsDescriptionMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsDescriptionMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsDescriptionMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsDescription");
        if(typeDataAccess != null) {
            festivalsDescriptionTypeAPI = new FestivalsDescriptionTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsDescriptionTypeAPI = new FestivalsDescriptionTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsDescription"));
        }
        addTypeAPI(festivalsDescriptionTypeAPI);
        factory = factoryOverrides.get("FestivalsDescription");
        if(factory == null)
            factory = new FestivalsDescriptionHollowFactory();
        if(cachedTypes.contains("FestivalsDescription")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsDescriptionProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsDescriptionProvider;
            festivalsDescriptionProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsDescriptionTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsDescriptionProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsDescriptionTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsFestivalNameTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsFestivalNameTranslatedTextsTypeAPI = new FestivalsFestivalNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsFestivalNameTranslatedTextsTypeAPI = new FestivalsFestivalNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsFestivalNameTranslatedTexts"));
        }
        addTypeAPI(festivalsFestivalNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsFestivalNameTranslatedTexts");
        if(factory == null)
            factory = new FestivalsFestivalNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsFestivalNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsFestivalNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsFestivalNameTranslatedTextsProvider;
            festivalsFestivalNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsFestivalNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsFestivalNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsFestivalNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsFestivalNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsFestivalNameMapOfTranslatedTextsTypeAPI = new FestivalsFestivalNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            festivalsFestivalNameMapOfTranslatedTextsTypeAPI = new FestivalsFestivalNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "FestivalsFestivalNameMapOfTranslatedTexts"));
        }
        addTypeAPI(festivalsFestivalNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsFestivalNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new FestivalsFestivalNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsFestivalNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsFestivalNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsFestivalNameMapOfTranslatedTextsProvider;
            festivalsFestivalNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsFestivalNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsFestivalNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsFestivalNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsFestivalName");
        if(typeDataAccess != null) {
            festivalsFestivalNameTypeAPI = new FestivalsFestivalNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsFestivalNameTypeAPI = new FestivalsFestivalNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsFestivalName"));
        }
        addTypeAPI(festivalsFestivalNameTypeAPI);
        factory = factoryOverrides.get("FestivalsFestivalName");
        if(factory == null)
            factory = new FestivalsFestivalNameHollowFactory();
        if(cachedTypes.contains("FestivalsFestivalName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsFestivalNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsFestivalNameProvider;
            festivalsFestivalNameProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsFestivalNameTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsFestivalNameProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsFestivalNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsShortNameTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsShortNameTranslatedTextsTypeAPI = new FestivalsShortNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsShortNameTranslatedTextsTypeAPI = new FestivalsShortNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsShortNameTranslatedTexts"));
        }
        addTypeAPI(festivalsShortNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsShortNameTranslatedTexts");
        if(factory == null)
            factory = new FestivalsShortNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsShortNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsShortNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsShortNameTranslatedTextsProvider;
            festivalsShortNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsShortNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsShortNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsShortNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsShortNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsShortNameMapOfTranslatedTextsTypeAPI = new FestivalsShortNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            festivalsShortNameMapOfTranslatedTextsTypeAPI = new FestivalsShortNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "FestivalsShortNameMapOfTranslatedTexts"));
        }
        addTypeAPI(festivalsShortNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsShortNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new FestivalsShortNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsShortNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsShortNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsShortNameMapOfTranslatedTextsProvider;
            festivalsShortNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsShortNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsShortNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsShortNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsShortName");
        if(typeDataAccess != null) {
            festivalsShortNameTypeAPI = new FestivalsShortNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsShortNameTypeAPI = new FestivalsShortNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsShortName"));
        }
        addTypeAPI(festivalsShortNameTypeAPI);
        factory = factoryOverrides.get("FestivalsShortName");
        if(factory == null)
            factory = new FestivalsShortNameHollowFactory();
        if(cachedTypes.contains("FestivalsShortName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsShortNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsShortNameProvider;
            festivalsShortNameProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsShortNameTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsShortNameProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsShortNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsSingularNameTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsSingularNameTranslatedTextsTypeAPI = new FestivalsSingularNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsSingularNameTranslatedTextsTypeAPI = new FestivalsSingularNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsSingularNameTranslatedTexts"));
        }
        addTypeAPI(festivalsSingularNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsSingularNameTranslatedTexts");
        if(factory == null)
            factory = new FestivalsSingularNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsSingularNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsSingularNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsSingularNameTranslatedTextsProvider;
            festivalsSingularNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsSingularNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsSingularNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsSingularNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsSingularNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            festivalsSingularNameMapOfTranslatedTextsTypeAPI = new FestivalsSingularNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            festivalsSingularNameMapOfTranslatedTextsTypeAPI = new FestivalsSingularNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "FestivalsSingularNameMapOfTranslatedTexts"));
        }
        addTypeAPI(festivalsSingularNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("FestivalsSingularNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new FestivalsSingularNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("FestivalsSingularNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsSingularNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsSingularNameMapOfTranslatedTextsProvider;
            festivalsSingularNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsSingularNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsSingularNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsSingularNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("FestivalsSingularName");
        if(typeDataAccess != null) {
            festivalsSingularNameTypeAPI = new FestivalsSingularNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsSingularNameTypeAPI = new FestivalsSingularNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "FestivalsSingularName"));
        }
        addTypeAPI(festivalsSingularNameTypeAPI);
        factory = factoryOverrides.get("FestivalsSingularName");
        if(factory == null)
            factory = new FestivalsSingularNameHollowFactory();
        if(cachedTypes.contains("FestivalsSingularName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsSingularNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsSingularNameProvider;
            festivalsSingularNameProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsSingularNameTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsSingularNameProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsSingularNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Festivals");
        if(typeDataAccess != null) {
            festivalsTypeAPI = new FestivalsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            festivalsTypeAPI = new FestivalsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Festivals"));
        }
        addTypeAPI(festivalsTypeAPI);
        factory = factoryOverrides.get("Festivals");
        if(factory == null)
            factory = new FestivalsHollowFactory();
        if(cachedTypes.contains("Festivals")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.festivalsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.festivalsProvider;
            festivalsProvider = new HollowObjectCacheProvider(typeDataAccess, festivalsTypeAPI, factory, previousCacheProvider);
        } else {
            festivalsProvider = new HollowObjectFactoryProvider(typeDataAccess, festivalsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LanguagesNameTranslatedTexts");
        if(typeDataAccess != null) {
            languagesNameTranslatedTextsTypeAPI = new LanguagesNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            languagesNameTranslatedTextsTypeAPI = new LanguagesNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LanguagesNameTranslatedTexts"));
        }
        addTypeAPI(languagesNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("LanguagesNameTranslatedTexts");
        if(factory == null)
            factory = new LanguagesNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("LanguagesNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.languagesNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.languagesNameTranslatedTextsProvider;
            languagesNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, languagesNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            languagesNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, languagesNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LanguagesNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            languagesNameMapOfTranslatedTextsTypeAPI = new LanguagesNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            languagesNameMapOfTranslatedTextsTypeAPI = new LanguagesNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "LanguagesNameMapOfTranslatedTexts"));
        }
        addTypeAPI(languagesNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("LanguagesNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new LanguagesNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("LanguagesNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.languagesNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.languagesNameMapOfTranslatedTextsProvider;
            languagesNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, languagesNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            languagesNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, languagesNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LanguagesName");
        if(typeDataAccess != null) {
            languagesNameTypeAPI = new LanguagesNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            languagesNameTypeAPI = new LanguagesNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LanguagesName"));
        }
        addTypeAPI(languagesNameTypeAPI);
        factory = factoryOverrides.get("LanguagesName");
        if(factory == null)
            factory = new LanguagesNameHollowFactory();
        if(cachedTypes.contains("LanguagesName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.languagesNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.languagesNameProvider;
            languagesNameProvider = new HollowObjectCacheProvider(typeDataAccess, languagesNameTypeAPI, factory, previousCacheProvider);
        } else {
            languagesNameProvider = new HollowObjectFactoryProvider(typeDataAccess, languagesNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Languages");
        if(typeDataAccess != null) {
            languagesTypeAPI = new LanguagesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            languagesTypeAPI = new LanguagesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Languages"));
        }
        addTypeAPI(languagesTypeAPI);
        factory = factoryOverrides.get("Languages");
        if(factory == null)
            factory = new LanguagesHollowFactory();
        if(cachedTypes.contains("Languages")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.languagesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.languagesProvider;
            languagesProvider = new HollowObjectCacheProvider(typeDataAccess, languagesTypeAPI, factory, previousCacheProvider);
        } else {
            languagesProvider = new HollowObjectFactoryProvider(typeDataAccess, languagesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocalizedCharacterTranslatedTexts");
        if(typeDataAccess != null) {
            localizedCharacterTranslatedTextsTypeAPI = new LocalizedCharacterTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            localizedCharacterTranslatedTextsTypeAPI = new LocalizedCharacterTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LocalizedCharacterTranslatedTexts"));
        }
        addTypeAPI(localizedCharacterTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("LocalizedCharacterTranslatedTexts");
        if(factory == null)
            factory = new LocalizedCharacterTranslatedTextsHollowFactory();
        if(cachedTypes.contains("LocalizedCharacterTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localizedCharacterTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localizedCharacterTranslatedTextsProvider;
            localizedCharacterTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, localizedCharacterTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            localizedCharacterTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, localizedCharacterTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocalizedCharacterMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            localizedCharacterMapOfTranslatedTextsTypeAPI = new LocalizedCharacterMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            localizedCharacterMapOfTranslatedTextsTypeAPI = new LocalizedCharacterMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "LocalizedCharacterMapOfTranslatedTexts"));
        }
        addTypeAPI(localizedCharacterMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("LocalizedCharacterMapOfTranslatedTexts");
        if(factory == null)
            factory = new LocalizedCharacterMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("LocalizedCharacterMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localizedCharacterMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localizedCharacterMapOfTranslatedTextsProvider;
            localizedCharacterMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, localizedCharacterMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            localizedCharacterMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, localizedCharacterMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocalizedCharacter");
        if(typeDataAccess != null) {
            localizedCharacterTypeAPI = new LocalizedCharacterTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            localizedCharacterTypeAPI = new LocalizedCharacterTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LocalizedCharacter"));
        }
        addTypeAPI(localizedCharacterTypeAPI);
        factory = factoryOverrides.get("LocalizedCharacter");
        if(factory == null)
            factory = new LocalizedCharacterHollowFactory();
        if(cachedTypes.contains("LocalizedCharacter")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localizedCharacterProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localizedCharacterProvider;
            localizedCharacterProvider = new HollowObjectCacheProvider(typeDataAccess, localizedCharacterTypeAPI, factory, previousCacheProvider);
        } else {
            localizedCharacterProvider = new HollowObjectFactoryProvider(typeDataAccess, localizedCharacterTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocalizedMetadataTranslatedTexts");
        if(typeDataAccess != null) {
            localizedMetadataTranslatedTextsTypeAPI = new LocalizedMetadataTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            localizedMetadataTranslatedTextsTypeAPI = new LocalizedMetadataTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LocalizedMetadataTranslatedTexts"));
        }
        addTypeAPI(localizedMetadataTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("LocalizedMetadataTranslatedTexts");
        if(factory == null)
            factory = new LocalizedMetadataTranslatedTextsHollowFactory();
        if(cachedTypes.contains("LocalizedMetadataTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localizedMetadataTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localizedMetadataTranslatedTextsProvider;
            localizedMetadataTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, localizedMetadataTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            localizedMetadataTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, localizedMetadataTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocalizedMetadataMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            localizedMetadataMapOfTranslatedTextsTypeAPI = new LocalizedMetadataMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            localizedMetadataMapOfTranslatedTextsTypeAPI = new LocalizedMetadataMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "LocalizedMetadataMapOfTranslatedTexts"));
        }
        addTypeAPI(localizedMetadataMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("LocalizedMetadataMapOfTranslatedTexts");
        if(factory == null)
            factory = new LocalizedMetadataMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("LocalizedMetadataMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localizedMetadataMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localizedMetadataMapOfTranslatedTextsProvider;
            localizedMetadataMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, localizedMetadataMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            localizedMetadataMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, localizedMetadataMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocalizedMetadata");
        if(typeDataAccess != null) {
            localizedMetadataTypeAPI = new LocalizedMetadataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            localizedMetadataTypeAPI = new LocalizedMetadataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LocalizedMetadata"));
        }
        addTypeAPI(localizedMetadataTypeAPI);
        factory = factoryOverrides.get("LocalizedMetadata");
        if(factory == null)
            factory = new LocalizedMetadataHollowFactory();
        if(cachedTypes.contains("LocalizedMetadata")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localizedMetadataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localizedMetadataProvider;
            localizedMetadataProvider = new HollowObjectCacheProvider(typeDataAccess, localizedMetadataTypeAPI, factory, previousCacheProvider);
        } else {
            localizedMetadataProvider = new HollowObjectFactoryProvider(typeDataAccess, localizedMetadataTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieRatingsRatingReasonTranslatedTexts");
        if(typeDataAccess != null) {
            movieRatingsRatingReasonTranslatedTextsTypeAPI = new MovieRatingsRatingReasonTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieRatingsRatingReasonTranslatedTextsTypeAPI = new MovieRatingsRatingReasonTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieRatingsRatingReasonTranslatedTexts"));
        }
        addTypeAPI(movieRatingsRatingReasonTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MovieRatingsRatingReasonTranslatedTexts");
        if(factory == null)
            factory = new MovieRatingsRatingReasonTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MovieRatingsRatingReasonTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieRatingsRatingReasonTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieRatingsRatingReasonTranslatedTextsProvider;
            movieRatingsRatingReasonTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, movieRatingsRatingReasonTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            movieRatingsRatingReasonTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, movieRatingsRatingReasonTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieRatingsRatingReasonMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            movieRatingsRatingReasonMapOfTranslatedTextsTypeAPI = new MovieRatingsRatingReasonMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            movieRatingsRatingReasonMapOfTranslatedTextsTypeAPI = new MovieRatingsRatingReasonMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MovieRatingsRatingReasonMapOfTranslatedTexts"));
        }
        addTypeAPI(movieRatingsRatingReasonMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MovieRatingsRatingReasonMapOfTranslatedTexts");
        if(factory == null)
            factory = new MovieRatingsRatingReasonMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MovieRatingsRatingReasonMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieRatingsRatingReasonMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieRatingsRatingReasonMapOfTranslatedTextsProvider;
            movieRatingsRatingReasonMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, movieRatingsRatingReasonMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            movieRatingsRatingReasonMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, movieRatingsRatingReasonMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieRatingsRatingReason");
        if(typeDataAccess != null) {
            movieRatingsRatingReasonTypeAPI = new MovieRatingsRatingReasonTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieRatingsRatingReasonTypeAPI = new MovieRatingsRatingReasonTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieRatingsRatingReason"));
        }
        addTypeAPI(movieRatingsRatingReasonTypeAPI);
        factory = factoryOverrides.get("MovieRatingsRatingReason");
        if(factory == null)
            factory = new MovieRatingsRatingReasonHollowFactory();
        if(cachedTypes.contains("MovieRatingsRatingReason")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieRatingsRatingReasonProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieRatingsRatingReasonProvider;
            movieRatingsRatingReasonProvider = new HollowObjectCacheProvider(typeDataAccess, movieRatingsRatingReasonTypeAPI, factory, previousCacheProvider);
        } else {
            movieRatingsRatingReasonProvider = new HollowObjectFactoryProvider(typeDataAccess, movieRatingsRatingReasonTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieRatings");
        if(typeDataAccess != null) {
            movieRatingsTypeAPI = new MovieRatingsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieRatingsTypeAPI = new MovieRatingsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieRatings"));
        }
        addTypeAPI(movieRatingsTypeAPI);
        factory = factoryOverrides.get("MovieRatings");
        if(factory == null)
            factory = new MovieRatingsHollowFactory();
        if(cachedTypes.contains("MovieRatings")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieRatingsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieRatingsProvider;
            movieRatingsProvider = new HollowObjectCacheProvider(typeDataAccess, movieRatingsTypeAPI, factory, previousCacheProvider);
        } else {
            movieRatingsProvider = new HollowObjectFactoryProvider(typeDataAccess, movieRatingsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesAkaTranslatedTexts");
        if(typeDataAccess != null) {
            moviesAkaTranslatedTextsTypeAPI = new MoviesAkaTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesAkaTranslatedTextsTypeAPI = new MoviesAkaTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesAkaTranslatedTexts"));
        }
        addTypeAPI(moviesAkaTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesAkaTranslatedTexts");
        if(factory == null)
            factory = new MoviesAkaTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesAkaTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesAkaTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesAkaTranslatedTextsProvider;
            moviesAkaTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesAkaTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesAkaTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesAkaTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesAkaMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            moviesAkaMapOfTranslatedTextsTypeAPI = new MoviesAkaMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            moviesAkaMapOfTranslatedTextsTypeAPI = new MoviesAkaMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MoviesAkaMapOfTranslatedTexts"));
        }
        addTypeAPI(moviesAkaMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesAkaMapOfTranslatedTexts");
        if(factory == null)
            factory = new MoviesAkaMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesAkaMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesAkaMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesAkaMapOfTranslatedTextsProvider;
            moviesAkaMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesAkaMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesAkaMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesAkaMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesAka");
        if(typeDataAccess != null) {
            moviesAkaTypeAPI = new MoviesAkaTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesAkaTypeAPI = new MoviesAkaTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesAka"));
        }
        addTypeAPI(moviesAkaTypeAPI);
        factory = factoryOverrides.get("MoviesAka");
        if(factory == null)
            factory = new MoviesAkaHollowFactory();
        if(cachedTypes.contains("MoviesAka")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesAkaProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesAkaProvider;
            moviesAkaProvider = new HollowObjectCacheProvider(typeDataAccess, moviesAkaTypeAPI, factory, previousCacheProvider);
        } else {
            moviesAkaProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesAkaTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesDisplayNameTranslatedTexts");
        if(typeDataAccess != null) {
            moviesDisplayNameTranslatedTextsTypeAPI = new MoviesDisplayNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesDisplayNameTranslatedTextsTypeAPI = new MoviesDisplayNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesDisplayNameTranslatedTexts"));
        }
        addTypeAPI(moviesDisplayNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesDisplayNameTranslatedTexts");
        if(factory == null)
            factory = new MoviesDisplayNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesDisplayNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesDisplayNameTranslatedTextsProvider;
            moviesDisplayNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesDisplayNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesDisplayNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesDisplayNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesDisplayNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            moviesDisplayNameMapOfTranslatedTextsTypeAPI = new MoviesDisplayNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            moviesDisplayNameMapOfTranslatedTextsTypeAPI = new MoviesDisplayNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MoviesDisplayNameMapOfTranslatedTexts"));
        }
        addTypeAPI(moviesDisplayNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesDisplayNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new MoviesDisplayNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesDisplayNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesDisplayNameMapOfTranslatedTextsProvider;
            moviesDisplayNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesDisplayNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesDisplayNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesDisplayNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesDisplayName");
        if(typeDataAccess != null) {
            moviesDisplayNameTypeAPI = new MoviesDisplayNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesDisplayNameTypeAPI = new MoviesDisplayNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesDisplayName"));
        }
        addTypeAPI(moviesDisplayNameTypeAPI);
        factory = factoryOverrides.get("MoviesDisplayName");
        if(factory == null)
            factory = new MoviesDisplayNameHollowFactory();
        if(cachedTypes.contains("MoviesDisplayName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesDisplayNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesDisplayNameProvider;
            moviesDisplayNameProvider = new HollowObjectCacheProvider(typeDataAccess, moviesDisplayNameTypeAPI, factory, previousCacheProvider);
        } else {
            moviesDisplayNameProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesDisplayNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesOriginalTitleTranslatedTexts");
        if(typeDataAccess != null) {
            moviesOriginalTitleTranslatedTextsTypeAPI = new MoviesOriginalTitleTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesOriginalTitleTranslatedTextsTypeAPI = new MoviesOriginalTitleTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesOriginalTitleTranslatedTexts"));
        }
        addTypeAPI(moviesOriginalTitleTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesOriginalTitleTranslatedTexts");
        if(factory == null)
            factory = new MoviesOriginalTitleTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesOriginalTitleTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesOriginalTitleTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesOriginalTitleTranslatedTextsProvider;
            moviesOriginalTitleTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesOriginalTitleTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesOriginalTitleTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesOriginalTitleTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesOriginalTitleMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            moviesOriginalTitleMapOfTranslatedTextsTypeAPI = new MoviesOriginalTitleMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            moviesOriginalTitleMapOfTranslatedTextsTypeAPI = new MoviesOriginalTitleMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MoviesOriginalTitleMapOfTranslatedTexts"));
        }
        addTypeAPI(moviesOriginalTitleMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesOriginalTitleMapOfTranslatedTexts");
        if(factory == null)
            factory = new MoviesOriginalTitleMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesOriginalTitleMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesOriginalTitleMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesOriginalTitleMapOfTranslatedTextsProvider;
            moviesOriginalTitleMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesOriginalTitleMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesOriginalTitleMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesOriginalTitleMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesOriginalTitle");
        if(typeDataAccess != null) {
            moviesOriginalTitleTypeAPI = new MoviesOriginalTitleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesOriginalTitleTypeAPI = new MoviesOriginalTitleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesOriginalTitle"));
        }
        addTypeAPI(moviesOriginalTitleTypeAPI);
        factory = factoryOverrides.get("MoviesOriginalTitle");
        if(factory == null)
            factory = new MoviesOriginalTitleHollowFactory();
        if(cachedTypes.contains("MoviesOriginalTitle")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesOriginalTitleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesOriginalTitleProvider;
            moviesOriginalTitleProvider = new HollowObjectCacheProvider(typeDataAccess, moviesOriginalTitleTypeAPI, factory, previousCacheProvider);
        } else {
            moviesOriginalTitleProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesOriginalTitleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesShortDisplayNameTranslatedTexts");
        if(typeDataAccess != null) {
            moviesShortDisplayNameTranslatedTextsTypeAPI = new MoviesShortDisplayNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesShortDisplayNameTranslatedTextsTypeAPI = new MoviesShortDisplayNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesShortDisplayNameTranslatedTexts"));
        }
        addTypeAPI(moviesShortDisplayNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesShortDisplayNameTranslatedTexts");
        if(factory == null)
            factory = new MoviesShortDisplayNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesShortDisplayNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesShortDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesShortDisplayNameTranslatedTextsProvider;
            moviesShortDisplayNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesShortDisplayNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesShortDisplayNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesShortDisplayNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesShortDisplayNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            moviesShortDisplayNameMapOfTranslatedTextsTypeAPI = new MoviesShortDisplayNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            moviesShortDisplayNameMapOfTranslatedTextsTypeAPI = new MoviesShortDisplayNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MoviesShortDisplayNameMapOfTranslatedTexts"));
        }
        addTypeAPI(moviesShortDisplayNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesShortDisplayNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new MoviesShortDisplayNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesShortDisplayNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesShortDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesShortDisplayNameMapOfTranslatedTextsProvider;
            moviesShortDisplayNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesShortDisplayNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesShortDisplayNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesShortDisplayNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesShortDisplayName");
        if(typeDataAccess != null) {
            moviesShortDisplayNameTypeAPI = new MoviesShortDisplayNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesShortDisplayNameTypeAPI = new MoviesShortDisplayNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesShortDisplayName"));
        }
        addTypeAPI(moviesShortDisplayNameTypeAPI);
        factory = factoryOverrides.get("MoviesShortDisplayName");
        if(factory == null)
            factory = new MoviesShortDisplayNameHollowFactory();
        if(cachedTypes.contains("MoviesShortDisplayName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesShortDisplayNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesShortDisplayNameProvider;
            moviesShortDisplayNameProvider = new HollowObjectCacheProvider(typeDataAccess, moviesShortDisplayNameTypeAPI, factory, previousCacheProvider);
        } else {
            moviesShortDisplayNameProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesShortDisplayNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesSiteSynopsisTranslatedTexts");
        if(typeDataAccess != null) {
            moviesSiteSynopsisTranslatedTextsTypeAPI = new MoviesSiteSynopsisTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesSiteSynopsisTranslatedTextsTypeAPI = new MoviesSiteSynopsisTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesSiteSynopsisTranslatedTexts"));
        }
        addTypeAPI(moviesSiteSynopsisTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesSiteSynopsisTranslatedTexts");
        if(factory == null)
            factory = new MoviesSiteSynopsisTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesSiteSynopsisTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesSiteSynopsisTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesSiteSynopsisTranslatedTextsProvider;
            moviesSiteSynopsisTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesSiteSynopsisTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesSiteSynopsisTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesSiteSynopsisTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesSiteSynopsisMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            moviesSiteSynopsisMapOfTranslatedTextsTypeAPI = new MoviesSiteSynopsisMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            moviesSiteSynopsisMapOfTranslatedTextsTypeAPI = new MoviesSiteSynopsisMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MoviesSiteSynopsisMapOfTranslatedTexts"));
        }
        addTypeAPI(moviesSiteSynopsisMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesSiteSynopsisMapOfTranslatedTexts");
        if(factory == null)
            factory = new MoviesSiteSynopsisMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesSiteSynopsisMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesSiteSynopsisMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesSiteSynopsisMapOfTranslatedTextsProvider;
            moviesSiteSynopsisMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesSiteSynopsisMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesSiteSynopsisMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesSiteSynopsisMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesSiteSynopsis");
        if(typeDataAccess != null) {
            moviesSiteSynopsisTypeAPI = new MoviesSiteSynopsisTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesSiteSynopsisTypeAPI = new MoviesSiteSynopsisTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesSiteSynopsis"));
        }
        addTypeAPI(moviesSiteSynopsisTypeAPI);
        factory = factoryOverrides.get("MoviesSiteSynopsis");
        if(factory == null)
            factory = new MoviesSiteSynopsisHollowFactory();
        if(cachedTypes.contains("MoviesSiteSynopsis")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesSiteSynopsisProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesSiteSynopsisProvider;
            moviesSiteSynopsisProvider = new HollowObjectCacheProvider(typeDataAccess, moviesSiteSynopsisTypeAPI, factory, previousCacheProvider);
        } else {
            moviesSiteSynopsisProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesSiteSynopsisTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesTransliteratedTranslatedTexts");
        if(typeDataAccess != null) {
            moviesTransliteratedTranslatedTextsTypeAPI = new MoviesTransliteratedTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesTransliteratedTranslatedTextsTypeAPI = new MoviesTransliteratedTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesTransliteratedTranslatedTexts"));
        }
        addTypeAPI(moviesTransliteratedTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesTransliteratedTranslatedTexts");
        if(factory == null)
            factory = new MoviesTransliteratedTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesTransliteratedTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesTransliteratedTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesTransliteratedTranslatedTextsProvider;
            moviesTransliteratedTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesTransliteratedTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesTransliteratedTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesTransliteratedTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesTransliteratedMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            moviesTransliteratedMapOfTranslatedTextsTypeAPI = new MoviesTransliteratedMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            moviesTransliteratedMapOfTranslatedTextsTypeAPI = new MoviesTransliteratedMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MoviesTransliteratedMapOfTranslatedTexts"));
        }
        addTypeAPI(moviesTransliteratedMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesTransliteratedMapOfTranslatedTexts");
        if(factory == null)
            factory = new MoviesTransliteratedMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesTransliteratedMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesTransliteratedMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesTransliteratedMapOfTranslatedTextsProvider;
            moviesTransliteratedMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesTransliteratedMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesTransliteratedMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesTransliteratedMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesTransliterated");
        if(typeDataAccess != null) {
            moviesTransliteratedTypeAPI = new MoviesTransliteratedTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesTransliteratedTypeAPI = new MoviesTransliteratedTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesTransliterated"));
        }
        addTypeAPI(moviesTransliteratedTypeAPI);
        factory = factoryOverrides.get("MoviesTransliterated");
        if(factory == null)
            factory = new MoviesTransliteratedHollowFactory();
        if(cachedTypes.contains("MoviesTransliterated")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesTransliteratedProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesTransliteratedProvider;
            moviesTransliteratedProvider = new HollowObjectCacheProvider(typeDataAccess, moviesTransliteratedTypeAPI, factory, previousCacheProvider);
        } else {
            moviesTransliteratedProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesTransliteratedTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesTvSynopsisTranslatedTexts");
        if(typeDataAccess != null) {
            moviesTvSynopsisTranslatedTextsTypeAPI = new MoviesTvSynopsisTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesTvSynopsisTranslatedTextsTypeAPI = new MoviesTvSynopsisTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesTvSynopsisTranslatedTexts"));
        }
        addTypeAPI(moviesTvSynopsisTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesTvSynopsisTranslatedTexts");
        if(factory == null)
            factory = new MoviesTvSynopsisTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesTvSynopsisTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesTvSynopsisTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesTvSynopsisTranslatedTextsProvider;
            moviesTvSynopsisTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesTvSynopsisTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesTvSynopsisTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesTvSynopsisTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesTvSynopsisMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            moviesTvSynopsisMapOfTranslatedTextsTypeAPI = new MoviesTvSynopsisMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            moviesTvSynopsisMapOfTranslatedTextsTypeAPI = new MoviesTvSynopsisMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MoviesTvSynopsisMapOfTranslatedTexts"));
        }
        addTypeAPI(moviesTvSynopsisMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("MoviesTvSynopsisMapOfTranslatedTexts");
        if(factory == null)
            factory = new MoviesTvSynopsisMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("MoviesTvSynopsisMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesTvSynopsisMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesTvSynopsisMapOfTranslatedTextsProvider;
            moviesTvSynopsisMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, moviesTvSynopsisMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            moviesTvSynopsisMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesTvSynopsisMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MoviesTvSynopsis");
        if(typeDataAccess != null) {
            moviesTvSynopsisTypeAPI = new MoviesTvSynopsisTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesTvSynopsisTypeAPI = new MoviesTvSynopsisTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MoviesTvSynopsis"));
        }
        addTypeAPI(moviesTvSynopsisTypeAPI);
        factory = factoryOverrides.get("MoviesTvSynopsis");
        if(factory == null)
            factory = new MoviesTvSynopsisHollowFactory();
        if(cachedTypes.contains("MoviesTvSynopsis")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesTvSynopsisProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesTvSynopsisProvider;
            moviesTvSynopsisProvider = new HollowObjectCacheProvider(typeDataAccess, moviesTvSynopsisTypeAPI, factory, previousCacheProvider);
        } else {
            moviesTvSynopsisProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesTvSynopsisTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Movies");
        if(typeDataAccess != null) {
            moviesTypeAPI = new MoviesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            moviesTypeAPI = new MoviesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Movies"));
        }
        addTypeAPI(moviesTypeAPI);
        factory = factoryOverrides.get("Movies");
        if(factory == null)
            factory = new MoviesHollowFactory();
        if(cachedTypes.contains("Movies")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.moviesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.moviesProvider;
            moviesProvider = new HollowObjectCacheProvider(typeDataAccess, moviesTypeAPI, factory, previousCacheProvider);
        } else {
            moviesProvider = new HollowObjectFactoryProvider(typeDataAccess, moviesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("OriginServers");
        if(typeDataAccess != null) {
            originServersTypeAPI = new OriginServersTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            originServersTypeAPI = new OriginServersTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "OriginServers"));
        }
        addTypeAPI(originServersTypeAPI);
        factory = factoryOverrides.get("OriginServers");
        if(factory == null)
            factory = new OriginServersHollowFactory();
        if(cachedTypes.contains("OriginServers")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.originServersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.originServersProvider;
            originServersProvider = new HollowObjectCacheProvider(typeDataAccess, originServersTypeAPI, factory, previousCacheProvider);
        } else {
            originServersProvider = new HollowObjectFactoryProvider(typeDataAccess, originServersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonAliasesNameTranslatedTexts");
        if(typeDataAccess != null) {
            personAliasesNameTranslatedTextsTypeAPI = new PersonAliasesNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personAliasesNameTranslatedTextsTypeAPI = new PersonAliasesNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonAliasesNameTranslatedTexts"));
        }
        addTypeAPI(personAliasesNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("PersonAliasesNameTranslatedTexts");
        if(factory == null)
            factory = new PersonAliasesNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("PersonAliasesNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personAliasesNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personAliasesNameTranslatedTextsProvider;
            personAliasesNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, personAliasesNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            personAliasesNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, personAliasesNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonAliasesNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            personAliasesNameMapOfTranslatedTextsTypeAPI = new PersonAliasesNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            personAliasesNameMapOfTranslatedTextsTypeAPI = new PersonAliasesNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "PersonAliasesNameMapOfTranslatedTexts"));
        }
        addTypeAPI(personAliasesNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("PersonAliasesNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new PersonAliasesNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("PersonAliasesNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personAliasesNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personAliasesNameMapOfTranslatedTextsProvider;
            personAliasesNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, personAliasesNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            personAliasesNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, personAliasesNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonAliasesName");
        if(typeDataAccess != null) {
            personAliasesNameTypeAPI = new PersonAliasesNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personAliasesNameTypeAPI = new PersonAliasesNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonAliasesName"));
        }
        addTypeAPI(personAliasesNameTypeAPI);
        factory = factoryOverrides.get("PersonAliasesName");
        if(factory == null)
            factory = new PersonAliasesNameHollowFactory();
        if(cachedTypes.contains("PersonAliasesName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personAliasesNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personAliasesNameProvider;
            personAliasesNameProvider = new HollowObjectCacheProvider(typeDataAccess, personAliasesNameTypeAPI, factory, previousCacheProvider);
        } else {
            personAliasesNameProvider = new HollowObjectFactoryProvider(typeDataAccess, personAliasesNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonAliases");
        if(typeDataAccess != null) {
            personAliasesTypeAPI = new PersonAliasesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personAliasesTypeAPI = new PersonAliasesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonAliases"));
        }
        addTypeAPI(personAliasesTypeAPI);
        factory = factoryOverrides.get("PersonAliases");
        if(factory == null)
            factory = new PersonAliasesHollowFactory();
        if(cachedTypes.contains("PersonAliases")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personAliasesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personAliasesProvider;
            personAliasesProvider = new HollowObjectCacheProvider(typeDataAccess, personAliasesTypeAPI, factory, previousCacheProvider);
        } else {
            personAliasesProvider = new HollowObjectFactoryProvider(typeDataAccess, personAliasesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkAttributes");
        if(typeDataAccess != null) {
            personArtworkAttributesTypeAPI = new PersonArtworkAttributesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkAttributesTypeAPI = new PersonArtworkAttributesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkAttributes"));
        }
        addTypeAPI(personArtworkAttributesTypeAPI);
        factory = factoryOverrides.get("PersonArtworkAttributes");
        if(factory == null)
            factory = new PersonArtworkAttributesHollowFactory();
        if(cachedTypes.contains("PersonArtworkAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkAttributesProvider;
            personArtworkAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkDerivatives");
        if(typeDataAccess != null) {
            personArtworkDerivativesTypeAPI = new PersonArtworkDerivativesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkDerivativesTypeAPI = new PersonArtworkDerivativesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkDerivatives"));
        }
        addTypeAPI(personArtworkDerivativesTypeAPI);
        factory = factoryOverrides.get("PersonArtworkDerivatives");
        if(factory == null)
            factory = new PersonArtworkDerivativesHollowFactory();
        if(cachedTypes.contains("PersonArtworkDerivatives")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkDerivativesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkDerivativesProvider;
            personArtworkDerivativesProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkDerivativesTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkDerivativesProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkDerivativesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkArrayOfDerivatives");
        if(typeDataAccess != null) {
            personArtworkArrayOfDerivativesTypeAPI = new PersonArtworkArrayOfDerivativesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personArtworkArrayOfDerivativesTypeAPI = new PersonArtworkArrayOfDerivativesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonArtworkArrayOfDerivatives"));
        }
        addTypeAPI(personArtworkArrayOfDerivativesTypeAPI);
        factory = factoryOverrides.get("PersonArtworkArrayOfDerivatives");
        if(factory == null)
            factory = new PersonArtworkArrayOfDerivativesHollowFactory();
        if(cachedTypes.contains("PersonArtworkArrayOfDerivatives")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkArrayOfDerivativesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkArrayOfDerivativesProvider;
            personArtworkArrayOfDerivativesProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkArrayOfDerivativesTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkArrayOfDerivativesProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkArrayOfDerivativesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkLocalesTerritoryCodes");
        if(typeDataAccess != null) {
            personArtworkLocalesTerritoryCodesTypeAPI = new PersonArtworkLocalesTerritoryCodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkLocalesTerritoryCodesTypeAPI = new PersonArtworkLocalesTerritoryCodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkLocalesTerritoryCodes"));
        }
        addTypeAPI(personArtworkLocalesTerritoryCodesTypeAPI);
        factory = factoryOverrides.get("PersonArtworkLocalesTerritoryCodes");
        if(factory == null)
            factory = new PersonArtworkLocalesTerritoryCodesHollowFactory();
        if(cachedTypes.contains("PersonArtworkLocalesTerritoryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkLocalesTerritoryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkLocalesTerritoryCodesProvider;
            personArtworkLocalesTerritoryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkLocalesTerritoryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkLocalesTerritoryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkLocalesTerritoryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkLocalesArrayOfTerritoryCodes");
        if(typeDataAccess != null) {
            personArtworkLocalesArrayOfTerritoryCodesTypeAPI = new PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personArtworkLocalesArrayOfTerritoryCodesTypeAPI = new PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonArtworkLocalesArrayOfTerritoryCodes"));
        }
        addTypeAPI(personArtworkLocalesArrayOfTerritoryCodesTypeAPI);
        factory = factoryOverrides.get("PersonArtworkLocalesArrayOfTerritoryCodes");
        if(factory == null)
            factory = new PersonArtworkLocalesArrayOfTerritoryCodesHollowFactory();
        if(cachedTypes.contains("PersonArtworkLocalesArrayOfTerritoryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkLocalesArrayOfTerritoryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkLocalesArrayOfTerritoryCodesProvider;
            personArtworkLocalesArrayOfTerritoryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkLocalesArrayOfTerritoryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkLocalesArrayOfTerritoryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkLocalesArrayOfTerritoryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkLocales");
        if(typeDataAccess != null) {
            personArtworkLocalesTypeAPI = new PersonArtworkLocalesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkLocalesTypeAPI = new PersonArtworkLocalesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkLocales"));
        }
        addTypeAPI(personArtworkLocalesTypeAPI);
        factory = factoryOverrides.get("PersonArtworkLocales");
        if(factory == null)
            factory = new PersonArtworkLocalesHollowFactory();
        if(cachedTypes.contains("PersonArtworkLocales")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkLocalesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkLocalesProvider;
            personArtworkLocalesProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkLocalesTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkLocalesProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkLocalesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkArrayOfLocales");
        if(typeDataAccess != null) {
            personArtworkArrayOfLocalesTypeAPI = new PersonArtworkArrayOfLocalesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personArtworkArrayOfLocalesTypeAPI = new PersonArtworkArrayOfLocalesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonArtworkArrayOfLocales"));
        }
        addTypeAPI(personArtworkArrayOfLocalesTypeAPI);
        factory = factoryOverrides.get("PersonArtworkArrayOfLocales");
        if(factory == null)
            factory = new PersonArtworkArrayOfLocalesHollowFactory();
        if(cachedTypes.contains("PersonArtworkArrayOfLocales")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkArrayOfLocalesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkArrayOfLocalesProvider;
            personArtworkArrayOfLocalesProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkArrayOfLocalesTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkArrayOfLocalesProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkArrayOfLocalesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtwork");
        if(typeDataAccess != null) {
            personArtworkTypeAPI = new PersonArtworkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkTypeAPI = new PersonArtworkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtwork"));
        }
        addTypeAPI(personArtworkTypeAPI);
        factory = factoryOverrides.get("PersonArtwork");
        if(factory == null)
            factory = new PersonArtworkHollowFactory();
        if(cachedTypes.contains("PersonArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkProvider;
            personArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonsBioTranslatedTexts");
        if(typeDataAccess != null) {
            personsBioTranslatedTextsTypeAPI = new PersonsBioTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personsBioTranslatedTextsTypeAPI = new PersonsBioTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonsBioTranslatedTexts"));
        }
        addTypeAPI(personsBioTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("PersonsBioTranslatedTexts");
        if(factory == null)
            factory = new PersonsBioTranslatedTextsHollowFactory();
        if(cachedTypes.contains("PersonsBioTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personsBioTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personsBioTranslatedTextsProvider;
            personsBioTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, personsBioTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            personsBioTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, personsBioTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonsBioMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            personsBioMapOfTranslatedTextsTypeAPI = new PersonsBioMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            personsBioMapOfTranslatedTextsTypeAPI = new PersonsBioMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "PersonsBioMapOfTranslatedTexts"));
        }
        addTypeAPI(personsBioMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("PersonsBioMapOfTranslatedTexts");
        if(factory == null)
            factory = new PersonsBioMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("PersonsBioMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personsBioMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personsBioMapOfTranslatedTextsProvider;
            personsBioMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, personsBioMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            personsBioMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, personsBioMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonsBio");
        if(typeDataAccess != null) {
            personsBioTypeAPI = new PersonsBioTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personsBioTypeAPI = new PersonsBioTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonsBio"));
        }
        addTypeAPI(personsBioTypeAPI);
        factory = factoryOverrides.get("PersonsBio");
        if(factory == null)
            factory = new PersonsBioHollowFactory();
        if(cachedTypes.contains("PersonsBio")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personsBioProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personsBioProvider;
            personsBioProvider = new HollowObjectCacheProvider(typeDataAccess, personsBioTypeAPI, factory, previousCacheProvider);
        } else {
            personsBioProvider = new HollowObjectFactoryProvider(typeDataAccess, personsBioTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonsNameTranslatedTexts");
        if(typeDataAccess != null) {
            personsNameTranslatedTextsTypeAPI = new PersonsNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personsNameTranslatedTextsTypeAPI = new PersonsNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonsNameTranslatedTexts"));
        }
        addTypeAPI(personsNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("PersonsNameTranslatedTexts");
        if(factory == null)
            factory = new PersonsNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("PersonsNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personsNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personsNameTranslatedTextsProvider;
            personsNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, personsNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            personsNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, personsNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonsNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            personsNameMapOfTranslatedTextsTypeAPI = new PersonsNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            personsNameMapOfTranslatedTextsTypeAPI = new PersonsNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "PersonsNameMapOfTranslatedTexts"));
        }
        addTypeAPI(personsNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("PersonsNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new PersonsNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("PersonsNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personsNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personsNameMapOfTranslatedTextsProvider;
            personsNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, personsNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            personsNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, personsNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonsName");
        if(typeDataAccess != null) {
            personsNameTypeAPI = new PersonsNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personsNameTypeAPI = new PersonsNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonsName"));
        }
        addTypeAPI(personsNameTypeAPI);
        factory = factoryOverrides.get("PersonsName");
        if(factory == null)
            factory = new PersonsNameHollowFactory();
        if(cachedTypes.contains("PersonsName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personsNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personsNameProvider;
            personsNameProvider = new HollowObjectCacheProvider(typeDataAccess, personsNameTypeAPI, factory, previousCacheProvider);
        } else {
            personsNameProvider = new HollowObjectFactoryProvider(typeDataAccess, personsNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Persons");
        if(typeDataAccess != null) {
            personsTypeAPI = new PersonsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personsTypeAPI = new PersonsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Persons"));
        }
        addTypeAPI(personsTypeAPI);
        factory = factoryOverrides.get("Persons");
        if(factory == null)
            factory = new PersonsHollowFactory();
        if(cachedTypes.contains("Persons")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personsProvider;
            personsProvider = new HollowObjectCacheProvider(typeDataAccess, personsTypeAPI, factory, previousCacheProvider);
        } else {
            personsProvider = new HollowObjectFactoryProvider(typeDataAccess, personsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ProtectionTypes");
        if(typeDataAccess != null) {
            protectionTypesTypeAPI = new ProtectionTypesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            protectionTypesTypeAPI = new ProtectionTypesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ProtectionTypes"));
        }
        addTypeAPI(protectionTypesTypeAPI);
        factory = factoryOverrides.get("ProtectionTypes");
        if(factory == null)
            factory = new ProtectionTypesHollowFactory();
        if(cachedTypes.contains("ProtectionTypes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.protectionTypesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.protectionTypesProvider;
            protectionTypesProvider = new HollowObjectCacheProvider(typeDataAccess, protectionTypesTypeAPI, factory, previousCacheProvider);
        } else {
            protectionTypesProvider = new HollowObjectFactoryProvider(typeDataAccess, protectionTypesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RatingsDescriptionTranslatedTexts");
        if(typeDataAccess != null) {
            ratingsDescriptionTranslatedTextsTypeAPI = new RatingsDescriptionTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            ratingsDescriptionTranslatedTextsTypeAPI = new RatingsDescriptionTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RatingsDescriptionTranslatedTexts"));
        }
        addTypeAPI(ratingsDescriptionTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("RatingsDescriptionTranslatedTexts");
        if(factory == null)
            factory = new RatingsDescriptionTranslatedTextsHollowFactory();
        if(cachedTypes.contains("RatingsDescriptionTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsDescriptionTranslatedTextsProvider;
            ratingsDescriptionTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsDescriptionTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsDescriptionTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsDescriptionTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RatingsDescriptionMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            ratingsDescriptionMapOfTranslatedTextsTypeAPI = new RatingsDescriptionMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            ratingsDescriptionMapOfTranslatedTextsTypeAPI = new RatingsDescriptionMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RatingsDescriptionMapOfTranslatedTexts"));
        }
        addTypeAPI(ratingsDescriptionMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("RatingsDescriptionMapOfTranslatedTexts");
        if(factory == null)
            factory = new RatingsDescriptionMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("RatingsDescriptionMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsDescriptionMapOfTranslatedTextsProvider;
            ratingsDescriptionMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsDescriptionMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsDescriptionMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsDescriptionMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RatingsDescription");
        if(typeDataAccess != null) {
            ratingsDescriptionTypeAPI = new RatingsDescriptionTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            ratingsDescriptionTypeAPI = new RatingsDescriptionTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RatingsDescription"));
        }
        addTypeAPI(ratingsDescriptionTypeAPI);
        factory = factoryOverrides.get("RatingsDescription");
        if(factory == null)
            factory = new RatingsDescriptionHollowFactory();
        if(cachedTypes.contains("RatingsDescription")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsDescriptionProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsDescriptionProvider;
            ratingsDescriptionProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsDescriptionTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsDescriptionProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsDescriptionTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RatingsRatingCodeTranslatedTexts");
        if(typeDataAccess != null) {
            ratingsRatingCodeTranslatedTextsTypeAPI = new RatingsRatingCodeTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            ratingsRatingCodeTranslatedTextsTypeAPI = new RatingsRatingCodeTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RatingsRatingCodeTranslatedTexts"));
        }
        addTypeAPI(ratingsRatingCodeTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("RatingsRatingCodeTranslatedTexts");
        if(factory == null)
            factory = new RatingsRatingCodeTranslatedTextsHollowFactory();
        if(cachedTypes.contains("RatingsRatingCodeTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsRatingCodeTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsRatingCodeTranslatedTextsProvider;
            ratingsRatingCodeTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsRatingCodeTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsRatingCodeTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsRatingCodeTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RatingsRatingCodeMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            ratingsRatingCodeMapOfTranslatedTextsTypeAPI = new RatingsRatingCodeMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            ratingsRatingCodeMapOfTranslatedTextsTypeAPI = new RatingsRatingCodeMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RatingsRatingCodeMapOfTranslatedTexts"));
        }
        addTypeAPI(ratingsRatingCodeMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("RatingsRatingCodeMapOfTranslatedTexts");
        if(factory == null)
            factory = new RatingsRatingCodeMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("RatingsRatingCodeMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsRatingCodeMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsRatingCodeMapOfTranslatedTextsProvider;
            ratingsRatingCodeMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsRatingCodeMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsRatingCodeMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsRatingCodeMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RatingsRatingCode");
        if(typeDataAccess != null) {
            ratingsRatingCodeTypeAPI = new RatingsRatingCodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            ratingsRatingCodeTypeAPI = new RatingsRatingCodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RatingsRatingCode"));
        }
        addTypeAPI(ratingsRatingCodeTypeAPI);
        factory = factoryOverrides.get("RatingsRatingCode");
        if(factory == null)
            factory = new RatingsRatingCodeHollowFactory();
        if(cachedTypes.contains("RatingsRatingCode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsRatingCodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsRatingCodeProvider;
            ratingsRatingCodeProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsRatingCodeTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsRatingCodeProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsRatingCodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Ratings");
        if(typeDataAccess != null) {
            ratingsTypeAPI = new RatingsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            ratingsTypeAPI = new RatingsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Ratings"));
        }
        addTypeAPI(ratingsTypeAPI);
        factory = factoryOverrides.get("Ratings");
        if(factory == null)
            factory = new RatingsHollowFactory();
        if(cachedTypes.contains("Ratings")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsProvider;
            ratingsProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArtwork_newSourceFileIds");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI = new RolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI = new RolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsArtwork_newSourceFileIds"));
        }
        addTypeAPI(rolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArtwork_newSourceFileIds");
        if(factory == null)
            factory = new RolloutPhasesElementsArtwork_newSourceFileIdsHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArtwork_newSourceFileIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArtwork_newSourceFileIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArtwork_newSourceFileIdsProvider;
            rolloutPhasesElementsArtwork_newSourceFileIdsProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArtwork_newSourceFileIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArtwork_newArrayOfSourceFileIds");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI = new RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI = new RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhasesElementsArtwork_newArrayOfSourceFileIds"));
        }
        addTypeAPI(rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArtwork_newArrayOfSourceFileIds");
        if(factory == null)
            factory = new RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArtwork_newArrayOfSourceFileIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider;
            rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArtwork_new");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArtwork_newTypeAPI = new RolloutPhasesElementsArtwork_newTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArtwork_newTypeAPI = new RolloutPhasesElementsArtwork_newTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsArtwork_new"));
        }
        addTypeAPI(rolloutPhasesElementsArtwork_newTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArtwork_new");
        if(factory == null)
            factory = new RolloutPhasesElementsArtwork_newHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArtwork_new")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArtwork_newProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArtwork_newProvider;
            rolloutPhasesElementsArtwork_newProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArtwork_newTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArtwork_newProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArtwork_newTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsLocalized_metadata");
        if(typeDataAccess != null) {
            rolloutPhasesElementsLocalized_metadataTypeAPI = new RolloutPhasesElementsLocalized_metadataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsLocalized_metadataTypeAPI = new RolloutPhasesElementsLocalized_metadataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsLocalized_metadata"));
        }
        addTypeAPI(rolloutPhasesElementsLocalized_metadataTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsLocalized_metadata");
        if(factory == null)
            factory = new RolloutPhasesElementsLocalized_metadataHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsLocalized_metadata")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsLocalized_metadataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsLocalized_metadataProvider;
            rolloutPhasesElementsLocalized_metadataProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsLocalized_metadataTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsLocalized_metadataProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsLocalized_metadataTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsTrailersSupplementalInfo");
        if(typeDataAccess != null) {
            rolloutPhasesElementsTrailersSupplementalInfoTypeAPI = new RolloutPhasesElementsTrailersSupplementalInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsTrailersSupplementalInfoTypeAPI = new RolloutPhasesElementsTrailersSupplementalInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsTrailersSupplementalInfo"));
        }
        addTypeAPI(rolloutPhasesElementsTrailersSupplementalInfoTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsTrailersSupplementalInfo");
        if(factory == null)
            factory = new RolloutPhasesElementsTrailersSupplementalInfoHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsTrailersSupplementalInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsTrailersSupplementalInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsTrailersSupplementalInfoProvider;
            rolloutPhasesElementsTrailersSupplementalInfoProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsTrailersSupplementalInfoTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsTrailersSupplementalInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsTrailersSupplementalInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsTrailersMapOfSupplementalInfo");
        if(typeDataAccess != null) {
            rolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI = new RolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI = new RolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RolloutPhasesElementsTrailersMapOfSupplementalInfo"));
        }
        addTypeAPI(rolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsTrailersMapOfSupplementalInfo");
        if(factory == null)
            factory = new RolloutPhasesElementsTrailersMapOfSupplementalInfoHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsTrailersMapOfSupplementalInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider;
            rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsTrailers");
        if(typeDataAccess != null) {
            rolloutPhasesElementsTrailersTypeAPI = new RolloutPhasesElementsTrailersTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsTrailersTypeAPI = new RolloutPhasesElementsTrailersTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElementsTrailers"));
        }
        addTypeAPI(rolloutPhasesElementsTrailersTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsTrailers");
        if(factory == null)
            factory = new RolloutPhasesElementsTrailersHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsTrailers")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsTrailersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsTrailersProvider;
            rolloutPhasesElementsTrailersProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsTrailersTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsTrailersProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsTrailersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsArrayOfTrailers");
        if(typeDataAccess != null) {
            rolloutPhasesElementsArrayOfTrailersTypeAPI = new RolloutPhasesElementsArrayOfTrailersTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsArrayOfTrailersTypeAPI = new RolloutPhasesElementsArrayOfTrailersTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhasesElementsArrayOfTrailers"));
        }
        addTypeAPI(rolloutPhasesElementsArrayOfTrailersTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsArrayOfTrailers");
        if(factory == null)
            factory = new RolloutPhasesElementsArrayOfTrailersHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsArrayOfTrailers")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsArrayOfTrailersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsArrayOfTrailersProvider;
            rolloutPhasesElementsArrayOfTrailersProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsArrayOfTrailersTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsArrayOfTrailersProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsArrayOfTrailersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElements");
        if(typeDataAccess != null) {
            rolloutPhasesElementsTypeAPI = new RolloutPhasesElementsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsTypeAPI = new RolloutPhasesElementsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhasesElements"));
        }
        addTypeAPI(rolloutPhasesElementsTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElements");
        if(factory == null)
            factory = new RolloutPhasesElementsHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElements")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsProvider;
            rolloutPhasesElementsProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhases");
        if(typeDataAccess != null) {
            rolloutPhasesTypeAPI = new RolloutPhasesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesTypeAPI = new RolloutPhasesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhases"));
        }
        addTypeAPI(rolloutPhasesTypeAPI);
        factory = factoryOverrides.get("RolloutPhases");
        if(factory == null)
            factory = new RolloutPhasesHollowFactory();
        if(cachedTypes.contains("RolloutPhases")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesProvider;
            rolloutPhasesProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutArrayOfPhases");
        if(typeDataAccess != null) {
            rolloutArrayOfPhasesTypeAPI = new RolloutArrayOfPhasesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutArrayOfPhasesTypeAPI = new RolloutArrayOfPhasesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutArrayOfPhases"));
        }
        addTypeAPI(rolloutArrayOfPhasesTypeAPI);
        factory = factoryOverrides.get("RolloutArrayOfPhases");
        if(factory == null)
            factory = new RolloutArrayOfPhasesHollowFactory();
        if(cachedTypes.contains("RolloutArrayOfPhases")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutArrayOfPhasesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutArrayOfPhasesProvider;
            rolloutArrayOfPhasesProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutArrayOfPhasesTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutArrayOfPhasesProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutArrayOfPhasesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Rollout");
        if(typeDataAccess != null) {
            rolloutTypeAPI = new RolloutTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutTypeAPI = new RolloutTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Rollout"));
        }
        addTypeAPI(rolloutTypeAPI);
        factory = factoryOverrides.get("Rollout");
        if(factory == null)
            factory = new RolloutHollowFactory();
        if(cachedTypes.contains("Rollout")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutProvider;
            rolloutProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberTypesDisplayNameTranslatedTexts");
        if(typeDataAccess != null) {
            showMemberTypesDisplayNameTranslatedTextsTypeAPI = new ShowMemberTypesDisplayNameTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypesDisplayNameTranslatedTextsTypeAPI = new ShowMemberTypesDisplayNameTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowMemberTypesDisplayNameTranslatedTexts"));
        }
        addTypeAPI(showMemberTypesDisplayNameTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ShowMemberTypesDisplayNameTranslatedTexts");
        if(factory == null)
            factory = new ShowMemberTypesDisplayNameTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ShowMemberTypesDisplayNameTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypesDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypesDisplayNameTranslatedTextsProvider;
            showMemberTypesDisplayNameTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypesDisplayNameTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypesDisplayNameTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypesDisplayNameTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberTypesDisplayNameMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            showMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI = new ShowMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI = new ShowMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "ShowMemberTypesDisplayNameMapOfTranslatedTexts"));
        }
        addTypeAPI(showMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("ShowMemberTypesDisplayNameMapOfTranslatedTexts");
        if(factory == null)
            factory = new ShowMemberTypesDisplayNameMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("ShowMemberTypesDisplayNameMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypesDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypesDisplayNameMapOfTranslatedTextsProvider;
            showMemberTypesDisplayNameMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypesDisplayNameMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberTypesDisplayName");
        if(typeDataAccess != null) {
            showMemberTypesDisplayNameTypeAPI = new ShowMemberTypesDisplayNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypesDisplayNameTypeAPI = new ShowMemberTypesDisplayNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowMemberTypesDisplayName"));
        }
        addTypeAPI(showMemberTypesDisplayNameTypeAPI);
        factory = factoryOverrides.get("ShowMemberTypesDisplayName");
        if(factory == null)
            factory = new ShowMemberTypesDisplayNameHollowFactory();
        if(cachedTypes.contains("ShowMemberTypesDisplayName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypesDisplayNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypesDisplayNameProvider;
            showMemberTypesDisplayNameProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypesDisplayNameTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypesDisplayNameProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypesDisplayNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberTypes");
        if(typeDataAccess != null) {
            showMemberTypesTypeAPI = new ShowMemberTypesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypesTypeAPI = new ShowMemberTypesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowMemberTypes"));
        }
        addTypeAPI(showMemberTypesTypeAPI);
        factory = factoryOverrides.get("ShowMemberTypes");
        if(factory == null)
            factory = new ShowMemberTypesHollowFactory();
        if(cachedTypes.contains("ShowMemberTypes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypesProvider;
            showMemberTypesProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypesTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypesProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StorageGroupsCountries");
        if(typeDataAccess != null) {
            storageGroupsCountriesTypeAPI = new StorageGroupsCountriesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            storageGroupsCountriesTypeAPI = new StorageGroupsCountriesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StorageGroupsCountries"));
        }
        addTypeAPI(storageGroupsCountriesTypeAPI);
        factory = factoryOverrides.get("StorageGroupsCountries");
        if(factory == null)
            factory = new StorageGroupsCountriesHollowFactory();
        if(cachedTypes.contains("StorageGroupsCountries")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.storageGroupsCountriesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.storageGroupsCountriesProvider;
            storageGroupsCountriesProvider = new HollowObjectCacheProvider(typeDataAccess, storageGroupsCountriesTypeAPI, factory, previousCacheProvider);
        } else {
            storageGroupsCountriesProvider = new HollowObjectFactoryProvider(typeDataAccess, storageGroupsCountriesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StorageGroupsArrayOfCountries");
        if(typeDataAccess != null) {
            storageGroupsArrayOfCountriesTypeAPI = new StorageGroupsArrayOfCountriesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            storageGroupsArrayOfCountriesTypeAPI = new StorageGroupsArrayOfCountriesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "StorageGroupsArrayOfCountries"));
        }
        addTypeAPI(storageGroupsArrayOfCountriesTypeAPI);
        factory = factoryOverrides.get("StorageGroupsArrayOfCountries");
        if(factory == null)
            factory = new StorageGroupsArrayOfCountriesHollowFactory();
        if(cachedTypes.contains("StorageGroupsArrayOfCountries")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.storageGroupsArrayOfCountriesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.storageGroupsArrayOfCountriesProvider;
            storageGroupsArrayOfCountriesProvider = new HollowObjectCacheProvider(typeDataAccess, storageGroupsArrayOfCountriesTypeAPI, factory, previousCacheProvider);
        } else {
            storageGroupsArrayOfCountriesProvider = new HollowObjectFactoryProvider(typeDataAccess, storageGroupsArrayOfCountriesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StorageGroups");
        if(typeDataAccess != null) {
            storageGroupsTypeAPI = new StorageGroupsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            storageGroupsTypeAPI = new StorageGroupsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StorageGroups"));
        }
        addTypeAPI(storageGroupsTypeAPI);
        factory = factoryOverrides.get("StorageGroups");
        if(factory == null)
            factory = new StorageGroupsHollowFactory();
        if(cachedTypes.contains("StorageGroups")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.storageGroupsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.storageGroupsProvider;
            storageGroupsProvider = new HollowObjectCacheProvider(typeDataAccess, storageGroupsTypeAPI, factory, previousCacheProvider);
        } else {
            storageGroupsProvider = new HollowObjectFactoryProvider(typeDataAccess, storageGroupsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_SynopsesHooksTranslatedTexts");
        if(typeDataAccess != null) {
            stories_SynopsesHooksTranslatedTextsTypeAPI = new Stories_SynopsesHooksTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesHooksTranslatedTextsTypeAPI = new Stories_SynopsesHooksTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Stories_SynopsesHooksTranslatedTexts"));
        }
        addTypeAPI(stories_SynopsesHooksTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("Stories_SynopsesHooksTranslatedTexts");
        if(factory == null)
            factory = new Stories_SynopsesHooksTranslatedTextsHollowFactory();
        if(cachedTypes.contains("Stories_SynopsesHooksTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesHooksTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesHooksTranslatedTextsProvider;
            stories_SynopsesHooksTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesHooksTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesHooksTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesHooksTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_SynopsesHooksMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            stories_SynopsesHooksMapOfTranslatedTextsTypeAPI = new Stories_SynopsesHooksMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesHooksMapOfTranslatedTextsTypeAPI = new Stories_SynopsesHooksMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "Stories_SynopsesHooksMapOfTranslatedTexts"));
        }
        addTypeAPI(stories_SynopsesHooksMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("Stories_SynopsesHooksMapOfTranslatedTexts");
        if(factory == null)
            factory = new Stories_SynopsesHooksMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("Stories_SynopsesHooksMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesHooksMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesHooksMapOfTranslatedTextsProvider;
            stories_SynopsesHooksMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesHooksMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesHooksMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesHooksMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_SynopsesHooks");
        if(typeDataAccess != null) {
            stories_SynopsesHooksTypeAPI = new Stories_SynopsesHooksTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesHooksTypeAPI = new Stories_SynopsesHooksTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Stories_SynopsesHooks"));
        }
        addTypeAPI(stories_SynopsesHooksTypeAPI);
        factory = factoryOverrides.get("Stories_SynopsesHooks");
        if(factory == null)
            factory = new Stories_SynopsesHooksHollowFactory();
        if(cachedTypes.contains("Stories_SynopsesHooks")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesHooksProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesHooksProvider;
            stories_SynopsesHooksProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesHooksTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesHooksProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesHooksTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_SynopsesArrayOfHooks");
        if(typeDataAccess != null) {
            stories_SynopsesArrayOfHooksTypeAPI = new Stories_SynopsesArrayOfHooksTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesArrayOfHooksTypeAPI = new Stories_SynopsesArrayOfHooksTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "Stories_SynopsesArrayOfHooks"));
        }
        addTypeAPI(stories_SynopsesArrayOfHooksTypeAPI);
        factory = factoryOverrides.get("Stories_SynopsesArrayOfHooks");
        if(factory == null)
            factory = new Stories_SynopsesArrayOfHooksHollowFactory();
        if(cachedTypes.contains("Stories_SynopsesArrayOfHooks")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesArrayOfHooksProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesArrayOfHooksProvider;
            stories_SynopsesArrayOfHooksProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesArrayOfHooksTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesArrayOfHooksProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesArrayOfHooksTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_SynopsesNarrativeTextTranslatedTexts");
        if(typeDataAccess != null) {
            stories_SynopsesNarrativeTextTranslatedTextsTypeAPI = new Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesNarrativeTextTranslatedTextsTypeAPI = new Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Stories_SynopsesNarrativeTextTranslatedTexts"));
        }
        addTypeAPI(stories_SynopsesNarrativeTextTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("Stories_SynopsesNarrativeTextTranslatedTexts");
        if(factory == null)
            factory = new Stories_SynopsesNarrativeTextTranslatedTextsHollowFactory();
        if(cachedTypes.contains("Stories_SynopsesNarrativeTextTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesNarrativeTextTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesNarrativeTextTranslatedTextsProvider;
            stories_SynopsesNarrativeTextTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesNarrativeTextTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesNarrativeTextTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesNarrativeTextTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_SynopsesNarrativeTextMapOfTranslatedTexts");
        if(typeDataAccess != null) {
            stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI = new Stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI = new Stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "Stories_SynopsesNarrativeTextMapOfTranslatedTexts"));
        }
        addTypeAPI(stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI);
        factory = factoryOverrides.get("Stories_SynopsesNarrativeTextMapOfTranslatedTexts");
        if(factory == null)
            factory = new Stories_SynopsesNarrativeTextMapOfTranslatedTextsHollowFactory();
        if(cachedTypes.contains("Stories_SynopsesNarrativeTextMapOfTranslatedTexts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider;
            stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_SynopsesNarrativeText");
        if(typeDataAccess != null) {
            stories_SynopsesNarrativeTextTypeAPI = new Stories_SynopsesNarrativeTextTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesNarrativeTextTypeAPI = new Stories_SynopsesNarrativeTextTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Stories_SynopsesNarrativeText"));
        }
        addTypeAPI(stories_SynopsesNarrativeTextTypeAPI);
        factory = factoryOverrides.get("Stories_SynopsesNarrativeText");
        if(factory == null)
            factory = new Stories_SynopsesNarrativeTextHollowFactory();
        if(cachedTypes.contains("Stories_SynopsesNarrativeText")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesNarrativeTextProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesNarrativeTextProvider;
            stories_SynopsesNarrativeTextProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesNarrativeTextTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesNarrativeTextProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesNarrativeTextTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Stories_Synopses");
        if(typeDataAccess != null) {
            stories_SynopsesTypeAPI = new Stories_SynopsesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stories_SynopsesTypeAPI = new Stories_SynopsesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Stories_Synopses"));
        }
        addTypeAPI(stories_SynopsesTypeAPI);
        factory = factoryOverrides.get("Stories_Synopses");
        if(factory == null)
            factory = new Stories_SynopsesHollowFactory();
        if(cachedTypes.contains("Stories_Synopses")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stories_SynopsesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stories_SynopsesProvider;
            stories_SynopsesProvider = new HollowObjectCacheProvider(typeDataAccess, stories_SynopsesTypeAPI, factory, previousCacheProvider);
        } else {
            stories_SynopsesProvider = new HollowObjectFactoryProvider(typeDataAccess, stories_SynopsesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamProfileGroups");
        if(typeDataAccess != null) {
            streamProfileGroupsTypeAPI = new StreamProfileGroupsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamProfileGroupsTypeAPI = new StreamProfileGroupsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamProfileGroups"));
        }
        addTypeAPI(streamProfileGroupsTypeAPI);
        factory = factoryOverrides.get("StreamProfileGroups");
        if(factory == null)
            factory = new StreamProfileGroupsHollowFactory();
        if(cachedTypes.contains("StreamProfileGroups")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamProfileGroupsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamProfileGroupsProvider;
            streamProfileGroupsProvider = new HollowObjectCacheProvider(typeDataAccess, streamProfileGroupsTypeAPI, factory, previousCacheProvider);
        } else {
            streamProfileGroupsProvider = new HollowObjectFactoryProvider(typeDataAccess, streamProfileGroupsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamProfiles");
        if(typeDataAccess != null) {
            streamProfilesTypeAPI = new StreamProfilesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamProfilesTypeAPI = new StreamProfilesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamProfiles"));
        }
        addTypeAPI(streamProfilesTypeAPI);
        factory = factoryOverrides.get("StreamProfiles");
        if(factory == null)
            factory = new StreamProfilesHollowFactory();
        if(cachedTypes.contains("StreamProfiles")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamProfilesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamProfilesProvider;
            streamProfilesProvider = new HollowObjectCacheProvider(typeDataAccess, streamProfilesTypeAPI, factory, previousCacheProvider);
        } else {
            streamProfilesProvider = new HollowObjectFactoryProvider(typeDataAccess, streamProfilesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TerritoryCountriesCountryCodes");
        if(typeDataAccess != null) {
            territoryCountriesCountryCodesTypeAPI = new TerritoryCountriesCountryCodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            territoryCountriesCountryCodesTypeAPI = new TerritoryCountriesCountryCodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TerritoryCountriesCountryCodes"));
        }
        addTypeAPI(territoryCountriesCountryCodesTypeAPI);
        factory = factoryOverrides.get("TerritoryCountriesCountryCodes");
        if(factory == null)
            factory = new TerritoryCountriesCountryCodesHollowFactory();
        if(cachedTypes.contains("TerritoryCountriesCountryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.territoryCountriesCountryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.territoryCountriesCountryCodesProvider;
            territoryCountriesCountryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, territoryCountriesCountryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            territoryCountriesCountryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, territoryCountriesCountryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TerritoryCountriesArrayOfCountryCodes");
        if(typeDataAccess != null) {
            territoryCountriesArrayOfCountryCodesTypeAPI = new TerritoryCountriesArrayOfCountryCodesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            territoryCountriesArrayOfCountryCodesTypeAPI = new TerritoryCountriesArrayOfCountryCodesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "TerritoryCountriesArrayOfCountryCodes"));
        }
        addTypeAPI(territoryCountriesArrayOfCountryCodesTypeAPI);
        factory = factoryOverrides.get("TerritoryCountriesArrayOfCountryCodes");
        if(factory == null)
            factory = new TerritoryCountriesArrayOfCountryCodesHollowFactory();
        if(cachedTypes.contains("TerritoryCountriesArrayOfCountryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.territoryCountriesArrayOfCountryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.territoryCountriesArrayOfCountryCodesProvider;
            territoryCountriesArrayOfCountryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, territoryCountriesArrayOfCountryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            territoryCountriesArrayOfCountryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, territoryCountriesArrayOfCountryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TerritoryCountries");
        if(typeDataAccess != null) {
            territoryCountriesTypeAPI = new TerritoryCountriesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            territoryCountriesTypeAPI = new TerritoryCountriesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TerritoryCountries"));
        }
        addTypeAPI(territoryCountriesTypeAPI);
        factory = factoryOverrides.get("TerritoryCountries");
        if(factory == null)
            factory = new TerritoryCountriesHollowFactory();
        if(cachedTypes.contains("TerritoryCountries")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.territoryCountriesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.territoryCountriesProvider;
            territoryCountriesProvider = new HollowObjectCacheProvider(typeDataAccess, territoryCountriesTypeAPI, factory, previousCacheProvider);
        } else {
            territoryCountriesProvider = new HollowObjectFactoryProvider(typeDataAccess, territoryCountriesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TopNAttributes");
        if(typeDataAccess != null) {
            topNAttributesTypeAPI = new TopNAttributesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            topNAttributesTypeAPI = new TopNAttributesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TopNAttributes"));
        }
        addTypeAPI(topNAttributesTypeAPI);
        factory = factoryOverrides.get("TopNAttributes");
        if(factory == null)
            factory = new TopNAttributesHollowFactory();
        if(cachedTypes.contains("TopNAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNAttributesProvider;
            topNAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, topNAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            topNAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, topNAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TopNArrayOfAttributes");
        if(typeDataAccess != null) {
            topNArrayOfAttributesTypeAPI = new TopNArrayOfAttributesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            topNArrayOfAttributesTypeAPI = new TopNArrayOfAttributesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "TopNArrayOfAttributes"));
        }
        addTypeAPI(topNArrayOfAttributesTypeAPI);
        factory = factoryOverrides.get("TopNArrayOfAttributes");
        if(factory == null)
            factory = new TopNArrayOfAttributesHollowFactory();
        if(cachedTypes.contains("TopNArrayOfAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNArrayOfAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNArrayOfAttributesProvider;
            topNArrayOfAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, topNArrayOfAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            topNArrayOfAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, topNArrayOfAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TopN");
        if(typeDataAccess != null) {
            topNTypeAPI = new TopNTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            topNTypeAPI = new TopNTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TopN"));
        }
        addTypeAPI(topNTypeAPI);
        factory = factoryOverrides.get("TopN");
        if(factory == null)
            factory = new TopNHollowFactory();
        if(cachedTypes.contains("TopN")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNProvider;
            topNProvider = new HollowObjectCacheProvider(typeDataAccess, topNTypeAPI, factory, previousCacheProvider);
        } else {
            topNProvider = new HollowObjectFactoryProvider(typeDataAccess, topNTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TrailerTrailersThemes");
        if(typeDataAccess != null) {
            trailerTrailersThemesTypeAPI = new TrailerTrailersThemesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            trailerTrailersThemesTypeAPI = new TrailerTrailersThemesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TrailerTrailersThemes"));
        }
        addTypeAPI(trailerTrailersThemesTypeAPI);
        factory = factoryOverrides.get("TrailerTrailersThemes");
        if(factory == null)
            factory = new TrailerTrailersThemesHollowFactory();
        if(cachedTypes.contains("TrailerTrailersThemes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailerTrailersThemesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailerTrailersThemesProvider;
            trailerTrailersThemesProvider = new HollowObjectCacheProvider(typeDataAccess, trailerTrailersThemesTypeAPI, factory, previousCacheProvider);
        } else {
            trailerTrailersThemesProvider = new HollowObjectFactoryProvider(typeDataAccess, trailerTrailersThemesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TrailerTrailersArrayOfThemes");
        if(typeDataAccess != null) {
            trailerTrailersArrayOfThemesTypeAPI = new TrailerTrailersArrayOfThemesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            trailerTrailersArrayOfThemesTypeAPI = new TrailerTrailersArrayOfThemesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "TrailerTrailersArrayOfThemes"));
        }
        addTypeAPI(trailerTrailersArrayOfThemesTypeAPI);
        factory = factoryOverrides.get("TrailerTrailersArrayOfThemes");
        if(factory == null)
            factory = new TrailerTrailersArrayOfThemesHollowFactory();
        if(cachedTypes.contains("TrailerTrailersArrayOfThemes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailerTrailersArrayOfThemesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailerTrailersArrayOfThemesProvider;
            trailerTrailersArrayOfThemesProvider = new HollowObjectCacheProvider(typeDataAccess, trailerTrailersArrayOfThemesTypeAPI, factory, previousCacheProvider);
        } else {
            trailerTrailersArrayOfThemesProvider = new HollowObjectFactoryProvider(typeDataAccess, trailerTrailersArrayOfThemesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TrailerTrailers");
        if(typeDataAccess != null) {
            trailerTrailersTypeAPI = new TrailerTrailersTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            trailerTrailersTypeAPI = new TrailerTrailersTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TrailerTrailers"));
        }
        addTypeAPI(trailerTrailersTypeAPI);
        factory = factoryOverrides.get("TrailerTrailers");
        if(factory == null)
            factory = new TrailerTrailersHollowFactory();
        if(cachedTypes.contains("TrailerTrailers")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailerTrailersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailerTrailersProvider;
            trailerTrailersProvider = new HollowObjectCacheProvider(typeDataAccess, trailerTrailersTypeAPI, factory, previousCacheProvider);
        } else {
            trailerTrailersProvider = new HollowObjectFactoryProvider(typeDataAccess, trailerTrailersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TrailerArrayOfTrailers");
        if(typeDataAccess != null) {
            trailerArrayOfTrailersTypeAPI = new TrailerArrayOfTrailersTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            trailerArrayOfTrailersTypeAPI = new TrailerArrayOfTrailersTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "TrailerArrayOfTrailers"));
        }
        addTypeAPI(trailerArrayOfTrailersTypeAPI);
        factory = factoryOverrides.get("TrailerArrayOfTrailers");
        if(factory == null)
            factory = new TrailerArrayOfTrailersHollowFactory();
        if(cachedTypes.contains("TrailerArrayOfTrailers")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailerArrayOfTrailersProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailerArrayOfTrailersProvider;
            trailerArrayOfTrailersProvider = new HollowObjectCacheProvider(typeDataAccess, trailerArrayOfTrailersTypeAPI, factory, previousCacheProvider);
        } else {
            trailerArrayOfTrailersProvider = new HollowObjectFactoryProvider(typeDataAccess, trailerArrayOfTrailersTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Trailer");
        if(typeDataAccess != null) {
            trailerTypeAPI = new TrailerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            trailerTypeAPI = new TrailerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Trailer"));
        }
        addTypeAPI(trailerTypeAPI);
        factory = factoryOverrides.get("Trailer");
        if(factory == null)
            factory = new TrailerHollowFactory();
        if(cachedTypes.contains("Trailer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailerProvider;
            trailerProvider = new HollowObjectCacheProvider(typeDataAccess, trailerTypeAPI, factory, previousCacheProvider);
        } else {
            trailerProvider = new HollowObjectFactoryProvider(typeDataAccess, trailerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VMSAward");
        if(typeDataAccess != null) {
            vMSAwardTypeAPI = new VMSAwardTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            vMSAwardTypeAPI = new VMSAwardTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VMSAward"));
        }
        addTypeAPI(vMSAwardTypeAPI);
        factory = factoryOverrides.get("VMSAward");
        if(factory == null)
            factory = new VMSAwardHollowFactory();
        if(cachedTypes.contains("VMSAward")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.vMSAwardProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.vMSAwardProvider;
            vMSAwardProvider = new HollowObjectCacheProvider(typeDataAccess, vMSAwardTypeAPI, factory, previousCacheProvider);
        } else {
            vMSAwardProvider = new HollowObjectFactoryProvider(typeDataAccess, vMSAwardTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkAttributes");
        if(typeDataAccess != null) {
            videoArtWorkAttributesTypeAPI = new VideoArtWorkAttributesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkAttributesTypeAPI = new VideoArtWorkAttributesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkAttributes"));
        }
        addTypeAPI(videoArtWorkAttributesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkAttributes");
        if(factory == null)
            factory = new VideoArtWorkAttributesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkAttributesProvider;
            videoArtWorkAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkArrayOfAttributes");
        if(typeDataAccess != null) {
            videoArtWorkArrayOfAttributesTypeAPI = new VideoArtWorkArrayOfAttributesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkArrayOfAttributesTypeAPI = new VideoArtWorkArrayOfAttributesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkArrayOfAttributes"));
        }
        addTypeAPI(videoArtWorkArrayOfAttributesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkArrayOfAttributes");
        if(factory == null)
            factory = new VideoArtWorkArrayOfAttributesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkArrayOfAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkArrayOfAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkArrayOfAttributesProvider;
            videoArtWorkArrayOfAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkArrayOfAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkArrayOfAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkArrayOfAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkExtensions");
        if(typeDataAccess != null) {
            videoArtWorkExtensionsTypeAPI = new VideoArtWorkExtensionsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkExtensionsTypeAPI = new VideoArtWorkExtensionsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkExtensions"));
        }
        addTypeAPI(videoArtWorkExtensionsTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkExtensions");
        if(factory == null)
            factory = new VideoArtWorkExtensionsHollowFactory();
        if(cachedTypes.contains("VideoArtWorkExtensions")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkExtensionsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkExtensionsProvider;
            videoArtWorkExtensionsProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkExtensionsTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkExtensionsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkExtensionsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkArrayOfExtensions");
        if(typeDataAccess != null) {
            videoArtWorkArrayOfExtensionsTypeAPI = new VideoArtWorkArrayOfExtensionsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkArrayOfExtensionsTypeAPI = new VideoArtWorkArrayOfExtensionsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkArrayOfExtensions"));
        }
        addTypeAPI(videoArtWorkArrayOfExtensionsTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkArrayOfExtensions");
        if(factory == null)
            factory = new VideoArtWorkArrayOfExtensionsHollowFactory();
        if(cachedTypes.contains("VideoArtWorkArrayOfExtensions")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkArrayOfExtensionsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkArrayOfExtensionsProvider;
            videoArtWorkArrayOfExtensionsProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkArrayOfExtensionsTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkArrayOfExtensionsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkArrayOfExtensionsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkLocalesTerritoryCodes");
        if(typeDataAccess != null) {
            videoArtWorkLocalesTerritoryCodesTypeAPI = new VideoArtWorkLocalesTerritoryCodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkLocalesTerritoryCodesTypeAPI = new VideoArtWorkLocalesTerritoryCodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkLocalesTerritoryCodes"));
        }
        addTypeAPI(videoArtWorkLocalesTerritoryCodesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkLocalesTerritoryCodes");
        if(factory == null)
            factory = new VideoArtWorkLocalesTerritoryCodesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkLocalesTerritoryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkLocalesTerritoryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkLocalesTerritoryCodesProvider;
            videoArtWorkLocalesTerritoryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkLocalesTerritoryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkLocalesTerritoryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkLocalesTerritoryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkLocalesArrayOfTerritoryCodes");
        if(typeDataAccess != null) {
            videoArtWorkLocalesArrayOfTerritoryCodesTypeAPI = new VideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkLocalesArrayOfTerritoryCodesTypeAPI = new VideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkLocalesArrayOfTerritoryCodes"));
        }
        addTypeAPI(videoArtWorkLocalesArrayOfTerritoryCodesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkLocalesArrayOfTerritoryCodes");
        if(factory == null)
            factory = new VideoArtWorkLocalesArrayOfTerritoryCodesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkLocalesArrayOfTerritoryCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkLocalesArrayOfTerritoryCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkLocalesArrayOfTerritoryCodesProvider;
            videoArtWorkLocalesArrayOfTerritoryCodesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkLocalesArrayOfTerritoryCodesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkLocalesArrayOfTerritoryCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkLocalesArrayOfTerritoryCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkLocales");
        if(typeDataAccess != null) {
            videoArtWorkLocalesTypeAPI = new VideoArtWorkLocalesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkLocalesTypeAPI = new VideoArtWorkLocalesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkLocales"));
        }
        addTypeAPI(videoArtWorkLocalesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkLocales");
        if(factory == null)
            factory = new VideoArtWorkLocalesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkLocales")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkLocalesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkLocalesProvider;
            videoArtWorkLocalesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkLocalesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkLocalesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkLocalesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkArrayOfLocales");
        if(typeDataAccess != null) {
            videoArtWorkArrayOfLocalesTypeAPI = new VideoArtWorkArrayOfLocalesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkArrayOfLocalesTypeAPI = new VideoArtWorkArrayOfLocalesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkArrayOfLocales"));
        }
        addTypeAPI(videoArtWorkArrayOfLocalesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkArrayOfLocales");
        if(factory == null)
            factory = new VideoArtWorkArrayOfLocalesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkArrayOfLocales")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkArrayOfLocalesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkArrayOfLocalesProvider;
            videoArtWorkArrayOfLocalesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkArrayOfLocalesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkArrayOfLocalesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkArrayOfLocalesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkRecipes");
        if(typeDataAccess != null) {
            videoArtWorkRecipesTypeAPI = new VideoArtWorkRecipesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkRecipesTypeAPI = new VideoArtWorkRecipesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkRecipes"));
        }
        addTypeAPI(videoArtWorkRecipesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkRecipes");
        if(factory == null)
            factory = new VideoArtWorkRecipesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkRecipes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkRecipesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkRecipesProvider;
            videoArtWorkRecipesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkRecipesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkRecipesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkRecipesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkArrayOfRecipes");
        if(typeDataAccess != null) {
            videoArtWorkArrayOfRecipesTypeAPI = new VideoArtWorkArrayOfRecipesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkArrayOfRecipesTypeAPI = new VideoArtWorkArrayOfRecipesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkArrayOfRecipes"));
        }
        addTypeAPI(videoArtWorkArrayOfRecipesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkArrayOfRecipes");
        if(factory == null)
            factory = new VideoArtWorkArrayOfRecipesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkArrayOfRecipes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkArrayOfRecipesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkArrayOfRecipesProvider;
            videoArtWorkArrayOfRecipesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkArrayOfRecipesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkArrayOfRecipesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkArrayOfRecipesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesAWARD_CAMPAIGNS");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI = new VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI = new VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesAWARD_CAMPAIGNS"));
        }
        addTypeAPI(videoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesAWARD_CAMPAIGNS");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesAWARD_CAMPAIGNS")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider;
            videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNS");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI = new VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI = new VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNS"));
        }
        addTypeAPI(videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNS");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNS")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider;
            videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesIDENTIFIERS");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesIDENTIFIERSTypeAPI = new VideoArtWorkSourceAttributesIDENTIFIERSTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesIDENTIFIERSTypeAPI = new VideoArtWorkSourceAttributesIDENTIFIERSTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesIDENTIFIERS"));
        }
        addTypeAPI(videoArtWorkSourceAttributesIDENTIFIERSTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesIDENTIFIERS");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesIDENTIFIERSHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesIDENTIFIERS")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesIDENTIFIERSProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesIDENTIFIERSProvider;
            videoArtWorkSourceAttributesIDENTIFIERSProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesIDENTIFIERSTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesIDENTIFIERSProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesIDENTIFIERSTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfIDENTIFIERS");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI = new VideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI = new VideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesArrayOfIDENTIFIERS"));
        }
        addTypeAPI(videoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesArrayOfIDENTIFIERS");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesArrayOfIDENTIFIERS")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider;
            videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesPERSON_IDS");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesPERSON_IDSTypeAPI = new VideoArtWorkSourceAttributesPERSON_IDSTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesPERSON_IDSTypeAPI = new VideoArtWorkSourceAttributesPERSON_IDSTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesPERSON_IDS"));
        }
        addTypeAPI(videoArtWorkSourceAttributesPERSON_IDSTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesPERSON_IDS");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesPERSON_IDSHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesPERSON_IDS")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesPERSON_IDSProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesPERSON_IDSProvider;
            videoArtWorkSourceAttributesPERSON_IDSProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesPERSON_IDSTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesPERSON_IDSProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesPERSON_IDSTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfPERSON_IDS");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI = new VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI = new VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesArrayOfPERSON_IDS"));
        }
        addTypeAPI(videoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesArrayOfPERSON_IDS");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesArrayOfPERSON_IDS")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider;
            videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesThemes");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesThemesTypeAPI = new VideoArtWorkSourceAttributesThemesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesThemesTypeAPI = new VideoArtWorkSourceAttributesThemesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesThemes"));
        }
        addTypeAPI(videoArtWorkSourceAttributesThemesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesThemes");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesThemesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesThemes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesThemesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesThemesProvider;
            videoArtWorkSourceAttributesThemesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesThemesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesThemesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesThemesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfThemes");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesArrayOfThemesTypeAPI = new VideoArtWorkSourceAttributesArrayOfThemesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesArrayOfThemesTypeAPI = new VideoArtWorkSourceAttributesArrayOfThemesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributesArrayOfThemes"));
        }
        addTypeAPI(videoArtWorkSourceAttributesArrayOfThemesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributesArrayOfThemes");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesArrayOfThemesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributesArrayOfThemes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesArrayOfThemesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesArrayOfThemesProvider;
            videoArtWorkSourceAttributesArrayOfThemesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfThemesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesArrayOfThemesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesArrayOfThemesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkSourceAttributes");
        if(typeDataAccess != null) {
            videoArtWorkSourceAttributesTypeAPI = new VideoArtWorkSourceAttributesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkSourceAttributesTypeAPI = new VideoArtWorkSourceAttributesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWorkSourceAttributes"));
        }
        addTypeAPI(videoArtWorkSourceAttributesTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkSourceAttributes");
        if(factory == null)
            factory = new VideoArtWorkSourceAttributesHollowFactory();
        if(cachedTypes.contains("VideoArtWorkSourceAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkSourceAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkSourceAttributesProvider;
            videoArtWorkSourceAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkSourceAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkSourceAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkSourceAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWork");
        if(typeDataAccess != null) {
            videoArtWorkTypeAPI = new VideoArtWorkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkTypeAPI = new VideoArtWorkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtWork"));
        }
        addTypeAPI(videoArtWorkTypeAPI);
        factory = factoryOverrides.get("VideoArtWork");
        if(factory == null)
            factory = new VideoArtWorkHollowFactory();
        if(cachedTypes.contains("VideoArtWork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkProvider;
            videoArtWorkProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAwardAward");
        if(typeDataAccess != null) {
            videoAwardAwardTypeAPI = new VideoAwardAwardTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoAwardAwardTypeAPI = new VideoAwardAwardTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoAwardAward"));
        }
        addTypeAPI(videoAwardAwardTypeAPI);
        factory = factoryOverrides.get("VideoAwardAward");
        if(factory == null)
            factory = new VideoAwardAwardHollowFactory();
        if(cachedTypes.contains("VideoAwardAward")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardAwardProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardAwardProvider;
            videoAwardAwardProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardAwardTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardAwardProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardAwardTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAwardArrayOfAward");
        if(typeDataAccess != null) {
            videoAwardArrayOfAwardTypeAPI = new VideoAwardArrayOfAwardTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoAwardArrayOfAwardTypeAPI = new VideoAwardArrayOfAwardTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoAwardArrayOfAward"));
        }
        addTypeAPI(videoAwardArrayOfAwardTypeAPI);
        factory = factoryOverrides.get("VideoAwardArrayOfAward");
        if(factory == null)
            factory = new VideoAwardArrayOfAwardHollowFactory();
        if(cachedTypes.contains("VideoAwardArrayOfAward")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardArrayOfAwardProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardArrayOfAwardProvider;
            videoAwardArrayOfAwardProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardArrayOfAwardTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardArrayOfAwardProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardArrayOfAwardTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAward");
        if(typeDataAccess != null) {
            videoAwardTypeAPI = new VideoAwardTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoAwardTypeAPI = new VideoAwardTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoAward"));
        }
        addTypeAPI(videoAwardTypeAPI);
        factory = factoryOverrides.get("VideoAward");
        if(factory == null)
            factory = new VideoAwardHollowFactory();
        if(cachedTypes.contains("VideoAward")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardProvider;
            videoAwardProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDateWindow");
        if(typeDataAccess != null) {
            videoDateWindowTypeAPI = new VideoDateWindowTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDateWindowTypeAPI = new VideoDateWindowTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDateWindow"));
        }
        addTypeAPI(videoDateWindowTypeAPI);
        factory = factoryOverrides.get("VideoDateWindow");
        if(factory == null)
            factory = new VideoDateWindowHollowFactory();
        if(cachedTypes.contains("VideoDateWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDateWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDateWindowProvider;
            videoDateWindowProvider = new HollowObjectCacheProvider(typeDataAccess, videoDateWindowTypeAPI, factory, previousCacheProvider);
        } else {
            videoDateWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDateWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDateArrayOfWindow");
        if(typeDataAccess != null) {
            videoDateArrayOfWindowTypeAPI = new VideoDateArrayOfWindowTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoDateArrayOfWindowTypeAPI = new VideoDateArrayOfWindowTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoDateArrayOfWindow"));
        }
        addTypeAPI(videoDateArrayOfWindowTypeAPI);
        factory = factoryOverrides.get("VideoDateArrayOfWindow");
        if(factory == null)
            factory = new VideoDateArrayOfWindowHollowFactory();
        if(cachedTypes.contains("VideoDateArrayOfWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDateArrayOfWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDateArrayOfWindowProvider;
            videoDateArrayOfWindowProvider = new HollowObjectCacheProvider(typeDataAccess, videoDateArrayOfWindowTypeAPI, factory, previousCacheProvider);
        } else {
            videoDateArrayOfWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDateArrayOfWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDate");
        if(typeDataAccess != null) {
            videoDateTypeAPI = new VideoDateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDateTypeAPI = new VideoDateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDate"));
        }
        addTypeAPI(videoDateTypeAPI);
        factory = factoryOverrides.get("VideoDate");
        if(factory == null)
            factory = new VideoDateHollowFactory();
        if(cachedTypes.contains("VideoDate")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDateProvider;
            videoDateProvider = new HollowObjectCacheProvider(typeDataAccess, videoDateTypeAPI, factory, previousCacheProvider);
        } else {
            videoDateProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDateTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDisplaySetSetsChildrenChildren");
        if(typeDataAccess != null) {
            videoDisplaySetSetsChildrenChildrenTypeAPI = new VideoDisplaySetSetsChildrenChildrenTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDisplaySetSetsChildrenChildrenTypeAPI = new VideoDisplaySetSetsChildrenChildrenTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDisplaySetSetsChildrenChildren"));
        }
        addTypeAPI(videoDisplaySetSetsChildrenChildrenTypeAPI);
        factory = factoryOverrides.get("VideoDisplaySetSetsChildrenChildren");
        if(factory == null)
            factory = new VideoDisplaySetSetsChildrenChildrenHollowFactory();
        if(cachedTypes.contains("VideoDisplaySetSetsChildrenChildren")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDisplaySetSetsChildrenChildrenProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDisplaySetSetsChildrenChildrenProvider;
            videoDisplaySetSetsChildrenChildrenProvider = new HollowObjectCacheProvider(typeDataAccess, videoDisplaySetSetsChildrenChildrenTypeAPI, factory, previousCacheProvider);
        } else {
            videoDisplaySetSetsChildrenChildrenProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDisplaySetSetsChildrenChildrenTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDisplaySetSetsChildrenArrayOfChildren");
        if(typeDataAccess != null) {
            videoDisplaySetSetsChildrenArrayOfChildrenTypeAPI = new VideoDisplaySetSetsChildrenArrayOfChildrenTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoDisplaySetSetsChildrenArrayOfChildrenTypeAPI = new VideoDisplaySetSetsChildrenArrayOfChildrenTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoDisplaySetSetsChildrenArrayOfChildren"));
        }
        addTypeAPI(videoDisplaySetSetsChildrenArrayOfChildrenTypeAPI);
        factory = factoryOverrides.get("VideoDisplaySetSetsChildrenArrayOfChildren");
        if(factory == null)
            factory = new VideoDisplaySetSetsChildrenArrayOfChildrenHollowFactory();
        if(cachedTypes.contains("VideoDisplaySetSetsChildrenArrayOfChildren")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDisplaySetSetsChildrenArrayOfChildrenProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDisplaySetSetsChildrenArrayOfChildrenProvider;
            videoDisplaySetSetsChildrenArrayOfChildrenProvider = new HollowObjectCacheProvider(typeDataAccess, videoDisplaySetSetsChildrenArrayOfChildrenTypeAPI, factory, previousCacheProvider);
        } else {
            videoDisplaySetSetsChildrenArrayOfChildrenProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDisplaySetSetsChildrenArrayOfChildrenTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDisplaySetSetsChildren");
        if(typeDataAccess != null) {
            videoDisplaySetSetsChildrenTypeAPI = new VideoDisplaySetSetsChildrenTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDisplaySetSetsChildrenTypeAPI = new VideoDisplaySetSetsChildrenTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDisplaySetSetsChildren"));
        }
        addTypeAPI(videoDisplaySetSetsChildrenTypeAPI);
        factory = factoryOverrides.get("VideoDisplaySetSetsChildren");
        if(factory == null)
            factory = new VideoDisplaySetSetsChildrenHollowFactory();
        if(cachedTypes.contains("VideoDisplaySetSetsChildren")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDisplaySetSetsChildrenProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDisplaySetSetsChildrenProvider;
            videoDisplaySetSetsChildrenProvider = new HollowObjectCacheProvider(typeDataAccess, videoDisplaySetSetsChildrenTypeAPI, factory, previousCacheProvider);
        } else {
            videoDisplaySetSetsChildrenProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDisplaySetSetsChildrenTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDisplaySetSetsArrayOfChildren");
        if(typeDataAccess != null) {
            videoDisplaySetSetsArrayOfChildrenTypeAPI = new VideoDisplaySetSetsArrayOfChildrenTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoDisplaySetSetsArrayOfChildrenTypeAPI = new VideoDisplaySetSetsArrayOfChildrenTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoDisplaySetSetsArrayOfChildren"));
        }
        addTypeAPI(videoDisplaySetSetsArrayOfChildrenTypeAPI);
        factory = factoryOverrides.get("VideoDisplaySetSetsArrayOfChildren");
        if(factory == null)
            factory = new VideoDisplaySetSetsArrayOfChildrenHollowFactory();
        if(cachedTypes.contains("VideoDisplaySetSetsArrayOfChildren")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDisplaySetSetsArrayOfChildrenProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDisplaySetSetsArrayOfChildrenProvider;
            videoDisplaySetSetsArrayOfChildrenProvider = new HollowObjectCacheProvider(typeDataAccess, videoDisplaySetSetsArrayOfChildrenTypeAPI, factory, previousCacheProvider);
        } else {
            videoDisplaySetSetsArrayOfChildrenProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDisplaySetSetsArrayOfChildrenTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDisplaySetSets");
        if(typeDataAccess != null) {
            videoDisplaySetSetsTypeAPI = new VideoDisplaySetSetsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDisplaySetSetsTypeAPI = new VideoDisplaySetSetsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDisplaySetSets"));
        }
        addTypeAPI(videoDisplaySetSetsTypeAPI);
        factory = factoryOverrides.get("VideoDisplaySetSets");
        if(factory == null)
            factory = new VideoDisplaySetSetsHollowFactory();
        if(cachedTypes.contains("VideoDisplaySetSets")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDisplaySetSetsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDisplaySetSetsProvider;
            videoDisplaySetSetsProvider = new HollowObjectCacheProvider(typeDataAccess, videoDisplaySetSetsTypeAPI, factory, previousCacheProvider);
        } else {
            videoDisplaySetSetsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDisplaySetSetsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDisplaySetArrayOfSets");
        if(typeDataAccess != null) {
            videoDisplaySetArrayOfSetsTypeAPI = new VideoDisplaySetArrayOfSetsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoDisplaySetArrayOfSetsTypeAPI = new VideoDisplaySetArrayOfSetsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoDisplaySetArrayOfSets"));
        }
        addTypeAPI(videoDisplaySetArrayOfSetsTypeAPI);
        factory = factoryOverrides.get("VideoDisplaySetArrayOfSets");
        if(factory == null)
            factory = new VideoDisplaySetArrayOfSetsHollowFactory();
        if(cachedTypes.contains("VideoDisplaySetArrayOfSets")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDisplaySetArrayOfSetsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDisplaySetArrayOfSetsProvider;
            videoDisplaySetArrayOfSetsProvider = new HollowObjectCacheProvider(typeDataAccess, videoDisplaySetArrayOfSetsTypeAPI, factory, previousCacheProvider);
        } else {
            videoDisplaySetArrayOfSetsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDisplaySetArrayOfSetsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDisplaySet");
        if(typeDataAccess != null) {
            videoDisplaySetTypeAPI = new VideoDisplaySetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoDisplaySetTypeAPI = new VideoDisplaySetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoDisplaySet"));
        }
        addTypeAPI(videoDisplaySetTypeAPI);
        factory = factoryOverrides.get("VideoDisplaySet");
        if(factory == null)
            factory = new VideoDisplaySetHollowFactory();
        if(cachedTypes.contains("VideoDisplaySet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDisplaySetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDisplaySetProvider;
            videoDisplaySetProvider = new HollowObjectCacheProvider(typeDataAccess, videoDisplaySetTypeAPI, factory, previousCacheProvider);
        } else {
            videoDisplaySetProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDisplaySetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralAliases");
        if(typeDataAccess != null) {
            videoGeneralAliasesTypeAPI = new VideoGeneralAliasesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralAliasesTypeAPI = new VideoGeneralAliasesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralAliases"));
        }
        addTypeAPI(videoGeneralAliasesTypeAPI);
        factory = factoryOverrides.get("VideoGeneralAliases");
        if(factory == null)
            factory = new VideoGeneralAliasesHollowFactory();
        if(cachedTypes.contains("VideoGeneralAliases")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralAliasesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralAliasesProvider;
            videoGeneralAliasesProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralAliasesTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralAliasesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralAliasesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralArrayOfAliases");
        if(typeDataAccess != null) {
            videoGeneralArrayOfAliasesTypeAPI = new VideoGeneralArrayOfAliasesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralArrayOfAliasesTypeAPI = new VideoGeneralArrayOfAliasesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralArrayOfAliases"));
        }
        addTypeAPI(videoGeneralArrayOfAliasesTypeAPI);
        factory = factoryOverrides.get("VideoGeneralArrayOfAliases");
        if(factory == null)
            factory = new VideoGeneralArrayOfAliasesHollowFactory();
        if(cachedTypes.contains("VideoGeneralArrayOfAliases")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralArrayOfAliasesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralArrayOfAliasesProvider;
            videoGeneralArrayOfAliasesProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralArrayOfAliasesTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralArrayOfAliasesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralArrayOfAliasesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralEpisodeTypes");
        if(typeDataAccess != null) {
            videoGeneralEpisodeTypesTypeAPI = new VideoGeneralEpisodeTypesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralEpisodeTypesTypeAPI = new VideoGeneralEpisodeTypesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralEpisodeTypes"));
        }
        addTypeAPI(videoGeneralEpisodeTypesTypeAPI);
        factory = factoryOverrides.get("VideoGeneralEpisodeTypes");
        if(factory == null)
            factory = new VideoGeneralEpisodeTypesHollowFactory();
        if(cachedTypes.contains("VideoGeneralEpisodeTypes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralEpisodeTypesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralEpisodeTypesProvider;
            videoGeneralEpisodeTypesProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralEpisodeTypesTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralEpisodeTypesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralEpisodeTypesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralArrayOfEpisodeTypes");
        if(typeDataAccess != null) {
            videoGeneralArrayOfEpisodeTypesTypeAPI = new VideoGeneralArrayOfEpisodeTypesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralArrayOfEpisodeTypesTypeAPI = new VideoGeneralArrayOfEpisodeTypesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralArrayOfEpisodeTypes"));
        }
        addTypeAPI(videoGeneralArrayOfEpisodeTypesTypeAPI);
        factory = factoryOverrides.get("VideoGeneralArrayOfEpisodeTypes");
        if(factory == null)
            factory = new VideoGeneralArrayOfEpisodeTypesHollowFactory();
        if(cachedTypes.contains("VideoGeneralArrayOfEpisodeTypes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralArrayOfEpisodeTypesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralArrayOfEpisodeTypesProvider;
            videoGeneralArrayOfEpisodeTypesProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralArrayOfEpisodeTypesTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralArrayOfEpisodeTypesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralArrayOfEpisodeTypesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralTitleTypes");
        if(typeDataAccess != null) {
            videoGeneralTitleTypesTypeAPI = new VideoGeneralTitleTypesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralTitleTypesTypeAPI = new VideoGeneralTitleTypesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralTitleTypes"));
        }
        addTypeAPI(videoGeneralTitleTypesTypeAPI);
        factory = factoryOverrides.get("VideoGeneralTitleTypes");
        if(factory == null)
            factory = new VideoGeneralTitleTypesHollowFactory();
        if(cachedTypes.contains("VideoGeneralTitleTypes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralTitleTypesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralTitleTypesProvider;
            videoGeneralTitleTypesProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralTitleTypesTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralTitleTypesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralTitleTypesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralArrayOfTitleTypes");
        if(typeDataAccess != null) {
            videoGeneralArrayOfTitleTypesTypeAPI = new VideoGeneralArrayOfTitleTypesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralArrayOfTitleTypesTypeAPI = new VideoGeneralArrayOfTitleTypesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralArrayOfTitleTypes"));
        }
        addTypeAPI(videoGeneralArrayOfTitleTypesTypeAPI);
        factory = factoryOverrides.get("VideoGeneralArrayOfTitleTypes");
        if(factory == null)
            factory = new VideoGeneralArrayOfTitleTypesHollowFactory();
        if(cachedTypes.contains("VideoGeneralArrayOfTitleTypes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralArrayOfTitleTypesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralArrayOfTitleTypesProvider;
            videoGeneralArrayOfTitleTypesProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralArrayOfTitleTypesTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralArrayOfTitleTypesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralArrayOfTitleTypesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneral");
        if(typeDataAccess != null) {
            videoGeneralTypeAPI = new VideoGeneralTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralTypeAPI = new VideoGeneralTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneral"));
        }
        addTypeAPI(videoGeneralTypeAPI);
        factory = factoryOverrides.get("VideoGeneral");
        if(factory == null)
            factory = new VideoGeneralHollowFactory();
        if(cachedTypes.contains("VideoGeneral")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralProvider;
            videoGeneralProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoPersonAlias");
        if(typeDataAccess != null) {
            videoPersonAliasTypeAPI = new VideoPersonAliasTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoPersonAliasTypeAPI = new VideoPersonAliasTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoPersonAlias"));
        }
        addTypeAPI(videoPersonAliasTypeAPI);
        factory = factoryOverrides.get("VideoPersonAlias");
        if(factory == null)
            factory = new VideoPersonAliasHollowFactory();
        if(cachedTypes.contains("VideoPersonAlias")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoPersonAliasProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoPersonAliasProvider;
            videoPersonAliasProvider = new HollowObjectCacheProvider(typeDataAccess, videoPersonAliasTypeAPI, factory, previousCacheProvider);
        } else {
            videoPersonAliasProvider = new HollowObjectFactoryProvider(typeDataAccess, videoPersonAliasTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoPersonArrayOfAlias");
        if(typeDataAccess != null) {
            videoPersonArrayOfAliasTypeAPI = new VideoPersonArrayOfAliasTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoPersonArrayOfAliasTypeAPI = new VideoPersonArrayOfAliasTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoPersonArrayOfAlias"));
        }
        addTypeAPI(videoPersonArrayOfAliasTypeAPI);
        factory = factoryOverrides.get("VideoPersonArrayOfAlias");
        if(factory == null)
            factory = new VideoPersonArrayOfAliasHollowFactory();
        if(cachedTypes.contains("VideoPersonArrayOfAlias")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoPersonArrayOfAliasProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoPersonArrayOfAliasProvider;
            videoPersonArrayOfAliasProvider = new HollowObjectCacheProvider(typeDataAccess, videoPersonArrayOfAliasTypeAPI, factory, previousCacheProvider);
        } else {
            videoPersonArrayOfAliasProvider = new HollowObjectFactoryProvider(typeDataAccess, videoPersonArrayOfAliasTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoPersonCast");
        if(typeDataAccess != null) {
            videoPersonCastTypeAPI = new VideoPersonCastTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoPersonCastTypeAPI = new VideoPersonCastTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoPersonCast"));
        }
        addTypeAPI(videoPersonCastTypeAPI);
        factory = factoryOverrides.get("VideoPersonCast");
        if(factory == null)
            factory = new VideoPersonCastHollowFactory();
        if(cachedTypes.contains("VideoPersonCast")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoPersonCastProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoPersonCastProvider;
            videoPersonCastProvider = new HollowObjectCacheProvider(typeDataAccess, videoPersonCastTypeAPI, factory, previousCacheProvider);
        } else {
            videoPersonCastProvider = new HollowObjectFactoryProvider(typeDataAccess, videoPersonCastTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoPersonArrayOfCast");
        if(typeDataAccess != null) {
            videoPersonArrayOfCastTypeAPI = new VideoPersonArrayOfCastTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoPersonArrayOfCastTypeAPI = new VideoPersonArrayOfCastTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoPersonArrayOfCast"));
        }
        addTypeAPI(videoPersonArrayOfCastTypeAPI);
        factory = factoryOverrides.get("VideoPersonArrayOfCast");
        if(factory == null)
            factory = new VideoPersonArrayOfCastHollowFactory();
        if(cachedTypes.contains("VideoPersonArrayOfCast")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoPersonArrayOfCastProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoPersonArrayOfCastProvider;
            videoPersonArrayOfCastProvider = new HollowObjectCacheProvider(typeDataAccess, videoPersonArrayOfCastTypeAPI, factory, previousCacheProvider);
        } else {
            videoPersonArrayOfCastProvider = new HollowObjectFactoryProvider(typeDataAccess, videoPersonArrayOfCastTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoPerson");
        if(typeDataAccess != null) {
            videoPersonTypeAPI = new VideoPersonTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoPersonTypeAPI = new VideoPersonTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoPerson"));
        }
        addTypeAPI(videoPersonTypeAPI);
        factory = factoryOverrides.get("VideoPerson");
        if(factory == null)
            factory = new VideoPersonHollowFactory();
        if(cachedTypes.contains("VideoPerson")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoPersonProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoPersonProvider;
            videoPersonProvider = new HollowObjectCacheProvider(typeDataAccess, videoPersonTypeAPI, factory, previousCacheProvider);
        } else {
            videoPersonProvider = new HollowObjectFactoryProvider(typeDataAccess, videoPersonTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingRatingReasonIds");
        if(typeDataAccess != null) {
            videoRatingRatingReasonIdsTypeAPI = new VideoRatingRatingReasonIdsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRatingRatingReasonIdsTypeAPI = new VideoRatingRatingReasonIdsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRatingRatingReasonIds"));
        }
        addTypeAPI(videoRatingRatingReasonIdsTypeAPI);
        factory = factoryOverrides.get("VideoRatingRatingReasonIds");
        if(factory == null)
            factory = new VideoRatingRatingReasonIdsHollowFactory();
        if(cachedTypes.contains("VideoRatingRatingReasonIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingRatingReasonIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingRatingReasonIdsProvider;
            videoRatingRatingReasonIdsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingRatingReasonIdsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingRatingReasonIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingRatingReasonIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingRatingReasonArrayOfIds");
        if(typeDataAccess != null) {
            videoRatingRatingReasonArrayOfIdsTypeAPI = new VideoRatingRatingReasonArrayOfIdsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRatingRatingReasonArrayOfIdsTypeAPI = new VideoRatingRatingReasonArrayOfIdsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRatingRatingReasonArrayOfIds"));
        }
        addTypeAPI(videoRatingRatingReasonArrayOfIdsTypeAPI);
        factory = factoryOverrides.get("VideoRatingRatingReasonArrayOfIds");
        if(factory == null)
            factory = new VideoRatingRatingReasonArrayOfIdsHollowFactory();
        if(cachedTypes.contains("VideoRatingRatingReasonArrayOfIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingRatingReasonArrayOfIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingRatingReasonArrayOfIdsProvider;
            videoRatingRatingReasonArrayOfIdsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingRatingReasonArrayOfIdsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingRatingReasonArrayOfIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingRatingReasonArrayOfIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingRatingReason");
        if(typeDataAccess != null) {
            videoRatingRatingReasonTypeAPI = new VideoRatingRatingReasonTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRatingRatingReasonTypeAPI = new VideoRatingRatingReasonTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRatingRatingReason"));
        }
        addTypeAPI(videoRatingRatingReasonTypeAPI);
        factory = factoryOverrides.get("VideoRatingRatingReason");
        if(factory == null)
            factory = new VideoRatingRatingReasonHollowFactory();
        if(cachedTypes.contains("VideoRatingRatingReason")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingRatingReasonProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingRatingReasonProvider;
            videoRatingRatingReasonProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingRatingReasonTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingRatingReasonProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingRatingReasonTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingRating");
        if(typeDataAccess != null) {
            videoRatingRatingTypeAPI = new VideoRatingRatingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRatingRatingTypeAPI = new VideoRatingRatingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRatingRating"));
        }
        addTypeAPI(videoRatingRatingTypeAPI);
        factory = factoryOverrides.get("VideoRatingRating");
        if(factory == null)
            factory = new VideoRatingRatingHollowFactory();
        if(cachedTypes.contains("VideoRatingRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingRatingProvider;
            videoRatingRatingProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingRatingTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingArrayOfRating");
        if(typeDataAccess != null) {
            videoRatingArrayOfRatingTypeAPI = new VideoRatingArrayOfRatingTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRatingArrayOfRatingTypeAPI = new VideoRatingArrayOfRatingTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRatingArrayOfRating"));
        }
        addTypeAPI(videoRatingArrayOfRatingTypeAPI);
        factory = factoryOverrides.get("VideoRatingArrayOfRating");
        if(factory == null)
            factory = new VideoRatingArrayOfRatingHollowFactory();
        if(cachedTypes.contains("VideoRatingArrayOfRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingArrayOfRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingArrayOfRatingProvider;
            videoRatingArrayOfRatingProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingArrayOfRatingTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingArrayOfRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingArrayOfRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRating");
        if(typeDataAccess != null) {
            videoRatingTypeAPI = new VideoRatingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRatingTypeAPI = new VideoRatingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRating"));
        }
        addTypeAPI(videoRatingTypeAPI);
        factory = factoryOverrides.get("VideoRating");
        if(factory == null)
            factory = new VideoRatingHollowFactory();
        if(cachedTypes.contains("VideoRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingProvider;
            videoRatingProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsFlagsFirstDisplayDates");
        if(typeDataAccess != null) {
            videoRightsFlagsFirstDisplayDatesTypeAPI = new VideoRightsFlagsFirstDisplayDatesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsFlagsFirstDisplayDatesTypeAPI = new VideoRightsFlagsFirstDisplayDatesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsFlagsFirstDisplayDates"));
        }
        addTypeAPI(videoRightsFlagsFirstDisplayDatesTypeAPI);
        factory = factoryOverrides.get("VideoRightsFlagsFirstDisplayDates");
        if(factory == null)
            factory = new VideoRightsFlagsFirstDisplayDatesHollowFactory();
        if(cachedTypes.contains("VideoRightsFlagsFirstDisplayDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsFlagsFirstDisplayDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsFlagsFirstDisplayDatesProvider;
            videoRightsFlagsFirstDisplayDatesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsFlagsFirstDisplayDatesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsFlagsFirstDisplayDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsFlagsFirstDisplayDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsFlagsMapOfFirstDisplayDates");
        if(typeDataAccess != null) {
            videoRightsFlagsMapOfFirstDisplayDatesTypeAPI = new VideoRightsFlagsMapOfFirstDisplayDatesTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            videoRightsFlagsMapOfFirstDisplayDatesTypeAPI = new VideoRightsFlagsMapOfFirstDisplayDatesTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "VideoRightsFlagsMapOfFirstDisplayDates"));
        }
        addTypeAPI(videoRightsFlagsMapOfFirstDisplayDatesTypeAPI);
        factory = factoryOverrides.get("VideoRightsFlagsMapOfFirstDisplayDates");
        if(factory == null)
            factory = new VideoRightsFlagsMapOfFirstDisplayDatesHollowFactory();
        if(cachedTypes.contains("VideoRightsFlagsMapOfFirstDisplayDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsFlagsMapOfFirstDisplayDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsFlagsMapOfFirstDisplayDatesProvider;
            videoRightsFlagsMapOfFirstDisplayDatesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsFlagsMapOfFirstDisplayDatesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsFlagsMapOfFirstDisplayDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsFlagsMapOfFirstDisplayDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsFlags");
        if(typeDataAccess != null) {
            videoRightsFlagsTypeAPI = new VideoRightsFlagsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsFlagsTypeAPI = new VideoRightsFlagsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsFlags"));
        }
        addTypeAPI(videoRightsFlagsTypeAPI);
        factory = factoryOverrides.get("VideoRightsFlags");
        if(factory == null)
            factory = new VideoRightsFlagsHollowFactory();
        if(cachedTypes.contains("VideoRightsFlags")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsFlagsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsFlagsProvider;
            videoRightsFlagsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsFlagsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsFlagsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsFlagsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsAssets");
        if(typeDataAccess != null) {
            videoRightsRightsContractsAssetsTypeAPI = new VideoRightsRightsContractsAssetsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsAssetsTypeAPI = new VideoRightsRightsContractsAssetsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRightsContractsAssets"));
        }
        addTypeAPI(videoRightsRightsContractsAssetsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsAssets");
        if(factory == null)
            factory = new VideoRightsRightsContractsAssetsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsAssets")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsAssetsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsAssetsProvider;
            videoRightsRightsContractsAssetsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsAssetsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsAssetsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsAssetsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsArrayOfAssets");
        if(typeDataAccess != null) {
            videoRightsRightsContractsArrayOfAssetsTypeAPI = new VideoRightsRightsContractsArrayOfAssetsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsArrayOfAssetsTypeAPI = new VideoRightsRightsContractsArrayOfAssetsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsRightsContractsArrayOfAssets"));
        }
        addTypeAPI(videoRightsRightsContractsArrayOfAssetsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsArrayOfAssets");
        if(factory == null)
            factory = new VideoRightsRightsContractsArrayOfAssetsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsArrayOfAssets")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsArrayOfAssetsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsArrayOfAssetsProvider;
            videoRightsRightsContractsArrayOfAssetsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsArrayOfAssetsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsArrayOfAssetsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsArrayOfAssetsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodes");
        if(typeDataAccess != null) {
            videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI = new VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI = new VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodes"));
        }
        addTypeAPI(videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodes");
        if(factory == null)
            factory = new VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider;
            videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodes");
        if(typeDataAccess != null) {
            videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI = new VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI = new VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodes"));
        }
        addTypeAPI(videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodes");
        if(factory == null)
            factory = new VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider;
            videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsDisallowedAssetBundles");
        if(typeDataAccess != null) {
            videoRightsRightsContractsDisallowedAssetBundlesTypeAPI = new VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsDisallowedAssetBundlesTypeAPI = new VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRightsContractsDisallowedAssetBundles"));
        }
        addTypeAPI(videoRightsRightsContractsDisallowedAssetBundlesTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsDisallowedAssetBundles");
        if(factory == null)
            factory = new VideoRightsRightsContractsDisallowedAssetBundlesHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsDisallowedAssetBundles")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsDisallowedAssetBundlesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsDisallowedAssetBundlesProvider;
            videoRightsRightsContractsDisallowedAssetBundlesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsDisallowedAssetBundlesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsDisallowedAssetBundlesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsDisallowedAssetBundlesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsArrayOfDisallowedAssetBundles");
        if(typeDataAccess != null) {
            videoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI = new VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI = new VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsRightsContractsArrayOfDisallowedAssetBundles"));
        }
        addTypeAPI(videoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsArrayOfDisallowedAssetBundles");
        if(factory == null)
            factory = new VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsArrayOfDisallowedAssetBundles")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider;
            videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsPackages");
        if(typeDataAccess != null) {
            videoRightsRightsContractsPackagesTypeAPI = new VideoRightsRightsContractsPackagesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsPackagesTypeAPI = new VideoRightsRightsContractsPackagesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRightsContractsPackages"));
        }
        addTypeAPI(videoRightsRightsContractsPackagesTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsPackages");
        if(factory == null)
            factory = new VideoRightsRightsContractsPackagesHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsPackages")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsPackagesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsPackagesProvider;
            videoRightsRightsContractsPackagesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsPackagesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsPackagesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsPackagesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContractsArrayOfPackages");
        if(typeDataAccess != null) {
            videoRightsRightsContractsArrayOfPackagesTypeAPI = new VideoRightsRightsContractsArrayOfPackagesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsArrayOfPackagesTypeAPI = new VideoRightsRightsContractsArrayOfPackagesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsRightsContractsArrayOfPackages"));
        }
        addTypeAPI(videoRightsRightsContractsArrayOfPackagesTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContractsArrayOfPackages");
        if(factory == null)
            factory = new VideoRightsRightsContractsArrayOfPackagesHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContractsArrayOfPackages")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsArrayOfPackagesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsArrayOfPackagesProvider;
            videoRightsRightsContractsArrayOfPackagesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsArrayOfPackagesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsArrayOfPackagesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsArrayOfPackagesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsContracts");
        if(typeDataAccess != null) {
            videoRightsRightsContractsTypeAPI = new VideoRightsRightsContractsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsContractsTypeAPI = new VideoRightsRightsContractsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRightsContracts"));
        }
        addTypeAPI(videoRightsRightsContractsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsContracts");
        if(factory == null)
            factory = new VideoRightsRightsContractsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsContracts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsContractsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsContractsProvider;
            videoRightsRightsContractsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsContractsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsContractsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsContractsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsArrayOfContracts");
        if(typeDataAccess != null) {
            videoRightsRightsArrayOfContractsTypeAPI = new VideoRightsRightsArrayOfContractsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsArrayOfContractsTypeAPI = new VideoRightsRightsArrayOfContractsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsRightsArrayOfContracts"));
        }
        addTypeAPI(videoRightsRightsArrayOfContractsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsArrayOfContracts");
        if(factory == null)
            factory = new VideoRightsRightsArrayOfContractsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsArrayOfContracts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsArrayOfContractsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsArrayOfContractsProvider;
            videoRightsRightsArrayOfContractsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsArrayOfContractsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsArrayOfContractsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsArrayOfContractsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsWindowsContractIds");
        if(typeDataAccess != null) {
            videoRightsRightsWindowsContractIdsTypeAPI = new VideoRightsRightsWindowsContractIdsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsWindowsContractIdsTypeAPI = new VideoRightsRightsWindowsContractIdsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRightsWindowsContractIds"));
        }
        addTypeAPI(videoRightsRightsWindowsContractIdsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsWindowsContractIds");
        if(factory == null)
            factory = new VideoRightsRightsWindowsContractIdsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsWindowsContractIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsWindowsContractIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsWindowsContractIdsProvider;
            videoRightsRightsWindowsContractIdsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsWindowsContractIdsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsWindowsContractIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsWindowsContractIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsWindowsArrayOfContractIds");
        if(typeDataAccess != null) {
            videoRightsRightsWindowsArrayOfContractIdsTypeAPI = new VideoRightsRightsWindowsArrayOfContractIdsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsWindowsArrayOfContractIdsTypeAPI = new VideoRightsRightsWindowsArrayOfContractIdsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsRightsWindowsArrayOfContractIds"));
        }
        addTypeAPI(videoRightsRightsWindowsArrayOfContractIdsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsWindowsArrayOfContractIds");
        if(factory == null)
            factory = new VideoRightsRightsWindowsArrayOfContractIdsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsWindowsArrayOfContractIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsWindowsArrayOfContractIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsWindowsArrayOfContractIdsProvider;
            videoRightsRightsWindowsArrayOfContractIdsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsWindowsArrayOfContractIdsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsWindowsArrayOfContractIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsWindowsArrayOfContractIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsWindows");
        if(typeDataAccess != null) {
            videoRightsRightsWindowsTypeAPI = new VideoRightsRightsWindowsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsWindowsTypeAPI = new VideoRightsRightsWindowsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRightsWindows"));
        }
        addTypeAPI(videoRightsRightsWindowsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsWindows");
        if(factory == null)
            factory = new VideoRightsRightsWindowsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsWindows")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsWindowsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsWindowsProvider;
            videoRightsRightsWindowsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsWindowsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsWindowsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsWindowsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRightsArrayOfWindows");
        if(typeDataAccess != null) {
            videoRightsRightsArrayOfWindowsTypeAPI = new VideoRightsRightsArrayOfWindowsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsArrayOfWindowsTypeAPI = new VideoRightsRightsArrayOfWindowsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsRightsArrayOfWindows"));
        }
        addTypeAPI(videoRightsRightsArrayOfWindowsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRightsArrayOfWindows");
        if(factory == null)
            factory = new VideoRightsRightsArrayOfWindowsHollowFactory();
        if(cachedTypes.contains("VideoRightsRightsArrayOfWindows")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsArrayOfWindowsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsArrayOfWindowsProvider;
            videoRightsRightsArrayOfWindowsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsArrayOfWindowsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsArrayOfWindowsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsArrayOfWindowsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsRights");
        if(typeDataAccess != null) {
            videoRightsRightsTypeAPI = new VideoRightsRightsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsRightsTypeAPI = new VideoRightsRightsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsRights"));
        }
        addTypeAPI(videoRightsRightsTypeAPI);
        factory = factoryOverrides.get("VideoRightsRights");
        if(factory == null)
            factory = new VideoRightsRightsHollowFactory();
        if(cachedTypes.contains("VideoRightsRights")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsRightsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsRightsProvider;
            videoRightsRightsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsRightsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsRightsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsRightsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRights");
        if(typeDataAccess != null) {
            videoRightsTypeAPI = new VideoRightsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsTypeAPI = new VideoRightsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRights"));
        }
        addTypeAPI(videoRightsTypeAPI);
        factory = factoryOverrides.get("VideoRights");
        if(factory == null)
            factory = new VideoRightsHollowFactory();
        if(cachedTypes.contains("VideoRights")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsProvider;
            videoRightsProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeTypeMedia");
        if(typeDataAccess != null) {
            videoTypeTypeMediaTypeAPI = new VideoTypeTypeMediaTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeTypeMediaTypeAPI = new VideoTypeTypeMediaTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoTypeTypeMedia"));
        }
        addTypeAPI(videoTypeTypeMediaTypeAPI);
        factory = factoryOverrides.get("VideoTypeTypeMedia");
        if(factory == null)
            factory = new VideoTypeTypeMediaHollowFactory();
        if(cachedTypes.contains("VideoTypeTypeMedia")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeTypeMediaProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeTypeMediaProvider;
            videoTypeTypeMediaProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeTypeMediaTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeTypeMediaProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeTypeMediaTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeTypeArrayOfMedia");
        if(typeDataAccess != null) {
            videoTypeTypeArrayOfMediaTypeAPI = new VideoTypeTypeArrayOfMediaTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoTypeTypeArrayOfMediaTypeAPI = new VideoTypeTypeArrayOfMediaTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoTypeTypeArrayOfMedia"));
        }
        addTypeAPI(videoTypeTypeArrayOfMediaTypeAPI);
        factory = factoryOverrides.get("VideoTypeTypeArrayOfMedia");
        if(factory == null)
            factory = new VideoTypeTypeArrayOfMediaHollowFactory();
        if(cachedTypes.contains("VideoTypeTypeArrayOfMedia")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeTypeArrayOfMediaProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeTypeArrayOfMediaProvider;
            videoTypeTypeArrayOfMediaProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeTypeArrayOfMediaTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeTypeArrayOfMediaProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeTypeArrayOfMediaTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeType");
        if(typeDataAccess != null) {
            videoTypeTypeTypeAPI = new VideoTypeTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeTypeTypeAPI = new VideoTypeTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoTypeType"));
        }
        addTypeAPI(videoTypeTypeTypeAPI);
        factory = factoryOverrides.get("VideoTypeType");
        if(factory == null)
            factory = new VideoTypeTypeHollowFactory();
        if(cachedTypes.contains("VideoTypeType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeTypeProvider;
            videoTypeTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeArrayOfType");
        if(typeDataAccess != null) {
            videoTypeArrayOfTypeTypeAPI = new VideoTypeArrayOfTypeTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoTypeArrayOfTypeTypeAPI = new VideoTypeArrayOfTypeTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoTypeArrayOfType"));
        }
        addTypeAPI(videoTypeArrayOfTypeTypeAPI);
        factory = factoryOverrides.get("VideoTypeArrayOfType");
        if(factory == null)
            factory = new VideoTypeArrayOfTypeHollowFactory();
        if(cachedTypes.contains("VideoTypeArrayOfType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeArrayOfTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeArrayOfTypeProvider;
            videoTypeArrayOfTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeArrayOfTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeArrayOfTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeArrayOfTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoType");
        if(typeDataAccess != null) {
            videoTypeTypeAPI = new VideoTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeTypeAPI = new VideoTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoType"));
        }
        addTypeAPI(videoTypeTypeAPI);
        factory = factoryOverrides.get("VideoType");
        if(factory == null)
            factory = new VideoTypeHollowFactory();
        if(cachedTypes.contains("VideoType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeProvider;
            videoTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(awardsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsDescriptionTranslatedTextsProvider).detach();
        if(awardsDescriptionProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsDescriptionProvider).detach();
        if(characterQuotesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterQuotesProvider).detach();
        if(characterArrayOfQuotesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArrayOfQuotesProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider).detach();
        if(mapKeyProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapKeyProvider).detach();
        if(rolloutLaunchDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutLaunchDatesProvider).detach();
        if(rolloutMapOfLaunchDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutMapOfLaunchDatesProvider).detach();
        if(rolloutPhasesElementsArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArtworkProvider).detach();
        if(rolloutPhasesElementsArrayOfArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArrayOfArtworkProvider).detach();
        if(rolloutPhasesElementsCastProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsCastProvider).detach();
        if(rolloutPhasesElementsArrayOfCastProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArrayOfCastProvider).detach();
        if(rolloutPhasesElementsCharactersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsCharactersProvider).detach();
        if(rolloutPhasesElementsArrayOfCharactersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArrayOfCharactersProvider).detach();
        if(rolloutPhasesWindowsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesWindowsProvider).detach();
        if(rolloutPhasesMapOfWindowsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesMapOfWindowsProvider).detach();
        if(streamProfileGroupsStreamProfileIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfileGroupsStreamProfileIdsProvider).detach();
        if(streamProfileGroupsArrayOfStreamProfileIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfileGroupsArrayOfStreamProfileIdsProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(altGenresAlternateNamesTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresAlternateNamesTranslatedTextsProvider).detach();
        if(altGenresAlternateNamesMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresAlternateNamesMapOfTranslatedTextsProvider).detach();
        if(altGenresAlternateNamesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresAlternateNamesProvider).detach();
        if(altGenresArrayOfAlternateNamesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresArrayOfAlternateNamesProvider).detach();
        if(altGenresDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresDisplayNameTranslatedTextsProvider).detach();
        if(altGenresDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresDisplayNameMapOfTranslatedTextsProvider).detach();
        if(altGenresDisplayNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresDisplayNameProvider).detach();
        if(altGenresShortNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresShortNameTranslatedTextsProvider).detach();
        if(altGenresShortNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresShortNameMapOfTranslatedTextsProvider).detach();
        if(altGenresShortNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresShortNameProvider).detach();
        if(altGenresProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresProvider).detach();
        if(artWorkImageFormatProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artWorkImageFormatProvider).detach();
        if(artWorkImageTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artWorkImageTypeProvider).detach();
        if(artworkRecipeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artworkRecipeProvider).detach();
        if(assetMetaDatasTrackLabelsTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)assetMetaDatasTrackLabelsTranslatedTextsProvider).detach();
        if(assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider).detach();
        if(assetMetaDatasTrackLabelsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)assetMetaDatasTrackLabelsProvider).detach();
        if(assetMetaDatasProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)assetMetaDatasProvider).detach();
        if(awardsAlternateNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsAlternateNameTranslatedTextsProvider).detach();
        if(awardsAlternateNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsAlternateNameMapOfTranslatedTextsProvider).detach();
        if(awardsAlternateNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsAlternateNameProvider).detach();
        if(awardsAwardNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsAwardNameTranslatedTextsProvider).detach();
        if(awardsAwardNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsAwardNameMapOfTranslatedTextsProvider).detach();
        if(awardsAwardNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsAwardNameProvider).detach();
        if(awardsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsProvider).detach();
        if(bcp47CodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)bcp47CodeProvider).detach();
        if(cSMReviewProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cSMReviewProvider).detach();
        if(cacheDeploymentIntentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cacheDeploymentIntentProvider).detach();
        if(categoriesDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesDisplayNameTranslatedTextsProvider).detach();
        if(categoriesDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesDisplayNameMapOfTranslatedTextsProvider).detach();
        if(categoriesDisplayNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesDisplayNameProvider).detach();
        if(categoriesShortNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesShortNameTranslatedTextsProvider).detach();
        if(categoriesShortNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesShortNameMapOfTranslatedTextsProvider).detach();
        if(categoriesShortNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesShortNameProvider).detach();
        if(categoriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesProvider).detach();
        if(categoryGroupsCategoryGroupNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoryGroupsCategoryGroupNameTranslatedTextsProvider).detach();
        if(categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider).detach();
        if(categoryGroupsCategoryGroupNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoryGroupsCategoryGroupNameProvider).detach();
        if(categoryGroupsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoryGroupsProvider).detach();
        if(cdnsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cdnsProvider).detach();
        if(certificationSystemRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemRatingProvider).detach();
        if(certificationSystemArrayOfRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemArrayOfRatingProvider).detach();
        if(certificationSystemProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemProvider).detach();
        if(certificationsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsDescriptionTranslatedTextsProvider).detach();
        if(certificationsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsDescriptionMapOfTranslatedTextsProvider).detach();
        if(certificationsDescriptionProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsDescriptionProvider).detach();
        if(certificationsNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsNameTranslatedTextsProvider).detach();
        if(certificationsNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsNameMapOfTranslatedTextsProvider).detach();
        if(certificationsNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsNameProvider).detach();
        if(certificationsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsProvider).detach();
        if(characterArtworkAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkAttributesProvider).detach();
        if(characterArtworkDerivativesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkDerivativesProvider).detach();
        if(characterArtworkArrayOfDerivativesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkArrayOfDerivativesProvider).detach();
        if(characterArtworkLocalesTerritoryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkLocalesTerritoryCodesProvider).detach();
        if(characterArtworkLocalesArrayOfTerritoryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkLocalesArrayOfTerritoryCodesProvider).detach();
        if(characterArtworkLocalesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkLocalesProvider).detach();
        if(characterArtworkArrayOfLocalesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkArrayOfLocalesProvider).detach();
        if(characterArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkProvider).detach();
        if(characterElementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterElementsProvider).detach();
        if(characterProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterProvider).detach();
        if(charactersBTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersBTranslatedTextsProvider).detach();
        if(charactersBMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersBMapOfTranslatedTextsProvider).detach();
        if(charactersBProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersBProvider).detach();
        if(charactersCnTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersCnTranslatedTextsProvider).detach();
        if(charactersCnMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersCnMapOfTranslatedTextsProvider).detach();
        if(charactersCnProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersCnProvider).detach();
        if(charactersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersProvider).detach();
        if(consolidatedCertificationSystemsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsDescriptionTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsDescriptionProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsDescriptionProvider).detach();
        if(consolidatedCertificationSystemsNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsNameTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsNameProvider).detach();
        if(consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsRatingDescriptionsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsRatingDescriptionsProvider).detach();
        if(consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider).detach();
        if(consolidatedCertificationSystemsRatingRatingCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsRatingRatingCodesProvider).detach();
        if(consolidatedCertificationSystemsRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsRatingProvider).detach();
        if(consolidatedCertificationSystemsArrayOfRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsArrayOfRatingProvider).detach();
        if(consolidatedCertificationSystemsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryListProvider).detach();
        if(consolidatedVideoRatingsRatingsArrayOfCountryListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsArrayOfCountryListProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider).detach();
        if(consolidatedVideoRatingsRatingsCountryRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsCountryRatingsProvider).detach();
        if(consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider).detach();
        if(consolidatedVideoRatingsRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsRatingsProvider).detach();
        if(consolidatedVideoRatingsArrayOfRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsArrayOfRatingsProvider).detach();
        if(consolidatedVideoRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsProvider).detach();
        if(defaultExtensionRecipeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)defaultExtensionRecipeProvider).detach();
        if(deployablePackagesCountryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)deployablePackagesCountryCodesProvider).detach();
        if(deployablePackagesArrayOfCountryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)deployablePackagesArrayOfCountryCodesProvider).detach();
        if(deployablePackagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)deployablePackagesProvider).detach();
        if(drmSystemIdentifiersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmSystemIdentifiersProvider).detach();
        if(episodesEpisodeNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodesEpisodeNameTranslatedTextsProvider).detach();
        if(episodesEpisodeNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodesEpisodeNameMapOfTranslatedTextsProvider).detach();
        if(episodesEpisodeNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodesEpisodeNameProvider).detach();
        if(episodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodesProvider).detach();
        if(festivalsCopyrightTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsCopyrightTranslatedTextsProvider).detach();
        if(festivalsCopyrightMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsCopyrightMapOfTranslatedTextsProvider).detach();
        if(festivalsCopyrightProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsCopyrightProvider).detach();
        if(festivalsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsDescriptionTranslatedTextsProvider).detach();
        if(festivalsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsDescriptionMapOfTranslatedTextsProvider).detach();
        if(festivalsDescriptionProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsDescriptionProvider).detach();
        if(festivalsFestivalNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsFestivalNameTranslatedTextsProvider).detach();
        if(festivalsFestivalNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsFestivalNameMapOfTranslatedTextsProvider).detach();
        if(festivalsFestivalNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsFestivalNameProvider).detach();
        if(festivalsShortNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsShortNameTranslatedTextsProvider).detach();
        if(festivalsShortNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsShortNameMapOfTranslatedTextsProvider).detach();
        if(festivalsShortNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsShortNameProvider).detach();
        if(festivalsSingularNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsSingularNameTranslatedTextsProvider).detach();
        if(festivalsSingularNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsSingularNameMapOfTranslatedTextsProvider).detach();
        if(festivalsSingularNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsSingularNameProvider).detach();
        if(festivalsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsProvider).detach();
        if(languagesNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)languagesNameTranslatedTextsProvider).detach();
        if(languagesNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)languagesNameMapOfTranslatedTextsProvider).detach();
        if(languagesNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)languagesNameProvider).detach();
        if(languagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)languagesProvider).detach();
        if(localizedCharacterTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedCharacterTranslatedTextsProvider).detach();
        if(localizedCharacterMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedCharacterMapOfTranslatedTextsProvider).detach();
        if(localizedCharacterProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedCharacterProvider).detach();
        if(localizedMetadataTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedMetadataTranslatedTextsProvider).detach();
        if(localizedMetadataMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedMetadataMapOfTranslatedTextsProvider).detach();
        if(localizedMetadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedMetadataProvider).detach();
        if(movieRatingsRatingReasonTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieRatingsRatingReasonTranslatedTextsProvider).detach();
        if(movieRatingsRatingReasonMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieRatingsRatingReasonMapOfTranslatedTextsProvider).detach();
        if(movieRatingsRatingReasonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieRatingsRatingReasonProvider).detach();
        if(movieRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieRatingsProvider).detach();
        if(moviesAkaTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesAkaTranslatedTextsProvider).detach();
        if(moviesAkaMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesAkaMapOfTranslatedTextsProvider).detach();
        if(moviesAkaProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesAkaProvider).detach();
        if(moviesDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesDisplayNameTranslatedTextsProvider).detach();
        if(moviesDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesDisplayNameMapOfTranslatedTextsProvider).detach();
        if(moviesDisplayNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesDisplayNameProvider).detach();
        if(moviesOriginalTitleTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesOriginalTitleTranslatedTextsProvider).detach();
        if(moviesOriginalTitleMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesOriginalTitleMapOfTranslatedTextsProvider).detach();
        if(moviesOriginalTitleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesOriginalTitleProvider).detach();
        if(moviesShortDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesShortDisplayNameTranslatedTextsProvider).detach();
        if(moviesShortDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesShortDisplayNameMapOfTranslatedTextsProvider).detach();
        if(moviesShortDisplayNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesShortDisplayNameProvider).detach();
        if(moviesSiteSynopsisTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesSiteSynopsisTranslatedTextsProvider).detach();
        if(moviesSiteSynopsisMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesSiteSynopsisMapOfTranslatedTextsProvider).detach();
        if(moviesSiteSynopsisProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesSiteSynopsisProvider).detach();
        if(moviesTransliteratedTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesTransliteratedTranslatedTextsProvider).detach();
        if(moviesTransliteratedMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesTransliteratedMapOfTranslatedTextsProvider).detach();
        if(moviesTransliteratedProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesTransliteratedProvider).detach();
        if(moviesTvSynopsisTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesTvSynopsisTranslatedTextsProvider).detach();
        if(moviesTvSynopsisMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesTvSynopsisMapOfTranslatedTextsProvider).detach();
        if(moviesTvSynopsisProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesTvSynopsisProvider).detach();
        if(moviesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesProvider).detach();
        if(originServersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)originServersProvider).detach();
        if(personAliasesNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personAliasesNameTranslatedTextsProvider).detach();
        if(personAliasesNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personAliasesNameMapOfTranslatedTextsProvider).detach();
        if(personAliasesNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personAliasesNameProvider).detach();
        if(personAliasesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personAliasesProvider).detach();
        if(personArtworkAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkAttributesProvider).detach();
        if(personArtworkDerivativesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkDerivativesProvider).detach();
        if(personArtworkArrayOfDerivativesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkArrayOfDerivativesProvider).detach();
        if(personArtworkLocalesTerritoryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkLocalesTerritoryCodesProvider).detach();
        if(personArtworkLocalesArrayOfTerritoryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkLocalesArrayOfTerritoryCodesProvider).detach();
        if(personArtworkLocalesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkLocalesProvider).detach();
        if(personArtworkArrayOfLocalesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkArrayOfLocalesProvider).detach();
        if(personArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkProvider).detach();
        if(personsBioTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsBioTranslatedTextsProvider).detach();
        if(personsBioMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsBioMapOfTranslatedTextsProvider).detach();
        if(personsBioProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsBioProvider).detach();
        if(personsNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsNameTranslatedTextsProvider).detach();
        if(personsNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsNameMapOfTranslatedTextsProvider).detach();
        if(personsNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsNameProvider).detach();
        if(personsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsProvider).detach();
        if(protectionTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)protectionTypesProvider).detach();
        if(ratingsDescriptionTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsDescriptionTranslatedTextsProvider).detach();
        if(ratingsDescriptionMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsDescriptionMapOfTranslatedTextsProvider).detach();
        if(ratingsDescriptionProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsDescriptionProvider).detach();
        if(ratingsRatingCodeTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsRatingCodeTranslatedTextsProvider).detach();
        if(ratingsRatingCodeMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsRatingCodeMapOfTranslatedTextsProvider).detach();
        if(ratingsRatingCodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsRatingCodeProvider).detach();
        if(ratingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsProvider).detach();
        if(rolloutPhasesElementsArtwork_newSourceFileIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArtwork_newSourceFileIdsProvider).detach();
        if(rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider).detach();
        if(rolloutPhasesElementsArtwork_newProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArtwork_newProvider).detach();
        if(rolloutPhasesElementsLocalized_metadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsLocalized_metadataProvider).detach();
        if(rolloutPhasesElementsTrailersSupplementalInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsTrailersSupplementalInfoProvider).detach();
        if(rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider).detach();
        if(rolloutPhasesElementsTrailersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsTrailersProvider).detach();
        if(rolloutPhasesElementsArrayOfTrailersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsArrayOfTrailersProvider).detach();
        if(rolloutPhasesElementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsProvider).detach();
        if(rolloutPhasesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesProvider).detach();
        if(rolloutArrayOfPhasesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutArrayOfPhasesProvider).detach();
        if(rolloutProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutProvider).detach();
        if(showMemberTypesDisplayNameTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypesDisplayNameTranslatedTextsProvider).detach();
        if(showMemberTypesDisplayNameMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypesDisplayNameMapOfTranslatedTextsProvider).detach();
        if(showMemberTypesDisplayNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypesDisplayNameProvider).detach();
        if(showMemberTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypesProvider).detach();
        if(storageGroupsCountriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)storageGroupsCountriesProvider).detach();
        if(storageGroupsArrayOfCountriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)storageGroupsArrayOfCountriesProvider).detach();
        if(storageGroupsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)storageGroupsProvider).detach();
        if(stories_SynopsesHooksTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesHooksTranslatedTextsProvider).detach();
        if(stories_SynopsesHooksMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesHooksMapOfTranslatedTextsProvider).detach();
        if(stories_SynopsesHooksProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesHooksProvider).detach();
        if(stories_SynopsesArrayOfHooksProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesArrayOfHooksProvider).detach();
        if(stories_SynopsesNarrativeTextTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesNarrativeTextTranslatedTextsProvider).detach();
        if(stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider).detach();
        if(stories_SynopsesNarrativeTextProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesNarrativeTextProvider).detach();
        if(stories_SynopsesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesProvider).detach();
        if(streamProfileGroupsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfileGroupsProvider).detach();
        if(streamProfilesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfilesProvider).detach();
        if(territoryCountriesCountryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)territoryCountriesCountryCodesProvider).detach();
        if(territoryCountriesArrayOfCountryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)territoryCountriesArrayOfCountryCodesProvider).detach();
        if(territoryCountriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)territoryCountriesProvider).detach();
        if(topNAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNAttributesProvider).detach();
        if(topNArrayOfAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNArrayOfAttributesProvider).detach();
        if(topNProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNProvider).detach();
        if(trailerTrailersThemesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerTrailersThemesProvider).detach();
        if(trailerTrailersArrayOfThemesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerTrailersArrayOfThemesProvider).detach();
        if(trailerTrailersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerTrailersProvider).detach();
        if(trailerArrayOfTrailersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerArrayOfTrailersProvider).detach();
        if(trailerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerProvider).detach();
        if(vMSAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)vMSAwardProvider).detach();
        if(videoArtWorkAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkAttributesProvider).detach();
        if(videoArtWorkArrayOfAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkArrayOfAttributesProvider).detach();
        if(videoArtWorkExtensionsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkExtensionsProvider).detach();
        if(videoArtWorkArrayOfExtensionsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkArrayOfExtensionsProvider).detach();
        if(videoArtWorkLocalesTerritoryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkLocalesTerritoryCodesProvider).detach();
        if(videoArtWorkLocalesArrayOfTerritoryCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkLocalesArrayOfTerritoryCodesProvider).detach();
        if(videoArtWorkLocalesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkLocalesProvider).detach();
        if(videoArtWorkArrayOfLocalesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkArrayOfLocalesProvider).detach();
        if(videoArtWorkRecipesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkRecipesProvider).detach();
        if(videoArtWorkArrayOfRecipesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkArrayOfRecipesProvider).detach();
        if(videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider).detach();
        if(videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider).detach();
        if(videoArtWorkSourceAttributesIDENTIFIERSProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesIDENTIFIERSProvider).detach();
        if(videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider).detach();
        if(videoArtWorkSourceAttributesPERSON_IDSProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesPERSON_IDSProvider).detach();
        if(videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider).detach();
        if(videoArtWorkSourceAttributesThemesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesThemesProvider).detach();
        if(videoArtWorkSourceAttributesArrayOfThemesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesArrayOfThemesProvider).detach();
        if(videoArtWorkSourceAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesProvider).detach();
        if(videoArtWorkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkProvider).detach();
        if(videoAwardAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardAwardProvider).detach();
        if(videoAwardArrayOfAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardArrayOfAwardProvider).detach();
        if(videoAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardProvider).detach();
        if(videoDateWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateWindowProvider).detach();
        if(videoDateArrayOfWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateArrayOfWindowProvider).detach();
        if(videoDateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateProvider).detach();
        if(videoDisplaySetSetsChildrenChildrenProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetSetsChildrenChildrenProvider).detach();
        if(videoDisplaySetSetsChildrenArrayOfChildrenProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetSetsChildrenArrayOfChildrenProvider).detach();
        if(videoDisplaySetSetsChildrenProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetSetsChildrenProvider).detach();
        if(videoDisplaySetSetsArrayOfChildrenProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetSetsArrayOfChildrenProvider).detach();
        if(videoDisplaySetSetsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetSetsProvider).detach();
        if(videoDisplaySetArrayOfSetsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetArrayOfSetsProvider).detach();
        if(videoDisplaySetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetProvider).detach();
        if(videoGeneralAliasesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralAliasesProvider).detach();
        if(videoGeneralArrayOfAliasesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralArrayOfAliasesProvider).detach();
        if(videoGeneralEpisodeTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralEpisodeTypesProvider).detach();
        if(videoGeneralArrayOfEpisodeTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralArrayOfEpisodeTypesProvider).detach();
        if(videoGeneralTitleTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralTitleTypesProvider).detach();
        if(videoGeneralArrayOfTitleTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralArrayOfTitleTypesProvider).detach();
        if(videoGeneralProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralProvider).detach();
        if(videoPersonAliasProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonAliasProvider).detach();
        if(videoPersonArrayOfAliasProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonArrayOfAliasProvider).detach();
        if(videoPersonCastProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonCastProvider).detach();
        if(videoPersonArrayOfCastProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonArrayOfCastProvider).detach();
        if(videoPersonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonProvider).detach();
        if(videoRatingRatingReasonIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingRatingReasonIdsProvider).detach();
        if(videoRatingRatingReasonArrayOfIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingRatingReasonArrayOfIdsProvider).detach();
        if(videoRatingRatingReasonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingRatingReasonProvider).detach();
        if(videoRatingRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingRatingProvider).detach();
        if(videoRatingArrayOfRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingArrayOfRatingProvider).detach();
        if(videoRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingProvider).detach();
        if(videoRightsFlagsFirstDisplayDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsFlagsFirstDisplayDatesProvider).detach();
        if(videoRightsFlagsMapOfFirstDisplayDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsFlagsMapOfFirstDisplayDatesProvider).detach();
        if(videoRightsFlagsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsFlagsProvider).detach();
        if(videoRightsRightsContractsAssetsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsAssetsProvider).detach();
        if(videoRightsRightsContractsArrayOfAssetsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsArrayOfAssetsProvider).detach();
        if(videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider).detach();
        if(videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider).detach();
        if(videoRightsRightsContractsDisallowedAssetBundlesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsDisallowedAssetBundlesProvider).detach();
        if(videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider).detach();
        if(videoRightsRightsContractsPackagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsPackagesProvider).detach();
        if(videoRightsRightsContractsArrayOfPackagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsArrayOfPackagesProvider).detach();
        if(videoRightsRightsContractsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsContractsProvider).detach();
        if(videoRightsRightsArrayOfContractsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsArrayOfContractsProvider).detach();
        if(videoRightsRightsWindowsContractIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsWindowsContractIdsProvider).detach();
        if(videoRightsRightsWindowsArrayOfContractIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsWindowsArrayOfContractIdsProvider).detach();
        if(videoRightsRightsWindowsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsWindowsProvider).detach();
        if(videoRightsRightsArrayOfWindowsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsArrayOfWindowsProvider).detach();
        if(videoRightsRightsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsProvider).detach();
        if(videoRightsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsProvider).detach();
        if(videoTypeTypeMediaProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeTypeMediaProvider).detach();
        if(videoTypeTypeArrayOfMediaProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeTypeArrayOfMediaProvider).detach();
        if(videoTypeTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeTypeProvider).detach();
        if(videoTypeArrayOfTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeArrayOfTypeProvider).detach();
        if(videoTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeProvider).detach();
    }

    public AwardsDescriptionTranslatedTextsTypeAPI getAwardsDescriptionTranslatedTextsTypeAPI() {
        return awardsDescriptionTranslatedTextsTypeAPI;
    }
    public AwardsDescriptionTypeAPI getAwardsDescriptionTypeAPI() {
        return awardsDescriptionTypeAPI;
    }
    public CharacterQuotesTypeAPI getCharacterQuotesTypeAPI() {
        return characterQuotesTypeAPI;
    }
    public CharacterArrayOfQuotesTypeAPI getCharacterArrayOfQuotesTypeAPI() {
        return characterArrayOfQuotesTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI;
    }
    public MapKeyTypeAPI getMapKeyTypeAPI() {
        return mapKeyTypeAPI;
    }
    public RolloutLaunchDatesTypeAPI getRolloutLaunchDatesTypeAPI() {
        return rolloutLaunchDatesTypeAPI;
    }
    public RolloutMapOfLaunchDatesTypeAPI getRolloutMapOfLaunchDatesTypeAPI() {
        return rolloutMapOfLaunchDatesTypeAPI;
    }
    public RolloutPhasesElementsArtworkTypeAPI getRolloutPhasesElementsArtworkTypeAPI() {
        return rolloutPhasesElementsArtworkTypeAPI;
    }
    public RolloutPhasesElementsArrayOfArtworkTypeAPI getRolloutPhasesElementsArrayOfArtworkTypeAPI() {
        return rolloutPhasesElementsArrayOfArtworkTypeAPI;
    }
    public RolloutPhasesElementsCastTypeAPI getRolloutPhasesElementsCastTypeAPI() {
        return rolloutPhasesElementsCastTypeAPI;
    }
    public RolloutPhasesElementsArrayOfCastTypeAPI getRolloutPhasesElementsArrayOfCastTypeAPI() {
        return rolloutPhasesElementsArrayOfCastTypeAPI;
    }
    public RolloutPhasesElementsCharactersTypeAPI getRolloutPhasesElementsCharactersTypeAPI() {
        return rolloutPhasesElementsCharactersTypeAPI;
    }
    public RolloutPhasesElementsArrayOfCharactersTypeAPI getRolloutPhasesElementsArrayOfCharactersTypeAPI() {
        return rolloutPhasesElementsArrayOfCharactersTypeAPI;
    }
    public RolloutPhasesWindowsTypeAPI getRolloutPhasesWindowsTypeAPI() {
        return rolloutPhasesWindowsTypeAPI;
    }
    public RolloutPhasesMapOfWindowsTypeAPI getRolloutPhasesMapOfWindowsTypeAPI() {
        return rolloutPhasesMapOfWindowsTypeAPI;
    }
    public StreamProfileGroupsStreamProfileIdsTypeAPI getStreamProfileGroupsStreamProfileIdsTypeAPI() {
        return streamProfileGroupsStreamProfileIdsTypeAPI;
    }
    public StreamProfileGroupsArrayOfStreamProfileIdsTypeAPI getStreamProfileGroupsArrayOfStreamProfileIdsTypeAPI() {
        return streamProfileGroupsArrayOfStreamProfileIdsTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public AltGenresAlternateNamesTranslatedTextsTypeAPI getAltGenresAlternateNamesTranslatedTextsTypeAPI() {
        return altGenresAlternateNamesTranslatedTextsTypeAPI;
    }
    public AltGenresAlternateNamesMapOfTranslatedTextsTypeAPI getAltGenresAlternateNamesMapOfTranslatedTextsTypeAPI() {
        return altGenresAlternateNamesMapOfTranslatedTextsTypeAPI;
    }
    public AltGenresAlternateNamesTypeAPI getAltGenresAlternateNamesTypeAPI() {
        return altGenresAlternateNamesTypeAPI;
    }
    public AltGenresArrayOfAlternateNamesTypeAPI getAltGenresArrayOfAlternateNamesTypeAPI() {
        return altGenresArrayOfAlternateNamesTypeAPI;
    }
    public AltGenresDisplayNameTranslatedTextsTypeAPI getAltGenresDisplayNameTranslatedTextsTypeAPI() {
        return altGenresDisplayNameTranslatedTextsTypeAPI;
    }
    public AltGenresDisplayNameMapOfTranslatedTextsTypeAPI getAltGenresDisplayNameMapOfTranslatedTextsTypeAPI() {
        return altGenresDisplayNameMapOfTranslatedTextsTypeAPI;
    }
    public AltGenresDisplayNameTypeAPI getAltGenresDisplayNameTypeAPI() {
        return altGenresDisplayNameTypeAPI;
    }
    public AltGenresShortNameTranslatedTextsTypeAPI getAltGenresShortNameTranslatedTextsTypeAPI() {
        return altGenresShortNameTranslatedTextsTypeAPI;
    }
    public AltGenresShortNameMapOfTranslatedTextsTypeAPI getAltGenresShortNameMapOfTranslatedTextsTypeAPI() {
        return altGenresShortNameMapOfTranslatedTextsTypeAPI;
    }
    public AltGenresShortNameTypeAPI getAltGenresShortNameTypeAPI() {
        return altGenresShortNameTypeAPI;
    }
    public AltGenresTypeAPI getAltGenresTypeAPI() {
        return altGenresTypeAPI;
    }
    public ArtWorkImageFormatTypeAPI getArtWorkImageFormatTypeAPI() {
        return artWorkImageFormatTypeAPI;
    }
    public ArtWorkImageTypeTypeAPI getArtWorkImageTypeTypeAPI() {
        return artWorkImageTypeTypeAPI;
    }
    public ArtworkRecipeTypeAPI getArtworkRecipeTypeAPI() {
        return artworkRecipeTypeAPI;
    }
    public AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI getAssetMetaDatasTrackLabelsTranslatedTextsTypeAPI() {
        return assetMetaDatasTrackLabelsTranslatedTextsTypeAPI;
    }
    public AssetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI getAssetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI() {
        return assetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI;
    }
    public AssetMetaDatasTrackLabelsTypeAPI getAssetMetaDatasTrackLabelsTypeAPI() {
        return assetMetaDatasTrackLabelsTypeAPI;
    }
    public AssetMetaDatasTypeAPI getAssetMetaDatasTypeAPI() {
        return assetMetaDatasTypeAPI;
    }
    public AwardsAlternateNameTranslatedTextsTypeAPI getAwardsAlternateNameTranslatedTextsTypeAPI() {
        return awardsAlternateNameTranslatedTextsTypeAPI;
    }
    public AwardsAlternateNameMapOfTranslatedTextsTypeAPI getAwardsAlternateNameMapOfTranslatedTextsTypeAPI() {
        return awardsAlternateNameMapOfTranslatedTextsTypeAPI;
    }
    public AwardsAlternateNameTypeAPI getAwardsAlternateNameTypeAPI() {
        return awardsAlternateNameTypeAPI;
    }
    public AwardsAwardNameTranslatedTextsTypeAPI getAwardsAwardNameTranslatedTextsTypeAPI() {
        return awardsAwardNameTranslatedTextsTypeAPI;
    }
    public AwardsAwardNameMapOfTranslatedTextsTypeAPI getAwardsAwardNameMapOfTranslatedTextsTypeAPI() {
        return awardsAwardNameMapOfTranslatedTextsTypeAPI;
    }
    public AwardsAwardNameTypeAPI getAwardsAwardNameTypeAPI() {
        return awardsAwardNameTypeAPI;
    }
    public AwardsTypeAPI getAwardsTypeAPI() {
        return awardsTypeAPI;
    }
    public Bcp47CodeTypeAPI getBcp47CodeTypeAPI() {
        return bcp47CodeTypeAPI;
    }
    public CSMReviewTypeAPI getCSMReviewTypeAPI() {
        return cSMReviewTypeAPI;
    }
    public CacheDeploymentIntentTypeAPI getCacheDeploymentIntentTypeAPI() {
        return cacheDeploymentIntentTypeAPI;
    }
    public CategoriesDisplayNameTranslatedTextsTypeAPI getCategoriesDisplayNameTranslatedTextsTypeAPI() {
        return categoriesDisplayNameTranslatedTextsTypeAPI;
    }
    public CategoriesDisplayNameMapOfTranslatedTextsTypeAPI getCategoriesDisplayNameMapOfTranslatedTextsTypeAPI() {
        return categoriesDisplayNameMapOfTranslatedTextsTypeAPI;
    }
    public CategoriesDisplayNameTypeAPI getCategoriesDisplayNameTypeAPI() {
        return categoriesDisplayNameTypeAPI;
    }
    public CategoriesShortNameTranslatedTextsTypeAPI getCategoriesShortNameTranslatedTextsTypeAPI() {
        return categoriesShortNameTranslatedTextsTypeAPI;
    }
    public CategoriesShortNameMapOfTranslatedTextsTypeAPI getCategoriesShortNameMapOfTranslatedTextsTypeAPI() {
        return categoriesShortNameMapOfTranslatedTextsTypeAPI;
    }
    public CategoriesShortNameTypeAPI getCategoriesShortNameTypeAPI() {
        return categoriesShortNameTypeAPI;
    }
    public CategoriesTypeAPI getCategoriesTypeAPI() {
        return categoriesTypeAPI;
    }
    public CategoryGroupsCategoryGroupNameTranslatedTextsTypeAPI getCategoryGroupsCategoryGroupNameTranslatedTextsTypeAPI() {
        return categoryGroupsCategoryGroupNameTranslatedTextsTypeAPI;
    }
    public CategoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI getCategoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI() {
        return categoryGroupsCategoryGroupNameMapOfTranslatedTextsTypeAPI;
    }
    public CategoryGroupsCategoryGroupNameTypeAPI getCategoryGroupsCategoryGroupNameTypeAPI() {
        return categoryGroupsCategoryGroupNameTypeAPI;
    }
    public CategoryGroupsTypeAPI getCategoryGroupsTypeAPI() {
        return categoryGroupsTypeAPI;
    }
    public CdnsTypeAPI getCdnsTypeAPI() {
        return cdnsTypeAPI;
    }
    public CertificationSystemRatingTypeAPI getCertificationSystemRatingTypeAPI() {
        return certificationSystemRatingTypeAPI;
    }
    public CertificationSystemArrayOfRatingTypeAPI getCertificationSystemArrayOfRatingTypeAPI() {
        return certificationSystemArrayOfRatingTypeAPI;
    }
    public CertificationSystemTypeAPI getCertificationSystemTypeAPI() {
        return certificationSystemTypeAPI;
    }
    public CertificationsDescriptionTranslatedTextsTypeAPI getCertificationsDescriptionTranslatedTextsTypeAPI() {
        return certificationsDescriptionTranslatedTextsTypeAPI;
    }
    public CertificationsDescriptionMapOfTranslatedTextsTypeAPI getCertificationsDescriptionMapOfTranslatedTextsTypeAPI() {
        return certificationsDescriptionMapOfTranslatedTextsTypeAPI;
    }
    public CertificationsDescriptionTypeAPI getCertificationsDescriptionTypeAPI() {
        return certificationsDescriptionTypeAPI;
    }
    public CertificationsNameTranslatedTextsTypeAPI getCertificationsNameTranslatedTextsTypeAPI() {
        return certificationsNameTranslatedTextsTypeAPI;
    }
    public CertificationsNameMapOfTranslatedTextsTypeAPI getCertificationsNameMapOfTranslatedTextsTypeAPI() {
        return certificationsNameMapOfTranslatedTextsTypeAPI;
    }
    public CertificationsNameTypeAPI getCertificationsNameTypeAPI() {
        return certificationsNameTypeAPI;
    }
    public CertificationsTypeAPI getCertificationsTypeAPI() {
        return certificationsTypeAPI;
    }
    public CharacterArtworkAttributesTypeAPI getCharacterArtworkAttributesTypeAPI() {
        return characterArtworkAttributesTypeAPI;
    }
    public CharacterArtworkDerivativesTypeAPI getCharacterArtworkDerivativesTypeAPI() {
        return characterArtworkDerivativesTypeAPI;
    }
    public CharacterArtworkArrayOfDerivativesTypeAPI getCharacterArtworkArrayOfDerivativesTypeAPI() {
        return characterArtworkArrayOfDerivativesTypeAPI;
    }
    public CharacterArtworkLocalesTerritoryCodesTypeAPI getCharacterArtworkLocalesTerritoryCodesTypeAPI() {
        return characterArtworkLocalesTerritoryCodesTypeAPI;
    }
    public CharacterArtworkLocalesArrayOfTerritoryCodesTypeAPI getCharacterArtworkLocalesArrayOfTerritoryCodesTypeAPI() {
        return characterArtworkLocalesArrayOfTerritoryCodesTypeAPI;
    }
    public CharacterArtworkLocalesTypeAPI getCharacterArtworkLocalesTypeAPI() {
        return characterArtworkLocalesTypeAPI;
    }
    public CharacterArtworkArrayOfLocalesTypeAPI getCharacterArtworkArrayOfLocalesTypeAPI() {
        return characterArtworkArrayOfLocalesTypeAPI;
    }
    public CharacterArtworkTypeAPI getCharacterArtworkTypeAPI() {
        return characterArtworkTypeAPI;
    }
    public CharacterElementsTypeAPI getCharacterElementsTypeAPI() {
        return characterElementsTypeAPI;
    }
    public CharacterTypeAPI getCharacterTypeAPI() {
        return characterTypeAPI;
    }
    public CharactersBTranslatedTextsTypeAPI getCharactersBTranslatedTextsTypeAPI() {
        return charactersBTranslatedTextsTypeAPI;
    }
    public CharactersBMapOfTranslatedTextsTypeAPI getCharactersBMapOfTranslatedTextsTypeAPI() {
        return charactersBMapOfTranslatedTextsTypeAPI;
    }
    public CharactersBTypeAPI getCharactersBTypeAPI() {
        return charactersBTypeAPI;
    }
    public CharactersCnTranslatedTextsTypeAPI getCharactersCnTranslatedTextsTypeAPI() {
        return charactersCnTranslatedTextsTypeAPI;
    }
    public CharactersCnMapOfTranslatedTextsTypeAPI getCharactersCnMapOfTranslatedTextsTypeAPI() {
        return charactersCnMapOfTranslatedTextsTypeAPI;
    }
    public CharactersCnTypeAPI getCharactersCnTypeAPI() {
        return charactersCnTypeAPI;
    }
    public CharactersTypeAPI getCharactersTypeAPI() {
        return charactersTypeAPI;
    }
    public ConsolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI getConsolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsDescriptionTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI getConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsDescriptionTypeAPI getConsolidatedCertificationSystemsDescriptionTypeAPI() {
        return consolidatedCertificationSystemsDescriptionTypeAPI;
    }
    public ConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI getConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsNameTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI getConsolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsNameMapOfTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsNameTypeAPI getConsolidatedCertificationSystemsNameTypeAPI() {
        return consolidatedCertificationSystemsNameTypeAPI;
    }
    public ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI getConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI getConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI getConsolidatedCertificationSystemsRatingDescriptionsTypeAPI() {
        return consolidatedCertificationSystemsRatingDescriptionsTypeAPI;
    }
    public ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI getConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI getConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI() {
        return consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI;
    }
    public ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI getConsolidatedCertificationSystemsRatingRatingCodesTypeAPI() {
        return consolidatedCertificationSystemsRatingRatingCodesTypeAPI;
    }
    public ConsolidatedCertificationSystemsRatingTypeAPI getConsolidatedCertificationSystemsRatingTypeAPI() {
        return consolidatedCertificationSystemsRatingTypeAPI;
    }
    public ConsolidatedCertificationSystemsArrayOfRatingTypeAPI getConsolidatedCertificationSystemsArrayOfRatingTypeAPI() {
        return consolidatedCertificationSystemsArrayOfRatingTypeAPI;
    }
    public ConsolidatedCertificationSystemsTypeAPI getConsolidatedCertificationSystemsTypeAPI() {
        return consolidatedCertificationSystemsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryListTypeAPI getConsolidatedVideoRatingsRatingsCountryListTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryListTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI getConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI() {
        return consolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI getConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI getConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI getConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI getConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI() {
        return consolidatedVideoRatingsRatingsCountryRatingsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI getConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI() {
        return consolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI;
    }
    public ConsolidatedVideoRatingsRatingsTypeAPI getConsolidatedVideoRatingsRatingsTypeAPI() {
        return consolidatedVideoRatingsRatingsTypeAPI;
    }
    public ConsolidatedVideoRatingsArrayOfRatingsTypeAPI getConsolidatedVideoRatingsArrayOfRatingsTypeAPI() {
        return consolidatedVideoRatingsArrayOfRatingsTypeAPI;
    }
    public ConsolidatedVideoRatingsTypeAPI getConsolidatedVideoRatingsTypeAPI() {
        return consolidatedVideoRatingsTypeAPI;
    }
    public DefaultExtensionRecipeTypeAPI getDefaultExtensionRecipeTypeAPI() {
        return defaultExtensionRecipeTypeAPI;
    }
    public DeployablePackagesCountryCodesTypeAPI getDeployablePackagesCountryCodesTypeAPI() {
        return deployablePackagesCountryCodesTypeAPI;
    }
    public DeployablePackagesArrayOfCountryCodesTypeAPI getDeployablePackagesArrayOfCountryCodesTypeAPI() {
        return deployablePackagesArrayOfCountryCodesTypeAPI;
    }
    public DeployablePackagesTypeAPI getDeployablePackagesTypeAPI() {
        return deployablePackagesTypeAPI;
    }
    public DrmSystemIdentifiersTypeAPI getDrmSystemIdentifiersTypeAPI() {
        return drmSystemIdentifiersTypeAPI;
    }
    public EpisodesEpisodeNameTranslatedTextsTypeAPI getEpisodesEpisodeNameTranslatedTextsTypeAPI() {
        return episodesEpisodeNameTranslatedTextsTypeAPI;
    }
    public EpisodesEpisodeNameMapOfTranslatedTextsTypeAPI getEpisodesEpisodeNameMapOfTranslatedTextsTypeAPI() {
        return episodesEpisodeNameMapOfTranslatedTextsTypeAPI;
    }
    public EpisodesEpisodeNameTypeAPI getEpisodesEpisodeNameTypeAPI() {
        return episodesEpisodeNameTypeAPI;
    }
    public EpisodesTypeAPI getEpisodesTypeAPI() {
        return episodesTypeAPI;
    }
    public FestivalsCopyrightTranslatedTextsTypeAPI getFestivalsCopyrightTranslatedTextsTypeAPI() {
        return festivalsCopyrightTranslatedTextsTypeAPI;
    }
    public FestivalsCopyrightMapOfTranslatedTextsTypeAPI getFestivalsCopyrightMapOfTranslatedTextsTypeAPI() {
        return festivalsCopyrightMapOfTranslatedTextsTypeAPI;
    }
    public FestivalsCopyrightTypeAPI getFestivalsCopyrightTypeAPI() {
        return festivalsCopyrightTypeAPI;
    }
    public FestivalsDescriptionTranslatedTextsTypeAPI getFestivalsDescriptionTranslatedTextsTypeAPI() {
        return festivalsDescriptionTranslatedTextsTypeAPI;
    }
    public FestivalsDescriptionMapOfTranslatedTextsTypeAPI getFestivalsDescriptionMapOfTranslatedTextsTypeAPI() {
        return festivalsDescriptionMapOfTranslatedTextsTypeAPI;
    }
    public FestivalsDescriptionTypeAPI getFestivalsDescriptionTypeAPI() {
        return festivalsDescriptionTypeAPI;
    }
    public FestivalsFestivalNameTranslatedTextsTypeAPI getFestivalsFestivalNameTranslatedTextsTypeAPI() {
        return festivalsFestivalNameTranslatedTextsTypeAPI;
    }
    public FestivalsFestivalNameMapOfTranslatedTextsTypeAPI getFestivalsFestivalNameMapOfTranslatedTextsTypeAPI() {
        return festivalsFestivalNameMapOfTranslatedTextsTypeAPI;
    }
    public FestivalsFestivalNameTypeAPI getFestivalsFestivalNameTypeAPI() {
        return festivalsFestivalNameTypeAPI;
    }
    public FestivalsShortNameTranslatedTextsTypeAPI getFestivalsShortNameTranslatedTextsTypeAPI() {
        return festivalsShortNameTranslatedTextsTypeAPI;
    }
    public FestivalsShortNameMapOfTranslatedTextsTypeAPI getFestivalsShortNameMapOfTranslatedTextsTypeAPI() {
        return festivalsShortNameMapOfTranslatedTextsTypeAPI;
    }
    public FestivalsShortNameTypeAPI getFestivalsShortNameTypeAPI() {
        return festivalsShortNameTypeAPI;
    }
    public FestivalsSingularNameTranslatedTextsTypeAPI getFestivalsSingularNameTranslatedTextsTypeAPI() {
        return festivalsSingularNameTranslatedTextsTypeAPI;
    }
    public FestivalsSingularNameMapOfTranslatedTextsTypeAPI getFestivalsSingularNameMapOfTranslatedTextsTypeAPI() {
        return festivalsSingularNameMapOfTranslatedTextsTypeAPI;
    }
    public FestivalsSingularNameTypeAPI getFestivalsSingularNameTypeAPI() {
        return festivalsSingularNameTypeAPI;
    }
    public FestivalsTypeAPI getFestivalsTypeAPI() {
        return festivalsTypeAPI;
    }
    public LanguagesNameTranslatedTextsTypeAPI getLanguagesNameTranslatedTextsTypeAPI() {
        return languagesNameTranslatedTextsTypeAPI;
    }
    public LanguagesNameMapOfTranslatedTextsTypeAPI getLanguagesNameMapOfTranslatedTextsTypeAPI() {
        return languagesNameMapOfTranslatedTextsTypeAPI;
    }
    public LanguagesNameTypeAPI getLanguagesNameTypeAPI() {
        return languagesNameTypeAPI;
    }
    public LanguagesTypeAPI getLanguagesTypeAPI() {
        return languagesTypeAPI;
    }
    public LocalizedCharacterTranslatedTextsTypeAPI getLocalizedCharacterTranslatedTextsTypeAPI() {
        return localizedCharacterTranslatedTextsTypeAPI;
    }
    public LocalizedCharacterMapOfTranslatedTextsTypeAPI getLocalizedCharacterMapOfTranslatedTextsTypeAPI() {
        return localizedCharacterMapOfTranslatedTextsTypeAPI;
    }
    public LocalizedCharacterTypeAPI getLocalizedCharacterTypeAPI() {
        return localizedCharacterTypeAPI;
    }
    public LocalizedMetadataTranslatedTextsTypeAPI getLocalizedMetadataTranslatedTextsTypeAPI() {
        return localizedMetadataTranslatedTextsTypeAPI;
    }
    public LocalizedMetadataMapOfTranslatedTextsTypeAPI getLocalizedMetadataMapOfTranslatedTextsTypeAPI() {
        return localizedMetadataMapOfTranslatedTextsTypeAPI;
    }
    public LocalizedMetadataTypeAPI getLocalizedMetadataTypeAPI() {
        return localizedMetadataTypeAPI;
    }
    public MovieRatingsRatingReasonTranslatedTextsTypeAPI getMovieRatingsRatingReasonTranslatedTextsTypeAPI() {
        return movieRatingsRatingReasonTranslatedTextsTypeAPI;
    }
    public MovieRatingsRatingReasonMapOfTranslatedTextsTypeAPI getMovieRatingsRatingReasonMapOfTranslatedTextsTypeAPI() {
        return movieRatingsRatingReasonMapOfTranslatedTextsTypeAPI;
    }
    public MovieRatingsRatingReasonTypeAPI getMovieRatingsRatingReasonTypeAPI() {
        return movieRatingsRatingReasonTypeAPI;
    }
    public MovieRatingsTypeAPI getMovieRatingsTypeAPI() {
        return movieRatingsTypeAPI;
    }
    public MoviesAkaTranslatedTextsTypeAPI getMoviesAkaTranslatedTextsTypeAPI() {
        return moviesAkaTranslatedTextsTypeAPI;
    }
    public MoviesAkaMapOfTranslatedTextsTypeAPI getMoviesAkaMapOfTranslatedTextsTypeAPI() {
        return moviesAkaMapOfTranslatedTextsTypeAPI;
    }
    public MoviesAkaTypeAPI getMoviesAkaTypeAPI() {
        return moviesAkaTypeAPI;
    }
    public MoviesDisplayNameTranslatedTextsTypeAPI getMoviesDisplayNameTranslatedTextsTypeAPI() {
        return moviesDisplayNameTranslatedTextsTypeAPI;
    }
    public MoviesDisplayNameMapOfTranslatedTextsTypeAPI getMoviesDisplayNameMapOfTranslatedTextsTypeAPI() {
        return moviesDisplayNameMapOfTranslatedTextsTypeAPI;
    }
    public MoviesDisplayNameTypeAPI getMoviesDisplayNameTypeAPI() {
        return moviesDisplayNameTypeAPI;
    }
    public MoviesOriginalTitleTranslatedTextsTypeAPI getMoviesOriginalTitleTranslatedTextsTypeAPI() {
        return moviesOriginalTitleTranslatedTextsTypeAPI;
    }
    public MoviesOriginalTitleMapOfTranslatedTextsTypeAPI getMoviesOriginalTitleMapOfTranslatedTextsTypeAPI() {
        return moviesOriginalTitleMapOfTranslatedTextsTypeAPI;
    }
    public MoviesOriginalTitleTypeAPI getMoviesOriginalTitleTypeAPI() {
        return moviesOriginalTitleTypeAPI;
    }
    public MoviesShortDisplayNameTranslatedTextsTypeAPI getMoviesShortDisplayNameTranslatedTextsTypeAPI() {
        return moviesShortDisplayNameTranslatedTextsTypeAPI;
    }
    public MoviesShortDisplayNameMapOfTranslatedTextsTypeAPI getMoviesShortDisplayNameMapOfTranslatedTextsTypeAPI() {
        return moviesShortDisplayNameMapOfTranslatedTextsTypeAPI;
    }
    public MoviesShortDisplayNameTypeAPI getMoviesShortDisplayNameTypeAPI() {
        return moviesShortDisplayNameTypeAPI;
    }
    public MoviesSiteSynopsisTranslatedTextsTypeAPI getMoviesSiteSynopsisTranslatedTextsTypeAPI() {
        return moviesSiteSynopsisTranslatedTextsTypeAPI;
    }
    public MoviesSiteSynopsisMapOfTranslatedTextsTypeAPI getMoviesSiteSynopsisMapOfTranslatedTextsTypeAPI() {
        return moviesSiteSynopsisMapOfTranslatedTextsTypeAPI;
    }
    public MoviesSiteSynopsisTypeAPI getMoviesSiteSynopsisTypeAPI() {
        return moviesSiteSynopsisTypeAPI;
    }
    public MoviesTransliteratedTranslatedTextsTypeAPI getMoviesTransliteratedTranslatedTextsTypeAPI() {
        return moviesTransliteratedTranslatedTextsTypeAPI;
    }
    public MoviesTransliteratedMapOfTranslatedTextsTypeAPI getMoviesTransliteratedMapOfTranslatedTextsTypeAPI() {
        return moviesTransliteratedMapOfTranslatedTextsTypeAPI;
    }
    public MoviesTransliteratedTypeAPI getMoviesTransliteratedTypeAPI() {
        return moviesTransliteratedTypeAPI;
    }
    public MoviesTvSynopsisTranslatedTextsTypeAPI getMoviesTvSynopsisTranslatedTextsTypeAPI() {
        return moviesTvSynopsisTranslatedTextsTypeAPI;
    }
    public MoviesTvSynopsisMapOfTranslatedTextsTypeAPI getMoviesTvSynopsisMapOfTranslatedTextsTypeAPI() {
        return moviesTvSynopsisMapOfTranslatedTextsTypeAPI;
    }
    public MoviesTvSynopsisTypeAPI getMoviesTvSynopsisTypeAPI() {
        return moviesTvSynopsisTypeAPI;
    }
    public MoviesTypeAPI getMoviesTypeAPI() {
        return moviesTypeAPI;
    }
    public OriginServersTypeAPI getOriginServersTypeAPI() {
        return originServersTypeAPI;
    }
    public PersonAliasesNameTranslatedTextsTypeAPI getPersonAliasesNameTranslatedTextsTypeAPI() {
        return personAliasesNameTranslatedTextsTypeAPI;
    }
    public PersonAliasesNameMapOfTranslatedTextsTypeAPI getPersonAliasesNameMapOfTranslatedTextsTypeAPI() {
        return personAliasesNameMapOfTranslatedTextsTypeAPI;
    }
    public PersonAliasesNameTypeAPI getPersonAliasesNameTypeAPI() {
        return personAliasesNameTypeAPI;
    }
    public PersonAliasesTypeAPI getPersonAliasesTypeAPI() {
        return personAliasesTypeAPI;
    }
    public PersonArtworkAttributesTypeAPI getPersonArtworkAttributesTypeAPI() {
        return personArtworkAttributesTypeAPI;
    }
    public PersonArtworkDerivativesTypeAPI getPersonArtworkDerivativesTypeAPI() {
        return personArtworkDerivativesTypeAPI;
    }
    public PersonArtworkArrayOfDerivativesTypeAPI getPersonArtworkArrayOfDerivativesTypeAPI() {
        return personArtworkArrayOfDerivativesTypeAPI;
    }
    public PersonArtworkLocalesTerritoryCodesTypeAPI getPersonArtworkLocalesTerritoryCodesTypeAPI() {
        return personArtworkLocalesTerritoryCodesTypeAPI;
    }
    public PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI getPersonArtworkLocalesArrayOfTerritoryCodesTypeAPI() {
        return personArtworkLocalesArrayOfTerritoryCodesTypeAPI;
    }
    public PersonArtworkLocalesTypeAPI getPersonArtworkLocalesTypeAPI() {
        return personArtworkLocalesTypeAPI;
    }
    public PersonArtworkArrayOfLocalesTypeAPI getPersonArtworkArrayOfLocalesTypeAPI() {
        return personArtworkArrayOfLocalesTypeAPI;
    }
    public PersonArtworkTypeAPI getPersonArtworkTypeAPI() {
        return personArtworkTypeAPI;
    }
    public PersonsBioTranslatedTextsTypeAPI getPersonsBioTranslatedTextsTypeAPI() {
        return personsBioTranslatedTextsTypeAPI;
    }
    public PersonsBioMapOfTranslatedTextsTypeAPI getPersonsBioMapOfTranslatedTextsTypeAPI() {
        return personsBioMapOfTranslatedTextsTypeAPI;
    }
    public PersonsBioTypeAPI getPersonsBioTypeAPI() {
        return personsBioTypeAPI;
    }
    public PersonsNameTranslatedTextsTypeAPI getPersonsNameTranslatedTextsTypeAPI() {
        return personsNameTranslatedTextsTypeAPI;
    }
    public PersonsNameMapOfTranslatedTextsTypeAPI getPersonsNameMapOfTranslatedTextsTypeAPI() {
        return personsNameMapOfTranslatedTextsTypeAPI;
    }
    public PersonsNameTypeAPI getPersonsNameTypeAPI() {
        return personsNameTypeAPI;
    }
    public PersonsTypeAPI getPersonsTypeAPI() {
        return personsTypeAPI;
    }
    public ProtectionTypesTypeAPI getProtectionTypesTypeAPI() {
        return protectionTypesTypeAPI;
    }
    public RatingsDescriptionTranslatedTextsTypeAPI getRatingsDescriptionTranslatedTextsTypeAPI() {
        return ratingsDescriptionTranslatedTextsTypeAPI;
    }
    public RatingsDescriptionMapOfTranslatedTextsTypeAPI getRatingsDescriptionMapOfTranslatedTextsTypeAPI() {
        return ratingsDescriptionMapOfTranslatedTextsTypeAPI;
    }
    public RatingsDescriptionTypeAPI getRatingsDescriptionTypeAPI() {
        return ratingsDescriptionTypeAPI;
    }
    public RatingsRatingCodeTranslatedTextsTypeAPI getRatingsRatingCodeTranslatedTextsTypeAPI() {
        return ratingsRatingCodeTranslatedTextsTypeAPI;
    }
    public RatingsRatingCodeMapOfTranslatedTextsTypeAPI getRatingsRatingCodeMapOfTranslatedTextsTypeAPI() {
        return ratingsRatingCodeMapOfTranslatedTextsTypeAPI;
    }
    public RatingsRatingCodeTypeAPI getRatingsRatingCodeTypeAPI() {
        return ratingsRatingCodeTypeAPI;
    }
    public RatingsTypeAPI getRatingsTypeAPI() {
        return ratingsTypeAPI;
    }
    public RolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI getRolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI() {
        return rolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI;
    }
    public RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI getRolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI() {
        return rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI;
    }
    public RolloutPhasesElementsArtwork_newTypeAPI getRolloutPhasesElementsArtwork_newTypeAPI() {
        return rolloutPhasesElementsArtwork_newTypeAPI;
    }
    public RolloutPhasesElementsLocalized_metadataTypeAPI getRolloutPhasesElementsLocalized_metadataTypeAPI() {
        return rolloutPhasesElementsLocalized_metadataTypeAPI;
    }
    public RolloutPhasesElementsTrailersSupplementalInfoTypeAPI getRolloutPhasesElementsTrailersSupplementalInfoTypeAPI() {
        return rolloutPhasesElementsTrailersSupplementalInfoTypeAPI;
    }
    public RolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI getRolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI() {
        return rolloutPhasesElementsTrailersMapOfSupplementalInfoTypeAPI;
    }
    public RolloutPhasesElementsTrailersTypeAPI getRolloutPhasesElementsTrailersTypeAPI() {
        return rolloutPhasesElementsTrailersTypeAPI;
    }
    public RolloutPhasesElementsArrayOfTrailersTypeAPI getRolloutPhasesElementsArrayOfTrailersTypeAPI() {
        return rolloutPhasesElementsArrayOfTrailersTypeAPI;
    }
    public RolloutPhasesElementsTypeAPI getRolloutPhasesElementsTypeAPI() {
        return rolloutPhasesElementsTypeAPI;
    }
    public RolloutPhasesTypeAPI getRolloutPhasesTypeAPI() {
        return rolloutPhasesTypeAPI;
    }
    public RolloutArrayOfPhasesTypeAPI getRolloutArrayOfPhasesTypeAPI() {
        return rolloutArrayOfPhasesTypeAPI;
    }
    public RolloutTypeAPI getRolloutTypeAPI() {
        return rolloutTypeAPI;
    }
    public ShowMemberTypesDisplayNameTranslatedTextsTypeAPI getShowMemberTypesDisplayNameTranslatedTextsTypeAPI() {
        return showMemberTypesDisplayNameTranslatedTextsTypeAPI;
    }
    public ShowMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI getShowMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI() {
        return showMemberTypesDisplayNameMapOfTranslatedTextsTypeAPI;
    }
    public ShowMemberTypesDisplayNameTypeAPI getShowMemberTypesDisplayNameTypeAPI() {
        return showMemberTypesDisplayNameTypeAPI;
    }
    public ShowMemberTypesTypeAPI getShowMemberTypesTypeAPI() {
        return showMemberTypesTypeAPI;
    }
    public StorageGroupsCountriesTypeAPI getStorageGroupsCountriesTypeAPI() {
        return storageGroupsCountriesTypeAPI;
    }
    public StorageGroupsArrayOfCountriesTypeAPI getStorageGroupsArrayOfCountriesTypeAPI() {
        return storageGroupsArrayOfCountriesTypeAPI;
    }
    public StorageGroupsTypeAPI getStorageGroupsTypeAPI() {
        return storageGroupsTypeAPI;
    }
    public Stories_SynopsesHooksTranslatedTextsTypeAPI getStories_SynopsesHooksTranslatedTextsTypeAPI() {
        return stories_SynopsesHooksTranslatedTextsTypeAPI;
    }
    public Stories_SynopsesHooksMapOfTranslatedTextsTypeAPI getStories_SynopsesHooksMapOfTranslatedTextsTypeAPI() {
        return stories_SynopsesHooksMapOfTranslatedTextsTypeAPI;
    }
    public Stories_SynopsesHooksTypeAPI getStories_SynopsesHooksTypeAPI() {
        return stories_SynopsesHooksTypeAPI;
    }
    public Stories_SynopsesArrayOfHooksTypeAPI getStories_SynopsesArrayOfHooksTypeAPI() {
        return stories_SynopsesArrayOfHooksTypeAPI;
    }
    public Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI getStories_SynopsesNarrativeTextTranslatedTextsTypeAPI() {
        return stories_SynopsesNarrativeTextTranslatedTextsTypeAPI;
    }
    public Stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI getStories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI() {
        return stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI;
    }
    public Stories_SynopsesNarrativeTextTypeAPI getStories_SynopsesNarrativeTextTypeAPI() {
        return stories_SynopsesNarrativeTextTypeAPI;
    }
    public Stories_SynopsesTypeAPI getStories_SynopsesTypeAPI() {
        return stories_SynopsesTypeAPI;
    }
    public StreamProfileGroupsTypeAPI getStreamProfileGroupsTypeAPI() {
        return streamProfileGroupsTypeAPI;
    }
    public StreamProfilesTypeAPI getStreamProfilesTypeAPI() {
        return streamProfilesTypeAPI;
    }
    public TerritoryCountriesCountryCodesTypeAPI getTerritoryCountriesCountryCodesTypeAPI() {
        return territoryCountriesCountryCodesTypeAPI;
    }
    public TerritoryCountriesArrayOfCountryCodesTypeAPI getTerritoryCountriesArrayOfCountryCodesTypeAPI() {
        return territoryCountriesArrayOfCountryCodesTypeAPI;
    }
    public TerritoryCountriesTypeAPI getTerritoryCountriesTypeAPI() {
        return territoryCountriesTypeAPI;
    }
    public TopNAttributesTypeAPI getTopNAttributesTypeAPI() {
        return topNAttributesTypeAPI;
    }
    public TopNArrayOfAttributesTypeAPI getTopNArrayOfAttributesTypeAPI() {
        return topNArrayOfAttributesTypeAPI;
    }
    public TopNTypeAPI getTopNTypeAPI() {
        return topNTypeAPI;
    }
    public TrailerTrailersThemesTypeAPI getTrailerTrailersThemesTypeAPI() {
        return trailerTrailersThemesTypeAPI;
    }
    public TrailerTrailersArrayOfThemesTypeAPI getTrailerTrailersArrayOfThemesTypeAPI() {
        return trailerTrailersArrayOfThemesTypeAPI;
    }
    public TrailerTrailersTypeAPI getTrailerTrailersTypeAPI() {
        return trailerTrailersTypeAPI;
    }
    public TrailerArrayOfTrailersTypeAPI getTrailerArrayOfTrailersTypeAPI() {
        return trailerArrayOfTrailersTypeAPI;
    }
    public TrailerTypeAPI getTrailerTypeAPI() {
        return trailerTypeAPI;
    }
    public VMSAwardTypeAPI getVMSAwardTypeAPI() {
        return vMSAwardTypeAPI;
    }
    public VideoArtWorkAttributesTypeAPI getVideoArtWorkAttributesTypeAPI() {
        return videoArtWorkAttributesTypeAPI;
    }
    public VideoArtWorkArrayOfAttributesTypeAPI getVideoArtWorkArrayOfAttributesTypeAPI() {
        return videoArtWorkArrayOfAttributesTypeAPI;
    }
    public VideoArtWorkExtensionsTypeAPI getVideoArtWorkExtensionsTypeAPI() {
        return videoArtWorkExtensionsTypeAPI;
    }
    public VideoArtWorkArrayOfExtensionsTypeAPI getVideoArtWorkArrayOfExtensionsTypeAPI() {
        return videoArtWorkArrayOfExtensionsTypeAPI;
    }
    public VideoArtWorkLocalesTerritoryCodesTypeAPI getVideoArtWorkLocalesTerritoryCodesTypeAPI() {
        return videoArtWorkLocalesTerritoryCodesTypeAPI;
    }
    public VideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI getVideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI() {
        return videoArtWorkLocalesArrayOfTerritoryCodesTypeAPI;
    }
    public VideoArtWorkLocalesTypeAPI getVideoArtWorkLocalesTypeAPI() {
        return videoArtWorkLocalesTypeAPI;
    }
    public VideoArtWorkArrayOfLocalesTypeAPI getVideoArtWorkArrayOfLocalesTypeAPI() {
        return videoArtWorkArrayOfLocalesTypeAPI;
    }
    public VideoArtWorkRecipesTypeAPI getVideoArtWorkRecipesTypeAPI() {
        return videoArtWorkRecipesTypeAPI;
    }
    public VideoArtWorkArrayOfRecipesTypeAPI getVideoArtWorkArrayOfRecipesTypeAPI() {
        return videoArtWorkArrayOfRecipesTypeAPI;
    }
    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI getVideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI() {
        return videoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI;
    }
    public VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI getVideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI() {
        return videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI;
    }
    public VideoArtWorkSourceAttributesIDENTIFIERSTypeAPI getVideoArtWorkSourceAttributesIDENTIFIERSTypeAPI() {
        return videoArtWorkSourceAttributesIDENTIFIERSTypeAPI;
    }
    public VideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI getVideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI() {
        return videoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI;
    }
    public VideoArtWorkSourceAttributesPERSON_IDSTypeAPI getVideoArtWorkSourceAttributesPERSON_IDSTypeAPI() {
        return videoArtWorkSourceAttributesPERSON_IDSTypeAPI;
    }
    public VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI getVideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI() {
        return videoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI;
    }
    public VideoArtWorkSourceAttributesThemesTypeAPI getVideoArtWorkSourceAttributesThemesTypeAPI() {
        return videoArtWorkSourceAttributesThemesTypeAPI;
    }
    public VideoArtWorkSourceAttributesArrayOfThemesTypeAPI getVideoArtWorkSourceAttributesArrayOfThemesTypeAPI() {
        return videoArtWorkSourceAttributesArrayOfThemesTypeAPI;
    }
    public VideoArtWorkSourceAttributesTypeAPI getVideoArtWorkSourceAttributesTypeAPI() {
        return videoArtWorkSourceAttributesTypeAPI;
    }
    public VideoArtWorkTypeAPI getVideoArtWorkTypeAPI() {
        return videoArtWorkTypeAPI;
    }
    public VideoAwardAwardTypeAPI getVideoAwardAwardTypeAPI() {
        return videoAwardAwardTypeAPI;
    }
    public VideoAwardArrayOfAwardTypeAPI getVideoAwardArrayOfAwardTypeAPI() {
        return videoAwardArrayOfAwardTypeAPI;
    }
    public VideoAwardTypeAPI getVideoAwardTypeAPI() {
        return videoAwardTypeAPI;
    }
    public VideoDateWindowTypeAPI getVideoDateWindowTypeAPI() {
        return videoDateWindowTypeAPI;
    }
    public VideoDateArrayOfWindowTypeAPI getVideoDateArrayOfWindowTypeAPI() {
        return videoDateArrayOfWindowTypeAPI;
    }
    public VideoDateTypeAPI getVideoDateTypeAPI() {
        return videoDateTypeAPI;
    }
    public VideoDisplaySetSetsChildrenChildrenTypeAPI getVideoDisplaySetSetsChildrenChildrenTypeAPI() {
        return videoDisplaySetSetsChildrenChildrenTypeAPI;
    }
    public VideoDisplaySetSetsChildrenArrayOfChildrenTypeAPI getVideoDisplaySetSetsChildrenArrayOfChildrenTypeAPI() {
        return videoDisplaySetSetsChildrenArrayOfChildrenTypeAPI;
    }
    public VideoDisplaySetSetsChildrenTypeAPI getVideoDisplaySetSetsChildrenTypeAPI() {
        return videoDisplaySetSetsChildrenTypeAPI;
    }
    public VideoDisplaySetSetsArrayOfChildrenTypeAPI getVideoDisplaySetSetsArrayOfChildrenTypeAPI() {
        return videoDisplaySetSetsArrayOfChildrenTypeAPI;
    }
    public VideoDisplaySetSetsTypeAPI getVideoDisplaySetSetsTypeAPI() {
        return videoDisplaySetSetsTypeAPI;
    }
    public VideoDisplaySetArrayOfSetsTypeAPI getVideoDisplaySetArrayOfSetsTypeAPI() {
        return videoDisplaySetArrayOfSetsTypeAPI;
    }
    public VideoDisplaySetTypeAPI getVideoDisplaySetTypeAPI() {
        return videoDisplaySetTypeAPI;
    }
    public VideoGeneralAliasesTypeAPI getVideoGeneralAliasesTypeAPI() {
        return videoGeneralAliasesTypeAPI;
    }
    public VideoGeneralArrayOfAliasesTypeAPI getVideoGeneralArrayOfAliasesTypeAPI() {
        return videoGeneralArrayOfAliasesTypeAPI;
    }
    public VideoGeneralEpisodeTypesTypeAPI getVideoGeneralEpisodeTypesTypeAPI() {
        return videoGeneralEpisodeTypesTypeAPI;
    }
    public VideoGeneralArrayOfEpisodeTypesTypeAPI getVideoGeneralArrayOfEpisodeTypesTypeAPI() {
        return videoGeneralArrayOfEpisodeTypesTypeAPI;
    }
    public VideoGeneralTitleTypesTypeAPI getVideoGeneralTitleTypesTypeAPI() {
        return videoGeneralTitleTypesTypeAPI;
    }
    public VideoGeneralArrayOfTitleTypesTypeAPI getVideoGeneralArrayOfTitleTypesTypeAPI() {
        return videoGeneralArrayOfTitleTypesTypeAPI;
    }
    public VideoGeneralTypeAPI getVideoGeneralTypeAPI() {
        return videoGeneralTypeAPI;
    }
    public VideoPersonAliasTypeAPI getVideoPersonAliasTypeAPI() {
        return videoPersonAliasTypeAPI;
    }
    public VideoPersonArrayOfAliasTypeAPI getVideoPersonArrayOfAliasTypeAPI() {
        return videoPersonArrayOfAliasTypeAPI;
    }
    public VideoPersonCastTypeAPI getVideoPersonCastTypeAPI() {
        return videoPersonCastTypeAPI;
    }
    public VideoPersonArrayOfCastTypeAPI getVideoPersonArrayOfCastTypeAPI() {
        return videoPersonArrayOfCastTypeAPI;
    }
    public VideoPersonTypeAPI getVideoPersonTypeAPI() {
        return videoPersonTypeAPI;
    }
    public VideoRatingRatingReasonIdsTypeAPI getVideoRatingRatingReasonIdsTypeAPI() {
        return videoRatingRatingReasonIdsTypeAPI;
    }
    public VideoRatingRatingReasonArrayOfIdsTypeAPI getVideoRatingRatingReasonArrayOfIdsTypeAPI() {
        return videoRatingRatingReasonArrayOfIdsTypeAPI;
    }
    public VideoRatingRatingReasonTypeAPI getVideoRatingRatingReasonTypeAPI() {
        return videoRatingRatingReasonTypeAPI;
    }
    public VideoRatingRatingTypeAPI getVideoRatingRatingTypeAPI() {
        return videoRatingRatingTypeAPI;
    }
    public VideoRatingArrayOfRatingTypeAPI getVideoRatingArrayOfRatingTypeAPI() {
        return videoRatingArrayOfRatingTypeAPI;
    }
    public VideoRatingTypeAPI getVideoRatingTypeAPI() {
        return videoRatingTypeAPI;
    }
    public VideoRightsFlagsFirstDisplayDatesTypeAPI getVideoRightsFlagsFirstDisplayDatesTypeAPI() {
        return videoRightsFlagsFirstDisplayDatesTypeAPI;
    }
    public VideoRightsFlagsMapOfFirstDisplayDatesTypeAPI getVideoRightsFlagsMapOfFirstDisplayDatesTypeAPI() {
        return videoRightsFlagsMapOfFirstDisplayDatesTypeAPI;
    }
    public VideoRightsFlagsTypeAPI getVideoRightsFlagsTypeAPI() {
        return videoRightsFlagsTypeAPI;
    }
    public VideoRightsRightsContractsAssetsTypeAPI getVideoRightsRightsContractsAssetsTypeAPI() {
        return videoRightsRightsContractsAssetsTypeAPI;
    }
    public VideoRightsRightsContractsArrayOfAssetsTypeAPI getVideoRightsRightsContractsArrayOfAssetsTypeAPI() {
        return videoRightsRightsContractsArrayOfAssetsTypeAPI;
    }
    public VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI getVideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI() {
        return videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesTypeAPI;
    }
    public VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI getVideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI() {
        return videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI;
    }
    public VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI getVideoRightsRightsContractsDisallowedAssetBundlesTypeAPI() {
        return videoRightsRightsContractsDisallowedAssetBundlesTypeAPI;
    }
    public VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI getVideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI() {
        return videoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI;
    }
    public VideoRightsRightsContractsPackagesTypeAPI getVideoRightsRightsContractsPackagesTypeAPI() {
        return videoRightsRightsContractsPackagesTypeAPI;
    }
    public VideoRightsRightsContractsArrayOfPackagesTypeAPI getVideoRightsRightsContractsArrayOfPackagesTypeAPI() {
        return videoRightsRightsContractsArrayOfPackagesTypeAPI;
    }
    public VideoRightsRightsContractsTypeAPI getVideoRightsRightsContractsTypeAPI() {
        return videoRightsRightsContractsTypeAPI;
    }
    public VideoRightsRightsArrayOfContractsTypeAPI getVideoRightsRightsArrayOfContractsTypeAPI() {
        return videoRightsRightsArrayOfContractsTypeAPI;
    }
    public VideoRightsRightsWindowsContractIdsTypeAPI getVideoRightsRightsWindowsContractIdsTypeAPI() {
        return videoRightsRightsWindowsContractIdsTypeAPI;
    }
    public VideoRightsRightsWindowsArrayOfContractIdsTypeAPI getVideoRightsRightsWindowsArrayOfContractIdsTypeAPI() {
        return videoRightsRightsWindowsArrayOfContractIdsTypeAPI;
    }
    public VideoRightsRightsWindowsTypeAPI getVideoRightsRightsWindowsTypeAPI() {
        return videoRightsRightsWindowsTypeAPI;
    }
    public VideoRightsRightsArrayOfWindowsTypeAPI getVideoRightsRightsArrayOfWindowsTypeAPI() {
        return videoRightsRightsArrayOfWindowsTypeAPI;
    }
    public VideoRightsRightsTypeAPI getVideoRightsRightsTypeAPI() {
        return videoRightsRightsTypeAPI;
    }
    public VideoRightsTypeAPI getVideoRightsTypeAPI() {
        return videoRightsTypeAPI;
    }
    public VideoTypeTypeMediaTypeAPI getVideoTypeTypeMediaTypeAPI() {
        return videoTypeTypeMediaTypeAPI;
    }
    public VideoTypeTypeArrayOfMediaTypeAPI getVideoTypeTypeArrayOfMediaTypeAPI() {
        return videoTypeTypeArrayOfMediaTypeAPI;
    }
    public VideoTypeTypeTypeAPI getVideoTypeTypeTypeAPI() {
        return videoTypeTypeTypeAPI;
    }
    public VideoTypeArrayOfTypeTypeAPI getVideoTypeArrayOfTypeTypeAPI() {
        return videoTypeArrayOfTypeTypeAPI;
    }
    public VideoTypeTypeAPI getVideoTypeTypeAPI() {
        return videoTypeTypeAPI;
    }
    public Collection<AwardsDescriptionTranslatedTextsHollow> getAllAwardsDescriptionTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AwardsDescriptionTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AwardsDescriptionTranslatedTexts").getTypeState()) {
            protected AwardsDescriptionTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAwardsDescriptionTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AwardsDescriptionTranslatedTextsHollow getAwardsDescriptionTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (AwardsDescriptionTranslatedTextsHollow)awardsDescriptionTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsDescriptionHollow> getAllAwardsDescriptionHollow() {
        return new AllHollowRecordCollection<AwardsDescriptionHollow>(getDataAccess().getTypeDataAccess("AwardsDescription").getTypeState()) {
            protected AwardsDescriptionHollow getForOrdinal(int ordinal) {
                return getAwardsDescriptionHollow(ordinal);
            }
        };
    }
    public AwardsDescriptionHollow getAwardsDescriptionHollow(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (AwardsDescriptionHollow)awardsDescriptionProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterQuotesHollow> getAllCharacterQuotesHollow() {
        return new AllHollowRecordCollection<CharacterQuotesHollow>(getDataAccess().getTypeDataAccess("CharacterQuotes").getTypeState()) {
            protected CharacterQuotesHollow getForOrdinal(int ordinal) {
                return getCharacterQuotesHollow(ordinal);
            }
        };
    }
    public CharacterQuotesHollow getCharacterQuotesHollow(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (CharacterQuotesHollow)characterQuotesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArrayOfQuotesHollow> getAllCharacterArrayOfQuotesHollow() {
        return new AllHollowRecordCollection<CharacterArrayOfQuotesHollow>(getDataAccess().getTypeDataAccess("CharacterArrayOfQuotes").getTypeState()) {
            protected CharacterArrayOfQuotesHollow getForOrdinal(int ordinal) {
                return getCharacterArrayOfQuotesHollow(ordinal);
            }
        };
    }
    public CharacterArrayOfQuotesHollow getCharacterArrayOfQuotesHollow(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (CharacterArrayOfQuotesHollow)characterArrayOfQuotesProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow> getAllConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIds").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow)consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow> getAllConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIds").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow)consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow> getAllConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsAdvisories").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow)consolidatedVideoRatingsRatingsCountryRatingsAdvisoriesProvider.getHollowObject(ordinal);
    }
    public Collection<MapKeyHollow> getAllMapKeyHollow() {
        return new AllHollowRecordCollection<MapKeyHollow>(getDataAccess().getTypeDataAccess("MapKey").getTypeState()) {
            protected MapKeyHollow getForOrdinal(int ordinal) {
                return getMapKeyHollow(ordinal);
            }
        };
    }
    public MapKeyHollow getMapKeyHollow(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (MapKeyHollow)mapKeyProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutLaunchDatesHollow> getAllRolloutLaunchDatesHollow() {
        return new AllHollowRecordCollection<RolloutLaunchDatesHollow>(getDataAccess().getTypeDataAccess("RolloutLaunchDates").getTypeState()) {
            protected RolloutLaunchDatesHollow getForOrdinal(int ordinal) {
                return getRolloutLaunchDatesHollow(ordinal);
            }
        };
    }
    public RolloutLaunchDatesHollow getRolloutLaunchDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(8);
        return (RolloutLaunchDatesHollow)rolloutLaunchDatesProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutMapOfLaunchDatesHollow> getAllRolloutMapOfLaunchDatesHollow() {
        return new AllHollowRecordCollection<RolloutMapOfLaunchDatesHollow>(getDataAccess().getTypeDataAccess("RolloutMapOfLaunchDates").getTypeState()) {
            protected RolloutMapOfLaunchDatesHollow getForOrdinal(int ordinal) {
                return getRolloutMapOfLaunchDatesHollow(ordinal);
            }
        };
    }
    public RolloutMapOfLaunchDatesHollow getRolloutMapOfLaunchDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(9);
        return (RolloutMapOfLaunchDatesHollow)rolloutMapOfLaunchDatesProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArtworkHollow> getAllRolloutPhasesElementsArtworkHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArtworkHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArtwork").getTypeState()) {
            protected RolloutPhasesElementsArtworkHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArtworkHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArtworkHollow getRolloutPhasesElementsArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(10);
        return (RolloutPhasesElementsArtworkHollow)rolloutPhasesElementsArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArrayOfArtworkHollow> getAllRolloutPhasesElementsArrayOfArtworkHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArrayOfArtworkHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArrayOfArtwork").getTypeState()) {
            protected RolloutPhasesElementsArrayOfArtworkHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArrayOfArtworkHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArrayOfArtworkHollow getRolloutPhasesElementsArrayOfArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(11);
        return (RolloutPhasesElementsArrayOfArtworkHollow)rolloutPhasesElementsArrayOfArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsCastHollow> getAllRolloutPhasesElementsCastHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsCastHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsCast").getTypeState()) {
            protected RolloutPhasesElementsCastHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsCastHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsCastHollow getRolloutPhasesElementsCastHollow(int ordinal) {
        objectCreationSampler.recordCreation(12);
        return (RolloutPhasesElementsCastHollow)rolloutPhasesElementsCastProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArrayOfCastHollow> getAllRolloutPhasesElementsArrayOfCastHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArrayOfCastHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArrayOfCast").getTypeState()) {
            protected RolloutPhasesElementsArrayOfCastHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArrayOfCastHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArrayOfCastHollow getRolloutPhasesElementsArrayOfCastHollow(int ordinal) {
        objectCreationSampler.recordCreation(13);
        return (RolloutPhasesElementsArrayOfCastHollow)rolloutPhasesElementsArrayOfCastProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsCharactersHollow> getAllRolloutPhasesElementsCharactersHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsCharactersHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsCharacters").getTypeState()) {
            protected RolloutPhasesElementsCharactersHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsCharactersHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsCharactersHollow getRolloutPhasesElementsCharactersHollow(int ordinal) {
        objectCreationSampler.recordCreation(14);
        return (RolloutPhasesElementsCharactersHollow)rolloutPhasesElementsCharactersProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArrayOfCharactersHollow> getAllRolloutPhasesElementsArrayOfCharactersHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArrayOfCharactersHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArrayOfCharacters").getTypeState()) {
            protected RolloutPhasesElementsArrayOfCharactersHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArrayOfCharactersHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArrayOfCharactersHollow getRolloutPhasesElementsArrayOfCharactersHollow(int ordinal) {
        objectCreationSampler.recordCreation(15);
        return (RolloutPhasesElementsArrayOfCharactersHollow)rolloutPhasesElementsArrayOfCharactersProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesWindowsHollow> getAllRolloutPhasesWindowsHollow() {
        return new AllHollowRecordCollection<RolloutPhasesWindowsHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesWindows").getTypeState()) {
            protected RolloutPhasesWindowsHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesWindowsHollow(ordinal);
            }
        };
    }
    public RolloutPhasesWindowsHollow getRolloutPhasesWindowsHollow(int ordinal) {
        objectCreationSampler.recordCreation(16);
        return (RolloutPhasesWindowsHollow)rolloutPhasesWindowsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesMapOfWindowsHollow> getAllRolloutPhasesMapOfWindowsHollow() {
        return new AllHollowRecordCollection<RolloutPhasesMapOfWindowsHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesMapOfWindows").getTypeState()) {
            protected RolloutPhasesMapOfWindowsHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesMapOfWindowsHollow(ordinal);
            }
        };
    }
    public RolloutPhasesMapOfWindowsHollow getRolloutPhasesMapOfWindowsHollow(int ordinal) {
        objectCreationSampler.recordCreation(17);
        return (RolloutPhasesMapOfWindowsHollow)rolloutPhasesMapOfWindowsProvider.getHollowObject(ordinal);
    }
    public Collection<StreamProfileGroupsStreamProfileIdsHollow> getAllStreamProfileGroupsStreamProfileIdsHollow() {
        return new AllHollowRecordCollection<StreamProfileGroupsStreamProfileIdsHollow>(getDataAccess().getTypeDataAccess("StreamProfileGroupsStreamProfileIds").getTypeState()) {
            protected StreamProfileGroupsStreamProfileIdsHollow getForOrdinal(int ordinal) {
                return getStreamProfileGroupsStreamProfileIdsHollow(ordinal);
            }
        };
    }
    public StreamProfileGroupsStreamProfileIdsHollow getStreamProfileGroupsStreamProfileIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(18);
        return (StreamProfileGroupsStreamProfileIdsHollow)streamProfileGroupsStreamProfileIdsProvider.getHollowObject(ordinal);
    }
    public Collection<StreamProfileGroupsArrayOfStreamProfileIdsHollow> getAllStreamProfileGroupsArrayOfStreamProfileIdsHollow() {
        return new AllHollowRecordCollection<StreamProfileGroupsArrayOfStreamProfileIdsHollow>(getDataAccess().getTypeDataAccess("StreamProfileGroupsArrayOfStreamProfileIds").getTypeState()) {
            protected StreamProfileGroupsArrayOfStreamProfileIdsHollow getForOrdinal(int ordinal) {
                return getStreamProfileGroupsArrayOfStreamProfileIdsHollow(ordinal);
            }
        };
    }
    public StreamProfileGroupsArrayOfStreamProfileIdsHollow getStreamProfileGroupsArrayOfStreamProfileIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(19);
        return (StreamProfileGroupsArrayOfStreamProfileIdsHollow)streamProfileGroupsArrayOfStreamProfileIdsProvider.getHollowObject(ordinal);
    }
    public Collection<StringHollow> getAllStringHollow() {
        return new AllHollowRecordCollection<StringHollow>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected StringHollow getForOrdinal(int ordinal) {
                return getStringHollow(ordinal);
            }
        };
    }
    public StringHollow getStringHollow(int ordinal) {
        objectCreationSampler.recordCreation(20);
        return (StringHollow)stringProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresAlternateNamesTranslatedTextsHollow> getAllAltGenresAlternateNamesTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AltGenresAlternateNamesTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AltGenresAlternateNamesTranslatedTexts").getTypeState()) {
            protected AltGenresAlternateNamesTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAltGenresAlternateNamesTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AltGenresAlternateNamesTranslatedTextsHollow getAltGenresAlternateNamesTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(21);
        return (AltGenresAlternateNamesTranslatedTextsHollow)altGenresAlternateNamesTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresAlternateNamesMapOfTranslatedTextsHollow> getAllAltGenresAlternateNamesMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AltGenresAlternateNamesMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AltGenresAlternateNamesMapOfTranslatedTexts").getTypeState()) {
            protected AltGenresAlternateNamesMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAltGenresAlternateNamesMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AltGenresAlternateNamesMapOfTranslatedTextsHollow getAltGenresAlternateNamesMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(22);
        return (AltGenresAlternateNamesMapOfTranslatedTextsHollow)altGenresAlternateNamesMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresAlternateNamesHollow> getAllAltGenresAlternateNamesHollow() {
        return new AllHollowRecordCollection<AltGenresAlternateNamesHollow>(getDataAccess().getTypeDataAccess("AltGenresAlternateNames").getTypeState()) {
            protected AltGenresAlternateNamesHollow getForOrdinal(int ordinal) {
                return getAltGenresAlternateNamesHollow(ordinal);
            }
        };
    }
    public AltGenresAlternateNamesHollow getAltGenresAlternateNamesHollow(int ordinal) {
        objectCreationSampler.recordCreation(23);
        return (AltGenresAlternateNamesHollow)altGenresAlternateNamesProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresArrayOfAlternateNamesHollow> getAllAltGenresArrayOfAlternateNamesHollow() {
        return new AllHollowRecordCollection<AltGenresArrayOfAlternateNamesHollow>(getDataAccess().getTypeDataAccess("AltGenresArrayOfAlternateNames").getTypeState()) {
            protected AltGenresArrayOfAlternateNamesHollow getForOrdinal(int ordinal) {
                return getAltGenresArrayOfAlternateNamesHollow(ordinal);
            }
        };
    }
    public AltGenresArrayOfAlternateNamesHollow getAltGenresArrayOfAlternateNamesHollow(int ordinal) {
        objectCreationSampler.recordCreation(24);
        return (AltGenresArrayOfAlternateNamesHollow)altGenresArrayOfAlternateNamesProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresDisplayNameTranslatedTextsHollow> getAllAltGenresDisplayNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AltGenresDisplayNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AltGenresDisplayNameTranslatedTexts").getTypeState()) {
            protected AltGenresDisplayNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAltGenresDisplayNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AltGenresDisplayNameTranslatedTextsHollow getAltGenresDisplayNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(25);
        return (AltGenresDisplayNameTranslatedTextsHollow)altGenresDisplayNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresDisplayNameMapOfTranslatedTextsHollow> getAllAltGenresDisplayNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AltGenresDisplayNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AltGenresDisplayNameMapOfTranslatedTexts").getTypeState()) {
            protected AltGenresDisplayNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAltGenresDisplayNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AltGenresDisplayNameMapOfTranslatedTextsHollow getAltGenresDisplayNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(26);
        return (AltGenresDisplayNameMapOfTranslatedTextsHollow)altGenresDisplayNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresDisplayNameHollow> getAllAltGenresDisplayNameHollow() {
        return new AllHollowRecordCollection<AltGenresDisplayNameHollow>(getDataAccess().getTypeDataAccess("AltGenresDisplayName").getTypeState()) {
            protected AltGenresDisplayNameHollow getForOrdinal(int ordinal) {
                return getAltGenresDisplayNameHollow(ordinal);
            }
        };
    }
    public AltGenresDisplayNameHollow getAltGenresDisplayNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(27);
        return (AltGenresDisplayNameHollow)altGenresDisplayNameProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresShortNameTranslatedTextsHollow> getAllAltGenresShortNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AltGenresShortNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AltGenresShortNameTranslatedTexts").getTypeState()) {
            protected AltGenresShortNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAltGenresShortNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AltGenresShortNameTranslatedTextsHollow getAltGenresShortNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(28);
        return (AltGenresShortNameTranslatedTextsHollow)altGenresShortNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresShortNameMapOfTranslatedTextsHollow> getAllAltGenresShortNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AltGenresShortNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AltGenresShortNameMapOfTranslatedTexts").getTypeState()) {
            protected AltGenresShortNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAltGenresShortNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AltGenresShortNameMapOfTranslatedTextsHollow getAltGenresShortNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(29);
        return (AltGenresShortNameMapOfTranslatedTextsHollow)altGenresShortNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresShortNameHollow> getAllAltGenresShortNameHollow() {
        return new AllHollowRecordCollection<AltGenresShortNameHollow>(getDataAccess().getTypeDataAccess("AltGenresShortName").getTypeState()) {
            protected AltGenresShortNameHollow getForOrdinal(int ordinal) {
                return getAltGenresShortNameHollow(ordinal);
            }
        };
    }
    public AltGenresShortNameHollow getAltGenresShortNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(30);
        return (AltGenresShortNameHollow)altGenresShortNameProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresHollow> getAllAltGenresHollow() {
        return new AllHollowRecordCollection<AltGenresHollow>(getDataAccess().getTypeDataAccess("AltGenres").getTypeState()) {
            protected AltGenresHollow getForOrdinal(int ordinal) {
                return getAltGenresHollow(ordinal);
            }
        };
    }
    public AltGenresHollow getAltGenresHollow(int ordinal) {
        objectCreationSampler.recordCreation(31);
        return (AltGenresHollow)altGenresProvider.getHollowObject(ordinal);
    }
    public Collection<ArtWorkImageFormatHollow> getAllArtWorkImageFormatHollow() {
        return new AllHollowRecordCollection<ArtWorkImageFormatHollow>(getDataAccess().getTypeDataAccess("ArtWorkImageFormat").getTypeState()) {
            protected ArtWorkImageFormatHollow getForOrdinal(int ordinal) {
                return getArtWorkImageFormatHollow(ordinal);
            }
        };
    }
    public ArtWorkImageFormatHollow getArtWorkImageFormatHollow(int ordinal) {
        objectCreationSampler.recordCreation(32);
        return (ArtWorkImageFormatHollow)artWorkImageFormatProvider.getHollowObject(ordinal);
    }
    public Collection<ArtWorkImageTypeHollow> getAllArtWorkImageTypeHollow() {
        return new AllHollowRecordCollection<ArtWorkImageTypeHollow>(getDataAccess().getTypeDataAccess("ArtWorkImageType").getTypeState()) {
            protected ArtWorkImageTypeHollow getForOrdinal(int ordinal) {
                return getArtWorkImageTypeHollow(ordinal);
            }
        };
    }
    public ArtWorkImageTypeHollow getArtWorkImageTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(33);
        return (ArtWorkImageTypeHollow)artWorkImageTypeProvider.getHollowObject(ordinal);
    }
    public Collection<ArtworkRecipeHollow> getAllArtworkRecipeHollow() {
        return new AllHollowRecordCollection<ArtworkRecipeHollow>(getDataAccess().getTypeDataAccess("ArtworkRecipe").getTypeState()) {
            protected ArtworkRecipeHollow getForOrdinal(int ordinal) {
                return getArtworkRecipeHollow(ordinal);
            }
        };
    }
    public ArtworkRecipeHollow getArtworkRecipeHollow(int ordinal) {
        objectCreationSampler.recordCreation(34);
        return (ArtworkRecipeHollow)artworkRecipeProvider.getHollowObject(ordinal);
    }
    public Collection<AssetMetaDatasTrackLabelsTranslatedTextsHollow> getAllAssetMetaDatasTrackLabelsTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AssetMetaDatasTrackLabelsTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AssetMetaDatasTrackLabelsTranslatedTexts").getTypeState()) {
            protected AssetMetaDatasTrackLabelsTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAssetMetaDatasTrackLabelsTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AssetMetaDatasTrackLabelsTranslatedTextsHollow getAssetMetaDatasTrackLabelsTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(35);
        return (AssetMetaDatasTrackLabelsTranslatedTextsHollow)assetMetaDatasTrackLabelsTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow> getAllAssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AssetMetaDatasTrackLabelsMapOfTranslatedTexts").getTypeState()) {
            protected AssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow getAssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(36);
        return (AssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow)assetMetaDatasTrackLabelsMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AssetMetaDatasTrackLabelsHollow> getAllAssetMetaDatasTrackLabelsHollow() {
        return new AllHollowRecordCollection<AssetMetaDatasTrackLabelsHollow>(getDataAccess().getTypeDataAccess("AssetMetaDatasTrackLabels").getTypeState()) {
            protected AssetMetaDatasTrackLabelsHollow getForOrdinal(int ordinal) {
                return getAssetMetaDatasTrackLabelsHollow(ordinal);
            }
        };
    }
    public AssetMetaDatasTrackLabelsHollow getAssetMetaDatasTrackLabelsHollow(int ordinal) {
        objectCreationSampler.recordCreation(37);
        return (AssetMetaDatasTrackLabelsHollow)assetMetaDatasTrackLabelsProvider.getHollowObject(ordinal);
    }
    public Collection<AssetMetaDatasHollow> getAllAssetMetaDatasHollow() {
        return new AllHollowRecordCollection<AssetMetaDatasHollow>(getDataAccess().getTypeDataAccess("AssetMetaDatas").getTypeState()) {
            protected AssetMetaDatasHollow getForOrdinal(int ordinal) {
                return getAssetMetaDatasHollow(ordinal);
            }
        };
    }
    public AssetMetaDatasHollow getAssetMetaDatasHollow(int ordinal) {
        objectCreationSampler.recordCreation(38);
        return (AssetMetaDatasHollow)assetMetaDatasProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsAlternateNameTranslatedTextsHollow> getAllAwardsAlternateNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AwardsAlternateNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AwardsAlternateNameTranslatedTexts").getTypeState()) {
            protected AwardsAlternateNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAwardsAlternateNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AwardsAlternateNameTranslatedTextsHollow getAwardsAlternateNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(39);
        return (AwardsAlternateNameTranslatedTextsHollow)awardsAlternateNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsAlternateNameMapOfTranslatedTextsHollow> getAllAwardsAlternateNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AwardsAlternateNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AwardsAlternateNameMapOfTranslatedTexts").getTypeState()) {
            protected AwardsAlternateNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAwardsAlternateNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AwardsAlternateNameMapOfTranslatedTextsHollow getAwardsAlternateNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(40);
        return (AwardsAlternateNameMapOfTranslatedTextsHollow)awardsAlternateNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsAlternateNameHollow> getAllAwardsAlternateNameHollow() {
        return new AllHollowRecordCollection<AwardsAlternateNameHollow>(getDataAccess().getTypeDataAccess("AwardsAlternateName").getTypeState()) {
            protected AwardsAlternateNameHollow getForOrdinal(int ordinal) {
                return getAwardsAlternateNameHollow(ordinal);
            }
        };
    }
    public AwardsAlternateNameHollow getAwardsAlternateNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(41);
        return (AwardsAlternateNameHollow)awardsAlternateNameProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsAwardNameTranslatedTextsHollow> getAllAwardsAwardNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AwardsAwardNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AwardsAwardNameTranslatedTexts").getTypeState()) {
            protected AwardsAwardNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAwardsAwardNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AwardsAwardNameTranslatedTextsHollow getAwardsAwardNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(42);
        return (AwardsAwardNameTranslatedTextsHollow)awardsAwardNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsAwardNameMapOfTranslatedTextsHollow> getAllAwardsAwardNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<AwardsAwardNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("AwardsAwardNameMapOfTranslatedTexts").getTypeState()) {
            protected AwardsAwardNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getAwardsAwardNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public AwardsAwardNameMapOfTranslatedTextsHollow getAwardsAwardNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(43);
        return (AwardsAwardNameMapOfTranslatedTextsHollow)awardsAwardNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsAwardNameHollow> getAllAwardsAwardNameHollow() {
        return new AllHollowRecordCollection<AwardsAwardNameHollow>(getDataAccess().getTypeDataAccess("AwardsAwardName").getTypeState()) {
            protected AwardsAwardNameHollow getForOrdinal(int ordinal) {
                return getAwardsAwardNameHollow(ordinal);
            }
        };
    }
    public AwardsAwardNameHollow getAwardsAwardNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(44);
        return (AwardsAwardNameHollow)awardsAwardNameProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsHollow> getAllAwardsHollow() {
        return new AllHollowRecordCollection<AwardsHollow>(getDataAccess().getTypeDataAccess("Awards").getTypeState()) {
            protected AwardsHollow getForOrdinal(int ordinal) {
                return getAwardsHollow(ordinal);
            }
        };
    }
    public AwardsHollow getAwardsHollow(int ordinal) {
        objectCreationSampler.recordCreation(45);
        return (AwardsHollow)awardsProvider.getHollowObject(ordinal);
    }
    public Collection<Bcp47CodeHollow> getAllBcp47CodeHollow() {
        return new AllHollowRecordCollection<Bcp47CodeHollow>(getDataAccess().getTypeDataAccess("Bcp47Code").getTypeState()) {
            protected Bcp47CodeHollow getForOrdinal(int ordinal) {
                return getBcp47CodeHollow(ordinal);
            }
        };
    }
    public Bcp47CodeHollow getBcp47CodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(46);
        return (Bcp47CodeHollow)bcp47CodeProvider.getHollowObject(ordinal);
    }
    public Collection<CSMReviewHollow> getAllCSMReviewHollow() {
        return new AllHollowRecordCollection<CSMReviewHollow>(getDataAccess().getTypeDataAccess("CSMReview").getTypeState()) {
            protected CSMReviewHollow getForOrdinal(int ordinal) {
                return getCSMReviewHollow(ordinal);
            }
        };
    }
    public CSMReviewHollow getCSMReviewHollow(int ordinal) {
        objectCreationSampler.recordCreation(47);
        return (CSMReviewHollow)cSMReviewProvider.getHollowObject(ordinal);
    }
    public Collection<CacheDeploymentIntentHollow> getAllCacheDeploymentIntentHollow() {
        return new AllHollowRecordCollection<CacheDeploymentIntentHollow>(getDataAccess().getTypeDataAccess("CacheDeploymentIntent").getTypeState()) {
            protected CacheDeploymentIntentHollow getForOrdinal(int ordinal) {
                return getCacheDeploymentIntentHollow(ordinal);
            }
        };
    }
    public CacheDeploymentIntentHollow getCacheDeploymentIntentHollow(int ordinal) {
        objectCreationSampler.recordCreation(48);
        return (CacheDeploymentIntentHollow)cacheDeploymentIntentProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesDisplayNameTranslatedTextsHollow> getAllCategoriesDisplayNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CategoriesDisplayNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CategoriesDisplayNameTranslatedTexts").getTypeState()) {
            protected CategoriesDisplayNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCategoriesDisplayNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CategoriesDisplayNameTranslatedTextsHollow getCategoriesDisplayNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(49);
        return (CategoriesDisplayNameTranslatedTextsHollow)categoriesDisplayNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesDisplayNameMapOfTranslatedTextsHollow> getAllCategoriesDisplayNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CategoriesDisplayNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CategoriesDisplayNameMapOfTranslatedTexts").getTypeState()) {
            protected CategoriesDisplayNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCategoriesDisplayNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CategoriesDisplayNameMapOfTranslatedTextsHollow getCategoriesDisplayNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(50);
        return (CategoriesDisplayNameMapOfTranslatedTextsHollow)categoriesDisplayNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesDisplayNameHollow> getAllCategoriesDisplayNameHollow() {
        return new AllHollowRecordCollection<CategoriesDisplayNameHollow>(getDataAccess().getTypeDataAccess("CategoriesDisplayName").getTypeState()) {
            protected CategoriesDisplayNameHollow getForOrdinal(int ordinal) {
                return getCategoriesDisplayNameHollow(ordinal);
            }
        };
    }
    public CategoriesDisplayNameHollow getCategoriesDisplayNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(51);
        return (CategoriesDisplayNameHollow)categoriesDisplayNameProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesShortNameTranslatedTextsHollow> getAllCategoriesShortNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CategoriesShortNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CategoriesShortNameTranslatedTexts").getTypeState()) {
            protected CategoriesShortNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCategoriesShortNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CategoriesShortNameTranslatedTextsHollow getCategoriesShortNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(52);
        return (CategoriesShortNameTranslatedTextsHollow)categoriesShortNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesShortNameMapOfTranslatedTextsHollow> getAllCategoriesShortNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CategoriesShortNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CategoriesShortNameMapOfTranslatedTexts").getTypeState()) {
            protected CategoriesShortNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCategoriesShortNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CategoriesShortNameMapOfTranslatedTextsHollow getCategoriesShortNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(53);
        return (CategoriesShortNameMapOfTranslatedTextsHollow)categoriesShortNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesShortNameHollow> getAllCategoriesShortNameHollow() {
        return new AllHollowRecordCollection<CategoriesShortNameHollow>(getDataAccess().getTypeDataAccess("CategoriesShortName").getTypeState()) {
            protected CategoriesShortNameHollow getForOrdinal(int ordinal) {
                return getCategoriesShortNameHollow(ordinal);
            }
        };
    }
    public CategoriesShortNameHollow getCategoriesShortNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(54);
        return (CategoriesShortNameHollow)categoriesShortNameProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesHollow> getAllCategoriesHollow() {
        return new AllHollowRecordCollection<CategoriesHollow>(getDataAccess().getTypeDataAccess("Categories").getTypeState()) {
            protected CategoriesHollow getForOrdinal(int ordinal) {
                return getCategoriesHollow(ordinal);
            }
        };
    }
    public CategoriesHollow getCategoriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(55);
        return (CategoriesHollow)categoriesProvider.getHollowObject(ordinal);
    }
    public Collection<CategoryGroupsCategoryGroupNameTranslatedTextsHollow> getAllCategoryGroupsCategoryGroupNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CategoryGroupsCategoryGroupNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CategoryGroupsCategoryGroupNameTranslatedTexts").getTypeState()) {
            protected CategoryGroupsCategoryGroupNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCategoryGroupsCategoryGroupNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CategoryGroupsCategoryGroupNameTranslatedTextsHollow getCategoryGroupsCategoryGroupNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(56);
        return (CategoryGroupsCategoryGroupNameTranslatedTextsHollow)categoryGroupsCategoryGroupNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow> getAllCategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CategoryGroupsCategoryGroupNameMapOfTranslatedTexts").getTypeState()) {
            protected CategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow getCategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(57);
        return (CategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow)categoryGroupsCategoryGroupNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CategoryGroupsCategoryGroupNameHollow> getAllCategoryGroupsCategoryGroupNameHollow() {
        return new AllHollowRecordCollection<CategoryGroupsCategoryGroupNameHollow>(getDataAccess().getTypeDataAccess("CategoryGroupsCategoryGroupName").getTypeState()) {
            protected CategoryGroupsCategoryGroupNameHollow getForOrdinal(int ordinal) {
                return getCategoryGroupsCategoryGroupNameHollow(ordinal);
            }
        };
    }
    public CategoryGroupsCategoryGroupNameHollow getCategoryGroupsCategoryGroupNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(58);
        return (CategoryGroupsCategoryGroupNameHollow)categoryGroupsCategoryGroupNameProvider.getHollowObject(ordinal);
    }
    public Collection<CategoryGroupsHollow> getAllCategoryGroupsHollow() {
        return new AllHollowRecordCollection<CategoryGroupsHollow>(getDataAccess().getTypeDataAccess("CategoryGroups").getTypeState()) {
            protected CategoryGroupsHollow getForOrdinal(int ordinal) {
                return getCategoryGroupsHollow(ordinal);
            }
        };
    }
    public CategoryGroupsHollow getCategoryGroupsHollow(int ordinal) {
        objectCreationSampler.recordCreation(59);
        return (CategoryGroupsHollow)categoryGroupsProvider.getHollowObject(ordinal);
    }
    public Collection<CdnsHollow> getAllCdnsHollow() {
        return new AllHollowRecordCollection<CdnsHollow>(getDataAccess().getTypeDataAccess("Cdns").getTypeState()) {
            protected CdnsHollow getForOrdinal(int ordinal) {
                return getCdnsHollow(ordinal);
            }
        };
    }
    public CdnsHollow getCdnsHollow(int ordinal) {
        objectCreationSampler.recordCreation(60);
        return (CdnsHollow)cdnsProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationSystemRatingHollow> getAllCertificationSystemRatingHollow() {
        return new AllHollowRecordCollection<CertificationSystemRatingHollow>(getDataAccess().getTypeDataAccess("CertificationSystemRating").getTypeState()) {
            protected CertificationSystemRatingHollow getForOrdinal(int ordinal) {
                return getCertificationSystemRatingHollow(ordinal);
            }
        };
    }
    public CertificationSystemRatingHollow getCertificationSystemRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(61);
        return (CertificationSystemRatingHollow)certificationSystemRatingProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationSystemArrayOfRatingHollow> getAllCertificationSystemArrayOfRatingHollow() {
        return new AllHollowRecordCollection<CertificationSystemArrayOfRatingHollow>(getDataAccess().getTypeDataAccess("CertificationSystemArrayOfRating").getTypeState()) {
            protected CertificationSystemArrayOfRatingHollow getForOrdinal(int ordinal) {
                return getCertificationSystemArrayOfRatingHollow(ordinal);
            }
        };
    }
    public CertificationSystemArrayOfRatingHollow getCertificationSystemArrayOfRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(62);
        return (CertificationSystemArrayOfRatingHollow)certificationSystemArrayOfRatingProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationSystemHollow> getAllCertificationSystemHollow() {
        return new AllHollowRecordCollection<CertificationSystemHollow>(getDataAccess().getTypeDataAccess("CertificationSystem").getTypeState()) {
            protected CertificationSystemHollow getForOrdinal(int ordinal) {
                return getCertificationSystemHollow(ordinal);
            }
        };
    }
    public CertificationSystemHollow getCertificationSystemHollow(int ordinal) {
        objectCreationSampler.recordCreation(63);
        return (CertificationSystemHollow)certificationSystemProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsDescriptionTranslatedTextsHollow> getAllCertificationsDescriptionTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CertificationsDescriptionTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CertificationsDescriptionTranslatedTexts").getTypeState()) {
            protected CertificationsDescriptionTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCertificationsDescriptionTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CertificationsDescriptionTranslatedTextsHollow getCertificationsDescriptionTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(64);
        return (CertificationsDescriptionTranslatedTextsHollow)certificationsDescriptionTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsDescriptionMapOfTranslatedTextsHollow> getAllCertificationsDescriptionMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CertificationsDescriptionMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CertificationsDescriptionMapOfTranslatedTexts").getTypeState()) {
            protected CertificationsDescriptionMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCertificationsDescriptionMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CertificationsDescriptionMapOfTranslatedTextsHollow getCertificationsDescriptionMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(65);
        return (CertificationsDescriptionMapOfTranslatedTextsHollow)certificationsDescriptionMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsDescriptionHollow> getAllCertificationsDescriptionHollow() {
        return new AllHollowRecordCollection<CertificationsDescriptionHollow>(getDataAccess().getTypeDataAccess("CertificationsDescription").getTypeState()) {
            protected CertificationsDescriptionHollow getForOrdinal(int ordinal) {
                return getCertificationsDescriptionHollow(ordinal);
            }
        };
    }
    public CertificationsDescriptionHollow getCertificationsDescriptionHollow(int ordinal) {
        objectCreationSampler.recordCreation(66);
        return (CertificationsDescriptionHollow)certificationsDescriptionProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsNameTranslatedTextsHollow> getAllCertificationsNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CertificationsNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CertificationsNameTranslatedTexts").getTypeState()) {
            protected CertificationsNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCertificationsNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CertificationsNameTranslatedTextsHollow getCertificationsNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(67);
        return (CertificationsNameTranslatedTextsHollow)certificationsNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsNameMapOfTranslatedTextsHollow> getAllCertificationsNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CertificationsNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CertificationsNameMapOfTranslatedTexts").getTypeState()) {
            protected CertificationsNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCertificationsNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CertificationsNameMapOfTranslatedTextsHollow getCertificationsNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(68);
        return (CertificationsNameMapOfTranslatedTextsHollow)certificationsNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsNameHollow> getAllCertificationsNameHollow() {
        return new AllHollowRecordCollection<CertificationsNameHollow>(getDataAccess().getTypeDataAccess("CertificationsName").getTypeState()) {
            protected CertificationsNameHollow getForOrdinal(int ordinal) {
                return getCertificationsNameHollow(ordinal);
            }
        };
    }
    public CertificationsNameHollow getCertificationsNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(69);
        return (CertificationsNameHollow)certificationsNameProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsHollow> getAllCertificationsHollow() {
        return new AllHollowRecordCollection<CertificationsHollow>(getDataAccess().getTypeDataAccess("Certifications").getTypeState()) {
            protected CertificationsHollow getForOrdinal(int ordinal) {
                return getCertificationsHollow(ordinal);
            }
        };
    }
    public CertificationsHollow getCertificationsHollow(int ordinal) {
        objectCreationSampler.recordCreation(70);
        return (CertificationsHollow)certificationsProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkAttributesHollow> getAllCharacterArtworkAttributesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkAttributesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkAttributes").getTypeState()) {
            protected CharacterArtworkAttributesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkAttributesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkAttributesHollow getCharacterArtworkAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(71);
        return (CharacterArtworkAttributesHollow)characterArtworkAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkDerivativesHollow> getAllCharacterArtworkDerivativesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkDerivativesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkDerivatives").getTypeState()) {
            protected CharacterArtworkDerivativesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkDerivativesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkDerivativesHollow getCharacterArtworkDerivativesHollow(int ordinal) {
        objectCreationSampler.recordCreation(72);
        return (CharacterArtworkDerivativesHollow)characterArtworkDerivativesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkArrayOfDerivativesHollow> getAllCharacterArtworkArrayOfDerivativesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkArrayOfDerivativesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkArrayOfDerivatives").getTypeState()) {
            protected CharacterArtworkArrayOfDerivativesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkArrayOfDerivativesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkArrayOfDerivativesHollow getCharacterArtworkArrayOfDerivativesHollow(int ordinal) {
        objectCreationSampler.recordCreation(73);
        return (CharacterArtworkArrayOfDerivativesHollow)characterArtworkArrayOfDerivativesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkLocalesTerritoryCodesHollow> getAllCharacterArtworkLocalesTerritoryCodesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkLocalesTerritoryCodesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkLocalesTerritoryCodes").getTypeState()) {
            protected CharacterArtworkLocalesTerritoryCodesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkLocalesTerritoryCodesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkLocalesTerritoryCodesHollow getCharacterArtworkLocalesTerritoryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(74);
        return (CharacterArtworkLocalesTerritoryCodesHollow)characterArtworkLocalesTerritoryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkLocalesArrayOfTerritoryCodesHollow> getAllCharacterArtworkLocalesArrayOfTerritoryCodesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkLocalesArrayOfTerritoryCodesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkLocalesArrayOfTerritoryCodes").getTypeState()) {
            protected CharacterArtworkLocalesArrayOfTerritoryCodesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkLocalesArrayOfTerritoryCodesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkLocalesArrayOfTerritoryCodesHollow getCharacterArtworkLocalesArrayOfTerritoryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(75);
        return (CharacterArtworkLocalesArrayOfTerritoryCodesHollow)characterArtworkLocalesArrayOfTerritoryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkLocalesHollow> getAllCharacterArtworkLocalesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkLocalesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkLocales").getTypeState()) {
            protected CharacterArtworkLocalesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkLocalesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkLocalesHollow getCharacterArtworkLocalesHollow(int ordinal) {
        objectCreationSampler.recordCreation(76);
        return (CharacterArtworkLocalesHollow)characterArtworkLocalesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkArrayOfLocalesHollow> getAllCharacterArtworkArrayOfLocalesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkArrayOfLocalesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkArrayOfLocales").getTypeState()) {
            protected CharacterArtworkArrayOfLocalesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkArrayOfLocalesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkArrayOfLocalesHollow getCharacterArtworkArrayOfLocalesHollow(int ordinal) {
        objectCreationSampler.recordCreation(77);
        return (CharacterArtworkArrayOfLocalesHollow)characterArtworkArrayOfLocalesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkHollow> getAllCharacterArtworkHollow() {
        return new AllHollowRecordCollection<CharacterArtworkHollow>(getDataAccess().getTypeDataAccess("CharacterArtwork").getTypeState()) {
            protected CharacterArtworkHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkHollow(ordinal);
            }
        };
    }
    public CharacterArtworkHollow getCharacterArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(78);
        return (CharacterArtworkHollow)characterArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterElementsHollow> getAllCharacterElementsHollow() {
        return new AllHollowRecordCollection<CharacterElementsHollow>(getDataAccess().getTypeDataAccess("CharacterElements").getTypeState()) {
            protected CharacterElementsHollow getForOrdinal(int ordinal) {
                return getCharacterElementsHollow(ordinal);
            }
        };
    }
    public CharacterElementsHollow getCharacterElementsHollow(int ordinal) {
        objectCreationSampler.recordCreation(79);
        return (CharacterElementsHollow)characterElementsProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterHollow> getAllCharacterHollow() {
        return new AllHollowRecordCollection<CharacterHollow>(getDataAccess().getTypeDataAccess("Character").getTypeState()) {
            protected CharacterHollow getForOrdinal(int ordinal) {
                return getCharacterHollow(ordinal);
            }
        };
    }
    public CharacterHollow getCharacterHollow(int ordinal) {
        objectCreationSampler.recordCreation(80);
        return (CharacterHollow)characterProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersBTranslatedTextsHollow> getAllCharactersBTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CharactersBTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CharactersBTranslatedTexts").getTypeState()) {
            protected CharactersBTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCharactersBTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CharactersBTranslatedTextsHollow getCharactersBTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(81);
        return (CharactersBTranslatedTextsHollow)charactersBTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersBMapOfTranslatedTextsHollow> getAllCharactersBMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CharactersBMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CharactersBMapOfTranslatedTexts").getTypeState()) {
            protected CharactersBMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCharactersBMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CharactersBMapOfTranslatedTextsHollow getCharactersBMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(82);
        return (CharactersBMapOfTranslatedTextsHollow)charactersBMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersBHollow> getAllCharactersBHollow() {
        return new AllHollowRecordCollection<CharactersBHollow>(getDataAccess().getTypeDataAccess("CharactersB").getTypeState()) {
            protected CharactersBHollow getForOrdinal(int ordinal) {
                return getCharactersBHollow(ordinal);
            }
        };
    }
    public CharactersBHollow getCharactersBHollow(int ordinal) {
        objectCreationSampler.recordCreation(83);
        return (CharactersBHollow)charactersBProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersCnTranslatedTextsHollow> getAllCharactersCnTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CharactersCnTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CharactersCnTranslatedTexts").getTypeState()) {
            protected CharactersCnTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCharactersCnTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CharactersCnTranslatedTextsHollow getCharactersCnTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(84);
        return (CharactersCnTranslatedTextsHollow)charactersCnTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersCnMapOfTranslatedTextsHollow> getAllCharactersCnMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<CharactersCnMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("CharactersCnMapOfTranslatedTexts").getTypeState()) {
            protected CharactersCnMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getCharactersCnMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public CharactersCnMapOfTranslatedTextsHollow getCharactersCnMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(85);
        return (CharactersCnMapOfTranslatedTextsHollow)charactersCnMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersCnHollow> getAllCharactersCnHollow() {
        return new AllHollowRecordCollection<CharactersCnHollow>(getDataAccess().getTypeDataAccess("CharactersCn").getTypeState()) {
            protected CharactersCnHollow getForOrdinal(int ordinal) {
                return getCharactersCnHollow(ordinal);
            }
        };
    }
    public CharactersCnHollow getCharactersCnHollow(int ordinal) {
        objectCreationSampler.recordCreation(86);
        return (CharactersCnHollow)charactersCnProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersHollow> getAllCharactersHollow() {
        return new AllHollowRecordCollection<CharactersHollow>(getDataAccess().getTypeDataAccess("Characters").getTypeState()) {
            protected CharactersHollow getForOrdinal(int ordinal) {
                return getCharactersHollow(ordinal);
            }
        };
    }
    public CharactersHollow getCharactersHollow(int ordinal) {
        objectCreationSampler.recordCreation(87);
        return (CharactersHollow)charactersProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow> getAllConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsDescriptionTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow getConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(88);
        return (ConsolidatedCertificationSystemsDescriptionTranslatedTextsHollow)consolidatedCertificationSystemsDescriptionTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow> getAllConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow getConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(89);
        return (ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow)consolidatedCertificationSystemsDescriptionMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsDescriptionHollow> getAllConsolidatedCertificationSystemsDescriptionHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsDescriptionHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsDescription").getTypeState()) {
            protected ConsolidatedCertificationSystemsDescriptionHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsDescriptionHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsDescriptionHollow getConsolidatedCertificationSystemsDescriptionHollow(int ordinal) {
        objectCreationSampler.recordCreation(90);
        return (ConsolidatedCertificationSystemsDescriptionHollow)consolidatedCertificationSystemsDescriptionProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsNameTranslatedTextsHollow> getAllConsolidatedCertificationSystemsNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsNameTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsNameTranslatedTextsHollow getConsolidatedCertificationSystemsNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(91);
        return (ConsolidatedCertificationSystemsNameTranslatedTextsHollow)consolidatedCertificationSystemsNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow> getAllConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsNameMapOfTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow getConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(92);
        return (ConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow)consolidatedCertificationSystemsNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsNameHollow> getAllConsolidatedCertificationSystemsNameHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsNameHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsName").getTypeState()) {
            protected ConsolidatedCertificationSystemsNameHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsNameHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsNameHollow getConsolidatedCertificationSystemsNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(93);
        return (ConsolidatedCertificationSystemsNameHollow)consolidatedCertificationSystemsNameProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow> getAllConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow getConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(94);
        return (ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsHollow)consolidatedCertificationSystemsRatingDescriptionsTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow> getAllConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow getConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(95);
        return (ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow)consolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsRatingDescriptionsHollow> getAllConsolidatedCertificationSystemsRatingDescriptionsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsRatingDescriptionsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsRatingDescriptions").getTypeState()) {
            protected ConsolidatedCertificationSystemsRatingDescriptionsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsRatingDescriptionsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsRatingDescriptionsHollow getConsolidatedCertificationSystemsRatingDescriptionsHollow(int ordinal) {
        objectCreationSampler.recordCreation(96);
        return (ConsolidatedCertificationSystemsRatingDescriptionsHollow)consolidatedCertificationSystemsRatingDescriptionsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow> getAllConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow getConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(97);
        return (ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow)consolidatedCertificationSystemsRatingRatingCodesTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow> getAllConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTexts").getTypeState()) {
            protected ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow getConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(98);
        return (ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow)consolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsRatingRatingCodesHollow> getAllConsolidatedCertificationSystemsRatingRatingCodesHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsRatingRatingCodesHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsRatingRatingCodes").getTypeState()) {
            protected ConsolidatedCertificationSystemsRatingRatingCodesHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsRatingRatingCodesHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsRatingRatingCodesHollow getConsolidatedCertificationSystemsRatingRatingCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(99);
        return (ConsolidatedCertificationSystemsRatingRatingCodesHollow)consolidatedCertificationSystemsRatingRatingCodesProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsRatingHollow> getAllConsolidatedCertificationSystemsRatingHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsRatingHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsRating").getTypeState()) {
            protected ConsolidatedCertificationSystemsRatingHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsRatingHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsRatingHollow getConsolidatedCertificationSystemsRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(100);
        return (ConsolidatedCertificationSystemsRatingHollow)consolidatedCertificationSystemsRatingProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsArrayOfRatingHollow> getAllConsolidatedCertificationSystemsArrayOfRatingHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsArrayOfRatingHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystemsArrayOfRating").getTypeState()) {
            protected ConsolidatedCertificationSystemsArrayOfRatingHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsArrayOfRatingHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsArrayOfRatingHollow getConsolidatedCertificationSystemsArrayOfRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(101);
        return (ConsolidatedCertificationSystemsArrayOfRatingHollow)consolidatedCertificationSystemsArrayOfRatingProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsHollow> getAllConsolidatedCertificationSystemsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystems").getTypeState()) {
            protected ConsolidatedCertificationSystemsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsHollow getConsolidatedCertificationSystemsHollow(int ordinal) {
        objectCreationSampler.recordCreation(102);
        return (ConsolidatedCertificationSystemsHollow)consolidatedCertificationSystemsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryListHollow> getAllConsolidatedVideoRatingsRatingsCountryListHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryListHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryList").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryListHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryListHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryListHollow getConsolidatedVideoRatingsRatingsCountryListHollow(int ordinal) {
        objectCreationSampler.recordCreation(103);
        return (ConsolidatedVideoRatingsRatingsCountryListHollow)consolidatedVideoRatingsRatingsCountryListProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow> getAllConsolidatedVideoRatingsRatingsArrayOfCountryListHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsArrayOfCountryList").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsArrayOfCountryListHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow getConsolidatedVideoRatingsRatingsArrayOfCountryListHollow(int ordinal) {
        objectCreationSampler.recordCreation(104);
        return (ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow)consolidatedVideoRatingsRatingsArrayOfCountryListProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow> getAllConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTexts").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow getConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(105);
        return (ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsHollow)consolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow> getAllConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTexts").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow getConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(106);
        return (ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow)consolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow> getAllConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatingsReasons").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow getConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow(int ordinal) {
        objectCreationSampler.recordCreation(107);
        return (ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow)consolidatedVideoRatingsRatingsCountryRatingsReasonsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsCountryRatingsHollow> getAllConsolidatedVideoRatingsRatingsCountryRatingsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsCountryRatingsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsCountryRatings").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsCountryRatingsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsCountryRatingsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsCountryRatingsHollow getConsolidatedVideoRatingsRatingsCountryRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(108);
        return (ConsolidatedVideoRatingsRatingsCountryRatingsHollow)consolidatedVideoRatingsRatingsCountryRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow> getAllConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatingsArrayOfCountryRatings").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow getConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(109);
        return (ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow)consolidatedVideoRatingsRatingsArrayOfCountryRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsRatingsHollow> getAllConsolidatedVideoRatingsRatingsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsRatingsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsRatings").getTypeState()) {
            protected ConsolidatedVideoRatingsRatingsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsRatingsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsRatingsHollow getConsolidatedVideoRatingsRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(110);
        return (ConsolidatedVideoRatingsRatingsHollow)consolidatedVideoRatingsRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsArrayOfRatingsHollow> getAllConsolidatedVideoRatingsArrayOfRatingsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsArrayOfRatingsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingsArrayOfRatings").getTypeState()) {
            protected ConsolidatedVideoRatingsArrayOfRatingsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsArrayOfRatingsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsArrayOfRatingsHollow getConsolidatedVideoRatingsArrayOfRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(111);
        return (ConsolidatedVideoRatingsArrayOfRatingsHollow)consolidatedVideoRatingsArrayOfRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsHollow> getAllConsolidatedVideoRatingsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatings").getTypeState()) {
            protected ConsolidatedVideoRatingsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsHollow getConsolidatedVideoRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(112);
        return (ConsolidatedVideoRatingsHollow)consolidatedVideoRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<DefaultExtensionRecipeHollow> getAllDefaultExtensionRecipeHollow() {
        return new AllHollowRecordCollection<DefaultExtensionRecipeHollow>(getDataAccess().getTypeDataAccess("DefaultExtensionRecipe").getTypeState()) {
            protected DefaultExtensionRecipeHollow getForOrdinal(int ordinal) {
                return getDefaultExtensionRecipeHollow(ordinal);
            }
        };
    }
    public DefaultExtensionRecipeHollow getDefaultExtensionRecipeHollow(int ordinal) {
        objectCreationSampler.recordCreation(113);
        return (DefaultExtensionRecipeHollow)defaultExtensionRecipeProvider.getHollowObject(ordinal);
    }
    public Collection<DeployablePackagesCountryCodesHollow> getAllDeployablePackagesCountryCodesHollow() {
        return new AllHollowRecordCollection<DeployablePackagesCountryCodesHollow>(getDataAccess().getTypeDataAccess("DeployablePackagesCountryCodes").getTypeState()) {
            protected DeployablePackagesCountryCodesHollow getForOrdinal(int ordinal) {
                return getDeployablePackagesCountryCodesHollow(ordinal);
            }
        };
    }
    public DeployablePackagesCountryCodesHollow getDeployablePackagesCountryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(114);
        return (DeployablePackagesCountryCodesHollow)deployablePackagesCountryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<DeployablePackagesArrayOfCountryCodesHollow> getAllDeployablePackagesArrayOfCountryCodesHollow() {
        return new AllHollowRecordCollection<DeployablePackagesArrayOfCountryCodesHollow>(getDataAccess().getTypeDataAccess("DeployablePackagesArrayOfCountryCodes").getTypeState()) {
            protected DeployablePackagesArrayOfCountryCodesHollow getForOrdinal(int ordinal) {
                return getDeployablePackagesArrayOfCountryCodesHollow(ordinal);
            }
        };
    }
    public DeployablePackagesArrayOfCountryCodesHollow getDeployablePackagesArrayOfCountryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(115);
        return (DeployablePackagesArrayOfCountryCodesHollow)deployablePackagesArrayOfCountryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<DeployablePackagesHollow> getAllDeployablePackagesHollow() {
        return new AllHollowRecordCollection<DeployablePackagesHollow>(getDataAccess().getTypeDataAccess("DeployablePackages").getTypeState()) {
            protected DeployablePackagesHollow getForOrdinal(int ordinal) {
                return getDeployablePackagesHollow(ordinal);
            }
        };
    }
    public DeployablePackagesHollow getDeployablePackagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(116);
        return (DeployablePackagesHollow)deployablePackagesProvider.getHollowObject(ordinal);
    }
    public Collection<DrmSystemIdentifiersHollow> getAllDrmSystemIdentifiersHollow() {
        return new AllHollowRecordCollection<DrmSystemIdentifiersHollow>(getDataAccess().getTypeDataAccess("DrmSystemIdentifiers").getTypeState()) {
            protected DrmSystemIdentifiersHollow getForOrdinal(int ordinal) {
                return getDrmSystemIdentifiersHollow(ordinal);
            }
        };
    }
    public DrmSystemIdentifiersHollow getDrmSystemIdentifiersHollow(int ordinal) {
        objectCreationSampler.recordCreation(117);
        return (DrmSystemIdentifiersHollow)drmSystemIdentifiersProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodesEpisodeNameTranslatedTextsHollow> getAllEpisodesEpisodeNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<EpisodesEpisodeNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("EpisodesEpisodeNameTranslatedTexts").getTypeState()) {
            protected EpisodesEpisodeNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getEpisodesEpisodeNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public EpisodesEpisodeNameTranslatedTextsHollow getEpisodesEpisodeNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(118);
        return (EpisodesEpisodeNameTranslatedTextsHollow)episodesEpisodeNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodesEpisodeNameMapOfTranslatedTextsHollow> getAllEpisodesEpisodeNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<EpisodesEpisodeNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("EpisodesEpisodeNameMapOfTranslatedTexts").getTypeState()) {
            protected EpisodesEpisodeNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getEpisodesEpisodeNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public EpisodesEpisodeNameMapOfTranslatedTextsHollow getEpisodesEpisodeNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(119);
        return (EpisodesEpisodeNameMapOfTranslatedTextsHollow)episodesEpisodeNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodesEpisodeNameHollow> getAllEpisodesEpisodeNameHollow() {
        return new AllHollowRecordCollection<EpisodesEpisodeNameHollow>(getDataAccess().getTypeDataAccess("EpisodesEpisodeName").getTypeState()) {
            protected EpisodesEpisodeNameHollow getForOrdinal(int ordinal) {
                return getEpisodesEpisodeNameHollow(ordinal);
            }
        };
    }
    public EpisodesEpisodeNameHollow getEpisodesEpisodeNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(120);
        return (EpisodesEpisodeNameHollow)episodesEpisodeNameProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodesHollow> getAllEpisodesHollow() {
        return new AllHollowRecordCollection<EpisodesHollow>(getDataAccess().getTypeDataAccess("Episodes").getTypeState()) {
            protected EpisodesHollow getForOrdinal(int ordinal) {
                return getEpisodesHollow(ordinal);
            }
        };
    }
    public EpisodesHollow getEpisodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(121);
        return (EpisodesHollow)episodesProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsCopyrightTranslatedTextsHollow> getAllFestivalsCopyrightTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsCopyrightTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsCopyrightTranslatedTexts").getTypeState()) {
            protected FestivalsCopyrightTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsCopyrightTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsCopyrightTranslatedTextsHollow getFestivalsCopyrightTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(122);
        return (FestivalsCopyrightTranslatedTextsHollow)festivalsCopyrightTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsCopyrightMapOfTranslatedTextsHollow> getAllFestivalsCopyrightMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsCopyrightMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsCopyrightMapOfTranslatedTexts").getTypeState()) {
            protected FestivalsCopyrightMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsCopyrightMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsCopyrightMapOfTranslatedTextsHollow getFestivalsCopyrightMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(123);
        return (FestivalsCopyrightMapOfTranslatedTextsHollow)festivalsCopyrightMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsCopyrightHollow> getAllFestivalsCopyrightHollow() {
        return new AllHollowRecordCollection<FestivalsCopyrightHollow>(getDataAccess().getTypeDataAccess("FestivalsCopyright").getTypeState()) {
            protected FestivalsCopyrightHollow getForOrdinal(int ordinal) {
                return getFestivalsCopyrightHollow(ordinal);
            }
        };
    }
    public FestivalsCopyrightHollow getFestivalsCopyrightHollow(int ordinal) {
        objectCreationSampler.recordCreation(124);
        return (FestivalsCopyrightHollow)festivalsCopyrightProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsDescriptionTranslatedTextsHollow> getAllFestivalsDescriptionTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsDescriptionTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsDescriptionTranslatedTexts").getTypeState()) {
            protected FestivalsDescriptionTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsDescriptionTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsDescriptionTranslatedTextsHollow getFestivalsDescriptionTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(125);
        return (FestivalsDescriptionTranslatedTextsHollow)festivalsDescriptionTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsDescriptionMapOfTranslatedTextsHollow> getAllFestivalsDescriptionMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsDescriptionMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsDescriptionMapOfTranslatedTexts").getTypeState()) {
            protected FestivalsDescriptionMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsDescriptionMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsDescriptionMapOfTranslatedTextsHollow getFestivalsDescriptionMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(126);
        return (FestivalsDescriptionMapOfTranslatedTextsHollow)festivalsDescriptionMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsDescriptionHollow> getAllFestivalsDescriptionHollow() {
        return new AllHollowRecordCollection<FestivalsDescriptionHollow>(getDataAccess().getTypeDataAccess("FestivalsDescription").getTypeState()) {
            protected FestivalsDescriptionHollow getForOrdinal(int ordinal) {
                return getFestivalsDescriptionHollow(ordinal);
            }
        };
    }
    public FestivalsDescriptionHollow getFestivalsDescriptionHollow(int ordinal) {
        objectCreationSampler.recordCreation(127);
        return (FestivalsDescriptionHollow)festivalsDescriptionProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsFestivalNameTranslatedTextsHollow> getAllFestivalsFestivalNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsFestivalNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsFestivalNameTranslatedTexts").getTypeState()) {
            protected FestivalsFestivalNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsFestivalNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsFestivalNameTranslatedTextsHollow getFestivalsFestivalNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(128);
        return (FestivalsFestivalNameTranslatedTextsHollow)festivalsFestivalNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsFestivalNameMapOfTranslatedTextsHollow> getAllFestivalsFestivalNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsFestivalNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsFestivalNameMapOfTranslatedTexts").getTypeState()) {
            protected FestivalsFestivalNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsFestivalNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsFestivalNameMapOfTranslatedTextsHollow getFestivalsFestivalNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(129);
        return (FestivalsFestivalNameMapOfTranslatedTextsHollow)festivalsFestivalNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsFestivalNameHollow> getAllFestivalsFestivalNameHollow() {
        return new AllHollowRecordCollection<FestivalsFestivalNameHollow>(getDataAccess().getTypeDataAccess("FestivalsFestivalName").getTypeState()) {
            protected FestivalsFestivalNameHollow getForOrdinal(int ordinal) {
                return getFestivalsFestivalNameHollow(ordinal);
            }
        };
    }
    public FestivalsFestivalNameHollow getFestivalsFestivalNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(130);
        return (FestivalsFestivalNameHollow)festivalsFestivalNameProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsShortNameTranslatedTextsHollow> getAllFestivalsShortNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsShortNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsShortNameTranslatedTexts").getTypeState()) {
            protected FestivalsShortNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsShortNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsShortNameTranslatedTextsHollow getFestivalsShortNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(131);
        return (FestivalsShortNameTranslatedTextsHollow)festivalsShortNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsShortNameMapOfTranslatedTextsHollow> getAllFestivalsShortNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsShortNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsShortNameMapOfTranslatedTexts").getTypeState()) {
            protected FestivalsShortNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsShortNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsShortNameMapOfTranslatedTextsHollow getFestivalsShortNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(132);
        return (FestivalsShortNameMapOfTranslatedTextsHollow)festivalsShortNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsShortNameHollow> getAllFestivalsShortNameHollow() {
        return new AllHollowRecordCollection<FestivalsShortNameHollow>(getDataAccess().getTypeDataAccess("FestivalsShortName").getTypeState()) {
            protected FestivalsShortNameHollow getForOrdinal(int ordinal) {
                return getFestivalsShortNameHollow(ordinal);
            }
        };
    }
    public FestivalsShortNameHollow getFestivalsShortNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(133);
        return (FestivalsShortNameHollow)festivalsShortNameProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsSingularNameTranslatedTextsHollow> getAllFestivalsSingularNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsSingularNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsSingularNameTranslatedTexts").getTypeState()) {
            protected FestivalsSingularNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsSingularNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsSingularNameTranslatedTextsHollow getFestivalsSingularNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(134);
        return (FestivalsSingularNameTranslatedTextsHollow)festivalsSingularNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsSingularNameMapOfTranslatedTextsHollow> getAllFestivalsSingularNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<FestivalsSingularNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("FestivalsSingularNameMapOfTranslatedTexts").getTypeState()) {
            protected FestivalsSingularNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getFestivalsSingularNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public FestivalsSingularNameMapOfTranslatedTextsHollow getFestivalsSingularNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(135);
        return (FestivalsSingularNameMapOfTranslatedTextsHollow)festivalsSingularNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsSingularNameHollow> getAllFestivalsSingularNameHollow() {
        return new AllHollowRecordCollection<FestivalsSingularNameHollow>(getDataAccess().getTypeDataAccess("FestivalsSingularName").getTypeState()) {
            protected FestivalsSingularNameHollow getForOrdinal(int ordinal) {
                return getFestivalsSingularNameHollow(ordinal);
            }
        };
    }
    public FestivalsSingularNameHollow getFestivalsSingularNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(136);
        return (FestivalsSingularNameHollow)festivalsSingularNameProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsHollow> getAllFestivalsHollow() {
        return new AllHollowRecordCollection<FestivalsHollow>(getDataAccess().getTypeDataAccess("Festivals").getTypeState()) {
            protected FestivalsHollow getForOrdinal(int ordinal) {
                return getFestivalsHollow(ordinal);
            }
        };
    }
    public FestivalsHollow getFestivalsHollow(int ordinal) {
        objectCreationSampler.recordCreation(137);
        return (FestivalsHollow)festivalsProvider.getHollowObject(ordinal);
    }
    public Collection<LanguagesNameTranslatedTextsHollow> getAllLanguagesNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<LanguagesNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("LanguagesNameTranslatedTexts").getTypeState()) {
            protected LanguagesNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getLanguagesNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public LanguagesNameTranslatedTextsHollow getLanguagesNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(138);
        return (LanguagesNameTranslatedTextsHollow)languagesNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<LanguagesNameMapOfTranslatedTextsHollow> getAllLanguagesNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<LanguagesNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("LanguagesNameMapOfTranslatedTexts").getTypeState()) {
            protected LanguagesNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getLanguagesNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public LanguagesNameMapOfTranslatedTextsHollow getLanguagesNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(139);
        return (LanguagesNameMapOfTranslatedTextsHollow)languagesNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<LanguagesNameHollow> getAllLanguagesNameHollow() {
        return new AllHollowRecordCollection<LanguagesNameHollow>(getDataAccess().getTypeDataAccess("LanguagesName").getTypeState()) {
            protected LanguagesNameHollow getForOrdinal(int ordinal) {
                return getLanguagesNameHollow(ordinal);
            }
        };
    }
    public LanguagesNameHollow getLanguagesNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(140);
        return (LanguagesNameHollow)languagesNameProvider.getHollowObject(ordinal);
    }
    public Collection<LanguagesHollow> getAllLanguagesHollow() {
        return new AllHollowRecordCollection<LanguagesHollow>(getDataAccess().getTypeDataAccess("Languages").getTypeState()) {
            protected LanguagesHollow getForOrdinal(int ordinal) {
                return getLanguagesHollow(ordinal);
            }
        };
    }
    public LanguagesHollow getLanguagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(141);
        return (LanguagesHollow)languagesProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedCharacterTranslatedTextsHollow> getAllLocalizedCharacterTranslatedTextsHollow() {
        return new AllHollowRecordCollection<LocalizedCharacterTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("LocalizedCharacterTranslatedTexts").getTypeState()) {
            protected LocalizedCharacterTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getLocalizedCharacterTranslatedTextsHollow(ordinal);
            }
        };
    }
    public LocalizedCharacterTranslatedTextsHollow getLocalizedCharacterTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(142);
        return (LocalizedCharacterTranslatedTextsHollow)localizedCharacterTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedCharacterMapOfTranslatedTextsHollow> getAllLocalizedCharacterMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<LocalizedCharacterMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("LocalizedCharacterMapOfTranslatedTexts").getTypeState()) {
            protected LocalizedCharacterMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getLocalizedCharacterMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public LocalizedCharacterMapOfTranslatedTextsHollow getLocalizedCharacterMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(143);
        return (LocalizedCharacterMapOfTranslatedTextsHollow)localizedCharacterMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedCharacterHollow> getAllLocalizedCharacterHollow() {
        return new AllHollowRecordCollection<LocalizedCharacterHollow>(getDataAccess().getTypeDataAccess("LocalizedCharacter").getTypeState()) {
            protected LocalizedCharacterHollow getForOrdinal(int ordinal) {
                return getLocalizedCharacterHollow(ordinal);
            }
        };
    }
    public LocalizedCharacterHollow getLocalizedCharacterHollow(int ordinal) {
        objectCreationSampler.recordCreation(144);
        return (LocalizedCharacterHollow)localizedCharacterProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedMetadataTranslatedTextsHollow> getAllLocalizedMetadataTranslatedTextsHollow() {
        return new AllHollowRecordCollection<LocalizedMetadataTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("LocalizedMetadataTranslatedTexts").getTypeState()) {
            protected LocalizedMetadataTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getLocalizedMetadataTranslatedTextsHollow(ordinal);
            }
        };
    }
    public LocalizedMetadataTranslatedTextsHollow getLocalizedMetadataTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(145);
        return (LocalizedMetadataTranslatedTextsHollow)localizedMetadataTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedMetadataMapOfTranslatedTextsHollow> getAllLocalizedMetadataMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<LocalizedMetadataMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("LocalizedMetadataMapOfTranslatedTexts").getTypeState()) {
            protected LocalizedMetadataMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getLocalizedMetadataMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public LocalizedMetadataMapOfTranslatedTextsHollow getLocalizedMetadataMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(146);
        return (LocalizedMetadataMapOfTranslatedTextsHollow)localizedMetadataMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedMetadataHollow> getAllLocalizedMetadataHollow() {
        return new AllHollowRecordCollection<LocalizedMetadataHollow>(getDataAccess().getTypeDataAccess("LocalizedMetadata").getTypeState()) {
            protected LocalizedMetadataHollow getForOrdinal(int ordinal) {
                return getLocalizedMetadataHollow(ordinal);
            }
        };
    }
    public LocalizedMetadataHollow getLocalizedMetadataHollow(int ordinal) {
        objectCreationSampler.recordCreation(147);
        return (LocalizedMetadataHollow)localizedMetadataProvider.getHollowObject(ordinal);
    }
    public Collection<MovieRatingsRatingReasonTranslatedTextsHollow> getAllMovieRatingsRatingReasonTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MovieRatingsRatingReasonTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MovieRatingsRatingReasonTranslatedTexts").getTypeState()) {
            protected MovieRatingsRatingReasonTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMovieRatingsRatingReasonTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MovieRatingsRatingReasonTranslatedTextsHollow getMovieRatingsRatingReasonTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(148);
        return (MovieRatingsRatingReasonTranslatedTextsHollow)movieRatingsRatingReasonTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MovieRatingsRatingReasonMapOfTranslatedTextsHollow> getAllMovieRatingsRatingReasonMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MovieRatingsRatingReasonMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MovieRatingsRatingReasonMapOfTranslatedTexts").getTypeState()) {
            protected MovieRatingsRatingReasonMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMovieRatingsRatingReasonMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MovieRatingsRatingReasonMapOfTranslatedTextsHollow getMovieRatingsRatingReasonMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(149);
        return (MovieRatingsRatingReasonMapOfTranslatedTextsHollow)movieRatingsRatingReasonMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MovieRatingsRatingReasonHollow> getAllMovieRatingsRatingReasonHollow() {
        return new AllHollowRecordCollection<MovieRatingsRatingReasonHollow>(getDataAccess().getTypeDataAccess("MovieRatingsRatingReason").getTypeState()) {
            protected MovieRatingsRatingReasonHollow getForOrdinal(int ordinal) {
                return getMovieRatingsRatingReasonHollow(ordinal);
            }
        };
    }
    public MovieRatingsRatingReasonHollow getMovieRatingsRatingReasonHollow(int ordinal) {
        objectCreationSampler.recordCreation(150);
        return (MovieRatingsRatingReasonHollow)movieRatingsRatingReasonProvider.getHollowObject(ordinal);
    }
    public Collection<MovieRatingsHollow> getAllMovieRatingsHollow() {
        return new AllHollowRecordCollection<MovieRatingsHollow>(getDataAccess().getTypeDataAccess("MovieRatings").getTypeState()) {
            protected MovieRatingsHollow getForOrdinal(int ordinal) {
                return getMovieRatingsHollow(ordinal);
            }
        };
    }
    public MovieRatingsHollow getMovieRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(151);
        return (MovieRatingsHollow)movieRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesAkaTranslatedTextsHollow> getAllMoviesAkaTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesAkaTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesAkaTranslatedTexts").getTypeState()) {
            protected MoviesAkaTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesAkaTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesAkaTranslatedTextsHollow getMoviesAkaTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(152);
        return (MoviesAkaTranslatedTextsHollow)moviesAkaTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesAkaMapOfTranslatedTextsHollow> getAllMoviesAkaMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesAkaMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesAkaMapOfTranslatedTexts").getTypeState()) {
            protected MoviesAkaMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesAkaMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesAkaMapOfTranslatedTextsHollow getMoviesAkaMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(153);
        return (MoviesAkaMapOfTranslatedTextsHollow)moviesAkaMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesAkaHollow> getAllMoviesAkaHollow() {
        return new AllHollowRecordCollection<MoviesAkaHollow>(getDataAccess().getTypeDataAccess("MoviesAka").getTypeState()) {
            protected MoviesAkaHollow getForOrdinal(int ordinal) {
                return getMoviesAkaHollow(ordinal);
            }
        };
    }
    public MoviesAkaHollow getMoviesAkaHollow(int ordinal) {
        objectCreationSampler.recordCreation(154);
        return (MoviesAkaHollow)moviesAkaProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesDisplayNameTranslatedTextsHollow> getAllMoviesDisplayNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesDisplayNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesDisplayNameTranslatedTexts").getTypeState()) {
            protected MoviesDisplayNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesDisplayNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesDisplayNameTranslatedTextsHollow getMoviesDisplayNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(155);
        return (MoviesDisplayNameTranslatedTextsHollow)moviesDisplayNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesDisplayNameMapOfTranslatedTextsHollow> getAllMoviesDisplayNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesDisplayNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesDisplayNameMapOfTranslatedTexts").getTypeState()) {
            protected MoviesDisplayNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesDisplayNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesDisplayNameMapOfTranslatedTextsHollow getMoviesDisplayNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(156);
        return (MoviesDisplayNameMapOfTranslatedTextsHollow)moviesDisplayNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesDisplayNameHollow> getAllMoviesDisplayNameHollow() {
        return new AllHollowRecordCollection<MoviesDisplayNameHollow>(getDataAccess().getTypeDataAccess("MoviesDisplayName").getTypeState()) {
            protected MoviesDisplayNameHollow getForOrdinal(int ordinal) {
                return getMoviesDisplayNameHollow(ordinal);
            }
        };
    }
    public MoviesDisplayNameHollow getMoviesDisplayNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(157);
        return (MoviesDisplayNameHollow)moviesDisplayNameProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesOriginalTitleTranslatedTextsHollow> getAllMoviesOriginalTitleTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesOriginalTitleTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesOriginalTitleTranslatedTexts").getTypeState()) {
            protected MoviesOriginalTitleTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesOriginalTitleTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesOriginalTitleTranslatedTextsHollow getMoviesOriginalTitleTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(158);
        return (MoviesOriginalTitleTranslatedTextsHollow)moviesOriginalTitleTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesOriginalTitleMapOfTranslatedTextsHollow> getAllMoviesOriginalTitleMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesOriginalTitleMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesOriginalTitleMapOfTranslatedTexts").getTypeState()) {
            protected MoviesOriginalTitleMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesOriginalTitleMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesOriginalTitleMapOfTranslatedTextsHollow getMoviesOriginalTitleMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(159);
        return (MoviesOriginalTitleMapOfTranslatedTextsHollow)moviesOriginalTitleMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesOriginalTitleHollow> getAllMoviesOriginalTitleHollow() {
        return new AllHollowRecordCollection<MoviesOriginalTitleHollow>(getDataAccess().getTypeDataAccess("MoviesOriginalTitle").getTypeState()) {
            protected MoviesOriginalTitleHollow getForOrdinal(int ordinal) {
                return getMoviesOriginalTitleHollow(ordinal);
            }
        };
    }
    public MoviesOriginalTitleHollow getMoviesOriginalTitleHollow(int ordinal) {
        objectCreationSampler.recordCreation(160);
        return (MoviesOriginalTitleHollow)moviesOriginalTitleProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesShortDisplayNameTranslatedTextsHollow> getAllMoviesShortDisplayNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesShortDisplayNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesShortDisplayNameTranslatedTexts").getTypeState()) {
            protected MoviesShortDisplayNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesShortDisplayNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesShortDisplayNameTranslatedTextsHollow getMoviesShortDisplayNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(161);
        return (MoviesShortDisplayNameTranslatedTextsHollow)moviesShortDisplayNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesShortDisplayNameMapOfTranslatedTextsHollow> getAllMoviesShortDisplayNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesShortDisplayNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesShortDisplayNameMapOfTranslatedTexts").getTypeState()) {
            protected MoviesShortDisplayNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesShortDisplayNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesShortDisplayNameMapOfTranslatedTextsHollow getMoviesShortDisplayNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(162);
        return (MoviesShortDisplayNameMapOfTranslatedTextsHollow)moviesShortDisplayNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesShortDisplayNameHollow> getAllMoviesShortDisplayNameHollow() {
        return new AllHollowRecordCollection<MoviesShortDisplayNameHollow>(getDataAccess().getTypeDataAccess("MoviesShortDisplayName").getTypeState()) {
            protected MoviesShortDisplayNameHollow getForOrdinal(int ordinal) {
                return getMoviesShortDisplayNameHollow(ordinal);
            }
        };
    }
    public MoviesShortDisplayNameHollow getMoviesShortDisplayNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(163);
        return (MoviesShortDisplayNameHollow)moviesShortDisplayNameProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesSiteSynopsisTranslatedTextsHollow> getAllMoviesSiteSynopsisTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesSiteSynopsisTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesSiteSynopsisTranslatedTexts").getTypeState()) {
            protected MoviesSiteSynopsisTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesSiteSynopsisTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesSiteSynopsisTranslatedTextsHollow getMoviesSiteSynopsisTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(164);
        return (MoviesSiteSynopsisTranslatedTextsHollow)moviesSiteSynopsisTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesSiteSynopsisMapOfTranslatedTextsHollow> getAllMoviesSiteSynopsisMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesSiteSynopsisMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesSiteSynopsisMapOfTranslatedTexts").getTypeState()) {
            protected MoviesSiteSynopsisMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesSiteSynopsisMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesSiteSynopsisMapOfTranslatedTextsHollow getMoviesSiteSynopsisMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(165);
        return (MoviesSiteSynopsisMapOfTranslatedTextsHollow)moviesSiteSynopsisMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesSiteSynopsisHollow> getAllMoviesSiteSynopsisHollow() {
        return new AllHollowRecordCollection<MoviesSiteSynopsisHollow>(getDataAccess().getTypeDataAccess("MoviesSiteSynopsis").getTypeState()) {
            protected MoviesSiteSynopsisHollow getForOrdinal(int ordinal) {
                return getMoviesSiteSynopsisHollow(ordinal);
            }
        };
    }
    public MoviesSiteSynopsisHollow getMoviesSiteSynopsisHollow(int ordinal) {
        objectCreationSampler.recordCreation(166);
        return (MoviesSiteSynopsisHollow)moviesSiteSynopsisProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesTransliteratedTranslatedTextsHollow> getAllMoviesTransliteratedTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesTransliteratedTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesTransliteratedTranslatedTexts").getTypeState()) {
            protected MoviesTransliteratedTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesTransliteratedTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesTransliteratedTranslatedTextsHollow getMoviesTransliteratedTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(167);
        return (MoviesTransliteratedTranslatedTextsHollow)moviesTransliteratedTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesTransliteratedMapOfTranslatedTextsHollow> getAllMoviesTransliteratedMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesTransliteratedMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesTransliteratedMapOfTranslatedTexts").getTypeState()) {
            protected MoviesTransliteratedMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesTransliteratedMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesTransliteratedMapOfTranslatedTextsHollow getMoviesTransliteratedMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(168);
        return (MoviesTransliteratedMapOfTranslatedTextsHollow)moviesTransliteratedMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesTransliteratedHollow> getAllMoviesTransliteratedHollow() {
        return new AllHollowRecordCollection<MoviesTransliteratedHollow>(getDataAccess().getTypeDataAccess("MoviesTransliterated").getTypeState()) {
            protected MoviesTransliteratedHollow getForOrdinal(int ordinal) {
                return getMoviesTransliteratedHollow(ordinal);
            }
        };
    }
    public MoviesTransliteratedHollow getMoviesTransliteratedHollow(int ordinal) {
        objectCreationSampler.recordCreation(169);
        return (MoviesTransliteratedHollow)moviesTransliteratedProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesTvSynopsisTranslatedTextsHollow> getAllMoviesTvSynopsisTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesTvSynopsisTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesTvSynopsisTranslatedTexts").getTypeState()) {
            protected MoviesTvSynopsisTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesTvSynopsisTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesTvSynopsisTranslatedTextsHollow getMoviesTvSynopsisTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(170);
        return (MoviesTvSynopsisTranslatedTextsHollow)moviesTvSynopsisTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesTvSynopsisMapOfTranslatedTextsHollow> getAllMoviesTvSynopsisMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<MoviesTvSynopsisMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("MoviesTvSynopsisMapOfTranslatedTexts").getTypeState()) {
            protected MoviesTvSynopsisMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getMoviesTvSynopsisMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public MoviesTvSynopsisMapOfTranslatedTextsHollow getMoviesTvSynopsisMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(171);
        return (MoviesTvSynopsisMapOfTranslatedTextsHollow)moviesTvSynopsisMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesTvSynopsisHollow> getAllMoviesTvSynopsisHollow() {
        return new AllHollowRecordCollection<MoviesTvSynopsisHollow>(getDataAccess().getTypeDataAccess("MoviesTvSynopsis").getTypeState()) {
            protected MoviesTvSynopsisHollow getForOrdinal(int ordinal) {
                return getMoviesTvSynopsisHollow(ordinal);
            }
        };
    }
    public MoviesTvSynopsisHollow getMoviesTvSynopsisHollow(int ordinal) {
        objectCreationSampler.recordCreation(172);
        return (MoviesTvSynopsisHollow)moviesTvSynopsisProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesHollow> getAllMoviesHollow() {
        return new AllHollowRecordCollection<MoviesHollow>(getDataAccess().getTypeDataAccess("Movies").getTypeState()) {
            protected MoviesHollow getForOrdinal(int ordinal) {
                return getMoviesHollow(ordinal);
            }
        };
    }
    public MoviesHollow getMoviesHollow(int ordinal) {
        objectCreationSampler.recordCreation(173);
        return (MoviesHollow)moviesProvider.getHollowObject(ordinal);
    }
    public Collection<OriginServersHollow> getAllOriginServersHollow() {
        return new AllHollowRecordCollection<OriginServersHollow>(getDataAccess().getTypeDataAccess("OriginServers").getTypeState()) {
            protected OriginServersHollow getForOrdinal(int ordinal) {
                return getOriginServersHollow(ordinal);
            }
        };
    }
    public OriginServersHollow getOriginServersHollow(int ordinal) {
        objectCreationSampler.recordCreation(174);
        return (OriginServersHollow)originServersProvider.getHollowObject(ordinal);
    }
    public Collection<PersonAliasesNameTranslatedTextsHollow> getAllPersonAliasesNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<PersonAliasesNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("PersonAliasesNameTranslatedTexts").getTypeState()) {
            protected PersonAliasesNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getPersonAliasesNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public PersonAliasesNameTranslatedTextsHollow getPersonAliasesNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(175);
        return (PersonAliasesNameTranslatedTextsHollow)personAliasesNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonAliasesNameMapOfTranslatedTextsHollow> getAllPersonAliasesNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<PersonAliasesNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("PersonAliasesNameMapOfTranslatedTexts").getTypeState()) {
            protected PersonAliasesNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getPersonAliasesNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public PersonAliasesNameMapOfTranslatedTextsHollow getPersonAliasesNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(176);
        return (PersonAliasesNameMapOfTranslatedTextsHollow)personAliasesNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonAliasesNameHollow> getAllPersonAliasesNameHollow() {
        return new AllHollowRecordCollection<PersonAliasesNameHollow>(getDataAccess().getTypeDataAccess("PersonAliasesName").getTypeState()) {
            protected PersonAliasesNameHollow getForOrdinal(int ordinal) {
                return getPersonAliasesNameHollow(ordinal);
            }
        };
    }
    public PersonAliasesNameHollow getPersonAliasesNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(177);
        return (PersonAliasesNameHollow)personAliasesNameProvider.getHollowObject(ordinal);
    }
    public Collection<PersonAliasesHollow> getAllPersonAliasesHollow() {
        return new AllHollowRecordCollection<PersonAliasesHollow>(getDataAccess().getTypeDataAccess("PersonAliases").getTypeState()) {
            protected PersonAliasesHollow getForOrdinal(int ordinal) {
                return getPersonAliasesHollow(ordinal);
            }
        };
    }
    public PersonAliasesHollow getPersonAliasesHollow(int ordinal) {
        objectCreationSampler.recordCreation(178);
        return (PersonAliasesHollow)personAliasesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkAttributesHollow> getAllPersonArtworkAttributesHollow() {
        return new AllHollowRecordCollection<PersonArtworkAttributesHollow>(getDataAccess().getTypeDataAccess("PersonArtworkAttributes").getTypeState()) {
            protected PersonArtworkAttributesHollow getForOrdinal(int ordinal) {
                return getPersonArtworkAttributesHollow(ordinal);
            }
        };
    }
    public PersonArtworkAttributesHollow getPersonArtworkAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(179);
        return (PersonArtworkAttributesHollow)personArtworkAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkDerivativesHollow> getAllPersonArtworkDerivativesHollow() {
        return new AllHollowRecordCollection<PersonArtworkDerivativesHollow>(getDataAccess().getTypeDataAccess("PersonArtworkDerivatives").getTypeState()) {
            protected PersonArtworkDerivativesHollow getForOrdinal(int ordinal) {
                return getPersonArtworkDerivativesHollow(ordinal);
            }
        };
    }
    public PersonArtworkDerivativesHollow getPersonArtworkDerivativesHollow(int ordinal) {
        objectCreationSampler.recordCreation(180);
        return (PersonArtworkDerivativesHollow)personArtworkDerivativesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkArrayOfDerivativesHollow> getAllPersonArtworkArrayOfDerivativesHollow() {
        return new AllHollowRecordCollection<PersonArtworkArrayOfDerivativesHollow>(getDataAccess().getTypeDataAccess("PersonArtworkArrayOfDerivatives").getTypeState()) {
            protected PersonArtworkArrayOfDerivativesHollow getForOrdinal(int ordinal) {
                return getPersonArtworkArrayOfDerivativesHollow(ordinal);
            }
        };
    }
    public PersonArtworkArrayOfDerivativesHollow getPersonArtworkArrayOfDerivativesHollow(int ordinal) {
        objectCreationSampler.recordCreation(181);
        return (PersonArtworkArrayOfDerivativesHollow)personArtworkArrayOfDerivativesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkLocalesTerritoryCodesHollow> getAllPersonArtworkLocalesTerritoryCodesHollow() {
        return new AllHollowRecordCollection<PersonArtworkLocalesTerritoryCodesHollow>(getDataAccess().getTypeDataAccess("PersonArtworkLocalesTerritoryCodes").getTypeState()) {
            protected PersonArtworkLocalesTerritoryCodesHollow getForOrdinal(int ordinal) {
                return getPersonArtworkLocalesTerritoryCodesHollow(ordinal);
            }
        };
    }
    public PersonArtworkLocalesTerritoryCodesHollow getPersonArtworkLocalesTerritoryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(182);
        return (PersonArtworkLocalesTerritoryCodesHollow)personArtworkLocalesTerritoryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkLocalesArrayOfTerritoryCodesHollow> getAllPersonArtworkLocalesArrayOfTerritoryCodesHollow() {
        return new AllHollowRecordCollection<PersonArtworkLocalesArrayOfTerritoryCodesHollow>(getDataAccess().getTypeDataAccess("PersonArtworkLocalesArrayOfTerritoryCodes").getTypeState()) {
            protected PersonArtworkLocalesArrayOfTerritoryCodesHollow getForOrdinal(int ordinal) {
                return getPersonArtworkLocalesArrayOfTerritoryCodesHollow(ordinal);
            }
        };
    }
    public PersonArtworkLocalesArrayOfTerritoryCodesHollow getPersonArtworkLocalesArrayOfTerritoryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(183);
        return (PersonArtworkLocalesArrayOfTerritoryCodesHollow)personArtworkLocalesArrayOfTerritoryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkLocalesHollow> getAllPersonArtworkLocalesHollow() {
        return new AllHollowRecordCollection<PersonArtworkLocalesHollow>(getDataAccess().getTypeDataAccess("PersonArtworkLocales").getTypeState()) {
            protected PersonArtworkLocalesHollow getForOrdinal(int ordinal) {
                return getPersonArtworkLocalesHollow(ordinal);
            }
        };
    }
    public PersonArtworkLocalesHollow getPersonArtworkLocalesHollow(int ordinal) {
        objectCreationSampler.recordCreation(184);
        return (PersonArtworkLocalesHollow)personArtworkLocalesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkArrayOfLocalesHollow> getAllPersonArtworkArrayOfLocalesHollow() {
        return new AllHollowRecordCollection<PersonArtworkArrayOfLocalesHollow>(getDataAccess().getTypeDataAccess("PersonArtworkArrayOfLocales").getTypeState()) {
            protected PersonArtworkArrayOfLocalesHollow getForOrdinal(int ordinal) {
                return getPersonArtworkArrayOfLocalesHollow(ordinal);
            }
        };
    }
    public PersonArtworkArrayOfLocalesHollow getPersonArtworkArrayOfLocalesHollow(int ordinal) {
        objectCreationSampler.recordCreation(185);
        return (PersonArtworkArrayOfLocalesHollow)personArtworkArrayOfLocalesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkHollow> getAllPersonArtworkHollow() {
        return new AllHollowRecordCollection<PersonArtworkHollow>(getDataAccess().getTypeDataAccess("PersonArtwork").getTypeState()) {
            protected PersonArtworkHollow getForOrdinal(int ordinal) {
                return getPersonArtworkHollow(ordinal);
            }
        };
    }
    public PersonArtworkHollow getPersonArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(186);
        return (PersonArtworkHollow)personArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsBioTranslatedTextsHollow> getAllPersonsBioTranslatedTextsHollow() {
        return new AllHollowRecordCollection<PersonsBioTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("PersonsBioTranslatedTexts").getTypeState()) {
            protected PersonsBioTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getPersonsBioTranslatedTextsHollow(ordinal);
            }
        };
    }
    public PersonsBioTranslatedTextsHollow getPersonsBioTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(187);
        return (PersonsBioTranslatedTextsHollow)personsBioTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsBioMapOfTranslatedTextsHollow> getAllPersonsBioMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<PersonsBioMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("PersonsBioMapOfTranslatedTexts").getTypeState()) {
            protected PersonsBioMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getPersonsBioMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public PersonsBioMapOfTranslatedTextsHollow getPersonsBioMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(188);
        return (PersonsBioMapOfTranslatedTextsHollow)personsBioMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsBioHollow> getAllPersonsBioHollow() {
        return new AllHollowRecordCollection<PersonsBioHollow>(getDataAccess().getTypeDataAccess("PersonsBio").getTypeState()) {
            protected PersonsBioHollow getForOrdinal(int ordinal) {
                return getPersonsBioHollow(ordinal);
            }
        };
    }
    public PersonsBioHollow getPersonsBioHollow(int ordinal) {
        objectCreationSampler.recordCreation(189);
        return (PersonsBioHollow)personsBioProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsNameTranslatedTextsHollow> getAllPersonsNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<PersonsNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("PersonsNameTranslatedTexts").getTypeState()) {
            protected PersonsNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getPersonsNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public PersonsNameTranslatedTextsHollow getPersonsNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(190);
        return (PersonsNameTranslatedTextsHollow)personsNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsNameMapOfTranslatedTextsHollow> getAllPersonsNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<PersonsNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("PersonsNameMapOfTranslatedTexts").getTypeState()) {
            protected PersonsNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getPersonsNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public PersonsNameMapOfTranslatedTextsHollow getPersonsNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(191);
        return (PersonsNameMapOfTranslatedTextsHollow)personsNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsNameHollow> getAllPersonsNameHollow() {
        return new AllHollowRecordCollection<PersonsNameHollow>(getDataAccess().getTypeDataAccess("PersonsName").getTypeState()) {
            protected PersonsNameHollow getForOrdinal(int ordinal) {
                return getPersonsNameHollow(ordinal);
            }
        };
    }
    public PersonsNameHollow getPersonsNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(192);
        return (PersonsNameHollow)personsNameProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsHollow> getAllPersonsHollow() {
        return new AllHollowRecordCollection<PersonsHollow>(getDataAccess().getTypeDataAccess("Persons").getTypeState()) {
            protected PersonsHollow getForOrdinal(int ordinal) {
                return getPersonsHollow(ordinal);
            }
        };
    }
    public PersonsHollow getPersonsHollow(int ordinal) {
        objectCreationSampler.recordCreation(193);
        return (PersonsHollow)personsProvider.getHollowObject(ordinal);
    }
    public Collection<ProtectionTypesHollow> getAllProtectionTypesHollow() {
        return new AllHollowRecordCollection<ProtectionTypesHollow>(getDataAccess().getTypeDataAccess("ProtectionTypes").getTypeState()) {
            protected ProtectionTypesHollow getForOrdinal(int ordinal) {
                return getProtectionTypesHollow(ordinal);
            }
        };
    }
    public ProtectionTypesHollow getProtectionTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(194);
        return (ProtectionTypesHollow)protectionTypesProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsDescriptionTranslatedTextsHollow> getAllRatingsDescriptionTranslatedTextsHollow() {
        return new AllHollowRecordCollection<RatingsDescriptionTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("RatingsDescriptionTranslatedTexts").getTypeState()) {
            protected RatingsDescriptionTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getRatingsDescriptionTranslatedTextsHollow(ordinal);
            }
        };
    }
    public RatingsDescriptionTranslatedTextsHollow getRatingsDescriptionTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(195);
        return (RatingsDescriptionTranslatedTextsHollow)ratingsDescriptionTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsDescriptionMapOfTranslatedTextsHollow> getAllRatingsDescriptionMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<RatingsDescriptionMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("RatingsDescriptionMapOfTranslatedTexts").getTypeState()) {
            protected RatingsDescriptionMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getRatingsDescriptionMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public RatingsDescriptionMapOfTranslatedTextsHollow getRatingsDescriptionMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(196);
        return (RatingsDescriptionMapOfTranslatedTextsHollow)ratingsDescriptionMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsDescriptionHollow> getAllRatingsDescriptionHollow() {
        return new AllHollowRecordCollection<RatingsDescriptionHollow>(getDataAccess().getTypeDataAccess("RatingsDescription").getTypeState()) {
            protected RatingsDescriptionHollow getForOrdinal(int ordinal) {
                return getRatingsDescriptionHollow(ordinal);
            }
        };
    }
    public RatingsDescriptionHollow getRatingsDescriptionHollow(int ordinal) {
        objectCreationSampler.recordCreation(197);
        return (RatingsDescriptionHollow)ratingsDescriptionProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsRatingCodeTranslatedTextsHollow> getAllRatingsRatingCodeTranslatedTextsHollow() {
        return new AllHollowRecordCollection<RatingsRatingCodeTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("RatingsRatingCodeTranslatedTexts").getTypeState()) {
            protected RatingsRatingCodeTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getRatingsRatingCodeTranslatedTextsHollow(ordinal);
            }
        };
    }
    public RatingsRatingCodeTranslatedTextsHollow getRatingsRatingCodeTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(198);
        return (RatingsRatingCodeTranslatedTextsHollow)ratingsRatingCodeTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsRatingCodeMapOfTranslatedTextsHollow> getAllRatingsRatingCodeMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<RatingsRatingCodeMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("RatingsRatingCodeMapOfTranslatedTexts").getTypeState()) {
            protected RatingsRatingCodeMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getRatingsRatingCodeMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public RatingsRatingCodeMapOfTranslatedTextsHollow getRatingsRatingCodeMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(199);
        return (RatingsRatingCodeMapOfTranslatedTextsHollow)ratingsRatingCodeMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsRatingCodeHollow> getAllRatingsRatingCodeHollow() {
        return new AllHollowRecordCollection<RatingsRatingCodeHollow>(getDataAccess().getTypeDataAccess("RatingsRatingCode").getTypeState()) {
            protected RatingsRatingCodeHollow getForOrdinal(int ordinal) {
                return getRatingsRatingCodeHollow(ordinal);
            }
        };
    }
    public RatingsRatingCodeHollow getRatingsRatingCodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(200);
        return (RatingsRatingCodeHollow)ratingsRatingCodeProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsHollow> getAllRatingsHollow() {
        return new AllHollowRecordCollection<RatingsHollow>(getDataAccess().getTypeDataAccess("Ratings").getTypeState()) {
            protected RatingsHollow getForOrdinal(int ordinal) {
                return getRatingsHollow(ordinal);
            }
        };
    }
    public RatingsHollow getRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(201);
        return (RatingsHollow)ratingsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArtwork_newSourceFileIdsHollow> getAllRolloutPhasesElementsArtwork_newSourceFileIdsHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArtwork_newSourceFileIdsHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArtwork_newSourceFileIds").getTypeState()) {
            protected RolloutPhasesElementsArtwork_newSourceFileIdsHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArtwork_newSourceFileIdsHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArtwork_newSourceFileIdsHollow getRolloutPhasesElementsArtwork_newSourceFileIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(202);
        return (RolloutPhasesElementsArtwork_newSourceFileIdsHollow)rolloutPhasesElementsArtwork_newSourceFileIdsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow> getAllRolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArtwork_newArrayOfSourceFileIds").getTypeState()) {
            protected RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow getRolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(203);
        return (RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow)rolloutPhasesElementsArtwork_newArrayOfSourceFileIdsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArtwork_newHollow> getAllRolloutPhasesElementsArtwork_newHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArtwork_newHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArtwork_new").getTypeState()) {
            protected RolloutPhasesElementsArtwork_newHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArtwork_newHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArtwork_newHollow getRolloutPhasesElementsArtwork_newHollow(int ordinal) {
        objectCreationSampler.recordCreation(204);
        return (RolloutPhasesElementsArtwork_newHollow)rolloutPhasesElementsArtwork_newProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsLocalized_metadataHollow> getAllRolloutPhasesElementsLocalized_metadataHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsLocalized_metadataHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsLocalized_metadata").getTypeState()) {
            protected RolloutPhasesElementsLocalized_metadataHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsLocalized_metadataHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsLocalized_metadataHollow getRolloutPhasesElementsLocalized_metadataHollow(int ordinal) {
        objectCreationSampler.recordCreation(205);
        return (RolloutPhasesElementsLocalized_metadataHollow)rolloutPhasesElementsLocalized_metadataProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsTrailersSupplementalInfoHollow> getAllRolloutPhasesElementsTrailersSupplementalInfoHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsTrailersSupplementalInfoHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsTrailersSupplementalInfo").getTypeState()) {
            protected RolloutPhasesElementsTrailersSupplementalInfoHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsTrailersSupplementalInfoHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsTrailersSupplementalInfoHollow getRolloutPhasesElementsTrailersSupplementalInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(206);
        return (RolloutPhasesElementsTrailersSupplementalInfoHollow)rolloutPhasesElementsTrailersSupplementalInfoProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsTrailersMapOfSupplementalInfoHollow> getAllRolloutPhasesElementsTrailersMapOfSupplementalInfoHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsTrailersMapOfSupplementalInfoHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsTrailersMapOfSupplementalInfo").getTypeState()) {
            protected RolloutPhasesElementsTrailersMapOfSupplementalInfoHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsTrailersMapOfSupplementalInfoHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsTrailersMapOfSupplementalInfoHollow getRolloutPhasesElementsTrailersMapOfSupplementalInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(207);
        return (RolloutPhasesElementsTrailersMapOfSupplementalInfoHollow)rolloutPhasesElementsTrailersMapOfSupplementalInfoProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsTrailersHollow> getAllRolloutPhasesElementsTrailersHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsTrailersHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsTrailers").getTypeState()) {
            protected RolloutPhasesElementsTrailersHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsTrailersHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsTrailersHollow getRolloutPhasesElementsTrailersHollow(int ordinal) {
        objectCreationSampler.recordCreation(208);
        return (RolloutPhasesElementsTrailersHollow)rolloutPhasesElementsTrailersProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsArrayOfTrailersHollow> getAllRolloutPhasesElementsArrayOfTrailersHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsArrayOfTrailersHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsArrayOfTrailers").getTypeState()) {
            protected RolloutPhasesElementsArrayOfTrailersHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsArrayOfTrailersHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsArrayOfTrailersHollow getRolloutPhasesElementsArrayOfTrailersHollow(int ordinal) {
        objectCreationSampler.recordCreation(209);
        return (RolloutPhasesElementsArrayOfTrailersHollow)rolloutPhasesElementsArrayOfTrailersProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsHollow> getAllRolloutPhasesElementsHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElements").getTypeState()) {
            protected RolloutPhasesElementsHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsHollow getRolloutPhasesElementsHollow(int ordinal) {
        objectCreationSampler.recordCreation(210);
        return (RolloutPhasesElementsHollow)rolloutPhasesElementsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesHollow> getAllRolloutPhasesHollow() {
        return new AllHollowRecordCollection<RolloutPhasesHollow>(getDataAccess().getTypeDataAccess("RolloutPhases").getTypeState()) {
            protected RolloutPhasesHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesHollow(ordinal);
            }
        };
    }
    public RolloutPhasesHollow getRolloutPhasesHollow(int ordinal) {
        objectCreationSampler.recordCreation(211);
        return (RolloutPhasesHollow)rolloutPhasesProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutArrayOfPhasesHollow> getAllRolloutArrayOfPhasesHollow() {
        return new AllHollowRecordCollection<RolloutArrayOfPhasesHollow>(getDataAccess().getTypeDataAccess("RolloutArrayOfPhases").getTypeState()) {
            protected RolloutArrayOfPhasesHollow getForOrdinal(int ordinal) {
                return getRolloutArrayOfPhasesHollow(ordinal);
            }
        };
    }
    public RolloutArrayOfPhasesHollow getRolloutArrayOfPhasesHollow(int ordinal) {
        objectCreationSampler.recordCreation(212);
        return (RolloutArrayOfPhasesHollow)rolloutArrayOfPhasesProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutHollow> getAllRolloutHollow() {
        return new AllHollowRecordCollection<RolloutHollow>(getDataAccess().getTypeDataAccess("Rollout").getTypeState()) {
            protected RolloutHollow getForOrdinal(int ordinal) {
                return getRolloutHollow(ordinal);
            }
        };
    }
    public RolloutHollow getRolloutHollow(int ordinal) {
        objectCreationSampler.recordCreation(213);
        return (RolloutHollow)rolloutProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypesDisplayNameTranslatedTextsHollow> getAllShowMemberTypesDisplayNameTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ShowMemberTypesDisplayNameTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ShowMemberTypesDisplayNameTranslatedTexts").getTypeState()) {
            protected ShowMemberTypesDisplayNameTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getShowMemberTypesDisplayNameTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ShowMemberTypesDisplayNameTranslatedTextsHollow getShowMemberTypesDisplayNameTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(214);
        return (ShowMemberTypesDisplayNameTranslatedTextsHollow)showMemberTypesDisplayNameTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypesDisplayNameMapOfTranslatedTextsHollow> getAllShowMemberTypesDisplayNameMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<ShowMemberTypesDisplayNameMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("ShowMemberTypesDisplayNameMapOfTranslatedTexts").getTypeState()) {
            protected ShowMemberTypesDisplayNameMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getShowMemberTypesDisplayNameMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public ShowMemberTypesDisplayNameMapOfTranslatedTextsHollow getShowMemberTypesDisplayNameMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(215);
        return (ShowMemberTypesDisplayNameMapOfTranslatedTextsHollow)showMemberTypesDisplayNameMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypesDisplayNameHollow> getAllShowMemberTypesDisplayNameHollow() {
        return new AllHollowRecordCollection<ShowMemberTypesDisplayNameHollow>(getDataAccess().getTypeDataAccess("ShowMemberTypesDisplayName").getTypeState()) {
            protected ShowMemberTypesDisplayNameHollow getForOrdinal(int ordinal) {
                return getShowMemberTypesDisplayNameHollow(ordinal);
            }
        };
    }
    public ShowMemberTypesDisplayNameHollow getShowMemberTypesDisplayNameHollow(int ordinal) {
        objectCreationSampler.recordCreation(216);
        return (ShowMemberTypesDisplayNameHollow)showMemberTypesDisplayNameProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypesHollow> getAllShowMemberTypesHollow() {
        return new AllHollowRecordCollection<ShowMemberTypesHollow>(getDataAccess().getTypeDataAccess("ShowMemberTypes").getTypeState()) {
            protected ShowMemberTypesHollow getForOrdinal(int ordinal) {
                return getShowMemberTypesHollow(ordinal);
            }
        };
    }
    public ShowMemberTypesHollow getShowMemberTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(217);
        return (ShowMemberTypesHollow)showMemberTypesProvider.getHollowObject(ordinal);
    }
    public Collection<StorageGroupsCountriesHollow> getAllStorageGroupsCountriesHollow() {
        return new AllHollowRecordCollection<StorageGroupsCountriesHollow>(getDataAccess().getTypeDataAccess("StorageGroupsCountries").getTypeState()) {
            protected StorageGroupsCountriesHollow getForOrdinal(int ordinal) {
                return getStorageGroupsCountriesHollow(ordinal);
            }
        };
    }
    public StorageGroupsCountriesHollow getStorageGroupsCountriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(218);
        return (StorageGroupsCountriesHollow)storageGroupsCountriesProvider.getHollowObject(ordinal);
    }
    public Collection<StorageGroupsArrayOfCountriesHollow> getAllStorageGroupsArrayOfCountriesHollow() {
        return new AllHollowRecordCollection<StorageGroupsArrayOfCountriesHollow>(getDataAccess().getTypeDataAccess("StorageGroupsArrayOfCountries").getTypeState()) {
            protected StorageGroupsArrayOfCountriesHollow getForOrdinal(int ordinal) {
                return getStorageGroupsArrayOfCountriesHollow(ordinal);
            }
        };
    }
    public StorageGroupsArrayOfCountriesHollow getStorageGroupsArrayOfCountriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(219);
        return (StorageGroupsArrayOfCountriesHollow)storageGroupsArrayOfCountriesProvider.getHollowObject(ordinal);
    }
    public Collection<StorageGroupsHollow> getAllStorageGroupsHollow() {
        return new AllHollowRecordCollection<StorageGroupsHollow>(getDataAccess().getTypeDataAccess("StorageGroups").getTypeState()) {
            protected StorageGroupsHollow getForOrdinal(int ordinal) {
                return getStorageGroupsHollow(ordinal);
            }
        };
    }
    public StorageGroupsHollow getStorageGroupsHollow(int ordinal) {
        objectCreationSampler.recordCreation(220);
        return (StorageGroupsHollow)storageGroupsProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesHooksTranslatedTextsHollow> getAllStories_SynopsesHooksTranslatedTextsHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesHooksTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("Stories_SynopsesHooksTranslatedTexts").getTypeState()) {
            protected Stories_SynopsesHooksTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesHooksTranslatedTextsHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesHooksTranslatedTextsHollow getStories_SynopsesHooksTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(221);
        return (Stories_SynopsesHooksTranslatedTextsHollow)stories_SynopsesHooksTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesHooksMapOfTranslatedTextsHollow> getAllStories_SynopsesHooksMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesHooksMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("Stories_SynopsesHooksMapOfTranslatedTexts").getTypeState()) {
            protected Stories_SynopsesHooksMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesHooksMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesHooksMapOfTranslatedTextsHollow getStories_SynopsesHooksMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(222);
        return (Stories_SynopsesHooksMapOfTranslatedTextsHollow)stories_SynopsesHooksMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesHooksHollow> getAllStories_SynopsesHooksHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesHooksHollow>(getDataAccess().getTypeDataAccess("Stories_SynopsesHooks").getTypeState()) {
            protected Stories_SynopsesHooksHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesHooksHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesHooksHollow getStories_SynopsesHooksHollow(int ordinal) {
        objectCreationSampler.recordCreation(223);
        return (Stories_SynopsesHooksHollow)stories_SynopsesHooksProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesArrayOfHooksHollow> getAllStories_SynopsesArrayOfHooksHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesArrayOfHooksHollow>(getDataAccess().getTypeDataAccess("Stories_SynopsesArrayOfHooks").getTypeState()) {
            protected Stories_SynopsesArrayOfHooksHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesArrayOfHooksHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesArrayOfHooksHollow getStories_SynopsesArrayOfHooksHollow(int ordinal) {
        objectCreationSampler.recordCreation(224);
        return (Stories_SynopsesArrayOfHooksHollow)stories_SynopsesArrayOfHooksProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesNarrativeTextTranslatedTextsHollow> getAllStories_SynopsesNarrativeTextTranslatedTextsHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesNarrativeTextTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("Stories_SynopsesNarrativeTextTranslatedTexts").getTypeState()) {
            protected Stories_SynopsesNarrativeTextTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesNarrativeTextTranslatedTextsHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesNarrativeTextTranslatedTextsHollow getStories_SynopsesNarrativeTextTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(225);
        return (Stories_SynopsesNarrativeTextTranslatedTextsHollow)stories_SynopsesNarrativeTextTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesNarrativeTextMapOfTranslatedTextsHollow> getAllStories_SynopsesNarrativeTextMapOfTranslatedTextsHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesNarrativeTextMapOfTranslatedTextsHollow>(getDataAccess().getTypeDataAccess("Stories_SynopsesNarrativeTextMapOfTranslatedTexts").getTypeState()) {
            protected Stories_SynopsesNarrativeTextMapOfTranslatedTextsHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesNarrativeTextMapOfTranslatedTextsHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesNarrativeTextMapOfTranslatedTextsHollow getStories_SynopsesNarrativeTextMapOfTranslatedTextsHollow(int ordinal) {
        objectCreationSampler.recordCreation(226);
        return (Stories_SynopsesNarrativeTextMapOfTranslatedTextsHollow)stories_SynopsesNarrativeTextMapOfTranslatedTextsProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesNarrativeTextHollow> getAllStories_SynopsesNarrativeTextHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesNarrativeTextHollow>(getDataAccess().getTypeDataAccess("Stories_SynopsesNarrativeText").getTypeState()) {
            protected Stories_SynopsesNarrativeTextHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesNarrativeTextHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesNarrativeTextHollow getStories_SynopsesNarrativeTextHollow(int ordinal) {
        objectCreationSampler.recordCreation(227);
        return (Stories_SynopsesNarrativeTextHollow)stories_SynopsesNarrativeTextProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesHollow> getAllStories_SynopsesHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesHollow>(getDataAccess().getTypeDataAccess("Stories_Synopses").getTypeState()) {
            protected Stories_SynopsesHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesHollow getStories_SynopsesHollow(int ordinal) {
        objectCreationSampler.recordCreation(228);
        return (Stories_SynopsesHollow)stories_SynopsesProvider.getHollowObject(ordinal);
    }
    public Collection<StreamProfileGroupsHollow> getAllStreamProfileGroupsHollow() {
        return new AllHollowRecordCollection<StreamProfileGroupsHollow>(getDataAccess().getTypeDataAccess("StreamProfileGroups").getTypeState()) {
            protected StreamProfileGroupsHollow getForOrdinal(int ordinal) {
                return getStreamProfileGroupsHollow(ordinal);
            }
        };
    }
    public StreamProfileGroupsHollow getStreamProfileGroupsHollow(int ordinal) {
        objectCreationSampler.recordCreation(229);
        return (StreamProfileGroupsHollow)streamProfileGroupsProvider.getHollowObject(ordinal);
    }
    public Collection<StreamProfilesHollow> getAllStreamProfilesHollow() {
        return new AllHollowRecordCollection<StreamProfilesHollow>(getDataAccess().getTypeDataAccess("StreamProfiles").getTypeState()) {
            protected StreamProfilesHollow getForOrdinal(int ordinal) {
                return getStreamProfilesHollow(ordinal);
            }
        };
    }
    public StreamProfilesHollow getStreamProfilesHollow(int ordinal) {
        objectCreationSampler.recordCreation(230);
        return (StreamProfilesHollow)streamProfilesProvider.getHollowObject(ordinal);
    }
    public Collection<TerritoryCountriesCountryCodesHollow> getAllTerritoryCountriesCountryCodesHollow() {
        return new AllHollowRecordCollection<TerritoryCountriesCountryCodesHollow>(getDataAccess().getTypeDataAccess("TerritoryCountriesCountryCodes").getTypeState()) {
            protected TerritoryCountriesCountryCodesHollow getForOrdinal(int ordinal) {
                return getTerritoryCountriesCountryCodesHollow(ordinal);
            }
        };
    }
    public TerritoryCountriesCountryCodesHollow getTerritoryCountriesCountryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(231);
        return (TerritoryCountriesCountryCodesHollow)territoryCountriesCountryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<TerritoryCountriesArrayOfCountryCodesHollow> getAllTerritoryCountriesArrayOfCountryCodesHollow() {
        return new AllHollowRecordCollection<TerritoryCountriesArrayOfCountryCodesHollow>(getDataAccess().getTypeDataAccess("TerritoryCountriesArrayOfCountryCodes").getTypeState()) {
            protected TerritoryCountriesArrayOfCountryCodesHollow getForOrdinal(int ordinal) {
                return getTerritoryCountriesArrayOfCountryCodesHollow(ordinal);
            }
        };
    }
    public TerritoryCountriesArrayOfCountryCodesHollow getTerritoryCountriesArrayOfCountryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(232);
        return (TerritoryCountriesArrayOfCountryCodesHollow)territoryCountriesArrayOfCountryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<TerritoryCountriesHollow> getAllTerritoryCountriesHollow() {
        return new AllHollowRecordCollection<TerritoryCountriesHollow>(getDataAccess().getTypeDataAccess("TerritoryCountries").getTypeState()) {
            protected TerritoryCountriesHollow getForOrdinal(int ordinal) {
                return getTerritoryCountriesHollow(ordinal);
            }
        };
    }
    public TerritoryCountriesHollow getTerritoryCountriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(233);
        return (TerritoryCountriesHollow)territoryCountriesProvider.getHollowObject(ordinal);
    }
    public Collection<TopNAttributesHollow> getAllTopNAttributesHollow() {
        return new AllHollowRecordCollection<TopNAttributesHollow>(getDataAccess().getTypeDataAccess("TopNAttributes").getTypeState()) {
            protected TopNAttributesHollow getForOrdinal(int ordinal) {
                return getTopNAttributesHollow(ordinal);
            }
        };
    }
    public TopNAttributesHollow getTopNAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(234);
        return (TopNAttributesHollow)topNAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<TopNArrayOfAttributesHollow> getAllTopNArrayOfAttributesHollow() {
        return new AllHollowRecordCollection<TopNArrayOfAttributesHollow>(getDataAccess().getTypeDataAccess("TopNArrayOfAttributes").getTypeState()) {
            protected TopNArrayOfAttributesHollow getForOrdinal(int ordinal) {
                return getTopNArrayOfAttributesHollow(ordinal);
            }
        };
    }
    public TopNArrayOfAttributesHollow getTopNArrayOfAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(235);
        return (TopNArrayOfAttributesHollow)topNArrayOfAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<TopNHollow> getAllTopNHollow() {
        return new AllHollowRecordCollection<TopNHollow>(getDataAccess().getTypeDataAccess("TopN").getTypeState()) {
            protected TopNHollow getForOrdinal(int ordinal) {
                return getTopNHollow(ordinal);
            }
        };
    }
    public TopNHollow getTopNHollow(int ordinal) {
        objectCreationSampler.recordCreation(236);
        return (TopNHollow)topNProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerTrailersThemesHollow> getAllTrailerTrailersThemesHollow() {
        return new AllHollowRecordCollection<TrailerTrailersThemesHollow>(getDataAccess().getTypeDataAccess("TrailerTrailersThemes").getTypeState()) {
            protected TrailerTrailersThemesHollow getForOrdinal(int ordinal) {
                return getTrailerTrailersThemesHollow(ordinal);
            }
        };
    }
    public TrailerTrailersThemesHollow getTrailerTrailersThemesHollow(int ordinal) {
        objectCreationSampler.recordCreation(237);
        return (TrailerTrailersThemesHollow)trailerTrailersThemesProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerTrailersArrayOfThemesHollow> getAllTrailerTrailersArrayOfThemesHollow() {
        return new AllHollowRecordCollection<TrailerTrailersArrayOfThemesHollow>(getDataAccess().getTypeDataAccess("TrailerTrailersArrayOfThemes").getTypeState()) {
            protected TrailerTrailersArrayOfThemesHollow getForOrdinal(int ordinal) {
                return getTrailerTrailersArrayOfThemesHollow(ordinal);
            }
        };
    }
    public TrailerTrailersArrayOfThemesHollow getTrailerTrailersArrayOfThemesHollow(int ordinal) {
        objectCreationSampler.recordCreation(238);
        return (TrailerTrailersArrayOfThemesHollow)trailerTrailersArrayOfThemesProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerTrailersHollow> getAllTrailerTrailersHollow() {
        return new AllHollowRecordCollection<TrailerTrailersHollow>(getDataAccess().getTypeDataAccess("TrailerTrailers").getTypeState()) {
            protected TrailerTrailersHollow getForOrdinal(int ordinal) {
                return getTrailerTrailersHollow(ordinal);
            }
        };
    }
    public TrailerTrailersHollow getTrailerTrailersHollow(int ordinal) {
        objectCreationSampler.recordCreation(239);
        return (TrailerTrailersHollow)trailerTrailersProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerArrayOfTrailersHollow> getAllTrailerArrayOfTrailersHollow() {
        return new AllHollowRecordCollection<TrailerArrayOfTrailersHollow>(getDataAccess().getTypeDataAccess("TrailerArrayOfTrailers").getTypeState()) {
            protected TrailerArrayOfTrailersHollow getForOrdinal(int ordinal) {
                return getTrailerArrayOfTrailersHollow(ordinal);
            }
        };
    }
    public TrailerArrayOfTrailersHollow getTrailerArrayOfTrailersHollow(int ordinal) {
        objectCreationSampler.recordCreation(240);
        return (TrailerArrayOfTrailersHollow)trailerArrayOfTrailersProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerHollow> getAllTrailerHollow() {
        return new AllHollowRecordCollection<TrailerHollow>(getDataAccess().getTypeDataAccess("Trailer").getTypeState()) {
            protected TrailerHollow getForOrdinal(int ordinal) {
                return getTrailerHollow(ordinal);
            }
        };
    }
    public TrailerHollow getTrailerHollow(int ordinal) {
        objectCreationSampler.recordCreation(241);
        return (TrailerHollow)trailerProvider.getHollowObject(ordinal);
    }
    public Collection<VMSAwardHollow> getAllVMSAwardHollow() {
        return new AllHollowRecordCollection<VMSAwardHollow>(getDataAccess().getTypeDataAccess("VMSAward").getTypeState()) {
            protected VMSAwardHollow getForOrdinal(int ordinal) {
                return getVMSAwardHollow(ordinal);
            }
        };
    }
    public VMSAwardHollow getVMSAwardHollow(int ordinal) {
        objectCreationSampler.recordCreation(242);
        return (VMSAwardHollow)vMSAwardProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkAttributesHollow> getAllVideoArtWorkAttributesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkAttributesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkAttributes").getTypeState()) {
            protected VideoArtWorkAttributesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkAttributesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkAttributesHollow getVideoArtWorkAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(243);
        return (VideoArtWorkAttributesHollow)videoArtWorkAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkArrayOfAttributesHollow> getAllVideoArtWorkArrayOfAttributesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkArrayOfAttributesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkArrayOfAttributes").getTypeState()) {
            protected VideoArtWorkArrayOfAttributesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkArrayOfAttributesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkArrayOfAttributesHollow getVideoArtWorkArrayOfAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(244);
        return (VideoArtWorkArrayOfAttributesHollow)videoArtWorkArrayOfAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkExtensionsHollow> getAllVideoArtWorkExtensionsHollow() {
        return new AllHollowRecordCollection<VideoArtWorkExtensionsHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkExtensions").getTypeState()) {
            protected VideoArtWorkExtensionsHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkExtensionsHollow(ordinal);
            }
        };
    }
    public VideoArtWorkExtensionsHollow getVideoArtWorkExtensionsHollow(int ordinal) {
        objectCreationSampler.recordCreation(245);
        return (VideoArtWorkExtensionsHollow)videoArtWorkExtensionsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkArrayOfExtensionsHollow> getAllVideoArtWorkArrayOfExtensionsHollow() {
        return new AllHollowRecordCollection<VideoArtWorkArrayOfExtensionsHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkArrayOfExtensions").getTypeState()) {
            protected VideoArtWorkArrayOfExtensionsHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkArrayOfExtensionsHollow(ordinal);
            }
        };
    }
    public VideoArtWorkArrayOfExtensionsHollow getVideoArtWorkArrayOfExtensionsHollow(int ordinal) {
        objectCreationSampler.recordCreation(246);
        return (VideoArtWorkArrayOfExtensionsHollow)videoArtWorkArrayOfExtensionsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkLocalesTerritoryCodesHollow> getAllVideoArtWorkLocalesTerritoryCodesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkLocalesTerritoryCodesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkLocalesTerritoryCodes").getTypeState()) {
            protected VideoArtWorkLocalesTerritoryCodesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkLocalesTerritoryCodesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkLocalesTerritoryCodesHollow getVideoArtWorkLocalesTerritoryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(247);
        return (VideoArtWorkLocalesTerritoryCodesHollow)videoArtWorkLocalesTerritoryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkLocalesArrayOfTerritoryCodesHollow> getAllVideoArtWorkLocalesArrayOfTerritoryCodesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkLocalesArrayOfTerritoryCodesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkLocalesArrayOfTerritoryCodes").getTypeState()) {
            protected VideoArtWorkLocalesArrayOfTerritoryCodesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkLocalesArrayOfTerritoryCodesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkLocalesArrayOfTerritoryCodesHollow getVideoArtWorkLocalesArrayOfTerritoryCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(248);
        return (VideoArtWorkLocalesArrayOfTerritoryCodesHollow)videoArtWorkLocalesArrayOfTerritoryCodesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkLocalesHollow> getAllVideoArtWorkLocalesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkLocalesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkLocales").getTypeState()) {
            protected VideoArtWorkLocalesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkLocalesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkLocalesHollow getVideoArtWorkLocalesHollow(int ordinal) {
        objectCreationSampler.recordCreation(249);
        return (VideoArtWorkLocalesHollow)videoArtWorkLocalesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkArrayOfLocalesHollow> getAllVideoArtWorkArrayOfLocalesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkArrayOfLocalesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkArrayOfLocales").getTypeState()) {
            protected VideoArtWorkArrayOfLocalesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkArrayOfLocalesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkArrayOfLocalesHollow getVideoArtWorkArrayOfLocalesHollow(int ordinal) {
        objectCreationSampler.recordCreation(250);
        return (VideoArtWorkArrayOfLocalesHollow)videoArtWorkArrayOfLocalesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkRecipesHollow> getAllVideoArtWorkRecipesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkRecipesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkRecipes").getTypeState()) {
            protected VideoArtWorkRecipesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkRecipesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkRecipesHollow getVideoArtWorkRecipesHollow(int ordinal) {
        objectCreationSampler.recordCreation(251);
        return (VideoArtWorkRecipesHollow)videoArtWorkRecipesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkArrayOfRecipesHollow> getAllVideoArtWorkArrayOfRecipesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkArrayOfRecipesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkArrayOfRecipes").getTypeState()) {
            protected VideoArtWorkArrayOfRecipesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkArrayOfRecipesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkArrayOfRecipesHollow getVideoArtWorkArrayOfRecipesHollow(int ordinal) {
        objectCreationSampler.recordCreation(252);
        return (VideoArtWorkArrayOfRecipesHollow)videoArtWorkArrayOfRecipesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow> getAllVideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesAWARD_CAMPAIGNS").getTypeState()) {
            protected VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow getVideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow(int ordinal) {
        objectCreationSampler.recordCreation(253);
        return (VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow)videoArtWorkSourceAttributesAWARD_CAMPAIGNSProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow> getAllVideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNS").getTypeState()) {
            protected VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow getVideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow(int ordinal) {
        objectCreationSampler.recordCreation(254);
        return (VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow)videoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesIDENTIFIERSHollow> getAllVideoArtWorkSourceAttributesIDENTIFIERSHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesIDENTIFIERSHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesIDENTIFIERS").getTypeState()) {
            protected VideoArtWorkSourceAttributesIDENTIFIERSHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesIDENTIFIERSHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesIDENTIFIERSHollow getVideoArtWorkSourceAttributesIDENTIFIERSHollow(int ordinal) {
        objectCreationSampler.recordCreation(255);
        return (VideoArtWorkSourceAttributesIDENTIFIERSHollow)videoArtWorkSourceAttributesIDENTIFIERSProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow> getAllVideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfIDENTIFIERS").getTypeState()) {
            protected VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow getVideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow(int ordinal) {
        objectCreationSampler.recordCreation(256);
        return (VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow)videoArtWorkSourceAttributesArrayOfIDENTIFIERSProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesPERSON_IDSHollow> getAllVideoArtWorkSourceAttributesPERSON_IDSHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesPERSON_IDSHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesPERSON_IDS").getTypeState()) {
            protected VideoArtWorkSourceAttributesPERSON_IDSHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesPERSON_IDSHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesPERSON_IDSHollow getVideoArtWorkSourceAttributesPERSON_IDSHollow(int ordinal) {
        objectCreationSampler.recordCreation(257);
        return (VideoArtWorkSourceAttributesPERSON_IDSHollow)videoArtWorkSourceAttributesPERSON_IDSProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow> getAllVideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfPERSON_IDS").getTypeState()) {
            protected VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow getVideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow(int ordinal) {
        objectCreationSampler.recordCreation(258);
        return (VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow)videoArtWorkSourceAttributesArrayOfPERSON_IDSProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesThemesHollow> getAllVideoArtWorkSourceAttributesThemesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesThemesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesThemes").getTypeState()) {
            protected VideoArtWorkSourceAttributesThemesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesThemesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesThemesHollow getVideoArtWorkSourceAttributesThemesHollow(int ordinal) {
        objectCreationSampler.recordCreation(259);
        return (VideoArtWorkSourceAttributesThemesHollow)videoArtWorkSourceAttributesThemesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesArrayOfThemesHollow> getAllVideoArtWorkSourceAttributesArrayOfThemesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesArrayOfThemesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesArrayOfThemes").getTypeState()) {
            protected VideoArtWorkSourceAttributesArrayOfThemesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesArrayOfThemesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesArrayOfThemesHollow getVideoArtWorkSourceAttributesArrayOfThemesHollow(int ordinal) {
        objectCreationSampler.recordCreation(260);
        return (VideoArtWorkSourceAttributesArrayOfThemesHollow)videoArtWorkSourceAttributesArrayOfThemesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesHollow> getAllVideoArtWorkSourceAttributesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributes").getTypeState()) {
            protected VideoArtWorkSourceAttributesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesHollow getVideoArtWorkSourceAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(261);
        return (VideoArtWorkSourceAttributesHollow)videoArtWorkSourceAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkHollow> getAllVideoArtWorkHollow() {
        return new AllHollowRecordCollection<VideoArtWorkHollow>(getDataAccess().getTypeDataAccess("VideoArtWork").getTypeState()) {
            protected VideoArtWorkHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkHollow(ordinal);
            }
        };
    }
    public VideoArtWorkHollow getVideoArtWorkHollow(int ordinal) {
        objectCreationSampler.recordCreation(262);
        return (VideoArtWorkHollow)videoArtWorkProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardAwardHollow> getAllVideoAwardAwardHollow() {
        return new AllHollowRecordCollection<VideoAwardAwardHollow>(getDataAccess().getTypeDataAccess("VideoAwardAward").getTypeState()) {
            protected VideoAwardAwardHollow getForOrdinal(int ordinal) {
                return getVideoAwardAwardHollow(ordinal);
            }
        };
    }
    public VideoAwardAwardHollow getVideoAwardAwardHollow(int ordinal) {
        objectCreationSampler.recordCreation(263);
        return (VideoAwardAwardHollow)videoAwardAwardProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardArrayOfAwardHollow> getAllVideoAwardArrayOfAwardHollow() {
        return new AllHollowRecordCollection<VideoAwardArrayOfAwardHollow>(getDataAccess().getTypeDataAccess("VideoAwardArrayOfAward").getTypeState()) {
            protected VideoAwardArrayOfAwardHollow getForOrdinal(int ordinal) {
                return getVideoAwardArrayOfAwardHollow(ordinal);
            }
        };
    }
    public VideoAwardArrayOfAwardHollow getVideoAwardArrayOfAwardHollow(int ordinal) {
        objectCreationSampler.recordCreation(264);
        return (VideoAwardArrayOfAwardHollow)videoAwardArrayOfAwardProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardHollow> getAllVideoAwardHollow() {
        return new AllHollowRecordCollection<VideoAwardHollow>(getDataAccess().getTypeDataAccess("VideoAward").getTypeState()) {
            protected VideoAwardHollow getForOrdinal(int ordinal) {
                return getVideoAwardHollow(ordinal);
            }
        };
    }
    public VideoAwardHollow getVideoAwardHollow(int ordinal) {
        objectCreationSampler.recordCreation(265);
        return (VideoAwardHollow)videoAwardProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDateWindowHollow> getAllVideoDateWindowHollow() {
        return new AllHollowRecordCollection<VideoDateWindowHollow>(getDataAccess().getTypeDataAccess("VideoDateWindow").getTypeState()) {
            protected VideoDateWindowHollow getForOrdinal(int ordinal) {
                return getVideoDateWindowHollow(ordinal);
            }
        };
    }
    public VideoDateWindowHollow getVideoDateWindowHollow(int ordinal) {
        objectCreationSampler.recordCreation(266);
        return (VideoDateWindowHollow)videoDateWindowProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDateArrayOfWindowHollow> getAllVideoDateArrayOfWindowHollow() {
        return new AllHollowRecordCollection<VideoDateArrayOfWindowHollow>(getDataAccess().getTypeDataAccess("VideoDateArrayOfWindow").getTypeState()) {
            protected VideoDateArrayOfWindowHollow getForOrdinal(int ordinal) {
                return getVideoDateArrayOfWindowHollow(ordinal);
            }
        };
    }
    public VideoDateArrayOfWindowHollow getVideoDateArrayOfWindowHollow(int ordinal) {
        objectCreationSampler.recordCreation(267);
        return (VideoDateArrayOfWindowHollow)videoDateArrayOfWindowProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDateHollow> getAllVideoDateHollow() {
        return new AllHollowRecordCollection<VideoDateHollow>(getDataAccess().getTypeDataAccess("VideoDate").getTypeState()) {
            protected VideoDateHollow getForOrdinal(int ordinal) {
                return getVideoDateHollow(ordinal);
            }
        };
    }
    public VideoDateHollow getVideoDateHollow(int ordinal) {
        objectCreationSampler.recordCreation(268);
        return (VideoDateHollow)videoDateProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetSetsChildrenChildrenHollow> getAllVideoDisplaySetSetsChildrenChildrenHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetSetsChildrenChildrenHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySetSetsChildrenChildren").getTypeState()) {
            protected VideoDisplaySetSetsChildrenChildrenHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetSetsChildrenChildrenHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetSetsChildrenChildrenHollow getVideoDisplaySetSetsChildrenChildrenHollow(int ordinal) {
        objectCreationSampler.recordCreation(269);
        return (VideoDisplaySetSetsChildrenChildrenHollow)videoDisplaySetSetsChildrenChildrenProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetSetsChildrenArrayOfChildrenHollow> getAllVideoDisplaySetSetsChildrenArrayOfChildrenHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetSetsChildrenArrayOfChildrenHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySetSetsChildrenArrayOfChildren").getTypeState()) {
            protected VideoDisplaySetSetsChildrenArrayOfChildrenHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetSetsChildrenArrayOfChildrenHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetSetsChildrenArrayOfChildrenHollow getVideoDisplaySetSetsChildrenArrayOfChildrenHollow(int ordinal) {
        objectCreationSampler.recordCreation(270);
        return (VideoDisplaySetSetsChildrenArrayOfChildrenHollow)videoDisplaySetSetsChildrenArrayOfChildrenProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetSetsChildrenHollow> getAllVideoDisplaySetSetsChildrenHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetSetsChildrenHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySetSetsChildren").getTypeState()) {
            protected VideoDisplaySetSetsChildrenHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetSetsChildrenHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetSetsChildrenHollow getVideoDisplaySetSetsChildrenHollow(int ordinal) {
        objectCreationSampler.recordCreation(271);
        return (VideoDisplaySetSetsChildrenHollow)videoDisplaySetSetsChildrenProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetSetsArrayOfChildrenHollow> getAllVideoDisplaySetSetsArrayOfChildrenHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetSetsArrayOfChildrenHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySetSetsArrayOfChildren").getTypeState()) {
            protected VideoDisplaySetSetsArrayOfChildrenHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetSetsArrayOfChildrenHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetSetsArrayOfChildrenHollow getVideoDisplaySetSetsArrayOfChildrenHollow(int ordinal) {
        objectCreationSampler.recordCreation(272);
        return (VideoDisplaySetSetsArrayOfChildrenHollow)videoDisplaySetSetsArrayOfChildrenProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetSetsHollow> getAllVideoDisplaySetSetsHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetSetsHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySetSets").getTypeState()) {
            protected VideoDisplaySetSetsHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetSetsHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetSetsHollow getVideoDisplaySetSetsHollow(int ordinal) {
        objectCreationSampler.recordCreation(273);
        return (VideoDisplaySetSetsHollow)videoDisplaySetSetsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetArrayOfSetsHollow> getAllVideoDisplaySetArrayOfSetsHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetArrayOfSetsHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySetArrayOfSets").getTypeState()) {
            protected VideoDisplaySetArrayOfSetsHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetArrayOfSetsHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetArrayOfSetsHollow getVideoDisplaySetArrayOfSetsHollow(int ordinal) {
        objectCreationSampler.recordCreation(274);
        return (VideoDisplaySetArrayOfSetsHollow)videoDisplaySetArrayOfSetsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetHollow> getAllVideoDisplaySetHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySet").getTypeState()) {
            protected VideoDisplaySetHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetHollow getVideoDisplaySetHollow(int ordinal) {
        objectCreationSampler.recordCreation(275);
        return (VideoDisplaySetHollow)videoDisplaySetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralAliasesHollow> getAllVideoGeneralAliasesHollow() {
        return new AllHollowRecordCollection<VideoGeneralAliasesHollow>(getDataAccess().getTypeDataAccess("VideoGeneralAliases").getTypeState()) {
            protected VideoGeneralAliasesHollow getForOrdinal(int ordinal) {
                return getVideoGeneralAliasesHollow(ordinal);
            }
        };
    }
    public VideoGeneralAliasesHollow getVideoGeneralAliasesHollow(int ordinal) {
        objectCreationSampler.recordCreation(276);
        return (VideoGeneralAliasesHollow)videoGeneralAliasesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralArrayOfAliasesHollow> getAllVideoGeneralArrayOfAliasesHollow() {
        return new AllHollowRecordCollection<VideoGeneralArrayOfAliasesHollow>(getDataAccess().getTypeDataAccess("VideoGeneralArrayOfAliases").getTypeState()) {
            protected VideoGeneralArrayOfAliasesHollow getForOrdinal(int ordinal) {
                return getVideoGeneralArrayOfAliasesHollow(ordinal);
            }
        };
    }
    public VideoGeneralArrayOfAliasesHollow getVideoGeneralArrayOfAliasesHollow(int ordinal) {
        objectCreationSampler.recordCreation(277);
        return (VideoGeneralArrayOfAliasesHollow)videoGeneralArrayOfAliasesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralEpisodeTypesHollow> getAllVideoGeneralEpisodeTypesHollow() {
        return new AllHollowRecordCollection<VideoGeneralEpisodeTypesHollow>(getDataAccess().getTypeDataAccess("VideoGeneralEpisodeTypes").getTypeState()) {
            protected VideoGeneralEpisodeTypesHollow getForOrdinal(int ordinal) {
                return getVideoGeneralEpisodeTypesHollow(ordinal);
            }
        };
    }
    public VideoGeneralEpisodeTypesHollow getVideoGeneralEpisodeTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(278);
        return (VideoGeneralEpisodeTypesHollow)videoGeneralEpisodeTypesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralArrayOfEpisodeTypesHollow> getAllVideoGeneralArrayOfEpisodeTypesHollow() {
        return new AllHollowRecordCollection<VideoGeneralArrayOfEpisodeTypesHollow>(getDataAccess().getTypeDataAccess("VideoGeneralArrayOfEpisodeTypes").getTypeState()) {
            protected VideoGeneralArrayOfEpisodeTypesHollow getForOrdinal(int ordinal) {
                return getVideoGeneralArrayOfEpisodeTypesHollow(ordinal);
            }
        };
    }
    public VideoGeneralArrayOfEpisodeTypesHollow getVideoGeneralArrayOfEpisodeTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(279);
        return (VideoGeneralArrayOfEpisodeTypesHollow)videoGeneralArrayOfEpisodeTypesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralTitleTypesHollow> getAllVideoGeneralTitleTypesHollow() {
        return new AllHollowRecordCollection<VideoGeneralTitleTypesHollow>(getDataAccess().getTypeDataAccess("VideoGeneralTitleTypes").getTypeState()) {
            protected VideoGeneralTitleTypesHollow getForOrdinal(int ordinal) {
                return getVideoGeneralTitleTypesHollow(ordinal);
            }
        };
    }
    public VideoGeneralTitleTypesHollow getVideoGeneralTitleTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(280);
        return (VideoGeneralTitleTypesHollow)videoGeneralTitleTypesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralArrayOfTitleTypesHollow> getAllVideoGeneralArrayOfTitleTypesHollow() {
        return new AllHollowRecordCollection<VideoGeneralArrayOfTitleTypesHollow>(getDataAccess().getTypeDataAccess("VideoGeneralArrayOfTitleTypes").getTypeState()) {
            protected VideoGeneralArrayOfTitleTypesHollow getForOrdinal(int ordinal) {
                return getVideoGeneralArrayOfTitleTypesHollow(ordinal);
            }
        };
    }
    public VideoGeneralArrayOfTitleTypesHollow getVideoGeneralArrayOfTitleTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(281);
        return (VideoGeneralArrayOfTitleTypesHollow)videoGeneralArrayOfTitleTypesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralHollow> getAllVideoGeneralHollow() {
        return new AllHollowRecordCollection<VideoGeneralHollow>(getDataAccess().getTypeDataAccess("VideoGeneral").getTypeState()) {
            protected VideoGeneralHollow getForOrdinal(int ordinal) {
                return getVideoGeneralHollow(ordinal);
            }
        };
    }
    public VideoGeneralHollow getVideoGeneralHollow(int ordinal) {
        objectCreationSampler.recordCreation(282);
        return (VideoGeneralHollow)videoGeneralProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonAliasHollow> getAllVideoPersonAliasHollow() {
        return new AllHollowRecordCollection<VideoPersonAliasHollow>(getDataAccess().getTypeDataAccess("VideoPersonAlias").getTypeState()) {
            protected VideoPersonAliasHollow getForOrdinal(int ordinal) {
                return getVideoPersonAliasHollow(ordinal);
            }
        };
    }
    public VideoPersonAliasHollow getVideoPersonAliasHollow(int ordinal) {
        objectCreationSampler.recordCreation(283);
        return (VideoPersonAliasHollow)videoPersonAliasProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonArrayOfAliasHollow> getAllVideoPersonArrayOfAliasHollow() {
        return new AllHollowRecordCollection<VideoPersonArrayOfAliasHollow>(getDataAccess().getTypeDataAccess("VideoPersonArrayOfAlias").getTypeState()) {
            protected VideoPersonArrayOfAliasHollow getForOrdinal(int ordinal) {
                return getVideoPersonArrayOfAliasHollow(ordinal);
            }
        };
    }
    public VideoPersonArrayOfAliasHollow getVideoPersonArrayOfAliasHollow(int ordinal) {
        objectCreationSampler.recordCreation(284);
        return (VideoPersonArrayOfAliasHollow)videoPersonArrayOfAliasProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonCastHollow> getAllVideoPersonCastHollow() {
        return new AllHollowRecordCollection<VideoPersonCastHollow>(getDataAccess().getTypeDataAccess("VideoPersonCast").getTypeState()) {
            protected VideoPersonCastHollow getForOrdinal(int ordinal) {
                return getVideoPersonCastHollow(ordinal);
            }
        };
    }
    public VideoPersonCastHollow getVideoPersonCastHollow(int ordinal) {
        objectCreationSampler.recordCreation(285);
        return (VideoPersonCastHollow)videoPersonCastProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonArrayOfCastHollow> getAllVideoPersonArrayOfCastHollow() {
        return new AllHollowRecordCollection<VideoPersonArrayOfCastHollow>(getDataAccess().getTypeDataAccess("VideoPersonArrayOfCast").getTypeState()) {
            protected VideoPersonArrayOfCastHollow getForOrdinal(int ordinal) {
                return getVideoPersonArrayOfCastHollow(ordinal);
            }
        };
    }
    public VideoPersonArrayOfCastHollow getVideoPersonArrayOfCastHollow(int ordinal) {
        objectCreationSampler.recordCreation(286);
        return (VideoPersonArrayOfCastHollow)videoPersonArrayOfCastProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonHollow> getAllVideoPersonHollow() {
        return new AllHollowRecordCollection<VideoPersonHollow>(getDataAccess().getTypeDataAccess("VideoPerson").getTypeState()) {
            protected VideoPersonHollow getForOrdinal(int ordinal) {
                return getVideoPersonHollow(ordinal);
            }
        };
    }
    public VideoPersonHollow getVideoPersonHollow(int ordinal) {
        objectCreationSampler.recordCreation(287);
        return (VideoPersonHollow)videoPersonProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingRatingReasonIdsHollow> getAllVideoRatingRatingReasonIdsHollow() {
        return new AllHollowRecordCollection<VideoRatingRatingReasonIdsHollow>(getDataAccess().getTypeDataAccess("VideoRatingRatingReasonIds").getTypeState()) {
            protected VideoRatingRatingReasonIdsHollow getForOrdinal(int ordinal) {
                return getVideoRatingRatingReasonIdsHollow(ordinal);
            }
        };
    }
    public VideoRatingRatingReasonIdsHollow getVideoRatingRatingReasonIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(288);
        return (VideoRatingRatingReasonIdsHollow)videoRatingRatingReasonIdsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingRatingReasonArrayOfIdsHollow> getAllVideoRatingRatingReasonArrayOfIdsHollow() {
        return new AllHollowRecordCollection<VideoRatingRatingReasonArrayOfIdsHollow>(getDataAccess().getTypeDataAccess("VideoRatingRatingReasonArrayOfIds").getTypeState()) {
            protected VideoRatingRatingReasonArrayOfIdsHollow getForOrdinal(int ordinal) {
                return getVideoRatingRatingReasonArrayOfIdsHollow(ordinal);
            }
        };
    }
    public VideoRatingRatingReasonArrayOfIdsHollow getVideoRatingRatingReasonArrayOfIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(289);
        return (VideoRatingRatingReasonArrayOfIdsHollow)videoRatingRatingReasonArrayOfIdsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingRatingReasonHollow> getAllVideoRatingRatingReasonHollow() {
        return new AllHollowRecordCollection<VideoRatingRatingReasonHollow>(getDataAccess().getTypeDataAccess("VideoRatingRatingReason").getTypeState()) {
            protected VideoRatingRatingReasonHollow getForOrdinal(int ordinal) {
                return getVideoRatingRatingReasonHollow(ordinal);
            }
        };
    }
    public VideoRatingRatingReasonHollow getVideoRatingRatingReasonHollow(int ordinal) {
        objectCreationSampler.recordCreation(290);
        return (VideoRatingRatingReasonHollow)videoRatingRatingReasonProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingRatingHollow> getAllVideoRatingRatingHollow() {
        return new AllHollowRecordCollection<VideoRatingRatingHollow>(getDataAccess().getTypeDataAccess("VideoRatingRating").getTypeState()) {
            protected VideoRatingRatingHollow getForOrdinal(int ordinal) {
                return getVideoRatingRatingHollow(ordinal);
            }
        };
    }
    public VideoRatingRatingHollow getVideoRatingRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(291);
        return (VideoRatingRatingHollow)videoRatingRatingProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingArrayOfRatingHollow> getAllVideoRatingArrayOfRatingHollow() {
        return new AllHollowRecordCollection<VideoRatingArrayOfRatingHollow>(getDataAccess().getTypeDataAccess("VideoRatingArrayOfRating").getTypeState()) {
            protected VideoRatingArrayOfRatingHollow getForOrdinal(int ordinal) {
                return getVideoRatingArrayOfRatingHollow(ordinal);
            }
        };
    }
    public VideoRatingArrayOfRatingHollow getVideoRatingArrayOfRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(292);
        return (VideoRatingArrayOfRatingHollow)videoRatingArrayOfRatingProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingHollow> getAllVideoRatingHollow() {
        return new AllHollowRecordCollection<VideoRatingHollow>(getDataAccess().getTypeDataAccess("VideoRating").getTypeState()) {
            protected VideoRatingHollow getForOrdinal(int ordinal) {
                return getVideoRatingHollow(ordinal);
            }
        };
    }
    public VideoRatingHollow getVideoRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(293);
        return (VideoRatingHollow)videoRatingProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsFlagsFirstDisplayDatesHollow> getAllVideoRightsFlagsFirstDisplayDatesHollow() {
        return new AllHollowRecordCollection<VideoRightsFlagsFirstDisplayDatesHollow>(getDataAccess().getTypeDataAccess("VideoRightsFlagsFirstDisplayDates").getTypeState()) {
            protected VideoRightsFlagsFirstDisplayDatesHollow getForOrdinal(int ordinal) {
                return getVideoRightsFlagsFirstDisplayDatesHollow(ordinal);
            }
        };
    }
    public VideoRightsFlagsFirstDisplayDatesHollow getVideoRightsFlagsFirstDisplayDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(294);
        return (VideoRightsFlagsFirstDisplayDatesHollow)videoRightsFlagsFirstDisplayDatesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsFlagsMapOfFirstDisplayDatesHollow> getAllVideoRightsFlagsMapOfFirstDisplayDatesHollow() {
        return new AllHollowRecordCollection<VideoRightsFlagsMapOfFirstDisplayDatesHollow>(getDataAccess().getTypeDataAccess("VideoRightsFlagsMapOfFirstDisplayDates").getTypeState()) {
            protected VideoRightsFlagsMapOfFirstDisplayDatesHollow getForOrdinal(int ordinal) {
                return getVideoRightsFlagsMapOfFirstDisplayDatesHollow(ordinal);
            }
        };
    }
    public VideoRightsFlagsMapOfFirstDisplayDatesHollow getVideoRightsFlagsMapOfFirstDisplayDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(295);
        return (VideoRightsFlagsMapOfFirstDisplayDatesHollow)videoRightsFlagsMapOfFirstDisplayDatesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsFlagsHollow> getAllVideoRightsFlagsHollow() {
        return new AllHollowRecordCollection<VideoRightsFlagsHollow>(getDataAccess().getTypeDataAccess("VideoRightsFlags").getTypeState()) {
            protected VideoRightsFlagsHollow getForOrdinal(int ordinal) {
                return getVideoRightsFlagsHollow(ordinal);
            }
        };
    }
    public VideoRightsFlagsHollow getVideoRightsFlagsHollow(int ordinal) {
        objectCreationSampler.recordCreation(296);
        return (VideoRightsFlagsHollow)videoRightsFlagsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsAssetsHollow> getAllVideoRightsRightsContractsAssetsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsAssetsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsAssets").getTypeState()) {
            protected VideoRightsRightsContractsAssetsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsAssetsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsAssetsHollow getVideoRightsRightsContractsAssetsHollow(int ordinal) {
        objectCreationSampler.recordCreation(297);
        return (VideoRightsRightsContractsAssetsHollow)videoRightsRightsContractsAssetsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsArrayOfAssetsHollow> getAllVideoRightsRightsContractsArrayOfAssetsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsArrayOfAssetsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsArrayOfAssets").getTypeState()) {
            protected VideoRightsRightsContractsArrayOfAssetsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsArrayOfAssetsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsArrayOfAssetsHollow getVideoRightsRightsContractsArrayOfAssetsHollow(int ordinal) {
        objectCreationSampler.recordCreation(298);
        return (VideoRightsRightsContractsArrayOfAssetsHollow)videoRightsRightsContractsArrayOfAssetsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow> getAllVideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodes").getTypeState()) {
            protected VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow getVideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(299);
        return (VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow)videoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow> getAllVideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodes").getTypeState()) {
            protected VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow getVideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(300);
        return (VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow)videoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsDisallowedAssetBundlesHollow> getAllVideoRightsRightsContractsDisallowedAssetBundlesHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsDisallowedAssetBundlesHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsDisallowedAssetBundles").getTypeState()) {
            protected VideoRightsRightsContractsDisallowedAssetBundlesHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsDisallowedAssetBundlesHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsDisallowedAssetBundlesHollow getVideoRightsRightsContractsDisallowedAssetBundlesHollow(int ordinal) {
        objectCreationSampler.recordCreation(301);
        return (VideoRightsRightsContractsDisallowedAssetBundlesHollow)videoRightsRightsContractsDisallowedAssetBundlesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow> getAllVideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsArrayOfDisallowedAssetBundles").getTypeState()) {
            protected VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow getVideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow(int ordinal) {
        objectCreationSampler.recordCreation(302);
        return (VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow)videoRightsRightsContractsArrayOfDisallowedAssetBundlesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsPackagesHollow> getAllVideoRightsRightsContractsPackagesHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsPackagesHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsPackages").getTypeState()) {
            protected VideoRightsRightsContractsPackagesHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsPackagesHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsPackagesHollow getVideoRightsRightsContractsPackagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(303);
        return (VideoRightsRightsContractsPackagesHollow)videoRightsRightsContractsPackagesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsArrayOfPackagesHollow> getAllVideoRightsRightsContractsArrayOfPackagesHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsArrayOfPackagesHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContractsArrayOfPackages").getTypeState()) {
            protected VideoRightsRightsContractsArrayOfPackagesHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsArrayOfPackagesHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsArrayOfPackagesHollow getVideoRightsRightsContractsArrayOfPackagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(304);
        return (VideoRightsRightsContractsArrayOfPackagesHollow)videoRightsRightsContractsArrayOfPackagesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsContractsHollow> getAllVideoRightsRightsContractsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsContractsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsContracts").getTypeState()) {
            protected VideoRightsRightsContractsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsContractsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsContractsHollow getVideoRightsRightsContractsHollow(int ordinal) {
        objectCreationSampler.recordCreation(305);
        return (VideoRightsRightsContractsHollow)videoRightsRightsContractsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsArrayOfContractsHollow> getAllVideoRightsRightsArrayOfContractsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsArrayOfContractsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsArrayOfContracts").getTypeState()) {
            protected VideoRightsRightsArrayOfContractsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsArrayOfContractsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsArrayOfContractsHollow getVideoRightsRightsArrayOfContractsHollow(int ordinal) {
        objectCreationSampler.recordCreation(306);
        return (VideoRightsRightsArrayOfContractsHollow)videoRightsRightsArrayOfContractsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsWindowsContractIdsHollow> getAllVideoRightsRightsWindowsContractIdsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsWindowsContractIdsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsWindowsContractIds").getTypeState()) {
            protected VideoRightsRightsWindowsContractIdsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsWindowsContractIdsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsWindowsContractIdsHollow getVideoRightsRightsWindowsContractIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(307);
        return (VideoRightsRightsWindowsContractIdsHollow)videoRightsRightsWindowsContractIdsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsWindowsArrayOfContractIdsHollow> getAllVideoRightsRightsWindowsArrayOfContractIdsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsWindowsArrayOfContractIdsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsWindowsArrayOfContractIds").getTypeState()) {
            protected VideoRightsRightsWindowsArrayOfContractIdsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsWindowsArrayOfContractIdsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsWindowsArrayOfContractIdsHollow getVideoRightsRightsWindowsArrayOfContractIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(308);
        return (VideoRightsRightsWindowsArrayOfContractIdsHollow)videoRightsRightsWindowsArrayOfContractIdsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsWindowsHollow> getAllVideoRightsRightsWindowsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsWindowsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsWindows").getTypeState()) {
            protected VideoRightsRightsWindowsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsWindowsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsWindowsHollow getVideoRightsRightsWindowsHollow(int ordinal) {
        objectCreationSampler.recordCreation(309);
        return (VideoRightsRightsWindowsHollow)videoRightsRightsWindowsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsArrayOfWindowsHollow> getAllVideoRightsRightsArrayOfWindowsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsArrayOfWindowsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRightsArrayOfWindows").getTypeState()) {
            protected VideoRightsRightsArrayOfWindowsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsArrayOfWindowsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsArrayOfWindowsHollow getVideoRightsRightsArrayOfWindowsHollow(int ordinal) {
        objectCreationSampler.recordCreation(310);
        return (VideoRightsRightsArrayOfWindowsHollow)videoRightsRightsArrayOfWindowsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsHollow> getAllVideoRightsRightsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRights").getTypeState()) {
            protected VideoRightsRightsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsHollow getVideoRightsRightsHollow(int ordinal) {
        objectCreationSampler.recordCreation(311);
        return (VideoRightsRightsHollow)videoRightsRightsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsHollow> getAllVideoRightsHollow() {
        return new AllHollowRecordCollection<VideoRightsHollow>(getDataAccess().getTypeDataAccess("VideoRights").getTypeState()) {
            protected VideoRightsHollow getForOrdinal(int ordinal) {
                return getVideoRightsHollow(ordinal);
            }
        };
    }
    public VideoRightsHollow getVideoRightsHollow(int ordinal) {
        objectCreationSampler.recordCreation(312);
        return (VideoRightsHollow)videoRightsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeTypeMediaHollow> getAllVideoTypeTypeMediaHollow() {
        return new AllHollowRecordCollection<VideoTypeTypeMediaHollow>(getDataAccess().getTypeDataAccess("VideoTypeTypeMedia").getTypeState()) {
            protected VideoTypeTypeMediaHollow getForOrdinal(int ordinal) {
                return getVideoTypeTypeMediaHollow(ordinal);
            }
        };
    }
    public VideoTypeTypeMediaHollow getVideoTypeTypeMediaHollow(int ordinal) {
        objectCreationSampler.recordCreation(313);
        return (VideoTypeTypeMediaHollow)videoTypeTypeMediaProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeTypeArrayOfMediaHollow> getAllVideoTypeTypeArrayOfMediaHollow() {
        return new AllHollowRecordCollection<VideoTypeTypeArrayOfMediaHollow>(getDataAccess().getTypeDataAccess("VideoTypeTypeArrayOfMedia").getTypeState()) {
            protected VideoTypeTypeArrayOfMediaHollow getForOrdinal(int ordinal) {
                return getVideoTypeTypeArrayOfMediaHollow(ordinal);
            }
        };
    }
    public VideoTypeTypeArrayOfMediaHollow getVideoTypeTypeArrayOfMediaHollow(int ordinal) {
        objectCreationSampler.recordCreation(314);
        return (VideoTypeTypeArrayOfMediaHollow)videoTypeTypeArrayOfMediaProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeTypeHollow> getAllVideoTypeTypeHollow() {
        return new AllHollowRecordCollection<VideoTypeTypeHollow>(getDataAccess().getTypeDataAccess("VideoTypeType").getTypeState()) {
            protected VideoTypeTypeHollow getForOrdinal(int ordinal) {
                return getVideoTypeTypeHollow(ordinal);
            }
        };
    }
    public VideoTypeTypeHollow getVideoTypeTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(315);
        return (VideoTypeTypeHollow)videoTypeTypeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeArrayOfTypeHollow> getAllVideoTypeArrayOfTypeHollow() {
        return new AllHollowRecordCollection<VideoTypeArrayOfTypeHollow>(getDataAccess().getTypeDataAccess("VideoTypeArrayOfType").getTypeState()) {
            protected VideoTypeArrayOfTypeHollow getForOrdinal(int ordinal) {
                return getVideoTypeArrayOfTypeHollow(ordinal);
            }
        };
    }
    public VideoTypeArrayOfTypeHollow getVideoTypeArrayOfTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(316);
        return (VideoTypeArrayOfTypeHollow)videoTypeArrayOfTypeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeHollow> getAllVideoTypeHollow() {
        return new AllHollowRecordCollection<VideoTypeHollow>(getDataAccess().getTypeDataAccess("VideoType").getTypeState()) {
            protected VideoTypeHollow getForOrdinal(int ordinal) {
                return getVideoTypeHollow(ordinal);
            }
        };
    }
    public VideoTypeHollow getVideoTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(317);
        return (VideoTypeHollow)videoTypeProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}