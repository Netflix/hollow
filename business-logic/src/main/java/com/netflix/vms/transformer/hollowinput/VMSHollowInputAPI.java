package com.netflix.vms.transformer.hollowinput;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.util.AllHollowRecordCollection;

@SuppressWarnings("all")
public class VMSHollowInputAPI extends HollowAPI {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final CharacterQuoteTypeAPI characterQuoteTypeAPI;
    private final CharacterQuoteListTypeAPI characterQuoteListTypeAPI;
    private final ChunkDurationsStringTypeAPI chunkDurationsStringTypeAPI;
    private final CodecPrivateDataStringTypeAPI codecPrivateDataStringTypeAPI;
    private final DateTypeAPI dateTypeAPI;
    private final DerivativeTagTypeAPI derivativeTagTypeAPI;
    private final DownloadableIdTypeAPI downloadableIdTypeAPI;
    private final DownloadableIdListTypeAPI downloadableIdListTypeAPI;
    private final DrmInfoStringTypeAPI drmInfoStringTypeAPI;
    private final EpisodeTypeAPI episodeTypeAPI;
    private final EpisodeListTypeAPI episodeListTypeAPI;
    private final ExplicitDateTypeAPI explicitDateTypeAPI;
    private final ISOCountryTypeAPI iSOCountryTypeAPI;
    private final ISOCountryListTypeAPI iSOCountryListTypeAPI;
    private final ISOCountrySetTypeAPI iSOCountrySetTypeAPI;
    private final ListOfDerivativeTagTypeAPI listOfDerivativeTagTypeAPI;
    private final MapKeyTypeAPI mapKeyTypeAPI;
    private final MapOfFlagsFirstDisplayDatesTypeAPI mapOfFlagsFirstDisplayDatesTypeAPI;
    private final FlagsTypeAPI flagsTypeAPI;
    private final PersonCharacterTypeAPI personCharacterTypeAPI;
    private final CharacterListTypeAPI characterListTypeAPI;
    private final MovieCharacterPersonTypeAPI movieCharacterPersonTypeAPI;
    private final PersonVideoAliasIdTypeAPI personVideoAliasIdTypeAPI;
    private final PersonVideoAliasIdsListTypeAPI personVideoAliasIdsListTypeAPI;
    private final PersonVideoRoleTypeAPI personVideoRoleTypeAPI;
    private final PersonVideoRolesListTypeAPI personVideoRolesListTypeAPI;
    private final PersonVideoTypeAPI personVideoTypeAPI;
    private final RightsAssetSetIdTypeAPI rightsAssetSetIdTypeAPI;
    private final RightsContractPackageTypeAPI rightsContractPackageTypeAPI;
    private final ListOfRightsContractPackageTypeAPI listOfRightsContractPackageTypeAPI;
    private final RightsWindowContractTypeAPI rightsWindowContractTypeAPI;
    private final ListOfRightsWindowContractTypeAPI listOfRightsWindowContractTypeAPI;
    private final RightsWindowTypeAPI rightsWindowTypeAPI;
    private final ListOfRightsWindowTypeAPI listOfRightsWindowTypeAPI;
    private final RolloutPhaseWindowTypeAPI rolloutPhaseWindowTypeAPI;
    private final RolloutPhaseWindowMapTypeAPI rolloutPhaseWindowMapTypeAPI;
    private final SeasonTypeAPI seasonTypeAPI;
    private final SeasonListTypeAPI seasonListTypeAPI;
    private final ShowMemberTypeTypeAPI showMemberTypeTypeAPI;
    private final ShowMemberTypeListTypeAPI showMemberTypeListTypeAPI;
    private final ShowCountryLabelTypeAPI showCountryLabelTypeAPI;
    private final ShowSeasonEpisodeTypeAPI showSeasonEpisodeTypeAPI;
    private final StreamAssetMetadataTypeAPI streamAssetMetadataTypeAPI;
    private final StreamDimensionsTypeAPI streamDimensionsTypeAPI;
    private final StreamFileIdentificationTypeAPI streamFileIdentificationTypeAPI;
    private final StreamProfileIdTypeAPI streamProfileIdTypeAPI;
    private final StreamProfileIdListTypeAPI streamProfileIdListTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final AbsoluteScheduleTypeAPI absoluteScheduleTypeAPI;
    private final ArtWorkImageTypeTypeAPI artWorkImageTypeTypeAPI;
    private final ArtworkRecipeTypeAPI artworkRecipeTypeAPI;
    private final AudioStreamInfoTypeAPI audioStreamInfoTypeAPI;
    private final CSMReviewTypeAPI cSMReviewTypeAPI;
    private final CacheDeploymentIntentTypeAPI cacheDeploymentIntentTypeAPI;
    private final CdnTypeAPI cdnTypeAPI;
    private final CdnDeploymentTypeAPI cdnDeploymentTypeAPI;
    private final CdnDeploymentSetTypeAPI cdnDeploymentSetTypeAPI;
    private final CertificationSystemRatingTypeAPI certificationSystemRatingTypeAPI;
    private final CertificationSystemRatingListTypeAPI certificationSystemRatingListTypeAPI;
    private final CertificationSystemTypeAPI certificationSystemTypeAPI;
    private final CharacterElementsTypeAPI characterElementsTypeAPI;
    private final CharacterTypeAPI characterTypeAPI;
    private final DamMerchStillsMomentTypeAPI damMerchStillsMomentTypeAPI;
    private final DamMerchStillsTypeAPI damMerchStillsTypeAPI;
    private final DisallowedSubtitleLangCodeTypeAPI disallowedSubtitleLangCodeTypeAPI;
    private final DisallowedSubtitleLangCodesListTypeAPI disallowedSubtitleLangCodesListTypeAPI;
    private final DisallowedAssetBundleTypeAPI disallowedAssetBundleTypeAPI;
    private final DisallowedAssetBundlesListTypeAPI disallowedAssetBundlesListTypeAPI;
    private final ContractTypeAPI contractTypeAPI;
    private final DrmHeaderInfoTypeAPI drmHeaderInfoTypeAPI;
    private final DrmHeaderInfoListTypeAPI drmHeaderInfoListTypeAPI;
    private final DrmSystemIdentifiersTypeAPI drmSystemIdentifiersTypeAPI;
    private final IPLArtworkDerivativeTypeAPI iPLArtworkDerivativeTypeAPI;
    private final IPLDerivativeSetTypeAPI iPLDerivativeSetTypeAPI;
    private final IPLDerivativeGroupTypeAPI iPLDerivativeGroupTypeAPI;
    private final IPLDerivativeGroupSetTypeAPI iPLDerivativeGroupSetTypeAPI;
    private final IPLArtworkDerivativeSetTypeAPI iPLArtworkDerivativeSetTypeAPI;
    private final ImageStreamInfoTypeAPI imageStreamInfoTypeAPI;
    private final ListOfContractTypeAPI listOfContractTypeAPI;
    private final ContractsTypeAPI contractsTypeAPI;
    private final ListOfPackageTagsTypeAPI listOfPackageTagsTypeAPI;
    private final DeployablePackagesTypeAPI deployablePackagesTypeAPI;
    private final ListOfStringTypeAPI listOfStringTypeAPI;
    private final LocaleTerritoryCodeTypeAPI localeTerritoryCodeTypeAPI;
    private final LocaleTerritoryCodeListTypeAPI localeTerritoryCodeListTypeAPI;
    private final ArtworkLocaleTypeAPI artworkLocaleTypeAPI;
    private final ArtworkLocaleListTypeAPI artworkLocaleListTypeAPI;
    private final MasterScheduleTypeAPI masterScheduleTypeAPI;
    private final MultiValuePassthroughMapTypeAPI multiValuePassthroughMapTypeAPI;
    private final OriginServerTypeAPI originServerTypeAPI;
    private final OverrideScheduleTypeAPI overrideScheduleTypeAPI;
    private final PackageDrmInfoTypeAPI packageDrmInfoTypeAPI;
    private final PackageDrmInfoListTypeAPI packageDrmInfoListTypeAPI;
    private final PackageMomentTypeAPI packageMomentTypeAPI;
    private final PackageMomentListTypeAPI packageMomentListTypeAPI;
    private final PhaseTagTypeAPI phaseTagTypeAPI;
    private final PhaseTagListTypeAPI phaseTagListTypeAPI;
    private final ProtectionTypesTypeAPI protectionTypesTypeAPI;
    private final ReleaseDateTypeAPI releaseDateTypeAPI;
    private final ListOfReleaseDatesTypeAPI listOfReleaseDatesTypeAPI;
    private final RightsAssetTypeAPI rightsAssetTypeAPI;
    private final RightsContractAssetTypeAPI rightsContractAssetTypeAPI;
    private final ListOfRightsContractAssetTypeAPI listOfRightsContractAssetTypeAPI;
    private final RightsContractTypeAPI rightsContractTypeAPI;
    private final ListOfRightsContractTypeAPI listOfRightsContractTypeAPI;
    private final RightsTypeAPI rightsTypeAPI;
    private final RolloutPhaseArtworkSourceFileIdTypeAPI rolloutPhaseArtworkSourceFileIdTypeAPI;
    private final RolloutPhaseArtworkSourceFileIdListTypeAPI rolloutPhaseArtworkSourceFileIdListTypeAPI;
    private final RolloutPhaseArtworkTypeAPI rolloutPhaseArtworkTypeAPI;
    private final RolloutPhaseLocalizedMetadataTypeAPI rolloutPhaseLocalizedMetadataTypeAPI;
    private final RolloutPhaseElementsTypeAPI rolloutPhaseElementsTypeAPI;
    private final RolloutPhaseTypeAPI rolloutPhaseTypeAPI;
    private final RolloutPhaseListTypeAPI rolloutPhaseListTypeAPI;
    private final RolloutTypeAPI rolloutTypeAPI;
    private final SetOfRightsAssetTypeAPI setOfRightsAssetTypeAPI;
    private final RightsAssetsTypeAPI rightsAssetsTypeAPI;
    private final SetOfStringTypeAPI setOfStringTypeAPI;
    private final SingleValuePassthroughMapTypeAPI singleValuePassthroughMapTypeAPI;
    private final PassthroughDataTypeAPI passthroughDataTypeAPI;
    private final ArtworkAttributesTypeAPI artworkAttributesTypeAPI;
    private final CharacterArtworkSourceTypeAPI characterArtworkSourceTypeAPI;
    private final IndividualSupplementalTypeAPI individualSupplementalTypeAPI;
    private final PersonArtworkSourceTypeAPI personArtworkSourceTypeAPI;
    private final StatusTypeAPI statusTypeAPI;
    private final StorageGroupsTypeAPI storageGroupsTypeAPI;
    private final StreamAssetTypeTypeAPI streamAssetTypeTypeAPI;
    private final StreamDeploymentInfoTypeAPI streamDeploymentInfoTypeAPI;
    private final StreamDeploymentLabelTypeAPI streamDeploymentLabelTypeAPI;
    private final StreamDeploymentLabelSetTypeAPI streamDeploymentLabelSetTypeAPI;
    private final StreamDeploymentTypeAPI streamDeploymentTypeAPI;
    private final StreamDrmInfoTypeAPI streamDrmInfoTypeAPI;
    private final StreamProfileGroupsTypeAPI streamProfileGroupsTypeAPI;
    private final StreamProfilesTypeAPI streamProfilesTypeAPI;
    private final SupplementalsListTypeAPI supplementalsListTypeAPI;
    private final SupplementalsTypeAPI supplementalsTypeAPI;
    private final TerritoryCountriesTypeAPI territoryCountriesTypeAPI;
    private final TextStreamInfoTypeAPI textStreamInfoTypeAPI;
    private final TopNAttributeTypeAPI topNAttributeTypeAPI;
    private final TopNAttributesSetTypeAPI topNAttributesSetTypeAPI;
    private final TopNTypeAPI topNTypeAPI;
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
    private final PersonCharacterResourceTypeAPI personCharacterResourceTypeAPI;
    private final PersonsTypeAPI personsTypeAPI;
    private final RatingsTypeAPI ratingsTypeAPI;
    private final ShowMemberTypesTypeAPI showMemberTypesTypeAPI;
    private final StoriesSynopsesTypeAPI storiesSynopsesTypeAPI;
    private final TurboCollectionsTypeAPI turboCollectionsTypeAPI;
    private final VMSAwardTypeAPI vMSAwardTypeAPI;
    private final VideoArtworkSourceTypeAPI videoArtworkSourceTypeAPI;
    private final VideoAwardMappingTypeAPI videoAwardMappingTypeAPI;
    private final VideoAwardListTypeAPI videoAwardListTypeAPI;
    private final VideoAwardTypeAPI videoAwardTypeAPI;
    private final VideoDateWindowTypeAPI videoDateWindowTypeAPI;
    private final VideoDateWindowListTypeAPI videoDateWindowListTypeAPI;
    private final VideoDateTypeAPI videoDateTypeAPI;
    private final VideoGeneralAliasTypeAPI videoGeneralAliasTypeAPI;
    private final VideoGeneralAliasListTypeAPI videoGeneralAliasListTypeAPI;
    private final VideoGeneralEpisodeTypeTypeAPI videoGeneralEpisodeTypeTypeAPI;
    private final VideoGeneralEpisodeTypeListTypeAPI videoGeneralEpisodeTypeListTypeAPI;
    private final VideoGeneralTitleTypeTypeAPI videoGeneralTitleTypeTypeAPI;
    private final VideoGeneralTitleTypeListTypeAPI videoGeneralTitleTypeListTypeAPI;
    private final VideoGeneralTypeAPI videoGeneralTypeAPI;
    private final VideoIdTypeAPI videoIdTypeAPI;
    private final ListOfVideoIdsTypeAPI listOfVideoIdsTypeAPI;
    private final PersonBioTypeAPI personBioTypeAPI;
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
    private final VideoStreamCropParamsTypeAPI videoStreamCropParamsTypeAPI;
    private final VideoStreamInfoTypeAPI videoStreamInfoTypeAPI;
    private final StreamNonImageInfoTypeAPI streamNonImageInfoTypeAPI;
    private final PackageStreamTypeAPI packageStreamTypeAPI;
    private final PackageStreamSetTypeAPI packageStreamSetTypeAPI;
    private final PackageTypeAPI packageTypeAPI;
    private final VideoTypeMediaTypeAPI videoTypeMediaTypeAPI;
    private final VideoTypeMediaListTypeAPI videoTypeMediaListTypeAPI;
    private final VideoTypeDescriptorTypeAPI videoTypeDescriptorTypeAPI;
    private final VideoTypeDescriptorSetTypeAPI videoTypeDescriptorSetTypeAPI;
    private final VideoTypeTypeAPI videoTypeTypeAPI;

    private final HollowObjectProvider characterQuoteProvider;
    private final HollowObjectProvider characterQuoteListProvider;
    private final HollowObjectProvider chunkDurationsStringProvider;
    private final HollowObjectProvider codecPrivateDataStringProvider;
    private final HollowObjectProvider dateProvider;
    private final HollowObjectProvider derivativeTagProvider;
    private final HollowObjectProvider downloadableIdProvider;
    private final HollowObjectProvider downloadableIdListProvider;
    private final HollowObjectProvider drmInfoStringProvider;
    private final HollowObjectProvider episodeProvider;
    private final HollowObjectProvider episodeListProvider;
    private final HollowObjectProvider explicitDateProvider;
    private final HollowObjectProvider iSOCountryProvider;
    private final HollowObjectProvider iSOCountryListProvider;
    private final HollowObjectProvider iSOCountrySetProvider;
    private final HollowObjectProvider listOfDerivativeTagProvider;
    private final HollowObjectProvider mapKeyProvider;
    private final HollowObjectProvider mapOfFlagsFirstDisplayDatesProvider;
    private final HollowObjectProvider flagsProvider;
    private final HollowObjectProvider personCharacterProvider;
    private final HollowObjectProvider characterListProvider;
    private final HollowObjectProvider movieCharacterPersonProvider;
    private final HollowObjectProvider personVideoAliasIdProvider;
    private final HollowObjectProvider personVideoAliasIdsListProvider;
    private final HollowObjectProvider personVideoRoleProvider;
    private final HollowObjectProvider personVideoRolesListProvider;
    private final HollowObjectProvider personVideoProvider;
    private final HollowObjectProvider rightsAssetSetIdProvider;
    private final HollowObjectProvider rightsContractPackageProvider;
    private final HollowObjectProvider listOfRightsContractPackageProvider;
    private final HollowObjectProvider rightsWindowContractProvider;
    private final HollowObjectProvider listOfRightsWindowContractProvider;
    private final HollowObjectProvider rightsWindowProvider;
    private final HollowObjectProvider listOfRightsWindowProvider;
    private final HollowObjectProvider rolloutPhaseWindowProvider;
    private final HollowObjectProvider rolloutPhaseWindowMapProvider;
    private final HollowObjectProvider seasonProvider;
    private final HollowObjectProvider seasonListProvider;
    private final HollowObjectProvider showMemberTypeProvider;
    private final HollowObjectProvider showMemberTypeListProvider;
    private final HollowObjectProvider showCountryLabelProvider;
    private final HollowObjectProvider showSeasonEpisodeProvider;
    private final HollowObjectProvider streamAssetMetadataProvider;
    private final HollowObjectProvider streamDimensionsProvider;
    private final HollowObjectProvider streamFileIdentificationProvider;
    private final HollowObjectProvider streamProfileIdProvider;
    private final HollowObjectProvider streamProfileIdListProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider absoluteScheduleProvider;
    private final HollowObjectProvider artWorkImageTypeProvider;
    private final HollowObjectProvider artworkRecipeProvider;
    private final HollowObjectProvider audioStreamInfoProvider;
    private final HollowObjectProvider cSMReviewProvider;
    private final HollowObjectProvider cacheDeploymentIntentProvider;
    private final HollowObjectProvider cdnProvider;
    private final HollowObjectProvider cdnDeploymentProvider;
    private final HollowObjectProvider cdnDeploymentSetProvider;
    private final HollowObjectProvider certificationSystemRatingProvider;
    private final HollowObjectProvider certificationSystemRatingListProvider;
    private final HollowObjectProvider certificationSystemProvider;
    private final HollowObjectProvider characterElementsProvider;
    private final HollowObjectProvider characterProvider;
    private final HollowObjectProvider damMerchStillsMomentProvider;
    private final HollowObjectProvider damMerchStillsProvider;
    private final HollowObjectProvider disallowedSubtitleLangCodeProvider;
    private final HollowObjectProvider disallowedSubtitleLangCodesListProvider;
    private final HollowObjectProvider disallowedAssetBundleProvider;
    private final HollowObjectProvider disallowedAssetBundlesListProvider;
    private final HollowObjectProvider contractProvider;
    private final HollowObjectProvider drmHeaderInfoProvider;
    private final HollowObjectProvider drmHeaderInfoListProvider;
    private final HollowObjectProvider drmSystemIdentifiersProvider;
    private final HollowObjectProvider iPLArtworkDerivativeProvider;
    private final HollowObjectProvider iPLDerivativeSetProvider;
    private final HollowObjectProvider iPLDerivativeGroupProvider;
    private final HollowObjectProvider iPLDerivativeGroupSetProvider;
    private final HollowObjectProvider iPLArtworkDerivativeSetProvider;
    private final HollowObjectProvider imageStreamInfoProvider;
    private final HollowObjectProvider listOfContractProvider;
    private final HollowObjectProvider contractsProvider;
    private final HollowObjectProvider listOfPackageTagsProvider;
    private final HollowObjectProvider deployablePackagesProvider;
    private final HollowObjectProvider listOfStringProvider;
    private final HollowObjectProvider localeTerritoryCodeProvider;
    private final HollowObjectProvider localeTerritoryCodeListProvider;
    private final HollowObjectProvider artworkLocaleProvider;
    private final HollowObjectProvider artworkLocaleListProvider;
    private final HollowObjectProvider masterScheduleProvider;
    private final HollowObjectProvider multiValuePassthroughMapProvider;
    private final HollowObjectProvider originServerProvider;
    private final HollowObjectProvider overrideScheduleProvider;
    private final HollowObjectProvider packageDrmInfoProvider;
    private final HollowObjectProvider packageDrmInfoListProvider;
    private final HollowObjectProvider packageMomentProvider;
    private final HollowObjectProvider packageMomentListProvider;
    private final HollowObjectProvider phaseTagProvider;
    private final HollowObjectProvider phaseTagListProvider;
    private final HollowObjectProvider protectionTypesProvider;
    private final HollowObjectProvider releaseDateProvider;
    private final HollowObjectProvider listOfReleaseDatesProvider;
    private final HollowObjectProvider rightsAssetProvider;
    private final HollowObjectProvider rightsContractAssetProvider;
    private final HollowObjectProvider listOfRightsContractAssetProvider;
    private final HollowObjectProvider rightsContractProvider;
    private final HollowObjectProvider listOfRightsContractProvider;
    private final HollowObjectProvider rightsProvider;
    private final HollowObjectProvider rolloutPhaseArtworkSourceFileIdProvider;
    private final HollowObjectProvider rolloutPhaseArtworkSourceFileIdListProvider;
    private final HollowObjectProvider rolloutPhaseArtworkProvider;
    private final HollowObjectProvider rolloutPhaseLocalizedMetadataProvider;
    private final HollowObjectProvider rolloutPhaseElementsProvider;
    private final HollowObjectProvider rolloutPhaseProvider;
    private final HollowObjectProvider rolloutPhaseListProvider;
    private final HollowObjectProvider rolloutProvider;
    private final HollowObjectProvider setOfRightsAssetProvider;
    private final HollowObjectProvider rightsAssetsProvider;
    private final HollowObjectProvider setOfStringProvider;
    private final HollowObjectProvider singleValuePassthroughMapProvider;
    private final HollowObjectProvider passthroughDataProvider;
    private final HollowObjectProvider artworkAttributesProvider;
    private final HollowObjectProvider characterArtworkSourceProvider;
    private final HollowObjectProvider individualSupplementalProvider;
    private final HollowObjectProvider personArtworkSourceProvider;
    private final HollowObjectProvider statusProvider;
    private final HollowObjectProvider storageGroupsProvider;
    private final HollowObjectProvider streamAssetTypeProvider;
    private final HollowObjectProvider streamDeploymentInfoProvider;
    private final HollowObjectProvider streamDeploymentLabelProvider;
    private final HollowObjectProvider streamDeploymentLabelSetProvider;
    private final HollowObjectProvider streamDeploymentProvider;
    private final HollowObjectProvider streamDrmInfoProvider;
    private final HollowObjectProvider streamProfileGroupsProvider;
    private final HollowObjectProvider streamProfilesProvider;
    private final HollowObjectProvider supplementalsListProvider;
    private final HollowObjectProvider supplementalsProvider;
    private final HollowObjectProvider territoryCountriesProvider;
    private final HollowObjectProvider textStreamInfoProvider;
    private final HollowObjectProvider topNAttributeProvider;
    private final HollowObjectProvider topNAttributesSetProvider;
    private final HollowObjectProvider topNProvider;
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
    private final HollowObjectProvider personCharacterResourceProvider;
    private final HollowObjectProvider personsProvider;
    private final HollowObjectProvider ratingsProvider;
    private final HollowObjectProvider showMemberTypesProvider;
    private final HollowObjectProvider storiesSynopsesProvider;
    private final HollowObjectProvider turboCollectionsProvider;
    private final HollowObjectProvider vMSAwardProvider;
    private final HollowObjectProvider videoArtworkSourceProvider;
    private final HollowObjectProvider videoAwardMappingProvider;
    private final HollowObjectProvider videoAwardListProvider;
    private final HollowObjectProvider videoAwardProvider;
    private final HollowObjectProvider videoDateWindowProvider;
    private final HollowObjectProvider videoDateWindowListProvider;
    private final HollowObjectProvider videoDateProvider;
    private final HollowObjectProvider videoGeneralAliasProvider;
    private final HollowObjectProvider videoGeneralAliasListProvider;
    private final HollowObjectProvider videoGeneralEpisodeTypeProvider;
    private final HollowObjectProvider videoGeneralEpisodeTypeListProvider;
    private final HollowObjectProvider videoGeneralTitleTypeProvider;
    private final HollowObjectProvider videoGeneralTitleTypeListProvider;
    private final HollowObjectProvider videoGeneralProvider;
    private final HollowObjectProvider videoIdProvider;
    private final HollowObjectProvider listOfVideoIdsProvider;
    private final HollowObjectProvider personBioProvider;
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
    private final HollowObjectProvider videoStreamCropParamsProvider;
    private final HollowObjectProvider videoStreamInfoProvider;
    private final HollowObjectProvider streamNonImageInfoProvider;
    private final HollowObjectProvider packageStreamProvider;
    private final HollowObjectProvider packageStreamSetProvider;
    private final HollowObjectProvider packageProvider;
    private final HollowObjectProvider videoTypeMediaProvider;
    private final HollowObjectProvider videoTypeMediaListProvider;
    private final HollowObjectProvider videoTypeDescriptorProvider;
    private final HollowObjectProvider videoTypeDescriptorSetProvider;
    private final HollowObjectProvider videoTypeProvider;

    public VMSHollowInputAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public VMSHollowInputAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public VMSHollowInputAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public VMSHollowInputAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, VMSHollowInputAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("CharacterQuote","CharacterQuoteList","ChunkDurationsString","CodecPrivateDataString","Date","DerivativeTag","DownloadableId","DownloadableIdList","DrmInfoString","Episode","EpisodeList","ExplicitDate","ISOCountry","ISOCountryList","ISOCountrySet","ListOfDerivativeTag","MapKey","MapOfFlagsFirstDisplayDates","Flags","PersonCharacter","CharacterList","MovieCharacterPerson","PersonVideoAliasId","PersonVideoAliasIdsList","PersonVideoRole","PersonVideoRolesList","PersonVideo","RightsAssetSetId","RightsContractPackage","ListOfRightsContractPackage","RightsWindowContract","ListOfRightsWindowContract","RightsWindow","ListOfRightsWindow","RolloutPhaseWindow","RolloutPhaseWindowMap","Season","SeasonList","ShowMemberType","ShowMemberTypeList","ShowCountryLabel","ShowSeasonEpisode","StreamAssetMetadata","StreamDimensions","StreamFileIdentification","StreamProfileId","StreamProfileIdList","String","AbsoluteSchedule","ArtWorkImageType","ArtworkRecipe","AudioStreamInfo","CSMReview","CacheDeploymentIntent","Cdn","CdnDeployment","CdnDeploymentSet","CertificationSystemRating","CertificationSystemRatingList","CertificationSystem","CharacterElements","Character","DamMerchStillsMoment","DamMerchStills","DisallowedSubtitleLangCode","DisallowedSubtitleLangCodesList","DisallowedAssetBundle","DisallowedAssetBundlesList","Contract","DrmHeaderInfo","DrmHeaderInfoList","DrmSystemIdentifiers","IPLArtworkDerivative","IPLDerivativeSet","IPLDerivativeGroup","IPLDerivativeGroupSet","IPLArtworkDerivativeSet","ImageStreamInfo","ListOfContract","Contracts","ListOfPackageTags","DeployablePackages","ListOfString","LocaleTerritoryCode","LocaleTerritoryCodeList","ArtworkLocale","ArtworkLocaleList","MasterSchedule","MultiValuePassthroughMap","OriginServer","OverrideSchedule","PackageDrmInfo","PackageDrmInfoList","PackageMoment","PackageMomentList","PhaseTag","PhaseTagList","ProtectionTypes","ReleaseDate","ListOfReleaseDates","RightsAsset","RightsContractAsset","ListOfRightsContractAsset","RightsContract","ListOfRightsContract","Rights","RolloutPhaseArtworkSourceFileId","RolloutPhaseArtworkSourceFileIdList","RolloutPhaseArtwork","RolloutPhaseLocalizedMetadata","RolloutPhaseElements","RolloutPhase","RolloutPhaseList","Rollout","SetOfRightsAsset","RightsAssets","SetOfString","SingleValuePassthroughMap","PassthroughData","ArtworkAttributes","CharacterArtworkSource","IndividualSupplemental","PersonArtworkSource","Status","StorageGroups","StreamAssetType","StreamDeploymentInfo","StreamDeploymentLabel","StreamDeploymentLabelSet","StreamDeployment","StreamDrmInfo","StreamProfileGroups","StreamProfiles","SupplementalsList","Supplementals","TerritoryCountries","TextStreamInfo","TopNAttribute","TopNAttributesSet","TopN","TranslatedTextValue","MapOfTranslatedText","AltGenresAlternateNames","AltGenresAlternateNamesList","LocalizedCharacter","LocalizedMetadata","StoriesSynopsesHook","StoriesSynopsesHookList","TranslatedText","AltGenres","AssetMetaDatas","Awards","Categories","CategoryGroups","Certifications","Characters","ConsolidatedCertSystemRating","ConsolidatedCertSystemRatingList","ConsolidatedCertificationSystems","Episodes","Festivals","Languages","MovieRatings","Movies","PersonAliases","PersonCharacterResource","Persons","Ratings","ShowMemberTypes","StoriesSynopses","TurboCollections","VMSAward","VideoArtworkSource","VideoAwardMapping","VideoAwardList","VideoAward","VideoDateWindow","VideoDateWindowList","VideoDate","VideoGeneralAlias","VideoGeneralAliasList","VideoGeneralEpisodeType","VideoGeneralEpisodeTypeList","VideoGeneralTitleType","VideoGeneralTitleTypeList","VideoGeneral","VideoId","ListOfVideoIds","PersonBio","VideoRatingAdvisoryId","VideoRatingAdvisoryIdList","VideoRatingAdvisories","ConsolidatedVideoCountryRating","ConsolidatedVideoCountryRatingList","ConsolidatedVideoRating","ConsolidatedVideoRatingList","ConsolidatedVideoRatings","VideoRatingRatingReasonIds","VideoRatingRatingReasonArrayOfIds","VideoRatingRatingReason","VideoRatingRating","VideoRatingArrayOfRating","VideoRating","VideoStreamCropParams","VideoStreamInfo","StreamNonImageInfo","PackageStream","PackageStreamSet","Package","VideoTypeMedia","VideoTypeMediaList","VideoTypeDescriptor","VideoTypeDescriptorSet","VideoType");

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

        typeDataAccess = dataAccess.getTypeDataAccess("DerivativeTag");
        if(typeDataAccess != null) {
            derivativeTagTypeAPI = new DerivativeTagTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            derivativeTagTypeAPI = new DerivativeTagTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DerivativeTag"));
        }
        addTypeAPI(derivativeTagTypeAPI);
        factory = factoryOverrides.get("DerivativeTag");
        if(factory == null)
            factory = new DerivativeTagHollowFactory();
        if(cachedTypes.contains("DerivativeTag")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.derivativeTagProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.derivativeTagProvider;
            derivativeTagProvider = new HollowObjectCacheProvider(typeDataAccess, derivativeTagTypeAPI, factory, previousCacheProvider);
        } else {
            derivativeTagProvider = new HollowObjectFactoryProvider(typeDataAccess, derivativeTagTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ExplicitDate");
        if(typeDataAccess != null) {
            explicitDateTypeAPI = new ExplicitDateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            explicitDateTypeAPI = new ExplicitDateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ExplicitDate"));
        }
        addTypeAPI(explicitDateTypeAPI);
        factory = factoryOverrides.get("ExplicitDate");
        if(factory == null)
            factory = new ExplicitDateHollowFactory();
        if(cachedTypes.contains("ExplicitDate")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.explicitDateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.explicitDateProvider;
            explicitDateProvider = new HollowObjectCacheProvider(typeDataAccess, explicitDateTypeAPI, factory, previousCacheProvider);
        } else {
            explicitDateProvider = new HollowObjectFactoryProvider(typeDataAccess, explicitDateTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfDerivativeTag");
        if(typeDataAccess != null) {
            listOfDerivativeTagTypeAPI = new ListOfDerivativeTagTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfDerivativeTagTypeAPI = new ListOfDerivativeTagTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfDerivativeTag"));
        }
        addTypeAPI(listOfDerivativeTagTypeAPI);
        factory = factoryOverrides.get("ListOfDerivativeTag");
        if(factory == null)
            factory = new ListOfDerivativeTagHollowFactory();
        if(cachedTypes.contains("ListOfDerivativeTag")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfDerivativeTagProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfDerivativeTagProvider;
            listOfDerivativeTagProvider = new HollowObjectCacheProvider(typeDataAccess, listOfDerivativeTagTypeAPI, factory, previousCacheProvider);
        } else {
            listOfDerivativeTagProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfDerivativeTagTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfFlagsFirstDisplayDates");
        if(typeDataAccess != null) {
            mapOfFlagsFirstDisplayDatesTypeAPI = new MapOfFlagsFirstDisplayDatesTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfFlagsFirstDisplayDatesTypeAPI = new MapOfFlagsFirstDisplayDatesTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfFlagsFirstDisplayDates"));
        }
        addTypeAPI(mapOfFlagsFirstDisplayDatesTypeAPI);
        factory = factoryOverrides.get("MapOfFlagsFirstDisplayDates");
        if(factory == null)
            factory = new MapOfFlagsFirstDisplayDatesHollowFactory();
        if(cachedTypes.contains("MapOfFlagsFirstDisplayDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfFlagsFirstDisplayDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfFlagsFirstDisplayDatesProvider;
            mapOfFlagsFirstDisplayDatesProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfFlagsFirstDisplayDatesTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfFlagsFirstDisplayDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfFlagsFirstDisplayDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Flags");
        if(typeDataAccess != null) {
            flagsTypeAPI = new FlagsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            flagsTypeAPI = new FlagsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Flags"));
        }
        addTypeAPI(flagsTypeAPI);
        factory = factoryOverrides.get("Flags");
        if(factory == null)
            factory = new FlagsHollowFactory();
        if(cachedTypes.contains("Flags")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.flagsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.flagsProvider;
            flagsProvider = new HollowObjectCacheProvider(typeDataAccess, flagsTypeAPI, factory, previousCacheProvider);
        } else {
            flagsProvider = new HollowObjectFactoryProvider(typeDataAccess, flagsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonCharacter");
        if(typeDataAccess != null) {
            personCharacterTypeAPI = new PersonCharacterTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personCharacterTypeAPI = new PersonCharacterTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonCharacter"));
        }
        addTypeAPI(personCharacterTypeAPI);
        factory = factoryOverrides.get("PersonCharacter");
        if(factory == null)
            factory = new PersonCharacterHollowFactory();
        if(cachedTypes.contains("PersonCharacter")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personCharacterProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personCharacterProvider;
            personCharacterProvider = new HollowObjectCacheProvider(typeDataAccess, personCharacterTypeAPI, factory, previousCacheProvider);
        } else {
            personCharacterProvider = new HollowObjectFactoryProvider(typeDataAccess, personCharacterTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterList");
        if(typeDataAccess != null) {
            characterListTypeAPI = new CharacterListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            characterListTypeAPI = new CharacterListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "CharacterList"));
        }
        addTypeAPI(characterListTypeAPI);
        factory = factoryOverrides.get("CharacterList");
        if(factory == null)
            factory = new CharacterListHollowFactory();
        if(cachedTypes.contains("CharacterList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterListProvider;
            characterListProvider = new HollowObjectCacheProvider(typeDataAccess, characterListTypeAPI, factory, previousCacheProvider);
        } else {
            characterListProvider = new HollowObjectFactoryProvider(typeDataAccess, characterListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieCharacterPerson");
        if(typeDataAccess != null) {
            movieCharacterPersonTypeAPI = new MovieCharacterPersonTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieCharacterPersonTypeAPI = new MovieCharacterPersonTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieCharacterPerson"));
        }
        addTypeAPI(movieCharacterPersonTypeAPI);
        factory = factoryOverrides.get("MovieCharacterPerson");
        if(factory == null)
            factory = new MovieCharacterPersonHollowFactory();
        if(cachedTypes.contains("MovieCharacterPerson")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieCharacterPersonProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieCharacterPersonProvider;
            movieCharacterPersonProvider = new HollowObjectCacheProvider(typeDataAccess, movieCharacterPersonTypeAPI, factory, previousCacheProvider);
        } else {
            movieCharacterPersonProvider = new HollowObjectFactoryProvider(typeDataAccess, movieCharacterPersonTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoAliasId");
        if(typeDataAccess != null) {
            personVideoAliasIdTypeAPI = new PersonVideoAliasIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personVideoAliasIdTypeAPI = new PersonVideoAliasIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonVideoAliasId"));
        }
        addTypeAPI(personVideoAliasIdTypeAPI);
        factory = factoryOverrides.get("PersonVideoAliasId");
        if(factory == null)
            factory = new PersonVideoAliasIdHollowFactory();
        if(cachedTypes.contains("PersonVideoAliasId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoAliasIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoAliasIdProvider;
            personVideoAliasIdProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoAliasIdTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoAliasIdProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoAliasIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoAliasIdsList");
        if(typeDataAccess != null) {
            personVideoAliasIdsListTypeAPI = new PersonVideoAliasIdsListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personVideoAliasIdsListTypeAPI = new PersonVideoAliasIdsListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonVideoAliasIdsList"));
        }
        addTypeAPI(personVideoAliasIdsListTypeAPI);
        factory = factoryOverrides.get("PersonVideoAliasIdsList");
        if(factory == null)
            factory = new PersonVideoAliasIdsListHollowFactory();
        if(cachedTypes.contains("PersonVideoAliasIdsList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoAliasIdsListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoAliasIdsListProvider;
            personVideoAliasIdsListProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoAliasIdsListTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoAliasIdsListProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoAliasIdsListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoRole");
        if(typeDataAccess != null) {
            personVideoRoleTypeAPI = new PersonVideoRoleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personVideoRoleTypeAPI = new PersonVideoRoleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonVideoRole"));
        }
        addTypeAPI(personVideoRoleTypeAPI);
        factory = factoryOverrides.get("PersonVideoRole");
        if(factory == null)
            factory = new PersonVideoRoleHollowFactory();
        if(cachedTypes.contains("PersonVideoRole")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoRoleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoRoleProvider;
            personVideoRoleProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoRoleTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoRoleProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoRoleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoRolesList");
        if(typeDataAccess != null) {
            personVideoRolesListTypeAPI = new PersonVideoRolesListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personVideoRolesListTypeAPI = new PersonVideoRolesListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonVideoRolesList"));
        }
        addTypeAPI(personVideoRolesListTypeAPI);
        factory = factoryOverrides.get("PersonVideoRolesList");
        if(factory == null)
            factory = new PersonVideoRolesListHollowFactory();
        if(cachedTypes.contains("PersonVideoRolesList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoRolesListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoRolesListProvider;
            personVideoRolesListProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoRolesListTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoRolesListProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoRolesListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideo");
        if(typeDataAccess != null) {
            personVideoTypeAPI = new PersonVideoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personVideoTypeAPI = new PersonVideoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonVideo"));
        }
        addTypeAPI(personVideoTypeAPI);
        factory = factoryOverrides.get("PersonVideo");
        if(factory == null)
            factory = new PersonVideoHollowFactory();
        if(cachedTypes.contains("PersonVideo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoProvider;
            personVideoProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsAssetSetId");
        if(typeDataAccess != null) {
            rightsAssetSetIdTypeAPI = new RightsAssetSetIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsAssetSetIdTypeAPI = new RightsAssetSetIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsAssetSetId"));
        }
        addTypeAPI(rightsAssetSetIdTypeAPI);
        factory = factoryOverrides.get("RightsAssetSetId");
        if(factory == null)
            factory = new RightsAssetSetIdHollowFactory();
        if(cachedTypes.contains("RightsAssetSetId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsAssetSetIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsAssetSetIdProvider;
            rightsAssetSetIdProvider = new HollowObjectCacheProvider(typeDataAccess, rightsAssetSetIdTypeAPI, factory, previousCacheProvider);
        } else {
            rightsAssetSetIdProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsAssetSetIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsContractPackage");
        if(typeDataAccess != null) {
            rightsContractPackageTypeAPI = new RightsContractPackageTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsContractPackageTypeAPI = new RightsContractPackageTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsContractPackage"));
        }
        addTypeAPI(rightsContractPackageTypeAPI);
        factory = factoryOverrides.get("RightsContractPackage");
        if(factory == null)
            factory = new RightsContractPackageHollowFactory();
        if(cachedTypes.contains("RightsContractPackage")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsContractPackageProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsContractPackageProvider;
            rightsContractPackageProvider = new HollowObjectCacheProvider(typeDataAccess, rightsContractPackageTypeAPI, factory, previousCacheProvider);
        } else {
            rightsContractPackageProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsContractPackageTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsContractPackage");
        if(typeDataAccess != null) {
            listOfRightsContractPackageTypeAPI = new ListOfRightsContractPackageTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsContractPackageTypeAPI = new ListOfRightsContractPackageTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsContractPackage"));
        }
        addTypeAPI(listOfRightsContractPackageTypeAPI);
        factory = factoryOverrides.get("ListOfRightsContractPackage");
        if(factory == null)
            factory = new ListOfRightsContractPackageHollowFactory();
        if(cachedTypes.contains("ListOfRightsContractPackage")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsContractPackageProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsContractPackageProvider;
            listOfRightsContractPackageProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsContractPackageTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsContractPackageProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsContractPackageTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsWindowContract");
        if(typeDataAccess != null) {
            rightsWindowContractTypeAPI = new RightsWindowContractTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsWindowContractTypeAPI = new RightsWindowContractTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsWindowContract"));
        }
        addTypeAPI(rightsWindowContractTypeAPI);
        factory = factoryOverrides.get("RightsWindowContract");
        if(factory == null)
            factory = new RightsWindowContractHollowFactory();
        if(cachedTypes.contains("RightsWindowContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsWindowContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsWindowContractProvider;
            rightsWindowContractProvider = new HollowObjectCacheProvider(typeDataAccess, rightsWindowContractTypeAPI, factory, previousCacheProvider);
        } else {
            rightsWindowContractProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsWindowContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsWindowContract");
        if(typeDataAccess != null) {
            listOfRightsWindowContractTypeAPI = new ListOfRightsWindowContractTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsWindowContractTypeAPI = new ListOfRightsWindowContractTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsWindowContract"));
        }
        addTypeAPI(listOfRightsWindowContractTypeAPI);
        factory = factoryOverrides.get("ListOfRightsWindowContract");
        if(factory == null)
            factory = new ListOfRightsWindowContractHollowFactory();
        if(cachedTypes.contains("ListOfRightsWindowContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsWindowContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsWindowContractProvider;
            listOfRightsWindowContractProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsWindowContractTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsWindowContractProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsWindowContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsWindow");
        if(typeDataAccess != null) {
            rightsWindowTypeAPI = new RightsWindowTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsWindowTypeAPI = new RightsWindowTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsWindow"));
        }
        addTypeAPI(rightsWindowTypeAPI);
        factory = factoryOverrides.get("RightsWindow");
        if(factory == null)
            factory = new RightsWindowHollowFactory();
        if(cachedTypes.contains("RightsWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsWindowProvider;
            rightsWindowProvider = new HollowObjectCacheProvider(typeDataAccess, rightsWindowTypeAPI, factory, previousCacheProvider);
        } else {
            rightsWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsWindow");
        if(typeDataAccess != null) {
            listOfRightsWindowTypeAPI = new ListOfRightsWindowTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsWindowTypeAPI = new ListOfRightsWindowTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsWindow"));
        }
        addTypeAPI(listOfRightsWindowTypeAPI);
        factory = factoryOverrides.get("ListOfRightsWindow");
        if(factory == null)
            factory = new ListOfRightsWindowHollowFactory();
        if(cachedTypes.contains("ListOfRightsWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsWindowProvider;
            listOfRightsWindowProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsWindowTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsWindowTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberType");
        if(typeDataAccess != null) {
            showMemberTypeTypeAPI = new ShowMemberTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypeTypeAPI = new ShowMemberTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowMemberType"));
        }
        addTypeAPI(showMemberTypeTypeAPI);
        factory = factoryOverrides.get("ShowMemberType");
        if(factory == null)
            factory = new ShowMemberTypeHollowFactory();
        if(cachedTypes.contains("ShowMemberType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypeProvider;
            showMemberTypeProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypeTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberTypeList");
        if(typeDataAccess != null) {
            showMemberTypeListTypeAPI = new ShowMemberTypeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypeListTypeAPI = new ShowMemberTypeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ShowMemberTypeList"));
        }
        addTypeAPI(showMemberTypeListTypeAPI);
        factory = factoryOverrides.get("ShowMemberTypeList");
        if(factory == null)
            factory = new ShowMemberTypeListHollowFactory();
        if(cachedTypes.contains("ShowMemberTypeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypeListProvider;
            showMemberTypeListProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypeListTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypeListProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowCountryLabel");
        if(typeDataAccess != null) {
            showCountryLabelTypeAPI = new ShowCountryLabelTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showCountryLabelTypeAPI = new ShowCountryLabelTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowCountryLabel"));
        }
        addTypeAPI(showCountryLabelTypeAPI);
        factory = factoryOverrides.get("ShowCountryLabel");
        if(factory == null)
            factory = new ShowCountryLabelHollowFactory();
        if(cachedTypes.contains("ShowCountryLabel")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showCountryLabelProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showCountryLabelProvider;
            showCountryLabelProvider = new HollowObjectCacheProvider(typeDataAccess, showCountryLabelTypeAPI, factory, previousCacheProvider);
        } else {
            showCountryLabelProvider = new HollowObjectFactoryProvider(typeDataAccess, showCountryLabelTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowSeasonEpisode");
        if(typeDataAccess != null) {
            showSeasonEpisodeTypeAPI = new ShowSeasonEpisodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showSeasonEpisodeTypeAPI = new ShowSeasonEpisodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowSeasonEpisode"));
        }
        addTypeAPI(showSeasonEpisodeTypeAPI);
        factory = factoryOverrides.get("ShowSeasonEpisode");
        if(factory == null)
            factory = new ShowSeasonEpisodeHollowFactory();
        if(cachedTypes.contains("ShowSeasonEpisode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showSeasonEpisodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showSeasonEpisodeProvider;
            showSeasonEpisodeProvider = new HollowObjectCacheProvider(typeDataAccess, showSeasonEpisodeTypeAPI, factory, previousCacheProvider);
        } else {
            showSeasonEpisodeProvider = new HollowObjectFactoryProvider(typeDataAccess, showSeasonEpisodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("StreamAssetMetadata");
        if(typeDataAccess != null) {
            streamAssetMetadataTypeAPI = new StreamAssetMetadataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            streamAssetMetadataTypeAPI = new StreamAssetMetadataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StreamAssetMetadata"));
        }
        addTypeAPI(streamAssetMetadataTypeAPI);
        factory = factoryOverrides.get("StreamAssetMetadata");
        if(factory == null)
            factory = new StreamAssetMetadataHollowFactory();
        if(cachedTypes.contains("StreamAssetMetadata")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.streamAssetMetadataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.streamAssetMetadataProvider;
            streamAssetMetadataProvider = new HollowObjectCacheProvider(typeDataAccess, streamAssetMetadataTypeAPI, factory, previousCacheProvider);
        } else {
            streamAssetMetadataProvider = new HollowObjectFactoryProvider(typeDataAccess, streamAssetMetadataTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("AbsoluteSchedule");
        if(typeDataAccess != null) {
            absoluteScheduleTypeAPI = new AbsoluteScheduleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            absoluteScheduleTypeAPI = new AbsoluteScheduleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AbsoluteSchedule"));
        }
        addTypeAPI(absoluteScheduleTypeAPI);
        factory = factoryOverrides.get("AbsoluteSchedule");
        if(factory == null)
            factory = new AbsoluteScheduleHollowFactory();
        if(cachedTypes.contains("AbsoluteSchedule")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.absoluteScheduleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.absoluteScheduleProvider;
            absoluteScheduleProvider = new HollowObjectCacheProvider(typeDataAccess, absoluteScheduleTypeAPI, factory, previousCacheProvider);
        } else {
            absoluteScheduleProvider = new HollowObjectFactoryProvider(typeDataAccess, absoluteScheduleTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("Cdn");
        if(typeDataAccess != null) {
            cdnTypeAPI = new CdnTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            cdnTypeAPI = new CdnTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Cdn"));
        }
        addTypeAPI(cdnTypeAPI);
        factory = factoryOverrides.get("Cdn");
        if(factory == null)
            factory = new CdnHollowFactory();
        if(cachedTypes.contains("Cdn")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.cdnProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.cdnProvider;
            cdnProvider = new HollowObjectCacheProvider(typeDataAccess, cdnTypeAPI, factory, previousCacheProvider);
        } else {
            cdnProvider = new HollowObjectFactoryProvider(typeDataAccess, cdnTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("DamMerchStillsMoment");
        if(typeDataAccess != null) {
            damMerchStillsMomentTypeAPI = new DamMerchStillsMomentTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            damMerchStillsMomentTypeAPI = new DamMerchStillsMomentTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DamMerchStillsMoment"));
        }
        addTypeAPI(damMerchStillsMomentTypeAPI);
        factory = factoryOverrides.get("DamMerchStillsMoment");
        if(factory == null)
            factory = new DamMerchStillsMomentHollowFactory();
        if(cachedTypes.contains("DamMerchStillsMoment")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.damMerchStillsMomentProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.damMerchStillsMomentProvider;
            damMerchStillsMomentProvider = new HollowObjectCacheProvider(typeDataAccess, damMerchStillsMomentTypeAPI, factory, previousCacheProvider);
        } else {
            damMerchStillsMomentProvider = new HollowObjectFactoryProvider(typeDataAccess, damMerchStillsMomentTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DamMerchStills");
        if(typeDataAccess != null) {
            damMerchStillsTypeAPI = new DamMerchStillsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            damMerchStillsTypeAPI = new DamMerchStillsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DamMerchStills"));
        }
        addTypeAPI(damMerchStillsTypeAPI);
        factory = factoryOverrides.get("DamMerchStills");
        if(factory == null)
            factory = new DamMerchStillsHollowFactory();
        if(cachedTypes.contains("DamMerchStills")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.damMerchStillsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.damMerchStillsProvider;
            damMerchStillsProvider = new HollowObjectCacheProvider(typeDataAccess, damMerchStillsTypeAPI, factory, previousCacheProvider);
        } else {
            damMerchStillsProvider = new HollowObjectFactoryProvider(typeDataAccess, damMerchStillsTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("Contract");
        if(typeDataAccess != null) {
            contractTypeAPI = new ContractTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            contractTypeAPI = new ContractTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Contract"));
        }
        addTypeAPI(contractTypeAPI);
        factory = factoryOverrides.get("Contract");
        if(factory == null)
            factory = new ContractHollowFactory();
        if(cachedTypes.contains("Contract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.contractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.contractProvider;
            contractProvider = new HollowObjectCacheProvider(typeDataAccess, contractTypeAPI, factory, previousCacheProvider);
        } else {
            contractProvider = new HollowObjectFactoryProvider(typeDataAccess, contractTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("IPLArtworkDerivative");
        if(typeDataAccess != null) {
            iPLArtworkDerivativeTypeAPI = new IPLArtworkDerivativeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iPLArtworkDerivativeTypeAPI = new IPLArtworkDerivativeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IPLArtworkDerivative"));
        }
        addTypeAPI(iPLArtworkDerivativeTypeAPI);
        factory = factoryOverrides.get("IPLArtworkDerivative");
        if(factory == null)
            factory = new IPLArtworkDerivativeHollowFactory();
        if(cachedTypes.contains("IPLArtworkDerivative")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLArtworkDerivativeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLArtworkDerivativeProvider;
            iPLArtworkDerivativeProvider = new HollowObjectCacheProvider(typeDataAccess, iPLArtworkDerivativeTypeAPI, factory, previousCacheProvider);
        } else {
            iPLArtworkDerivativeProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLArtworkDerivativeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLDerivativeSet");
        if(typeDataAccess != null) {
            iPLDerivativeSetTypeAPI = new IPLDerivativeSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            iPLDerivativeSetTypeAPI = new IPLDerivativeSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "IPLDerivativeSet"));
        }
        addTypeAPI(iPLDerivativeSetTypeAPI);
        factory = factoryOverrides.get("IPLDerivativeSet");
        if(factory == null)
            factory = new IPLDerivativeSetHollowFactory();
        if(cachedTypes.contains("IPLDerivativeSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLDerivativeSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLDerivativeSetProvider;
            iPLDerivativeSetProvider = new HollowObjectCacheProvider(typeDataAccess, iPLDerivativeSetTypeAPI, factory, previousCacheProvider);
        } else {
            iPLDerivativeSetProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLDerivativeSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLDerivativeGroup");
        if(typeDataAccess != null) {
            iPLDerivativeGroupTypeAPI = new IPLDerivativeGroupTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iPLDerivativeGroupTypeAPI = new IPLDerivativeGroupTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IPLDerivativeGroup"));
        }
        addTypeAPI(iPLDerivativeGroupTypeAPI);
        factory = factoryOverrides.get("IPLDerivativeGroup");
        if(factory == null)
            factory = new IPLDerivativeGroupHollowFactory();
        if(cachedTypes.contains("IPLDerivativeGroup")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLDerivativeGroupProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLDerivativeGroupProvider;
            iPLDerivativeGroupProvider = new HollowObjectCacheProvider(typeDataAccess, iPLDerivativeGroupTypeAPI, factory, previousCacheProvider);
        } else {
            iPLDerivativeGroupProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLDerivativeGroupTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLDerivativeGroupSet");
        if(typeDataAccess != null) {
            iPLDerivativeGroupSetTypeAPI = new IPLDerivativeGroupSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            iPLDerivativeGroupSetTypeAPI = new IPLDerivativeGroupSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "IPLDerivativeGroupSet"));
        }
        addTypeAPI(iPLDerivativeGroupSetTypeAPI);
        factory = factoryOverrides.get("IPLDerivativeGroupSet");
        if(factory == null)
            factory = new IPLDerivativeGroupSetHollowFactory();
        if(cachedTypes.contains("IPLDerivativeGroupSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLDerivativeGroupSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLDerivativeGroupSetProvider;
            iPLDerivativeGroupSetProvider = new HollowObjectCacheProvider(typeDataAccess, iPLDerivativeGroupSetTypeAPI, factory, previousCacheProvider);
        } else {
            iPLDerivativeGroupSetProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLDerivativeGroupSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLArtworkDerivativeSet");
        if(typeDataAccess != null) {
            iPLArtworkDerivativeSetTypeAPI = new IPLArtworkDerivativeSetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iPLArtworkDerivativeSetTypeAPI = new IPLArtworkDerivativeSetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IPLArtworkDerivativeSet"));
        }
        addTypeAPI(iPLArtworkDerivativeSetTypeAPI);
        factory = factoryOverrides.get("IPLArtworkDerivativeSet");
        if(factory == null)
            factory = new IPLArtworkDerivativeSetHollowFactory();
        if(cachedTypes.contains("IPLArtworkDerivativeSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLArtworkDerivativeSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLArtworkDerivativeSetProvider;
            iPLArtworkDerivativeSetProvider = new HollowObjectCacheProvider(typeDataAccess, iPLArtworkDerivativeSetTypeAPI, factory, previousCacheProvider);
        } else {
            iPLArtworkDerivativeSetProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLArtworkDerivativeSetTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfContract");
        if(typeDataAccess != null) {
            listOfContractTypeAPI = new ListOfContractTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfContractTypeAPI = new ListOfContractTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfContract"));
        }
        addTypeAPI(listOfContractTypeAPI);
        factory = factoryOverrides.get("ListOfContract");
        if(factory == null)
            factory = new ListOfContractHollowFactory();
        if(cachedTypes.contains("ListOfContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfContractProvider;
            listOfContractProvider = new HollowObjectCacheProvider(typeDataAccess, listOfContractTypeAPI, factory, previousCacheProvider);
        } else {
            listOfContractProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Contracts");
        if(typeDataAccess != null) {
            contractsTypeAPI = new ContractsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            contractsTypeAPI = new ContractsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Contracts"));
        }
        addTypeAPI(contractsTypeAPI);
        factory = factoryOverrides.get("Contracts");
        if(factory == null)
            factory = new ContractsHollowFactory();
        if(cachedTypes.contains("Contracts")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.contractsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.contractsProvider;
            contractsProvider = new HollowObjectCacheProvider(typeDataAccess, contractsTypeAPI, factory, previousCacheProvider);
        } else {
            contractsProvider = new HollowObjectFactoryProvider(typeDataAccess, contractsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfPackageTags");
        if(typeDataAccess != null) {
            listOfPackageTagsTypeAPI = new ListOfPackageTagsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfPackageTagsTypeAPI = new ListOfPackageTagsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfPackageTags"));
        }
        addTypeAPI(listOfPackageTagsTypeAPI);
        factory = factoryOverrides.get("ListOfPackageTags");
        if(factory == null)
            factory = new ListOfPackageTagsHollowFactory();
        if(cachedTypes.contains("ListOfPackageTags")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfPackageTagsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfPackageTagsProvider;
            listOfPackageTagsProvider = new HollowObjectCacheProvider(typeDataAccess, listOfPackageTagsTypeAPI, factory, previousCacheProvider);
        } else {
            listOfPackageTagsProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfPackageTagsTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfString");
        if(typeDataAccess != null) {
            listOfStringTypeAPI = new ListOfStringTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfStringTypeAPI = new ListOfStringTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfString"));
        }
        addTypeAPI(listOfStringTypeAPI);
        factory = factoryOverrides.get("ListOfString");
        if(factory == null)
            factory = new ListOfStringHollowFactory();
        if(cachedTypes.contains("ListOfString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfStringProvider;
            listOfStringProvider = new HollowObjectCacheProvider(typeDataAccess, listOfStringTypeAPI, factory, previousCacheProvider);
        } else {
            listOfStringProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfStringTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ArtworkLocale");
        if(typeDataAccess != null) {
            artworkLocaleTypeAPI = new ArtworkLocaleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            artworkLocaleTypeAPI = new ArtworkLocaleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ArtworkLocale"));
        }
        addTypeAPI(artworkLocaleTypeAPI);
        factory = factoryOverrides.get("ArtworkLocale");
        if(factory == null)
            factory = new ArtworkLocaleHollowFactory();
        if(cachedTypes.contains("ArtworkLocale")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.artworkLocaleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.artworkLocaleProvider;
            artworkLocaleProvider = new HollowObjectCacheProvider(typeDataAccess, artworkLocaleTypeAPI, factory, previousCacheProvider);
        } else {
            artworkLocaleProvider = new HollowObjectFactoryProvider(typeDataAccess, artworkLocaleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ArtworkLocaleList");
        if(typeDataAccess != null) {
            artworkLocaleListTypeAPI = new ArtworkLocaleListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            artworkLocaleListTypeAPI = new ArtworkLocaleListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ArtworkLocaleList"));
        }
        addTypeAPI(artworkLocaleListTypeAPI);
        factory = factoryOverrides.get("ArtworkLocaleList");
        if(factory == null)
            factory = new ArtworkLocaleListHollowFactory();
        if(cachedTypes.contains("ArtworkLocaleList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.artworkLocaleListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.artworkLocaleListProvider;
            artworkLocaleListProvider = new HollowObjectCacheProvider(typeDataAccess, artworkLocaleListTypeAPI, factory, previousCacheProvider);
        } else {
            artworkLocaleListProvider = new HollowObjectFactoryProvider(typeDataAccess, artworkLocaleListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MasterSchedule");
        if(typeDataAccess != null) {
            masterScheduleTypeAPI = new MasterScheduleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            masterScheduleTypeAPI = new MasterScheduleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MasterSchedule"));
        }
        addTypeAPI(masterScheduleTypeAPI);
        factory = factoryOverrides.get("MasterSchedule");
        if(factory == null)
            factory = new MasterScheduleHollowFactory();
        if(cachedTypes.contains("MasterSchedule")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.masterScheduleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.masterScheduleProvider;
            masterScheduleProvider = new HollowObjectCacheProvider(typeDataAccess, masterScheduleTypeAPI, factory, previousCacheProvider);
        } else {
            masterScheduleProvider = new HollowObjectFactoryProvider(typeDataAccess, masterScheduleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MultiValuePassthroughMap");
        if(typeDataAccess != null) {
            multiValuePassthroughMapTypeAPI = new MultiValuePassthroughMapTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            multiValuePassthroughMapTypeAPI = new MultiValuePassthroughMapTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MultiValuePassthroughMap"));
        }
        addTypeAPI(multiValuePassthroughMapTypeAPI);
        factory = factoryOverrides.get("MultiValuePassthroughMap");
        if(factory == null)
            factory = new MultiValuePassthroughMapHollowFactory();
        if(cachedTypes.contains("MultiValuePassthroughMap")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.multiValuePassthroughMapProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.multiValuePassthroughMapProvider;
            multiValuePassthroughMapProvider = new HollowObjectCacheProvider(typeDataAccess, multiValuePassthroughMapTypeAPI, factory, previousCacheProvider);
        } else {
            multiValuePassthroughMapProvider = new HollowObjectFactoryProvider(typeDataAccess, multiValuePassthroughMapTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("OriginServer");
        if(typeDataAccess != null) {
            originServerTypeAPI = new OriginServerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            originServerTypeAPI = new OriginServerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "OriginServer"));
        }
        addTypeAPI(originServerTypeAPI);
        factory = factoryOverrides.get("OriginServer");
        if(factory == null)
            factory = new OriginServerHollowFactory();
        if(cachedTypes.contains("OriginServer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.originServerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.originServerProvider;
            originServerProvider = new HollowObjectCacheProvider(typeDataAccess, originServerTypeAPI, factory, previousCacheProvider);
        } else {
            originServerProvider = new HollowObjectFactoryProvider(typeDataAccess, originServerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("OverrideSchedule");
        if(typeDataAccess != null) {
            overrideScheduleTypeAPI = new OverrideScheduleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            overrideScheduleTypeAPI = new OverrideScheduleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "OverrideSchedule"));
        }
        addTypeAPI(overrideScheduleTypeAPI);
        factory = factoryOverrides.get("OverrideSchedule");
        if(factory == null)
            factory = new OverrideScheduleHollowFactory();
        if(cachedTypes.contains("OverrideSchedule")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.overrideScheduleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.overrideScheduleProvider;
            overrideScheduleProvider = new HollowObjectCacheProvider(typeDataAccess, overrideScheduleTypeAPI, factory, previousCacheProvider);
        } else {
            overrideScheduleProvider = new HollowObjectFactoryProvider(typeDataAccess, overrideScheduleTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseTag");
        if(typeDataAccess != null) {
            phaseTagTypeAPI = new PhaseTagTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseTagTypeAPI = new PhaseTagTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseTag"));
        }
        addTypeAPI(phaseTagTypeAPI);
        factory = factoryOverrides.get("PhaseTag");
        if(factory == null)
            factory = new PhaseTagHollowFactory();
        if(cachedTypes.contains("PhaseTag")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseTagProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseTagProvider;
            phaseTagProvider = new HollowObjectCacheProvider(typeDataAccess, phaseTagTypeAPI, factory, previousCacheProvider);
        } else {
            phaseTagProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseTagTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseTagList");
        if(typeDataAccess != null) {
            phaseTagListTypeAPI = new PhaseTagListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            phaseTagListTypeAPI = new PhaseTagListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PhaseTagList"));
        }
        addTypeAPI(phaseTagListTypeAPI);
        factory = factoryOverrides.get("PhaseTagList");
        if(factory == null)
            factory = new PhaseTagListHollowFactory();
        if(cachedTypes.contains("PhaseTagList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseTagListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseTagListProvider;
            phaseTagListProvider = new HollowObjectCacheProvider(typeDataAccess, phaseTagListTypeAPI, factory, previousCacheProvider);
        } else {
            phaseTagListProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseTagListTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ReleaseDate");
        if(typeDataAccess != null) {
            releaseDateTypeAPI = new ReleaseDateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            releaseDateTypeAPI = new ReleaseDateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ReleaseDate"));
        }
        addTypeAPI(releaseDateTypeAPI);
        factory = factoryOverrides.get("ReleaseDate");
        if(factory == null)
            factory = new ReleaseDateHollowFactory();
        if(cachedTypes.contains("ReleaseDate")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.releaseDateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.releaseDateProvider;
            releaseDateProvider = new HollowObjectCacheProvider(typeDataAccess, releaseDateTypeAPI, factory, previousCacheProvider);
        } else {
            releaseDateProvider = new HollowObjectFactoryProvider(typeDataAccess, releaseDateTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfReleaseDates");
        if(typeDataAccess != null) {
            listOfReleaseDatesTypeAPI = new ListOfReleaseDatesTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfReleaseDatesTypeAPI = new ListOfReleaseDatesTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfReleaseDates"));
        }
        addTypeAPI(listOfReleaseDatesTypeAPI);
        factory = factoryOverrides.get("ListOfReleaseDates");
        if(factory == null)
            factory = new ListOfReleaseDatesHollowFactory();
        if(cachedTypes.contains("ListOfReleaseDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfReleaseDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfReleaseDatesProvider;
            listOfReleaseDatesProvider = new HollowObjectCacheProvider(typeDataAccess, listOfReleaseDatesTypeAPI, factory, previousCacheProvider);
        } else {
            listOfReleaseDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfReleaseDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsAsset");
        if(typeDataAccess != null) {
            rightsAssetTypeAPI = new RightsAssetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsAssetTypeAPI = new RightsAssetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsAsset"));
        }
        addTypeAPI(rightsAssetTypeAPI);
        factory = factoryOverrides.get("RightsAsset");
        if(factory == null)
            factory = new RightsAssetHollowFactory();
        if(cachedTypes.contains("RightsAsset")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsAssetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsAssetProvider;
            rightsAssetProvider = new HollowObjectCacheProvider(typeDataAccess, rightsAssetTypeAPI, factory, previousCacheProvider);
        } else {
            rightsAssetProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsAssetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsContractAsset");
        if(typeDataAccess != null) {
            rightsContractAssetTypeAPI = new RightsContractAssetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsContractAssetTypeAPI = new RightsContractAssetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsContractAsset"));
        }
        addTypeAPI(rightsContractAssetTypeAPI);
        factory = factoryOverrides.get("RightsContractAsset");
        if(factory == null)
            factory = new RightsContractAssetHollowFactory();
        if(cachedTypes.contains("RightsContractAsset")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsContractAssetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsContractAssetProvider;
            rightsContractAssetProvider = new HollowObjectCacheProvider(typeDataAccess, rightsContractAssetTypeAPI, factory, previousCacheProvider);
        } else {
            rightsContractAssetProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsContractAssetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsContractAsset");
        if(typeDataAccess != null) {
            listOfRightsContractAssetTypeAPI = new ListOfRightsContractAssetTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsContractAssetTypeAPI = new ListOfRightsContractAssetTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsContractAsset"));
        }
        addTypeAPI(listOfRightsContractAssetTypeAPI);
        factory = factoryOverrides.get("ListOfRightsContractAsset");
        if(factory == null)
            factory = new ListOfRightsContractAssetHollowFactory();
        if(cachedTypes.contains("ListOfRightsContractAsset")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsContractAssetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsContractAssetProvider;
            listOfRightsContractAssetProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsContractAssetTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsContractAssetProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsContractAssetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsContract");
        if(typeDataAccess != null) {
            rightsContractTypeAPI = new RightsContractTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsContractTypeAPI = new RightsContractTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsContract"));
        }
        addTypeAPI(rightsContractTypeAPI);
        factory = factoryOverrides.get("RightsContract");
        if(factory == null)
            factory = new RightsContractHollowFactory();
        if(cachedTypes.contains("RightsContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsContractProvider;
            rightsContractProvider = new HollowObjectCacheProvider(typeDataAccess, rightsContractTypeAPI, factory, previousCacheProvider);
        } else {
            rightsContractProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsContract");
        if(typeDataAccess != null) {
            listOfRightsContractTypeAPI = new ListOfRightsContractTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsContractTypeAPI = new ListOfRightsContractTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsContract"));
        }
        addTypeAPI(listOfRightsContractTypeAPI);
        factory = factoryOverrides.get("ListOfRightsContract");
        if(factory == null)
            factory = new ListOfRightsContractHollowFactory();
        if(cachedTypes.contains("ListOfRightsContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsContractProvider;
            listOfRightsContractProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsContractTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsContractProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Rights");
        if(typeDataAccess != null) {
            rightsTypeAPI = new RightsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsTypeAPI = new RightsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Rights"));
        }
        addTypeAPI(rightsTypeAPI);
        factory = factoryOverrides.get("Rights");
        if(factory == null)
            factory = new RightsHollowFactory();
        if(cachedTypes.contains("Rights")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsProvider;
            rightsProvider = new HollowObjectCacheProvider(typeDataAccess, rightsTypeAPI, factory, previousCacheProvider);
        } else {
            rightsProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseArtwork");
        if(typeDataAccess != null) {
            rolloutPhaseArtworkTypeAPI = new RolloutPhaseArtworkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseArtworkTypeAPI = new RolloutPhaseArtworkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseArtwork"));
        }
        addTypeAPI(rolloutPhaseArtworkTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseArtwork");
        if(factory == null)
            factory = new RolloutPhaseArtworkHollowFactory();
        if(cachedTypes.contains("RolloutPhaseArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseArtworkProvider;
            rolloutPhaseArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseArtworkTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfRightsAsset");
        if(typeDataAccess != null) {
            setOfRightsAssetTypeAPI = new SetOfRightsAssetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfRightsAssetTypeAPI = new SetOfRightsAssetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfRightsAsset"));
        }
        addTypeAPI(setOfRightsAssetTypeAPI);
        factory = factoryOverrides.get("SetOfRightsAsset");
        if(factory == null)
            factory = new SetOfRightsAssetHollowFactory();
        if(cachedTypes.contains("SetOfRightsAsset")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfRightsAssetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfRightsAssetProvider;
            setOfRightsAssetProvider = new HollowObjectCacheProvider(typeDataAccess, setOfRightsAssetTypeAPI, factory, previousCacheProvider);
        } else {
            setOfRightsAssetProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfRightsAssetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsAssets");
        if(typeDataAccess != null) {
            rightsAssetsTypeAPI = new RightsAssetsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsAssetsTypeAPI = new RightsAssetsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsAssets"));
        }
        addTypeAPI(rightsAssetsTypeAPI);
        factory = factoryOverrides.get("RightsAssets");
        if(factory == null)
            factory = new RightsAssetsHollowFactory();
        if(cachedTypes.contains("RightsAssets")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsAssetsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsAssetsProvider;
            rightsAssetsProvider = new HollowObjectCacheProvider(typeDataAccess, rightsAssetsTypeAPI, factory, previousCacheProvider);
        } else {
            rightsAssetsProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsAssetsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfString");
        if(typeDataAccess != null) {
            setOfStringTypeAPI = new SetOfStringTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfStringTypeAPI = new SetOfStringTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfString"));
        }
        addTypeAPI(setOfStringTypeAPI);
        factory = factoryOverrides.get("SetOfString");
        if(factory == null)
            factory = new SetOfStringHollowFactory();
        if(cachedTypes.contains("SetOfString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfStringProvider;
            setOfStringProvider = new HollowObjectCacheProvider(typeDataAccess, setOfStringTypeAPI, factory, previousCacheProvider);
        } else {
            setOfStringProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SingleValuePassthroughMap");
        if(typeDataAccess != null) {
            singleValuePassthroughMapTypeAPI = new SingleValuePassthroughMapTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            singleValuePassthroughMapTypeAPI = new SingleValuePassthroughMapTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "SingleValuePassthroughMap"));
        }
        addTypeAPI(singleValuePassthroughMapTypeAPI);
        factory = factoryOverrides.get("SingleValuePassthroughMap");
        if(factory == null)
            factory = new SingleValuePassthroughMapHollowFactory();
        if(cachedTypes.contains("SingleValuePassthroughMap")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.singleValuePassthroughMapProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.singleValuePassthroughMapProvider;
            singleValuePassthroughMapProvider = new HollowObjectCacheProvider(typeDataAccess, singleValuePassthroughMapTypeAPI, factory, previousCacheProvider);
        } else {
            singleValuePassthroughMapProvider = new HollowObjectFactoryProvider(typeDataAccess, singleValuePassthroughMapTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PassthroughData");
        if(typeDataAccess != null) {
            passthroughDataTypeAPI = new PassthroughDataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            passthroughDataTypeAPI = new PassthroughDataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PassthroughData"));
        }
        addTypeAPI(passthroughDataTypeAPI);
        factory = factoryOverrides.get("PassthroughData");
        if(factory == null)
            factory = new PassthroughDataHollowFactory();
        if(cachedTypes.contains("PassthroughData")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.passthroughDataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.passthroughDataProvider;
            passthroughDataProvider = new HollowObjectCacheProvider(typeDataAccess, passthroughDataTypeAPI, factory, previousCacheProvider);
        } else {
            passthroughDataProvider = new HollowObjectFactoryProvider(typeDataAccess, passthroughDataTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ArtworkAttributes");
        if(typeDataAccess != null) {
            artworkAttributesTypeAPI = new ArtworkAttributesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            artworkAttributesTypeAPI = new ArtworkAttributesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ArtworkAttributes"));
        }
        addTypeAPI(artworkAttributesTypeAPI);
        factory = factoryOverrides.get("ArtworkAttributes");
        if(factory == null)
            factory = new ArtworkAttributesHollowFactory();
        if(cachedTypes.contains("ArtworkAttributes")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.artworkAttributesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.artworkAttributesProvider;
            artworkAttributesProvider = new HollowObjectCacheProvider(typeDataAccess, artworkAttributesTypeAPI, factory, previousCacheProvider);
        } else {
            artworkAttributesProvider = new HollowObjectFactoryProvider(typeDataAccess, artworkAttributesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CharacterArtworkSource");
        if(typeDataAccess != null) {
            characterArtworkSourceTypeAPI = new CharacterArtworkSourceTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            characterArtworkSourceTypeAPI = new CharacterArtworkSourceTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CharacterArtworkSource"));
        }
        addTypeAPI(characterArtworkSourceTypeAPI);
        factory = factoryOverrides.get("CharacterArtworkSource");
        if(factory == null)
            factory = new CharacterArtworkSourceHollowFactory();
        if(cachedTypes.contains("CharacterArtworkSource")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.characterArtworkSourceProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.characterArtworkSourceProvider;
            characterArtworkSourceProvider = new HollowObjectCacheProvider(typeDataAccess, characterArtworkSourceTypeAPI, factory, previousCacheProvider);
        } else {
            characterArtworkSourceProvider = new HollowObjectFactoryProvider(typeDataAccess, characterArtworkSourceTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IndividualSupplemental");
        if(typeDataAccess != null) {
            individualSupplementalTypeAPI = new IndividualSupplementalTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            individualSupplementalTypeAPI = new IndividualSupplementalTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IndividualSupplemental"));
        }
        addTypeAPI(individualSupplementalTypeAPI);
        factory = factoryOverrides.get("IndividualSupplemental");
        if(factory == null)
            factory = new IndividualSupplementalHollowFactory();
        if(cachedTypes.contains("IndividualSupplemental")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.individualSupplementalProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.individualSupplementalProvider;
            individualSupplementalProvider = new HollowObjectCacheProvider(typeDataAccess, individualSupplementalTypeAPI, factory, previousCacheProvider);
        } else {
            individualSupplementalProvider = new HollowObjectFactoryProvider(typeDataAccess, individualSupplementalTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonArtworkSource");
        if(typeDataAccess != null) {
            personArtworkSourceTypeAPI = new PersonArtworkSourceTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personArtworkSourceTypeAPI = new PersonArtworkSourceTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonArtworkSource"));
        }
        addTypeAPI(personArtworkSourceTypeAPI);
        factory = factoryOverrides.get("PersonArtworkSource");
        if(factory == null)
            factory = new PersonArtworkSourceHollowFactory();
        if(cachedTypes.contains("PersonArtworkSource")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personArtworkSourceProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personArtworkSourceProvider;
            personArtworkSourceProvider = new HollowObjectCacheProvider(typeDataAccess, personArtworkSourceTypeAPI, factory, previousCacheProvider);
        } else {
            personArtworkSourceProvider = new HollowObjectFactoryProvider(typeDataAccess, personArtworkSourceTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Status");
        if(typeDataAccess != null) {
            statusTypeAPI = new StatusTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            statusTypeAPI = new StatusTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Status"));
        }
        addTypeAPI(statusTypeAPI);
        factory = factoryOverrides.get("Status");
        if(factory == null)
            factory = new StatusHollowFactory();
        if(cachedTypes.contains("Status")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.statusProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.statusProvider;
            statusProvider = new HollowObjectCacheProvider(typeDataAccess, statusTypeAPI, factory, previousCacheProvider);
        } else {
            statusProvider = new HollowObjectFactoryProvider(typeDataAccess, statusTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("SupplementalsList");
        if(typeDataAccess != null) {
            supplementalsListTypeAPI = new SupplementalsListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            supplementalsListTypeAPI = new SupplementalsListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "SupplementalsList"));
        }
        addTypeAPI(supplementalsListTypeAPI);
        factory = factoryOverrides.get("SupplementalsList");
        if(factory == null)
            factory = new SupplementalsListHollowFactory();
        if(cachedTypes.contains("SupplementalsList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.supplementalsListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.supplementalsListProvider;
            supplementalsListProvider = new HollowObjectCacheProvider(typeDataAccess, supplementalsListTypeAPI, factory, previousCacheProvider);
        } else {
            supplementalsListProvider = new HollowObjectFactoryProvider(typeDataAccess, supplementalsListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Supplementals");
        if(typeDataAccess != null) {
            supplementalsTypeAPI = new SupplementalsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            supplementalsTypeAPI = new SupplementalsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Supplementals"));
        }
        addTypeAPI(supplementalsTypeAPI);
        factory = factoryOverrides.get("Supplementals");
        if(factory == null)
            factory = new SupplementalsHollowFactory();
        if(cachedTypes.contains("Supplementals")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.supplementalsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.supplementalsProvider;
            supplementalsProvider = new HollowObjectCacheProvider(typeDataAccess, supplementalsTypeAPI, factory, previousCacheProvider);
        } else {
            supplementalsProvider = new HollowObjectFactoryProvider(typeDataAccess, supplementalsTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("TopNAttributesSet");
        if(typeDataAccess != null) {
            topNAttributesSetTypeAPI = new TopNAttributesSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            topNAttributesSetTypeAPI = new TopNAttributesSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "TopNAttributesSet"));
        }
        addTypeAPI(topNAttributesSetTypeAPI);
        factory = factoryOverrides.get("TopNAttributesSet");
        if(factory == null)
            factory = new TopNAttributesSetHollowFactory();
        if(cachedTypes.contains("TopNAttributesSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.topNAttributesSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.topNAttributesSetProvider;
            topNAttributesSetProvider = new HollowObjectCacheProvider(typeDataAccess, topNAttributesSetTypeAPI, factory, previousCacheProvider);
        } else {
            topNAttributesSetProvider = new HollowObjectFactoryProvider(typeDataAccess, topNAttributesSetTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("PersonCharacterResource");
        if(typeDataAccess != null) {
            personCharacterResourceTypeAPI = new PersonCharacterResourceTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personCharacterResourceTypeAPI = new PersonCharacterResourceTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonCharacterResource"));
        }
        addTypeAPI(personCharacterResourceTypeAPI);
        factory = factoryOverrides.get("PersonCharacterResource");
        if(factory == null)
            factory = new PersonCharacterResourceHollowFactory();
        if(cachedTypes.contains("PersonCharacterResource")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personCharacterResourceProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personCharacterResourceProvider;
            personCharacterResourceProvider = new HollowObjectCacheProvider(typeDataAccess, personCharacterResourceTypeAPI, factory, previousCacheProvider);
        } else {
            personCharacterResourceProvider = new HollowObjectFactoryProvider(typeDataAccess, personCharacterResourceTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("StoriesSynopses");
        if(typeDataAccess != null) {
            storiesSynopsesTypeAPI = new StoriesSynopsesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            storiesSynopsesTypeAPI = new StoriesSynopsesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "StoriesSynopses"));
        }
        addTypeAPI(storiesSynopsesTypeAPI);
        factory = factoryOverrides.get("StoriesSynopses");
        if(factory == null)
            factory = new StoriesSynopsesHollowFactory();
        if(cachedTypes.contains("StoriesSynopses")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.storiesSynopsesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.storiesSynopsesProvider;
            storiesSynopsesProvider = new HollowObjectCacheProvider(typeDataAccess, storiesSynopsesTypeAPI, factory, previousCacheProvider);
        } else {
            storiesSynopsesProvider = new HollowObjectFactoryProvider(typeDataAccess, storiesSynopsesTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoArtworkSource");
        if(typeDataAccess != null) {
            videoArtworkSourceTypeAPI = new VideoArtworkSourceTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoArtworkSourceTypeAPI = new VideoArtworkSourceTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoArtworkSource"));
        }
        addTypeAPI(videoArtworkSourceTypeAPI);
        factory = factoryOverrides.get("VideoArtworkSource");
        if(factory == null)
            factory = new VideoArtworkSourceHollowFactory();
        if(cachedTypes.contains("VideoArtworkSource")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoArtworkSourceProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoArtworkSourceProvider;
            videoArtworkSourceProvider = new HollowObjectCacheProvider(typeDataAccess, videoArtworkSourceTypeAPI, factory, previousCacheProvider);
        } else {
            videoArtworkSourceProvider = new HollowObjectFactoryProvider(typeDataAccess, videoArtworkSourceTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoId");
        if(typeDataAccess != null) {
            videoIdTypeAPI = new VideoIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoIdTypeAPI = new VideoIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoId"));
        }
        addTypeAPI(videoIdTypeAPI);
        factory = factoryOverrides.get("VideoId");
        if(factory == null)
            factory = new VideoIdHollowFactory();
        if(cachedTypes.contains("VideoId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoIdProvider;
            videoIdProvider = new HollowObjectCacheProvider(typeDataAccess, videoIdTypeAPI, factory, previousCacheProvider);
        } else {
            videoIdProvider = new HollowObjectFactoryProvider(typeDataAccess, videoIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfVideoIds");
        if(typeDataAccess != null) {
            listOfVideoIdsTypeAPI = new ListOfVideoIdsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfVideoIdsTypeAPI = new ListOfVideoIdsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfVideoIds"));
        }
        addTypeAPI(listOfVideoIdsTypeAPI);
        factory = factoryOverrides.get("ListOfVideoIds");
        if(factory == null)
            factory = new ListOfVideoIdsHollowFactory();
        if(cachedTypes.contains("ListOfVideoIds")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfVideoIdsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfVideoIdsProvider;
            listOfVideoIdsProvider = new HollowObjectCacheProvider(typeDataAccess, listOfVideoIdsTypeAPI, factory, previousCacheProvider);
        } else {
            listOfVideoIdsProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfVideoIdsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonBio");
        if(typeDataAccess != null) {
            personBioTypeAPI = new PersonBioTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personBioTypeAPI = new PersonBioTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonBio"));
        }
        addTypeAPI(personBioTypeAPI);
        factory = factoryOverrides.get("PersonBio");
        if(factory == null)
            factory = new PersonBioHollowFactory();
        if(cachedTypes.contains("PersonBio")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personBioProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personBioProvider;
            personBioProvider = new HollowObjectCacheProvider(typeDataAccess, personBioTypeAPI, factory, previousCacheProvider);
        } else {
            personBioProvider = new HollowObjectFactoryProvider(typeDataAccess, personBioTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoStreamCropParams");
        if(typeDataAccess != null) {
            videoStreamCropParamsTypeAPI = new VideoStreamCropParamsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoStreamCropParamsTypeAPI = new VideoStreamCropParamsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoStreamCropParams"));
        }
        addTypeAPI(videoStreamCropParamsTypeAPI);
        factory = factoryOverrides.get("VideoStreamCropParams");
        if(factory == null)
            factory = new VideoStreamCropParamsHollowFactory();
        if(cachedTypes.contains("VideoStreamCropParams")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoStreamCropParamsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoStreamCropParamsProvider;
            videoStreamCropParamsProvider = new HollowObjectCacheProvider(typeDataAccess, videoStreamCropParamsTypeAPI, factory, previousCacheProvider);
        } else {
            videoStreamCropParamsProvider = new HollowObjectFactoryProvider(typeDataAccess, videoStreamCropParamsTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("Package");
        if(typeDataAccess != null) {
            packageTypeAPI = new PackageTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            packageTypeAPI = new PackageTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Package"));
        }
        addTypeAPI(packageTypeAPI);
        factory = factoryOverrides.get("Package");
        if(factory == null)
            factory = new PackageHollowFactory();
        if(cachedTypes.contains("Package")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageProvider;
            packageProvider = new HollowObjectCacheProvider(typeDataAccess, packageTypeAPI, factory, previousCacheProvider);
        } else {
            packageProvider = new HollowObjectFactoryProvider(typeDataAccess, packageTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("VideoTypeDescriptorSet");
        if(typeDataAccess != null) {
            videoTypeDescriptorSetTypeAPI = new VideoTypeDescriptorSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            videoTypeDescriptorSetTypeAPI = new VideoTypeDescriptorSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "VideoTypeDescriptorSet"));
        }
        addTypeAPI(videoTypeDescriptorSetTypeAPI);
        factory = factoryOverrides.get("VideoTypeDescriptorSet");
        if(factory == null)
            factory = new VideoTypeDescriptorSetHollowFactory();
        if(cachedTypes.contains("VideoTypeDescriptorSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoTypeDescriptorSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoTypeDescriptorSetProvider;
            videoTypeDescriptorSetProvider = new HollowObjectCacheProvider(typeDataAccess, videoTypeDescriptorSetTypeAPI, factory, previousCacheProvider);
        } else {
            videoTypeDescriptorSetProvider = new HollowObjectFactoryProvider(typeDataAccess, videoTypeDescriptorSetTypeAPI, factory);
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
        if(derivativeTagProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)derivativeTagProvider).detach();
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
        if(explicitDateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)explicitDateProvider).detach();
        if(iSOCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryProvider).detach();
        if(iSOCountryListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryListProvider).detach();
        if(iSOCountrySetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountrySetProvider).detach();
        if(listOfDerivativeTagProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfDerivativeTagProvider).detach();
        if(mapKeyProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapKeyProvider).detach();
        if(mapOfFlagsFirstDisplayDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfFlagsFirstDisplayDatesProvider).detach();
        if(flagsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)flagsProvider).detach();
        if(personCharacterProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personCharacterProvider).detach();
        if(characterListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterListProvider).detach();
        if(movieCharacterPersonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieCharacterPersonProvider).detach();
        if(personVideoAliasIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoAliasIdProvider).detach();
        if(personVideoAliasIdsListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoAliasIdsListProvider).detach();
        if(personVideoRoleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoRoleProvider).detach();
        if(personVideoRolesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoRolesListProvider).detach();
        if(personVideoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoProvider).detach();
        if(rightsAssetSetIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsAssetSetIdProvider).detach();
        if(rightsContractPackageProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsContractPackageProvider).detach();
        if(listOfRightsContractPackageProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsContractPackageProvider).detach();
        if(rightsWindowContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsWindowContractProvider).detach();
        if(listOfRightsWindowContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsWindowContractProvider).detach();
        if(rightsWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsWindowProvider).detach();
        if(listOfRightsWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsWindowProvider).detach();
        if(rolloutPhaseWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseWindowProvider).detach();
        if(rolloutPhaseWindowMapProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseWindowMapProvider).detach();
        if(seasonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)seasonProvider).detach();
        if(seasonListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)seasonListProvider).detach();
        if(showMemberTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypeProvider).detach();
        if(showMemberTypeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypeListProvider).detach();
        if(showCountryLabelProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showCountryLabelProvider).detach();
        if(showSeasonEpisodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showSeasonEpisodeProvider).detach();
        if(streamAssetMetadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamAssetMetadataProvider).detach();
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
        if(absoluteScheduleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)absoluteScheduleProvider).detach();
        if(artWorkImageTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artWorkImageTypeProvider).detach();
        if(artworkRecipeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artworkRecipeProvider).detach();
        if(audioStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)audioStreamInfoProvider).detach();
        if(cSMReviewProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cSMReviewProvider).detach();
        if(cacheDeploymentIntentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cacheDeploymentIntentProvider).detach();
        if(cdnProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cdnProvider).detach();
        if(cdnDeploymentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cdnDeploymentProvider).detach();
        if(cdnDeploymentSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)cdnDeploymentSetProvider).detach();
        if(certificationSystemRatingProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemRatingProvider).detach();
        if(certificationSystemRatingListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemRatingListProvider).detach();
        if(certificationSystemProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)certificationSystemProvider).detach();
        if(characterElementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterElementsProvider).detach();
        if(characterProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterProvider).detach();
        if(damMerchStillsMomentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)damMerchStillsMomentProvider).detach();
        if(damMerchStillsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)damMerchStillsProvider).detach();
        if(disallowedSubtitleLangCodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedSubtitleLangCodeProvider).detach();
        if(disallowedSubtitleLangCodesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedSubtitleLangCodesListProvider).detach();
        if(disallowedAssetBundleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedAssetBundleProvider).detach();
        if(disallowedAssetBundlesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedAssetBundlesListProvider).detach();
        if(contractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)contractProvider).detach();
        if(drmHeaderInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmHeaderInfoProvider).detach();
        if(drmHeaderInfoListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmHeaderInfoListProvider).detach();
        if(drmSystemIdentifiersProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)drmSystemIdentifiersProvider).detach();
        if(iPLArtworkDerivativeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLArtworkDerivativeProvider).detach();
        if(iPLDerivativeSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLDerivativeSetProvider).detach();
        if(iPLDerivativeGroupProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLDerivativeGroupProvider).detach();
        if(iPLDerivativeGroupSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLDerivativeGroupSetProvider).detach();
        if(iPLArtworkDerivativeSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLArtworkDerivativeSetProvider).detach();
        if(imageStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)imageStreamInfoProvider).detach();
        if(listOfContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfContractProvider).detach();
        if(contractsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)contractsProvider).detach();
        if(listOfPackageTagsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfPackageTagsProvider).detach();
        if(deployablePackagesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)deployablePackagesProvider).detach();
        if(listOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfStringProvider).detach();
        if(localeTerritoryCodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localeTerritoryCodeProvider).detach();
        if(localeTerritoryCodeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localeTerritoryCodeListProvider).detach();
        if(artworkLocaleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artworkLocaleProvider).detach();
        if(artworkLocaleListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artworkLocaleListProvider).detach();
        if(masterScheduleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)masterScheduleProvider).detach();
        if(multiValuePassthroughMapProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)multiValuePassthroughMapProvider).detach();
        if(originServerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)originServerProvider).detach();
        if(overrideScheduleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)overrideScheduleProvider).detach();
        if(packageDrmInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageDrmInfoProvider).detach();
        if(packageDrmInfoListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageDrmInfoListProvider).detach();
        if(packageMomentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageMomentProvider).detach();
        if(packageMomentListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageMomentListProvider).detach();
        if(phaseTagProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseTagProvider).detach();
        if(phaseTagListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseTagListProvider).detach();
        if(protectionTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)protectionTypesProvider).detach();
        if(releaseDateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)releaseDateProvider).detach();
        if(listOfReleaseDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfReleaseDatesProvider).detach();
        if(rightsAssetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsAssetProvider).detach();
        if(rightsContractAssetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsContractAssetProvider).detach();
        if(listOfRightsContractAssetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsContractAssetProvider).detach();
        if(rightsContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsContractProvider).detach();
        if(listOfRightsContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsContractProvider).detach();
        if(rightsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsProvider).detach();
        if(rolloutPhaseArtworkSourceFileIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkSourceFileIdProvider).detach();
        if(rolloutPhaseArtworkSourceFileIdListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkSourceFileIdListProvider).detach();
        if(rolloutPhaseArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkProvider).detach();
        if(rolloutPhaseLocalizedMetadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseLocalizedMetadataProvider).detach();
        if(rolloutPhaseElementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseElementsProvider).detach();
        if(rolloutPhaseProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseProvider).detach();
        if(rolloutPhaseListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseListProvider).detach();
        if(rolloutProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutProvider).detach();
        if(setOfRightsAssetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfRightsAssetProvider).detach();
        if(rightsAssetsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsAssetsProvider).detach();
        if(setOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfStringProvider).detach();
        if(singleValuePassthroughMapProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)singleValuePassthroughMapProvider).detach();
        if(passthroughDataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)passthroughDataProvider).detach();
        if(artworkAttributesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)artworkAttributesProvider).detach();
        if(characterArtworkSourceProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)characterArtworkSourceProvider).detach();
        if(individualSupplementalProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)individualSupplementalProvider).detach();
        if(personArtworkSourceProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personArtworkSourceProvider).detach();
        if(statusProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)statusProvider).detach();
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
        if(supplementalsListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)supplementalsListProvider).detach();
        if(supplementalsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)supplementalsProvider).detach();
        if(territoryCountriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)territoryCountriesProvider).detach();
        if(textStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)textStreamInfoProvider).detach();
        if(topNAttributeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNAttributeProvider).detach();
        if(topNAttributesSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNAttributesSetProvider).detach();
        if(topNProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)topNProvider).detach();
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
        if(personCharacterResourceProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personCharacterResourceProvider).detach();
        if(personsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personsProvider).detach();
        if(ratingsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsProvider).detach();
        if(showMemberTypesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypesProvider).detach();
        if(storiesSynopsesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)storiesSynopsesProvider).detach();
        if(turboCollectionsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)turboCollectionsProvider).detach();
        if(vMSAwardProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)vMSAwardProvider).detach();
        if(videoArtworkSourceProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoArtworkSourceProvider).detach();
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
        if(videoIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoIdProvider).detach();
        if(listOfVideoIdsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfVideoIdsProvider).detach();
        if(personBioProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personBioProvider).detach();
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
        if(videoStreamCropParamsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoStreamCropParamsProvider).detach();
        if(videoStreamInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoStreamInfoProvider).detach();
        if(streamNonImageInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)streamNonImageInfoProvider).detach();
        if(packageStreamProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageStreamProvider).detach();
        if(packageStreamSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageStreamSetProvider).detach();
        if(packageProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageProvider).detach();
        if(videoTypeMediaProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeMediaProvider).detach();
        if(videoTypeMediaListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeMediaListProvider).detach();
        if(videoTypeDescriptorProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeDescriptorProvider).detach();
        if(videoTypeDescriptorSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoTypeDescriptorSetProvider).detach();
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
    public DerivativeTagTypeAPI getDerivativeTagTypeAPI() {
        return derivativeTagTypeAPI;
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
    public ExplicitDateTypeAPI getExplicitDateTypeAPI() {
        return explicitDateTypeAPI;
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
    public ListOfDerivativeTagTypeAPI getListOfDerivativeTagTypeAPI() {
        return listOfDerivativeTagTypeAPI;
    }
    public MapKeyTypeAPI getMapKeyTypeAPI() {
        return mapKeyTypeAPI;
    }
    public MapOfFlagsFirstDisplayDatesTypeAPI getMapOfFlagsFirstDisplayDatesTypeAPI() {
        return mapOfFlagsFirstDisplayDatesTypeAPI;
    }
    public FlagsTypeAPI getFlagsTypeAPI() {
        return flagsTypeAPI;
    }
    public PersonCharacterTypeAPI getPersonCharacterTypeAPI() {
        return personCharacterTypeAPI;
    }
    public CharacterListTypeAPI getCharacterListTypeAPI() {
        return characterListTypeAPI;
    }
    public MovieCharacterPersonTypeAPI getMovieCharacterPersonTypeAPI() {
        return movieCharacterPersonTypeAPI;
    }
    public PersonVideoAliasIdTypeAPI getPersonVideoAliasIdTypeAPI() {
        return personVideoAliasIdTypeAPI;
    }
    public PersonVideoAliasIdsListTypeAPI getPersonVideoAliasIdsListTypeAPI() {
        return personVideoAliasIdsListTypeAPI;
    }
    public PersonVideoRoleTypeAPI getPersonVideoRoleTypeAPI() {
        return personVideoRoleTypeAPI;
    }
    public PersonVideoRolesListTypeAPI getPersonVideoRolesListTypeAPI() {
        return personVideoRolesListTypeAPI;
    }
    public PersonVideoTypeAPI getPersonVideoTypeAPI() {
        return personVideoTypeAPI;
    }
    public RightsAssetSetIdTypeAPI getRightsAssetSetIdTypeAPI() {
        return rightsAssetSetIdTypeAPI;
    }
    public RightsContractPackageTypeAPI getRightsContractPackageTypeAPI() {
        return rightsContractPackageTypeAPI;
    }
    public ListOfRightsContractPackageTypeAPI getListOfRightsContractPackageTypeAPI() {
        return listOfRightsContractPackageTypeAPI;
    }
    public RightsWindowContractTypeAPI getRightsWindowContractTypeAPI() {
        return rightsWindowContractTypeAPI;
    }
    public ListOfRightsWindowContractTypeAPI getListOfRightsWindowContractTypeAPI() {
        return listOfRightsWindowContractTypeAPI;
    }
    public RightsWindowTypeAPI getRightsWindowTypeAPI() {
        return rightsWindowTypeAPI;
    }
    public ListOfRightsWindowTypeAPI getListOfRightsWindowTypeAPI() {
        return listOfRightsWindowTypeAPI;
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
    public ShowMemberTypeTypeAPI getShowMemberTypeTypeAPI() {
        return showMemberTypeTypeAPI;
    }
    public ShowMemberTypeListTypeAPI getShowMemberTypeListTypeAPI() {
        return showMemberTypeListTypeAPI;
    }
    public ShowCountryLabelTypeAPI getShowCountryLabelTypeAPI() {
        return showCountryLabelTypeAPI;
    }
    public ShowSeasonEpisodeTypeAPI getShowSeasonEpisodeTypeAPI() {
        return showSeasonEpisodeTypeAPI;
    }
    public StreamAssetMetadataTypeAPI getStreamAssetMetadataTypeAPI() {
        return streamAssetMetadataTypeAPI;
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
    public AbsoluteScheduleTypeAPI getAbsoluteScheduleTypeAPI() {
        return absoluteScheduleTypeAPI;
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
    public CSMReviewTypeAPI getCSMReviewTypeAPI() {
        return cSMReviewTypeAPI;
    }
    public CacheDeploymentIntentTypeAPI getCacheDeploymentIntentTypeAPI() {
        return cacheDeploymentIntentTypeAPI;
    }
    public CdnTypeAPI getCdnTypeAPI() {
        return cdnTypeAPI;
    }
    public CdnDeploymentTypeAPI getCdnDeploymentTypeAPI() {
        return cdnDeploymentTypeAPI;
    }
    public CdnDeploymentSetTypeAPI getCdnDeploymentSetTypeAPI() {
        return cdnDeploymentSetTypeAPI;
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
    public CharacterElementsTypeAPI getCharacterElementsTypeAPI() {
        return characterElementsTypeAPI;
    }
    public CharacterTypeAPI getCharacterTypeAPI() {
        return characterTypeAPI;
    }
    public DamMerchStillsMomentTypeAPI getDamMerchStillsMomentTypeAPI() {
        return damMerchStillsMomentTypeAPI;
    }
    public DamMerchStillsTypeAPI getDamMerchStillsTypeAPI() {
        return damMerchStillsTypeAPI;
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
    public ContractTypeAPI getContractTypeAPI() {
        return contractTypeAPI;
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
    public IPLArtworkDerivativeTypeAPI getIPLArtworkDerivativeTypeAPI() {
        return iPLArtworkDerivativeTypeAPI;
    }
    public IPLDerivativeSetTypeAPI getIPLDerivativeSetTypeAPI() {
        return iPLDerivativeSetTypeAPI;
    }
    public IPLDerivativeGroupTypeAPI getIPLDerivativeGroupTypeAPI() {
        return iPLDerivativeGroupTypeAPI;
    }
    public IPLDerivativeGroupSetTypeAPI getIPLDerivativeGroupSetTypeAPI() {
        return iPLDerivativeGroupSetTypeAPI;
    }
    public IPLArtworkDerivativeSetTypeAPI getIPLArtworkDerivativeSetTypeAPI() {
        return iPLArtworkDerivativeSetTypeAPI;
    }
    public ImageStreamInfoTypeAPI getImageStreamInfoTypeAPI() {
        return imageStreamInfoTypeAPI;
    }
    public ListOfContractTypeAPI getListOfContractTypeAPI() {
        return listOfContractTypeAPI;
    }
    public ContractsTypeAPI getContractsTypeAPI() {
        return contractsTypeAPI;
    }
    public ListOfPackageTagsTypeAPI getListOfPackageTagsTypeAPI() {
        return listOfPackageTagsTypeAPI;
    }
    public DeployablePackagesTypeAPI getDeployablePackagesTypeAPI() {
        return deployablePackagesTypeAPI;
    }
    public ListOfStringTypeAPI getListOfStringTypeAPI() {
        return listOfStringTypeAPI;
    }
    public LocaleTerritoryCodeTypeAPI getLocaleTerritoryCodeTypeAPI() {
        return localeTerritoryCodeTypeAPI;
    }
    public LocaleTerritoryCodeListTypeAPI getLocaleTerritoryCodeListTypeAPI() {
        return localeTerritoryCodeListTypeAPI;
    }
    public ArtworkLocaleTypeAPI getArtworkLocaleTypeAPI() {
        return artworkLocaleTypeAPI;
    }
    public ArtworkLocaleListTypeAPI getArtworkLocaleListTypeAPI() {
        return artworkLocaleListTypeAPI;
    }
    public MasterScheduleTypeAPI getMasterScheduleTypeAPI() {
        return masterScheduleTypeAPI;
    }
    public MultiValuePassthroughMapTypeAPI getMultiValuePassthroughMapTypeAPI() {
        return multiValuePassthroughMapTypeAPI;
    }
    public OriginServerTypeAPI getOriginServerTypeAPI() {
        return originServerTypeAPI;
    }
    public OverrideScheduleTypeAPI getOverrideScheduleTypeAPI() {
        return overrideScheduleTypeAPI;
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
    public PhaseTagTypeAPI getPhaseTagTypeAPI() {
        return phaseTagTypeAPI;
    }
    public PhaseTagListTypeAPI getPhaseTagListTypeAPI() {
        return phaseTagListTypeAPI;
    }
    public ProtectionTypesTypeAPI getProtectionTypesTypeAPI() {
        return protectionTypesTypeAPI;
    }
    public ReleaseDateTypeAPI getReleaseDateTypeAPI() {
        return releaseDateTypeAPI;
    }
    public ListOfReleaseDatesTypeAPI getListOfReleaseDatesTypeAPI() {
        return listOfReleaseDatesTypeAPI;
    }
    public RightsAssetTypeAPI getRightsAssetTypeAPI() {
        return rightsAssetTypeAPI;
    }
    public RightsContractAssetTypeAPI getRightsContractAssetTypeAPI() {
        return rightsContractAssetTypeAPI;
    }
    public ListOfRightsContractAssetTypeAPI getListOfRightsContractAssetTypeAPI() {
        return listOfRightsContractAssetTypeAPI;
    }
    public RightsContractTypeAPI getRightsContractTypeAPI() {
        return rightsContractTypeAPI;
    }
    public ListOfRightsContractTypeAPI getListOfRightsContractTypeAPI() {
        return listOfRightsContractTypeAPI;
    }
    public RightsTypeAPI getRightsTypeAPI() {
        return rightsTypeAPI;
    }
    public RolloutPhaseArtworkSourceFileIdTypeAPI getRolloutPhaseArtworkSourceFileIdTypeAPI() {
        return rolloutPhaseArtworkSourceFileIdTypeAPI;
    }
    public RolloutPhaseArtworkSourceFileIdListTypeAPI getRolloutPhaseArtworkSourceFileIdListTypeAPI() {
        return rolloutPhaseArtworkSourceFileIdListTypeAPI;
    }
    public RolloutPhaseArtworkTypeAPI getRolloutPhaseArtworkTypeAPI() {
        return rolloutPhaseArtworkTypeAPI;
    }
    public RolloutPhaseLocalizedMetadataTypeAPI getRolloutPhaseLocalizedMetadataTypeAPI() {
        return rolloutPhaseLocalizedMetadataTypeAPI;
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
    public SetOfRightsAssetTypeAPI getSetOfRightsAssetTypeAPI() {
        return setOfRightsAssetTypeAPI;
    }
    public RightsAssetsTypeAPI getRightsAssetsTypeAPI() {
        return rightsAssetsTypeAPI;
    }
    public SetOfStringTypeAPI getSetOfStringTypeAPI() {
        return setOfStringTypeAPI;
    }
    public SingleValuePassthroughMapTypeAPI getSingleValuePassthroughMapTypeAPI() {
        return singleValuePassthroughMapTypeAPI;
    }
    public PassthroughDataTypeAPI getPassthroughDataTypeAPI() {
        return passthroughDataTypeAPI;
    }
    public ArtworkAttributesTypeAPI getArtworkAttributesTypeAPI() {
        return artworkAttributesTypeAPI;
    }
    public CharacterArtworkSourceTypeAPI getCharacterArtworkSourceTypeAPI() {
        return characterArtworkSourceTypeAPI;
    }
    public IndividualSupplementalTypeAPI getIndividualSupplementalTypeAPI() {
        return individualSupplementalTypeAPI;
    }
    public PersonArtworkSourceTypeAPI getPersonArtworkSourceTypeAPI() {
        return personArtworkSourceTypeAPI;
    }
    public StatusTypeAPI getStatusTypeAPI() {
        return statusTypeAPI;
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
    public SupplementalsListTypeAPI getSupplementalsListTypeAPI() {
        return supplementalsListTypeAPI;
    }
    public SupplementalsTypeAPI getSupplementalsTypeAPI() {
        return supplementalsTypeAPI;
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
    public TopNAttributesSetTypeAPI getTopNAttributesSetTypeAPI() {
        return topNAttributesSetTypeAPI;
    }
    public TopNTypeAPI getTopNTypeAPI() {
        return topNTypeAPI;
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
    public PersonCharacterResourceTypeAPI getPersonCharacterResourceTypeAPI() {
        return personCharacterResourceTypeAPI;
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
    public StoriesSynopsesTypeAPI getStoriesSynopsesTypeAPI() {
        return storiesSynopsesTypeAPI;
    }
    public TurboCollectionsTypeAPI getTurboCollectionsTypeAPI() {
        return turboCollectionsTypeAPI;
    }
    public VMSAwardTypeAPI getVMSAwardTypeAPI() {
        return vMSAwardTypeAPI;
    }
    public VideoArtworkSourceTypeAPI getVideoArtworkSourceTypeAPI() {
        return videoArtworkSourceTypeAPI;
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
    public VideoIdTypeAPI getVideoIdTypeAPI() {
        return videoIdTypeAPI;
    }
    public ListOfVideoIdsTypeAPI getListOfVideoIdsTypeAPI() {
        return listOfVideoIdsTypeAPI;
    }
    public PersonBioTypeAPI getPersonBioTypeAPI() {
        return personBioTypeAPI;
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
    public VideoStreamCropParamsTypeAPI getVideoStreamCropParamsTypeAPI() {
        return videoStreamCropParamsTypeAPI;
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
    public PackageTypeAPI getPackageTypeAPI() {
        return packageTypeAPI;
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
    public VideoTypeDescriptorSetTypeAPI getVideoTypeDescriptorSetTypeAPI() {
        return videoTypeDescriptorSetTypeAPI;
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
    public Collection<DerivativeTagHollow> getAllDerivativeTagHollow() {
        return new AllHollowRecordCollection<DerivativeTagHollow>(getDataAccess().getTypeDataAccess("DerivativeTag").getTypeState()) {
            protected DerivativeTagHollow getForOrdinal(int ordinal) {
                return getDerivativeTagHollow(ordinal);
            }
        };
    }
    public DerivativeTagHollow getDerivativeTagHollow(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (DerivativeTagHollow)derivativeTagProvider.getHollowObject(ordinal);
    }
    public Collection<DownloadableIdHollow> getAllDownloadableIdHollow() {
        return new AllHollowRecordCollection<DownloadableIdHollow>(getDataAccess().getTypeDataAccess("DownloadableId").getTypeState()) {
            protected DownloadableIdHollow getForOrdinal(int ordinal) {
                return getDownloadableIdHollow(ordinal);
            }
        };
    }
    public DownloadableIdHollow getDownloadableIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(6);
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
        objectCreationSampler.recordCreation(7);
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
        objectCreationSampler.recordCreation(8);
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
        objectCreationSampler.recordCreation(9);
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
        objectCreationSampler.recordCreation(10);
        return (EpisodeListHollow)episodeListProvider.getHollowObject(ordinal);
    }
    public Collection<ExplicitDateHollow> getAllExplicitDateHollow() {
        return new AllHollowRecordCollection<ExplicitDateHollow>(getDataAccess().getTypeDataAccess("ExplicitDate").getTypeState()) {
            protected ExplicitDateHollow getForOrdinal(int ordinal) {
                return getExplicitDateHollow(ordinal);
            }
        };
    }
    public ExplicitDateHollow getExplicitDateHollow(int ordinal) {
        objectCreationSampler.recordCreation(11);
        return (ExplicitDateHollow)explicitDateProvider.getHollowObject(ordinal);
    }
    public Collection<ISOCountryHollow> getAllISOCountryHollow() {
        return new AllHollowRecordCollection<ISOCountryHollow>(getDataAccess().getTypeDataAccess("ISOCountry").getTypeState()) {
            protected ISOCountryHollow getForOrdinal(int ordinal) {
                return getISOCountryHollow(ordinal);
            }
        };
    }
    public ISOCountryHollow getISOCountryHollow(int ordinal) {
        objectCreationSampler.recordCreation(12);
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
        objectCreationSampler.recordCreation(13);
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
        objectCreationSampler.recordCreation(14);
        return (ISOCountrySetHollow)iSOCountrySetProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfDerivativeTagHollow> getAllListOfDerivativeTagHollow() {
        return new AllHollowRecordCollection<ListOfDerivativeTagHollow>(getDataAccess().getTypeDataAccess("ListOfDerivativeTag").getTypeState()) {
            protected ListOfDerivativeTagHollow getForOrdinal(int ordinal) {
                return getListOfDerivativeTagHollow(ordinal);
            }
        };
    }
    public ListOfDerivativeTagHollow getListOfDerivativeTagHollow(int ordinal) {
        objectCreationSampler.recordCreation(15);
        return (ListOfDerivativeTagHollow)listOfDerivativeTagProvider.getHollowObject(ordinal);
    }
    public Collection<MapKeyHollow> getAllMapKeyHollow() {
        return new AllHollowRecordCollection<MapKeyHollow>(getDataAccess().getTypeDataAccess("MapKey").getTypeState()) {
            protected MapKeyHollow getForOrdinal(int ordinal) {
                return getMapKeyHollow(ordinal);
            }
        };
    }
    public MapKeyHollow getMapKeyHollow(int ordinal) {
        objectCreationSampler.recordCreation(16);
        return (MapKeyHollow)mapKeyProvider.getHollowObject(ordinal);
    }
    public Collection<MapOfFlagsFirstDisplayDatesHollow> getAllMapOfFlagsFirstDisplayDatesHollow() {
        return new AllHollowRecordCollection<MapOfFlagsFirstDisplayDatesHollow>(getDataAccess().getTypeDataAccess("MapOfFlagsFirstDisplayDates").getTypeState()) {
            protected MapOfFlagsFirstDisplayDatesHollow getForOrdinal(int ordinal) {
                return getMapOfFlagsFirstDisplayDatesHollow(ordinal);
            }
        };
    }
    public MapOfFlagsFirstDisplayDatesHollow getMapOfFlagsFirstDisplayDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(17);
        return (MapOfFlagsFirstDisplayDatesHollow)mapOfFlagsFirstDisplayDatesProvider.getHollowObject(ordinal);
    }
    public Collection<FlagsHollow> getAllFlagsHollow() {
        return new AllHollowRecordCollection<FlagsHollow>(getDataAccess().getTypeDataAccess("Flags").getTypeState()) {
            protected FlagsHollow getForOrdinal(int ordinal) {
                return getFlagsHollow(ordinal);
            }
        };
    }
    public FlagsHollow getFlagsHollow(int ordinal) {
        objectCreationSampler.recordCreation(18);
        return (FlagsHollow)flagsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonCharacterHollow> getAllPersonCharacterHollow() {
        return new AllHollowRecordCollection<PersonCharacterHollow>(getDataAccess().getTypeDataAccess("PersonCharacter").getTypeState()) {
            protected PersonCharacterHollow getForOrdinal(int ordinal) {
                return getPersonCharacterHollow(ordinal);
            }
        };
    }
    public PersonCharacterHollow getPersonCharacterHollow(int ordinal) {
        objectCreationSampler.recordCreation(19);
        return (PersonCharacterHollow)personCharacterProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterListHollow> getAllCharacterListHollow() {
        return new AllHollowRecordCollection<CharacterListHollow>(getDataAccess().getTypeDataAccess("CharacterList").getTypeState()) {
            protected CharacterListHollow getForOrdinal(int ordinal) {
                return getCharacterListHollow(ordinal);
            }
        };
    }
    public CharacterListHollow getCharacterListHollow(int ordinal) {
        objectCreationSampler.recordCreation(20);
        return (CharacterListHollow)characterListProvider.getHollowObject(ordinal);
    }
    public Collection<MovieCharacterPersonHollow> getAllMovieCharacterPersonHollow() {
        return new AllHollowRecordCollection<MovieCharacterPersonHollow>(getDataAccess().getTypeDataAccess("MovieCharacterPerson").getTypeState()) {
            protected MovieCharacterPersonHollow getForOrdinal(int ordinal) {
                return getMovieCharacterPersonHollow(ordinal);
            }
        };
    }
    public MovieCharacterPersonHollow getMovieCharacterPersonHollow(int ordinal) {
        objectCreationSampler.recordCreation(21);
        return (MovieCharacterPersonHollow)movieCharacterPersonProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoAliasIdHollow> getAllPersonVideoAliasIdHollow() {
        return new AllHollowRecordCollection<PersonVideoAliasIdHollow>(getDataAccess().getTypeDataAccess("PersonVideoAliasId").getTypeState()) {
            protected PersonVideoAliasIdHollow getForOrdinal(int ordinal) {
                return getPersonVideoAliasIdHollow(ordinal);
            }
        };
    }
    public PersonVideoAliasIdHollow getPersonVideoAliasIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(22);
        return (PersonVideoAliasIdHollow)personVideoAliasIdProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoAliasIdsListHollow> getAllPersonVideoAliasIdsListHollow() {
        return new AllHollowRecordCollection<PersonVideoAliasIdsListHollow>(getDataAccess().getTypeDataAccess("PersonVideoAliasIdsList").getTypeState()) {
            protected PersonVideoAliasIdsListHollow getForOrdinal(int ordinal) {
                return getPersonVideoAliasIdsListHollow(ordinal);
            }
        };
    }
    public PersonVideoAliasIdsListHollow getPersonVideoAliasIdsListHollow(int ordinal) {
        objectCreationSampler.recordCreation(23);
        return (PersonVideoAliasIdsListHollow)personVideoAliasIdsListProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoRoleHollow> getAllPersonVideoRoleHollow() {
        return new AllHollowRecordCollection<PersonVideoRoleHollow>(getDataAccess().getTypeDataAccess("PersonVideoRole").getTypeState()) {
            protected PersonVideoRoleHollow getForOrdinal(int ordinal) {
                return getPersonVideoRoleHollow(ordinal);
            }
        };
    }
    public PersonVideoRoleHollow getPersonVideoRoleHollow(int ordinal) {
        objectCreationSampler.recordCreation(24);
        return (PersonVideoRoleHollow)personVideoRoleProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoRolesListHollow> getAllPersonVideoRolesListHollow() {
        return new AllHollowRecordCollection<PersonVideoRolesListHollow>(getDataAccess().getTypeDataAccess("PersonVideoRolesList").getTypeState()) {
            protected PersonVideoRolesListHollow getForOrdinal(int ordinal) {
                return getPersonVideoRolesListHollow(ordinal);
            }
        };
    }
    public PersonVideoRolesListHollow getPersonVideoRolesListHollow(int ordinal) {
        objectCreationSampler.recordCreation(25);
        return (PersonVideoRolesListHollow)personVideoRolesListProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoHollow> getAllPersonVideoHollow() {
        return new AllHollowRecordCollection<PersonVideoHollow>(getDataAccess().getTypeDataAccess("PersonVideo").getTypeState()) {
            protected PersonVideoHollow getForOrdinal(int ordinal) {
                return getPersonVideoHollow(ordinal);
            }
        };
    }
    public PersonVideoHollow getPersonVideoHollow(int ordinal) {
        objectCreationSampler.recordCreation(26);
        return (PersonVideoHollow)personVideoProvider.getHollowObject(ordinal);
    }
    public Collection<RightsAssetSetIdHollow> getAllRightsAssetSetIdHollow() {
        return new AllHollowRecordCollection<RightsAssetSetIdHollow>(getDataAccess().getTypeDataAccess("RightsAssetSetId").getTypeState()) {
            protected RightsAssetSetIdHollow getForOrdinal(int ordinal) {
                return getRightsAssetSetIdHollow(ordinal);
            }
        };
    }
    public RightsAssetSetIdHollow getRightsAssetSetIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(27);
        return (RightsAssetSetIdHollow)rightsAssetSetIdProvider.getHollowObject(ordinal);
    }
    public Collection<RightsContractPackageHollow> getAllRightsContractPackageHollow() {
        return new AllHollowRecordCollection<RightsContractPackageHollow>(getDataAccess().getTypeDataAccess("RightsContractPackage").getTypeState()) {
            protected RightsContractPackageHollow getForOrdinal(int ordinal) {
                return getRightsContractPackageHollow(ordinal);
            }
        };
    }
    public RightsContractPackageHollow getRightsContractPackageHollow(int ordinal) {
        objectCreationSampler.recordCreation(28);
        return (RightsContractPackageHollow)rightsContractPackageProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsContractPackageHollow> getAllListOfRightsContractPackageHollow() {
        return new AllHollowRecordCollection<ListOfRightsContractPackageHollow>(getDataAccess().getTypeDataAccess("ListOfRightsContractPackage").getTypeState()) {
            protected ListOfRightsContractPackageHollow getForOrdinal(int ordinal) {
                return getListOfRightsContractPackageHollow(ordinal);
            }
        };
    }
    public ListOfRightsContractPackageHollow getListOfRightsContractPackageHollow(int ordinal) {
        objectCreationSampler.recordCreation(29);
        return (ListOfRightsContractPackageHollow)listOfRightsContractPackageProvider.getHollowObject(ordinal);
    }
    public Collection<RightsWindowContractHollow> getAllRightsWindowContractHollow() {
        return new AllHollowRecordCollection<RightsWindowContractHollow>(getDataAccess().getTypeDataAccess("RightsWindowContract").getTypeState()) {
            protected RightsWindowContractHollow getForOrdinal(int ordinal) {
                return getRightsWindowContractHollow(ordinal);
            }
        };
    }
    public RightsWindowContractHollow getRightsWindowContractHollow(int ordinal) {
        objectCreationSampler.recordCreation(30);
        return (RightsWindowContractHollow)rightsWindowContractProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsWindowContractHollow> getAllListOfRightsWindowContractHollow() {
        return new AllHollowRecordCollection<ListOfRightsWindowContractHollow>(getDataAccess().getTypeDataAccess("ListOfRightsWindowContract").getTypeState()) {
            protected ListOfRightsWindowContractHollow getForOrdinal(int ordinal) {
                return getListOfRightsWindowContractHollow(ordinal);
            }
        };
    }
    public ListOfRightsWindowContractHollow getListOfRightsWindowContractHollow(int ordinal) {
        objectCreationSampler.recordCreation(31);
        return (ListOfRightsWindowContractHollow)listOfRightsWindowContractProvider.getHollowObject(ordinal);
    }
    public Collection<RightsWindowHollow> getAllRightsWindowHollow() {
        return new AllHollowRecordCollection<RightsWindowHollow>(getDataAccess().getTypeDataAccess("RightsWindow").getTypeState()) {
            protected RightsWindowHollow getForOrdinal(int ordinal) {
                return getRightsWindowHollow(ordinal);
            }
        };
    }
    public RightsWindowHollow getRightsWindowHollow(int ordinal) {
        objectCreationSampler.recordCreation(32);
        return (RightsWindowHollow)rightsWindowProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsWindowHollow> getAllListOfRightsWindowHollow() {
        return new AllHollowRecordCollection<ListOfRightsWindowHollow>(getDataAccess().getTypeDataAccess("ListOfRightsWindow").getTypeState()) {
            protected ListOfRightsWindowHollow getForOrdinal(int ordinal) {
                return getListOfRightsWindowHollow(ordinal);
            }
        };
    }
    public ListOfRightsWindowHollow getListOfRightsWindowHollow(int ordinal) {
        objectCreationSampler.recordCreation(33);
        return (ListOfRightsWindowHollow)listOfRightsWindowProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseWindowHollow> getAllRolloutPhaseWindowHollow() {
        return new AllHollowRecordCollection<RolloutPhaseWindowHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseWindow").getTypeState()) {
            protected RolloutPhaseWindowHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseWindowHollow(ordinal);
            }
        };
    }
    public RolloutPhaseWindowHollow getRolloutPhaseWindowHollow(int ordinal) {
        objectCreationSampler.recordCreation(34);
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
        objectCreationSampler.recordCreation(35);
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
        objectCreationSampler.recordCreation(36);
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
        objectCreationSampler.recordCreation(37);
        return (SeasonListHollow)seasonListProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypeHollow> getAllShowMemberTypeHollow() {
        return new AllHollowRecordCollection<ShowMemberTypeHollow>(getDataAccess().getTypeDataAccess("ShowMemberType").getTypeState()) {
            protected ShowMemberTypeHollow getForOrdinal(int ordinal) {
                return getShowMemberTypeHollow(ordinal);
            }
        };
    }
    public ShowMemberTypeHollow getShowMemberTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(38);
        return (ShowMemberTypeHollow)showMemberTypeProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypeListHollow> getAllShowMemberTypeListHollow() {
        return new AllHollowRecordCollection<ShowMemberTypeListHollow>(getDataAccess().getTypeDataAccess("ShowMemberTypeList").getTypeState()) {
            protected ShowMemberTypeListHollow getForOrdinal(int ordinal) {
                return getShowMemberTypeListHollow(ordinal);
            }
        };
    }
    public ShowMemberTypeListHollow getShowMemberTypeListHollow(int ordinal) {
        objectCreationSampler.recordCreation(39);
        return (ShowMemberTypeListHollow)showMemberTypeListProvider.getHollowObject(ordinal);
    }
    public Collection<ShowCountryLabelHollow> getAllShowCountryLabelHollow() {
        return new AllHollowRecordCollection<ShowCountryLabelHollow>(getDataAccess().getTypeDataAccess("ShowCountryLabel").getTypeState()) {
            protected ShowCountryLabelHollow getForOrdinal(int ordinal) {
                return getShowCountryLabelHollow(ordinal);
            }
        };
    }
    public ShowCountryLabelHollow getShowCountryLabelHollow(int ordinal) {
        objectCreationSampler.recordCreation(40);
        return (ShowCountryLabelHollow)showCountryLabelProvider.getHollowObject(ordinal);
    }
    public Collection<ShowSeasonEpisodeHollow> getAllShowSeasonEpisodeHollow() {
        return new AllHollowRecordCollection<ShowSeasonEpisodeHollow>(getDataAccess().getTypeDataAccess("ShowSeasonEpisode").getTypeState()) {
            protected ShowSeasonEpisodeHollow getForOrdinal(int ordinal) {
                return getShowSeasonEpisodeHollow(ordinal);
            }
        };
    }
    public ShowSeasonEpisodeHollow getShowSeasonEpisodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(41);
        return (ShowSeasonEpisodeHollow)showSeasonEpisodeProvider.getHollowObject(ordinal);
    }
    public Collection<StreamAssetMetadataHollow> getAllStreamAssetMetadataHollow() {
        return new AllHollowRecordCollection<StreamAssetMetadataHollow>(getDataAccess().getTypeDataAccess("StreamAssetMetadata").getTypeState()) {
            protected StreamAssetMetadataHollow getForOrdinal(int ordinal) {
                return getStreamAssetMetadataHollow(ordinal);
            }
        };
    }
    public StreamAssetMetadataHollow getStreamAssetMetadataHollow(int ordinal) {
        objectCreationSampler.recordCreation(42);
        return (StreamAssetMetadataHollow)streamAssetMetadataProvider.getHollowObject(ordinal);
    }
    public Collection<StreamDimensionsHollow> getAllStreamDimensionsHollow() {
        return new AllHollowRecordCollection<StreamDimensionsHollow>(getDataAccess().getTypeDataAccess("StreamDimensions").getTypeState()) {
            protected StreamDimensionsHollow getForOrdinal(int ordinal) {
                return getStreamDimensionsHollow(ordinal);
            }
        };
    }
    public StreamDimensionsHollow getStreamDimensionsHollow(int ordinal) {
        objectCreationSampler.recordCreation(43);
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
        objectCreationSampler.recordCreation(44);
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
        objectCreationSampler.recordCreation(45);
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
        objectCreationSampler.recordCreation(46);
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
        objectCreationSampler.recordCreation(47);
        return (StringHollow)stringProvider.getHollowObject(ordinal);
    }
    public Collection<AbsoluteScheduleHollow> getAllAbsoluteScheduleHollow() {
        return new AllHollowRecordCollection<AbsoluteScheduleHollow>(getDataAccess().getTypeDataAccess("AbsoluteSchedule").getTypeState()) {
            protected AbsoluteScheduleHollow getForOrdinal(int ordinal) {
                return getAbsoluteScheduleHollow(ordinal);
            }
        };
    }
    public AbsoluteScheduleHollow getAbsoluteScheduleHollow(int ordinal) {
        objectCreationSampler.recordCreation(48);
        return (AbsoluteScheduleHollow)absoluteScheduleProvider.getHollowObject(ordinal);
    }
    public Collection<ArtWorkImageTypeHollow> getAllArtWorkImageTypeHollow() {
        return new AllHollowRecordCollection<ArtWorkImageTypeHollow>(getDataAccess().getTypeDataAccess("ArtWorkImageType").getTypeState()) {
            protected ArtWorkImageTypeHollow getForOrdinal(int ordinal) {
                return getArtWorkImageTypeHollow(ordinal);
            }
        };
    }
    public ArtWorkImageTypeHollow getArtWorkImageTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(49);
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
        objectCreationSampler.recordCreation(50);
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
        objectCreationSampler.recordCreation(51);
        return (AudioStreamInfoHollow)audioStreamInfoProvider.getHollowObject(ordinal);
    }
    public Collection<CSMReviewHollow> getAllCSMReviewHollow() {
        return new AllHollowRecordCollection<CSMReviewHollow>(getDataAccess().getTypeDataAccess("CSMReview").getTypeState()) {
            protected CSMReviewHollow getForOrdinal(int ordinal) {
                return getCSMReviewHollow(ordinal);
            }
        };
    }
    public CSMReviewHollow getCSMReviewHollow(int ordinal) {
        objectCreationSampler.recordCreation(52);
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
        objectCreationSampler.recordCreation(53);
        return (CacheDeploymentIntentHollow)cacheDeploymentIntentProvider.getHollowObject(ordinal);
    }
    public Collection<CdnHollow> getAllCdnHollow() {
        return new AllHollowRecordCollection<CdnHollow>(getDataAccess().getTypeDataAccess("Cdn").getTypeState()) {
            protected CdnHollow getForOrdinal(int ordinal) {
                return getCdnHollow(ordinal);
            }
        };
    }
    public CdnHollow getCdnHollow(int ordinal) {
        objectCreationSampler.recordCreation(54);
        return (CdnHollow)cdnProvider.getHollowObject(ordinal);
    }
    public Collection<CdnDeploymentHollow> getAllCdnDeploymentHollow() {
        return new AllHollowRecordCollection<CdnDeploymentHollow>(getDataAccess().getTypeDataAccess("CdnDeployment").getTypeState()) {
            protected CdnDeploymentHollow getForOrdinal(int ordinal) {
                return getCdnDeploymentHollow(ordinal);
            }
        };
    }
    public CdnDeploymentHollow getCdnDeploymentHollow(int ordinal) {
        objectCreationSampler.recordCreation(55);
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
        objectCreationSampler.recordCreation(56);
        return (CdnDeploymentSetHollow)cdnDeploymentSetProvider.getHollowObject(ordinal);
    }
    public Collection<CertificationSystemRatingHollow> getAllCertificationSystemRatingHollow() {
        return new AllHollowRecordCollection<CertificationSystemRatingHollow>(getDataAccess().getTypeDataAccess("CertificationSystemRating").getTypeState()) {
            protected CertificationSystemRatingHollow getForOrdinal(int ordinal) {
                return getCertificationSystemRatingHollow(ordinal);
            }
        };
    }
    public CertificationSystemRatingHollow getCertificationSystemRatingHollow(int ordinal) {
        objectCreationSampler.recordCreation(57);
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
        objectCreationSampler.recordCreation(58);
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
        objectCreationSampler.recordCreation(59);
        return (CertificationSystemHollow)certificationSystemProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterElementsHollow> getAllCharacterElementsHollow() {
        return new AllHollowRecordCollection<CharacterElementsHollow>(getDataAccess().getTypeDataAccess("CharacterElements").getTypeState()) {
            protected CharacterElementsHollow getForOrdinal(int ordinal) {
                return getCharacterElementsHollow(ordinal);
            }
        };
    }
    public CharacterElementsHollow getCharacterElementsHollow(int ordinal) {
        objectCreationSampler.recordCreation(60);
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
        objectCreationSampler.recordCreation(61);
        return (CharacterHollow)characterProvider.getHollowObject(ordinal);
    }
    public Collection<DamMerchStillsMomentHollow> getAllDamMerchStillsMomentHollow() {
        return new AllHollowRecordCollection<DamMerchStillsMomentHollow>(getDataAccess().getTypeDataAccess("DamMerchStillsMoment").getTypeState()) {
            protected DamMerchStillsMomentHollow getForOrdinal(int ordinal) {
                return getDamMerchStillsMomentHollow(ordinal);
            }
        };
    }
    public DamMerchStillsMomentHollow getDamMerchStillsMomentHollow(int ordinal) {
        objectCreationSampler.recordCreation(62);
        return (DamMerchStillsMomentHollow)damMerchStillsMomentProvider.getHollowObject(ordinal);
    }
    public Collection<DamMerchStillsHollow> getAllDamMerchStillsHollow() {
        return new AllHollowRecordCollection<DamMerchStillsHollow>(getDataAccess().getTypeDataAccess("DamMerchStills").getTypeState()) {
            protected DamMerchStillsHollow getForOrdinal(int ordinal) {
                return getDamMerchStillsHollow(ordinal);
            }
        };
    }
    public DamMerchStillsHollow getDamMerchStillsHollow(int ordinal) {
        objectCreationSampler.recordCreation(63);
        return (DamMerchStillsHollow)damMerchStillsProvider.getHollowObject(ordinal);
    }
    public Collection<DisallowedSubtitleLangCodeHollow> getAllDisallowedSubtitleLangCodeHollow() {
        return new AllHollowRecordCollection<DisallowedSubtitleLangCodeHollow>(getDataAccess().getTypeDataAccess("DisallowedSubtitleLangCode").getTypeState()) {
            protected DisallowedSubtitleLangCodeHollow getForOrdinal(int ordinal) {
                return getDisallowedSubtitleLangCodeHollow(ordinal);
            }
        };
    }
    public DisallowedSubtitleLangCodeHollow getDisallowedSubtitleLangCodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(64);
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
        objectCreationSampler.recordCreation(65);
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
        objectCreationSampler.recordCreation(66);
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
        objectCreationSampler.recordCreation(67);
        return (DisallowedAssetBundlesListHollow)disallowedAssetBundlesListProvider.getHollowObject(ordinal);
    }
    public Collection<ContractHollow> getAllContractHollow() {
        return new AllHollowRecordCollection<ContractHollow>(getDataAccess().getTypeDataAccess("Contract").getTypeState()) {
            protected ContractHollow getForOrdinal(int ordinal) {
                return getContractHollow(ordinal);
            }
        };
    }
    public ContractHollow getContractHollow(int ordinal) {
        objectCreationSampler.recordCreation(68);
        return (ContractHollow)contractProvider.getHollowObject(ordinal);
    }
    public Collection<DrmHeaderInfoHollow> getAllDrmHeaderInfoHollow() {
        return new AllHollowRecordCollection<DrmHeaderInfoHollow>(getDataAccess().getTypeDataAccess("DrmHeaderInfo").getTypeState()) {
            protected DrmHeaderInfoHollow getForOrdinal(int ordinal) {
                return getDrmHeaderInfoHollow(ordinal);
            }
        };
    }
    public DrmHeaderInfoHollow getDrmHeaderInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(69);
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
        objectCreationSampler.recordCreation(70);
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
        objectCreationSampler.recordCreation(71);
        return (DrmSystemIdentifiersHollow)drmSystemIdentifiersProvider.getHollowObject(ordinal);
    }
    public Collection<IPLArtworkDerivativeHollow> getAllIPLArtworkDerivativeHollow() {
        return new AllHollowRecordCollection<IPLArtworkDerivativeHollow>(getDataAccess().getTypeDataAccess("IPLArtworkDerivative").getTypeState()) {
            protected IPLArtworkDerivativeHollow getForOrdinal(int ordinal) {
                return getIPLArtworkDerivativeHollow(ordinal);
            }
        };
    }
    public IPLArtworkDerivativeHollow getIPLArtworkDerivativeHollow(int ordinal) {
        objectCreationSampler.recordCreation(72);
        return (IPLArtworkDerivativeHollow)iPLArtworkDerivativeProvider.getHollowObject(ordinal);
    }
    public Collection<IPLDerivativeSetHollow> getAllIPLDerivativeSetHollow() {
        return new AllHollowRecordCollection<IPLDerivativeSetHollow>(getDataAccess().getTypeDataAccess("IPLDerivativeSet").getTypeState()) {
            protected IPLDerivativeSetHollow getForOrdinal(int ordinal) {
                return getIPLDerivativeSetHollow(ordinal);
            }
        };
    }
    public IPLDerivativeSetHollow getIPLDerivativeSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(73);
        return (IPLDerivativeSetHollow)iPLDerivativeSetProvider.getHollowObject(ordinal);
    }
    public Collection<IPLDerivativeGroupHollow> getAllIPLDerivativeGroupHollow() {
        return new AllHollowRecordCollection<IPLDerivativeGroupHollow>(getDataAccess().getTypeDataAccess("IPLDerivativeGroup").getTypeState()) {
            protected IPLDerivativeGroupHollow getForOrdinal(int ordinal) {
                return getIPLDerivativeGroupHollow(ordinal);
            }
        };
    }
    public IPLDerivativeGroupHollow getIPLDerivativeGroupHollow(int ordinal) {
        objectCreationSampler.recordCreation(74);
        return (IPLDerivativeGroupHollow)iPLDerivativeGroupProvider.getHollowObject(ordinal);
    }
    public Collection<IPLDerivativeGroupSetHollow> getAllIPLDerivativeGroupSetHollow() {
        return new AllHollowRecordCollection<IPLDerivativeGroupSetHollow>(getDataAccess().getTypeDataAccess("IPLDerivativeGroupSet").getTypeState()) {
            protected IPLDerivativeGroupSetHollow getForOrdinal(int ordinal) {
                return getIPLDerivativeGroupSetHollow(ordinal);
            }
        };
    }
    public IPLDerivativeGroupSetHollow getIPLDerivativeGroupSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(75);
        return (IPLDerivativeGroupSetHollow)iPLDerivativeGroupSetProvider.getHollowObject(ordinal);
    }
    public Collection<IPLArtworkDerivativeSetHollow> getAllIPLArtworkDerivativeSetHollow() {
        return new AllHollowRecordCollection<IPLArtworkDerivativeSetHollow>(getDataAccess().getTypeDataAccess("IPLArtworkDerivativeSet").getTypeState()) {
            protected IPLArtworkDerivativeSetHollow getForOrdinal(int ordinal) {
                return getIPLArtworkDerivativeSetHollow(ordinal);
            }
        };
    }
    public IPLArtworkDerivativeSetHollow getIPLArtworkDerivativeSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(76);
        return (IPLArtworkDerivativeSetHollow)iPLArtworkDerivativeSetProvider.getHollowObject(ordinal);
    }
    public Collection<ImageStreamInfoHollow> getAllImageStreamInfoHollow() {
        return new AllHollowRecordCollection<ImageStreamInfoHollow>(getDataAccess().getTypeDataAccess("ImageStreamInfo").getTypeState()) {
            protected ImageStreamInfoHollow getForOrdinal(int ordinal) {
                return getImageStreamInfoHollow(ordinal);
            }
        };
    }
    public ImageStreamInfoHollow getImageStreamInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(77);
        return (ImageStreamInfoHollow)imageStreamInfoProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfContractHollow> getAllListOfContractHollow() {
        return new AllHollowRecordCollection<ListOfContractHollow>(getDataAccess().getTypeDataAccess("ListOfContract").getTypeState()) {
            protected ListOfContractHollow getForOrdinal(int ordinal) {
                return getListOfContractHollow(ordinal);
            }
        };
    }
    public ListOfContractHollow getListOfContractHollow(int ordinal) {
        objectCreationSampler.recordCreation(78);
        return (ListOfContractHollow)listOfContractProvider.getHollowObject(ordinal);
    }
    public Collection<ContractsHollow> getAllContractsHollow() {
        return new AllHollowRecordCollection<ContractsHollow>(getDataAccess().getTypeDataAccess("Contracts").getTypeState()) {
            protected ContractsHollow getForOrdinal(int ordinal) {
                return getContractsHollow(ordinal);
            }
        };
    }
    public ContractsHollow getContractsHollow(int ordinal) {
        objectCreationSampler.recordCreation(79);
        return (ContractsHollow)contractsProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfPackageTagsHollow> getAllListOfPackageTagsHollow() {
        return new AllHollowRecordCollection<ListOfPackageTagsHollow>(getDataAccess().getTypeDataAccess("ListOfPackageTags").getTypeState()) {
            protected ListOfPackageTagsHollow getForOrdinal(int ordinal) {
                return getListOfPackageTagsHollow(ordinal);
            }
        };
    }
    public ListOfPackageTagsHollow getListOfPackageTagsHollow(int ordinal) {
        objectCreationSampler.recordCreation(80);
        return (ListOfPackageTagsHollow)listOfPackageTagsProvider.getHollowObject(ordinal);
    }
    public Collection<DeployablePackagesHollow> getAllDeployablePackagesHollow() {
        return new AllHollowRecordCollection<DeployablePackagesHollow>(getDataAccess().getTypeDataAccess("DeployablePackages").getTypeState()) {
            protected DeployablePackagesHollow getForOrdinal(int ordinal) {
                return getDeployablePackagesHollow(ordinal);
            }
        };
    }
    public DeployablePackagesHollow getDeployablePackagesHollow(int ordinal) {
        objectCreationSampler.recordCreation(81);
        return (DeployablePackagesHollow)deployablePackagesProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfStringHollow> getAllListOfStringHollow() {
        return new AllHollowRecordCollection<ListOfStringHollow>(getDataAccess().getTypeDataAccess("ListOfString").getTypeState()) {
            protected ListOfStringHollow getForOrdinal(int ordinal) {
                return getListOfStringHollow(ordinal);
            }
        };
    }
    public ListOfStringHollow getListOfStringHollow(int ordinal) {
        objectCreationSampler.recordCreation(82);
        return (ListOfStringHollow)listOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<LocaleTerritoryCodeHollow> getAllLocaleTerritoryCodeHollow() {
        return new AllHollowRecordCollection<LocaleTerritoryCodeHollow>(getDataAccess().getTypeDataAccess("LocaleTerritoryCode").getTypeState()) {
            protected LocaleTerritoryCodeHollow getForOrdinal(int ordinal) {
                return getLocaleTerritoryCodeHollow(ordinal);
            }
        };
    }
    public LocaleTerritoryCodeHollow getLocaleTerritoryCodeHollow(int ordinal) {
        objectCreationSampler.recordCreation(83);
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
        objectCreationSampler.recordCreation(84);
        return (LocaleTerritoryCodeListHollow)localeTerritoryCodeListProvider.getHollowObject(ordinal);
    }
    public Collection<ArtworkLocaleHollow> getAllArtworkLocaleHollow() {
        return new AllHollowRecordCollection<ArtworkLocaleHollow>(getDataAccess().getTypeDataAccess("ArtworkLocale").getTypeState()) {
            protected ArtworkLocaleHollow getForOrdinal(int ordinal) {
                return getArtworkLocaleHollow(ordinal);
            }
        };
    }
    public ArtworkLocaleHollow getArtworkLocaleHollow(int ordinal) {
        objectCreationSampler.recordCreation(85);
        return (ArtworkLocaleHollow)artworkLocaleProvider.getHollowObject(ordinal);
    }
    public Collection<ArtworkLocaleListHollow> getAllArtworkLocaleListHollow() {
        return new AllHollowRecordCollection<ArtworkLocaleListHollow>(getDataAccess().getTypeDataAccess("ArtworkLocaleList").getTypeState()) {
            protected ArtworkLocaleListHollow getForOrdinal(int ordinal) {
                return getArtworkLocaleListHollow(ordinal);
            }
        };
    }
    public ArtworkLocaleListHollow getArtworkLocaleListHollow(int ordinal) {
        objectCreationSampler.recordCreation(86);
        return (ArtworkLocaleListHollow)artworkLocaleListProvider.getHollowObject(ordinal);
    }
    public Collection<MasterScheduleHollow> getAllMasterScheduleHollow() {
        return new AllHollowRecordCollection<MasterScheduleHollow>(getDataAccess().getTypeDataAccess("MasterSchedule").getTypeState()) {
            protected MasterScheduleHollow getForOrdinal(int ordinal) {
                return getMasterScheduleHollow(ordinal);
            }
        };
    }
    public MasterScheduleHollow getMasterScheduleHollow(int ordinal) {
        objectCreationSampler.recordCreation(87);
        return (MasterScheduleHollow)masterScheduleProvider.getHollowObject(ordinal);
    }
    public Collection<MultiValuePassthroughMapHollow> getAllMultiValuePassthroughMapHollow() {
        return new AllHollowRecordCollection<MultiValuePassthroughMapHollow>(getDataAccess().getTypeDataAccess("MultiValuePassthroughMap").getTypeState()) {
            protected MultiValuePassthroughMapHollow getForOrdinal(int ordinal) {
                return getMultiValuePassthroughMapHollow(ordinal);
            }
        };
    }
    public MultiValuePassthroughMapHollow getMultiValuePassthroughMapHollow(int ordinal) {
        objectCreationSampler.recordCreation(88);
        return (MultiValuePassthroughMapHollow)multiValuePassthroughMapProvider.getHollowObject(ordinal);
    }
    public Collection<OriginServerHollow> getAllOriginServerHollow() {
        return new AllHollowRecordCollection<OriginServerHollow>(getDataAccess().getTypeDataAccess("OriginServer").getTypeState()) {
            protected OriginServerHollow getForOrdinal(int ordinal) {
                return getOriginServerHollow(ordinal);
            }
        };
    }
    public OriginServerHollow getOriginServerHollow(int ordinal) {
        objectCreationSampler.recordCreation(89);
        return (OriginServerHollow)originServerProvider.getHollowObject(ordinal);
    }
    public Collection<OverrideScheduleHollow> getAllOverrideScheduleHollow() {
        return new AllHollowRecordCollection<OverrideScheduleHollow>(getDataAccess().getTypeDataAccess("OverrideSchedule").getTypeState()) {
            protected OverrideScheduleHollow getForOrdinal(int ordinal) {
                return getOverrideScheduleHollow(ordinal);
            }
        };
    }
    public OverrideScheduleHollow getOverrideScheduleHollow(int ordinal) {
        objectCreationSampler.recordCreation(90);
        return (OverrideScheduleHollow)overrideScheduleProvider.getHollowObject(ordinal);
    }
    public Collection<PackageDrmInfoHollow> getAllPackageDrmInfoHollow() {
        return new AllHollowRecordCollection<PackageDrmInfoHollow>(getDataAccess().getTypeDataAccess("PackageDrmInfo").getTypeState()) {
            protected PackageDrmInfoHollow getForOrdinal(int ordinal) {
                return getPackageDrmInfoHollow(ordinal);
            }
        };
    }
    public PackageDrmInfoHollow getPackageDrmInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(91);
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
        objectCreationSampler.recordCreation(92);
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
        objectCreationSampler.recordCreation(93);
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
        objectCreationSampler.recordCreation(94);
        return (PackageMomentListHollow)packageMomentListProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseTagHollow> getAllPhaseTagHollow() {
        return new AllHollowRecordCollection<PhaseTagHollow>(getDataAccess().getTypeDataAccess("PhaseTag").getTypeState()) {
            protected PhaseTagHollow getForOrdinal(int ordinal) {
                return getPhaseTagHollow(ordinal);
            }
        };
    }
    public PhaseTagHollow getPhaseTagHollow(int ordinal) {
        objectCreationSampler.recordCreation(95);
        return (PhaseTagHollow)phaseTagProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseTagListHollow> getAllPhaseTagListHollow() {
        return new AllHollowRecordCollection<PhaseTagListHollow>(getDataAccess().getTypeDataAccess("PhaseTagList").getTypeState()) {
            protected PhaseTagListHollow getForOrdinal(int ordinal) {
                return getPhaseTagListHollow(ordinal);
            }
        };
    }
    public PhaseTagListHollow getPhaseTagListHollow(int ordinal) {
        objectCreationSampler.recordCreation(96);
        return (PhaseTagListHollow)phaseTagListProvider.getHollowObject(ordinal);
    }
    public Collection<ProtectionTypesHollow> getAllProtectionTypesHollow() {
        return new AllHollowRecordCollection<ProtectionTypesHollow>(getDataAccess().getTypeDataAccess("ProtectionTypes").getTypeState()) {
            protected ProtectionTypesHollow getForOrdinal(int ordinal) {
                return getProtectionTypesHollow(ordinal);
            }
        };
    }
    public ProtectionTypesHollow getProtectionTypesHollow(int ordinal) {
        objectCreationSampler.recordCreation(97);
        return (ProtectionTypesHollow)protectionTypesProvider.getHollowObject(ordinal);
    }
    public Collection<ReleaseDateHollow> getAllReleaseDateHollow() {
        return new AllHollowRecordCollection<ReleaseDateHollow>(getDataAccess().getTypeDataAccess("ReleaseDate").getTypeState()) {
            protected ReleaseDateHollow getForOrdinal(int ordinal) {
                return getReleaseDateHollow(ordinal);
            }
        };
    }
    public ReleaseDateHollow getReleaseDateHollow(int ordinal) {
        objectCreationSampler.recordCreation(98);
        return (ReleaseDateHollow)releaseDateProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfReleaseDatesHollow> getAllListOfReleaseDatesHollow() {
        return new AllHollowRecordCollection<ListOfReleaseDatesHollow>(getDataAccess().getTypeDataAccess("ListOfReleaseDates").getTypeState()) {
            protected ListOfReleaseDatesHollow getForOrdinal(int ordinal) {
                return getListOfReleaseDatesHollow(ordinal);
            }
        };
    }
    public ListOfReleaseDatesHollow getListOfReleaseDatesHollow(int ordinal) {
        objectCreationSampler.recordCreation(99);
        return (ListOfReleaseDatesHollow)listOfReleaseDatesProvider.getHollowObject(ordinal);
    }
    public Collection<RightsAssetHollow> getAllRightsAssetHollow() {
        return new AllHollowRecordCollection<RightsAssetHollow>(getDataAccess().getTypeDataAccess("RightsAsset").getTypeState()) {
            protected RightsAssetHollow getForOrdinal(int ordinal) {
                return getRightsAssetHollow(ordinal);
            }
        };
    }
    public RightsAssetHollow getRightsAssetHollow(int ordinal) {
        objectCreationSampler.recordCreation(100);
        return (RightsAssetHollow)rightsAssetProvider.getHollowObject(ordinal);
    }
    public Collection<RightsContractAssetHollow> getAllRightsContractAssetHollow() {
        return new AllHollowRecordCollection<RightsContractAssetHollow>(getDataAccess().getTypeDataAccess("RightsContractAsset").getTypeState()) {
            protected RightsContractAssetHollow getForOrdinal(int ordinal) {
                return getRightsContractAssetHollow(ordinal);
            }
        };
    }
    public RightsContractAssetHollow getRightsContractAssetHollow(int ordinal) {
        objectCreationSampler.recordCreation(101);
        return (RightsContractAssetHollow)rightsContractAssetProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsContractAssetHollow> getAllListOfRightsContractAssetHollow() {
        return new AllHollowRecordCollection<ListOfRightsContractAssetHollow>(getDataAccess().getTypeDataAccess("ListOfRightsContractAsset").getTypeState()) {
            protected ListOfRightsContractAssetHollow getForOrdinal(int ordinal) {
                return getListOfRightsContractAssetHollow(ordinal);
            }
        };
    }
    public ListOfRightsContractAssetHollow getListOfRightsContractAssetHollow(int ordinal) {
        objectCreationSampler.recordCreation(102);
        return (ListOfRightsContractAssetHollow)listOfRightsContractAssetProvider.getHollowObject(ordinal);
    }
    public Collection<RightsContractHollow> getAllRightsContractHollow() {
        return new AllHollowRecordCollection<RightsContractHollow>(getDataAccess().getTypeDataAccess("RightsContract").getTypeState()) {
            protected RightsContractHollow getForOrdinal(int ordinal) {
                return getRightsContractHollow(ordinal);
            }
        };
    }
    public RightsContractHollow getRightsContractHollow(int ordinal) {
        objectCreationSampler.recordCreation(103);
        return (RightsContractHollow)rightsContractProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsContractHollow> getAllListOfRightsContractHollow() {
        return new AllHollowRecordCollection<ListOfRightsContractHollow>(getDataAccess().getTypeDataAccess("ListOfRightsContract").getTypeState()) {
            protected ListOfRightsContractHollow getForOrdinal(int ordinal) {
                return getListOfRightsContractHollow(ordinal);
            }
        };
    }
    public ListOfRightsContractHollow getListOfRightsContractHollow(int ordinal) {
        objectCreationSampler.recordCreation(104);
        return (ListOfRightsContractHollow)listOfRightsContractProvider.getHollowObject(ordinal);
    }
    public Collection<RightsHollow> getAllRightsHollow() {
        return new AllHollowRecordCollection<RightsHollow>(getDataAccess().getTypeDataAccess("Rights").getTypeState()) {
            protected RightsHollow getForOrdinal(int ordinal) {
                return getRightsHollow(ordinal);
            }
        };
    }
    public RightsHollow getRightsHollow(int ordinal) {
        objectCreationSampler.recordCreation(105);
        return (RightsHollow)rightsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseArtworkSourceFileIdHollow> getAllRolloutPhaseArtworkSourceFileIdHollow() {
        return new AllHollowRecordCollection<RolloutPhaseArtworkSourceFileIdHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseArtworkSourceFileId").getTypeState()) {
            protected RolloutPhaseArtworkSourceFileIdHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
            }
        };
    }
    public RolloutPhaseArtworkSourceFileIdHollow getRolloutPhaseArtworkSourceFileIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(106);
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
        objectCreationSampler.recordCreation(107);
        return (RolloutPhaseArtworkSourceFileIdListHollow)rolloutPhaseArtworkSourceFileIdListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseArtworkHollow> getAllRolloutPhaseArtworkHollow() {
        return new AllHollowRecordCollection<RolloutPhaseArtworkHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseArtwork").getTypeState()) {
            protected RolloutPhaseArtworkHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseArtworkHollow(ordinal);
            }
        };
    }
    public RolloutPhaseArtworkHollow getRolloutPhaseArtworkHollow(int ordinal) {
        objectCreationSampler.recordCreation(108);
        return (RolloutPhaseArtworkHollow)rolloutPhaseArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseLocalizedMetadataHollow> getAllRolloutPhaseLocalizedMetadataHollow() {
        return new AllHollowRecordCollection<RolloutPhaseLocalizedMetadataHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseLocalizedMetadata").getTypeState()) {
            protected RolloutPhaseLocalizedMetadataHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseLocalizedMetadataHollow(ordinal);
            }
        };
    }
    public RolloutPhaseLocalizedMetadataHollow getRolloutPhaseLocalizedMetadataHollow(int ordinal) {
        objectCreationSampler.recordCreation(109);
        return (RolloutPhaseLocalizedMetadataHollow)rolloutPhaseLocalizedMetadataProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseElementsHollow> getAllRolloutPhaseElementsHollow() {
        return new AllHollowRecordCollection<RolloutPhaseElementsHollow>(getDataAccess().getTypeDataAccess("RolloutPhaseElements").getTypeState()) {
            protected RolloutPhaseElementsHollow getForOrdinal(int ordinal) {
                return getRolloutPhaseElementsHollow(ordinal);
            }
        };
    }
    public RolloutPhaseElementsHollow getRolloutPhaseElementsHollow(int ordinal) {
        objectCreationSampler.recordCreation(110);
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
        objectCreationSampler.recordCreation(111);
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
        objectCreationSampler.recordCreation(112);
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
        objectCreationSampler.recordCreation(113);
        return (RolloutHollow)rolloutProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfRightsAssetHollow> getAllSetOfRightsAssetHollow() {
        return new AllHollowRecordCollection<SetOfRightsAssetHollow>(getDataAccess().getTypeDataAccess("SetOfRightsAsset").getTypeState()) {
            protected SetOfRightsAssetHollow getForOrdinal(int ordinal) {
                return getSetOfRightsAssetHollow(ordinal);
            }
        };
    }
    public SetOfRightsAssetHollow getSetOfRightsAssetHollow(int ordinal) {
        objectCreationSampler.recordCreation(114);
        return (SetOfRightsAssetHollow)setOfRightsAssetProvider.getHollowObject(ordinal);
    }
    public Collection<RightsAssetsHollow> getAllRightsAssetsHollow() {
        return new AllHollowRecordCollection<RightsAssetsHollow>(getDataAccess().getTypeDataAccess("RightsAssets").getTypeState()) {
            protected RightsAssetsHollow getForOrdinal(int ordinal) {
                return getRightsAssetsHollow(ordinal);
            }
        };
    }
    public RightsAssetsHollow getRightsAssetsHollow(int ordinal) {
        objectCreationSampler.recordCreation(115);
        return (RightsAssetsHollow)rightsAssetsProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfStringHollow> getAllSetOfStringHollow() {
        return new AllHollowRecordCollection<SetOfStringHollow>(getDataAccess().getTypeDataAccess("SetOfString").getTypeState()) {
            protected SetOfStringHollow getForOrdinal(int ordinal) {
                return getSetOfStringHollow(ordinal);
            }
        };
    }
    public SetOfStringHollow getSetOfStringHollow(int ordinal) {
        objectCreationSampler.recordCreation(116);
        return (SetOfStringHollow)setOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<SingleValuePassthroughMapHollow> getAllSingleValuePassthroughMapHollow() {
        return new AllHollowRecordCollection<SingleValuePassthroughMapHollow>(getDataAccess().getTypeDataAccess("SingleValuePassthroughMap").getTypeState()) {
            protected SingleValuePassthroughMapHollow getForOrdinal(int ordinal) {
                return getSingleValuePassthroughMapHollow(ordinal);
            }
        };
    }
    public SingleValuePassthroughMapHollow getSingleValuePassthroughMapHollow(int ordinal) {
        objectCreationSampler.recordCreation(117);
        return (SingleValuePassthroughMapHollow)singleValuePassthroughMapProvider.getHollowObject(ordinal);
    }
    public Collection<PassthroughDataHollow> getAllPassthroughDataHollow() {
        return new AllHollowRecordCollection<PassthroughDataHollow>(getDataAccess().getTypeDataAccess("PassthroughData").getTypeState()) {
            protected PassthroughDataHollow getForOrdinal(int ordinal) {
                return getPassthroughDataHollow(ordinal);
            }
        };
    }
    public PassthroughDataHollow getPassthroughDataHollow(int ordinal) {
        objectCreationSampler.recordCreation(118);
        return (PassthroughDataHollow)passthroughDataProvider.getHollowObject(ordinal);
    }
    public Collection<ArtworkAttributesHollow> getAllArtworkAttributesHollow() {
        return new AllHollowRecordCollection<ArtworkAttributesHollow>(getDataAccess().getTypeDataAccess("ArtworkAttributes").getTypeState()) {
            protected ArtworkAttributesHollow getForOrdinal(int ordinal) {
                return getArtworkAttributesHollow(ordinal);
            }
        };
    }
    public ArtworkAttributesHollow getArtworkAttributesHollow(int ordinal) {
        objectCreationSampler.recordCreation(119);
        return (ArtworkAttributesHollow)artworkAttributesProvider.getHollowObject(ordinal);
    }
    public Collection<CharacterArtworkSourceHollow> getAllCharacterArtworkSourceHollow() {
        return new AllHollowRecordCollection<CharacterArtworkSourceHollow>(getDataAccess().getTypeDataAccess("CharacterArtworkSource").getTypeState()) {
            protected CharacterArtworkSourceHollow getForOrdinal(int ordinal) {
                return getCharacterArtworkSourceHollow(ordinal);
            }
        };
    }
    public CharacterArtworkSourceHollow getCharacterArtworkSourceHollow(int ordinal) {
        objectCreationSampler.recordCreation(120);
        return (CharacterArtworkSourceHollow)characterArtworkSourceProvider.getHollowObject(ordinal);
    }
    public Collection<IndividualSupplementalHollow> getAllIndividualSupplementalHollow() {
        return new AllHollowRecordCollection<IndividualSupplementalHollow>(getDataAccess().getTypeDataAccess("IndividualSupplemental").getTypeState()) {
            protected IndividualSupplementalHollow getForOrdinal(int ordinal) {
                return getIndividualSupplementalHollow(ordinal);
            }
        };
    }
    public IndividualSupplementalHollow getIndividualSupplementalHollow(int ordinal) {
        objectCreationSampler.recordCreation(121);
        return (IndividualSupplementalHollow)individualSupplementalProvider.getHollowObject(ordinal);
    }
    public Collection<PersonArtworkSourceHollow> getAllPersonArtworkSourceHollow() {
        return new AllHollowRecordCollection<PersonArtworkSourceHollow>(getDataAccess().getTypeDataAccess("PersonArtworkSource").getTypeState()) {
            protected PersonArtworkSourceHollow getForOrdinal(int ordinal) {
                return getPersonArtworkSourceHollow(ordinal);
            }
        };
    }
    public PersonArtworkSourceHollow getPersonArtworkSourceHollow(int ordinal) {
        objectCreationSampler.recordCreation(122);
        return (PersonArtworkSourceHollow)personArtworkSourceProvider.getHollowObject(ordinal);
    }
    public Collection<StatusHollow> getAllStatusHollow() {
        return new AllHollowRecordCollection<StatusHollow>(getDataAccess().getTypeDataAccess("Status").getTypeState()) {
            protected StatusHollow getForOrdinal(int ordinal) {
                return getStatusHollow(ordinal);
            }
        };
    }
    public StatusHollow getStatusHollow(int ordinal) {
        objectCreationSampler.recordCreation(123);
        return (StatusHollow)statusProvider.getHollowObject(ordinal);
    }
    public Collection<StorageGroupsHollow> getAllStorageGroupsHollow() {
        return new AllHollowRecordCollection<StorageGroupsHollow>(getDataAccess().getTypeDataAccess("StorageGroups").getTypeState()) {
            protected StorageGroupsHollow getForOrdinal(int ordinal) {
                return getStorageGroupsHollow(ordinal);
            }
        };
    }
    public StorageGroupsHollow getStorageGroupsHollow(int ordinal) {
        objectCreationSampler.recordCreation(124);
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
        objectCreationSampler.recordCreation(125);
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
        objectCreationSampler.recordCreation(126);
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
        objectCreationSampler.recordCreation(127);
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
        objectCreationSampler.recordCreation(128);
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
        objectCreationSampler.recordCreation(129);
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
        objectCreationSampler.recordCreation(130);
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
        objectCreationSampler.recordCreation(131);
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
        objectCreationSampler.recordCreation(132);
        return (StreamProfilesHollow)streamProfilesProvider.getHollowObject(ordinal);
    }
    public Collection<SupplementalsListHollow> getAllSupplementalsListHollow() {
        return new AllHollowRecordCollection<SupplementalsListHollow>(getDataAccess().getTypeDataAccess("SupplementalsList").getTypeState()) {
            protected SupplementalsListHollow getForOrdinal(int ordinal) {
                return getSupplementalsListHollow(ordinal);
            }
        };
    }
    public SupplementalsListHollow getSupplementalsListHollow(int ordinal) {
        objectCreationSampler.recordCreation(133);
        return (SupplementalsListHollow)supplementalsListProvider.getHollowObject(ordinal);
    }
    public Collection<SupplementalsHollow> getAllSupplementalsHollow() {
        return new AllHollowRecordCollection<SupplementalsHollow>(getDataAccess().getTypeDataAccess("Supplementals").getTypeState()) {
            protected SupplementalsHollow getForOrdinal(int ordinal) {
                return getSupplementalsHollow(ordinal);
            }
        };
    }
    public SupplementalsHollow getSupplementalsHollow(int ordinal) {
        objectCreationSampler.recordCreation(134);
        return (SupplementalsHollow)supplementalsProvider.getHollowObject(ordinal);
    }
    public Collection<TerritoryCountriesHollow> getAllTerritoryCountriesHollow() {
        return new AllHollowRecordCollection<TerritoryCountriesHollow>(getDataAccess().getTypeDataAccess("TerritoryCountries").getTypeState()) {
            protected TerritoryCountriesHollow getForOrdinal(int ordinal) {
                return getTerritoryCountriesHollow(ordinal);
            }
        };
    }
    public TerritoryCountriesHollow getTerritoryCountriesHollow(int ordinal) {
        objectCreationSampler.recordCreation(135);
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
        objectCreationSampler.recordCreation(136);
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
        objectCreationSampler.recordCreation(137);
        return (TopNAttributeHollow)topNAttributeProvider.getHollowObject(ordinal);
    }
    public Collection<TopNAttributesSetHollow> getAllTopNAttributesSetHollow() {
        return new AllHollowRecordCollection<TopNAttributesSetHollow>(getDataAccess().getTypeDataAccess("TopNAttributesSet").getTypeState()) {
            protected TopNAttributesSetHollow getForOrdinal(int ordinal) {
                return getTopNAttributesSetHollow(ordinal);
            }
        };
    }
    public TopNAttributesSetHollow getTopNAttributesSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(138);
        return (TopNAttributesSetHollow)topNAttributesSetProvider.getHollowObject(ordinal);
    }
    public Collection<TopNHollow> getAllTopNHollow() {
        return new AllHollowRecordCollection<TopNHollow>(getDataAccess().getTypeDataAccess("TopN").getTypeState()) {
            protected TopNHollow getForOrdinal(int ordinal) {
                return getTopNHollow(ordinal);
            }
        };
    }
    public TopNHollow getTopNHollow(int ordinal) {
        objectCreationSampler.recordCreation(139);
        return (TopNHollow)topNProvider.getHollowObject(ordinal);
    }
    public Collection<TranslatedTextValueHollow> getAllTranslatedTextValueHollow() {
        return new AllHollowRecordCollection<TranslatedTextValueHollow>(getDataAccess().getTypeDataAccess("TranslatedTextValue").getTypeState()) {
            protected TranslatedTextValueHollow getForOrdinal(int ordinal) {
                return getTranslatedTextValueHollow(ordinal);
            }
        };
    }
    public TranslatedTextValueHollow getTranslatedTextValueHollow(int ordinal) {
        objectCreationSampler.recordCreation(140);
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
        objectCreationSampler.recordCreation(141);
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
        objectCreationSampler.recordCreation(142);
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
        objectCreationSampler.recordCreation(143);
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
        objectCreationSampler.recordCreation(144);
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
        objectCreationSampler.recordCreation(145);
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
        objectCreationSampler.recordCreation(146);
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
        objectCreationSampler.recordCreation(147);
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
        objectCreationSampler.recordCreation(148);
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
        objectCreationSampler.recordCreation(149);
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
        objectCreationSampler.recordCreation(150);
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
        objectCreationSampler.recordCreation(151);
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
        objectCreationSampler.recordCreation(152);
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
        objectCreationSampler.recordCreation(153);
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
        objectCreationSampler.recordCreation(154);
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
        objectCreationSampler.recordCreation(155);
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
        objectCreationSampler.recordCreation(156);
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
        objectCreationSampler.recordCreation(157);
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
        objectCreationSampler.recordCreation(158);
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
        objectCreationSampler.recordCreation(159);
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
        objectCreationSampler.recordCreation(160);
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
        objectCreationSampler.recordCreation(161);
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
        objectCreationSampler.recordCreation(162);
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
        objectCreationSampler.recordCreation(163);
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
        objectCreationSampler.recordCreation(164);
        return (PersonAliasesHollow)personAliasesProvider.getHollowObject(ordinal);
    }
    public Collection<PersonCharacterResourceHollow> getAllPersonCharacterResourceHollow() {
        return new AllHollowRecordCollection<PersonCharacterResourceHollow>(getDataAccess().getTypeDataAccess("PersonCharacterResource").getTypeState()) {
            protected PersonCharacterResourceHollow getForOrdinal(int ordinal) {
                return getPersonCharacterResourceHollow(ordinal);
            }
        };
    }
    public PersonCharacterResourceHollow getPersonCharacterResourceHollow(int ordinal) {
        objectCreationSampler.recordCreation(165);
        return (PersonCharacterResourceHollow)personCharacterResourceProvider.getHollowObject(ordinal);
    }
    public Collection<PersonsHollow> getAllPersonsHollow() {
        return new AllHollowRecordCollection<PersonsHollow>(getDataAccess().getTypeDataAccess("Persons").getTypeState()) {
            protected PersonsHollow getForOrdinal(int ordinal) {
                return getPersonsHollow(ordinal);
            }
        };
    }
    public PersonsHollow getPersonsHollow(int ordinal) {
        objectCreationSampler.recordCreation(166);
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
        objectCreationSampler.recordCreation(167);
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
        objectCreationSampler.recordCreation(168);
        return (ShowMemberTypesHollow)showMemberTypesProvider.getHollowObject(ordinal);
    }
    public Collection<StoriesSynopsesHollow> getAllStoriesSynopsesHollow() {
        return new AllHollowRecordCollection<StoriesSynopsesHollow>(getDataAccess().getTypeDataAccess("StoriesSynopses").getTypeState()) {
            protected StoriesSynopsesHollow getForOrdinal(int ordinal) {
                return getStoriesSynopsesHollow(ordinal);
            }
        };
    }
    public StoriesSynopsesHollow getStoriesSynopsesHollow(int ordinal) {
        objectCreationSampler.recordCreation(169);
        return (StoriesSynopsesHollow)storiesSynopsesProvider.getHollowObject(ordinal);
    }
    public Collection<TurboCollectionsHollow> getAllTurboCollectionsHollow() {
        return new AllHollowRecordCollection<TurboCollectionsHollow>(getDataAccess().getTypeDataAccess("TurboCollections").getTypeState()) {
            protected TurboCollectionsHollow getForOrdinal(int ordinal) {
                return getTurboCollectionsHollow(ordinal);
            }
        };
    }
    public TurboCollectionsHollow getTurboCollectionsHollow(int ordinal) {
        objectCreationSampler.recordCreation(170);
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
        objectCreationSampler.recordCreation(171);
        return (VMSAwardHollow)vMSAwardProvider.getHollowObject(ordinal);
    }
    public Collection<VideoArtworkSourceHollow> getAllVideoArtworkSourceHollow() {
        return new AllHollowRecordCollection<VideoArtworkSourceHollow>(getDataAccess().getTypeDataAccess("VideoArtworkSource").getTypeState()) {
            protected VideoArtworkSourceHollow getForOrdinal(int ordinal) {
                return getVideoArtworkSourceHollow(ordinal);
            }
        };
    }
    public VideoArtworkSourceHollow getVideoArtworkSourceHollow(int ordinal) {
        objectCreationSampler.recordCreation(172);
        return (VideoArtworkSourceHollow)videoArtworkSourceProvider.getHollowObject(ordinal);
    }
    public Collection<VideoAwardMappingHollow> getAllVideoAwardMappingHollow() {
        return new AllHollowRecordCollection<VideoAwardMappingHollow>(getDataAccess().getTypeDataAccess("VideoAwardMapping").getTypeState()) {
            protected VideoAwardMappingHollow getForOrdinal(int ordinal) {
                return getVideoAwardMappingHollow(ordinal);
            }
        };
    }
    public VideoAwardMappingHollow getVideoAwardMappingHollow(int ordinal) {
        objectCreationSampler.recordCreation(173);
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
        objectCreationSampler.recordCreation(174);
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
        objectCreationSampler.recordCreation(175);
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
        objectCreationSampler.recordCreation(176);
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
        objectCreationSampler.recordCreation(177);
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
        objectCreationSampler.recordCreation(178);
        return (VideoDateHollow)videoDateProvider.getHollowObject(ordinal);
    }
    public Collection<VideoGeneralAliasHollow> getAllVideoGeneralAliasHollow() {
        return new AllHollowRecordCollection<VideoGeneralAliasHollow>(getDataAccess().getTypeDataAccess("VideoGeneralAlias").getTypeState()) {
            protected VideoGeneralAliasHollow getForOrdinal(int ordinal) {
                return getVideoGeneralAliasHollow(ordinal);
            }
        };
    }
    public VideoGeneralAliasHollow getVideoGeneralAliasHollow(int ordinal) {
        objectCreationSampler.recordCreation(179);
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
        objectCreationSampler.recordCreation(180);
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
        objectCreationSampler.recordCreation(181);
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
        objectCreationSampler.recordCreation(182);
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
        objectCreationSampler.recordCreation(183);
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
        objectCreationSampler.recordCreation(184);
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
        objectCreationSampler.recordCreation(185);
        return (VideoGeneralHollow)videoGeneralProvider.getHollowObject(ordinal);
    }
    public Collection<VideoIdHollow> getAllVideoIdHollow() {
        return new AllHollowRecordCollection<VideoIdHollow>(getDataAccess().getTypeDataAccess("VideoId").getTypeState()) {
            protected VideoIdHollow getForOrdinal(int ordinal) {
                return getVideoIdHollow(ordinal);
            }
        };
    }
    public VideoIdHollow getVideoIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(186);
        return (VideoIdHollow)videoIdProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfVideoIdsHollow> getAllListOfVideoIdsHollow() {
        return new AllHollowRecordCollection<ListOfVideoIdsHollow>(getDataAccess().getTypeDataAccess("ListOfVideoIds").getTypeState()) {
            protected ListOfVideoIdsHollow getForOrdinal(int ordinal) {
                return getListOfVideoIdsHollow(ordinal);
            }
        };
    }
    public ListOfVideoIdsHollow getListOfVideoIdsHollow(int ordinal) {
        objectCreationSampler.recordCreation(187);
        return (ListOfVideoIdsHollow)listOfVideoIdsProvider.getHollowObject(ordinal);
    }
    public Collection<PersonBioHollow> getAllPersonBioHollow() {
        return new AllHollowRecordCollection<PersonBioHollow>(getDataAccess().getTypeDataAccess("PersonBio").getTypeState()) {
            protected PersonBioHollow getForOrdinal(int ordinal) {
                return getPersonBioHollow(ordinal);
            }
        };
    }
    public PersonBioHollow getPersonBioHollow(int ordinal) {
        objectCreationSampler.recordCreation(188);
        return (PersonBioHollow)personBioProvider.getHollowObject(ordinal);
    }
    public Collection<VideoRatingAdvisoryIdHollow> getAllVideoRatingAdvisoryIdHollow() {
        return new AllHollowRecordCollection<VideoRatingAdvisoryIdHollow>(getDataAccess().getTypeDataAccess("VideoRatingAdvisoryId").getTypeState()) {
            protected VideoRatingAdvisoryIdHollow getForOrdinal(int ordinal) {
                return getVideoRatingAdvisoryIdHollow(ordinal);
            }
        };
    }
    public VideoRatingAdvisoryIdHollow getVideoRatingAdvisoryIdHollow(int ordinal) {
        objectCreationSampler.recordCreation(189);
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
        objectCreationSampler.recordCreation(190);
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
        objectCreationSampler.recordCreation(191);
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
        objectCreationSampler.recordCreation(192);
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
        objectCreationSampler.recordCreation(193);
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
        objectCreationSampler.recordCreation(194);
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
        objectCreationSampler.recordCreation(195);
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
        objectCreationSampler.recordCreation(196);
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
        objectCreationSampler.recordCreation(197);
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
        objectCreationSampler.recordCreation(198);
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
        objectCreationSampler.recordCreation(199);
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
        objectCreationSampler.recordCreation(200);
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
        objectCreationSampler.recordCreation(201);
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
        objectCreationSampler.recordCreation(202);
        return (VideoRatingHollow)videoRatingProvider.getHollowObject(ordinal);
    }
    public Collection<VideoStreamCropParamsHollow> getAllVideoStreamCropParamsHollow() {
        return new AllHollowRecordCollection<VideoStreamCropParamsHollow>(getDataAccess().getTypeDataAccess("VideoStreamCropParams").getTypeState()) {
            protected VideoStreamCropParamsHollow getForOrdinal(int ordinal) {
                return getVideoStreamCropParamsHollow(ordinal);
            }
        };
    }
    public VideoStreamCropParamsHollow getVideoStreamCropParamsHollow(int ordinal) {
        objectCreationSampler.recordCreation(203);
        return (VideoStreamCropParamsHollow)videoStreamCropParamsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoStreamInfoHollow> getAllVideoStreamInfoHollow() {
        return new AllHollowRecordCollection<VideoStreamInfoHollow>(getDataAccess().getTypeDataAccess("VideoStreamInfo").getTypeState()) {
            protected VideoStreamInfoHollow getForOrdinal(int ordinal) {
                return getVideoStreamInfoHollow(ordinal);
            }
        };
    }
    public VideoStreamInfoHollow getVideoStreamInfoHollow(int ordinal) {
        objectCreationSampler.recordCreation(204);
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
        objectCreationSampler.recordCreation(205);
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
        objectCreationSampler.recordCreation(206);
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
        objectCreationSampler.recordCreation(207);
        return (PackageStreamSetHollow)packageStreamSetProvider.getHollowObject(ordinal);
    }
    public Collection<PackageHollow> getAllPackageHollow() {
        return new AllHollowRecordCollection<PackageHollow>(getDataAccess().getTypeDataAccess("Package").getTypeState()) {
            protected PackageHollow getForOrdinal(int ordinal) {
                return getPackageHollow(ordinal);
            }
        };
    }
    public PackageHollow getPackageHollow(int ordinal) {
        objectCreationSampler.recordCreation(208);
        return (PackageHollow)packageProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeMediaHollow> getAllVideoTypeMediaHollow() {
        return new AllHollowRecordCollection<VideoTypeMediaHollow>(getDataAccess().getTypeDataAccess("VideoTypeMedia").getTypeState()) {
            protected VideoTypeMediaHollow getForOrdinal(int ordinal) {
                return getVideoTypeMediaHollow(ordinal);
            }
        };
    }
    public VideoTypeMediaHollow getVideoTypeMediaHollow(int ordinal) {
        objectCreationSampler.recordCreation(209);
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
        objectCreationSampler.recordCreation(210);
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
        objectCreationSampler.recordCreation(211);
        return (VideoTypeDescriptorHollow)videoTypeDescriptorProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeDescriptorSetHollow> getAllVideoTypeDescriptorSetHollow() {
        return new AllHollowRecordCollection<VideoTypeDescriptorSetHollow>(getDataAccess().getTypeDataAccess("VideoTypeDescriptorSet").getTypeState()) {
            protected VideoTypeDescriptorSetHollow getForOrdinal(int ordinal) {
                return getVideoTypeDescriptorSetHollow(ordinal);
            }
        };
    }
    public VideoTypeDescriptorSetHollow getVideoTypeDescriptorSetHollow(int ordinal) {
        objectCreationSampler.recordCreation(212);
        return (VideoTypeDescriptorSetHollow)videoTypeDescriptorSetProvider.getHollowObject(ordinal);
    }
    public Collection<VideoTypeHollow> getAllVideoTypeHollow() {
        return new AllHollowRecordCollection<VideoTypeHollow>(getDataAccess().getTypeDataAccess("VideoType").getTypeState()) {
            protected VideoTypeHollow getForOrdinal(int ordinal) {
                return getVideoTypeHollow(ordinal);
            }
        };
    }
    public VideoTypeHollow getVideoTypeHollow(int ordinal) {
        objectCreationSampler.recordCreation(213);
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
