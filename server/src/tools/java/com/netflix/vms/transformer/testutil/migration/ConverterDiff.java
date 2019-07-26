package com.netflix.vms.transformer.testutil.migration;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.CONVERTER;
import static java.lang.String.format;

import com.google.inject.Inject;
import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.cinder.lifecycle.CinderConsumerModule;
import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.consumer.VMSInputDataConsumer;
import com.netflix.vms.transformer.input.datasets.slicers.ConverterDataSlicerImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GovernatorJunit4ClassRunner.class)
@ModulesForTesting({CinderConsumerModule.class, RuntimeCoreModule.class})
public class ConverterDiff {
    private static final String LOCAL_BLOB_STORE = "/space/local-blob-store";
    private static final int TARGET_NUMBER_OF_TOPNODES = 1000;

    private static HollowDiffUIServer server;
    private static boolean reuseSliceFiles;

    private boolean isProd;
    private InputDataSlicer slicer;

    @Inject
    private Supplier<CinderConsumerBuilder> cinderConsumerBuilder;

    @BeforeClass
    public static void createServer() throws Exception {
        reuseSliceFiles = true;
        server = new HollowDiffUIServer(8080);
        server.start();
    }

    @Before
    public void setUp() {
        isProd = false;
        slicer = new ConverterDataSlicerImpl();
    }

    @AfterClass
    public static void startServer() throws Exception {
        server.join();
    }

    @Test
    public void pr61_converterCodeCleanup() throws Exception {
        String name = "pr61";
        HollowReadStateEngine from = slice(name, "vmsconverter-muon", 20170315214609500L);
        HollowReadStateEngine to = slice(name, "vmsconverter-pr61", 20170315215144811L);

        server.addDiff(name, createDiff(from, to));
    }

    private HollowReadStateEngine slice(String diff, String converterNamespace, long version) throws IOException {
        File sliceFile = localBlobStore(isProd).resolve(format("vms.%s-%s_sliced-%d", converterNamespace, diff, version)).toFile();
        if(sliceFile.exists() && !reuseSliceFiles) sliceFile.delete();
        if(!sliceFile.exists()) {
            HollowConsumer inputConsumer = VMSInputDataConsumer.getNewProxyConsumer(cinderConsumerBuilder,
                    converterNamespace, localBlobStore(isProd).toString(), isProd, CONVERTER.getAPI());
            inputConsumer.triggerRefreshTo(version);

            HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(inputConsumer.getStateEngine());
            writeStateEngineSlice(slicedStateEngine, sliceFile);
        }
        return readStateEngineSlice(sliceFile);
    }

    private HollowReadStateEngine readStateEngineSlice(File sliceFile) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try(LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(sliceFile))) {
            reader.readSnapshot(is);
        }
        return stateEngine;
    }

    private void writeStateEngineSlice(HollowWriteStateEngine slicedStateEngine, File sliceFile) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(slicedStateEngine);
        try(LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(sliceFile))) {
            writer.writeSnapshot(os);
        }
    }

    private HollowDiff createDiff(HollowReadStateEngine from, HollowReadStateEngine to) {
        HollowDiff diff = new HollowDiff(from, to);

        diff.addTypeDiff("AltGenres", "altGenreId");
        diff.addTypeDiff("ArtWorkImageType", "imageType.value");
        diff.addTypeDiff("ArtworkRecipe", "recipeName.value");
        diff.addTypeDiff("AssetMetaDatas", "assetId.value");
        diff.addTypeDiff("Asset", "assetId.value");
        diff.addTypeDiff("Awards", "awardId");
        diff.addTypeDiff("CacheDeploymentIntent", "streamProfileId", "isoCountryCode.value", "bitrateKBPS");
        diff.addTypeDiff("Categories", "categoryId");
        diff.addTypeDiff("CategoryGroups", "categoryGroupId");
        diff.addTypeDiff("Certifications", "certificationTypeId");
        diff.addTypeDiff("CertificationSystem", "certificationSystemId");
        diff.addTypeDiff("Character", "characterId");
        diff.addTypeDiff("CharacterArtwork", "characterId", "sourceFileId.value");
        diff.addTypeDiff("Characters", "id");
        diff.addTypeDiff("ConsolidatedCertificationSystems", "certificationSystemId");
        diff.addTypeDiff("ConsolidatedVideoRatings", "videoId");
        diff.addTypeDiff("Contracts", "movieId", "countryCode.value");
        diff.addTypeDiff("DrmSystemIdentifiers", "id");
        diff.addTypeDiff("Episodes", "movieId", "episodeId");
        diff.addTypeDiff("Festivals", "festivalId");
        diff.addTypeDiff("Languages", "languageId");
        diff.addTypeDiff("LocalizedCharacter", "characterId");
        diff.addTypeDiff("LocalizedMetadata", "movieId", "attributeName.value", "label.value");
        diff.addTypeDiff("MovieRatings", "movieId", "certificationTypeId", "media.value");
        diff.addTypeDiff("Movies", "movieId");
        diff.addTypeDiff("Package", "packageId");
        diff.addTypeDiff("PersonArtwork", "personId", "sourceFileId.value");
        diff.addTypeDiff("PersonAliases", "aliasId");
        diff.addTypeDiff("Persons", "personId");
        diff.addTypeDiff("ProtectionTypes", "id");
        diff.addTypeDiff("Rollout", "movieId", "rolloutId");
        diff.addTypeDiff("ShowMemberTypes", "showMemberTypeId");
        diff.addTypeDiff("StorageGroups", "id.value"); // TODO: timt: was "id"
        diff.addTypeDiff("StreamProfileGroups", "groupName.value");
        diff.addTypeDiff("StreamProfiles", "id");
        diff.addTypeDiff("TerritoryCountries", "territoryCode.value");
        diff.addTypeDiff("VideoArtwork", "movieId", "sourceFileId.value");
        diff.addTypeDiff("VideoAward", "videoId");
        diff.addTypeDiff("VideoDate", "videoId");
        diff.addTypeDiff("VideoGeneral", "videoId");
        diff.addTypeDiff("VideoRating", "videoId");
        diff.addTypeDiff("VideoType", "videoId");
        diff.addTypeDiff("VMSAward", "awardId");

        /*
         *  AltGenres
         *  AltGenresAlternateNames
         *  AltGenresAlternateNamesList
         *  ArtWorkImageType
         *  ArtworkAttributes
         *  ArtworkDerivative
         *  ArtworkDerivativeSet
         *  ArtworkLocale
         *  ArtworkLocaleList
         *  ArtworkRecipe
         *  AssetMetaDatas
         *  AudioStreamInfo
         *  Awards
         *  CacheDeploymentIntent
         *  Categories
         *  CategoryGroups
         *  Cdn
         *  CdnDeployment
         *  CdnDeploymentSet
         *  CertificationSystem
         *  CertificationSystemRating
         *  CertificationSystemRatingList
         *  Certifications
         *  Character
         *  CharacterArtwork
         *  CharacterElements
         *  CharacterList
         *  CharacterQuote
         *  CharacterQuoteList
         *  Characters
         *  ChunkDurationsString
         *  CodecPrivateDataString
         *  ConsolidatedCertSystemRating
         *  ConsolidatedCertSystemRatingList
         *  ConsolidatedCertificationSystems
         *  ConsolidatedVideoCountryRating
         *  ConsolidatedVideoCountryRatingList
         *  ConsolidatedVideoRating
         *  ConsolidatedVideoRatingList
         *  ConsolidatedVideoRatings
         *  Contract
         *  Contracts
         *  DamMerchStills
         *  DamMerchStillsMoment
         *  Date
         *  DisallowedAssetBundle
         *  DisallowedAssetBundlesList
         *  DisallowedSubtitleLangCode
         *  DisallowedSubtitleLangCodesList
         *  DownloadableId
         *  DownloadableIdList
         *  DrmHeaderInfo
         *  DrmHeaderInfoList
         *  DrmInfoString
         *  DrmSystemIdentifiers
         *  Episode
         *  EpisodeList
         *  Episodes
         *  ExplicitDate
         *  Festivals
         *  Flags
         *  ISOCountry
         *  ISOCountryList
         *  ISOCountrySet
         *  ImageStreamInfo
         *  IndividualSupplemental
         *  Languages
         *  ListOfContract
         *  ListOfReleaseDates
         *  ListOfRightsContract
         *  ListOfRightsContractAsset
         *  ListOfRightsContractPackage
         *  ListOfRightsWindow
         *  ListOfRightsWindowContract
         *  ListOfString
         *  ListOfVideoIds
         *  LocaleTerritoryCode
         *  LocaleTerritoryCodeList
         *  LocalizedCharacter
         *  LocalizedMetadata
         *  MapKey
         *  MapOfFlagsFirstDisplayDates
         *  MapOfTranslatedText
         *  MovieCharacterPerson
         *  MovieRatings
         *  Movies
         *  MultiValuePassthroughMap
         *  OriginServer
         *  Package
         *  PackageDrmInfo
         *  PackageDrmInfoList
         *  PackageMoment
         *  PackageMomentList
         *  PackageStream
         *  PackageStreamSet
         *  PassthroughData
         *  PersonAliases
         *  PersonArtwork
         *  PersonBio
         *  PersonCharacter
         *  PersonCharacterResource
         *  PersonVideo
         *  PersonVideoAliasId
         *  PersonVideoAliasIdsList
         *  PersonVideoRole
         *  PersonVideoRolesList
         *  Persons
         *  ProtectionTypes
         *  Ratings
         *  ReleaseDate
         *  Rights
         *  RightsContract
         *  RightsContractAsset
         *  RightsContractPackage
         *  RightsWindow
         *  RightsWindowContract
         *  Rollout
         *  RolloutPhase
         *  RolloutPhaseArtwork
         *  RolloutPhaseArtworkSourceFileId
         *  RolloutPhaseArtworkSourceFileIdList
         *  RolloutPhaseElements
         *  RolloutPhaseList
         *  RolloutPhaseLocalizedMetadata
         *  RolloutPhaseWindow
         *  RolloutPhaseWindowMap
         *  Season
         *  SeasonList
         *  SetOfString
         *  ShowCountryLabel
         *  ShowMemberType
         *  ShowMemberTypeList
         *  ShowMemberTypes
         *  ShowSeasonEpisode
         *  SingleValuePassthroughMap
         *  StorageGroups
         *  StoriesSynopses
         *  StoriesSynopsesHook
         *  StoriesSynopsesHookList
         *  StreamAssetMetadata
         *  StreamAssetType
         *  StreamDeployment
         *  StreamDeploymentInfo
         *  StreamDeploymentLabel
         *  StreamDeploymentLabelSet
         *  StreamDimensions
         *  StreamDrmInfo
         *  StreamFileIdentification
         *  StreamNonImageInfo
         *  StreamProfileGroups
         *  StreamProfileId
         *  StreamProfileIdList
         *  StreamProfiles
         *  String
         *  Supplementals
         *  SupplementalsList
         *  TerritoryCountries
         *  TextStreamInfo
         *  TranslatedText
         *  TranslatedTextValue
         *  TurboCollections
         *  VMSAward
         *  VideoArtwork
         *  VideoAward
         *  VideoAwardList
         *  VideoAwardMapping
         *  VideoDate
         *  VideoDateWindow
         *  VideoDateWindowList
         *  VideoGeneral
         *  VideoGeneralAlias
         *  VideoGeneralAliasList
         *  VideoGeneralEpisodeType
         *  VideoGeneralEpisodeTypeList
         *  VideoGeneralTitleType
         *  VideoGeneralTitleTypeList
         *  VideoId
         *  VideoRating
         *  VideoRatingAdvisories
         *  VideoRatingAdvisoryId
         *  VideoRatingAdvisoryIdList
         *  VideoRatingArrayOfRating
         *  VideoRatingRating
         *  VideoRatingRatingReason
         *  VideoRatingRatingReasonArrayOfIds
         *  VideoRatingRatingReasonIds
         *  VideoStreamInfo
         *  VideoType
         *  VideoTypeDescriptor
         *  VideoTypeDescriptorSet
         *  VideoTypeMedia
         *  VideoTypeMediaList
         */



        diff.calculateDiffs();

        return diff;
    }

    private Path localBlobStore(boolean isProd) {
        try {
            Path path = Paths.get(LOCAL_BLOB_STORE, (isProd ? "PROD" : "TEST").toLowerCase());
            Files.createDirectories(path);
            return path;
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
