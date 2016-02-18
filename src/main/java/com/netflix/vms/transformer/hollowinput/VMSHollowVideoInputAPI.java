package com.netflix.vms.transformer.hollowinput;

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

    private final CharacterQuoteTypeAPI characterQuoteTypeAPI;
    private final CharacterQuoteListTypeAPI characterQuoteListTypeAPI;
    private final ChunkDurationsStringTypeAPI chunkDurationsStringTypeAPI;
    private final CodecPrivateDataStringTypeAPI codecPrivateDataStringTypeAPI;
    private final DateTypeAPI dateTypeAPI;
    private final DownloadableIdTypeAPI downloadableIdTypeAPI;
    private final DownloadableIdListTypeAPI downloadableIdListTypeAPI;
    private final DrmInfoStringTypeAPI drmInfoStringTypeAPI;
    private final EpisodeTypeAPI episodeTypeAPI;
    private final EpisodeListTypeAPI episodeListTypeAPI;
    private final ISOCountryTypeAPI iSOCountryTypeAPI;
    private final ISOCountryListTypeAPI iSOCountryListTypeAPI;
    private final ISOCountrySetTypeAPI iSOCountrySetTypeAPI;
    private final DeployablePackagesTypeAPI deployablePackagesTypeAPI;
    private final MapKeyTypeAPI mapKeyTypeAPI;
    private final MapOfFirstDisplayDatesTypeAPI mapOfFirstDisplayDatesTypeAPI;
    private final RolloutMapOfLaunchDatesTypeAPI rolloutMapOfLaunchDatesTypeAPI;
    private final RolloutPhaseCastTypeAPI rolloutPhaseCastTypeAPI;
    private final RolloutPhaseCastListTypeAPI rolloutPhaseCastListTypeAPI;
    private final RolloutPhaseCharacterTypeAPI rolloutPhaseCharacterTypeAPI;
    private final RolloutPhaseCharacterListTypeAPI rolloutPhaseCharacterListTypeAPI;
    private final RolloutPhaseImageIdTypeAPI rolloutPhaseImageIdTypeAPI;
    private final RolloutPhaseOldArtworkListTypeAPI rolloutPhaseOldArtworkListTypeAPI;
    private final RolloutPhaseWindowTypeAPI rolloutPhaseWindowTypeAPI;
    private final RolloutPhaseWindowMapTypeAPI rolloutPhaseWindowMapTypeAPI;
    private final SeasonTypeAPI seasonTypeAPI;
    private final SeasonListTypeAPI seasonListTypeAPI;
    private final StreamDimensionsTypeAPI streamDimensionsTypeAPI;
    private final StreamFileIdentificationTypeAPI streamFileIdentificationTypeAPI;
    private final StreamProfileIdTypeAPI streamProfileIdTypeAPI;
    private final StreamProfileIdListTypeAPI streamProfileIdListTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final ArtWorkImageFormatTypeAPI artWorkImageFormatTypeAPI;
    private final ArtWorkImageTypeTypeAPI artWorkImageTypeTypeAPI;
    private final ArtworkRecipeTypeAPI artworkRecipeTypeAPI;
    private final AudioStreamInfoTypeAPI audioStreamInfoTypeAPI;
    private final Bcp47CodeTypeAPI bcp47CodeTypeAPI;
    private final CSMReviewTypeAPI cSMReviewTypeAPI;
    private final CacheDeploymentIntentTypeAPI cacheDeploymentIntentTypeAPI;
    private final CdnDeploymentTypeAPI cdnDeploymentTypeAPI;
    private final CdnDeploymentSetTypeAPI cdnDeploymentSetTypeAPI;
    private final CdnsTypeAPI cdnsTypeAPI;
    private final CertificationSystemRatingTypeAPI certificationSystemRatingTypeAPI;
    private final CertificationSystemRatingListTypeAPI certificationSystemRatingListTypeAPI;
    private final CertificationSystemTypeAPI certificationSystemTypeAPI;
    private final CharacterArtworkAttributesTypeAPI characterArtworkAttributesTypeAPI;
    private final CharacterArtworkDerivativeTypeAPI characterArtworkDerivativeTypeAPI;
    private final CharacterArtworkDerivativeListTypeAPI characterArtworkDerivativeListTypeAPI;
    private final CharacterElementsTypeAPI characterElementsTypeAPI;
    private final CharacterTypeAPI characterTypeAPI;
    private final CountryVideoDisplaySetTypeAPI countryVideoDisplaySetTypeAPI;
    private final CountryVideoDisplaySetListTypeAPI countryVideoDisplaySetListTypeAPI;
    private final DefaultExtensionRecipeTypeAPI defaultExtensionRecipeTypeAPI;
    private final DisallowedSubtitleLangCodeTypeAPI disallowedSubtitleLangCodeTypeAPI;
    private final DisallowedSubtitleLangCodesListTypeAPI disallowedSubtitleLangCodesListTypeAPI;
    private final DisallowedAssetBundleTypeAPI disallowedAssetBundleTypeAPI;
    private final DisallowedAssetBundlesListTypeAPI disallowedAssetBundlesListTypeAPI;
    private final DrmHeaderInfoTypeAPI drmHeaderInfoTypeAPI;
    private final DrmHeaderInfoListTypeAPI drmHeaderInfoListTypeAPI;
    private final DrmSystemIdentifiersTypeAPI drmSystemIdentifiersTypeAPI;
    private final ImageStreamInfoTypeAPI imageStreamInfoTypeAPI;
    private final LocaleTerritoryCodeTypeAPI localeTerritoryCodeTypeAPI;
    private final LocaleTerritoryCodeListTypeAPI localeTerritoryCodeListTypeAPI;
    private final CharacterArtworkLocaleTypeAPI characterArtworkLocaleTypeAPI;
    private final CharacterArtworkLocaleListTypeAPI characterArtworkLocaleListTypeAPI;
    private final CharacterArtworkTypeAPI characterArtworkTypeAPI;
    private final OriginServersTypeAPI originServersTypeAPI;
    private final PackageDrmInfoTypeAPI packageDrmInfoTypeAPI;
    private final PackageDrmInfoListTypeAPI packageDrmInfoListTypeAPI;
    private final PackageMomentTypeAPI packageMomentTypeAPI;
    private final PackageMomentListTypeAPI packageMomentListTypeAPI;
    private final PersonArtworkAttributeTypeAPI personArtworkAttributeTypeAPI;
    private final PersonArtworkDerivativeTypeAPI personArtworkDerivativeTypeAPI;
    private final PersonArtworkDerivativeListTypeAPI personArtworkDerivativeListTypeAPI;
    private final PersonArtworkLocaleTypeAPI personArtworkLocaleTypeAPI;
    private final PersonArtworkLocaleListTypeAPI personArtworkLocaleListTypeAPI;
    private final PersonArtworkTypeAPI personArtworkTypeAPI;
    private final ProtectionTypesTypeAPI protectionTypesTypeAPI;
    private final RolloutPhaseArtworkSourceFileIdTypeAPI rolloutPhaseArtworkSourceFileIdTypeAPI;
    private final RolloutPhaseArtworkSourceFileIdListTypeAPI rolloutPhaseArtworkSourceFileIdListTypeAPI;
    private final RolloutPhaseLocalizedMetadataTypeAPI rolloutPhaseLocalizedMetadataTypeAPI;
    private final RolloutPhaseNewArtworkTypeAPI rolloutPhaseNewArtworkTypeAPI;
    private final RolloutPhaseTrailerSupplementalInfoTypeAPI rolloutPhaseTrailerSupplementalInfoTypeAPI;
    private final RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI rolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI;
    private final RolloutPhaseTrailerTypeAPI rolloutPhaseTrailerTypeAPI;
    private final RolloutPhaseTrailerListTypeAPI rolloutPhaseTrailerListTypeAPI;
    private final RolloutPhaseElementsTypeAPI rolloutPhaseElementsTypeAPI;
    private final RolloutPhaseTypeAPI rolloutPhaseTypeAPI;
    private final RolloutPhaseListTypeAPI rolloutPhaseListTypeAPI;
    private final RolloutTypeAPI rolloutTypeAPI;
    private final StorageGroupsTypeAPI storageGroupsTypeAPI;
    private final StreamAssetTypeTypeAPI streamAssetTypeTypeAPI;
    private final StreamDeploymentInfoTypeAPI streamDeploymentInfoTypeAPI;
    private final StreamDeploymentLabelTypeAPI streamDeploymentLabelTypeAPI;
    private final StreamDeploymentLabelSetTypeAPI streamDeploymentLabelSetTypeAPI;
    private final StreamDeploymentTypeAPI streamDeploymentTypeAPI;
    private final StreamDrmInfoTypeAPI streamDrmInfoTypeAPI;
    private final StreamProfileGroupsTypeAPI streamProfileGroupsTypeAPI;
    private final StreamProfilesTypeAPI streamProfilesTypeAPI;
    private final TerritoryCountriesTypeAPI territoryCountriesTypeAPI;
    private final TextStreamInfoTypeAPI textStreamInfoTypeAPI;
    private final TopNAttributeTypeAPI topNAttributeTypeAPI;
    private final TopNAttributesListTypeAPI topNAttributesListTypeAPI;
    private final TopNTypeAPI topNTypeAPI;
    private final TrailerThemeTypeAPI trailerThemeTypeAPI;
    private final TrailerThemeListTypeAPI trailerThemeListTypeAPI;
    private final IndividualTrailerTypeAPI individualTrailerTypeAPI;
    private final TrailersListTypeAPI trailersListTypeAPI;
    private final TrailerTypeAPI trailerTypeAPI;
    private final TranslatedTextValueTypeAPI translatedTextValueTypeAPI;
    private final MapOfTranslatedTextTypeAPI mapOfTranslatedTextTypeAPI;
    private final AltGenresAlternateNamesTypeAPI altGenresAlternateNamesTypeAPI;
    private final AltGenresAlternateNamesListTypeAPI altGenresAlternateNamesListTypeAPI;
    private final LocalizedCharacterTypeAPI localizedCharacterTypeAPI;
    private final LocalizedMetadataTypeAPI localizedMetadataTypeAPI;
    private final StoriesSynopsesHookTypeAPI storiesSynopsesHookTypeAPI;
    private final StoriesSynopsesHookListTypeAPI storiesSynopsesHookListTypeAPI;
    private final TranslatedTextTypeAPI translatedTextTypeAPI;
    private final AltGenresTypeAPI altGenresTypeAPI;
    private final AssetMetaDatasTypeAPI assetMetaDatasTypeAPI;
    private final AwardsTypeAPI awardsTypeAPI;
    private final CategoriesTypeAPI categoriesTypeAPI;
    private final CategoryGroupsTypeAPI categoryGroupsTypeAPI;
    private final CertificationsTypeAPI certificationsTypeAPI;
    private final CharactersTypeAPI charactersTypeAPI;
    private final ConsolidatedCertSystemRatingTypeAPI consolidatedCertSystemRatingTypeAPI;
    private final ConsolidatedCertSystemRatingListTypeAPI consolidatedCertSystemRatingListTypeAPI;
    private final ConsolidatedCertificationSystemsTypeAPI consolidatedCertificationSystemsTypeAPI;
    private final EpisodesTypeAPI episodesTypeAPI;
    private final FestivalsTypeAPI festivalsTypeAPI;
    private final LanguagesTypeAPI languagesTypeAPI;
    private final MovieRatingsTypeAPI movieRatingsTypeAPI;
    private final MoviesTypeAPI moviesTypeAPI;
    private final PersonAliasesTypeAPI personAliasesTypeAPI;
    private final PersonsTypeAPI personsTypeAPI;
    private final RatingsTypeAPI ratingsTypeAPI;
    private final ShowMemberTypesTypeAPI showMemberTypesTypeAPI;
    private final Stories_SynopsesTypeAPI stories_SynopsesTypeAPI;
    private final TurboCollectionsTypeAPI turboCollectionsTypeAPI;
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
    private final VideoArtWorkRecipeListTypeAPI videoArtWorkRecipeListTypeAPI;
    private final VideoArtWorkSourceAttributesThemesTypeAPI videoArtWorkSourceAttributesThemesTypeAPI;
    private final VideoArtworkAttributeTypeAPI videoArtworkAttributeTypeAPI;
    private final VideoArtWorkMultiValueAttributeTypeAPI videoArtWorkMultiValueAttributeTypeAPI;
    private final VideoArtWorkSourceAttributesTypeAPI videoArtWorkSourceAttributesTypeAPI;
    private final VideoArtWorkTypeAPI videoArtWorkTypeAPI;
    private final VideoAwardAwardTypeAPI videoAwardAwardTypeAPI;
    private final VideoAwardArrayOfAwardTypeAPI videoAwardArrayOfAwardTypeAPI;
    private final VideoAwardMappingTypeAPI videoAwardMappingTypeAPI;
    private final VideoAwardListTypeAPI videoAwardListTypeAPI;
    private final VideoAwardTypeAPI videoAwardTypeAPI;
    private final VideoDateWindowTypeAPI videoDateWindowTypeAPI;
    private final VideoDateWindowListTypeAPI videoDateWindowListTypeAPI;
    private final VideoDateTypeAPI videoDateTypeAPI;
    private final VideoDisplaySetTypeAPI videoDisplaySetTypeAPI;
    private final VideoGeneralAliasTypeAPI videoGeneralAliasTypeAPI;
    private final VideoGeneralAliasListTypeAPI videoGeneralAliasListTypeAPI;
    private final VideoGeneralEpisodeTypeTypeAPI videoGeneralEpisodeTypeTypeAPI;
    private final VideoGeneralEpisodeTypeListTypeAPI videoGeneralEpisodeTypeListTypeAPI;
    private final VideoGeneralTitleTypeTypeAPI videoGeneralTitleTypeTypeAPI;
    private final VideoGeneralTitleTypeListTypeAPI videoGeneralTitleTypeListTypeAPI;
    private final VideoGeneralTypeAPI videoGeneralTypeAPI;
    private final VideoPersonAliasTypeAPI videoPersonAliasTypeAPI;
    private final VideoPersonAliasListTypeAPI videoPersonAliasListTypeAPI;
    private final VideoPersonCastTypeAPI videoPersonCastTypeAPI;
    private final VideoPersonCastListTypeAPI videoPersonCastListTypeAPI;
    private final VideoPersonTypeAPI videoPersonTypeAPI;
    private final VideoRatingAdvisoryIdTypeAPI videoRatingAdvisoryIdTypeAPI;
    private final VideoRatingAdvisoryIdListTypeAPI videoRatingAdvisoryIdListTypeAPI;
    private final VideoRatingAdvisoriesTypeAPI videoRatingAdvisoriesTypeAPI;
    private final ConsolidatedVideoCountryRatingTypeAPI consolidatedVideoCountryRatingTypeAPI;
    private final ConsolidatedVideoCountryRatingListTypeAPI consolidatedVideoCountryRatingListTypeAPI;
    private final ConsolidatedVideoRatingTypeAPI consolidatedVideoRatingTypeAPI;
    private final ConsolidatedVideoRatingListTypeAPI consolidatedVideoRatingListTypeAPI;
    private final ConsolidatedVideoRatingsTypeAPI consolidatedVideoRatingsTypeAPI;
    private final VideoRatingRatingReasonIdsTypeAPI videoRatingRatingReasonIdsTypeAPI;
    private final VideoRatingRatingReasonArrayOfIdsTypeAPI videoRatingRatingReasonArrayOfIdsTypeAPI;
    private final VideoRatingRatingReasonTypeAPI videoRatingRatingReasonTypeAPI;
    private final VideoRatingRatingTypeAPI videoRatingRatingTypeAPI;
    private final VideoRatingArrayOfRatingTypeAPI videoRatingArrayOfRatingTypeAPI;
    private final VideoRatingTypeAPI videoRatingTypeAPI;
    private final VideoRightsContractAssetTypeAPI videoRightsContractAssetTypeAPI;
    private final VideoRightsContractAssetsSetTypeAPI videoRightsContractAssetsSetTypeAPI;
    private final VideoRightsContractIdTypeAPI videoRightsContractIdTypeAPI;
    private final VideoRightsContractPackageTypeAPI videoRightsContractPackageTypeAPI;
    private final VideoRightsContractPackagesListTypeAPI videoRightsContractPackagesListTypeAPI;
    private final VideoRightsContractTypeAPI videoRightsContractTypeAPI;
    private final VideoRightsContractSetTypeAPI videoRightsContractSetTypeAPI;
    private final VideoRightsFlagsTypeAPI videoRightsFlagsTypeAPI;
    private final VideoRightsWindowContractIdListTypeAPI videoRightsWindowContractIdListTypeAPI;
    private final VideoRightsWindowTypeAPI videoRightsWindowTypeAPI;
    private final VideoRightsWindowsSetTypeAPI videoRightsWindowsSetTypeAPI;
    private final VideoRightsRightsTypeAPI videoRightsRightsTypeAPI;
    private final VideoRightsTypeAPI videoRightsTypeAPI;
    private final VideoStreamInfoTypeAPI videoStreamInfoTypeAPI;
    private final StreamNonImageInfoTypeAPI streamNonImageInfoTypeAPI;
    private final PackageStreamTypeAPI packageStreamTypeAPI;
    private final PackageStreamSetTypeAPI packageStreamSetTypeAPI;
    private final PackagesTypeAPI packagesTypeAPI;
    private final VideoTypeMediaTypeAPI videoTypeMediaTypeAPI;
    private final VideoTypeMediaListTypeAPI videoTypeMediaListTypeAPI;
    private final VideoTypeDescriptorTypeAPI videoTypeDescriptorTypeAPI;
    private final VideoTypeDescriptorListTypeAPI videoTypeDescriptorListTypeAPI;
    private final VideoTypeTypeAPI videoTypeTypeAPI;

    private final HollowObjectProvider characterQuoteProvider;
    private final HollowObjectProvider characterQuoteListProvider;
    private final HollowObjectProvider chunkDurationsStringProvider;
    private final HollowObjectProvider codecPrivateDataStringProvider;
    private final HollowObjectProvider dateProvider;
    private final HollowObjectProvider downloadableIdProvider;
    private final HollowObjectProvider downloadableIdListProvider;
    private final HollowObjectProvider drmInfoStringProvider;
    private final HollowObjectProvider episodeProvider;
    private final HollowObjectProvider episodeListProvider;
    private final HollowObjectProvider iSOCountryProvider;
    private final HollowObjectProvider iSOCountryListProvider;
    private final HollowObjectProvider iSOCountrySetProvider;
    private final HollowObjectProvider deployablePackagesProvider;
    private final HollowObjectProvider mapKeyProvider;
    private final HollowObjectProvider mapOfFirstDisplayDatesProvider;
    private final HollowObjectProvider rolloutMapOfLaunchDatesProvider;
    private final HollowObjectProvider rolloutPhaseCastProvider;
    private final HollowObjectProvider rolloutPhaseCastListProvider;
    private final HollowObjectProvider rolloutPhaseCharacterProvider;
    private final HollowObjectProvider rolloutPhaseCharacterListProvider;
    private final HollowObjectProvider rolloutPhaseImageIdProvider;
    private final HollowObjectProvider rolloutPhaseOldArtworkListProvider;
    private final HollowObjectProvider rolloutPhaseWindowProvider;
    private final HollowObjectProvider rolloutPhaseWindowMapProvider;
    private final HollowObjectProvider seasonProvider;
    private final HollowObjectProvider seasonListProvider;
    private final HollowObjectProvider streamDimensionsProvider;
    private final HollowObjectProvider streamFileIdentificationProvider;
    private final HollowObjectProvider streamProfileIdProvider;
    private final HollowObjectProvider streamProfileIdListProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider artWorkImageFormatProvider;
    private final HollowObjectProvider artWorkImageTypeProvider;
    private final HollowObjectProvider artworkRecipeProvider;
    private final HollowObjectProvider audioStreamInfoProvider;
    private final HollowObjectProvider bcp47CodeProvider;
    private final HollowObjectProvider cSMReviewProvider;
    private final HollowObjectProvider cacheDeploymentIntentProvider;
    private final HollowObjectProvider cdnDeploymentProvider;
    private final HollowObjectProvider cdnDeploymentSetProvider;
    private final HollowObjectProvider cdnsProvider;
    private final HollowObjectProvider certificationSystemRatingProvider;
    private final HollowObjectProvider certificationSystemRatingListProvider;
    private final HollowObjectProvider certificationSystemProvider;
    private final HollowObjectProvider characterArtworkAttributesProvider;
    private final HollowObjectProvider characterArtworkDerivativeProvider;
    private final HollowObjectProvider characterArtworkDerivativeListProvider;
    private final HollowObjectProvider characterElementsProvider;
    private final HollowObjectProvider characterProvider;
    private final HollowObjectProvider countryVideoDisplaySetProvider;
    private final HollowObjectProvider countryVideoDisplaySetListProvider;
    private final HollowObjectProvider defaultExtensionRecipeProvider;
    private final HollowObjectProvider disallowedSubtitleLangCodeProvider;
    private final HollowObjectProvider disallowedSubtitleLangCodesListProvider;
    private final HollowObjectProvider disallowedAssetBundleProvider;
    private final HollowObjectProvider disallowedAssetBundlesListProvider;
    private final HollowObjectProvider drmHeaderInfoProvider;
    private final HollowObjectProvider drmHeaderInfoListProvider;
    private final HollowObjectProvider drmSystemIdentifiersProvider;
    private final HollowObjectProvider imageStreamInfoProvider;
    private final HollowObjectProvider localeTerritoryCodeProvider;
    private final HollowObjectProvider localeTerritoryCodeListProvider;
    private final HollowObjectProvider characterArtworkLocaleProvider;
    private final HollowObjectProvider characterArtworkLocaleListProvider;
    private final HollowObjectProvider characterArtworkProvider;
    private final HollowObjectProvider originServersProvider;
    private final HollowObjectProvider packageDrmInfoProvider;
    private final HollowObjectProvider packageDrmInfoListProvider;
    private final HollowObjectProvider packageMomentProvider;
    private final HollowObjectProvider packageMomentListProvider;
    private final HollowObjectProvider personArtworkAttributeProvider;
    private final HollowObjectProvider personArtworkDerivativeProvider;
    private final HollowObjectProvider personArtworkDerivativeListProvider;
    private final HollowObjectProvider personArtworkLocaleProvider;
    private final HollowObjectProvider personArtworkLocaleListProvider;
    private final HollowObjectProvider personArtworkProvider;
    private final HollowObjectProvider protectionTypesProvider;
    private final HollowObjectProvider rolloutPhaseArtworkSourceFileIdProvider;
    private final HollowObjectProvider rolloutPhaseArtworkSourceFileIdListProvider;
    private final HollowObjectProvider rolloutPhaseLocalizedMetadataProvider;
    private final HollowObjectProvider rolloutPhaseNewArtworkProvider;
    private final HollowObjectProvider rolloutPhaseTrailerSupplementalInfoProvider;
    private final HollowObjectProvider rolloutPhasesElementsTrailerSupplementalInfoMapProvider;
    private final HollowObjectProvider rolloutPhaseTrailerProvider;
    private final HollowObjectProvider rolloutPhaseTrailerListProvider;
    private final HollowObjectProvider rolloutPhaseElementsProvider;
    private final HollowObjectProvider rolloutPhaseProvider;
    private final HollowObjectProvider rolloutPhaseListProvider;
    private final HollowObjectProvider rolloutProvider;
    private final HollowObjectProvider storageGroupsProvider;
    private final HollowObjectProvider streamAssetTypeProvider;
    private final HollowObjectProvider streamDeploymentInfoProvider;
    private final HollowObjectProvider streamDeploymentLabelProvider;
    private final HollowObjectProvider streamDeploymentLabelSetProvider;
    private final HollowObjectProvider streamDeploymentProvider;
    private final HollowObjectProvider streamDrmInfoProvider;
    private final HollowObjectProvider streamProfileGroupsProvider;
    private final HollowObjectProvider streamProfilesProvider;
    private final HollowObjectProvider territoryCountriesProvider;
    private final HollowObjectProvider textStreamInfoProvider;
    private final HollowObjectProvider topNAttributeProvider;
    private final HollowObjectProvider topNAttributesListProvider;
    private final HollowObjectProvider topNProvider;
    private final HollowObjectProvider trailerThemeProvider;
    private final HollowObjectProvider trailerThemeListProvider;
    private final HollowObjectProvider individualTrailerProvider;
    private final HollowObjectProvider trailersListProvider;
    private final HollowObjectProvider trailerProvider;
    private final HollowObjectProvider translatedTextValueProvider;
    private final HollowObjectProvider mapOfTranslatedTextProvider;
    private final HollowObjectProvider altGenresAlternateNamesProvider;
    private final HollowObjectProvider altGenresAlternateNamesListProvider;
    private final HollowObjectProvider localizedCharacterProvider;
    private final HollowObjectProvider localizedMetadataProvider;
    private final HollowObjectProvider storiesSynopsesHookProvider;
    private final HollowObjectProvider storiesSynopsesHookListProvider;
    private final HollowObjectProvider translatedTextProvider;
    private final HollowObjectProvider altGenresProvider;
    private final HollowObjectProvider assetMetaDatasProvider;
    private final HollowObjectProvider awardsProvider;
    private final HollowObjectProvider categoriesProvider;
    private final HollowObjectProvider categoryGroupsProvider;
    private final HollowObjectProvider certificationsProvider;
    private final HollowObjectProvider charactersProvider;
    private final HollowObjectProvider consolidatedCertSystemRatingProvider;
    private final HollowObjectProvider consolidatedCertSystemRatingListProvider;
    private final HollowObjectProvider consolidatedCertificationSystemsProvider;
    private final HollowObjectProvider episodesProvider;
    private final HollowObjectProvider festivalsProvider;
    private final HollowObjectProvider languagesProvider;
    private final HollowObjectProvider movieRatingsProvider;
    private final HollowObjectProvider moviesProvider;
    private final HollowObjectProvider personAliasesProvider;
    private final HollowObjectProvider personsProvider;
    private final HollowObjectProvider ratingsProvider;
    private final HollowObjectProvider showMemberTypesProvider;
    private final HollowObjectProvider stories_SynopsesProvider;
    private final HollowObjectProvider turboCollectionsProvider;
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
    private final HollowObjectProvider videoArtWorkRecipeListProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesThemesProvider;
    private final HollowObjectProvider videoArtworkAttributeProvider;
    private final HollowObjectProvider videoArtWorkMultiValueAttributeProvider;
    private final HollowObjectProvider videoArtWorkSourceAttributesProvider;
    private final HollowObjectProvider videoArtWorkProvider;
    private final HollowObjectProvider videoAwardAwardProvider;
    private final HollowObjectProvider videoAwardArrayOfAwardProvider;
    private final HollowObjectProvider videoAwardMappingProvider;
    private final HollowObjectProvider videoAwardListProvider;
    private final HollowObjectProvider videoAwardProvider;
    private final HollowObjectProvider videoDateWindowProvider;
    private final HollowObjectProvider videoDateWindowListProvider;
    private final HollowObjectProvider videoDateProvider;
    private final HollowObjectProvider videoDisplaySetProvider;
    private final HollowObjectProvider videoGeneralAliasProvider;
    private final HollowObjectProvider videoGeneralAliasListProvider;
    private final HollowObjectProvider videoGeneralEpisodeTypeProvider;
    private final HollowObjectProvider videoGeneralEpisodeTypeListProvider;
    private final HollowObjectProvider videoGeneralTitleTypeProvider;
    private final HollowObjectProvider videoGeneralTitleTypeListProvider;
    private final HollowObjectProvider videoGeneralProvider;
    private final HollowObjectProvider videoPersonAliasProvider;
    private final HollowObjectProvider videoPersonAliasListProvider;
    private final HollowObjectProvider videoPersonCastProvider;
    private final HollowObjectProvider videoPersonCastListProvider;
    private final HollowObjectProvider videoPersonProvider;
    private final HollowObjectProvider videoRatingAdvisoryIdProvider;
    private final HollowObjectProvider videoRatingAdvisoryIdListProvider;
    private final HollowObjectProvider videoRatingAdvisoriesProvider;
    private final HollowObjectProvider consolidatedVideoCountryRatingProvider;
    private final HollowObjectProvider consolidatedVideoCountryRatingListProvider;
    private final HollowObjectProvider consolidatedVideoRatingProvider;
    private final HollowObjectProvider consolidatedVideoRatingListProvider;
    private final HollowObjectProvider consolidatedVideoRatingsProvider;
    private final HollowObjectProvider videoRatingRatingReasonIdsProvider;
    private final HollowObjectProvider videoRatingRatingReasonArrayOfIdsProvider;
    private final HollowObjectProvider videoRatingRatingReasonProvider;
    private final HollowObjectProvider videoRatingRatingProvider;
    private final HollowObjectProvider videoRatingArrayOfRatingProvider;
    private final HollowObjectProvider videoRatingProvider;
    private final HollowObjectProvider videoRightsContractAssetProvider;
    private final HollowObjectProvider videoRightsContractAssetsSetProvider;
    private final HollowObjectProvider videoRightsContractIdProvider;
    private final HollowObjectProvider videoRightsContractPackageProvider;
    private final HollowObjectProvider videoRightsContractPackagesListProvider;
    private final HollowObjectProvider videoRightsContractProvider;
    private final HollowObjectProvider videoRightsContractSetProvider;
    private final HollowObjectProvider videoRightsFlagsProvider;
    private final HollowObjectProvider videoRightsWindowContractIdListProvider;
    private final HollowObjectProvider videoRightsWindowProvider;
    private final HollowObjectProvider videoRightsWindowsSetProvider;
    private final HollowObjectProvider videoRightsRightsProvider;
    private final HollowObjectProvider videoRightsProvider;
    private final HollowObjectProvider videoStreamInfoProvider;
    private final HollowObjectProvider streamNonImageInfoProvider;
    private final HollowObjectProvider packageStreamProvider;
    private final HollowObjectProvider packageStreamSetProvider;
    private final HollowObjectProvider packagesProvider;
    private final HollowObjectProvider videoTypeMediaProvider;
    private final HollowObjectProvider videoTypeMediaListProvider;
    private final HollowObjectProvider videoTypeDescriptorProvider;
    private final HollowObjectProvider videoTypeDescriptorListProvider;
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

        objectCreationSampler = new HollowObjectCreationSampler("CharacterQuote","CharacterQuoteList","ChunkDurationsString","CodecPrivateDataString","Date","DownloadableId","DownloadableIdList","DrmInfoString","Episode","EpisodeList","ISOCountry","ISOCountryList","ISOCountrySet","DeployablePackages","MapKey","MapOfFirstDisplayDates","RolloutMapOfLaunchDates","RolloutPhaseCast","RolloutPhaseCastList","RolloutPhaseCharacter","RolloutPhaseCharacterList","RolloutPhaseImageId","RolloutPhaseOldArtworkList","RolloutPhaseWindow","RolloutPhaseWindowMap","Season","SeasonList","StreamDimensions","StreamFileIdentification","StreamProfileId","StreamProfileIdList","String","ArtWorkImageFormat","ArtWorkImageType","ArtworkRecipe","AudioStreamInfo","Bcp47Code","CSMReview","CacheDeploymentIntent","CdnDeployment","CdnDeploymentSet","Cdns","CertificationSystemRating","CertificationSystemRatingList","CertificationSystem","CharacterArtworkAttributes","CharacterArtworkDerivative","CharacterArtworkDerivativeList","CharacterElements","Character","CountryVideoDisplaySet","CountryVideoDisplaySetList","DefaultExtensionRecipe","DisallowedSubtitleLangCode","DisallowedSubtitleLangCodesList","DisallowedAssetBundle","DisallowedAssetBundlesList","DrmHeaderInfo","DrmHeaderInfoList","DrmSystemIdentifiers","ImageStreamInfo","LocaleTerritoryCode","LocaleTerritoryCodeList","CharacterArtworkLocale","CharacterArtworkLocaleList","CharacterArtwork","OriginServers","PackageDrmInfo","PackageDrmInfoList","PackageMoment","PackageMomentList","PersonArtworkAttribute","PersonArtworkDerivative","PersonArtworkDerivativeList","PersonArtworkLocale","PersonArtworkLocaleList","PersonArtwork","ProtectionTypes","RolloutPhaseArtworkSourceFileId","RolloutPhaseArtworkSourceFileIdList","RolloutPhaseLocalizedMetadata","RolloutPhaseNewArtwork","RolloutPhaseTrailerSupplementalInfo","RolloutPhasesElementsTrailerSupplementalInfoMap","RolloutPhaseTrailer","RolloutPhaseTrailerList","RolloutPhaseElements","RolloutPhase","RolloutPhaseList","Rollout","StorageGroups","StreamAssetType","StreamDeploymentInfo","StreamDeploymentLabel","StreamDeploymentLabelSet","StreamDeployment","StreamDrmInfo","StreamProfileGroups","StreamProfiles","TerritoryCountries","TextStreamInfo","TopNAttribute","TopNAttributesList","TopN","TrailerTheme","TrailerThemeList","IndividualTrailer","TrailersList","Trailer","TranslatedTextValue","MapOfTranslatedText","AltGenresAlternateNames","AltGenresAlternateNamesList","LocalizedCharacter","LocalizedMetadata","StoriesSynopsesHook","StoriesSynopsesHookList","TranslatedText","AltGenres","AssetMetaDatas","Awards","Categories","CategoryGroups","Certifications","Characters","ConsolidatedCertSystemRating","ConsolidatedCertSystemRatingList","ConsolidatedCertificationSystems","Episodes","Festivals","Languages","MovieRatings","Movies","PersonAliases","Persons","Ratings","ShowMemberTypes","Stories_Synopses","TurboCollections","VMSAward","VideoArtWorkAttributes","VideoArtWorkArrayOfAttributes","VideoArtWorkExtensions","VideoArtWorkArrayOfExtensions","VideoArtWorkLocalesTerritoryCodes","VideoArtWorkLocalesArrayOfTerritoryCodes","VideoArtWorkLocales","VideoArtWorkArrayOfLocales","VideoArtWorkRecipes","VideoArtWorkRecipeList","VideoArtWorkSourceAttributesThemes","VideoArtworkAttribute","VideoArtWorkMultiValueAttribute","VideoArtWorkSourceAttributes","VideoArtWork","VideoAwardAward","VideoAwardArrayOfAward","VideoAwardMapping","VideoAwardList","VideoAward","VideoDateWindow","VideoDateWindowList","VideoDate","VideoDisplaySet","VideoGeneralAlias","VideoGeneralAliasList","VideoGeneralEpisodeType","VideoGeneralEpisodeTypeList","VideoGeneralTitleType","VideoGeneralTitleTypeList","VideoGeneral","VideoPersonAlias","VideoPersonAliasList","VideoPersonCast","VideoPersonCastList","VideoPerson","VideoRatingAdvisoryId","VideoRatingAdvisoryIdList","VideoRatingAdvisories","ConsolidatedVideoCountryRating","ConsolidatedVideoCountryRatingList","ConsolidatedVideoRating","ConsolidatedVideoRatingList","ConsolidatedVideoRatings","VideoRatingRatingReasonIds","VideoRatingRatingReasonArrayOfIds","VideoRatingRatingReason","VideoRatingRating","VideoRatingArrayOfRating","VideoRating","VideoRightsContractAsset","VideoRightsContractAssetsSet","VideoRightsContractId","VideoRightsContractPackage","VideoRightsContractPackagesList","VideoRightsContract","VideoRightsContractSet","VideoRightsFlags","VideoRightsWindowContractIdList","VideoRightsWindow","VideoRightsWindowsSet","VideoRightsRights","VideoRights","VideoStreamInfo","StreamNonImageInfo","PackageStream","PackageStreamSet","Packages","VideoTypeMedia","VideoTypeMediaList","VideoTypeDescriptor","VideoTypeDescriptorList","VideoType");

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterQuote");
        if(typeDataAccess != null) {
            characterQuoteTypeAPI = new CharacterQuoteTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterQuoteTypeAPI = new CharacterQuoteTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterQuote"));
        }
        addTypeAPI(characterQuoteTypeAPI);
        factory = factoryOverrides.get("CharacterQuote");
        if(factory == null)
            factory = new CharacterQuoteHollowFactory();
        if(cachedTypes.contains("CharacterQuote")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterQuoteProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterQuoteProvider;
            characterQuoteProvider = new HollowObjectCacheProvider(typeDataAccess, characterQuoteTypeAPI, factory, previousCacheProvider);
        } else {
            characterQuoteProvider = new HollowObjectFactoryProvider(typeDataAccess, characterQuoteTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterQuoteList");
        if(typeDataAccess != null) {
            characterQuoteListTypeAPI = new CharacterQuoteListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterQuoteListTypeAPI = new CharacterQuoteListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterQuoteList"));
        }
        addTypeAPI(characterQuoteListTypeAPI);
        factory = factoryOverrides.get("CharacterQuoteList");
        if(factory == null)
            factory = new CharacterQuoteListHollowFactory();
        if(cachedTypes.contains("CharacterQuoteList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterQuoteListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterQuoteListProvider;
            characterQuoteListProvider = new HollowObjectCacheProvider(typeDataAccess, characterQuoteListTypeAPI, factory, previousCacheProvider);
        } else {
            characterQuoteListProvider = new HollowObjectFactoryProvider(typeDataAccess, characterQuoteListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ChunkDurationsString");
        if(typeDataAccess != null) {
            chunkDurationsStringTypeAPI = new ChunkDurationsStringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            chunkDurationsStringTypeAPI = new ChunkDurationsStringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ChunkDurationsString"));
        }
        addTypeAPI(chunkDurationsStringTypeAPI);
        factory = factoryOverrides.get("ChunkDurationsString");
        if(factory == null)
            factory = new ChunkDurationsStringHollowFactory();
        if(cachedTypes.contains("ChunkDurationsString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.chunkDurationsStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.chunkDurationsStringProvider;
            chunkDurationsStringProvider = new HollowObjectCacheProvider(typeDataAccess, chunkDurationsStringTypeAPI, factory, previousCacheProvider);
        } else {
            chunkDurationsStringProvider = new HollowObjectFactoryProvider(typeDataAccess, chunkDurationsStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CodecPrivateDataString");
        if(typeDataAccess != null) {
            codecPrivateDataStringTypeAPI = new CodecPrivateDataStringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            codecPrivateDataStringTypeAPI = new CodecPrivateDataStringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CodecPrivateDataString"));
        }
        addTypeAPI(codecPrivateDataStringTypeAPI);
        factory = factoryOverrides.get("CodecPrivateDataString");
        if(factory == null)
            factory = new CodecPrivateDataStringHollowFactory();
        if(cachedTypes.contains("CodecPrivateDataString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.codecPrivateDataStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.codecPrivateDataStringProvider;
            codecPrivateDataStringProvider = new HollowObjectCacheProvider(typeDataAccess, codecPrivateDataStringTypeAPI, factory, previousCacheProvider);
        } else {
            codecPrivateDataStringProvider = new HollowObjectFactoryProvider(typeDataAccess, codecPrivateDataStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Date");
        if(typeDataAccess != null) {
            dateTypeAPI = new DateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            dateTypeAPI = new DateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Date"));
        }
        addTypeAPI(dateTypeAPI);
        factory = factoryOverrides.get("Date");
        if(factory == null)
            factory = new DateHollowFactory();
        if(cachedTypes.contains("Date")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.dateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.dateProvider;
            dateProvider = new HollowObjectCacheProvider(typeDataAccess, dateTypeAPI, factory, previousCacheProvider);
        } else {
            dateProvider = new HollowObjectFactoryProvider(typeDataAccess, dateTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DownloadableId");
        if(typeDataAccess != null) {
            downloadableIdTypeAPI = new DownloadableIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            downloadableIdTypeAPI = new DownloadableIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DownloadableId"));
        }
        addTypeAPI(downloadableIdTypeAPI);
        factory = factoryOverrides.get("DownloadableId");
        if(factory == null)
            factory = new DownloadableIdHollowFactory();
        if(cachedTypes.contains("DownloadableId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.downloadableIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.downloadableIdProvider;
            downloadableIdProvider = new HollowObjectCacheProvider(typeDataAccess, downloadableIdTypeAPI, factory, previousCacheProvider);
        } else {
            downloadableIdProvider = new HollowObjectFactoryProvider(typeDataAccess, downloadableIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DownloadableIdList");
        if(typeDataAccess != null) {
            downloadableIdListTypeAPI = new DownloadableIdListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            downloadableIdListTypeAPI = new DownloadableIdListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "DownloadableIdList"));
        }
        addTypeAPI(downloadableIdListTypeAPI);
        factory = factoryOverrides.get("DownloadableIdList");
        if(factory == null)
            factory = new DownloadableIdListHollowFactory();
        if(cachedTypes.contains("DownloadableIdList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.downloadableIdListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.downloadableIdListProvider;
            downloadableIdListProvider = new HollowObjectCacheProvider(typeDataAccess, downloadableIdListTypeAPI, factory, previousCacheProvider);
        } else {
            downloadableIdListProvider = new HollowObjectFactoryProvider(typeDataAccess, downloadableIdListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DrmInfoString");
        if(typeDataAccess != null) {
            drmInfoStringTypeAPI = new DrmInfoStringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            drmInfoStringTypeAPI = new DrmInfoStringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DrmInfoString"));
        }
        addTypeAPI(drmInfoStringTypeAPI);
        factory = factoryOverrides.get("DrmInfoString");
        if(factory == null)
            factory = new DrmInfoStringHollowFactory();
        if(cachedTypes.contains("DrmInfoString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.drmInfoStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.drmInfoStringProvider;
            drmInfoStringProvider = new HollowObjectCacheProvider(typeDataAccess, drmInfoStringTypeAPI, factory, previousCacheProvider);
        } else {
            drmInfoStringProvider = new HollowObjectFactoryProvider(typeDataAccess, drmInfoStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Episode");
        if(typeDataAccess != null) {
            episodeTypeAPI = new EpisodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            episodeTypeAPI = new EpisodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Episode"));
        }
        addTypeAPI(episodeTypeAPI);
        factory = factoryOverrides.get("Episode");
        if(factory == null)
            factory = new EpisodeHollowFactory();
        if(cachedTypes.contains("Episode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodeProvider;
            episodeProvider = new HollowObjectCacheProvider(typeDataAccess, episodeTypeAPI, factory, previousCacheProvider);
        } else {
            episodeProvider = new HollowObjectFactoryProvider(typeDataAccess, episodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("EpisodeList");
        if(typeDataAccess != null) {
            episodeListTypeAPI = new EpisodeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            episodeListTypeAPI = new EpisodeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "EpisodeList"));
        }
        addTypeAPI(episodeListTypeAPI);
        factory = factoryOverrides.get("EpisodeList");
        if(factory == null)
            factory = new EpisodeListHollowFactory();
        if(cachedTypes.contains("EpisodeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.episodeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.episodeListProvider;
            episodeListProvider = new HollowObjectCacheProvider(typeDataAccess, episodeListTypeAPI, factory, previousCacheProvider);
        } else {
            episodeListProvider = new HollowObjectFactoryProvider(typeDataAccess, episodeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ISOCountry");
        if(typeDataAccess != null) {
            iSOCountryTypeAPI = new ISOCountryTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iSOCountryTypeAPI = new ISOCountryTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ISOCountry"));
        }
        addTypeAPI(iSOCountryTypeAPI);
        factory = factoryOverrides.get("ISOCountry");
        if(factory == null)
            factory = new ISOCountryHollowFactory();
        if(cachedTypes.contains("ISOCountry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iSOCountryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iSOCountryProvider;
            iSOCountryProvider = new HollowObjectCacheProvider(typeDataAccess, iSOCountryTypeAPI, factory, previousCacheProvider);
        } else {
            iSOCountryProvider = new HollowObjectFactoryProvider(typeDataAccess, iSOCountryTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ISOCountryList");
        if(typeDataAccess != null) {
            iSOCountryListTypeAPI = new ISOCountryListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            iSOCountryListTypeAPI = new ISOCountryListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ISOCountryList"));
        }
        addTypeAPI(iSOCountryListTypeAPI);
        factory = factoryOverrides.get("ISOCountryList");
        if(factory == null)
            factory = new ISOCountryListHollowFactory();
        if(cachedTypes.contains("ISOCountryList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iSOCountryListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iSOCountryListProvider;
            iSOCountryListProvider = new HollowObjectCacheProvider(typeDataAccess, iSOCountryListTypeAPI, factory, previousCacheProvider);
        } else {
            iSOCountryListProvider = new HollowObjectFactoryProvider(typeDataAccess, iSOCountryListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ISOCountrySet");
        if(typeDataAccess != null) {
            iSOCountrySetTypeAPI = new ISOCountrySetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            iSOCountrySetTypeAPI = new ISOCountrySetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "ISOCountrySet"));
        }
        addTypeAPI(iSOCountrySetTypeAPI);
        factory = factoryOverrides.get("ISOCountrySet");
        if(factory == null)
            factory = new ISOCountrySetHollowFactory();
        if(cachedTypes.contains("ISOCountrySet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iSOCountrySetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iSOCountrySetProvider;
            iSOCountrySetProvider = new HollowObjectCacheProvider(typeDataAccess, iSOCountrySetTypeAPI, factory, previousCacheProvider);
        } else {
            iSOCountrySetProvider = new HollowObjectFactoryProvider(typeDataAccess, iSOCountrySetTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfFirstDisplayDates");
        if(typeDataAccess != null) {
            mapOfFirstDisplayDatesTypeAPI = new MapOfFirstDisplayDatesTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfFirstDisplayDatesTypeAPI = new MapOfFirstDisplayDatesTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfFirstDisplayDates"));
        }
        addTypeAPI(mapOfFirstDisplayDatesTypeAPI);
        factory = factoryOverrides.get("MapOfFirstDisplayDates");
        if(factory == null)
            factory = new MapOfFirstDisplayDatesHollowFactory();
        if(cachedTypes.contains("MapOfFirstDisplayDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfFirstDisplayDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfFirstDisplayDatesProvider;
            mapOfFirstDisplayDatesProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfFirstDisplayDatesTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfFirstDisplayDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfFirstDisplayDatesTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseCast");
        if(typeDataAccess != null) {
            rolloutPhaseCastTypeAPI = new RolloutPhaseCastTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseCastTypeAPI = new RolloutPhaseCastTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseCast"));
        }
        addTypeAPI(rolloutPhaseCastTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseCast");
        if(factory == null)
            factory = new RolloutPhaseCastHollowFactory();
        if(cachedTypes.contains("RolloutPhaseCast")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseCastProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseCastProvider;
            rolloutPhaseCastProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseCastTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseCastProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseCastTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseCastList");
        if(typeDataAccess != null) {
            rolloutPhaseCastListTypeAPI = new RolloutPhaseCastListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseCastListTypeAPI = new RolloutPhaseCastListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseCastList"));
        }
        addTypeAPI(rolloutPhaseCastListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseCastList");
        if(factory == null)
            factory = new RolloutPhaseCastListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseCastList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseCastListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseCastListProvider;
            rolloutPhaseCastListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseCastListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseCastListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseCastListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseCharacter");
        if(typeDataAccess != null) {
            rolloutPhaseCharacterTypeAPI = new RolloutPhaseCharacterTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseCharacterTypeAPI = new RolloutPhaseCharacterTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseCharacter"));
        }
        addTypeAPI(rolloutPhaseCharacterTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseCharacter");
        if(factory == null)
            factory = new RolloutPhaseCharacterHollowFactory();
        if(cachedTypes.contains("RolloutPhaseCharacter")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseCharacterProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseCharacterProvider;
            rolloutPhaseCharacterProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseCharacterTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseCharacterProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseCharacterTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseCharacterList");
        if(typeDataAccess != null) {
            rolloutPhaseCharacterListTypeAPI = new RolloutPhaseCharacterListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseCharacterListTypeAPI = new RolloutPhaseCharacterListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseCharacterList"));
        }
        addTypeAPI(rolloutPhaseCharacterListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseCharacterList");
        if(factory == null)
            factory = new RolloutPhaseCharacterListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseCharacterList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseCharacterListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseCharacterListProvider;
            rolloutPhaseCharacterListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseCharacterListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseCharacterListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseCharacterListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseImageId");
        if(typeDataAccess != null) {
            rolloutPhaseImageIdTypeAPI = new RolloutPhaseImageIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseImageIdTypeAPI = new RolloutPhaseImageIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseImageId"));
        }
        addTypeAPI(rolloutPhaseImageIdTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseImageId");
        if(factory == null)
            factory = new RolloutPhaseImageIdHollowFactory();
        if(cachedTypes.contains("RolloutPhaseImageId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseImageIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseImageIdProvider;
            rolloutPhaseImageIdProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseImageIdTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseImageIdProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseImageIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseOldArtworkList");
        if(typeDataAccess != null) {
            rolloutPhaseOldArtworkListTypeAPI = new RolloutPhaseOldArtworkListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseOldArtworkListTypeAPI = new RolloutPhaseOldArtworkListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseOldArtworkList"));
        }
        addTypeAPI(rolloutPhaseOldArtworkListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseOldArtworkList");
        if(factory == null)
            factory = new RolloutPhaseOldArtworkListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseOldArtworkList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseOldArtworkListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseOldArtworkListProvider;
            rolloutPhaseOldArtworkListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseOldArtworkListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseOldArtworkListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseOldArtworkListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseWindow");
        if(typeDataAccess != null) {
            rolloutPhaseWindowTypeAPI = new RolloutPhaseWindowTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseWindowTypeAPI = new RolloutPhaseWindowTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseWindow"));
        }
        addTypeAPI(rolloutPhaseWindowTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseWindow");
        if(factory == null)
            factory = new RolloutPhaseWindowHollowFactory();
        if(cachedTypes.contains("RolloutPhaseWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseWindowProvider;
            rolloutPhaseWindowProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseWindowTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseWindowMap");
        if(typeDataAccess != null) {
            rolloutPhaseWindowMapTypeAPI = new RolloutPhaseWindowMapTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseWindowMapTypeAPI = new RolloutPhaseWindowMapTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RolloutPhaseWindowMap"));
        }
        addTypeAPI(rolloutPhaseWindowMapTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseWindowMap");
        if(factory == null)
            factory = new RolloutPhaseWindowMapHollowFactory();
        if(cachedTypes.contains("RolloutPhaseWindowMap")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseWindowMapProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseWindowMapProvider;
            rolloutPhaseWindowMapProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseWindowMapTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseWindowMapProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseWindowMapTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Season");
        if(typeDataAccess != null) {
            seasonTypeAPI = new SeasonTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            seasonTypeAPI = new SeasonTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Season"));
        }
        addTypeAPI(seasonTypeAPI);
        factory = factoryOverrides.get("Season");
        if(factory == null)
            factory = new SeasonHollowFactory();
        if(cachedTypes.contains("Season")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.seasonProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.seasonProvider;
            seasonProvider = new HollowObjectCacheProvider(typeDataAccess, seasonTypeAPI, factory, previousCacheProvider);
        } else {
            seasonProvider = new HollowObjectFactoryProvider(typeDataAccess, seasonTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SeasonList");
        if(typeDataAccess != null) {
            seasonListTypeAPI = new SeasonListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            seasonListTypeAPI = new SeasonListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "SeasonList"));
        }
        addTypeAPI(seasonListTypeAPI);
        factory = factoryOverrides.get("SeasonList");
        if(factory == null)
            factory = new SeasonListHollowFactory();
        if(cachedTypes.contains("SeasonList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.seasonListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.seasonListProvider;
            seasonListProvider = new HollowObjectCacheProvider(typeDataAccess, seasonListTypeAPI, factory, previousCacheProvider);
        } else {
            seasonListProvider = new HollowObjectFactoryProvider(typeDataAccess, seasonListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamDimensions");
        if(typeDataAccess != null) {
            streamDimensionsTypeAPI = new StreamDimensionsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamDimensionsTypeAPI = new StreamDimensionsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamDimensions"));
        }
        addTypeAPI(streamDimensionsTypeAPI);
        factory = factoryOverrides.get("StreamDimensions");
        if(factory == null)
            factory = new StreamDimensionsHollowFactory();
        if(cachedTypes.contains("StreamDimensions")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamDimensionsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamDimensionsProvider;
            streamDimensionsProvider = new HollowObjectCacheProvider(typeDataAccess, streamDimensionsTypeAPI, factory, previousCacheProvider);
        } else {
            streamDimensionsProvider = new HollowObjectFactoryProvider(typeDataAccess, streamDimensionsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamFileIdentification");
        if(typeDataAccess != null) {
            streamFileIdentificationTypeAPI = new StreamFileIdentificationTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamFileIdentificationTypeAPI = new StreamFileIdentificationTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamFileIdentification"));
        }
        addTypeAPI(streamFileIdentificationTypeAPI);
        factory = factoryOverrides.get("StreamFileIdentification");
        if(factory == null)
            factory = new StreamFileIdentificationHollowFactory();
        if(cachedTypes.contains("StreamFileIdentification")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamFileIdentificationProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamFileIdentificationProvider;
            streamFileIdentificationProvider = new HollowObjectCacheProvider(typeDataAccess, streamFileIdentificationTypeAPI, factory, previousCacheProvider);
        } else {
            streamFileIdentificationProvider = new HollowObjectFactoryProvider(typeDataAccess, streamFileIdentificationTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamProfileId");
        if(typeDataAccess != null) {
            streamProfileIdTypeAPI = new StreamProfileIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamProfileIdTypeAPI = new StreamProfileIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamProfileId"));
        }
        addTypeAPI(streamProfileIdTypeAPI);
        factory = factoryOverrides.get("StreamProfileId");
        if(factory == null)
            factory = new StreamProfileIdHollowFactory();
        if(cachedTypes.contains("StreamProfileId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamProfileIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamProfileIdProvider;
            streamProfileIdProvider = new HollowObjectCacheProvider(typeDataAccess, streamProfileIdTypeAPI, factory, previousCacheProvider);
        } else {
            streamProfileIdProvider = new HollowObjectFactoryProvider(typeDataAccess, streamProfileIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamProfileIdList");
        if(typeDataAccess != null) {
            streamProfileIdListTypeAPI = new StreamProfileIdListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            streamProfileIdListTypeAPI = new StreamProfileIdListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "StreamProfileIdList"));
        }
        addTypeAPI(streamProfileIdListTypeAPI);
        factory = factoryOverrides.get("StreamProfileIdList");
        if(factory == null)
            factory = new StreamProfileIdListHollowFactory();
        if(cachedTypes.contains("StreamProfileIdList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamProfileIdListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamProfileIdListProvider;
            streamProfileIdListProvider = new HollowObjectCacheProvider(typeDataAccess, streamProfileIdListTypeAPI, factory, previousCacheProvider);
        } else {
            streamProfileIdListProvider = new HollowObjectFactoryProvider(typeDataAccess, streamProfileIdListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("AudioStreamInfo");
        if(typeDataAccess != null) {
            audioStreamInfoTypeAPI = new AudioStreamInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            audioStreamInfoTypeAPI = new AudioStreamInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AudioStreamInfo"));
        }
        addTypeAPI(audioStreamInfoTypeAPI);
        factory = factoryOverrides.get("AudioStreamInfo");
        if(factory == null)
            factory = new AudioStreamInfoHollowFactory();
        if(cachedTypes.contains("AudioStreamInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.audioStreamInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.audioStreamInfoProvider;
            audioStreamInfoProvider = new HollowObjectCacheProvider(typeDataAccess, audioStreamInfoTypeAPI, factory, previousCacheProvider);
        } else {
            audioStreamInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, audioStreamInfoTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("CdnDeployment");
        if(typeDataAccess != null) {
            cdnDeploymentTypeAPI = new CdnDeploymentTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            cdnDeploymentTypeAPI = new CdnDeploymentTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CdnDeployment"));
        }
        addTypeAPI(cdnDeploymentTypeAPI);
        factory = factoryOverrides.get("CdnDeployment");
        if(factory == null)
            factory = new CdnDeploymentHollowFactory();
        if(cachedTypes.contains("CdnDeployment")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.cdnDeploymentProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.cdnDeploymentProvider;
            cdnDeploymentProvider = new HollowObjectCacheProvider(typeDataAccess, cdnDeploymentTypeAPI, factory, previousCacheProvider);
        } else {
            cdnDeploymentProvider = new HollowObjectFactoryProvider(typeDataAccess, cdnDeploymentTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CdnDeploymentSet");
        if(typeDataAccess != null) {
            cdnDeploymentSetTypeAPI = new CdnDeploymentSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            cdnDeploymentSetTypeAPI = new CdnDeploymentSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "CdnDeploymentSet"));
        }
        addTypeAPI(cdnDeploymentSetTypeAPI);
        factory = factoryOverrides.get("CdnDeploymentSet");
        if(factory == null)
            factory = new CdnDeploymentSetHollowFactory();
        if(cachedTypes.contains("CdnDeploymentSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.cdnDeploymentSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.cdnDeploymentSetProvider;
            cdnDeploymentSetProvider = new HollowObjectCacheProvider(typeDataAccess, cdnDeploymentSetTypeAPI, factory, previousCacheProvider);
        } else {
            cdnDeploymentSetProvider = new HollowObjectFactoryProvider(typeDataAccess, cdnDeploymentSetTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("CertificationSystemRatingList");
        if(typeDataAccess != null) {
            certificationSystemRatingListTypeAPI = new CertificationSystemRatingListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            certificationSystemRatingListTypeAPI = new CertificationSystemRatingListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CertificationSystemRatingList"));
        }
        addTypeAPI(certificationSystemRatingListTypeAPI);
        factory = factoryOverrides.get("CertificationSystemRatingList");
        if(factory == null)
            factory = new CertificationSystemRatingListHollowFactory();
        if(cachedTypes.contains("CertificationSystemRatingList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.certificationSystemRatingListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.certificationSystemRatingListProvider;
            certificationSystemRatingListProvider = new HollowObjectCacheProvider(typeDataAccess, certificationSystemRatingListTypeAPI, factory, previousCacheProvider);
        } else {
            certificationSystemRatingListProvider = new HollowObjectFactoryProvider(typeDataAccess, certificationSystemRatingListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkDerivative");
        if(typeDataAccess != null) {
            characterArtworkDerivativeTypeAPI = new CharacterArtworkDerivativeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkDerivativeTypeAPI = new CharacterArtworkDerivativeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtworkDerivative"));
        }
        addTypeAPI(characterArtworkDerivativeTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkDerivative");
        if(factory == null)
            factory = new CharacterArtworkDerivativeHollowFactory();
        if(cachedTypes.contains("CharacterArtworkDerivative")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkDerivativeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkDerivativeProvider;
            characterArtworkDerivativeProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkDerivativeTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkDerivativeProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkDerivativeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkDerivativeList");
        if(typeDataAccess != null) {
            characterArtworkDerivativeListTypeAPI = new CharacterArtworkDerivativeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkDerivativeListTypeAPI = new CharacterArtworkDerivativeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterArtworkDerivativeList"));
        }
        addTypeAPI(characterArtworkDerivativeListTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkDerivativeList");
        if(factory == null)
            factory = new CharacterArtworkDerivativeListHollowFactory();
        if(cachedTypes.contains("CharacterArtworkDerivativeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkDerivativeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkDerivativeListProvider;
            characterArtworkDerivativeListProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkDerivativeListTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkDerivativeListProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkDerivativeListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("CountryVideoDisplaySet");
        if(typeDataAccess != null) {
            countryVideoDisplaySetTypeAPI = new CountryVideoDisplaySetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            countryVideoDisplaySetTypeAPI = new CountryVideoDisplaySetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CountryVideoDisplaySet"));
        }
        addTypeAPI(countryVideoDisplaySetTypeAPI);
        factory = factoryOverrides.get("CountryVideoDisplaySet");
        if(factory == null)
            factory = new CountryVideoDisplaySetHollowFactory();
        if(cachedTypes.contains("CountryVideoDisplaySet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.countryVideoDisplaySetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.countryVideoDisplaySetProvider;
            countryVideoDisplaySetProvider = new HollowObjectCacheProvider(typeDataAccess, countryVideoDisplaySetTypeAPI, factory, previousCacheProvider);
        } else {
            countryVideoDisplaySetProvider = new HollowObjectFactoryProvider(typeDataAccess, countryVideoDisplaySetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CountryVideoDisplaySetList");
        if(typeDataAccess != null) {
            countryVideoDisplaySetListTypeAPI = new CountryVideoDisplaySetListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            countryVideoDisplaySetListTypeAPI = new CountryVideoDisplaySetListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CountryVideoDisplaySetList"));
        }
        addTypeAPI(countryVideoDisplaySetListTypeAPI);
        factory = factoryOverrides.get("CountryVideoDisplaySetList");
        if(factory == null)
            factory = new CountryVideoDisplaySetListHollowFactory();
        if(cachedTypes.contains("CountryVideoDisplaySetList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.countryVideoDisplaySetListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.countryVideoDisplaySetListProvider;
            countryVideoDisplaySetListProvider = new HollowObjectCacheProvider(typeDataAccess, countryVideoDisplaySetListTypeAPI, factory, previousCacheProvider);
        } else {
            countryVideoDisplaySetListProvider = new HollowObjectFactoryProvider(typeDataAccess, countryVideoDisplaySetListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("DisallowedSubtitleLangCode");
        if(typeDataAccess != null) {
            disallowedSubtitleLangCodeTypeAPI = new DisallowedSubtitleLangCodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            disallowedSubtitleLangCodeTypeAPI = new DisallowedSubtitleLangCodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DisallowedSubtitleLangCode"));
        }
        addTypeAPI(disallowedSubtitleLangCodeTypeAPI);
        factory = factoryOverrides.get("DisallowedSubtitleLangCode");
        if(factory == null)
            factory = new DisallowedSubtitleLangCodeHollowFactory();
        if(cachedTypes.contains("DisallowedSubtitleLangCode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.disallowedSubtitleLangCodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.disallowedSubtitleLangCodeProvider;
            disallowedSubtitleLangCodeProvider = new HollowObjectCacheProvider(typeDataAccess, disallowedSubtitleLangCodeTypeAPI, factory, previousCacheProvider);
        } else {
            disallowedSubtitleLangCodeProvider = new HollowObjectFactoryProvider(typeDataAccess, disallowedSubtitleLangCodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DisallowedSubtitleLangCodesList");
        if(typeDataAccess != null) {
            disallowedSubtitleLangCodesListTypeAPI = new DisallowedSubtitleLangCodesListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            disallowedSubtitleLangCodesListTypeAPI = new DisallowedSubtitleLangCodesListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "DisallowedSubtitleLangCodesList"));
        }
        addTypeAPI(disallowedSubtitleLangCodesListTypeAPI);
        factory = factoryOverrides.get("DisallowedSubtitleLangCodesList");
        if(factory == null)
            factory = new DisallowedSubtitleLangCodesListHollowFactory();
        if(cachedTypes.contains("DisallowedSubtitleLangCodesList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.disallowedSubtitleLangCodesListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.disallowedSubtitleLangCodesListProvider;
            disallowedSubtitleLangCodesListProvider = new HollowObjectCacheProvider(typeDataAccess, disallowedSubtitleLangCodesListTypeAPI, factory, previousCacheProvider);
        } else {
            disallowedSubtitleLangCodesListProvider = new HollowObjectFactoryProvider(typeDataAccess, disallowedSubtitleLangCodesListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DisallowedAssetBundle");
        if(typeDataAccess != null) {
            disallowedAssetBundleTypeAPI = new DisallowedAssetBundleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            disallowedAssetBundleTypeAPI = new DisallowedAssetBundleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DisallowedAssetBundle"));
        }
        addTypeAPI(disallowedAssetBundleTypeAPI);
        factory = factoryOverrides.get("DisallowedAssetBundle");
        if(factory == null)
            factory = new DisallowedAssetBundleHollowFactory();
        if(cachedTypes.contains("DisallowedAssetBundle")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.disallowedAssetBundleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.disallowedAssetBundleProvider;
            disallowedAssetBundleProvider = new HollowObjectCacheProvider(typeDataAccess, disallowedAssetBundleTypeAPI, factory, previousCacheProvider);
        } else {
            disallowedAssetBundleProvider = new HollowObjectFactoryProvider(typeDataAccess, disallowedAssetBundleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DisallowedAssetBundlesList");
        if(typeDataAccess != null) {
            disallowedAssetBundlesListTypeAPI = new DisallowedAssetBundlesListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            disallowedAssetBundlesListTypeAPI = new DisallowedAssetBundlesListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "DisallowedAssetBundlesList"));
        }
        addTypeAPI(disallowedAssetBundlesListTypeAPI);
        factory = factoryOverrides.get("DisallowedAssetBundlesList");
        if(factory == null)
            factory = new DisallowedAssetBundlesListHollowFactory();
        if(cachedTypes.contains("DisallowedAssetBundlesList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.disallowedAssetBundlesListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.disallowedAssetBundlesListProvider;
            disallowedAssetBundlesListProvider = new HollowObjectCacheProvider(typeDataAccess, disallowedAssetBundlesListTypeAPI, factory, previousCacheProvider);
        } else {
            disallowedAssetBundlesListProvider = new HollowObjectFactoryProvider(typeDataAccess, disallowedAssetBundlesListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DrmHeaderInfo");
        if(typeDataAccess != null) {
            drmHeaderInfoTypeAPI = new DrmHeaderInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            drmHeaderInfoTypeAPI = new DrmHeaderInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DrmHeaderInfo"));
        }
        addTypeAPI(drmHeaderInfoTypeAPI);
        factory = factoryOverrides.get("DrmHeaderInfo");
        if(factory == null)
            factory = new DrmHeaderInfoHollowFactory();
        if(cachedTypes.contains("DrmHeaderInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.drmHeaderInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.drmHeaderInfoProvider;
            drmHeaderInfoProvider = new HollowObjectCacheProvider(typeDataAccess, drmHeaderInfoTypeAPI, factory, previousCacheProvider);
        } else {
            drmHeaderInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, drmHeaderInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DrmHeaderInfoList");
        if(typeDataAccess != null) {
            drmHeaderInfoListTypeAPI = new DrmHeaderInfoListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            drmHeaderInfoListTypeAPI = new DrmHeaderInfoListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "DrmHeaderInfoList"));
        }
        addTypeAPI(drmHeaderInfoListTypeAPI);
        factory = factoryOverrides.get("DrmHeaderInfoList");
        if(factory == null)
            factory = new DrmHeaderInfoListHollowFactory();
        if(cachedTypes.contains("DrmHeaderInfoList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.drmHeaderInfoListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.drmHeaderInfoListProvider;
            drmHeaderInfoListProvider = new HollowObjectCacheProvider(typeDataAccess, drmHeaderInfoListTypeAPI, factory, previousCacheProvider);
        } else {
            drmHeaderInfoListProvider = new HollowObjectFactoryProvider(typeDataAccess, drmHeaderInfoListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ImageStreamInfo");
        if(typeDataAccess != null) {
            imageStreamInfoTypeAPI = new ImageStreamInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            imageStreamInfoTypeAPI = new ImageStreamInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ImageStreamInfo"));
        }
        addTypeAPI(imageStreamInfoTypeAPI);
        factory = factoryOverrides.get("ImageStreamInfo");
        if(factory == null)
            factory = new ImageStreamInfoHollowFactory();
        if(cachedTypes.contains("ImageStreamInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.imageStreamInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.imageStreamInfoProvider;
            imageStreamInfoProvider = new HollowObjectCacheProvider(typeDataAccess, imageStreamInfoTypeAPI, factory, previousCacheProvider);
        } else {
            imageStreamInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, imageStreamInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocaleTerritoryCode");
        if(typeDataAccess != null) {
            localeTerritoryCodeTypeAPI = new LocaleTerritoryCodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            localeTerritoryCodeTypeAPI = new LocaleTerritoryCodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LocaleTerritoryCode"));
        }
        addTypeAPI(localeTerritoryCodeTypeAPI);
        factory = factoryOverrides.get("LocaleTerritoryCode");
        if(factory == null)
            factory = new LocaleTerritoryCodeHollowFactory();
        if(cachedTypes.contains("LocaleTerritoryCode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localeTerritoryCodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localeTerritoryCodeProvider;
            localeTerritoryCodeProvider = new HollowObjectCacheProvider(typeDataAccess, localeTerritoryCodeTypeAPI, factory, previousCacheProvider);
        } else {
            localeTerritoryCodeProvider = new HollowObjectFactoryProvider(typeDataAccess, localeTerritoryCodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocaleTerritoryCodeList");
        if(typeDataAccess != null) {
            localeTerritoryCodeListTypeAPI = new LocaleTerritoryCodeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            localeTerritoryCodeListTypeAPI = new LocaleTerritoryCodeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "LocaleTerritoryCodeList"));
        }
        addTypeAPI(localeTerritoryCodeListTypeAPI);
        factory = factoryOverrides.get("LocaleTerritoryCodeList");
        if(factory == null)
            factory = new LocaleTerritoryCodeListHollowFactory();
        if(cachedTypes.contains("LocaleTerritoryCodeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localeTerritoryCodeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localeTerritoryCodeListProvider;
            localeTerritoryCodeListProvider = new HollowObjectCacheProvider(typeDataAccess, localeTerritoryCodeListTypeAPI, factory, previousCacheProvider);
        } else {
            localeTerritoryCodeListProvider = new HollowObjectFactoryProvider(typeDataAccess, localeTerritoryCodeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkLocale");
        if(typeDataAccess != null) {
            characterArtworkLocaleTypeAPI = new CharacterArtworkLocaleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkLocaleTypeAPI = new CharacterArtworkLocaleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtworkLocale"));
        }
        addTypeAPI(characterArtworkLocaleTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkLocale");
        if(factory == null)
            factory = new CharacterArtworkLocaleHollowFactory();
        if(cachedTypes.contains("CharacterArtworkLocale")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkLocaleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkLocaleProvider;
            characterArtworkLocaleProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkLocaleTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkLocaleProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkLocaleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkLocaleList");
        if(typeDataAccess != null) {
            characterArtworkLocaleListTypeAPI = new CharacterArtworkLocaleListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkLocaleListTypeAPI = new CharacterArtworkLocaleListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterArtworkLocaleList"));
        }
        addTypeAPI(characterArtworkLocaleListTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkLocaleList");
        if(factory == null)
            factory = new CharacterArtworkLocaleListHollowFactory();
        if(cachedTypes.contains("CharacterArtworkLocaleList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkLocaleListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkLocaleListProvider;
            characterArtworkLocaleListProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkLocaleListTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkLocaleListProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkLocaleListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("PackageDrmInfo");
        if(typeDataAccess != null) {
            packageDrmInfoTypeAPI = new PackageDrmInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            packageDrmInfoTypeAPI = new PackageDrmInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PackageDrmInfo"));
        }
        addTypeAPI(packageDrmInfoTypeAPI);
        factory = factoryOverrides.get("PackageDrmInfo");
        if(factory == null)
            factory = new PackageDrmInfoHollowFactory();
        if(cachedTypes.contains("PackageDrmInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageDrmInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageDrmInfoProvider;
            packageDrmInfoProvider = new HollowObjectCacheProvider(typeDataAccess, packageDrmInfoTypeAPI, factory, previousCacheProvider);
        } else {
            packageDrmInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, packageDrmInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PackageDrmInfoList");
        if(typeDataAccess != null) {
            packageDrmInfoListTypeAPI = new PackageDrmInfoListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            packageDrmInfoListTypeAPI = new PackageDrmInfoListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PackageDrmInfoList"));
        }
        addTypeAPI(packageDrmInfoListTypeAPI);
        factory = factoryOverrides.get("PackageDrmInfoList");
        if(factory == null)
            factory = new PackageDrmInfoListHollowFactory();
        if(cachedTypes.contains("PackageDrmInfoList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageDrmInfoListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageDrmInfoListProvider;
            packageDrmInfoListProvider = new HollowObjectCacheProvider(typeDataAccess, packageDrmInfoListTypeAPI, factory, previousCacheProvider);
        } else {
            packageDrmInfoListProvider = new HollowObjectFactoryProvider(typeDataAccess, packageDrmInfoListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PackageMoment");
        if(typeDataAccess != null) {
            packageMomentTypeAPI = new PackageMomentTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            packageMomentTypeAPI = new PackageMomentTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PackageMoment"));
        }
        addTypeAPI(packageMomentTypeAPI);
        factory = factoryOverrides.get("PackageMoment");
        if(factory == null)
            factory = new PackageMomentHollowFactory();
        if(cachedTypes.contains("PackageMoment")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageMomentProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageMomentProvider;
            packageMomentProvider = new HollowObjectCacheProvider(typeDataAccess, packageMomentTypeAPI, factory, previousCacheProvider);
        } else {
            packageMomentProvider = new HollowObjectFactoryProvider(typeDataAccess, packageMomentTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PackageMomentList");
        if(typeDataAccess != null) {
            packageMomentListTypeAPI = new PackageMomentListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            packageMomentListTypeAPI = new PackageMomentListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PackageMomentList"));
        }
        addTypeAPI(packageMomentListTypeAPI);
        factory = factoryOverrides.get("PackageMomentList");
        if(factory == null)
            factory = new PackageMomentListHollowFactory();
        if(cachedTypes.contains("PackageMomentList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageMomentListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageMomentListProvider;
            packageMomentListProvider = new HollowObjectCacheProvider(typeDataAccess, packageMomentListTypeAPI, factory, previousCacheProvider);
        } else {
            packageMomentListProvider = new HollowObjectFactoryProvider(typeDataAccess, packageMomentListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkAttribute");
        if(typeDataAccess != null) {
            personArtworkAttributeTypeAPI = new PersonArtworkAttributeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkAttributeTypeAPI = new PersonArtworkAttributeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkAttribute"));
        }
        addTypeAPI(personArtworkAttributeTypeAPI);
        factory = factoryOverrides.get("PersonArtworkAttribute");
        if(factory == null)
            factory = new PersonArtworkAttributeHollowFactory();
        if(cachedTypes.contains("PersonArtworkAttribute")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkAttributeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkAttributeProvider;
            personArtworkAttributeProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkAttributeTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkAttributeProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkAttributeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkDerivative");
        if(typeDataAccess != null) {
            personArtworkDerivativeTypeAPI = new PersonArtworkDerivativeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkDerivativeTypeAPI = new PersonArtworkDerivativeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkDerivative"));
        }
        addTypeAPI(personArtworkDerivativeTypeAPI);
        factory = factoryOverrides.get("PersonArtworkDerivative");
        if(factory == null)
            factory = new PersonArtworkDerivativeHollowFactory();
        if(cachedTypes.contains("PersonArtworkDerivative")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkDerivativeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkDerivativeProvider;
            personArtworkDerivativeProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkDerivativeTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkDerivativeProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkDerivativeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkDerivativeList");
        if(typeDataAccess != null) {
            personArtworkDerivativeListTypeAPI = new PersonArtworkDerivativeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personArtworkDerivativeListTypeAPI = new PersonArtworkDerivativeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonArtworkDerivativeList"));
        }
        addTypeAPI(personArtworkDerivativeListTypeAPI);
        factory = factoryOverrides.get("PersonArtworkDerivativeList");
        if(factory == null)
            factory = new PersonArtworkDerivativeListHollowFactory();
        if(cachedTypes.contains("PersonArtworkDerivativeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkDerivativeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkDerivativeListProvider;
            personArtworkDerivativeListProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkDerivativeListTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkDerivativeListProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkDerivativeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkLocale");
        if(typeDataAccess != null) {
            personArtworkLocaleTypeAPI = new PersonArtworkLocaleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkLocaleTypeAPI = new PersonArtworkLocaleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkLocale"));
        }
        addTypeAPI(personArtworkLocaleTypeAPI);
        factory = factoryOverrides.get("PersonArtworkLocale");
        if(factory == null)
            factory = new PersonArtworkLocaleHollowFactory();
        if(cachedTypes.contains("PersonArtworkLocale")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkLocaleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkLocaleProvider;
            personArtworkLocaleProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkLocaleTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkLocaleProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkLocaleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkLocaleList");
        if(typeDataAccess != null) {
            personArtworkLocaleListTypeAPI = new PersonArtworkLocaleListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personArtworkLocaleListTypeAPI = new PersonArtworkLocaleListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonArtworkLocaleList"));
        }
        addTypeAPI(personArtworkLocaleListTypeAPI);
        factory = factoryOverrides.get("PersonArtworkLocaleList");
        if(factory == null)
            factory = new PersonArtworkLocaleListHollowFactory();
        if(cachedTypes.contains("PersonArtworkLocaleList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkLocaleListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkLocaleListProvider;
            personArtworkLocaleListProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkLocaleListTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkLocaleListProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkLocaleListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseArtworkSourceFileId");
        if(typeDataAccess != null) {
            rolloutPhaseArtworkSourceFileIdTypeAPI = new RolloutPhaseArtworkSourceFileIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseArtworkSourceFileIdTypeAPI = new RolloutPhaseArtworkSourceFileIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseArtworkSourceFileId"));
        }
        addTypeAPI(rolloutPhaseArtworkSourceFileIdTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseArtworkSourceFileId");
        if(factory == null)
            factory = new RolloutPhaseArtworkSourceFileIdHollowFactory();
        if(cachedTypes.contains("RolloutPhaseArtworkSourceFileId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseArtworkSourceFileIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseArtworkSourceFileIdProvider;
            rolloutPhaseArtworkSourceFileIdProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseArtworkSourceFileIdProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseArtworkSourceFileIdList");
        if(typeDataAccess != null) {
            rolloutPhaseArtworkSourceFileIdListTypeAPI = new RolloutPhaseArtworkSourceFileIdListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseArtworkSourceFileIdListTypeAPI = new RolloutPhaseArtworkSourceFileIdListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseArtworkSourceFileIdList"));
        }
        addTypeAPI(rolloutPhaseArtworkSourceFileIdListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseArtworkSourceFileIdList");
        if(factory == null)
            factory = new RolloutPhaseArtworkSourceFileIdListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseArtworkSourceFileIdList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseArtworkSourceFileIdListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseArtworkSourceFileIdListProvider;
            rolloutPhaseArtworkSourceFileIdListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseArtworkSourceFileIdListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseLocalizedMetadata");
        if(typeDataAccess != null) {
            rolloutPhaseLocalizedMetadataTypeAPI = new RolloutPhaseLocalizedMetadataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseLocalizedMetadataTypeAPI = new RolloutPhaseLocalizedMetadataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseLocalizedMetadata"));
        }
        addTypeAPI(rolloutPhaseLocalizedMetadataTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseLocalizedMetadata");
        if(factory == null)
            factory = new RolloutPhaseLocalizedMetadataHollowFactory();
        if(cachedTypes.contains("RolloutPhaseLocalizedMetadata")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseLocalizedMetadataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseLocalizedMetadataProvider;
            rolloutPhaseLocalizedMetadataProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseLocalizedMetadataTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseLocalizedMetadataProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseLocalizedMetadataTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseNewArtwork");
        if(typeDataAccess != null) {
            rolloutPhaseNewArtworkTypeAPI = new RolloutPhaseNewArtworkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseNewArtworkTypeAPI = new RolloutPhaseNewArtworkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseNewArtwork"));
        }
        addTypeAPI(rolloutPhaseNewArtworkTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseNewArtwork");
        if(factory == null)
            factory = new RolloutPhaseNewArtworkHollowFactory();
        if(cachedTypes.contains("RolloutPhaseNewArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseNewArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseNewArtworkProvider;
            rolloutPhaseNewArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseNewArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseNewArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseNewArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseTrailerSupplementalInfo");
        if(typeDataAccess != null) {
            rolloutPhaseTrailerSupplementalInfoTypeAPI = new RolloutPhaseTrailerSupplementalInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseTrailerSupplementalInfoTypeAPI = new RolloutPhaseTrailerSupplementalInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseTrailerSupplementalInfo"));
        }
        addTypeAPI(rolloutPhaseTrailerSupplementalInfoTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseTrailerSupplementalInfo");
        if(factory == null)
            factory = new RolloutPhaseTrailerSupplementalInfoHollowFactory();
        if(cachedTypes.contains("RolloutPhaseTrailerSupplementalInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseTrailerSupplementalInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseTrailerSupplementalInfoProvider;
            rolloutPhaseTrailerSupplementalInfoProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseTrailerSupplementalInfoTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseTrailerSupplementalInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseTrailerSupplementalInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhasesElementsTrailerSupplementalInfoMap");
        if(typeDataAccess != null) {
            rolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI = new RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI = new RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RolloutPhasesElementsTrailerSupplementalInfoMap"));
        }
        addTypeAPI(rolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI);
        factory = factoryOverrides.get("RolloutPhasesElementsTrailerSupplementalInfoMap");
        if(factory == null)
            factory = new RolloutPhasesElementsTrailerSupplementalInfoMapHollowFactory();
        if(cachedTypes.contains("RolloutPhasesElementsTrailerSupplementalInfoMap")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhasesElementsTrailerSupplementalInfoMapProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhasesElementsTrailerSupplementalInfoMapProvider;
            rolloutPhasesElementsTrailerSupplementalInfoMapProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhasesElementsTrailerSupplementalInfoMapProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseTrailer");
        if(typeDataAccess != null) {
            rolloutPhaseTrailerTypeAPI = new RolloutPhaseTrailerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseTrailerTypeAPI = new RolloutPhaseTrailerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseTrailer"));
        }
        addTypeAPI(rolloutPhaseTrailerTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseTrailer");
        if(factory == null)
            factory = new RolloutPhaseTrailerHollowFactory();
        if(cachedTypes.contains("RolloutPhaseTrailer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseTrailerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseTrailerProvider;
            rolloutPhaseTrailerProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseTrailerTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseTrailerProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseTrailerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseTrailerList");
        if(typeDataAccess != null) {
            rolloutPhaseTrailerListTypeAPI = new RolloutPhaseTrailerListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseTrailerListTypeAPI = new RolloutPhaseTrailerListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseTrailerList"));
        }
        addTypeAPI(rolloutPhaseTrailerListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseTrailerList");
        if(factory == null)
            factory = new RolloutPhaseTrailerListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseTrailerList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseTrailerListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseTrailerListProvider;
            rolloutPhaseTrailerListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseTrailerListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseTrailerListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseTrailerListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseElements");
        if(typeDataAccess != null) {
            rolloutPhaseElementsTypeAPI = new RolloutPhaseElementsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseElementsTypeAPI = new RolloutPhaseElementsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseElements"));
        }
        addTypeAPI(rolloutPhaseElementsTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseElements");
        if(factory == null)
            factory = new RolloutPhaseElementsHollowFactory();
        if(cachedTypes.contains("RolloutPhaseElements")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseElementsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseElementsProvider;
            rolloutPhaseElementsProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseElementsTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseElementsProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseElementsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhase");
        if(typeDataAccess != null) {
            rolloutPhaseTypeAPI = new RolloutPhaseTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseTypeAPI = new RolloutPhaseTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhase"));
        }
        addTypeAPI(rolloutPhaseTypeAPI);
        factory = factoryOverrides.get("RolloutPhase");
        if(factory == null)
            factory = new RolloutPhaseHollowFactory();
        if(cachedTypes.contains("RolloutPhase")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseProvider;
            rolloutPhaseProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseList");
        if(typeDataAccess != null) {
            rolloutPhaseListTypeAPI = new RolloutPhaseListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseListTypeAPI = new RolloutPhaseListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseList"));
        }
        addTypeAPI(rolloutPhaseListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseList");
        if(factory == null)
            factory = new RolloutPhaseListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseListProvider;
            rolloutPhaseListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("StreamAssetType");
        if(typeDataAccess != null) {
            streamAssetTypeTypeAPI = new StreamAssetTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamAssetTypeTypeAPI = new StreamAssetTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamAssetType"));
        }
        addTypeAPI(streamAssetTypeTypeAPI);
        factory = factoryOverrides.get("StreamAssetType");
        if(factory == null)
            factory = new StreamAssetTypeHollowFactory();
        if(cachedTypes.contains("StreamAssetType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamAssetTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamAssetTypeProvider;
            streamAssetTypeProvider = new HollowObjectCacheProvider(typeDataAccess, streamAssetTypeTypeAPI, factory, previousCacheProvider);
        } else {
            streamAssetTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, streamAssetTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamDeploymentInfo");
        if(typeDataAccess != null) {
            streamDeploymentInfoTypeAPI = new StreamDeploymentInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamDeploymentInfoTypeAPI = new StreamDeploymentInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamDeploymentInfo"));
        }
        addTypeAPI(streamDeploymentInfoTypeAPI);
        factory = factoryOverrides.get("StreamDeploymentInfo");
        if(factory == null)
            factory = new StreamDeploymentInfoHollowFactory();
        if(cachedTypes.contains("StreamDeploymentInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamDeploymentInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamDeploymentInfoProvider;
            streamDeploymentInfoProvider = new HollowObjectCacheProvider(typeDataAccess, streamDeploymentInfoTypeAPI, factory, previousCacheProvider);
        } else {
            streamDeploymentInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, streamDeploymentInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamDeploymentLabel");
        if(typeDataAccess != null) {
            streamDeploymentLabelTypeAPI = new StreamDeploymentLabelTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamDeploymentLabelTypeAPI = new StreamDeploymentLabelTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamDeploymentLabel"));
        }
        addTypeAPI(streamDeploymentLabelTypeAPI);
        factory = factoryOverrides.get("StreamDeploymentLabel");
        if(factory == null)
            factory = new StreamDeploymentLabelHollowFactory();
        if(cachedTypes.contains("StreamDeploymentLabel")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamDeploymentLabelProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamDeploymentLabelProvider;
            streamDeploymentLabelProvider = new HollowObjectCacheProvider(typeDataAccess, streamDeploymentLabelTypeAPI, factory, previousCacheProvider);
        } else {
            streamDeploymentLabelProvider = new HollowObjectFactoryProvider(typeDataAccess, streamDeploymentLabelTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamDeploymentLabelSet");
        if(typeDataAccess != null) {
            streamDeploymentLabelSetTypeAPI = new StreamDeploymentLabelSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            streamDeploymentLabelSetTypeAPI = new StreamDeploymentLabelSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "StreamDeploymentLabelSet"));
        }
        addTypeAPI(streamDeploymentLabelSetTypeAPI);
        factory = factoryOverrides.get("StreamDeploymentLabelSet");
        if(factory == null)
            factory = new StreamDeploymentLabelSetHollowFactory();
        if(cachedTypes.contains("StreamDeploymentLabelSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamDeploymentLabelSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamDeploymentLabelSetProvider;
            streamDeploymentLabelSetProvider = new HollowObjectCacheProvider(typeDataAccess, streamDeploymentLabelSetTypeAPI, factory, previousCacheProvider);
        } else {
            streamDeploymentLabelSetProvider = new HollowObjectFactoryProvider(typeDataAccess, streamDeploymentLabelSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamDeployment");
        if(typeDataAccess != null) {
            streamDeploymentTypeAPI = new StreamDeploymentTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamDeploymentTypeAPI = new StreamDeploymentTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamDeployment"));
        }
        addTypeAPI(streamDeploymentTypeAPI);
        factory = factoryOverrides.get("StreamDeployment");
        if(factory == null)
            factory = new StreamDeploymentHollowFactory();
        if(cachedTypes.contains("StreamDeployment")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamDeploymentProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamDeploymentProvider;
            streamDeploymentProvider = new HollowObjectCacheProvider(typeDataAccess, streamDeploymentTypeAPI, factory, previousCacheProvider);
        } else {
            streamDeploymentProvider = new HollowObjectFactoryProvider(typeDataAccess, streamDeploymentTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamDrmInfo");
        if(typeDataAccess != null) {
            streamDrmInfoTypeAPI = new StreamDrmInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamDrmInfoTypeAPI = new StreamDrmInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamDrmInfo"));
        }
        addTypeAPI(streamDrmInfoTypeAPI);
        factory = factoryOverrides.get("StreamDrmInfo");
        if(factory == null)
            factory = new StreamDrmInfoHollowFactory();
        if(cachedTypes.contains("StreamDrmInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamDrmInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamDrmInfoProvider;
            streamDrmInfoProvider = new HollowObjectCacheProvider(typeDataAccess, streamDrmInfoTypeAPI, factory, previousCacheProvider);
        } else {
            streamDrmInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, streamDrmInfoTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("TextStreamInfo");
        if(typeDataAccess != null) {
            textStreamInfoTypeAPI = new TextStreamInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            textStreamInfoTypeAPI = new TextStreamInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TextStreamInfo"));
        }
        addTypeAPI(textStreamInfoTypeAPI);
        factory = factoryOverrides.get("TextStreamInfo");
        if(factory == null)
            factory = new TextStreamInfoHollowFactory();
        if(cachedTypes.contains("TextStreamInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.textStreamInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.textStreamInfoProvider;
            textStreamInfoProvider = new HollowObjectCacheProvider(typeDataAccess, textStreamInfoTypeAPI, factory, previousCacheProvider);
        } else {
            textStreamInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, textStreamInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TopNAttribute");
        if(typeDataAccess != null) {
            topNAttributeTypeAPI = new TopNAttributeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            topNAttributeTypeAPI = new TopNAttributeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TopNAttribute"));
        }
        addTypeAPI(topNAttributeTypeAPI);
        factory = factoryOverrides.get("TopNAttribute");
        if(factory == null)
            factory = new TopNAttributeHollowFactory();
        if(cachedTypes.contains("TopNAttribute")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNAttributeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNAttributeProvider;
            topNAttributeProvider = new HollowObjectCacheProvider(typeDataAccess, topNAttributeTypeAPI, factory, previousCacheProvider);
        } else {
            topNAttributeProvider = new HollowObjectFactoryProvider(typeDataAccess, topNAttributeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TopNAttributesList");
        if(typeDataAccess != null) {
            topNAttributesListTypeAPI = new TopNAttributesListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            topNAttributesListTypeAPI = new TopNAttributesListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "TopNAttributesList"));
        }
        addTypeAPI(topNAttributesListTypeAPI);
        factory = factoryOverrides.get("TopNAttributesList");
        if(factory == null)
            factory = new TopNAttributesListHollowFactory();
        if(cachedTypes.contains("TopNAttributesList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNAttributesListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNAttributesListProvider;
            topNAttributesListProvider = new HollowObjectCacheProvider(typeDataAccess, topNAttributesListTypeAPI, factory, previousCacheProvider);
        } else {
            topNAttributesListProvider = new HollowObjectFactoryProvider(typeDataAccess, topNAttributesListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("TrailerTheme");
        if(typeDataAccess != null) {
            trailerThemeTypeAPI = new TrailerThemeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            trailerThemeTypeAPI = new TrailerThemeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TrailerTheme"));
        }
        addTypeAPI(trailerThemeTypeAPI);
        factory = factoryOverrides.get("TrailerTheme");
        if(factory == null)
            factory = new TrailerThemeHollowFactory();
        if(cachedTypes.contains("TrailerTheme")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailerThemeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailerThemeProvider;
            trailerThemeProvider = new HollowObjectCacheProvider(typeDataAccess, trailerThemeTypeAPI, factory, previousCacheProvider);
        } else {
            trailerThemeProvider = new HollowObjectFactoryProvider(typeDataAccess, trailerThemeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TrailerThemeList");
        if(typeDataAccess != null) {
            trailerThemeListTypeAPI = new TrailerThemeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            trailerThemeListTypeAPI = new TrailerThemeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "TrailerThemeList"));
        }
        addTypeAPI(trailerThemeListTypeAPI);
        factory = factoryOverrides.get("TrailerThemeList");
        if(factory == null)
            factory = new TrailerThemeListHollowFactory();
        if(cachedTypes.contains("TrailerThemeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailerThemeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailerThemeListProvider;
            trailerThemeListProvider = new HollowObjectCacheProvider(typeDataAccess, trailerThemeListTypeAPI, factory, previousCacheProvider);
        } else {
            trailerThemeListProvider = new HollowObjectFactoryProvider(typeDataAccess, trailerThemeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IndividualTrailer");
        if(typeDataAccess != null) {
            individualTrailerTypeAPI = new IndividualTrailerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            individualTrailerTypeAPI = new IndividualTrailerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IndividualTrailer"));
        }
        addTypeAPI(individualTrailerTypeAPI);
        factory = factoryOverrides.get("IndividualTrailer");
        if(factory == null)
            factory = new IndividualTrailerHollowFactory();
        if(cachedTypes.contains("IndividualTrailer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.individualTrailerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.individualTrailerProvider;
            individualTrailerProvider = new HollowObjectCacheProvider(typeDataAccess, individualTrailerTypeAPI, factory, previousCacheProvider);
        } else {
            individualTrailerProvider = new HollowObjectFactoryProvider(typeDataAccess, individualTrailerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TrailersList");
        if(typeDataAccess != null) {
            trailersListTypeAPI = new TrailersListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            trailersListTypeAPI = new TrailersListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "TrailersList"));
        }
        addTypeAPI(trailersListTypeAPI);
        factory = factoryOverrides.get("TrailersList");
        if(factory == null)
            factory = new TrailersListHollowFactory();
        if(cachedTypes.contains("TrailersList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.trailersListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.trailersListProvider;
            trailersListProvider = new HollowObjectCacheProvider(typeDataAccess, trailersListTypeAPI, factory, previousCacheProvider);
        } else {
            trailersListProvider = new HollowObjectFactoryProvider(typeDataAccess, trailersListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("TranslatedTextValue");
        if(typeDataAccess != null) {
            translatedTextValueTypeAPI = new TranslatedTextValueTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            translatedTextValueTypeAPI = new TranslatedTextValueTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TranslatedTextValue"));
        }
        addTypeAPI(translatedTextValueTypeAPI);
        factory = factoryOverrides.get("TranslatedTextValue");
        if(factory == null)
            factory = new TranslatedTextValueHollowFactory();
        if(cachedTypes.contains("TranslatedTextValue")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.translatedTextValueProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.translatedTextValueProvider;
            translatedTextValueProvider = new HollowObjectCacheProvider(typeDataAccess, translatedTextValueTypeAPI, factory, previousCacheProvider);
        } else {
            translatedTextValueProvider = new HollowObjectFactoryProvider(typeDataAccess, translatedTextValueTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfTranslatedText");
        if(typeDataAccess != null) {
            mapOfTranslatedTextTypeAPI = new MapOfTranslatedTextTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfTranslatedTextTypeAPI = new MapOfTranslatedTextTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfTranslatedText"));
        }
        addTypeAPI(mapOfTranslatedTextTypeAPI);
        factory = factoryOverrides.get("MapOfTranslatedText");
        if(factory == null)
            factory = new MapOfTranslatedTextHollowFactory();
        if(cachedTypes.contains("MapOfTranslatedText")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfTranslatedTextProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfTranslatedTextProvider;
            mapOfTranslatedTextProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfTranslatedTextTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfTranslatedTextProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfTranslatedTextTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("AltGenresAlternateNamesList");
        if(typeDataAccess != null) {
            altGenresAlternateNamesListTypeAPI = new AltGenresAlternateNamesListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            altGenresAlternateNamesListTypeAPI = new AltGenresAlternateNamesListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "AltGenresAlternateNamesList"));
        }
        addTypeAPI(altGenresAlternateNamesListTypeAPI);
        factory = factoryOverrides.get("AltGenresAlternateNamesList");
        if(factory == null)
            factory = new AltGenresAlternateNamesListHollowFactory();
        if(cachedTypes.contains("AltGenresAlternateNamesList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.altGenresAlternateNamesListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.altGenresAlternateNamesListProvider;
            altGenresAlternateNamesListProvider = new HollowObjectCacheProvider(typeDataAccess, altGenresAlternateNamesListTypeAPI, factory, previousCacheProvider);
        } else {
            altGenresAlternateNamesListProvider = new HollowObjectFactoryProvider(typeDataAccess, altGenresAlternateNamesListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("StoriesSynopsesHook");
        if(typeDataAccess != null) {
            storiesSynopsesHookTypeAPI = new StoriesSynopsesHookTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            storiesSynopsesHookTypeAPI = new StoriesSynopsesHookTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StoriesSynopsesHook"));
        }
        addTypeAPI(storiesSynopsesHookTypeAPI);
        factory = factoryOverrides.get("StoriesSynopsesHook");
        if(factory == null)
            factory = new StoriesSynopsesHookHollowFactory();
        if(cachedTypes.contains("StoriesSynopsesHook")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.storiesSynopsesHookProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.storiesSynopsesHookProvider;
            storiesSynopsesHookProvider = new HollowObjectCacheProvider(typeDataAccess, storiesSynopsesHookTypeAPI, factory, previousCacheProvider);
        } else {
            storiesSynopsesHookProvider = new HollowObjectFactoryProvider(typeDataAccess, storiesSynopsesHookTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StoriesSynopsesHookList");
        if(typeDataAccess != null) {
            storiesSynopsesHookListTypeAPI = new StoriesSynopsesHookListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            storiesSynopsesHookListTypeAPI = new StoriesSynopsesHookListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "StoriesSynopsesHookList"));
        }
        addTypeAPI(storiesSynopsesHookListTypeAPI);
        factory = factoryOverrides.get("StoriesSynopsesHookList");
        if(factory == null)
            factory = new StoriesSynopsesHookListHollowFactory();
        if(cachedTypes.contains("StoriesSynopsesHookList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.storiesSynopsesHookListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.storiesSynopsesHookListProvider;
            storiesSynopsesHookListProvider = new HollowObjectCacheProvider(typeDataAccess, storiesSynopsesHookListTypeAPI, factory, previousCacheProvider);
        } else {
            storiesSynopsesHookListProvider = new HollowObjectFactoryProvider(typeDataAccess, storiesSynopsesHookListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TranslatedText");
        if(typeDataAccess != null) {
            translatedTextTypeAPI = new TranslatedTextTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            translatedTextTypeAPI = new TranslatedTextTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TranslatedText"));
        }
        addTypeAPI(translatedTextTypeAPI);
        factory = factoryOverrides.get("TranslatedText");
        if(factory == null)
            factory = new TranslatedTextHollowFactory();
        if(cachedTypes.contains("TranslatedText")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.translatedTextProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.translatedTextProvider;
            translatedTextProvider = new HollowObjectCacheProvider(typeDataAccess, translatedTextTypeAPI, factory, previousCacheProvider);
        } else {
            translatedTextProvider = new HollowObjectFactoryProvider(typeDataAccess, translatedTextTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertSystemRating");
        if(typeDataAccess != null) {
            consolidatedCertSystemRatingTypeAPI = new ConsolidatedCertSystemRatingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertSystemRatingTypeAPI = new ConsolidatedCertSystemRatingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedCertSystemRating"));
        }
        addTypeAPI(consolidatedCertSystemRatingTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertSystemRating");
        if(factory == null)
            factory = new ConsolidatedCertSystemRatingHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertSystemRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertSystemRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertSystemRatingProvider;
            consolidatedCertSystemRatingProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertSystemRatingTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertSystemRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertSystemRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedCertSystemRatingList");
        if(typeDataAccess != null) {
            consolidatedCertSystemRatingListTypeAPI = new ConsolidatedCertSystemRatingListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedCertSystemRatingListTypeAPI = new ConsolidatedCertSystemRatingListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedCertSystemRatingList"));
        }
        addTypeAPI(consolidatedCertSystemRatingListTypeAPI);
        factory = factoryOverrides.get("ConsolidatedCertSystemRatingList");
        if(factory == null)
            factory = new ConsolidatedCertSystemRatingListHollowFactory();
        if(cachedTypes.contains("ConsolidatedCertSystemRatingList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedCertSystemRatingListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedCertSystemRatingListProvider;
            consolidatedCertSystemRatingListProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedCertSystemRatingListTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedCertSystemRatingListProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedCertSystemRatingListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("TurboCollections");
        if(typeDataAccess != null) {
            turboCollectionsTypeAPI = new TurboCollectionsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            turboCollectionsTypeAPI = new TurboCollectionsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TurboCollections"));
        }
        addTypeAPI(turboCollectionsTypeAPI);
        factory = factoryOverrides.get("TurboCollections");
        if(factory == null)
            factory = new TurboCollectionsHollowFactory();
        if(cachedTypes.contains("TurboCollections")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.turboCollectionsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.turboCollectionsProvider;
            turboCollectionsProvider = new HollowObjectCacheProvider(typeDataAccess, turboCollectionsTypeAPI, factory, previousCacheProvider);
        } else {
            turboCollectionsProvider = new HollowObjectFactoryProvider(typeDataAccess, turboCollectionsTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkRecipeList");
        if(typeDataAccess != null) {
            videoArtWorkRecipeListTypeAPI = new VideoArtWorkRecipeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkRecipeListTypeAPI = new VideoArtWorkRecipeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkRecipeList"));
        }
        addTypeAPI(videoArtWorkRecipeListTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkRecipeList");
        if(factory == null)
            factory = new VideoArtWorkRecipeListHollowFactory();
        if(cachedTypes.contains("VideoArtWorkRecipeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkRecipeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkRecipeListProvider;
            videoArtWorkRecipeListProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkRecipeListTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkRecipeListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkRecipeListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtworkAttribute");
        if(typeDataAccess != null) {
            videoArtworkAttributeTypeAPI = new VideoArtworkAttributeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtworkAttributeTypeAPI = new VideoArtworkAttributeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtworkAttribute"));
        }
        addTypeAPI(videoArtworkAttributeTypeAPI);
        factory = factoryOverrides.get("VideoArtworkAttribute");
        if(factory == null)
            factory = new VideoArtworkAttributeHollowFactory();
        if(cachedTypes.contains("VideoArtworkAttribute")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtworkAttributeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtworkAttributeProvider;
            videoArtworkAttributeProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtworkAttributeTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtworkAttributeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtworkAttributeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtWorkMultiValueAttribute");
        if(typeDataAccess != null) {
            videoArtWorkMultiValueAttributeTypeAPI = new VideoArtWorkMultiValueAttributeTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoArtWorkMultiValueAttributeTypeAPI = new VideoArtWorkMultiValueAttributeTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoArtWorkMultiValueAttribute"));
        }
        addTypeAPI(videoArtWorkMultiValueAttributeTypeAPI);
        factory = factoryOverrides.get("VideoArtWorkMultiValueAttribute");
        if(factory == null)
            factory = new VideoArtWorkMultiValueAttributeHollowFactory();
        if(cachedTypes.contains("VideoArtWorkMultiValueAttribute")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtWorkMultiValueAttributeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtWorkMultiValueAttributeProvider;
            videoArtWorkMultiValueAttributeProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtWorkMultiValueAttributeTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtWorkMultiValueAttributeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtWorkMultiValueAttributeTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAwardMapping");
        if(typeDataAccess != null) {
            videoAwardMappingTypeAPI = new VideoAwardMappingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoAwardMappingTypeAPI = new VideoAwardMappingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoAwardMapping"));
        }
        addTypeAPI(videoAwardMappingTypeAPI);
        factory = factoryOverrides.get("VideoAwardMapping");
        if(factory == null)
            factory = new VideoAwardMappingHollowFactory();
        if(cachedTypes.contains("VideoAwardMapping")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardMappingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardMappingProvider;
            videoAwardMappingProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardMappingTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardMappingProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardMappingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoAwardList");
        if(typeDataAccess != null) {
            videoAwardListTypeAPI = new VideoAwardListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoAwardListTypeAPI = new VideoAwardListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoAwardList"));
        }
        addTypeAPI(videoAwardListTypeAPI);
        factory = factoryOverrides.get("VideoAwardList");
        if(factory == null)
            factory = new VideoAwardListHollowFactory();
        if(cachedTypes.contains("VideoAwardList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoAwardListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoAwardListProvider;
            videoAwardListProvider = new HollowObjectCacheProvider(typeDataAccess, videoAwardListTypeAPI, factory, previousCacheProvider);
        } else {
            videoAwardListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoAwardListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoDateWindowList");
        if(typeDataAccess != null) {
            videoDateWindowListTypeAPI = new VideoDateWindowListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoDateWindowListTypeAPI = new VideoDateWindowListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoDateWindowList"));
        }
        addTypeAPI(videoDateWindowListTypeAPI);
        factory = factoryOverrides.get("VideoDateWindowList");
        if(factory == null)
            factory = new VideoDateWindowListHollowFactory();
        if(cachedTypes.contains("VideoDateWindowList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoDateWindowListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoDateWindowListProvider;
            videoDateWindowListProvider = new HollowObjectCacheProvider(typeDataAccess, videoDateWindowListTypeAPI, factory, previousCacheProvider);
        } else {
            videoDateWindowListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoDateWindowListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralAlias");
        if(typeDataAccess != null) {
            videoGeneralAliasTypeAPI = new VideoGeneralAliasTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralAliasTypeAPI = new VideoGeneralAliasTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralAlias"));
        }
        addTypeAPI(videoGeneralAliasTypeAPI);
        factory = factoryOverrides.get("VideoGeneralAlias");
        if(factory == null)
            factory = new VideoGeneralAliasHollowFactory();
        if(cachedTypes.contains("VideoGeneralAlias")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralAliasProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralAliasProvider;
            videoGeneralAliasProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralAliasTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralAliasProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralAliasTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralAliasList");
        if(typeDataAccess != null) {
            videoGeneralAliasListTypeAPI = new VideoGeneralAliasListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralAliasListTypeAPI = new VideoGeneralAliasListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralAliasList"));
        }
        addTypeAPI(videoGeneralAliasListTypeAPI);
        factory = factoryOverrides.get("VideoGeneralAliasList");
        if(factory == null)
            factory = new VideoGeneralAliasListHollowFactory();
        if(cachedTypes.contains("VideoGeneralAliasList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralAliasListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralAliasListProvider;
            videoGeneralAliasListProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralAliasListTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralAliasListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralAliasListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralEpisodeType");
        if(typeDataAccess != null) {
            videoGeneralEpisodeTypeTypeAPI = new VideoGeneralEpisodeTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralEpisodeTypeTypeAPI = new VideoGeneralEpisodeTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralEpisodeType"));
        }
        addTypeAPI(videoGeneralEpisodeTypeTypeAPI);
        factory = factoryOverrides.get("VideoGeneralEpisodeType");
        if(factory == null)
            factory = new VideoGeneralEpisodeTypeHollowFactory();
        if(cachedTypes.contains("VideoGeneralEpisodeType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralEpisodeTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralEpisodeTypeProvider;
            videoGeneralEpisodeTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralEpisodeTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralEpisodeTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralEpisodeTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralEpisodeTypeList");
        if(typeDataAccess != null) {
            videoGeneralEpisodeTypeListTypeAPI = new VideoGeneralEpisodeTypeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralEpisodeTypeListTypeAPI = new VideoGeneralEpisodeTypeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralEpisodeTypeList"));
        }
        addTypeAPI(videoGeneralEpisodeTypeListTypeAPI);
        factory = factoryOverrides.get("VideoGeneralEpisodeTypeList");
        if(factory == null)
            factory = new VideoGeneralEpisodeTypeListHollowFactory();
        if(cachedTypes.contains("VideoGeneralEpisodeTypeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralEpisodeTypeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralEpisodeTypeListProvider;
            videoGeneralEpisodeTypeListProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralEpisodeTypeListTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralEpisodeTypeListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralEpisodeTypeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralTitleType");
        if(typeDataAccess != null) {
            videoGeneralTitleTypeTypeAPI = new VideoGeneralTitleTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralTitleTypeTypeAPI = new VideoGeneralTitleTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoGeneralTitleType"));
        }
        addTypeAPI(videoGeneralTitleTypeTypeAPI);
        factory = factoryOverrides.get("VideoGeneralTitleType");
        if(factory == null)
            factory = new VideoGeneralTitleTypeHollowFactory();
        if(cachedTypes.contains("VideoGeneralTitleType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralTitleTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralTitleTypeProvider;
            videoGeneralTitleTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralTitleTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralTitleTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralTitleTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoGeneralTitleTypeList");
        if(typeDataAccess != null) {
            videoGeneralTitleTypeListTypeAPI = new VideoGeneralTitleTypeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoGeneralTitleTypeListTypeAPI = new VideoGeneralTitleTypeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoGeneralTitleTypeList"));
        }
        addTypeAPI(videoGeneralTitleTypeListTypeAPI);
        factory = factoryOverrides.get("VideoGeneralTitleTypeList");
        if(factory == null)
            factory = new VideoGeneralTitleTypeListHollowFactory();
        if(cachedTypes.contains("VideoGeneralTitleTypeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoGeneralTitleTypeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoGeneralTitleTypeListProvider;
            videoGeneralTitleTypeListProvider = new HollowObjectCacheProvider(typeDataAccess, videoGeneralTitleTypeListTypeAPI, factory, previousCacheProvider);
        } else {
            videoGeneralTitleTypeListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoGeneralTitleTypeListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoPersonAliasList");
        if(typeDataAccess != null) {
            videoPersonAliasListTypeAPI = new VideoPersonAliasListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoPersonAliasListTypeAPI = new VideoPersonAliasListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoPersonAliasList"));
        }
        addTypeAPI(videoPersonAliasListTypeAPI);
        factory = factoryOverrides.get("VideoPersonAliasList");
        if(factory == null)
            factory = new VideoPersonAliasListHollowFactory();
        if(cachedTypes.contains("VideoPersonAliasList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoPersonAliasListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoPersonAliasListProvider;
            videoPersonAliasListProvider = new HollowObjectCacheProvider(typeDataAccess, videoPersonAliasListTypeAPI, factory, previousCacheProvider);
        } else {
            videoPersonAliasListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoPersonAliasListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoPersonCastList");
        if(typeDataAccess != null) {
            videoPersonCastListTypeAPI = new VideoPersonCastListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoPersonCastListTypeAPI = new VideoPersonCastListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoPersonCastList"));
        }
        addTypeAPI(videoPersonCastListTypeAPI);
        factory = factoryOverrides.get("VideoPersonCastList");
        if(factory == null)
            factory = new VideoPersonCastListHollowFactory();
        if(cachedTypes.contains("VideoPersonCastList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoPersonCastListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoPersonCastListProvider;
            videoPersonCastListProvider = new HollowObjectCacheProvider(typeDataAccess, videoPersonCastListTypeAPI, factory, previousCacheProvider);
        } else {
            videoPersonCastListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoPersonCastListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingAdvisoryId");
        if(typeDataAccess != null) {
            videoRatingAdvisoryIdTypeAPI = new VideoRatingAdvisoryIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRatingAdvisoryIdTypeAPI = new VideoRatingAdvisoryIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRatingAdvisoryId"));
        }
        addTypeAPI(videoRatingAdvisoryIdTypeAPI);
        factory = factoryOverrides.get("VideoRatingAdvisoryId");
        if(factory == null)
            factory = new VideoRatingAdvisoryIdHollowFactory();
        if(cachedTypes.contains("VideoRatingAdvisoryId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingAdvisoryIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingAdvisoryIdProvider;
            videoRatingAdvisoryIdProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingAdvisoryIdTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingAdvisoryIdProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingAdvisoryIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingAdvisoryIdList");
        if(typeDataAccess != null) {
            videoRatingAdvisoryIdListTypeAPI = new VideoRatingAdvisoryIdListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRatingAdvisoryIdListTypeAPI = new VideoRatingAdvisoryIdListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRatingAdvisoryIdList"));
        }
        addTypeAPI(videoRatingAdvisoryIdListTypeAPI);
        factory = factoryOverrides.get("VideoRatingAdvisoryIdList");
        if(factory == null)
            factory = new VideoRatingAdvisoryIdListHollowFactory();
        if(cachedTypes.contains("VideoRatingAdvisoryIdList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingAdvisoryIdListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingAdvisoryIdListProvider;
            videoRatingAdvisoryIdListProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingAdvisoryIdListTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingAdvisoryIdListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingAdvisoryIdListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRatingAdvisories");
        if(typeDataAccess != null) {
            videoRatingAdvisoriesTypeAPI = new VideoRatingAdvisoriesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRatingAdvisoriesTypeAPI = new VideoRatingAdvisoriesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRatingAdvisories"));
        }
        addTypeAPI(videoRatingAdvisoriesTypeAPI);
        factory = factoryOverrides.get("VideoRatingAdvisories");
        if(factory == null)
            factory = new VideoRatingAdvisoriesHollowFactory();
        if(cachedTypes.contains("VideoRatingAdvisories")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRatingAdvisoriesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRatingAdvisoriesProvider;
            videoRatingAdvisoriesProvider = new HollowObjectCacheProvider(typeDataAccess, videoRatingAdvisoriesTypeAPI, factory, previousCacheProvider);
        } else {
            videoRatingAdvisoriesProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRatingAdvisoriesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoCountryRating");
        if(typeDataAccess != null) {
            consolidatedVideoCountryRatingTypeAPI = new ConsolidatedVideoCountryRatingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoCountryRatingTypeAPI = new ConsolidatedVideoCountryRatingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoCountryRating"));
        }
        addTypeAPI(consolidatedVideoCountryRatingTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoCountryRating");
        if(factory == null)
            factory = new ConsolidatedVideoCountryRatingHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoCountryRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoCountryRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoCountryRatingProvider;
            consolidatedVideoCountryRatingProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoCountryRatingTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoCountryRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoCountryRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoCountryRatingList");
        if(typeDataAccess != null) {
            consolidatedVideoCountryRatingListTypeAPI = new ConsolidatedVideoCountryRatingListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoCountryRatingListTypeAPI = new ConsolidatedVideoCountryRatingListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedVideoCountryRatingList"));
        }
        addTypeAPI(consolidatedVideoCountryRatingListTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoCountryRatingList");
        if(factory == null)
            factory = new ConsolidatedVideoCountryRatingListHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoCountryRatingList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoCountryRatingListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoCountryRatingListProvider;
            consolidatedVideoCountryRatingListProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoCountryRatingListTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoCountryRatingListProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoCountryRatingListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRating");
        if(typeDataAccess != null) {
            consolidatedVideoRatingTypeAPI = new ConsolidatedVideoRatingTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingTypeAPI = new ConsolidatedVideoRatingTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ConsolidatedVideoRating"));
        }
        addTypeAPI(consolidatedVideoRatingTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRating");
        if(factory == null)
            factory = new ConsolidatedVideoRatingHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRating")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingProvider;
            consolidatedVideoRatingProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ConsolidatedVideoRatingList");
        if(typeDataAccess != null) {
            consolidatedVideoRatingListTypeAPI = new ConsolidatedVideoRatingListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            consolidatedVideoRatingListTypeAPI = new ConsolidatedVideoRatingListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ConsolidatedVideoRatingList"));
        }
        addTypeAPI(consolidatedVideoRatingListTypeAPI);
        factory = factoryOverrides.get("ConsolidatedVideoRatingList");
        if(factory == null)
            factory = new ConsolidatedVideoRatingListHollowFactory();
        if(cachedTypes.contains("ConsolidatedVideoRatingList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.consolidatedVideoRatingListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.consolidatedVideoRatingListProvider;
            consolidatedVideoRatingListProvider = new HollowObjectCacheProvider(typeDataAccess, consolidatedVideoRatingListTypeAPI, factory, previousCacheProvider);
        } else {
            consolidatedVideoRatingListProvider = new HollowObjectFactoryProvider(typeDataAccess, consolidatedVideoRatingListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsContractAsset");
        if(typeDataAccess != null) {
            videoRightsContractAssetTypeAPI = new VideoRightsContractAssetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsContractAssetTypeAPI = new VideoRightsContractAssetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsContractAsset"));
        }
        addTypeAPI(videoRightsContractAssetTypeAPI);
        factory = factoryOverrides.get("VideoRightsContractAsset");
        if(factory == null)
            factory = new VideoRightsContractAssetHollowFactory();
        if(cachedTypes.contains("VideoRightsContractAsset")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsContractAssetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsContractAssetProvider;
            videoRightsContractAssetProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsContractAssetTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsContractAssetProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsContractAssetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsContractAssetsSet");
        if(typeDataAccess != null) {
            videoRightsContractAssetsSetTypeAPI = new VideoRightsContractAssetsSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            videoRightsContractAssetsSetTypeAPI = new VideoRightsContractAssetsSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "VideoRightsContractAssetsSet"));
        }
        addTypeAPI(videoRightsContractAssetsSetTypeAPI);
        factory = factoryOverrides.get("VideoRightsContractAssetsSet");
        if(factory == null)
            factory = new VideoRightsContractAssetsSetHollowFactory();
        if(cachedTypes.contains("VideoRightsContractAssetsSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsContractAssetsSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsContractAssetsSetProvider;
            videoRightsContractAssetsSetProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsContractAssetsSetTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsContractAssetsSetProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsContractAssetsSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsContractId");
        if(typeDataAccess != null) {
            videoRightsContractIdTypeAPI = new VideoRightsContractIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsContractIdTypeAPI = new VideoRightsContractIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsContractId"));
        }
        addTypeAPI(videoRightsContractIdTypeAPI);
        factory = factoryOverrides.get("VideoRightsContractId");
        if(factory == null)
            factory = new VideoRightsContractIdHollowFactory();
        if(cachedTypes.contains("VideoRightsContractId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsContractIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsContractIdProvider;
            videoRightsContractIdProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsContractIdTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsContractIdProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsContractIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsContractPackage");
        if(typeDataAccess != null) {
            videoRightsContractPackageTypeAPI = new VideoRightsContractPackageTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsContractPackageTypeAPI = new VideoRightsContractPackageTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsContractPackage"));
        }
        addTypeAPI(videoRightsContractPackageTypeAPI);
        factory = factoryOverrides.get("VideoRightsContractPackage");
        if(factory == null)
            factory = new VideoRightsContractPackageHollowFactory();
        if(cachedTypes.contains("VideoRightsContractPackage")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsContractPackageProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsContractPackageProvider;
            videoRightsContractPackageProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsContractPackageTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsContractPackageProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsContractPackageTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsContractPackagesList");
        if(typeDataAccess != null) {
            videoRightsContractPackagesListTypeAPI = new VideoRightsContractPackagesListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsContractPackagesListTypeAPI = new VideoRightsContractPackagesListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsContractPackagesList"));
        }
        addTypeAPI(videoRightsContractPackagesListTypeAPI);
        factory = factoryOverrides.get("VideoRightsContractPackagesList");
        if(factory == null)
            factory = new VideoRightsContractPackagesListHollowFactory();
        if(cachedTypes.contains("VideoRightsContractPackagesList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsContractPackagesListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsContractPackagesListProvider;
            videoRightsContractPackagesListProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsContractPackagesListTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsContractPackagesListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsContractPackagesListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsContract");
        if(typeDataAccess != null) {
            videoRightsContractTypeAPI = new VideoRightsContractTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsContractTypeAPI = new VideoRightsContractTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsContract"));
        }
        addTypeAPI(videoRightsContractTypeAPI);
        factory = factoryOverrides.get("VideoRightsContract");
        if(factory == null)
            factory = new VideoRightsContractHollowFactory();
        if(cachedTypes.contains("VideoRightsContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsContractProvider;
            videoRightsContractProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsContractTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsContractProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsContractSet");
        if(typeDataAccess != null) {
            videoRightsContractSetTypeAPI = new VideoRightsContractSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            videoRightsContractSetTypeAPI = new VideoRightsContractSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "VideoRightsContractSet"));
        }
        addTypeAPI(videoRightsContractSetTypeAPI);
        factory = factoryOverrides.get("VideoRightsContractSet");
        if(factory == null)
            factory = new VideoRightsContractSetHollowFactory();
        if(cachedTypes.contains("VideoRightsContractSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsContractSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsContractSetProvider;
            videoRightsContractSetProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsContractSetTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsContractSetProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsContractSetTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsWindowContractIdList");
        if(typeDataAccess != null) {
            videoRightsWindowContractIdListTypeAPI = new VideoRightsWindowContractIdListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoRightsWindowContractIdListTypeAPI = new VideoRightsWindowContractIdListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoRightsWindowContractIdList"));
        }
        addTypeAPI(videoRightsWindowContractIdListTypeAPI);
        factory = factoryOverrides.get("VideoRightsWindowContractIdList");
        if(factory == null)
            factory = new VideoRightsWindowContractIdListHollowFactory();
        if(cachedTypes.contains("VideoRightsWindowContractIdList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsWindowContractIdListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsWindowContractIdListProvider;
            videoRightsWindowContractIdListProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsWindowContractIdListTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsWindowContractIdListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsWindowContractIdListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsWindow");
        if(typeDataAccess != null) {
            videoRightsWindowTypeAPI = new VideoRightsWindowTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoRightsWindowTypeAPI = new VideoRightsWindowTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoRightsWindow"));
        }
        addTypeAPI(videoRightsWindowTypeAPI);
        factory = factoryOverrides.get("VideoRightsWindow");
        if(factory == null)
            factory = new VideoRightsWindowHollowFactory();
        if(cachedTypes.contains("VideoRightsWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsWindowProvider;
            videoRightsWindowProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsWindowTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoRightsWindowsSet");
        if(typeDataAccess != null) {
            videoRightsWindowsSetTypeAPI = new VideoRightsWindowsSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            videoRightsWindowsSetTypeAPI = new VideoRightsWindowsSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "VideoRightsWindowsSet"));
        }
        addTypeAPI(videoRightsWindowsSetTypeAPI);
        factory = factoryOverrides.get("VideoRightsWindowsSet");
        if(factory == null)
            factory = new VideoRightsWindowsSetHollowFactory();
        if(cachedTypes.contains("VideoRightsWindowsSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoRightsWindowsSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoRightsWindowsSetProvider;
            videoRightsWindowsSetProvider = new HollowObjectCacheProvider(typeDataAccess, videoRightsWindowsSetTypeAPI, factory, previousCacheProvider);
        } else {
            videoRightsWindowsSetProvider = new HollowObjectFactoryProvider(typeDataAccess, videoRightsWindowsSetTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoStreamInfo");
        if(typeDataAccess != null) {
            videoStreamInfoTypeAPI = new VideoStreamInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoStreamInfoTypeAPI = new VideoStreamInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoStreamInfo"));
        }
        addTypeAPI(videoStreamInfoTypeAPI);
        factory = factoryOverrides.get("VideoStreamInfo");
        if(factory == null)
            factory = new VideoStreamInfoHollowFactory();
        if(cachedTypes.contains("VideoStreamInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoStreamInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoStreamInfoProvider;
            videoStreamInfoProvider = new HollowObjectCacheProvider(typeDataAccess, videoStreamInfoTypeAPI, factory, previousCacheProvider);
        } else {
            videoStreamInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, videoStreamInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamNonImageInfo");
        if(typeDataAccess != null) {
            streamNonImageInfoTypeAPI = new StreamNonImageInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamNonImageInfoTypeAPI = new StreamNonImageInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamNonImageInfo"));
        }
        addTypeAPI(streamNonImageInfoTypeAPI);
        factory = factoryOverrides.get("StreamNonImageInfo");
        if(factory == null)
            factory = new StreamNonImageInfoHollowFactory();
        if(cachedTypes.contains("StreamNonImageInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamNonImageInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamNonImageInfoProvider;
            streamNonImageInfoProvider = new HollowObjectCacheProvider(typeDataAccess, streamNonImageInfoTypeAPI, factory, previousCacheProvider);
        } else {
            streamNonImageInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, streamNonImageInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PackageStream");
        if(typeDataAccess != null) {
            packageStreamTypeAPI = new PackageStreamTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            packageStreamTypeAPI = new PackageStreamTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PackageStream"));
        }
        addTypeAPI(packageStreamTypeAPI);
        factory = factoryOverrides.get("PackageStream");
        if(factory == null)
            factory = new PackageStreamHollowFactory();
        if(cachedTypes.contains("PackageStream")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageStreamProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageStreamProvider;
            packageStreamProvider = new HollowObjectCacheProvider(typeDataAccess, packageStreamTypeAPI, factory, previousCacheProvider);
        } else {
            packageStreamProvider = new HollowObjectFactoryProvider(typeDataAccess, packageStreamTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PackageStreamSet");
        if(typeDataAccess != null) {
            packageStreamSetTypeAPI = new PackageStreamSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            packageStreamSetTypeAPI = new PackageStreamSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "PackageStreamSet"));
        }
        addTypeAPI(packageStreamSetTypeAPI);
        factory = factoryOverrides.get("PackageStreamSet");
        if(factory == null)
            factory = new PackageStreamSetHollowFactory();
        if(cachedTypes.contains("PackageStreamSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageStreamSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageStreamSetProvider;
            packageStreamSetProvider = new HollowObjectCacheProvider(typeDataAccess, packageStreamSetTypeAPI, factory, previousCacheProvider);
        } else {
            packageStreamSetProvider = new HollowObjectFactoryProvider(typeDataAccess, packageStreamSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Packages");
        if(typeDataAccess != null) {
            packagesTypeAPI = new PackagesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            packagesTypeAPI = new PackagesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Packages"));
        }
        addTypeAPI(packagesTypeAPI);
        factory = factoryOverrides.get("Packages");
        if(factory == null)
            factory = new PackagesHollowFactory();
        if(cachedTypes.contains("Packages")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packagesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packagesProvider;
            packagesProvider = new HollowObjectCacheProvider(typeDataAccess, packagesTypeAPI, factory, previousCacheProvider);
        } else {
            packagesProvider = new HollowObjectFactoryProvider(typeDataAccess, packagesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeMedia");
        if(typeDataAccess != null) {
            videoTypeMediaTypeAPI = new VideoTypeMediaTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeMediaTypeAPI = new VideoTypeMediaTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoTypeMedia"));
        }
        addTypeAPI(videoTypeMediaTypeAPI);
        factory = factoryOverrides.get("VideoTypeMedia");
        if(factory == null)
            factory = new VideoTypeMediaHollowFactory();
        if(cachedTypes.contains("VideoTypeMedia")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeMediaProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeMediaProvider;
            videoTypeMediaProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeMediaTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeMediaProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeMediaTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeMediaList");
        if(typeDataAccess != null) {
            videoTypeMediaListTypeAPI = new VideoTypeMediaListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoTypeMediaListTypeAPI = new VideoTypeMediaListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoTypeMediaList"));
        }
        addTypeAPI(videoTypeMediaListTypeAPI);
        factory = factoryOverrides.get("VideoTypeMediaList");
        if(factory == null)
            factory = new VideoTypeMediaListHollowFactory();
        if(cachedTypes.contains("VideoTypeMediaList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeMediaListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeMediaListProvider;
            videoTypeMediaListProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeMediaListTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeMediaListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeMediaListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeDescriptor");
        if(typeDataAccess != null) {
            videoTypeDescriptorTypeAPI = new VideoTypeDescriptorTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoTypeDescriptorTypeAPI = new VideoTypeDescriptorTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoTypeDescriptor"));
        }
        addTypeAPI(videoTypeDescriptorTypeAPI);
        factory = factoryOverrides.get("VideoTypeDescriptor");
        if(factory == null)
            factory = new VideoTypeDescriptorHollowFactory();
        if(cachedTypes.contains("VideoTypeDescriptor")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeDescriptorProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeDescriptorProvider;
            videoTypeDescriptorProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeDescriptorTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeDescriptorProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeDescriptorTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeDescriptorList");
        if(typeDataAccess != null) {
            videoTypeDescriptorListTypeAPI = new VideoTypeDescriptorListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            videoTypeDescriptorListTypeAPI = new VideoTypeDescriptorListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "VideoTypeDescriptorList"));
        }
        addTypeAPI(videoTypeDescriptorListTypeAPI);
        factory = factoryOverrides.get("VideoTypeDescriptorList");
        if(factory == null)
            factory = new VideoTypeDescriptorListHollowFactory();
        if(cachedTypes.contains("VideoTypeDescriptorList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeDescriptorListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeDescriptorListProvider;
            videoTypeDescriptorListProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeDescriptorListTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeDescriptorListProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeDescriptorListTypeAPI, factory);
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
        if(characterQuoteProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterQuoteProvider).detach();
        if(characterQuoteListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterQuoteListProvider).detach();
        if(chunkDurationsStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)chunkDurationsStringProvider).detach();
        if(codecPrivateDataStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)codecPrivateDataStringProvider).detach();
        if(dateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)dateProvider).detach();
        if(downloadableIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)downloadableIdProvider).detach();
        if(downloadableIdListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)downloadableIdListProvider).detach();
        if(drmInfoStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmInfoStringProvider).detach();
        if(episodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodeProvider).detach();
        if(episodeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodeListProvider).detach();
        if(iSOCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryProvider).detach();
        if(iSOCountryListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryListProvider).detach();
        if(iSOCountrySetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountrySetProvider).detach();
        if(deployablePackagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)deployablePackagesProvider).detach();
        if(mapKeyProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapKeyProvider).detach();
        if(mapOfFirstDisplayDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfFirstDisplayDatesProvider).detach();
        if(rolloutMapOfLaunchDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutMapOfLaunchDatesProvider).detach();
        if(rolloutPhaseCastProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseCastProvider).detach();
        if(rolloutPhaseCastListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseCastListProvider).detach();
        if(rolloutPhaseCharacterProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseCharacterProvider).detach();
        if(rolloutPhaseCharacterListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseCharacterListProvider).detach();
        if(rolloutPhaseImageIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseImageIdProvider).detach();
        if(rolloutPhaseOldArtworkListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseOldArtworkListProvider).detach();
        if(rolloutPhaseWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseWindowProvider).detach();
        if(rolloutPhaseWindowMapProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseWindowMapProvider).detach();
        if(seasonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)seasonProvider).detach();
        if(seasonListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)seasonListProvider).detach();
        if(streamDimensionsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamDimensionsProvider).detach();
        if(streamFileIdentificationProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamFileIdentificationProvider).detach();
        if(streamProfileIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfileIdProvider).detach();
        if(streamProfileIdListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfileIdListProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(artWorkImageFormatProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artWorkImageFormatProvider).detach();
        if(artWorkImageTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artWorkImageTypeProvider).detach();
        if(artworkRecipeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artworkRecipeProvider).detach();
        if(audioStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)audioStreamInfoProvider).detach();
        if(bcp47CodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)bcp47CodeProvider).detach();
        if(cSMReviewProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cSMReviewProvider).detach();
        if(cacheDeploymentIntentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cacheDeploymentIntentProvider).detach();
        if(cdnDeploymentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cdnDeploymentProvider).detach();
        if(cdnDeploymentSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cdnDeploymentSetProvider).detach();
        if(cdnsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cdnsProvider).detach();
        if(certificationSystemRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemRatingProvider).detach();
        if(certificationSystemRatingListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemRatingListProvider).detach();
        if(certificationSystemProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemProvider).detach();
        if(characterArtworkAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkAttributesProvider).detach();
        if(characterArtworkDerivativeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkDerivativeProvider).detach();
        if(characterArtworkDerivativeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkDerivativeListProvider).detach();
        if(characterElementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterElementsProvider).detach();
        if(characterProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterProvider).detach();
        if(countryVideoDisplaySetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)countryVideoDisplaySetProvider).detach();
        if(countryVideoDisplaySetListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)countryVideoDisplaySetListProvider).detach();
        if(defaultExtensionRecipeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)defaultExtensionRecipeProvider).detach();
        if(disallowedSubtitleLangCodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedSubtitleLangCodeProvider).detach();
        if(disallowedSubtitleLangCodesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedSubtitleLangCodesListProvider).detach();
        if(disallowedAssetBundleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedAssetBundleProvider).detach();
        if(disallowedAssetBundlesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedAssetBundlesListProvider).detach();
        if(drmHeaderInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmHeaderInfoProvider).detach();
        if(drmHeaderInfoListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmHeaderInfoListProvider).detach();
        if(drmSystemIdentifiersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmSystemIdentifiersProvider).detach();
        if(imageStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)imageStreamInfoProvider).detach();
        if(localeTerritoryCodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localeTerritoryCodeProvider).detach();
        if(localeTerritoryCodeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localeTerritoryCodeListProvider).detach();
        if(characterArtworkLocaleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkLocaleProvider).detach();
        if(characterArtworkLocaleListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkLocaleListProvider).detach();
        if(characterArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkProvider).detach();
        if(originServersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)originServersProvider).detach();
        if(packageDrmInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageDrmInfoProvider).detach();
        if(packageDrmInfoListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageDrmInfoListProvider).detach();
        if(packageMomentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageMomentProvider).detach();
        if(packageMomentListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageMomentListProvider).detach();
        if(personArtworkAttributeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkAttributeProvider).detach();
        if(personArtworkDerivativeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkDerivativeProvider).detach();
        if(personArtworkDerivativeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkDerivativeListProvider).detach();
        if(personArtworkLocaleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkLocaleProvider).detach();
        if(personArtworkLocaleListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkLocaleListProvider).detach();
        if(personArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkProvider).detach();
        if(protectionTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)protectionTypesProvider).detach();
        if(rolloutPhaseArtworkSourceFileIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkSourceFileIdProvider).detach();
        if(rolloutPhaseArtworkSourceFileIdListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkSourceFileIdListProvider).detach();
        if(rolloutPhaseLocalizedMetadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseLocalizedMetadataProvider).detach();
        if(rolloutPhaseNewArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseNewArtworkProvider).detach();
        if(rolloutPhaseTrailerSupplementalInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseTrailerSupplementalInfoProvider).detach();
        if(rolloutPhasesElementsTrailerSupplementalInfoMapProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhasesElementsTrailerSupplementalInfoMapProvider).detach();
        if(rolloutPhaseTrailerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseTrailerProvider).detach();
        if(rolloutPhaseTrailerListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseTrailerListProvider).detach();
        if(rolloutPhaseElementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseElementsProvider).detach();
        if(rolloutPhaseProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseProvider).detach();
        if(rolloutPhaseListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseListProvider).detach();
        if(rolloutProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutProvider).detach();
        if(storageGroupsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)storageGroupsProvider).detach();
        if(streamAssetTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamAssetTypeProvider).detach();
        if(streamDeploymentInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamDeploymentInfoProvider).detach();
        if(streamDeploymentLabelProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamDeploymentLabelProvider).detach();
        if(streamDeploymentLabelSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamDeploymentLabelSetProvider).detach();
        if(streamDeploymentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamDeploymentProvider).detach();
        if(streamDrmInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamDrmInfoProvider).detach();
        if(streamProfileGroupsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfileGroupsProvider).detach();
        if(streamProfilesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamProfilesProvider).detach();
        if(territoryCountriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)territoryCountriesProvider).detach();
        if(textStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)textStreamInfoProvider).detach();
        if(topNAttributeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNAttributeProvider).detach();
        if(topNAttributesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNAttributesListProvider).detach();
        if(topNProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNProvider).detach();
        if(trailerThemeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerThemeProvider).detach();
        if(trailerThemeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerThemeListProvider).detach();
        if(individualTrailerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)individualTrailerProvider).detach();
        if(trailersListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailersListProvider).detach();
        if(trailerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)trailerProvider).detach();
        if(translatedTextValueProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)translatedTextValueProvider).detach();
        if(mapOfTranslatedTextProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfTranslatedTextProvider).detach();
        if(altGenresAlternateNamesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresAlternateNamesProvider).detach();
        if(altGenresAlternateNamesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresAlternateNamesListProvider).detach();
        if(localizedCharacterProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedCharacterProvider).detach();
        if(localizedMetadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedMetadataProvider).detach();
        if(storiesSynopsesHookProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)storiesSynopsesHookProvider).detach();
        if(storiesSynopsesHookListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)storiesSynopsesHookListProvider).detach();
        if(translatedTextProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)translatedTextProvider).detach();
        if(altGenresProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)altGenresProvider).detach();
        if(assetMetaDatasProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)assetMetaDatasProvider).detach();
        if(awardsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)awardsProvider).detach();
        if(categoriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoriesProvider).detach();
        if(categoryGroupsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)categoryGroupsProvider).detach();
        if(certificationsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationsProvider).detach();
        if(charactersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)charactersProvider).detach();
        if(consolidatedCertSystemRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertSystemRatingProvider).detach();
        if(consolidatedCertSystemRatingListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertSystemRatingListProvider).detach();
        if(consolidatedCertificationSystemsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedCertificationSystemsProvider).detach();
        if(episodesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)episodesProvider).detach();
        if(festivalsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)festivalsProvider).detach();
        if(languagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)languagesProvider).detach();
        if(movieRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieRatingsProvider).detach();
        if(moviesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)moviesProvider).detach();
        if(personAliasesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personAliasesProvider).detach();
        if(personsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsProvider).detach();
        if(ratingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsProvider).detach();
        if(showMemberTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypesProvider).detach();
        if(stories_SynopsesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stories_SynopsesProvider).detach();
        if(turboCollectionsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)turboCollectionsProvider).detach();
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
        if(videoArtWorkRecipeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkRecipeListProvider).detach();
        if(videoArtWorkSourceAttributesThemesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesThemesProvider).detach();
        if(videoArtworkAttributeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtworkAttributeProvider).detach();
        if(videoArtWorkMultiValueAttributeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkMultiValueAttributeProvider).detach();
        if(videoArtWorkSourceAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkSourceAttributesProvider).detach();
        if(videoArtWorkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtWorkProvider).detach();
        if(videoAwardAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardAwardProvider).detach();
        if(videoAwardArrayOfAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardArrayOfAwardProvider).detach();
        if(videoAwardMappingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardMappingProvider).detach();
        if(videoAwardListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardListProvider).detach();
        if(videoAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoAwardProvider).detach();
        if(videoDateWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateWindowProvider).detach();
        if(videoDateWindowListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateWindowListProvider).detach();
        if(videoDateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDateProvider).detach();
        if(videoDisplaySetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoDisplaySetProvider).detach();
        if(videoGeneralAliasProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralAliasProvider).detach();
        if(videoGeneralAliasListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralAliasListProvider).detach();
        if(videoGeneralEpisodeTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralEpisodeTypeProvider).detach();
        if(videoGeneralEpisodeTypeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralEpisodeTypeListProvider).detach();
        if(videoGeneralTitleTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralTitleTypeProvider).detach();
        if(videoGeneralTitleTypeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralTitleTypeListProvider).detach();
        if(videoGeneralProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoGeneralProvider).detach();
        if(videoPersonAliasProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonAliasProvider).detach();
        if(videoPersonAliasListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonAliasListProvider).detach();
        if(videoPersonCastProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonCastProvider).detach();
        if(videoPersonCastListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonCastListProvider).detach();
        if(videoPersonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoPersonProvider).detach();
        if(videoRatingAdvisoryIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingAdvisoryIdProvider).detach();
        if(videoRatingAdvisoryIdListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingAdvisoryIdListProvider).detach();
        if(videoRatingAdvisoriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRatingAdvisoriesProvider).detach();
        if(consolidatedVideoCountryRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoCountryRatingProvider).detach();
        if(consolidatedVideoCountryRatingListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoCountryRatingListProvider).detach();
        if(consolidatedVideoRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingProvider).detach();
        if(consolidatedVideoRatingListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingListProvider).detach();
        if(consolidatedVideoRatingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)consolidatedVideoRatingsProvider).detach();
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
        if(videoRightsContractAssetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsContractAssetProvider).detach();
        if(videoRightsContractAssetsSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsContractAssetsSetProvider).detach();
        if(videoRightsContractIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsContractIdProvider).detach();
        if(videoRightsContractPackageProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsContractPackageProvider).detach();
        if(videoRightsContractPackagesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsContractPackagesListProvider).detach();
        if(videoRightsContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsContractProvider).detach();
        if(videoRightsContractSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsContractSetProvider).detach();
        if(videoRightsFlagsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsFlagsProvider).detach();
        if(videoRightsWindowContractIdListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsWindowContractIdListProvider).detach();
        if(videoRightsWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsWindowProvider).detach();
        if(videoRightsWindowsSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsWindowsSetProvider).detach();
        if(videoRightsRightsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsRightsProvider).detach();
        if(videoRightsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoRightsProvider).detach();
        if(videoStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoStreamInfoProvider).detach();
        if(streamNonImageInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamNonImageInfoProvider).detach();
        if(packageStreamProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageStreamProvider).detach();
        if(packageStreamSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageStreamSetProvider).detach();
        if(packagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packagesProvider).detach();
        if(videoTypeMediaProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeMediaProvider).detach();
        if(videoTypeMediaListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeMediaListProvider).detach();
        if(videoTypeDescriptorProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeDescriptorProvider).detach();
        if(videoTypeDescriptorListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeDescriptorListProvider).detach();
        if(videoTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeProvider).detach();
    }

    public CharacterQuoteTypeAPI getCharacterQuoteTypeAPI() {
        return characterQuoteTypeAPI;
    }
    public CharacterQuoteListTypeAPI getCharacterQuoteListTypeAPI() {
        return characterQuoteListTypeAPI;
    }
    public ChunkDurationsStringTypeAPI getChunkDurationsStringTypeAPI() {
        return chunkDurationsStringTypeAPI;
    }
    public CodecPrivateDataStringTypeAPI getCodecPrivateDataStringTypeAPI() {
        return codecPrivateDataStringTypeAPI;
    }
    public DateTypeAPI getDateTypeAPI() {
        return dateTypeAPI;
    }
    public DownloadableIdTypeAPI getDownloadableIdTypeAPI() {
        return downloadableIdTypeAPI;
    }
    public DownloadableIdListTypeAPI getDownloadableIdListTypeAPI() {
        return downloadableIdListTypeAPI;
    }
    public DrmInfoStringTypeAPI getDrmInfoStringTypeAPI() {
        return drmInfoStringTypeAPI;
    }
    public EpisodeTypeAPI getEpisodeTypeAPI() {
        return episodeTypeAPI;
    }
    public EpisodeListTypeAPI getEpisodeListTypeAPI() {
        return episodeListTypeAPI;
    }
    public ISOCountryTypeAPI getISOCountryTypeAPI() {
        return iSOCountryTypeAPI;
    }
    public ISOCountryListTypeAPI getISOCountryListTypeAPI() {
        return iSOCountryListTypeAPI;
    }
    public ISOCountrySetTypeAPI getISOCountrySetTypeAPI() {
        return iSOCountrySetTypeAPI;
    }
    public DeployablePackagesTypeAPI getDeployablePackagesTypeAPI() {
        return deployablePackagesTypeAPI;
    }
    public MapKeyTypeAPI getMapKeyTypeAPI() {
        return mapKeyTypeAPI;
    }
    public MapOfFirstDisplayDatesTypeAPI getMapOfFirstDisplayDatesTypeAPI() {
        return mapOfFirstDisplayDatesTypeAPI;
    }
    public RolloutMapOfLaunchDatesTypeAPI getRolloutMapOfLaunchDatesTypeAPI() {
        return rolloutMapOfLaunchDatesTypeAPI;
    }
    public RolloutPhaseCastTypeAPI getRolloutPhaseCastTypeAPI() {
        return rolloutPhaseCastTypeAPI;
    }
    public RolloutPhaseCastListTypeAPI getRolloutPhaseCastListTypeAPI() {
        return rolloutPhaseCastListTypeAPI;
    }
    public RolloutPhaseCharacterTypeAPI getRolloutPhaseCharacterTypeAPI() {
        return rolloutPhaseCharacterTypeAPI;
    }
    public RolloutPhaseCharacterListTypeAPI getRolloutPhaseCharacterListTypeAPI() {
        return rolloutPhaseCharacterListTypeAPI;
    }
    public RolloutPhaseImageIdTypeAPI getRolloutPhaseImageIdTypeAPI() {
        return rolloutPhaseImageIdTypeAPI;
    }
    public RolloutPhaseOldArtworkListTypeAPI getRolloutPhaseOldArtworkListTypeAPI() {
        return rolloutPhaseOldArtworkListTypeAPI;
    }
    public RolloutPhaseWindowTypeAPI getRolloutPhaseWindowTypeAPI() {
        return rolloutPhaseWindowTypeAPI;
    }
    public RolloutPhaseWindowMapTypeAPI getRolloutPhaseWindowMapTypeAPI() {
        return rolloutPhaseWindowMapTypeAPI;
    }
    public SeasonTypeAPI getSeasonTypeAPI() {
        return seasonTypeAPI;
    }
    public SeasonListTypeAPI getSeasonListTypeAPI() {
        return seasonListTypeAPI;
    }
    public StreamDimensionsTypeAPI getStreamDimensionsTypeAPI() {
        return streamDimensionsTypeAPI;
    }
    public StreamFileIdentificationTypeAPI getStreamFileIdentificationTypeAPI() {
        return streamFileIdentificationTypeAPI;
    }
    public StreamProfileIdTypeAPI getStreamProfileIdTypeAPI() {
        return streamProfileIdTypeAPI;
    }
    public StreamProfileIdListTypeAPI getStreamProfileIdListTypeAPI() {
        return streamProfileIdListTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
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
    public AudioStreamInfoTypeAPI getAudioStreamInfoTypeAPI() {
        return audioStreamInfoTypeAPI;
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
    public CdnDeploymentTypeAPI getCdnDeploymentTypeAPI() {
        return cdnDeploymentTypeAPI;
    }
    public CdnDeploymentSetTypeAPI getCdnDeploymentSetTypeAPI() {
        return cdnDeploymentSetTypeAPI;
    }
    public CdnsTypeAPI getCdnsTypeAPI() {
        return cdnsTypeAPI;
    }
    public CertificationSystemRatingTypeAPI getCertificationSystemRatingTypeAPI() {
        return certificationSystemRatingTypeAPI;
    }
    public CertificationSystemRatingListTypeAPI getCertificationSystemRatingListTypeAPI() {
        return certificationSystemRatingListTypeAPI;
    }
    public CertificationSystemTypeAPI getCertificationSystemTypeAPI() {
        return certificationSystemTypeAPI;
    }
    public CharacterArtworkAttributesTypeAPI getCharacterArtworkAttributesTypeAPI() {
        return characterArtworkAttributesTypeAPI;
    }
    public CharacterArtworkDerivativeTypeAPI getCharacterArtworkDerivativeTypeAPI() {
        return characterArtworkDerivativeTypeAPI;
    }
    public CharacterArtworkDerivativeListTypeAPI getCharacterArtworkDerivativeListTypeAPI() {
        return characterArtworkDerivativeListTypeAPI;
    }
    public CharacterElementsTypeAPI getCharacterElementsTypeAPI() {
        return characterElementsTypeAPI;
    }
    public CharacterTypeAPI getCharacterTypeAPI() {
        return characterTypeAPI;
    }
    public CountryVideoDisplaySetTypeAPI getCountryVideoDisplaySetTypeAPI() {
        return countryVideoDisplaySetTypeAPI;
    }
    public CountryVideoDisplaySetListTypeAPI getCountryVideoDisplaySetListTypeAPI() {
        return countryVideoDisplaySetListTypeAPI;
    }
    public DefaultExtensionRecipeTypeAPI getDefaultExtensionRecipeTypeAPI() {
        return defaultExtensionRecipeTypeAPI;
    }
    public DisallowedSubtitleLangCodeTypeAPI getDisallowedSubtitleLangCodeTypeAPI() {
        return disallowedSubtitleLangCodeTypeAPI;
    }
    public DisallowedSubtitleLangCodesListTypeAPI getDisallowedSubtitleLangCodesListTypeAPI() {
        return disallowedSubtitleLangCodesListTypeAPI;
    }
    public DisallowedAssetBundleTypeAPI getDisallowedAssetBundleTypeAPI() {
        return disallowedAssetBundleTypeAPI;
    }
    public DisallowedAssetBundlesListTypeAPI getDisallowedAssetBundlesListTypeAPI() {
        return disallowedAssetBundlesListTypeAPI;
    }
    public DrmHeaderInfoTypeAPI getDrmHeaderInfoTypeAPI() {
        return drmHeaderInfoTypeAPI;
    }
    public DrmHeaderInfoListTypeAPI getDrmHeaderInfoListTypeAPI() {
        return drmHeaderInfoListTypeAPI;
    }
    public DrmSystemIdentifiersTypeAPI getDrmSystemIdentifiersTypeAPI() {
        return drmSystemIdentifiersTypeAPI;
    }
    public ImageStreamInfoTypeAPI getImageStreamInfoTypeAPI() {
        return imageStreamInfoTypeAPI;
    }
    public LocaleTerritoryCodeTypeAPI getLocaleTerritoryCodeTypeAPI() {
        return localeTerritoryCodeTypeAPI;
    }
    public LocaleTerritoryCodeListTypeAPI getLocaleTerritoryCodeListTypeAPI() {
        return localeTerritoryCodeListTypeAPI;
    }
    public CharacterArtworkLocaleTypeAPI getCharacterArtworkLocaleTypeAPI() {
        return characterArtworkLocaleTypeAPI;
    }
    public CharacterArtworkLocaleListTypeAPI getCharacterArtworkLocaleListTypeAPI() {
        return characterArtworkLocaleListTypeAPI;
    }
    public CharacterArtworkTypeAPI getCharacterArtworkTypeAPI() {
        return characterArtworkTypeAPI;
    }
    public OriginServersTypeAPI getOriginServersTypeAPI() {
        return originServersTypeAPI;
    }
    public PackageDrmInfoTypeAPI getPackageDrmInfoTypeAPI() {
        return packageDrmInfoTypeAPI;
    }
    public PackageDrmInfoListTypeAPI getPackageDrmInfoListTypeAPI() {
        return packageDrmInfoListTypeAPI;
    }
    public PackageMomentTypeAPI getPackageMomentTypeAPI() {
        return packageMomentTypeAPI;
    }
    public PackageMomentListTypeAPI getPackageMomentListTypeAPI() {
        return packageMomentListTypeAPI;
    }
    public PersonArtworkAttributeTypeAPI getPersonArtworkAttributeTypeAPI() {
        return personArtworkAttributeTypeAPI;
    }
    public PersonArtworkDerivativeTypeAPI getPersonArtworkDerivativeTypeAPI() {
        return personArtworkDerivativeTypeAPI;
    }
    public PersonArtworkDerivativeListTypeAPI getPersonArtworkDerivativeListTypeAPI() {
        return personArtworkDerivativeListTypeAPI;
    }
    public PersonArtworkLocaleTypeAPI getPersonArtworkLocaleTypeAPI() {
        return personArtworkLocaleTypeAPI;
    }
    public PersonArtworkLocaleListTypeAPI getPersonArtworkLocaleListTypeAPI() {
        return personArtworkLocaleListTypeAPI;
    }
    public PersonArtworkTypeAPI getPersonArtworkTypeAPI() {
        return personArtworkTypeAPI;
    }
    public ProtectionTypesTypeAPI getProtectionTypesTypeAPI() {
        return protectionTypesTypeAPI;
    }
    public RolloutPhaseArtworkSourceFileIdTypeAPI getRolloutPhaseArtworkSourceFileIdTypeAPI() {
        return rolloutPhaseArtworkSourceFileIdTypeAPI;
    }
    public RolloutPhaseArtworkSourceFileIdListTypeAPI getRolloutPhaseArtworkSourceFileIdListTypeAPI() {
        return rolloutPhaseArtworkSourceFileIdListTypeAPI;
    }
    public RolloutPhaseLocalizedMetadataTypeAPI getRolloutPhaseLocalizedMetadataTypeAPI() {
        return rolloutPhaseLocalizedMetadataTypeAPI;
    }
    public RolloutPhaseNewArtworkTypeAPI getRolloutPhaseNewArtworkTypeAPI() {
        return rolloutPhaseNewArtworkTypeAPI;
    }
    public RolloutPhaseTrailerSupplementalInfoTypeAPI getRolloutPhaseTrailerSupplementalInfoTypeAPI() {
        return rolloutPhaseTrailerSupplementalInfoTypeAPI;
    }
    public RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI getRolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI() {
        return rolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI;
    }
    public RolloutPhaseTrailerTypeAPI getRolloutPhaseTrailerTypeAPI() {
        return rolloutPhaseTrailerTypeAPI;
    }
    public RolloutPhaseTrailerListTypeAPI getRolloutPhaseTrailerListTypeAPI() {
        return rolloutPhaseTrailerListTypeAPI;
    }
    public RolloutPhaseElementsTypeAPI getRolloutPhaseElementsTypeAPI() {
        return rolloutPhaseElementsTypeAPI;
    }
    public RolloutPhaseTypeAPI getRolloutPhaseTypeAPI() {
        return rolloutPhaseTypeAPI;
    }
    public RolloutPhaseListTypeAPI getRolloutPhaseListTypeAPI() {
        return rolloutPhaseListTypeAPI;
    }
    public RolloutTypeAPI getRolloutTypeAPI() {
        return rolloutTypeAPI;
    }
    public StorageGroupsTypeAPI getStorageGroupsTypeAPI() {
        return storageGroupsTypeAPI;
    }
    public StreamAssetTypeTypeAPI getStreamAssetTypeTypeAPI() {
        return streamAssetTypeTypeAPI;
    }
    public StreamDeploymentInfoTypeAPI getStreamDeploymentInfoTypeAPI() {
        return streamDeploymentInfoTypeAPI;
    }
    public StreamDeploymentLabelTypeAPI getStreamDeploymentLabelTypeAPI() {
        return streamDeploymentLabelTypeAPI;
    }
    public StreamDeploymentLabelSetTypeAPI getStreamDeploymentLabelSetTypeAPI() {
        return streamDeploymentLabelSetTypeAPI;
    }
    public StreamDeploymentTypeAPI getStreamDeploymentTypeAPI() {
        return streamDeploymentTypeAPI;
    }
    public StreamDrmInfoTypeAPI getStreamDrmInfoTypeAPI() {
        return streamDrmInfoTypeAPI;
    }
    public StreamProfileGroupsTypeAPI getStreamProfileGroupsTypeAPI() {
        return streamProfileGroupsTypeAPI;
    }
    public StreamProfilesTypeAPI getStreamProfilesTypeAPI() {
        return streamProfilesTypeAPI;
    }
    public TerritoryCountriesTypeAPI getTerritoryCountriesTypeAPI() {
        return territoryCountriesTypeAPI;
    }
    public TextStreamInfoTypeAPI getTextStreamInfoTypeAPI() {
        return textStreamInfoTypeAPI;
    }
    public TopNAttributeTypeAPI getTopNAttributeTypeAPI() {
        return topNAttributeTypeAPI;
    }
    public TopNAttributesListTypeAPI getTopNAttributesListTypeAPI() {
        return topNAttributesListTypeAPI;
    }
    public TopNTypeAPI getTopNTypeAPI() {
        return topNTypeAPI;
    }
    public TrailerThemeTypeAPI getTrailerThemeTypeAPI() {
        return trailerThemeTypeAPI;
    }
    public TrailerThemeListTypeAPI getTrailerThemeListTypeAPI() {
        return trailerThemeListTypeAPI;
    }
    public IndividualTrailerTypeAPI getIndividualTrailerTypeAPI() {
        return individualTrailerTypeAPI;
    }
    public TrailersListTypeAPI getTrailersListTypeAPI() {
        return trailersListTypeAPI;
    }
    public TrailerTypeAPI getTrailerTypeAPI() {
        return trailerTypeAPI;
    }
    public TranslatedTextValueTypeAPI getTranslatedTextValueTypeAPI() {
        return translatedTextValueTypeAPI;
    }
    public MapOfTranslatedTextTypeAPI getMapOfTranslatedTextTypeAPI() {
        return mapOfTranslatedTextTypeAPI;
    }
    public AltGenresAlternateNamesTypeAPI getAltGenresAlternateNamesTypeAPI() {
        return altGenresAlternateNamesTypeAPI;
    }
    public AltGenresAlternateNamesListTypeAPI getAltGenresAlternateNamesListTypeAPI() {
        return altGenresAlternateNamesListTypeAPI;
    }
    public LocalizedCharacterTypeAPI getLocalizedCharacterTypeAPI() {
        return localizedCharacterTypeAPI;
    }
    public LocalizedMetadataTypeAPI getLocalizedMetadataTypeAPI() {
        return localizedMetadataTypeAPI;
    }
    public StoriesSynopsesHookTypeAPI getStoriesSynopsesHookTypeAPI() {
        return storiesSynopsesHookTypeAPI;
    }
    public StoriesSynopsesHookListTypeAPI getStoriesSynopsesHookListTypeAPI() {
        return storiesSynopsesHookListTypeAPI;
    }
    public TranslatedTextTypeAPI getTranslatedTextTypeAPI() {
        return translatedTextTypeAPI;
    }
    public AltGenresTypeAPI getAltGenresTypeAPI() {
        return altGenresTypeAPI;
    }
    public AssetMetaDatasTypeAPI getAssetMetaDatasTypeAPI() {
        return assetMetaDatasTypeAPI;
    }
    public AwardsTypeAPI getAwardsTypeAPI() {
        return awardsTypeAPI;
    }
    public CategoriesTypeAPI getCategoriesTypeAPI() {
        return categoriesTypeAPI;
    }
    public CategoryGroupsTypeAPI getCategoryGroupsTypeAPI() {
        return categoryGroupsTypeAPI;
    }
    public CertificationsTypeAPI getCertificationsTypeAPI() {
        return certificationsTypeAPI;
    }
    public CharactersTypeAPI getCharactersTypeAPI() {
        return charactersTypeAPI;
    }
    public ConsolidatedCertSystemRatingTypeAPI getConsolidatedCertSystemRatingTypeAPI() {
        return consolidatedCertSystemRatingTypeAPI;
    }
    public ConsolidatedCertSystemRatingListTypeAPI getConsolidatedCertSystemRatingListTypeAPI() {
        return consolidatedCertSystemRatingListTypeAPI;
    }
    public ConsolidatedCertificationSystemsTypeAPI getConsolidatedCertificationSystemsTypeAPI() {
        return consolidatedCertificationSystemsTypeAPI;
    }
    public EpisodesTypeAPI getEpisodesTypeAPI() {
        return episodesTypeAPI;
    }
    public FestivalsTypeAPI getFestivalsTypeAPI() {
        return festivalsTypeAPI;
    }
    public LanguagesTypeAPI getLanguagesTypeAPI() {
        return languagesTypeAPI;
    }
    public MovieRatingsTypeAPI getMovieRatingsTypeAPI() {
        return movieRatingsTypeAPI;
    }
    public MoviesTypeAPI getMoviesTypeAPI() {
        return moviesTypeAPI;
    }
    public PersonAliasesTypeAPI getPersonAliasesTypeAPI() {
        return personAliasesTypeAPI;
    }
    public PersonsTypeAPI getPersonsTypeAPI() {
        return personsTypeAPI;
    }
    public RatingsTypeAPI getRatingsTypeAPI() {
        return ratingsTypeAPI;
    }
    public ShowMemberTypesTypeAPI getShowMemberTypesTypeAPI() {
        return showMemberTypesTypeAPI;
    }
    public Stories_SynopsesTypeAPI getStories_SynopsesTypeAPI() {
        return stories_SynopsesTypeAPI;
    }
    public TurboCollectionsTypeAPI getTurboCollectionsTypeAPI() {
        return turboCollectionsTypeAPI;
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
    public VideoArtWorkRecipeListTypeAPI getVideoArtWorkRecipeListTypeAPI() {
        return videoArtWorkRecipeListTypeAPI;
    }
    public VideoArtWorkSourceAttributesThemesTypeAPI getVideoArtWorkSourceAttributesThemesTypeAPI() {
        return videoArtWorkSourceAttributesThemesTypeAPI;
    }
    public VideoArtworkAttributeTypeAPI getVideoArtworkAttributeTypeAPI() {
        return videoArtworkAttributeTypeAPI;
    }
    public VideoArtWorkMultiValueAttributeTypeAPI getVideoArtWorkMultiValueAttributeTypeAPI() {
        return videoArtWorkMultiValueAttributeTypeAPI;
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
    public VideoAwardMappingTypeAPI getVideoAwardMappingTypeAPI() {
        return videoAwardMappingTypeAPI;
    }
    public VideoAwardListTypeAPI getVideoAwardListTypeAPI() {
        return videoAwardListTypeAPI;
    }
    public VideoAwardTypeAPI getVideoAwardTypeAPI() {
        return videoAwardTypeAPI;
    }
    public VideoDateWindowTypeAPI getVideoDateWindowTypeAPI() {
        return videoDateWindowTypeAPI;
    }
    public VideoDateWindowListTypeAPI getVideoDateWindowListTypeAPI() {
        return videoDateWindowListTypeAPI;
    }
    public VideoDateTypeAPI getVideoDateTypeAPI() {
        return videoDateTypeAPI;
    }
    public VideoDisplaySetTypeAPI getVideoDisplaySetTypeAPI() {
        return videoDisplaySetTypeAPI;
    }
    public VideoGeneralAliasTypeAPI getVideoGeneralAliasTypeAPI() {
        return videoGeneralAliasTypeAPI;
    }
    public VideoGeneralAliasListTypeAPI getVideoGeneralAliasListTypeAPI() {
        return videoGeneralAliasListTypeAPI;
    }
    public VideoGeneralEpisodeTypeTypeAPI getVideoGeneralEpisodeTypeTypeAPI() {
        return videoGeneralEpisodeTypeTypeAPI;
    }
    public VideoGeneralEpisodeTypeListTypeAPI getVideoGeneralEpisodeTypeListTypeAPI() {
        return videoGeneralEpisodeTypeListTypeAPI;
    }
    public VideoGeneralTitleTypeTypeAPI getVideoGeneralTitleTypeTypeAPI() {
        return videoGeneralTitleTypeTypeAPI;
    }
    public VideoGeneralTitleTypeListTypeAPI getVideoGeneralTitleTypeListTypeAPI() {
        return videoGeneralTitleTypeListTypeAPI;
    }
    public VideoGeneralTypeAPI getVideoGeneralTypeAPI() {
        return videoGeneralTypeAPI;
    }
    public VideoPersonAliasTypeAPI getVideoPersonAliasTypeAPI() {
        return videoPersonAliasTypeAPI;
    }
    public VideoPersonAliasListTypeAPI getVideoPersonAliasListTypeAPI() {
        return videoPersonAliasListTypeAPI;
    }
    public VideoPersonCastTypeAPI getVideoPersonCastTypeAPI() {
        return videoPersonCastTypeAPI;
    }
    public VideoPersonCastListTypeAPI getVideoPersonCastListTypeAPI() {
        return videoPersonCastListTypeAPI;
    }
    public VideoPersonTypeAPI getVideoPersonTypeAPI() {
        return videoPersonTypeAPI;
    }
    public VideoRatingAdvisoryIdTypeAPI getVideoRatingAdvisoryIdTypeAPI() {
        return videoRatingAdvisoryIdTypeAPI;
    }
    public VideoRatingAdvisoryIdListTypeAPI getVideoRatingAdvisoryIdListTypeAPI() {
        return videoRatingAdvisoryIdListTypeAPI;
    }
    public VideoRatingAdvisoriesTypeAPI getVideoRatingAdvisoriesTypeAPI() {
        return videoRatingAdvisoriesTypeAPI;
    }
    public ConsolidatedVideoCountryRatingTypeAPI getConsolidatedVideoCountryRatingTypeAPI() {
        return consolidatedVideoCountryRatingTypeAPI;
    }
    public ConsolidatedVideoCountryRatingListTypeAPI getConsolidatedVideoCountryRatingListTypeAPI() {
        return consolidatedVideoCountryRatingListTypeAPI;
    }
    public ConsolidatedVideoRatingTypeAPI getConsolidatedVideoRatingTypeAPI() {
        return consolidatedVideoRatingTypeAPI;
    }
    public ConsolidatedVideoRatingListTypeAPI getConsolidatedVideoRatingListTypeAPI() {
        return consolidatedVideoRatingListTypeAPI;
    }
    public ConsolidatedVideoRatingsTypeAPI getConsolidatedVideoRatingsTypeAPI() {
        return consolidatedVideoRatingsTypeAPI;
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
    public VideoRightsContractAssetTypeAPI getVideoRightsContractAssetTypeAPI() {
        return videoRightsContractAssetTypeAPI;
    }
    public VideoRightsContractAssetsSetTypeAPI getVideoRightsContractAssetsSetTypeAPI() {
        return videoRightsContractAssetsSetTypeAPI;
    }
    public VideoRightsContractIdTypeAPI getVideoRightsContractIdTypeAPI() {
        return videoRightsContractIdTypeAPI;
    }
    public VideoRightsContractPackageTypeAPI getVideoRightsContractPackageTypeAPI() {
        return videoRightsContractPackageTypeAPI;
    }
    public VideoRightsContractPackagesListTypeAPI getVideoRightsContractPackagesListTypeAPI() {
        return videoRightsContractPackagesListTypeAPI;
    }
    public VideoRightsContractTypeAPI getVideoRightsContractTypeAPI() {
        return videoRightsContractTypeAPI;
    }
    public VideoRightsContractSetTypeAPI getVideoRightsContractSetTypeAPI() {
        return videoRightsContractSetTypeAPI;
    }
    public VideoRightsFlagsTypeAPI getVideoRightsFlagsTypeAPI() {
        return videoRightsFlagsTypeAPI;
    }
    public VideoRightsWindowContractIdListTypeAPI getVideoRightsWindowContractIdListTypeAPI() {
        return videoRightsWindowContractIdListTypeAPI;
    }
    public VideoRightsWindowTypeAPI getVideoRightsWindowTypeAPI() {
        return videoRightsWindowTypeAPI;
    }
    public VideoRightsWindowsSetTypeAPI getVideoRightsWindowsSetTypeAPI() {
        return videoRightsWindowsSetTypeAPI;
    }
    public VideoRightsRightsTypeAPI getVideoRightsRightsTypeAPI() {
        return videoRightsRightsTypeAPI;
    }
    public VideoRightsTypeAPI getVideoRightsTypeAPI() {
        return videoRightsTypeAPI;
    }
    public VideoStreamInfoTypeAPI getVideoStreamInfoTypeAPI() {
        return videoStreamInfoTypeAPI;
    }
    public StreamNonImageInfoTypeAPI getStreamNonImageInfoTypeAPI() {
        return streamNonImageInfoTypeAPI;
    }
    public PackageStreamTypeAPI getPackageStreamTypeAPI() {
        return packageStreamTypeAPI;
    }
    public PackageStreamSetTypeAPI getPackageStreamSetTypeAPI() {
        return packageStreamSetTypeAPI;
    }
    public PackagesTypeAPI getPackagesTypeAPI() {
        return packagesTypeAPI;
    }
    public VideoTypeMediaTypeAPI getVideoTypeMediaTypeAPI() {
        return videoTypeMediaTypeAPI;
    }
    public VideoTypeMediaListTypeAPI getVideoTypeMediaListTypeAPI() {
        return videoTypeMediaListTypeAPI;
    }
    public VideoTypeDescriptorTypeAPI getVideoTypeDescriptorTypeAPI() {
        return videoTypeDescriptorTypeAPI;
    }
    public VideoTypeDescriptorListTypeAPI getVideoTypeDescriptorListTypeAPI() {
        return videoTypeDescriptorListTypeAPI;
    }
    public VideoTypeTypeAPI getVideoTypeTypeAPI() {
        return videoTypeTypeAPI;
    }
    public Collection<CharacterQuoteHollow> getAllCharacterQuoteHollow() {
        return new AllHollowRecordCollection<CharacterQuoteHollow>(getDataAccess().getTypeDataAccess("CharacterQuote").getTypeState()) {
            protected CharacterQuoteHollow getForOrdinal(int ordinal) {
                return getCharacterQuoteHollow(ordinal);
            }
        };
    }
    public CharacterQuoteHollow getCharacterQuoteHollow(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (CharacterQuoteHollow)characterQuoteProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterQuoteListHollow> getAllCharacterQuoteListHollow() {
        return new AllHollowRecordCollection<CharacterQuoteListHollow>(getDataAccess().getTypeDataAccess("CharacterQuoteList").getTypeState()) {
            protected CharacterQuoteListHollow getForOrdinal(int ordinal) {
                return getCharacterQuoteListHollow(ordinal);
            }
        };
    }
    public CharacterQuoteListHollow getCharacterQuoteListHollow(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (CharacterQuoteListHollow)characterQuoteListProvider.getHollowObject(ordinal);
    }
    public Collection<ChunkDurationsStringHollow> getAllChunkDurationsStringHollow() {
        return new AllHollowRecordCollection<ChunkDurationsStringHollow>(getDataAccess().getTypeDataAccess("ChunkDurationsString").getTypeState()) {
            protected ChunkDurationsStringHollow getForOrdinal(int ordinal) {
                return getChunkDurationsStringHollow(ordinal);
            }
        };
    }
    public ChunkDurationsStringHollow getChunkDurationsStringHollow(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (ChunkDurationsStringHollow)chunkDurationsStringProvider.getHollowObject(ordinal);
    }
    public Collection<CodecPrivateDataStringHollow> getAllCodecPrivateDataStringHollow() {
        return new AllHollowRecordCollection<CodecPrivateDataStringHollow>(getDataAccess().getTypeDataAccess("CodecPrivateDataString").getTypeState()) {
            protected CodecPrivateDataStringHollow getForOrdinal(int ordinal) {
                return getCodecPrivateDataStringHollow(ordinal);
            }
        };
    }
    public CodecPrivateDataStringHollow getCodecPrivateDataStringHollow(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (CodecPrivateDataStringHollow)codecPrivateDataStringProvider.getHollowObject(ordinal);
    }
    public Collection<DateHollow> getAllDateHollow() {
        return new AllHollowRecordCollection<DateHollow>(getDataAccess().getTypeDataAccess("Date").getTypeState()) {
            protected DateHollow getForOrdinal(int ordinal) {
                return getDateHollow(ordinal);
            }
        };
    }
    public DateHollow getDateHollow(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (DateHollow)dateProvider.getHollowObject(ordinal);
    }
    public Collection<DownloadableIdHollow> getAllDownloadableIdHollow() {
        return new AllHollowRecordCollection<DownloadableIdHollow>(getDataAccess().getTypeDataAccess("DownloadableId").getTypeState()) {
            protected DownloadableIdHollow getForOrdinal(int ordinal) {
                return getDownloadableIdHollow(ordinal);
            }
        };
    }
    public DownloadableIdHollow getDownloadableIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (DownloadableIdHollow)downloadableIdProvider.getHollowObject(ordinal);
    }
    public Collection<DownloadableIdListHollow> getAllDownloadableIdListHollow() {
        return new AllHollowRecordCollection<DownloadableIdListHollow>(getDataAccess().getTypeDataAccess("DownloadableIdList").getTypeState()) {
            protected DownloadableIdListHollow getForOrdinal(int ordinal) {
                return getDownloadableIdListHollow(ordinal);
            }
        };
    }
    public DownloadableIdListHollow getDownloadableIdListHollow(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (DownloadableIdListHollow)downloadableIdListProvider.getHollowObject(ordinal);
    }
    public Collection<DrmInfoStringHollow> getAllDrmInfoStringHollow() {
        return new AllHollowRecordCollection<DrmInfoStringHollow>(getDataAccess().getTypeDataAccess("DrmInfoString").getTypeState()) {
            protected DrmInfoStringHollow getForOrdinal(int ordinal) {
                return getDrmInfoStringHollow(ordinal);
            }
        };
    }
    public DrmInfoStringHollow getDrmInfoStringHollow(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (DrmInfoStringHollow)drmInfoStringProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodeHollow> getAllEpisodeHollow() {
        return new AllHollowRecordCollection<EpisodeHollow>(getDataAccess().getTypeDataAccess("Episode").getTypeState()) {
            protected EpisodeHollow getForOrdinal(int ordinal) {
                return getEpisodeHollow(ordinal);
            }
        };
    }
    public EpisodeHollow getEpisodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(8);
        return (EpisodeHollow)episodeProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodeListHollow> getAllEpisodeListHollow() {
        return new AllHollowRecordCollection<EpisodeListHollow>(getDataAccess().getTypeDataAccess("EpisodeList").getTypeState()) {
            protected EpisodeListHollow getForOrdinal(int ordinal) {
                return getEpisodeListHollow(ordinal);
            }
        };
    }
    public EpisodeListHollow getEpisodeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(9);
        return (EpisodeListHollow)episodeListProvider.getHollowObject(ordinal);
    }
    public Collection<ISOCountryHollow> getAllISOCountryHollow() {
        return new AllHollowRecordCollection<ISOCountryHollow>(getDataAccess().getTypeDataAccess("ISOCountry").getTypeState()) {
            protected ISOCountryHollow getForOrdinal(int ordinal) {
                return getISOCountryHollow(ordinal);
            }
        };
    }
    public ISOCountryHollow getISOCountryHollow(int ordinal) {
        objectCreationSampler.recordCreation(10);
        return (ISOCountryHollow)iSOCountryProvider.getHollowObject(ordinal);
    }
    public Collection<ISOCountryListHollow> getAllISOCountryListHollow() {
        return new AllHollowRecordCollection<ISOCountryListHollow>(getDataAccess().getTypeDataAccess("ISOCountryList").getTypeState()) {
            protected ISOCountryListHollow getForOrdinal(int ordinal) {
                return getISOCountryListHollow(ordinal);
            }
        };
    }
    public ISOCountryListHollow getISOCountryListHollow(int ordinal) {
        objectCreationSampler.recordCreation(11);
        return (ISOCountryListHollow)iSOCountryListProvider.getHollowObject(ordinal);
    }
    public Collection<ISOCountrySetHollow> getAllISOCountrySetHollow() {
        return new AllHollowRecordCollection<ISOCountrySetHollow>(getDataAccess().getTypeDataAccess("ISOCountrySet").getTypeState()) {
            protected ISOCountrySetHollow getForOrdinal(int ordinal) {
                return getISOCountrySetHollow(ordinal);
            }
        };
    }
    public ISOCountrySetHollow getISOCountrySetHollow(int ordinal) {
        objectCreationSampler.recordCreation(12);
        return (ISOCountrySetHollow)iSOCountrySetProvider.getHollowObject(ordinal);
    }
    public Collection<DeployablePackagesHollow> getAllDeployablePackagesHollow() {
        return new AllHollowRecordCollection<DeployablePackagesHollow>(getDataAccess().getTypeDataAccess("DeployablePackages").getTypeState()) {
            protected DeployablePackagesHollow getForOrdinal(int ordinal) {
                return getDeployablePackagesHollow(ordinal);
            }
        };
    }
    public DeployablePackagesHollow getDeployablePackagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(13);
        return (DeployablePackagesHollow)deployablePackagesProvider.getHollowObject(ordinal);
    }
    public Collection<MapKeyHollow> getAllMapKeyHollow() {
        return new AllHollowRecordCollection<MapKeyHollow>(getDataAccess().getTypeDataAccess("MapKey").getTypeState()) {
            protected MapKeyHollow getForOrdinal(int ordinal) {
                return getMapKeyHollow(ordinal);
            }
        };
    }
    public MapKeyHollow getMapKeyHollow(int ordinal) {
        objectCreationSampler.recordCreation(14);
        return (MapKeyHollow)mapKeyProvider.getHollowObject(ordinal);
    }
    public Collection<MapOfFirstDisplayDatesHollow> getAllMapOfFirstDisplayDatesHollow() {
        return new AllHollowRecordCollection<MapOfFirstDisplayDatesHollow>(getDataAccess().getTypeDataAccess("MapOfFirstDisplayDates").getTypeState()) {
            protected MapOfFirstDisplayDatesHollow getForOrdinal(int ordinal) {
                return getMapOfFirstDisplayDatesHollow(ordinal);
            }
        };
    }
    public MapOfFirstDisplayDatesHollow getMapOfFirstDisplayDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(15);
        return (MapOfFirstDisplayDatesHollow)mapOfFirstDisplayDatesProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutMapOfLaunchDatesHollow> getAllRolloutMapOfLaunchDatesHollow() {
        return new AllHollowRecordCollection<RolloutMapOfLaunchDatesHollow>(getDataAccess().getTypeDataAccess("RolloutMapOfLaunchDates").getTypeState()) {
            protected RolloutMapOfLaunchDatesHollow getForOrdinal(int ordinal) {
                return getRolloutMapOfLaunchDatesHollow(ordinal);
            }
        };
    }
    public RolloutMapOfLaunchDatesHollow getRolloutMapOfLaunchDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(16);
        return (RolloutMapOfLaunchDatesHollow)rolloutMapOfLaunchDatesProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseCastHollow> getAllRolloutPhaseCastHollow() {
        return new AllHollowRecordCollection<RolloutPhaseCastHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseCast").getTypeState()) {
            protected RolloutPhaseCastHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseCastHollow(ordinal);
            }
        };
    }
    public RolloutPhaseCastHollow getRolloutPhaseCastHollow(int ordinal) {
        objectCreationSampler.recordCreation(17);
        return (RolloutPhaseCastHollow)rolloutPhaseCastProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseCastListHollow> getAllRolloutPhaseCastListHollow() {
        return new AllHollowRecordCollection<RolloutPhaseCastListHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseCastList").getTypeState()) {
            protected RolloutPhaseCastListHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseCastListHollow(ordinal);
            }
        };
    }
    public RolloutPhaseCastListHollow getRolloutPhaseCastListHollow(int ordinal) {
        objectCreationSampler.recordCreation(18);
        return (RolloutPhaseCastListHollow)rolloutPhaseCastListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseCharacterHollow> getAllRolloutPhaseCharacterHollow() {
        return new AllHollowRecordCollection<RolloutPhaseCharacterHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseCharacter").getTypeState()) {
            protected RolloutPhaseCharacterHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseCharacterHollow(ordinal);
            }
        };
    }
    public RolloutPhaseCharacterHollow getRolloutPhaseCharacterHollow(int ordinal) {
        objectCreationSampler.recordCreation(19);
        return (RolloutPhaseCharacterHollow)rolloutPhaseCharacterProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseCharacterListHollow> getAllRolloutPhaseCharacterListHollow() {
        return new AllHollowRecordCollection<RolloutPhaseCharacterListHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseCharacterList").getTypeState()) {
            protected RolloutPhaseCharacterListHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseCharacterListHollow(ordinal);
            }
        };
    }
    public RolloutPhaseCharacterListHollow getRolloutPhaseCharacterListHollow(int ordinal) {
        objectCreationSampler.recordCreation(20);
        return (RolloutPhaseCharacterListHollow)rolloutPhaseCharacterListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseImageIdHollow> getAllRolloutPhaseImageIdHollow() {
        return new AllHollowRecordCollection<RolloutPhaseImageIdHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseImageId").getTypeState()) {
            protected RolloutPhaseImageIdHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseImageIdHollow(ordinal);
            }
        };
    }
    public RolloutPhaseImageIdHollow getRolloutPhaseImageIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(21);
        return (RolloutPhaseImageIdHollow)rolloutPhaseImageIdProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseOldArtworkListHollow> getAllRolloutPhaseOldArtworkListHollow() {
        return new AllHollowRecordCollection<RolloutPhaseOldArtworkListHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseOldArtworkList").getTypeState()) {
            protected RolloutPhaseOldArtworkListHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseOldArtworkListHollow(ordinal);
            }
        };
    }
    public RolloutPhaseOldArtworkListHollow getRolloutPhaseOldArtworkListHollow(int ordinal) {
        objectCreationSampler.recordCreation(22);
        return (RolloutPhaseOldArtworkListHollow)rolloutPhaseOldArtworkListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseWindowHollow> getAllRolloutPhaseWindowHollow() {
        return new AllHollowRecordCollection<RolloutPhaseWindowHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseWindow").getTypeState()) {
            protected RolloutPhaseWindowHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseWindowHollow(ordinal);
            }
        };
    }
    public RolloutPhaseWindowHollow getRolloutPhaseWindowHollow(int ordinal) {
        objectCreationSampler.recordCreation(23);
        return (RolloutPhaseWindowHollow)rolloutPhaseWindowProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseWindowMapHollow> getAllRolloutPhaseWindowMapHollow() {
        return new AllHollowRecordCollection<RolloutPhaseWindowMapHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseWindowMap").getTypeState()) {
            protected RolloutPhaseWindowMapHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseWindowMapHollow(ordinal);
            }
        };
    }
    public RolloutPhaseWindowMapHollow getRolloutPhaseWindowMapHollow(int ordinal) {
        objectCreationSampler.recordCreation(24);
        return (RolloutPhaseWindowMapHollow)rolloutPhaseWindowMapProvider.getHollowObject(ordinal);
    }
    public Collection<SeasonHollow> getAllSeasonHollow() {
        return new AllHollowRecordCollection<SeasonHollow>(getDataAccess().getTypeDataAccess("Season").getTypeState()) {
            protected SeasonHollow getForOrdinal(int ordinal) {
                return getSeasonHollow(ordinal);
            }
        };
    }
    public SeasonHollow getSeasonHollow(int ordinal) {
        objectCreationSampler.recordCreation(25);
        return (SeasonHollow)seasonProvider.getHollowObject(ordinal);
    }
    public Collection<SeasonListHollow> getAllSeasonListHollow() {
        return new AllHollowRecordCollection<SeasonListHollow>(getDataAccess().getTypeDataAccess("SeasonList").getTypeState()) {
            protected SeasonListHollow getForOrdinal(int ordinal) {
                return getSeasonListHollow(ordinal);
            }
        };
    }
    public SeasonListHollow getSeasonListHollow(int ordinal) {
        objectCreationSampler.recordCreation(26);
        return (SeasonListHollow)seasonListProvider.getHollowObject(ordinal);
    }
    public Collection<StreamDimensionsHollow> getAllStreamDimensionsHollow() {
        return new AllHollowRecordCollection<StreamDimensionsHollow>(getDataAccess().getTypeDataAccess("StreamDimensions").getTypeState()) {
            protected StreamDimensionsHollow getForOrdinal(int ordinal) {
                return getStreamDimensionsHollow(ordinal);
            }
        };
    }
    public StreamDimensionsHollow getStreamDimensionsHollow(int ordinal) {
        objectCreationSampler.recordCreation(27);
        return (StreamDimensionsHollow)streamDimensionsProvider.getHollowObject(ordinal);
    }
    public Collection<StreamFileIdentificationHollow> getAllStreamFileIdentificationHollow() {
        return new AllHollowRecordCollection<StreamFileIdentificationHollow>(getDataAccess().getTypeDataAccess("StreamFileIdentification").getTypeState()) {
            protected StreamFileIdentificationHollow getForOrdinal(int ordinal) {
                return getStreamFileIdentificationHollow(ordinal);
            }
        };
    }
    public StreamFileIdentificationHollow getStreamFileIdentificationHollow(int ordinal) {
        objectCreationSampler.recordCreation(28);
        return (StreamFileIdentificationHollow)streamFileIdentificationProvider.getHollowObject(ordinal);
    }
    public Collection<StreamProfileIdHollow> getAllStreamProfileIdHollow() {
        return new AllHollowRecordCollection<StreamProfileIdHollow>(getDataAccess().getTypeDataAccess("StreamProfileId").getTypeState()) {
            protected StreamProfileIdHollow getForOrdinal(int ordinal) {
                return getStreamProfileIdHollow(ordinal);
            }
        };
    }
    public StreamProfileIdHollow getStreamProfileIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(29);
        return (StreamProfileIdHollow)streamProfileIdProvider.getHollowObject(ordinal);
    }
    public Collection<StreamProfileIdListHollow> getAllStreamProfileIdListHollow() {
        return new AllHollowRecordCollection<StreamProfileIdListHollow>(getDataAccess().getTypeDataAccess("StreamProfileIdList").getTypeState()) {
            protected StreamProfileIdListHollow getForOrdinal(int ordinal) {
                return getStreamProfileIdListHollow(ordinal);
            }
        };
    }
    public StreamProfileIdListHollow getStreamProfileIdListHollow(int ordinal) {
        objectCreationSampler.recordCreation(30);
        return (StreamProfileIdListHollow)streamProfileIdListProvider.getHollowObject(ordinal);
    }
    public Collection<StringHollow> getAllStringHollow() {
        return new AllHollowRecordCollection<StringHollow>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected StringHollow getForOrdinal(int ordinal) {
                return getStringHollow(ordinal);
            }
        };
    }
    public StringHollow getStringHollow(int ordinal) {
        objectCreationSampler.recordCreation(31);
        return (StringHollow)stringProvider.getHollowObject(ordinal);
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
    public Collection<AudioStreamInfoHollow> getAllAudioStreamInfoHollow() {
        return new AllHollowRecordCollection<AudioStreamInfoHollow>(getDataAccess().getTypeDataAccess("AudioStreamInfo").getTypeState()) {
            protected AudioStreamInfoHollow getForOrdinal(int ordinal) {
                return getAudioStreamInfoHollow(ordinal);
            }
        };
    }
    public AudioStreamInfoHollow getAudioStreamInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(35);
        return (AudioStreamInfoHollow)audioStreamInfoProvider.getHollowObject(ordinal);
    }
    public Collection<Bcp47CodeHollow> getAllBcp47CodeHollow() {
        return new AllHollowRecordCollection<Bcp47CodeHollow>(getDataAccess().getTypeDataAccess("Bcp47Code").getTypeState()) {
            protected Bcp47CodeHollow getForOrdinal(int ordinal) {
                return getBcp47CodeHollow(ordinal);
            }
        };
    }
    public Bcp47CodeHollow getBcp47CodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(36);
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
        objectCreationSampler.recordCreation(37);
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
        objectCreationSampler.recordCreation(38);
        return (CacheDeploymentIntentHollow)cacheDeploymentIntentProvider.getHollowObject(ordinal);
    }
    public Collection<CdnDeploymentHollow> getAllCdnDeploymentHollow() {
        return new AllHollowRecordCollection<CdnDeploymentHollow>(getDataAccess().getTypeDataAccess("CdnDeployment").getTypeState()) {
            protected CdnDeploymentHollow getForOrdinal(int ordinal) {
                return getCdnDeploymentHollow(ordinal);
            }
        };
    }
    public CdnDeploymentHollow getCdnDeploymentHollow(int ordinal) {
        objectCreationSampler.recordCreation(39);
        return (CdnDeploymentHollow)cdnDeploymentProvider.getHollowObject(ordinal);
    }
    public Collection<CdnDeploymentSetHollow> getAllCdnDeploymentSetHollow() {
        return new AllHollowRecordCollection<CdnDeploymentSetHollow>(getDataAccess().getTypeDataAccess("CdnDeploymentSet").getTypeState()) {
            protected CdnDeploymentSetHollow getForOrdinal(int ordinal) {
                return getCdnDeploymentSetHollow(ordinal);
            }
        };
    }
    public CdnDeploymentSetHollow getCdnDeploymentSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(40);
        return (CdnDeploymentSetHollow)cdnDeploymentSetProvider.getHollowObject(ordinal);
    }
    public Collection<CdnsHollow> getAllCdnsHollow() {
        return new AllHollowRecordCollection<CdnsHollow>(getDataAccess().getTypeDataAccess("Cdns").getTypeState()) {
            protected CdnsHollow getForOrdinal(int ordinal) {
                return getCdnsHollow(ordinal);
            }
        };
    }
    public CdnsHollow getCdnsHollow(int ordinal) {
        objectCreationSampler.recordCreation(41);
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
        objectCreationSampler.recordCreation(42);
        return (CertificationSystemRatingHollow)certificationSystemRatingProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationSystemRatingListHollow> getAllCertificationSystemRatingListHollow() {
        return new AllHollowRecordCollection<CertificationSystemRatingListHollow>(getDataAccess().getTypeDataAccess("CertificationSystemRatingList").getTypeState()) {
            protected CertificationSystemRatingListHollow getForOrdinal(int ordinal) {
                return getCertificationSystemRatingListHollow(ordinal);
            }
        };
    }
    public CertificationSystemRatingListHollow getCertificationSystemRatingListHollow(int ordinal) {
        objectCreationSampler.recordCreation(43);
        return (CertificationSystemRatingListHollow)certificationSystemRatingListProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationSystemHollow> getAllCertificationSystemHollow() {
        return new AllHollowRecordCollection<CertificationSystemHollow>(getDataAccess().getTypeDataAccess("CertificationSystem").getTypeState()) {
            protected CertificationSystemHollow getForOrdinal(int ordinal) {
                return getCertificationSystemHollow(ordinal);
            }
        };
    }
    public CertificationSystemHollow getCertificationSystemHollow(int ordinal) {
        objectCreationSampler.recordCreation(44);
        return (CertificationSystemHollow)certificationSystemProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkAttributesHollow> getAllCharacterArtworkAttributesHollow() {
        return new AllHollowRecordCollection<CharacterArtworkAttributesHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkAttributes").getTypeState()) {
            protected CharacterArtworkAttributesHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkAttributesHollow(ordinal);
            }
        };
    }
    public CharacterArtworkAttributesHollow getCharacterArtworkAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(45);
        return (CharacterArtworkAttributesHollow)characterArtworkAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkDerivativeHollow> getAllCharacterArtworkDerivativeHollow() {
        return new AllHollowRecordCollection<CharacterArtworkDerivativeHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkDerivative").getTypeState()) {
            protected CharacterArtworkDerivativeHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkDerivativeHollow(ordinal);
            }
        };
    }
    public CharacterArtworkDerivativeHollow getCharacterArtworkDerivativeHollow(int ordinal) {
        objectCreationSampler.recordCreation(46);
        return (CharacterArtworkDerivativeHollow)characterArtworkDerivativeProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkDerivativeListHollow> getAllCharacterArtworkDerivativeListHollow() {
        return new AllHollowRecordCollection<CharacterArtworkDerivativeListHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkDerivativeList").getTypeState()) {
            protected CharacterArtworkDerivativeListHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkDerivativeListHollow(ordinal);
            }
        };
    }
    public CharacterArtworkDerivativeListHollow getCharacterArtworkDerivativeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(47);
        return (CharacterArtworkDerivativeListHollow)characterArtworkDerivativeListProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterElementsHollow> getAllCharacterElementsHollow() {
        return new AllHollowRecordCollection<CharacterElementsHollow>(getDataAccess().getTypeDataAccess("CharacterElements").getTypeState()) {
            protected CharacterElementsHollow getForOrdinal(int ordinal) {
                return getCharacterElementsHollow(ordinal);
            }
        };
    }
    public CharacterElementsHollow getCharacterElementsHollow(int ordinal) {
        objectCreationSampler.recordCreation(48);
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
        objectCreationSampler.recordCreation(49);
        return (CharacterHollow)characterProvider.getHollowObject(ordinal);
    }
    public Collection<CountryVideoDisplaySetHollow> getAllCountryVideoDisplaySetHollow() {
        return new AllHollowRecordCollection<CountryVideoDisplaySetHollow>(getDataAccess().getTypeDataAccess("CountryVideoDisplaySet").getTypeState()) {
            protected CountryVideoDisplaySetHollow getForOrdinal(int ordinal) {
                return getCountryVideoDisplaySetHollow(ordinal);
            }
        };
    }
    public CountryVideoDisplaySetHollow getCountryVideoDisplaySetHollow(int ordinal) {
        objectCreationSampler.recordCreation(50);
        return (CountryVideoDisplaySetHollow)countryVideoDisplaySetProvider.getHollowObject(ordinal);
    }
    public Collection<CountryVideoDisplaySetListHollow> getAllCountryVideoDisplaySetListHollow() {
        return new AllHollowRecordCollection<CountryVideoDisplaySetListHollow>(getDataAccess().getTypeDataAccess("CountryVideoDisplaySetList").getTypeState()) {
            protected CountryVideoDisplaySetListHollow getForOrdinal(int ordinal) {
                return getCountryVideoDisplaySetListHollow(ordinal);
            }
        };
    }
    public CountryVideoDisplaySetListHollow getCountryVideoDisplaySetListHollow(int ordinal) {
        objectCreationSampler.recordCreation(51);
        return (CountryVideoDisplaySetListHollow)countryVideoDisplaySetListProvider.getHollowObject(ordinal);
    }
    public Collection<DefaultExtensionRecipeHollow> getAllDefaultExtensionRecipeHollow() {
        return new AllHollowRecordCollection<DefaultExtensionRecipeHollow>(getDataAccess().getTypeDataAccess("DefaultExtensionRecipe").getTypeState()) {
            protected DefaultExtensionRecipeHollow getForOrdinal(int ordinal) {
                return getDefaultExtensionRecipeHollow(ordinal);
            }
        };
    }
    public DefaultExtensionRecipeHollow getDefaultExtensionRecipeHollow(int ordinal) {
        objectCreationSampler.recordCreation(52);
        return (DefaultExtensionRecipeHollow)defaultExtensionRecipeProvider.getHollowObject(ordinal);
    }
    public Collection<DisallowedSubtitleLangCodeHollow> getAllDisallowedSubtitleLangCodeHollow() {
        return new AllHollowRecordCollection<DisallowedSubtitleLangCodeHollow>(getDataAccess().getTypeDataAccess("DisallowedSubtitleLangCode").getTypeState()) {
            protected DisallowedSubtitleLangCodeHollow getForOrdinal(int ordinal) {
                return getDisallowedSubtitleLangCodeHollow(ordinal);
            }
        };
    }
    public DisallowedSubtitleLangCodeHollow getDisallowedSubtitleLangCodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(53);
        return (DisallowedSubtitleLangCodeHollow)disallowedSubtitleLangCodeProvider.getHollowObject(ordinal);
    }
    public Collection<DisallowedSubtitleLangCodesListHollow> getAllDisallowedSubtitleLangCodesListHollow() {
        return new AllHollowRecordCollection<DisallowedSubtitleLangCodesListHollow>(getDataAccess().getTypeDataAccess("DisallowedSubtitleLangCodesList").getTypeState()) {
            protected DisallowedSubtitleLangCodesListHollow getForOrdinal(int ordinal) {
                return getDisallowedSubtitleLangCodesListHollow(ordinal);
            }
        };
    }
    public DisallowedSubtitleLangCodesListHollow getDisallowedSubtitleLangCodesListHollow(int ordinal) {
        objectCreationSampler.recordCreation(54);
        return (DisallowedSubtitleLangCodesListHollow)disallowedSubtitleLangCodesListProvider.getHollowObject(ordinal);
    }
    public Collection<DisallowedAssetBundleHollow> getAllDisallowedAssetBundleHollow() {
        return new AllHollowRecordCollection<DisallowedAssetBundleHollow>(getDataAccess().getTypeDataAccess("DisallowedAssetBundle").getTypeState()) {
            protected DisallowedAssetBundleHollow getForOrdinal(int ordinal) {
                return getDisallowedAssetBundleHollow(ordinal);
            }
        };
    }
    public DisallowedAssetBundleHollow getDisallowedAssetBundleHollow(int ordinal) {
        objectCreationSampler.recordCreation(55);
        return (DisallowedAssetBundleHollow)disallowedAssetBundleProvider.getHollowObject(ordinal);
    }
    public Collection<DisallowedAssetBundlesListHollow> getAllDisallowedAssetBundlesListHollow() {
        return new AllHollowRecordCollection<DisallowedAssetBundlesListHollow>(getDataAccess().getTypeDataAccess("DisallowedAssetBundlesList").getTypeState()) {
            protected DisallowedAssetBundlesListHollow getForOrdinal(int ordinal) {
                return getDisallowedAssetBundlesListHollow(ordinal);
            }
        };
    }
    public DisallowedAssetBundlesListHollow getDisallowedAssetBundlesListHollow(int ordinal) {
        objectCreationSampler.recordCreation(56);
        return (DisallowedAssetBundlesListHollow)disallowedAssetBundlesListProvider.getHollowObject(ordinal);
    }
    public Collection<DrmHeaderInfoHollow> getAllDrmHeaderInfoHollow() {
        return new AllHollowRecordCollection<DrmHeaderInfoHollow>(getDataAccess().getTypeDataAccess("DrmHeaderInfo").getTypeState()) {
            protected DrmHeaderInfoHollow getForOrdinal(int ordinal) {
                return getDrmHeaderInfoHollow(ordinal);
            }
        };
    }
    public DrmHeaderInfoHollow getDrmHeaderInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(57);
        return (DrmHeaderInfoHollow)drmHeaderInfoProvider.getHollowObject(ordinal);
    }
    public Collection<DrmHeaderInfoListHollow> getAllDrmHeaderInfoListHollow() {
        return new AllHollowRecordCollection<DrmHeaderInfoListHollow>(getDataAccess().getTypeDataAccess("DrmHeaderInfoList").getTypeState()) {
            protected DrmHeaderInfoListHollow getForOrdinal(int ordinal) {
                return getDrmHeaderInfoListHollow(ordinal);
            }
        };
    }
    public DrmHeaderInfoListHollow getDrmHeaderInfoListHollow(int ordinal) {
        objectCreationSampler.recordCreation(58);
        return (DrmHeaderInfoListHollow)drmHeaderInfoListProvider.getHollowObject(ordinal);
    }
    public Collection<DrmSystemIdentifiersHollow> getAllDrmSystemIdentifiersHollow() {
        return new AllHollowRecordCollection<DrmSystemIdentifiersHollow>(getDataAccess().getTypeDataAccess("DrmSystemIdentifiers").getTypeState()) {
            protected DrmSystemIdentifiersHollow getForOrdinal(int ordinal) {
                return getDrmSystemIdentifiersHollow(ordinal);
            }
        };
    }
    public DrmSystemIdentifiersHollow getDrmSystemIdentifiersHollow(int ordinal) {
        objectCreationSampler.recordCreation(59);
        return (DrmSystemIdentifiersHollow)drmSystemIdentifiersProvider.getHollowObject(ordinal);
    }
    public Collection<ImageStreamInfoHollow> getAllImageStreamInfoHollow() {
        return new AllHollowRecordCollection<ImageStreamInfoHollow>(getDataAccess().getTypeDataAccess("ImageStreamInfo").getTypeState()) {
            protected ImageStreamInfoHollow getForOrdinal(int ordinal) {
                return getImageStreamInfoHollow(ordinal);
            }
        };
    }
    public ImageStreamInfoHollow getImageStreamInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(60);
        return (ImageStreamInfoHollow)imageStreamInfoProvider.getHollowObject(ordinal);
    }
    public Collection<LocaleTerritoryCodeHollow> getAllLocaleTerritoryCodeHollow() {
        return new AllHollowRecordCollection<LocaleTerritoryCodeHollow>(getDataAccess().getTypeDataAccess("LocaleTerritoryCode").getTypeState()) {
            protected LocaleTerritoryCodeHollow getForOrdinal(int ordinal) {
                return getLocaleTerritoryCodeHollow(ordinal);
            }
        };
    }
    public LocaleTerritoryCodeHollow getLocaleTerritoryCodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(61);
        return (LocaleTerritoryCodeHollow)localeTerritoryCodeProvider.getHollowObject(ordinal);
    }
    public Collection<LocaleTerritoryCodeListHollow> getAllLocaleTerritoryCodeListHollow() {
        return new AllHollowRecordCollection<LocaleTerritoryCodeListHollow>(getDataAccess().getTypeDataAccess("LocaleTerritoryCodeList").getTypeState()) {
            protected LocaleTerritoryCodeListHollow getForOrdinal(int ordinal) {
                return getLocaleTerritoryCodeListHollow(ordinal);
            }
        };
    }
    public LocaleTerritoryCodeListHollow getLocaleTerritoryCodeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(62);
        return (LocaleTerritoryCodeListHollow)localeTerritoryCodeListProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkLocaleHollow> getAllCharacterArtworkLocaleHollow() {
        return new AllHollowRecordCollection<CharacterArtworkLocaleHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkLocale").getTypeState()) {
            protected CharacterArtworkLocaleHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkLocaleHollow(ordinal);
            }
        };
    }
    public CharacterArtworkLocaleHollow getCharacterArtworkLocaleHollow(int ordinal) {
        objectCreationSampler.recordCreation(63);
        return (CharacterArtworkLocaleHollow)characterArtworkLocaleProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkLocaleListHollow> getAllCharacterArtworkLocaleListHollow() {
        return new AllHollowRecordCollection<CharacterArtworkLocaleListHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkLocaleList").getTypeState()) {
            protected CharacterArtworkLocaleListHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkLocaleListHollow(ordinal);
            }
        };
    }
    public CharacterArtworkLocaleListHollow getCharacterArtworkLocaleListHollow(int ordinal) {
        objectCreationSampler.recordCreation(64);
        return (CharacterArtworkLocaleListHollow)characterArtworkLocaleListProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkHollow> getAllCharacterArtworkHollow() {
        return new AllHollowRecordCollection<CharacterArtworkHollow>(getDataAccess().getTypeDataAccess("CharacterArtwork").getTypeState()) {
            protected CharacterArtworkHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkHollow(ordinal);
            }
        };
    }
    public CharacterArtworkHollow getCharacterArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(65);
        return (CharacterArtworkHollow)characterArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<OriginServersHollow> getAllOriginServersHollow() {
        return new AllHollowRecordCollection<OriginServersHollow>(getDataAccess().getTypeDataAccess("OriginServers").getTypeState()) {
            protected OriginServersHollow getForOrdinal(int ordinal) {
                return getOriginServersHollow(ordinal);
            }
        };
    }
    public OriginServersHollow getOriginServersHollow(int ordinal) {
        objectCreationSampler.recordCreation(66);
        return (OriginServersHollow)originServersProvider.getHollowObject(ordinal);
    }
    public Collection<PackageDrmInfoHollow> getAllPackageDrmInfoHollow() {
        return new AllHollowRecordCollection<PackageDrmInfoHollow>(getDataAccess().getTypeDataAccess("PackageDrmInfo").getTypeState()) {
            protected PackageDrmInfoHollow getForOrdinal(int ordinal) {
                return getPackageDrmInfoHollow(ordinal);
            }
        };
    }
    public PackageDrmInfoHollow getPackageDrmInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(67);
        return (PackageDrmInfoHollow)packageDrmInfoProvider.getHollowObject(ordinal);
    }
    public Collection<PackageDrmInfoListHollow> getAllPackageDrmInfoListHollow() {
        return new AllHollowRecordCollection<PackageDrmInfoListHollow>(getDataAccess().getTypeDataAccess("PackageDrmInfoList").getTypeState()) {
            protected PackageDrmInfoListHollow getForOrdinal(int ordinal) {
                return getPackageDrmInfoListHollow(ordinal);
            }
        };
    }
    public PackageDrmInfoListHollow getPackageDrmInfoListHollow(int ordinal) {
        objectCreationSampler.recordCreation(68);
        return (PackageDrmInfoListHollow)packageDrmInfoListProvider.getHollowObject(ordinal);
    }
    public Collection<PackageMomentHollow> getAllPackageMomentHollow() {
        return new AllHollowRecordCollection<PackageMomentHollow>(getDataAccess().getTypeDataAccess("PackageMoment").getTypeState()) {
            protected PackageMomentHollow getForOrdinal(int ordinal) {
                return getPackageMomentHollow(ordinal);
            }
        };
    }
    public PackageMomentHollow getPackageMomentHollow(int ordinal) {
        objectCreationSampler.recordCreation(69);
        return (PackageMomentHollow)packageMomentProvider.getHollowObject(ordinal);
    }
    public Collection<PackageMomentListHollow> getAllPackageMomentListHollow() {
        return new AllHollowRecordCollection<PackageMomentListHollow>(getDataAccess().getTypeDataAccess("PackageMomentList").getTypeState()) {
            protected PackageMomentListHollow getForOrdinal(int ordinal) {
                return getPackageMomentListHollow(ordinal);
            }
        };
    }
    public PackageMomentListHollow getPackageMomentListHollow(int ordinal) {
        objectCreationSampler.recordCreation(70);
        return (PackageMomentListHollow)packageMomentListProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkAttributeHollow> getAllPersonArtworkAttributeHollow() {
        return new AllHollowRecordCollection<PersonArtworkAttributeHollow>(getDataAccess().getTypeDataAccess("PersonArtworkAttribute").getTypeState()) {
            protected PersonArtworkAttributeHollow getForOrdinal(int ordinal) {
                return getPersonArtworkAttributeHollow(ordinal);
            }
        };
    }
    public PersonArtworkAttributeHollow getPersonArtworkAttributeHollow(int ordinal) {
        objectCreationSampler.recordCreation(71);
        return (PersonArtworkAttributeHollow)personArtworkAttributeProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkDerivativeHollow> getAllPersonArtworkDerivativeHollow() {
        return new AllHollowRecordCollection<PersonArtworkDerivativeHollow>(getDataAccess().getTypeDataAccess("PersonArtworkDerivative").getTypeState()) {
            protected PersonArtworkDerivativeHollow getForOrdinal(int ordinal) {
                return getPersonArtworkDerivativeHollow(ordinal);
            }
        };
    }
    public PersonArtworkDerivativeHollow getPersonArtworkDerivativeHollow(int ordinal) {
        objectCreationSampler.recordCreation(72);
        return (PersonArtworkDerivativeHollow)personArtworkDerivativeProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkDerivativeListHollow> getAllPersonArtworkDerivativeListHollow() {
        return new AllHollowRecordCollection<PersonArtworkDerivativeListHollow>(getDataAccess().getTypeDataAccess("PersonArtworkDerivativeList").getTypeState()) {
            protected PersonArtworkDerivativeListHollow getForOrdinal(int ordinal) {
                return getPersonArtworkDerivativeListHollow(ordinal);
            }
        };
    }
    public PersonArtworkDerivativeListHollow getPersonArtworkDerivativeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(73);
        return (PersonArtworkDerivativeListHollow)personArtworkDerivativeListProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkLocaleHollow> getAllPersonArtworkLocaleHollow() {
        return new AllHollowRecordCollection<PersonArtworkLocaleHollow>(getDataAccess().getTypeDataAccess("PersonArtworkLocale").getTypeState()) {
            protected PersonArtworkLocaleHollow getForOrdinal(int ordinal) {
                return getPersonArtworkLocaleHollow(ordinal);
            }
        };
    }
    public PersonArtworkLocaleHollow getPersonArtworkLocaleHollow(int ordinal) {
        objectCreationSampler.recordCreation(74);
        return (PersonArtworkLocaleHollow)personArtworkLocaleProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkLocaleListHollow> getAllPersonArtworkLocaleListHollow() {
        return new AllHollowRecordCollection<PersonArtworkLocaleListHollow>(getDataAccess().getTypeDataAccess("PersonArtworkLocaleList").getTypeState()) {
            protected PersonArtworkLocaleListHollow getForOrdinal(int ordinal) {
                return getPersonArtworkLocaleListHollow(ordinal);
            }
        };
    }
    public PersonArtworkLocaleListHollow getPersonArtworkLocaleListHollow(int ordinal) {
        objectCreationSampler.recordCreation(75);
        return (PersonArtworkLocaleListHollow)personArtworkLocaleListProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkHollow> getAllPersonArtworkHollow() {
        return new AllHollowRecordCollection<PersonArtworkHollow>(getDataAccess().getTypeDataAccess("PersonArtwork").getTypeState()) {
            protected PersonArtworkHollow getForOrdinal(int ordinal) {
                return getPersonArtworkHollow(ordinal);
            }
        };
    }
    public PersonArtworkHollow getPersonArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(76);
        return (PersonArtworkHollow)personArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<ProtectionTypesHollow> getAllProtectionTypesHollow() {
        return new AllHollowRecordCollection<ProtectionTypesHollow>(getDataAccess().getTypeDataAccess("ProtectionTypes").getTypeState()) {
            protected ProtectionTypesHollow getForOrdinal(int ordinal) {
                return getProtectionTypesHollow(ordinal);
            }
        };
    }
    public ProtectionTypesHollow getProtectionTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(77);
        return (ProtectionTypesHollow)protectionTypesProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseArtworkSourceFileIdHollow> getAllRolloutPhaseArtworkSourceFileIdHollow() {
        return new AllHollowRecordCollection<RolloutPhaseArtworkSourceFileIdHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseArtworkSourceFileId").getTypeState()) {
            protected RolloutPhaseArtworkSourceFileIdHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
            }
        };
    }
    public RolloutPhaseArtworkSourceFileIdHollow getRolloutPhaseArtworkSourceFileIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(78);
        return (RolloutPhaseArtworkSourceFileIdHollow)rolloutPhaseArtworkSourceFileIdProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseArtworkSourceFileIdListHollow> getAllRolloutPhaseArtworkSourceFileIdListHollow() {
        return new AllHollowRecordCollection<RolloutPhaseArtworkSourceFileIdListHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseArtworkSourceFileIdList").getTypeState()) {
            protected RolloutPhaseArtworkSourceFileIdListHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseArtworkSourceFileIdListHollow(ordinal);
            }
        };
    }
    public RolloutPhaseArtworkSourceFileIdListHollow getRolloutPhaseArtworkSourceFileIdListHollow(int ordinal) {
        objectCreationSampler.recordCreation(79);
        return (RolloutPhaseArtworkSourceFileIdListHollow)rolloutPhaseArtworkSourceFileIdListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseLocalizedMetadataHollow> getAllRolloutPhaseLocalizedMetadataHollow() {
        return new AllHollowRecordCollection<RolloutPhaseLocalizedMetadataHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseLocalizedMetadata").getTypeState()) {
            protected RolloutPhaseLocalizedMetadataHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseLocalizedMetadataHollow(ordinal);
            }
        };
    }
    public RolloutPhaseLocalizedMetadataHollow getRolloutPhaseLocalizedMetadataHollow(int ordinal) {
        objectCreationSampler.recordCreation(80);
        return (RolloutPhaseLocalizedMetadataHollow)rolloutPhaseLocalizedMetadataProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseNewArtworkHollow> getAllRolloutPhaseNewArtworkHollow() {
        return new AllHollowRecordCollection<RolloutPhaseNewArtworkHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseNewArtwork").getTypeState()) {
            protected RolloutPhaseNewArtworkHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseNewArtworkHollow(ordinal);
            }
        };
    }
    public RolloutPhaseNewArtworkHollow getRolloutPhaseNewArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(81);
        return (RolloutPhaseNewArtworkHollow)rolloutPhaseNewArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseTrailerSupplementalInfoHollow> getAllRolloutPhaseTrailerSupplementalInfoHollow() {
        return new AllHollowRecordCollection<RolloutPhaseTrailerSupplementalInfoHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseTrailerSupplementalInfo").getTypeState()) {
            protected RolloutPhaseTrailerSupplementalInfoHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseTrailerSupplementalInfoHollow(ordinal);
            }
        };
    }
    public RolloutPhaseTrailerSupplementalInfoHollow getRolloutPhaseTrailerSupplementalInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(82);
        return (RolloutPhaseTrailerSupplementalInfoHollow)rolloutPhaseTrailerSupplementalInfoProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhasesElementsTrailerSupplementalInfoMapHollow> getAllRolloutPhasesElementsTrailerSupplementalInfoMapHollow() {
        return new AllHollowRecordCollection<RolloutPhasesElementsTrailerSupplementalInfoMapHollow>(getDataAccess().getTypeDataAccess("RolloutPhasesElementsTrailerSupplementalInfoMap").getTypeState()) {
            protected RolloutPhasesElementsTrailerSupplementalInfoMapHollow getForOrdinal(int ordinal) {
                return getRolloutPhasesElementsTrailerSupplementalInfoMapHollow(ordinal);
            }
        };
    }
    public RolloutPhasesElementsTrailerSupplementalInfoMapHollow getRolloutPhasesElementsTrailerSupplementalInfoMapHollow(int ordinal) {
        objectCreationSampler.recordCreation(83);
        return (RolloutPhasesElementsTrailerSupplementalInfoMapHollow)rolloutPhasesElementsTrailerSupplementalInfoMapProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseTrailerHollow> getAllRolloutPhaseTrailerHollow() {
        return new AllHollowRecordCollection<RolloutPhaseTrailerHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseTrailer").getTypeState()) {
            protected RolloutPhaseTrailerHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseTrailerHollow(ordinal);
            }
        };
    }
    public RolloutPhaseTrailerHollow getRolloutPhaseTrailerHollow(int ordinal) {
        objectCreationSampler.recordCreation(84);
        return (RolloutPhaseTrailerHollow)rolloutPhaseTrailerProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseTrailerListHollow> getAllRolloutPhaseTrailerListHollow() {
        return new AllHollowRecordCollection<RolloutPhaseTrailerListHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseTrailerList").getTypeState()) {
            protected RolloutPhaseTrailerListHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseTrailerListHollow(ordinal);
            }
        };
    }
    public RolloutPhaseTrailerListHollow getRolloutPhaseTrailerListHollow(int ordinal) {
        objectCreationSampler.recordCreation(85);
        return (RolloutPhaseTrailerListHollow)rolloutPhaseTrailerListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseElementsHollow> getAllRolloutPhaseElementsHollow() {
        return new AllHollowRecordCollection<RolloutPhaseElementsHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseElements").getTypeState()) {
            protected RolloutPhaseElementsHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseElementsHollow(ordinal);
            }
        };
    }
    public RolloutPhaseElementsHollow getRolloutPhaseElementsHollow(int ordinal) {
        objectCreationSampler.recordCreation(86);
        return (RolloutPhaseElementsHollow)rolloutPhaseElementsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseHollow> getAllRolloutPhaseHollow() {
        return new AllHollowRecordCollection<RolloutPhaseHollow>(getDataAccess().getTypeDataAccess("RolloutPhase").getTypeState()) {
            protected RolloutPhaseHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseHollow(ordinal);
            }
        };
    }
    public RolloutPhaseHollow getRolloutPhaseHollow(int ordinal) {
        objectCreationSampler.recordCreation(87);
        return (RolloutPhaseHollow)rolloutPhaseProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseListHollow> getAllRolloutPhaseListHollow() {
        return new AllHollowRecordCollection<RolloutPhaseListHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseList").getTypeState()) {
            protected RolloutPhaseListHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseListHollow(ordinal);
            }
        };
    }
    public RolloutPhaseListHollow getRolloutPhaseListHollow(int ordinal) {
        objectCreationSampler.recordCreation(88);
        return (RolloutPhaseListHollow)rolloutPhaseListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutHollow> getAllRolloutHollow() {
        return new AllHollowRecordCollection<RolloutHollow>(getDataAccess().getTypeDataAccess("Rollout").getTypeState()) {
            protected RolloutHollow getForOrdinal(int ordinal) {
                return getRolloutHollow(ordinal);
            }
        };
    }
    public RolloutHollow getRolloutHollow(int ordinal) {
        objectCreationSampler.recordCreation(89);
        return (RolloutHollow)rolloutProvider.getHollowObject(ordinal);
    }
    public Collection<StorageGroupsHollow> getAllStorageGroupsHollow() {
        return new AllHollowRecordCollection<StorageGroupsHollow>(getDataAccess().getTypeDataAccess("StorageGroups").getTypeState()) {
            protected StorageGroupsHollow getForOrdinal(int ordinal) {
                return getStorageGroupsHollow(ordinal);
            }
        };
    }
    public StorageGroupsHollow getStorageGroupsHollow(int ordinal) {
        objectCreationSampler.recordCreation(90);
        return (StorageGroupsHollow)storageGroupsProvider.getHollowObject(ordinal);
    }
    public Collection<StreamAssetTypeHollow> getAllStreamAssetTypeHollow() {
        return new AllHollowRecordCollection<StreamAssetTypeHollow>(getDataAccess().getTypeDataAccess("StreamAssetType").getTypeState()) {
            protected StreamAssetTypeHollow getForOrdinal(int ordinal) {
                return getStreamAssetTypeHollow(ordinal);
            }
        };
    }
    public StreamAssetTypeHollow getStreamAssetTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(91);
        return (StreamAssetTypeHollow)streamAssetTypeProvider.getHollowObject(ordinal);
    }
    public Collection<StreamDeploymentInfoHollow> getAllStreamDeploymentInfoHollow() {
        return new AllHollowRecordCollection<StreamDeploymentInfoHollow>(getDataAccess().getTypeDataAccess("StreamDeploymentInfo").getTypeState()) {
            protected StreamDeploymentInfoHollow getForOrdinal(int ordinal) {
                return getStreamDeploymentInfoHollow(ordinal);
            }
        };
    }
    public StreamDeploymentInfoHollow getStreamDeploymentInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(92);
        return (StreamDeploymentInfoHollow)streamDeploymentInfoProvider.getHollowObject(ordinal);
    }
    public Collection<StreamDeploymentLabelHollow> getAllStreamDeploymentLabelHollow() {
        return new AllHollowRecordCollection<StreamDeploymentLabelHollow>(getDataAccess().getTypeDataAccess("StreamDeploymentLabel").getTypeState()) {
            protected StreamDeploymentLabelHollow getForOrdinal(int ordinal) {
                return getStreamDeploymentLabelHollow(ordinal);
            }
        };
    }
    public StreamDeploymentLabelHollow getStreamDeploymentLabelHollow(int ordinal) {
        objectCreationSampler.recordCreation(93);
        return (StreamDeploymentLabelHollow)streamDeploymentLabelProvider.getHollowObject(ordinal);
    }
    public Collection<StreamDeploymentLabelSetHollow> getAllStreamDeploymentLabelSetHollow() {
        return new AllHollowRecordCollection<StreamDeploymentLabelSetHollow>(getDataAccess().getTypeDataAccess("StreamDeploymentLabelSet").getTypeState()) {
            protected StreamDeploymentLabelSetHollow getForOrdinal(int ordinal) {
                return getStreamDeploymentLabelSetHollow(ordinal);
            }
        };
    }
    public StreamDeploymentLabelSetHollow getStreamDeploymentLabelSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(94);
        return (StreamDeploymentLabelSetHollow)streamDeploymentLabelSetProvider.getHollowObject(ordinal);
    }
    public Collection<StreamDeploymentHollow> getAllStreamDeploymentHollow() {
        return new AllHollowRecordCollection<StreamDeploymentHollow>(getDataAccess().getTypeDataAccess("StreamDeployment").getTypeState()) {
            protected StreamDeploymentHollow getForOrdinal(int ordinal) {
                return getStreamDeploymentHollow(ordinal);
            }
        };
    }
    public StreamDeploymentHollow getStreamDeploymentHollow(int ordinal) {
        objectCreationSampler.recordCreation(95);
        return (StreamDeploymentHollow)streamDeploymentProvider.getHollowObject(ordinal);
    }
    public Collection<StreamDrmInfoHollow> getAllStreamDrmInfoHollow() {
        return new AllHollowRecordCollection<StreamDrmInfoHollow>(getDataAccess().getTypeDataAccess("StreamDrmInfo").getTypeState()) {
            protected StreamDrmInfoHollow getForOrdinal(int ordinal) {
                return getStreamDrmInfoHollow(ordinal);
            }
        };
    }
    public StreamDrmInfoHollow getStreamDrmInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(96);
        return (StreamDrmInfoHollow)streamDrmInfoProvider.getHollowObject(ordinal);
    }
    public Collection<StreamProfileGroupsHollow> getAllStreamProfileGroupsHollow() {
        return new AllHollowRecordCollection<StreamProfileGroupsHollow>(getDataAccess().getTypeDataAccess("StreamProfileGroups").getTypeState()) {
            protected StreamProfileGroupsHollow getForOrdinal(int ordinal) {
                return getStreamProfileGroupsHollow(ordinal);
            }
        };
    }
    public StreamProfileGroupsHollow getStreamProfileGroupsHollow(int ordinal) {
        objectCreationSampler.recordCreation(97);
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
        objectCreationSampler.recordCreation(98);
        return (StreamProfilesHollow)streamProfilesProvider.getHollowObject(ordinal);
    }
    public Collection<TerritoryCountriesHollow> getAllTerritoryCountriesHollow() {
        return new AllHollowRecordCollection<TerritoryCountriesHollow>(getDataAccess().getTypeDataAccess("TerritoryCountries").getTypeState()) {
            protected TerritoryCountriesHollow getForOrdinal(int ordinal) {
                return getTerritoryCountriesHollow(ordinal);
            }
        };
    }
    public TerritoryCountriesHollow getTerritoryCountriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(99);
        return (TerritoryCountriesHollow)territoryCountriesProvider.getHollowObject(ordinal);
    }
    public Collection<TextStreamInfoHollow> getAllTextStreamInfoHollow() {
        return new AllHollowRecordCollection<TextStreamInfoHollow>(getDataAccess().getTypeDataAccess("TextStreamInfo").getTypeState()) {
            protected TextStreamInfoHollow getForOrdinal(int ordinal) {
                return getTextStreamInfoHollow(ordinal);
            }
        };
    }
    public TextStreamInfoHollow getTextStreamInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(100);
        return (TextStreamInfoHollow)textStreamInfoProvider.getHollowObject(ordinal);
    }
    public Collection<TopNAttributeHollow> getAllTopNAttributeHollow() {
        return new AllHollowRecordCollection<TopNAttributeHollow>(getDataAccess().getTypeDataAccess("TopNAttribute").getTypeState()) {
            protected TopNAttributeHollow getForOrdinal(int ordinal) {
                return getTopNAttributeHollow(ordinal);
            }
        };
    }
    public TopNAttributeHollow getTopNAttributeHollow(int ordinal) {
        objectCreationSampler.recordCreation(101);
        return (TopNAttributeHollow)topNAttributeProvider.getHollowObject(ordinal);
    }
    public Collection<TopNAttributesListHollow> getAllTopNAttributesListHollow() {
        return new AllHollowRecordCollection<TopNAttributesListHollow>(getDataAccess().getTypeDataAccess("TopNAttributesList").getTypeState()) {
            protected TopNAttributesListHollow getForOrdinal(int ordinal) {
                return getTopNAttributesListHollow(ordinal);
            }
        };
    }
    public TopNAttributesListHollow getTopNAttributesListHollow(int ordinal) {
        objectCreationSampler.recordCreation(102);
        return (TopNAttributesListHollow)topNAttributesListProvider.getHollowObject(ordinal);
    }
    public Collection<TopNHollow> getAllTopNHollow() {
        return new AllHollowRecordCollection<TopNHollow>(getDataAccess().getTypeDataAccess("TopN").getTypeState()) {
            protected TopNHollow getForOrdinal(int ordinal) {
                return getTopNHollow(ordinal);
            }
        };
    }
    public TopNHollow getTopNHollow(int ordinal) {
        objectCreationSampler.recordCreation(103);
        return (TopNHollow)topNProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerThemeHollow> getAllTrailerThemeHollow() {
        return new AllHollowRecordCollection<TrailerThemeHollow>(getDataAccess().getTypeDataAccess("TrailerTheme").getTypeState()) {
            protected TrailerThemeHollow getForOrdinal(int ordinal) {
                return getTrailerThemeHollow(ordinal);
            }
        };
    }
    public TrailerThemeHollow getTrailerThemeHollow(int ordinal) {
        objectCreationSampler.recordCreation(104);
        return (TrailerThemeHollow)trailerThemeProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerThemeListHollow> getAllTrailerThemeListHollow() {
        return new AllHollowRecordCollection<TrailerThemeListHollow>(getDataAccess().getTypeDataAccess("TrailerThemeList").getTypeState()) {
            protected TrailerThemeListHollow getForOrdinal(int ordinal) {
                return getTrailerThemeListHollow(ordinal);
            }
        };
    }
    public TrailerThemeListHollow getTrailerThemeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(105);
        return (TrailerThemeListHollow)trailerThemeListProvider.getHollowObject(ordinal);
    }
    public Collection<IndividualTrailerHollow> getAllIndividualTrailerHollow() {
        return new AllHollowRecordCollection<IndividualTrailerHollow>(getDataAccess().getTypeDataAccess("IndividualTrailer").getTypeState()) {
            protected IndividualTrailerHollow getForOrdinal(int ordinal) {
                return getIndividualTrailerHollow(ordinal);
            }
        };
    }
    public IndividualTrailerHollow getIndividualTrailerHollow(int ordinal) {
        objectCreationSampler.recordCreation(106);
        return (IndividualTrailerHollow)individualTrailerProvider.getHollowObject(ordinal);
    }
    public Collection<TrailersListHollow> getAllTrailersListHollow() {
        return new AllHollowRecordCollection<TrailersListHollow>(getDataAccess().getTypeDataAccess("TrailersList").getTypeState()) {
            protected TrailersListHollow getForOrdinal(int ordinal) {
                return getTrailersListHollow(ordinal);
            }
        };
    }
    public TrailersListHollow getTrailersListHollow(int ordinal) {
        objectCreationSampler.recordCreation(107);
        return (TrailersListHollow)trailersListProvider.getHollowObject(ordinal);
    }
    public Collection<TrailerHollow> getAllTrailerHollow() {
        return new AllHollowRecordCollection<TrailerHollow>(getDataAccess().getTypeDataAccess("Trailer").getTypeState()) {
            protected TrailerHollow getForOrdinal(int ordinal) {
                return getTrailerHollow(ordinal);
            }
        };
    }
    public TrailerHollow getTrailerHollow(int ordinal) {
        objectCreationSampler.recordCreation(108);
        return (TrailerHollow)trailerProvider.getHollowObject(ordinal);
    }
    public Collection<TranslatedTextValueHollow> getAllTranslatedTextValueHollow() {
        return new AllHollowRecordCollection<TranslatedTextValueHollow>(getDataAccess().getTypeDataAccess("TranslatedTextValue").getTypeState()) {
            protected TranslatedTextValueHollow getForOrdinal(int ordinal) {
                return getTranslatedTextValueHollow(ordinal);
            }
        };
    }
    public TranslatedTextValueHollow getTranslatedTextValueHollow(int ordinal) {
        objectCreationSampler.recordCreation(109);
        return (TranslatedTextValueHollow)translatedTextValueProvider.getHollowObject(ordinal);
    }
    public Collection<MapOfTranslatedTextHollow> getAllMapOfTranslatedTextHollow() {
        return new AllHollowRecordCollection<MapOfTranslatedTextHollow>(getDataAccess().getTypeDataAccess("MapOfTranslatedText").getTypeState()) {
            protected MapOfTranslatedTextHollow getForOrdinal(int ordinal) {
                return getMapOfTranslatedTextHollow(ordinal);
            }
        };
    }
    public MapOfTranslatedTextHollow getMapOfTranslatedTextHollow(int ordinal) {
        objectCreationSampler.recordCreation(110);
        return (MapOfTranslatedTextHollow)mapOfTranslatedTextProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresAlternateNamesHollow> getAllAltGenresAlternateNamesHollow() {
        return new AllHollowRecordCollection<AltGenresAlternateNamesHollow>(getDataAccess().getTypeDataAccess("AltGenresAlternateNames").getTypeState()) {
            protected AltGenresAlternateNamesHollow getForOrdinal(int ordinal) {
                return getAltGenresAlternateNamesHollow(ordinal);
            }
        };
    }
    public AltGenresAlternateNamesHollow getAltGenresAlternateNamesHollow(int ordinal) {
        objectCreationSampler.recordCreation(111);
        return (AltGenresAlternateNamesHollow)altGenresAlternateNamesProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresAlternateNamesListHollow> getAllAltGenresAlternateNamesListHollow() {
        return new AllHollowRecordCollection<AltGenresAlternateNamesListHollow>(getDataAccess().getTypeDataAccess("AltGenresAlternateNamesList").getTypeState()) {
            protected AltGenresAlternateNamesListHollow getForOrdinal(int ordinal) {
                return getAltGenresAlternateNamesListHollow(ordinal);
            }
        };
    }
    public AltGenresAlternateNamesListHollow getAltGenresAlternateNamesListHollow(int ordinal) {
        objectCreationSampler.recordCreation(112);
        return (AltGenresAlternateNamesListHollow)altGenresAlternateNamesListProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedCharacterHollow> getAllLocalizedCharacterHollow() {
        return new AllHollowRecordCollection<LocalizedCharacterHollow>(getDataAccess().getTypeDataAccess("LocalizedCharacter").getTypeState()) {
            protected LocalizedCharacterHollow getForOrdinal(int ordinal) {
                return getLocalizedCharacterHollow(ordinal);
            }
        };
    }
    public LocalizedCharacterHollow getLocalizedCharacterHollow(int ordinal) {
        objectCreationSampler.recordCreation(113);
        return (LocalizedCharacterHollow)localizedCharacterProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedMetadataHollow> getAllLocalizedMetadataHollow() {
        return new AllHollowRecordCollection<LocalizedMetadataHollow>(getDataAccess().getTypeDataAccess("LocalizedMetadata").getTypeState()) {
            protected LocalizedMetadataHollow getForOrdinal(int ordinal) {
                return getLocalizedMetadataHollow(ordinal);
            }
        };
    }
    public LocalizedMetadataHollow getLocalizedMetadataHollow(int ordinal) {
        objectCreationSampler.recordCreation(114);
        return (LocalizedMetadataHollow)localizedMetadataProvider.getHollowObject(ordinal);
    }
    public Collection<StoriesSynopsesHookHollow> getAllStoriesSynopsesHookHollow() {
        return new AllHollowRecordCollection<StoriesSynopsesHookHollow>(getDataAccess().getTypeDataAccess("StoriesSynopsesHook").getTypeState()) {
            protected StoriesSynopsesHookHollow getForOrdinal(int ordinal) {
                return getStoriesSynopsesHookHollow(ordinal);
            }
        };
    }
    public StoriesSynopsesHookHollow getStoriesSynopsesHookHollow(int ordinal) {
        objectCreationSampler.recordCreation(115);
        return (StoriesSynopsesHookHollow)storiesSynopsesHookProvider.getHollowObject(ordinal);
    }
    public Collection<StoriesSynopsesHookListHollow> getAllStoriesSynopsesHookListHollow() {
        return new AllHollowRecordCollection<StoriesSynopsesHookListHollow>(getDataAccess().getTypeDataAccess("StoriesSynopsesHookList").getTypeState()) {
            protected StoriesSynopsesHookListHollow getForOrdinal(int ordinal) {
                return getStoriesSynopsesHookListHollow(ordinal);
            }
        };
    }
    public StoriesSynopsesHookListHollow getStoriesSynopsesHookListHollow(int ordinal) {
        objectCreationSampler.recordCreation(116);
        return (StoriesSynopsesHookListHollow)storiesSynopsesHookListProvider.getHollowObject(ordinal);
    }
    public Collection<TranslatedTextHollow> getAllTranslatedTextHollow() {
        return new AllHollowRecordCollection<TranslatedTextHollow>(getDataAccess().getTypeDataAccess("TranslatedText").getTypeState()) {
            protected TranslatedTextHollow getForOrdinal(int ordinal) {
                return getTranslatedTextHollow(ordinal);
            }
        };
    }
    public TranslatedTextHollow getTranslatedTextHollow(int ordinal) {
        objectCreationSampler.recordCreation(117);
        return (TranslatedTextHollow)translatedTextProvider.getHollowObject(ordinal);
    }
    public Collection<AltGenresHollow> getAllAltGenresHollow() {
        return new AllHollowRecordCollection<AltGenresHollow>(getDataAccess().getTypeDataAccess("AltGenres").getTypeState()) {
            protected AltGenresHollow getForOrdinal(int ordinal) {
                return getAltGenresHollow(ordinal);
            }
        };
    }
    public AltGenresHollow getAltGenresHollow(int ordinal) {
        objectCreationSampler.recordCreation(118);
        return (AltGenresHollow)altGenresProvider.getHollowObject(ordinal);
    }
    public Collection<AssetMetaDatasHollow> getAllAssetMetaDatasHollow() {
        return new AllHollowRecordCollection<AssetMetaDatasHollow>(getDataAccess().getTypeDataAccess("AssetMetaDatas").getTypeState()) {
            protected AssetMetaDatasHollow getForOrdinal(int ordinal) {
                return getAssetMetaDatasHollow(ordinal);
            }
        };
    }
    public AssetMetaDatasHollow getAssetMetaDatasHollow(int ordinal) {
        objectCreationSampler.recordCreation(119);
        return (AssetMetaDatasHollow)assetMetaDatasProvider.getHollowObject(ordinal);
    }
    public Collection<AwardsHollow> getAllAwardsHollow() {
        return new AllHollowRecordCollection<AwardsHollow>(getDataAccess().getTypeDataAccess("Awards").getTypeState()) {
            protected AwardsHollow getForOrdinal(int ordinal) {
                return getAwardsHollow(ordinal);
            }
        };
    }
    public AwardsHollow getAwardsHollow(int ordinal) {
        objectCreationSampler.recordCreation(120);
        return (AwardsHollow)awardsProvider.getHollowObject(ordinal);
    }
    public Collection<CategoriesHollow> getAllCategoriesHollow() {
        return new AllHollowRecordCollection<CategoriesHollow>(getDataAccess().getTypeDataAccess("Categories").getTypeState()) {
            protected CategoriesHollow getForOrdinal(int ordinal) {
                return getCategoriesHollow(ordinal);
            }
        };
    }
    public CategoriesHollow getCategoriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(121);
        return (CategoriesHollow)categoriesProvider.getHollowObject(ordinal);
    }
    public Collection<CategoryGroupsHollow> getAllCategoryGroupsHollow() {
        return new AllHollowRecordCollection<CategoryGroupsHollow>(getDataAccess().getTypeDataAccess("CategoryGroups").getTypeState()) {
            protected CategoryGroupsHollow getForOrdinal(int ordinal) {
                return getCategoryGroupsHollow(ordinal);
            }
        };
    }
    public CategoryGroupsHollow getCategoryGroupsHollow(int ordinal) {
        objectCreationSampler.recordCreation(122);
        return (CategoryGroupsHollow)categoryGroupsProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationsHollow> getAllCertificationsHollow() {
        return new AllHollowRecordCollection<CertificationsHollow>(getDataAccess().getTypeDataAccess("Certifications").getTypeState()) {
            protected CertificationsHollow getForOrdinal(int ordinal) {
                return getCertificationsHollow(ordinal);
            }
        };
    }
    public CertificationsHollow getCertificationsHollow(int ordinal) {
        objectCreationSampler.recordCreation(123);
        return (CertificationsHollow)certificationsProvider.getHollowObject(ordinal);
    }
    public Collection<CharactersHollow> getAllCharactersHollow() {
        return new AllHollowRecordCollection<CharactersHollow>(getDataAccess().getTypeDataAccess("Characters").getTypeState()) {
            protected CharactersHollow getForOrdinal(int ordinal) {
                return getCharactersHollow(ordinal);
            }
        };
    }
    public CharactersHollow getCharactersHollow(int ordinal) {
        objectCreationSampler.recordCreation(124);
        return (CharactersHollow)charactersProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertSystemRatingHollow> getAllConsolidatedCertSystemRatingHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertSystemRatingHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertSystemRating").getTypeState()) {
            protected ConsolidatedCertSystemRatingHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertSystemRatingHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertSystemRatingHollow getConsolidatedCertSystemRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(125);
        return (ConsolidatedCertSystemRatingHollow)consolidatedCertSystemRatingProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertSystemRatingListHollow> getAllConsolidatedCertSystemRatingListHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertSystemRatingListHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertSystemRatingList").getTypeState()) {
            protected ConsolidatedCertSystemRatingListHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertSystemRatingListHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertSystemRatingListHollow getConsolidatedCertSystemRatingListHollow(int ordinal) {
        objectCreationSampler.recordCreation(126);
        return (ConsolidatedCertSystemRatingListHollow)consolidatedCertSystemRatingListProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedCertificationSystemsHollow> getAllConsolidatedCertificationSystemsHollow() {
        return new AllHollowRecordCollection<ConsolidatedCertificationSystemsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedCertificationSystems").getTypeState()) {
            protected ConsolidatedCertificationSystemsHollow getForOrdinal(int ordinal) {
                return getConsolidatedCertificationSystemsHollow(ordinal);
            }
        };
    }
    public ConsolidatedCertificationSystemsHollow getConsolidatedCertificationSystemsHollow(int ordinal) {
        objectCreationSampler.recordCreation(127);
        return (ConsolidatedCertificationSystemsHollow)consolidatedCertificationSystemsProvider.getHollowObject(ordinal);
    }
    public Collection<EpisodesHollow> getAllEpisodesHollow() {
        return new AllHollowRecordCollection<EpisodesHollow>(getDataAccess().getTypeDataAccess("Episodes").getTypeState()) {
            protected EpisodesHollow getForOrdinal(int ordinal) {
                return getEpisodesHollow(ordinal);
            }
        };
    }
    public EpisodesHollow getEpisodesHollow(int ordinal) {
        objectCreationSampler.recordCreation(128);
        return (EpisodesHollow)episodesProvider.getHollowObject(ordinal);
    }
    public Collection<FestivalsHollow> getAllFestivalsHollow() {
        return new AllHollowRecordCollection<FestivalsHollow>(getDataAccess().getTypeDataAccess("Festivals").getTypeState()) {
            protected FestivalsHollow getForOrdinal(int ordinal) {
                return getFestivalsHollow(ordinal);
            }
        };
    }
    public FestivalsHollow getFestivalsHollow(int ordinal) {
        objectCreationSampler.recordCreation(129);
        return (FestivalsHollow)festivalsProvider.getHollowObject(ordinal);
    }
    public Collection<LanguagesHollow> getAllLanguagesHollow() {
        return new AllHollowRecordCollection<LanguagesHollow>(getDataAccess().getTypeDataAccess("Languages").getTypeState()) {
            protected LanguagesHollow getForOrdinal(int ordinal) {
                return getLanguagesHollow(ordinal);
            }
        };
    }
    public LanguagesHollow getLanguagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(130);
        return (LanguagesHollow)languagesProvider.getHollowObject(ordinal);
    }
    public Collection<MovieRatingsHollow> getAllMovieRatingsHollow() {
        return new AllHollowRecordCollection<MovieRatingsHollow>(getDataAccess().getTypeDataAccess("MovieRatings").getTypeState()) {
            protected MovieRatingsHollow getForOrdinal(int ordinal) {
                return getMovieRatingsHollow(ordinal);
            }
        };
    }
    public MovieRatingsHollow getMovieRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(131);
        return (MovieRatingsHollow)movieRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<MoviesHollow> getAllMoviesHollow() {
        return new AllHollowRecordCollection<MoviesHollow>(getDataAccess().getTypeDataAccess("Movies").getTypeState()) {
            protected MoviesHollow getForOrdinal(int ordinal) {
                return getMoviesHollow(ordinal);
            }
        };
    }
    public MoviesHollow getMoviesHollow(int ordinal) {
        objectCreationSampler.recordCreation(132);
        return (MoviesHollow)moviesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonAliasesHollow> getAllPersonAliasesHollow() {
        return new AllHollowRecordCollection<PersonAliasesHollow>(getDataAccess().getTypeDataAccess("PersonAliases").getTypeState()) {
            protected PersonAliasesHollow getForOrdinal(int ordinal) {
                return getPersonAliasesHollow(ordinal);
            }
        };
    }
    public PersonAliasesHollow getPersonAliasesHollow(int ordinal) {
        objectCreationSampler.recordCreation(133);
        return (PersonAliasesHollow)personAliasesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsHollow> getAllPersonsHollow() {
        return new AllHollowRecordCollection<PersonsHollow>(getDataAccess().getTypeDataAccess("Persons").getTypeState()) {
            protected PersonsHollow getForOrdinal(int ordinal) {
                return getPersonsHollow(ordinal);
            }
        };
    }
    public PersonsHollow getPersonsHollow(int ordinal) {
        objectCreationSampler.recordCreation(134);
        return (PersonsHollow)personsProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsHollow> getAllRatingsHollow() {
        return new AllHollowRecordCollection<RatingsHollow>(getDataAccess().getTypeDataAccess("Ratings").getTypeState()) {
            protected RatingsHollow getForOrdinal(int ordinal) {
                return getRatingsHollow(ordinal);
            }
        };
    }
    public RatingsHollow getRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(135);
        return (RatingsHollow)ratingsProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypesHollow> getAllShowMemberTypesHollow() {
        return new AllHollowRecordCollection<ShowMemberTypesHollow>(getDataAccess().getTypeDataAccess("ShowMemberTypes").getTypeState()) {
            protected ShowMemberTypesHollow getForOrdinal(int ordinal) {
                return getShowMemberTypesHollow(ordinal);
            }
        };
    }
    public ShowMemberTypesHollow getShowMemberTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(136);
        return (ShowMemberTypesHollow)showMemberTypesProvider.getHollowObject(ordinal);
    }
    public Collection<Stories_SynopsesHollow> getAllStories_SynopsesHollow() {
        return new AllHollowRecordCollection<Stories_SynopsesHollow>(getDataAccess().getTypeDataAccess("Stories_Synopses").getTypeState()) {
            protected Stories_SynopsesHollow getForOrdinal(int ordinal) {
                return getStories_SynopsesHollow(ordinal);
            }
        };
    }
    public Stories_SynopsesHollow getStories_SynopsesHollow(int ordinal) {
        objectCreationSampler.recordCreation(137);
        return (Stories_SynopsesHollow)stories_SynopsesProvider.getHollowObject(ordinal);
    }
    public Collection<TurboCollectionsHollow> getAllTurboCollectionsHollow() {
        return new AllHollowRecordCollection<TurboCollectionsHollow>(getDataAccess().getTypeDataAccess("TurboCollections").getTypeState()) {
            protected TurboCollectionsHollow getForOrdinal(int ordinal) {
                return getTurboCollectionsHollow(ordinal);
            }
        };
    }
    public TurboCollectionsHollow getTurboCollectionsHollow(int ordinal) {
        objectCreationSampler.recordCreation(138);
        return (TurboCollectionsHollow)turboCollectionsProvider.getHollowObject(ordinal);
    }
    public Collection<VMSAwardHollow> getAllVMSAwardHollow() {
        return new AllHollowRecordCollection<VMSAwardHollow>(getDataAccess().getTypeDataAccess("VMSAward").getTypeState()) {
            protected VMSAwardHollow getForOrdinal(int ordinal) {
                return getVMSAwardHollow(ordinal);
            }
        };
    }
    public VMSAwardHollow getVMSAwardHollow(int ordinal) {
        objectCreationSampler.recordCreation(139);
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
        objectCreationSampler.recordCreation(140);
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
        objectCreationSampler.recordCreation(141);
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
        objectCreationSampler.recordCreation(142);
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
        objectCreationSampler.recordCreation(143);
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
        objectCreationSampler.recordCreation(144);
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
        objectCreationSampler.recordCreation(145);
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
        objectCreationSampler.recordCreation(146);
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
        objectCreationSampler.recordCreation(147);
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
        objectCreationSampler.recordCreation(148);
        return (VideoArtWorkRecipesHollow)videoArtWorkRecipesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkRecipeListHollow> getAllVideoArtWorkRecipeListHollow() {
        return new AllHollowRecordCollection<VideoArtWorkRecipeListHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkRecipeList").getTypeState()) {
            protected VideoArtWorkRecipeListHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkRecipeListHollow(ordinal);
            }
        };
    }
    public VideoArtWorkRecipeListHollow getVideoArtWorkRecipeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(149);
        return (VideoArtWorkRecipeListHollow)videoArtWorkRecipeListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesThemesHollow> getAllVideoArtWorkSourceAttributesThemesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesThemesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributesThemes").getTypeState()) {
            protected VideoArtWorkSourceAttributesThemesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesThemesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesThemesHollow getVideoArtWorkSourceAttributesThemesHollow(int ordinal) {
        objectCreationSampler.recordCreation(150);
        return (VideoArtWorkSourceAttributesThemesHollow)videoArtWorkSourceAttributesThemesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtworkAttributeHollow> getAllVideoArtworkAttributeHollow() {
        return new AllHollowRecordCollection<VideoArtworkAttributeHollow>(getDataAccess().getTypeDataAccess("VideoArtworkAttribute").getTypeState()) {
            protected VideoArtworkAttributeHollow getForOrdinal(int ordinal) {
                return getVideoArtworkAttributeHollow(ordinal);
            }
        };
    }
    public VideoArtworkAttributeHollow getVideoArtworkAttributeHollow(int ordinal) {
        objectCreationSampler.recordCreation(151);
        return (VideoArtworkAttributeHollow)videoArtworkAttributeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkMultiValueAttributeHollow> getAllVideoArtWorkMultiValueAttributeHollow() {
        return new AllHollowRecordCollection<VideoArtWorkMultiValueAttributeHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkMultiValueAttribute").getTypeState()) {
            protected VideoArtWorkMultiValueAttributeHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkMultiValueAttributeHollow(ordinal);
            }
        };
    }
    public VideoArtWorkMultiValueAttributeHollow getVideoArtWorkMultiValueAttributeHollow(int ordinal) {
        objectCreationSampler.recordCreation(152);
        return (VideoArtWorkMultiValueAttributeHollow)videoArtWorkMultiValueAttributeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtWorkSourceAttributesHollow> getAllVideoArtWorkSourceAttributesHollow() {
        return new AllHollowRecordCollection<VideoArtWorkSourceAttributesHollow>(getDataAccess().getTypeDataAccess("VideoArtWorkSourceAttributes").getTypeState()) {
            protected VideoArtWorkSourceAttributesHollow getForOrdinal(int ordinal) {
                return getVideoArtWorkSourceAttributesHollow(ordinal);
            }
        };
    }
    public VideoArtWorkSourceAttributesHollow getVideoArtWorkSourceAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(153);
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
        objectCreationSampler.recordCreation(154);
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
        objectCreationSampler.recordCreation(155);
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
        objectCreationSampler.recordCreation(156);
        return (VideoAwardArrayOfAwardHollow)videoAwardArrayOfAwardProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardMappingHollow> getAllVideoAwardMappingHollow() {
        return new AllHollowRecordCollection<VideoAwardMappingHollow>(getDataAccess().getTypeDataAccess("VideoAwardMapping").getTypeState()) {
            protected VideoAwardMappingHollow getForOrdinal(int ordinal) {
                return getVideoAwardMappingHollow(ordinal);
            }
        };
    }
    public VideoAwardMappingHollow getVideoAwardMappingHollow(int ordinal) {
        objectCreationSampler.recordCreation(157);
        return (VideoAwardMappingHollow)videoAwardMappingProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardListHollow> getAllVideoAwardListHollow() {
        return new AllHollowRecordCollection<VideoAwardListHollow>(getDataAccess().getTypeDataAccess("VideoAwardList").getTypeState()) {
            protected VideoAwardListHollow getForOrdinal(int ordinal) {
                return getVideoAwardListHollow(ordinal);
            }
        };
    }
    public VideoAwardListHollow getVideoAwardListHollow(int ordinal) {
        objectCreationSampler.recordCreation(158);
        return (VideoAwardListHollow)videoAwardListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardHollow> getAllVideoAwardHollow() {
        return new AllHollowRecordCollection<VideoAwardHollow>(getDataAccess().getTypeDataAccess("VideoAward").getTypeState()) {
            protected VideoAwardHollow getForOrdinal(int ordinal) {
                return getVideoAwardHollow(ordinal);
            }
        };
    }
    public VideoAwardHollow getVideoAwardHollow(int ordinal) {
        objectCreationSampler.recordCreation(159);
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
        objectCreationSampler.recordCreation(160);
        return (VideoDateWindowHollow)videoDateWindowProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDateWindowListHollow> getAllVideoDateWindowListHollow() {
        return new AllHollowRecordCollection<VideoDateWindowListHollow>(getDataAccess().getTypeDataAccess("VideoDateWindowList").getTypeState()) {
            protected VideoDateWindowListHollow getForOrdinal(int ordinal) {
                return getVideoDateWindowListHollow(ordinal);
            }
        };
    }
    public VideoDateWindowListHollow getVideoDateWindowListHollow(int ordinal) {
        objectCreationSampler.recordCreation(161);
        return (VideoDateWindowListHollow)videoDateWindowListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDateHollow> getAllVideoDateHollow() {
        return new AllHollowRecordCollection<VideoDateHollow>(getDataAccess().getTypeDataAccess("VideoDate").getTypeState()) {
            protected VideoDateHollow getForOrdinal(int ordinal) {
                return getVideoDateHollow(ordinal);
            }
        };
    }
    public VideoDateHollow getVideoDateHollow(int ordinal) {
        objectCreationSampler.recordCreation(162);
        return (VideoDateHollow)videoDateProvider.getHollowObject(ordinal);
    }
    public Collection<VideoDisplaySetHollow> getAllVideoDisplaySetHollow() {
        return new AllHollowRecordCollection<VideoDisplaySetHollow>(getDataAccess().getTypeDataAccess("VideoDisplaySet").getTypeState()) {
            protected VideoDisplaySetHollow getForOrdinal(int ordinal) {
                return getVideoDisplaySetHollow(ordinal);
            }
        };
    }
    public VideoDisplaySetHollow getVideoDisplaySetHollow(int ordinal) {
        objectCreationSampler.recordCreation(163);
        return (VideoDisplaySetHollow)videoDisplaySetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralAliasHollow> getAllVideoGeneralAliasHollow() {
        return new AllHollowRecordCollection<VideoGeneralAliasHollow>(getDataAccess().getTypeDataAccess("VideoGeneralAlias").getTypeState()) {
            protected VideoGeneralAliasHollow getForOrdinal(int ordinal) {
                return getVideoGeneralAliasHollow(ordinal);
            }
        };
    }
    public VideoGeneralAliasHollow getVideoGeneralAliasHollow(int ordinal) {
        objectCreationSampler.recordCreation(164);
        return (VideoGeneralAliasHollow)videoGeneralAliasProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralAliasListHollow> getAllVideoGeneralAliasListHollow() {
        return new AllHollowRecordCollection<VideoGeneralAliasListHollow>(getDataAccess().getTypeDataAccess("VideoGeneralAliasList").getTypeState()) {
            protected VideoGeneralAliasListHollow getForOrdinal(int ordinal) {
                return getVideoGeneralAliasListHollow(ordinal);
            }
        };
    }
    public VideoGeneralAliasListHollow getVideoGeneralAliasListHollow(int ordinal) {
        objectCreationSampler.recordCreation(165);
        return (VideoGeneralAliasListHollow)videoGeneralAliasListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralEpisodeTypeHollow> getAllVideoGeneralEpisodeTypeHollow() {
        return new AllHollowRecordCollection<VideoGeneralEpisodeTypeHollow>(getDataAccess().getTypeDataAccess("VideoGeneralEpisodeType").getTypeState()) {
            protected VideoGeneralEpisodeTypeHollow getForOrdinal(int ordinal) {
                return getVideoGeneralEpisodeTypeHollow(ordinal);
            }
        };
    }
    public VideoGeneralEpisodeTypeHollow getVideoGeneralEpisodeTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(166);
        return (VideoGeneralEpisodeTypeHollow)videoGeneralEpisodeTypeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralEpisodeTypeListHollow> getAllVideoGeneralEpisodeTypeListHollow() {
        return new AllHollowRecordCollection<VideoGeneralEpisodeTypeListHollow>(getDataAccess().getTypeDataAccess("VideoGeneralEpisodeTypeList").getTypeState()) {
            protected VideoGeneralEpisodeTypeListHollow getForOrdinal(int ordinal) {
                return getVideoGeneralEpisodeTypeListHollow(ordinal);
            }
        };
    }
    public VideoGeneralEpisodeTypeListHollow getVideoGeneralEpisodeTypeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(167);
        return (VideoGeneralEpisodeTypeListHollow)videoGeneralEpisodeTypeListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralTitleTypeHollow> getAllVideoGeneralTitleTypeHollow() {
        return new AllHollowRecordCollection<VideoGeneralTitleTypeHollow>(getDataAccess().getTypeDataAccess("VideoGeneralTitleType").getTypeState()) {
            protected VideoGeneralTitleTypeHollow getForOrdinal(int ordinal) {
                return getVideoGeneralTitleTypeHollow(ordinal);
            }
        };
    }
    public VideoGeneralTitleTypeHollow getVideoGeneralTitleTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(168);
        return (VideoGeneralTitleTypeHollow)videoGeneralTitleTypeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralTitleTypeListHollow> getAllVideoGeneralTitleTypeListHollow() {
        return new AllHollowRecordCollection<VideoGeneralTitleTypeListHollow>(getDataAccess().getTypeDataAccess("VideoGeneralTitleTypeList").getTypeState()) {
            protected VideoGeneralTitleTypeListHollow getForOrdinal(int ordinal) {
                return getVideoGeneralTitleTypeListHollow(ordinal);
            }
        };
    }
    public VideoGeneralTitleTypeListHollow getVideoGeneralTitleTypeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(169);
        return (VideoGeneralTitleTypeListHollow)videoGeneralTitleTypeListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralHollow> getAllVideoGeneralHollow() {
        return new AllHollowRecordCollection<VideoGeneralHollow>(getDataAccess().getTypeDataAccess("VideoGeneral").getTypeState()) {
            protected VideoGeneralHollow getForOrdinal(int ordinal) {
                return getVideoGeneralHollow(ordinal);
            }
        };
    }
    public VideoGeneralHollow getVideoGeneralHollow(int ordinal) {
        objectCreationSampler.recordCreation(170);
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
        objectCreationSampler.recordCreation(171);
        return (VideoPersonAliasHollow)videoPersonAliasProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonAliasListHollow> getAllVideoPersonAliasListHollow() {
        return new AllHollowRecordCollection<VideoPersonAliasListHollow>(getDataAccess().getTypeDataAccess("VideoPersonAliasList").getTypeState()) {
            protected VideoPersonAliasListHollow getForOrdinal(int ordinal) {
                return getVideoPersonAliasListHollow(ordinal);
            }
        };
    }
    public VideoPersonAliasListHollow getVideoPersonAliasListHollow(int ordinal) {
        objectCreationSampler.recordCreation(172);
        return (VideoPersonAliasListHollow)videoPersonAliasListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonCastHollow> getAllVideoPersonCastHollow() {
        return new AllHollowRecordCollection<VideoPersonCastHollow>(getDataAccess().getTypeDataAccess("VideoPersonCast").getTypeState()) {
            protected VideoPersonCastHollow getForOrdinal(int ordinal) {
                return getVideoPersonCastHollow(ordinal);
            }
        };
    }
    public VideoPersonCastHollow getVideoPersonCastHollow(int ordinal) {
        objectCreationSampler.recordCreation(173);
        return (VideoPersonCastHollow)videoPersonCastProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonCastListHollow> getAllVideoPersonCastListHollow() {
        return new AllHollowRecordCollection<VideoPersonCastListHollow>(getDataAccess().getTypeDataAccess("VideoPersonCastList").getTypeState()) {
            protected VideoPersonCastListHollow getForOrdinal(int ordinal) {
                return getVideoPersonCastListHollow(ordinal);
            }
        };
    }
    public VideoPersonCastListHollow getVideoPersonCastListHollow(int ordinal) {
        objectCreationSampler.recordCreation(174);
        return (VideoPersonCastListHollow)videoPersonCastListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoPersonHollow> getAllVideoPersonHollow() {
        return new AllHollowRecordCollection<VideoPersonHollow>(getDataAccess().getTypeDataAccess("VideoPerson").getTypeState()) {
            protected VideoPersonHollow getForOrdinal(int ordinal) {
                return getVideoPersonHollow(ordinal);
            }
        };
    }
    public VideoPersonHollow getVideoPersonHollow(int ordinal) {
        objectCreationSampler.recordCreation(175);
        return (VideoPersonHollow)videoPersonProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingAdvisoryIdHollow> getAllVideoRatingAdvisoryIdHollow() {
        return new AllHollowRecordCollection<VideoRatingAdvisoryIdHollow>(getDataAccess().getTypeDataAccess("VideoRatingAdvisoryId").getTypeState()) {
            protected VideoRatingAdvisoryIdHollow getForOrdinal(int ordinal) {
                return getVideoRatingAdvisoryIdHollow(ordinal);
            }
        };
    }
    public VideoRatingAdvisoryIdHollow getVideoRatingAdvisoryIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(176);
        return (VideoRatingAdvisoryIdHollow)videoRatingAdvisoryIdProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingAdvisoryIdListHollow> getAllVideoRatingAdvisoryIdListHollow() {
        return new AllHollowRecordCollection<VideoRatingAdvisoryIdListHollow>(getDataAccess().getTypeDataAccess("VideoRatingAdvisoryIdList").getTypeState()) {
            protected VideoRatingAdvisoryIdListHollow getForOrdinal(int ordinal) {
                return getVideoRatingAdvisoryIdListHollow(ordinal);
            }
        };
    }
    public VideoRatingAdvisoryIdListHollow getVideoRatingAdvisoryIdListHollow(int ordinal) {
        objectCreationSampler.recordCreation(177);
        return (VideoRatingAdvisoryIdListHollow)videoRatingAdvisoryIdListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingAdvisoriesHollow> getAllVideoRatingAdvisoriesHollow() {
        return new AllHollowRecordCollection<VideoRatingAdvisoriesHollow>(getDataAccess().getTypeDataAccess("VideoRatingAdvisories").getTypeState()) {
            protected VideoRatingAdvisoriesHollow getForOrdinal(int ordinal) {
                return getVideoRatingAdvisoriesHollow(ordinal);
            }
        };
    }
    public VideoRatingAdvisoriesHollow getVideoRatingAdvisoriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(178);
        return (VideoRatingAdvisoriesHollow)videoRatingAdvisoriesProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoCountryRatingHollow> getAllConsolidatedVideoCountryRatingHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoCountryRatingHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoCountryRating").getTypeState()) {
            protected ConsolidatedVideoCountryRatingHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoCountryRatingHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoCountryRatingHollow getConsolidatedVideoCountryRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(179);
        return (ConsolidatedVideoCountryRatingHollow)consolidatedVideoCountryRatingProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoCountryRatingListHollow> getAllConsolidatedVideoCountryRatingListHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoCountryRatingListHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoCountryRatingList").getTypeState()) {
            protected ConsolidatedVideoCountryRatingListHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoCountryRatingListHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoCountryRatingListHollow getConsolidatedVideoCountryRatingListHollow(int ordinal) {
        objectCreationSampler.recordCreation(180);
        return (ConsolidatedVideoCountryRatingListHollow)consolidatedVideoCountryRatingListProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingHollow> getAllConsolidatedVideoRatingHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRating").getTypeState()) {
            protected ConsolidatedVideoRatingHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingHollow getConsolidatedVideoRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(181);
        return (ConsolidatedVideoRatingHollow)consolidatedVideoRatingProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingListHollow> getAllConsolidatedVideoRatingListHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingListHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatingList").getTypeState()) {
            protected ConsolidatedVideoRatingListHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingListHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingListHollow getConsolidatedVideoRatingListHollow(int ordinal) {
        objectCreationSampler.recordCreation(182);
        return (ConsolidatedVideoRatingListHollow)consolidatedVideoRatingListProvider.getHollowObject(ordinal);
    }
    public Collection<ConsolidatedVideoRatingsHollow> getAllConsolidatedVideoRatingsHollow() {
        return new AllHollowRecordCollection<ConsolidatedVideoRatingsHollow>(getDataAccess().getTypeDataAccess("ConsolidatedVideoRatings").getTypeState()) {
            protected ConsolidatedVideoRatingsHollow getForOrdinal(int ordinal) {
                return getConsolidatedVideoRatingsHollow(ordinal);
            }
        };
    }
    public ConsolidatedVideoRatingsHollow getConsolidatedVideoRatingsHollow(int ordinal) {
        objectCreationSampler.recordCreation(183);
        return (ConsolidatedVideoRatingsHollow)consolidatedVideoRatingsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingRatingReasonIdsHollow> getAllVideoRatingRatingReasonIdsHollow() {
        return new AllHollowRecordCollection<VideoRatingRatingReasonIdsHollow>(getDataAccess().getTypeDataAccess("VideoRatingRatingReasonIds").getTypeState()) {
            protected VideoRatingRatingReasonIdsHollow getForOrdinal(int ordinal) {
                return getVideoRatingRatingReasonIdsHollow(ordinal);
            }
        };
    }
    public VideoRatingRatingReasonIdsHollow getVideoRatingRatingReasonIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(184);
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
        objectCreationSampler.recordCreation(185);
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
        objectCreationSampler.recordCreation(186);
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
        objectCreationSampler.recordCreation(187);
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
        objectCreationSampler.recordCreation(188);
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
        objectCreationSampler.recordCreation(189);
        return (VideoRatingHollow)videoRatingProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsContractAssetHollow> getAllVideoRightsContractAssetHollow() {
        return new AllHollowRecordCollection<VideoRightsContractAssetHollow>(getDataAccess().getTypeDataAccess("VideoRightsContractAsset").getTypeState()) {
            protected VideoRightsContractAssetHollow getForOrdinal(int ordinal) {
                return getVideoRightsContractAssetHollow(ordinal);
            }
        };
    }
    public VideoRightsContractAssetHollow getVideoRightsContractAssetHollow(int ordinal) {
        objectCreationSampler.recordCreation(190);
        return (VideoRightsContractAssetHollow)videoRightsContractAssetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsContractAssetsSetHollow> getAllVideoRightsContractAssetsSetHollow() {
        return new AllHollowRecordCollection<VideoRightsContractAssetsSetHollow>(getDataAccess().getTypeDataAccess("VideoRightsContractAssetsSet").getTypeState()) {
            protected VideoRightsContractAssetsSetHollow getForOrdinal(int ordinal) {
                return getVideoRightsContractAssetsSetHollow(ordinal);
            }
        };
    }
    public VideoRightsContractAssetsSetHollow getVideoRightsContractAssetsSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(191);
        return (VideoRightsContractAssetsSetHollow)videoRightsContractAssetsSetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsContractIdHollow> getAllVideoRightsContractIdHollow() {
        return new AllHollowRecordCollection<VideoRightsContractIdHollow>(getDataAccess().getTypeDataAccess("VideoRightsContractId").getTypeState()) {
            protected VideoRightsContractIdHollow getForOrdinal(int ordinal) {
                return getVideoRightsContractIdHollow(ordinal);
            }
        };
    }
    public VideoRightsContractIdHollow getVideoRightsContractIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(192);
        return (VideoRightsContractIdHollow)videoRightsContractIdProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsContractPackageHollow> getAllVideoRightsContractPackageHollow() {
        return new AllHollowRecordCollection<VideoRightsContractPackageHollow>(getDataAccess().getTypeDataAccess("VideoRightsContractPackage").getTypeState()) {
            protected VideoRightsContractPackageHollow getForOrdinal(int ordinal) {
                return getVideoRightsContractPackageHollow(ordinal);
            }
        };
    }
    public VideoRightsContractPackageHollow getVideoRightsContractPackageHollow(int ordinal) {
        objectCreationSampler.recordCreation(193);
        return (VideoRightsContractPackageHollow)videoRightsContractPackageProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsContractPackagesListHollow> getAllVideoRightsContractPackagesListHollow() {
        return new AllHollowRecordCollection<VideoRightsContractPackagesListHollow>(getDataAccess().getTypeDataAccess("VideoRightsContractPackagesList").getTypeState()) {
            protected VideoRightsContractPackagesListHollow getForOrdinal(int ordinal) {
                return getVideoRightsContractPackagesListHollow(ordinal);
            }
        };
    }
    public VideoRightsContractPackagesListHollow getVideoRightsContractPackagesListHollow(int ordinal) {
        objectCreationSampler.recordCreation(194);
        return (VideoRightsContractPackagesListHollow)videoRightsContractPackagesListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsContractHollow> getAllVideoRightsContractHollow() {
        return new AllHollowRecordCollection<VideoRightsContractHollow>(getDataAccess().getTypeDataAccess("VideoRightsContract").getTypeState()) {
            protected VideoRightsContractHollow getForOrdinal(int ordinal) {
                return getVideoRightsContractHollow(ordinal);
            }
        };
    }
    public VideoRightsContractHollow getVideoRightsContractHollow(int ordinal) {
        objectCreationSampler.recordCreation(195);
        return (VideoRightsContractHollow)videoRightsContractProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsContractSetHollow> getAllVideoRightsContractSetHollow() {
        return new AllHollowRecordCollection<VideoRightsContractSetHollow>(getDataAccess().getTypeDataAccess("VideoRightsContractSet").getTypeState()) {
            protected VideoRightsContractSetHollow getForOrdinal(int ordinal) {
                return getVideoRightsContractSetHollow(ordinal);
            }
        };
    }
    public VideoRightsContractSetHollow getVideoRightsContractSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(196);
        return (VideoRightsContractSetHollow)videoRightsContractSetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsFlagsHollow> getAllVideoRightsFlagsHollow() {
        return new AllHollowRecordCollection<VideoRightsFlagsHollow>(getDataAccess().getTypeDataAccess("VideoRightsFlags").getTypeState()) {
            protected VideoRightsFlagsHollow getForOrdinal(int ordinal) {
                return getVideoRightsFlagsHollow(ordinal);
            }
        };
    }
    public VideoRightsFlagsHollow getVideoRightsFlagsHollow(int ordinal) {
        objectCreationSampler.recordCreation(197);
        return (VideoRightsFlagsHollow)videoRightsFlagsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsWindowContractIdListHollow> getAllVideoRightsWindowContractIdListHollow() {
        return new AllHollowRecordCollection<VideoRightsWindowContractIdListHollow>(getDataAccess().getTypeDataAccess("VideoRightsWindowContractIdList").getTypeState()) {
            protected VideoRightsWindowContractIdListHollow getForOrdinal(int ordinal) {
                return getVideoRightsWindowContractIdListHollow(ordinal);
            }
        };
    }
    public VideoRightsWindowContractIdListHollow getVideoRightsWindowContractIdListHollow(int ordinal) {
        objectCreationSampler.recordCreation(198);
        return (VideoRightsWindowContractIdListHollow)videoRightsWindowContractIdListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsWindowHollow> getAllVideoRightsWindowHollow() {
        return new AllHollowRecordCollection<VideoRightsWindowHollow>(getDataAccess().getTypeDataAccess("VideoRightsWindow").getTypeState()) {
            protected VideoRightsWindowHollow getForOrdinal(int ordinal) {
                return getVideoRightsWindowHollow(ordinal);
            }
        };
    }
    public VideoRightsWindowHollow getVideoRightsWindowHollow(int ordinal) {
        objectCreationSampler.recordCreation(199);
        return (VideoRightsWindowHollow)videoRightsWindowProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsWindowsSetHollow> getAllVideoRightsWindowsSetHollow() {
        return new AllHollowRecordCollection<VideoRightsWindowsSetHollow>(getDataAccess().getTypeDataAccess("VideoRightsWindowsSet").getTypeState()) {
            protected VideoRightsWindowsSetHollow getForOrdinal(int ordinal) {
                return getVideoRightsWindowsSetHollow(ordinal);
            }
        };
    }
    public VideoRightsWindowsSetHollow getVideoRightsWindowsSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(200);
        return (VideoRightsWindowsSetHollow)videoRightsWindowsSetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRightsRightsHollow> getAllVideoRightsRightsHollow() {
        return new AllHollowRecordCollection<VideoRightsRightsHollow>(getDataAccess().getTypeDataAccess("VideoRightsRights").getTypeState()) {
            protected VideoRightsRightsHollow getForOrdinal(int ordinal) {
                return getVideoRightsRightsHollow(ordinal);
            }
        };
    }
    public VideoRightsRightsHollow getVideoRightsRightsHollow(int ordinal) {
        objectCreationSampler.recordCreation(201);
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
        objectCreationSampler.recordCreation(202);
        return (VideoRightsHollow)videoRightsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoStreamInfoHollow> getAllVideoStreamInfoHollow() {
        return new AllHollowRecordCollection<VideoStreamInfoHollow>(getDataAccess().getTypeDataAccess("VideoStreamInfo").getTypeState()) {
            protected VideoStreamInfoHollow getForOrdinal(int ordinal) {
                return getVideoStreamInfoHollow(ordinal);
            }
        };
    }
    public VideoStreamInfoHollow getVideoStreamInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(203);
        return (VideoStreamInfoHollow)videoStreamInfoProvider.getHollowObject(ordinal);
    }
    public Collection<StreamNonImageInfoHollow> getAllStreamNonImageInfoHollow() {
        return new AllHollowRecordCollection<StreamNonImageInfoHollow>(getDataAccess().getTypeDataAccess("StreamNonImageInfo").getTypeState()) {
            protected StreamNonImageInfoHollow getForOrdinal(int ordinal) {
                return getStreamNonImageInfoHollow(ordinal);
            }
        };
    }
    public StreamNonImageInfoHollow getStreamNonImageInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(204);
        return (StreamNonImageInfoHollow)streamNonImageInfoProvider.getHollowObject(ordinal);
    }
    public Collection<PackageStreamHollow> getAllPackageStreamHollow() {
        return new AllHollowRecordCollection<PackageStreamHollow>(getDataAccess().getTypeDataAccess("PackageStream").getTypeState()) {
            protected PackageStreamHollow getForOrdinal(int ordinal) {
                return getPackageStreamHollow(ordinal);
            }
        };
    }
    public PackageStreamHollow getPackageStreamHollow(int ordinal) {
        objectCreationSampler.recordCreation(205);
        return (PackageStreamHollow)packageStreamProvider.getHollowObject(ordinal);
    }
    public Collection<PackageStreamSetHollow> getAllPackageStreamSetHollow() {
        return new AllHollowRecordCollection<PackageStreamSetHollow>(getDataAccess().getTypeDataAccess("PackageStreamSet").getTypeState()) {
            protected PackageStreamSetHollow getForOrdinal(int ordinal) {
                return getPackageStreamSetHollow(ordinal);
            }
        };
    }
    public PackageStreamSetHollow getPackageStreamSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(206);
        return (PackageStreamSetHollow)packageStreamSetProvider.getHollowObject(ordinal);
    }
    public Collection<PackagesHollow> getAllPackagesHollow() {
        return new AllHollowRecordCollection<PackagesHollow>(getDataAccess().getTypeDataAccess("Packages").getTypeState()) {
            protected PackagesHollow getForOrdinal(int ordinal) {
                return getPackagesHollow(ordinal);
            }
        };
    }
    public PackagesHollow getPackagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(207);
        return (PackagesHollow)packagesProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeMediaHollow> getAllVideoTypeMediaHollow() {
        return new AllHollowRecordCollection<VideoTypeMediaHollow>(getDataAccess().getTypeDataAccess("VideoTypeMedia").getTypeState()) {
            protected VideoTypeMediaHollow getForOrdinal(int ordinal) {
                return getVideoTypeMediaHollow(ordinal);
            }
        };
    }
    public VideoTypeMediaHollow getVideoTypeMediaHollow(int ordinal) {
        objectCreationSampler.recordCreation(208);
        return (VideoTypeMediaHollow)videoTypeMediaProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeMediaListHollow> getAllVideoTypeMediaListHollow() {
        return new AllHollowRecordCollection<VideoTypeMediaListHollow>(getDataAccess().getTypeDataAccess("VideoTypeMediaList").getTypeState()) {
            protected VideoTypeMediaListHollow getForOrdinal(int ordinal) {
                return getVideoTypeMediaListHollow(ordinal);
            }
        };
    }
    public VideoTypeMediaListHollow getVideoTypeMediaListHollow(int ordinal) {
        objectCreationSampler.recordCreation(209);
        return (VideoTypeMediaListHollow)videoTypeMediaListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeDescriptorHollow> getAllVideoTypeDescriptorHollow() {
        return new AllHollowRecordCollection<VideoTypeDescriptorHollow>(getDataAccess().getTypeDataAccess("VideoTypeDescriptor").getTypeState()) {
            protected VideoTypeDescriptorHollow getForOrdinal(int ordinal) {
                return getVideoTypeDescriptorHollow(ordinal);
            }
        };
    }
    public VideoTypeDescriptorHollow getVideoTypeDescriptorHollow(int ordinal) {
        objectCreationSampler.recordCreation(210);
        return (VideoTypeDescriptorHollow)videoTypeDescriptorProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeDescriptorListHollow> getAllVideoTypeDescriptorListHollow() {
        return new AllHollowRecordCollection<VideoTypeDescriptorListHollow>(getDataAccess().getTypeDataAccess("VideoTypeDescriptorList").getTypeState()) {
            protected VideoTypeDescriptorListHollow getForOrdinal(int ordinal) {
                return getVideoTypeDescriptorListHollow(ordinal);
            }
        };
    }
    public VideoTypeDescriptorListHollow getVideoTypeDescriptorListHollow(int ordinal) {
        objectCreationSampler.recordCreation(211);
        return (VideoTypeDescriptorListHollow)videoTypeDescriptorListProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeHollow> getAllVideoTypeHollow() {
        return new AllHollowRecordCollection<VideoTypeHollow>(getDataAccess().getTypeDataAccess("VideoType").getTypeState()) {
            protected VideoTypeHollow getForOrdinal(int ordinal) {
                return getVideoTypeHollow(ordinal);
            }
        };
    }
    public VideoTypeHollow getVideoTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(212);
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