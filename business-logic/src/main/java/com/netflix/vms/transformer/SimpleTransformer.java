package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.FailedProcessingIndividualHierarchies;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CycleInterrupted;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.IndividualTransformFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.MultiLocaleCountries;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.NonVideoSpecificTransformDuration;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformInfo;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformProgress;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.VideoHierarchyGrouper.VideoHierarchyGroup;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.data.CupTokenFetcher;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.data.VideoDataCollection;
import com.netflix.vms.transformer.gatekeeper2migration.GatekeeperStatusRetriever;
import com.netflix.vms.transformer.hollowinput.CharacterListHollow;
import com.netflix.vms.transformer.hollowinput.MovieCharacterPersonHollow;
import com.netflix.vms.transformer.hollowinput.PersonCharacterHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoData;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoFacetData;
import com.netflix.vms.transformer.hollowoutput.FallbackUSArtwork;
import com.netflix.vms.transformer.hollowoutput.GlobalVideo;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.MoviePersonCharacter;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoCollectionsData;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoMiscData;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.logmessage.ProgressMessage;
import com.netflix.vms.transformer.misc.TopNVideoDataModule;
import com.netflix.vms.transformer.modules.TransformModule;
import com.netflix.vms.transformer.modules.artwork.CharacterImagesModule;
import com.netflix.vms.transformer.modules.artwork.PersonImagesModule;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsDataHierarchy;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsModule;
import com.netflix.vms.transformer.modules.countryspecific.CountrySpecificDataModule;
import com.netflix.vms.transformer.modules.deploymentintent.CacheDeploymentIntentModule;
import com.netflix.vms.transformer.modules.l10n.L10NMiscResourcesModule;
import com.netflix.vms.transformer.modules.l10n.L10NVideoResourcesModule;
import com.netflix.vms.transformer.modules.media.VideoMediaDataModule;
import com.netflix.vms.transformer.modules.meta.VideoImagesDataModule;
import com.netflix.vms.transformer.modules.meta.VideoMetaDataModule;
import com.netflix.vms.transformer.modules.meta.VideoMiscDataModule;
import com.netflix.vms.transformer.modules.mpl.DrmSystemModule;
import com.netflix.vms.transformer.modules.mpl.EncodingProfileModule;
import com.netflix.vms.transformer.modules.mpl.OriginServerModule;
import com.netflix.vms.transformer.modules.packages.PackageDataModule;
import com.netflix.vms.transformer.modules.packages.StreamDataModule;
import com.netflix.vms.transformer.modules.packages.contracts.LanguageRightsModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkImageRecipeModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkTypeModule;
import com.netflix.vms.transformer.modules.passthrough.mpl.EncodingProfileGroupModule;
import com.netflix.vms.transformer.modules.person.GlobalPersonModule;
import com.netflix.vms.transformer.modules.rollout.RolloutVideoModule;
import com.netflix.vms.transformer.namedlist.NamedListCompletionModule;
import com.netflix.vms.transformer.namedlist.VideoNamedListModule;
import com.netflix.vms.transformer.namedlist.VideoNamedListModule.VideoNamedListPopulator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleTransformer {

    private VideoNamedListModule videoNamedListModule;

    private final VMSHollowInputAPI api;
    private final HollowWriteStateEngine writeStateEngine;
    private final TransformerContext ctx;
    private final CycleConstants cycleConstants;
    private final VMSTransformerIndexer indexer;
    
    private final GatekeeperStatusRetriever statusRetriever;

    public SimpleTransformer(VMSHollowInputAPI converterInputAPI, 
                             HollowConsumer gk2StatusConsumer, 
                             HollowWriteStateEngine outputStateEngine, 
                             TransformerContext ctx) {
        this.api = converterInputAPI;
        this.writeStateEngine = outputStateEngine;
        this.ctx = ctx;
        HollowReadStateEngine inputStateEngine = (HollowReadStateEngine) converterInputAPI.getDataAccess();
        this.cycleConstants = new CycleConstants(inputStateEngine, gk2StatusConsumer == null ? inputStateEngine : gk2StatusConsumer.getStateEngine());
        long startTime = System.currentTimeMillis();
        this.indexer = new VMSTransformerIndexer(inputStateEngine, ctx);
        long endTime = System.currentTimeMillis();
        
        System.out.println("INDEXED IN " + (endTime - startTime) + "ms");
        
        this.statusRetriever = createGatekeeperStatusRetriever(converterInputAPI, gk2StatusConsumer, outputStateEngine, ctx);
    }

    /// TODO: Remove after full GK2 migration.
    private GatekeeperStatusRetriever createGatekeeperStatusRetriever(VMSHollowInputAPI converterInputAPI, 
                                                                      HollowConsumer gk2StatusConsumer, 
                                                                      HollowWriteStateEngine outputStateEngine, 
                                                                      TransformerContext ctx) {
        GatekeeperStatusRetriever statusRetriever;
        if(gk2StatusConsumer == null) {
            statusRetriever = new GatekeeperStatusRetriever(converterInputAPI, indexer);
        } else {
            String gk2CountriesStr = ctx.getConfig().getGatekeeper2Countries();
            Set<String> gk2Countries = Collections.emptySet();
            if(gk2CountriesStr != null && !"".equals(gk2CountriesStr)) {
                gk2Countries = new HashSet<>(Arrays.asList(gk2CountriesStr.split(",")));
            }
            
            outputStateEngine.addHeaderTag("gatekeeper2_countries", gk2CountriesStr == null ? "" : gk2CountriesStr);
            outputStateEngine.addHeaderTag("gatekeeper2_status_namespace", ctx.getConfig().getGatekeeper2Namespace());
            outputStateEngine.addHeaderTag("gatekeeper2_status_version", String.valueOf(gk2StatusConsumer.getCurrentVersionId()));
            
            statusRetriever = new GatekeeperStatusRetriever(gk2StatusConsumer, gk2Countries, converterInputAPI, indexer);
        }
        
        return statusRetriever;
    }

    public void setPublishCycleDataTS(long time) {
        ctx.setNowMillis(time);
    }

    public HollowWriteStateEngine transform() throws Throwable {
        StreamDataModule.clearVideoFormatDiffs();

        // hierarchy initializer, HollowObjectMapper and namedListModule
        final VideoHierarchyInitializer hierarchyInitializer = new VideoHierarchyInitializer(api, indexer, statusRetriever, ctx);
        final HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.doNotUseDefaultHashKeys();
        this.videoNamedListModule = new VideoNamedListModule(ctx, cycleConstants, objectMapper);

        // print multiLanguageCatalog countries
        OctoberSkyData octoberSkyData = ctx.getOctoberSkyData();
        Set<String> multiLanguageCatalogCountries = octoberSkyData.getMultiLanguageCatalogCountries();
        if (!ctx.getConfig().getOctoberSkyNamespace().isEmpty()) {
            ctx.getLogger().info(MultiLocaleCountries, "Using October sky namespace {}", ctx.getConfig().getOctoberSkyNamespace());
        }
        ctx.getLogger().info(MultiLocaleCountries, "Countries that will support multi-language catalogs are {} out of all supported countries {}", multiLanguageCatalogCountries, octoberSkyData.getSupportedCountries());
        for (String country : multiLanguageCatalogCountries) {
            Set<String> supportedLanguageCatalogs = octoberSkyData.getCatalogLanguages(country);
            ctx.getLogger().info(MultiLocaleCountries, "Country : {} supported language catalogs : {}", country, supportedLanguageCatalogs);
            for (String language : supportedLanguageCatalogs) {
                ctx.getLogger().info(MultiLocaleCountries, "Catalog {}:{} languageVariants:{}", country, language, octoberSkyData.getLanguageVariants(country, language));
            }
        }

        long startTime = System.currentTimeMillis();

        // this module can be removed in the future when we have fully migrated to cup tokens from cinder
        CupTokenFetcher cupTokenFetcher = new CupTokenFetcher(indexer, api, ctx.getConfig());

        // Grouper to group by hierarchy.
        VideoHierarchyGrouper showGrouper = new VideoHierarchyGrouper(api, ctx);
        final List<Set<VideoHierarchyGroup>> processGroups = showGrouper.getProcessGroups();
        ctx.getLogger().info(TransformInfo, "topNodes={}", processGroups.size());

        // counters
        AtomicInteger processedCount = new AtomicInteger();
        AtomicInteger failedIndividualTransforms = new AtomicInteger(0);
        int progressDivisor = getProgressDivisor(processGroups.size());

        // cycle data aggregator
        CycleDataAggregator cycleDataAggregator = new CycleDataAggregator(ctx);
        CycleDataAggregatorHelper.configureLogsTagsForVMSWindowModule(cycleDataAggregator);


        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "transform");
        for (int i = 0; i < executor.getCorePoolSize(); i++) {
            executor.execute(() -> {

                // create new modules for each executor thread
                PackageDataModule packageDataModule = new PackageDataModule(api, ctx, objectMapper, cycleConstants,
                        indexer, statusRetriever, cupTokenFetcher);
                VideoCollectionsModule collectionsModule = new VideoCollectionsModule(api, ctx, cycleConstants, indexer);
                VideoMetaDataModule metadataModule = new VideoMetaDataModule(api, ctx, cycleConstants, indexer, statusRetriever);
                VideoMediaDataModule mediaDataModule = new VideoMediaDataModule(api, indexer, statusRetriever);
                VideoMiscDataModule miscDataModule = new VideoMiscDataModule(api, indexer);
                VideoImagesDataModule imagesDataModule = new VideoImagesDataModule(api, ctx, objectMapper, cycleConstants, indexer, statusRetriever);
                CountrySpecificDataModule countrySpecificModule = new CountrySpecificDataModule(api, ctx,
                        objectMapper, cycleConstants, indexer, statusRetriever, cycleDataAggregator, cupTokenFetcher);
                L10NVideoResourcesModule l10nVideoResourcesModule = new L10NVideoResourcesModule(api, ctx, cycleConstants, objectMapper, indexer);

                int idx = processedCount.getAndIncrement();
                while (idx < processGroups.size()) {
                    Set<VideoHierarchyGroup> processGroup = processGroups.get(idx);
                    try {
                        TransformedVideoData transformedVideoData = new TransformedVideoData();

                        // NOTE: Legacy pipeline seems to propagate data for video that is not considered valid or not valid yet
                        // so need to keep track of them and allow those use cases to be in parity
                        Set<Integer> droppedVideoIds = new HashSet<>();
                        Map<String, Set<VideoHierarchy>> showHierarchiesByCountry = hierarchyInitializer.getShowHierarchiesByCountry(processGroup, droppedVideoIds);
                        packageDataModule.transform(showHierarchiesByCountry, droppedVideoIds, transformedVideoData);
                        l10nVideoResourcesModule.transform(showHierarchiesByCountry, droppedVideoIds);

                        // Spot to trigger Cycle Monkey if enabled
                        ctx.getCycleMonkey().doMonkeyBusiness("transform");

                        if (showHierarchiesByCountry != null) {
                            // get all transformed data for show hierarchies using all the modules.
                            collectionsModule.buildVideoCollectionsDataByCountry(showHierarchiesByCountry, transformedVideoData);
                            metadataModule.buildVideoMetaDataByCountry(showHierarchiesByCountry, transformedVideoData);
                            mediaDataModule.buildVideoMediaDataByCountry(showHierarchiesByCountry, transformedVideoData);
                            imagesDataModule.buildVideoImagesByCountry(showHierarchiesByCountry, transformedVideoData);
                            miscDataModule.buildVideoMiscDataByCountry(showHierarchiesByCountry, transformedVideoData);
                            countrySpecificModule.buildCountrySpecificDataByCountry(showHierarchiesByCountry, transformedVideoData);

                            // process the transformed video data
                            if (transformedVideoData.getVideoDataCollectionMap() != null && !transformedVideoData.getVideoDataCollectionMap().isEmpty())
                                processCurrentData(transformedVideoData, objectMapper);
                        }
                    } catch (Throwable th) {
                        ctx.getLogger().error(IndividualTransformFailed, "Transformation failed for hierarchy with top node(s) " + getTopNodeIdentifierString(processGroup), th);
                        failedIndividualTransforms.incrementAndGet();
                    }

                    if (idx % progressDivisor == 0) {
                        ctx.getLogger().info(TransformProgress, new ProgressMessage(idx, progressDivisor));
                    }

                    idx = processedCount.getAndIncrement();

                    // Interrupt if needed
                    if (ctx.getCycleInterrupter().isCycleInterrupted()) {
                        ctx.getLogger().error(CycleInterrupted, "Interrupted while processing video : {} ", new ProgressMessage(idx, progressDivisor));
                        break;
                    }
                }
            });
        }

        // @formatter:off
        // Register Transform Modules
        List<TransformModule> moduleList = Arrays.asList(
                new DrmSystemModule(api, ctx, cycleConstants, objectMapper),
                new OriginServerModule(api, ctx, cycleConstants, objectMapper, indexer),
                new EncodingProfileModule(api, ctx, cycleConstants, objectMapper, indexer),
                new CacheDeploymentIntentModule(api, ctx, cycleConstants, objectMapper),
                new ArtworkTypeModule(api, ctx, cycleConstants, objectMapper),
                new ArtworkImageRecipeModule(api, ctx, cycleConstants, objectMapper),
                new EncodingProfileGroupModule(api, ctx, cycleConstants, objectMapper),

                new L10NMiscResourcesModule(api, ctx, cycleConstants, objectMapper, indexer),
                new LanguageRightsModule(api, ctx, cycleConstants, objectMapper, indexer),
                new TopNVideoDataModule(api, ctx, cycleConstants, objectMapper),
                new RolloutVideoModule(api, ctx, cycleConstants, objectMapper, indexer),
                new PersonImagesModule(api, ctx, cycleConstants, objectMapper, indexer),
                new CharacterImagesModule(api, ctx, cycleConstants, objectMapper, indexer),
                new GlobalPersonModule(api, ctx, cycleConstants, objectMapper, indexer)
        );

        // @formatter:on
        // Execute Transform Modules
        for (TransformModule m : moduleList) {
            // Interrupt if needed
            if (ctx.getCycleInterrupter().isCycleInterrupted()) {
                ctx.getLogger().error(CycleInterrupted, "Interrupted while processing module={} ", m.getName());
                break;
            }

            long tStart = System.currentTimeMillis();
            m.transform();
            long tDuration = System.currentTimeMillis() - tStart;
            ctx.getLogger().info(NonVideoSpecificTransformDuration, "Finished Transform for module={}, duration={}", m.getName(), tDuration);
        }

        executor.awaitSuccessfulCompletion();
        if (!ctx.getCycleInterrupter().isCycleInterrupted()) {
            //// NamedListCompletionModule happens after all hierarchies are already processed -- now we have built the ThreadSafeBitSets corresponding
            //// to the NamedLists, and we can build the POJOs using those.
            long tStart = System.currentTimeMillis();
            NamedListCompletionModule namedListCompleter = new NamedListCompletionModule(videoNamedListModule, cycleConstants, objectMapper);
            namedListCompleter.transform();
            long tDuration = System.currentTimeMillis() - tStart;
            ctx.getLogger().info(NonVideoSpecificTransformDuration, "Finished Transform for module={}, duration={}", namedListCompleter.getName(), tDuration);

            StreamDataModule.logVideoFormatDiffs(ctx);
            ctx.getLogger().info(TransformProgress, new ProgressMessage(processedCount.get()));
        }

        ctx.getMetricRecorder().recordMetric(FailedProcessingIndividualHierarchies, failedIndividualTransforms.get());
        if (failedIndividualTransforms.get() > ctx.getConfig().getMaxTolerableFailedTransformerHierarchies())
            throw new RuntimeException("More than " + ctx.getConfig().getMaxTolerableFailedTransformerHierarchies() + " individual hierarchies failed transformation -- not publishing data");

        // log all aggregated data
        cycleDataAggregator.logAllAggregatedData();

        long endTime = System.currentTimeMillis();
        System.out.println("Processed all videos in " + (endTime - startTime) + "ms");
        

        // Check to determine whether to abort cycle due to interrupt
        ctx.getCycleInterrupter().triggerInterruptIfNeeded(ctx.getCurrentCycleId(), ctx.getLogger(), "Stopped at transform");
        return writeStateEngine;
    }

    private int getProgressDivisor(int numProcessGroups) {
        int totalCount = numProcessGroups;
        totalCount = (totalCount / 100) * 100 + 100;
        return totalCount / 100;
    }

    private void processCurrentData(TransformedVideoData transformedVideoData, HollowObjectMapper objectMapper) {
        Map<String, VideoDataCollection> videoCountryDataMap = transformedVideoData.getVideoDataCollectionMap();
        VideoNamedListPopulator namedListPopulator = videoNamedListModule.getPopulator();
        Map<Video, Map<ISOCountry, CompleteVideo>> globalVideoMap = new HashMap<>();

        // Process Complete Video
        for (Map.Entry<String, VideoDataCollection> countryHierarchyEntry : videoCountryDataMap.entrySet()) {
            String countryId = countryHierarchyEntry.getKey();
            ISOCountry country = cycleConstants.getISOCountry(countryId);
            VideoDataCollection videoDataCollection = countryHierarchyEntry.getValue();

            // if no videos in video collection hierarchies then continue to next.
            if (videoDataCollection.getVideoCollectionsDataHierarchies() == null) continue;

            for (VideoCollectionsDataHierarchy hierarchy : videoDataCollection.getVideoCollectionsDataHierarchies()) {

                int topNodeVideoId = hierarchy.getTopNodeId().value;
                VideoCollectionsData videoCollectionsData = hierarchy.getTopNode();
                namedListPopulator.setCountry(countryId);

                // Process TopNode
                VideoMiscData miscData = transformedVideoData.getVideoData(topNodeVideoId).getVideoMiscData();
                CompleteVideo topNodeCompleteVideo = getCompleteVideo(topNodeVideoId, country, videoDataCollection, videoCollectionsData, miscData);
                processCompleteVideo(objectMapper, namedListPopulator, topNodeCompleteVideo, true, country, globalVideoMap);

                // Process Show children
                if (topNodeCompleteVideo.data.facetData.videoCollectionsData.nodeType == cycleConstants.SHOW) {
                    int sequenceNumber = 0;
                    // Process Seasons
                    for (Map.Entry<Integer, VideoCollectionsData> seasonEntry : hierarchy.getOrderedSeasons().entrySet()) {

                        int seasonId = seasonEntry.getKey().intValue();
                        miscData = transformedVideoData.getVideoData(seasonId).getVideoMiscData();
                        CompleteVideo season = getCompleteVideo(seasonId, country, videoDataCollection, seasonEntry.getValue(), miscData);
                        processCompleteVideo(objectMapper, namedListPopulator, season, false, country, globalVideoMap);

                        // Process Episodes
                        for (Map.Entry<Integer, VideoCollectionsData> episodeEntry : hierarchy.getOrderedSeasonEpisodes(++sequenceNumber).entrySet()) {
                            int episodeId = episodeEntry.getKey().intValue();
                            miscData = transformedVideoData.getVideoData(episodeId).getVideoMiscData();
                            CompleteVideo episode = getCompleteVideo(episodeId, country, videoDataCollection, episodeEntry.getValue(), miscData);
                            processCompleteVideo(objectMapper, namedListPopulator, episode, false, country, globalVideoMap);
                        }
                    }
                }

                // Process Supplemental
                for (Map.Entry<Integer, VideoCollectionsData> supplementalEntry : hierarchy.getSupplementalVideosCollectionsData().entrySet()) {
                    int supplementalId = supplementalEntry.getKey().intValue();
                    miscData = transformedVideoData.getVideoData(supplementalId).getVideoMiscData();
                    CompleteVideo supplemental = getCompleteVideo(supplementalId, country, videoDataCollection, supplementalEntry.getValue(), miscData);
                    processCompleteVideo(objectMapper, namedListPopulator, supplemental, false, country, globalVideoMap);
                }
            }
        }

        // Process Global Video
        processGlobalVideo(globalVideoMap, objectMapper);
        // Process FallbackUSArtwork
        VideoDataCollection usData = videoCountryDataMap.get("US");
        if (usData != null)
            processUSFallbackArtworks(usData, objectMapper);
    }

    private void processCompleteVideo(HollowObjectMapper mapper, VideoNamedListPopulator namedListPopulator, CompleteVideo completeVideo, boolean isTopNode, ISOCountry country, Map<Video, Map<ISOCountry, CompleteVideo>> globalVideoMap) {
        mapper.addObject(completeVideo);
        namedListPopulator.addCompleteVideo(completeVideo, isTopNode);
        Video video = completeVideo.id;
        globalVideoMap.putIfAbsent(video, new TreeMap<>(countryComparator));
        globalVideoMap.get(video).put(country, completeVideo);
    }

    private void processGlobalVideo(Map<Video, Map<ISOCountry, CompleteVideo>> globalVideoMap, HollowObjectMapper mapper) {

        HollowPrimaryKeyIndex primaryKeyIndex = indexer.getPrimaryKeyIndex(IndexSpec.MOVIE_CHARACTER_PERSON);
        for (Map.Entry<Video, Map<ISOCountry, CompleteVideo>> globalEntry : globalVideoMap.entrySet()) {

            Set<ISOCountry> availableCountries = new HashSet<>();
            Set<Strings> aliases = new HashSet<>();
            CompleteVideo representativeVideo = null;

            for (Map.Entry<ISOCountry, CompleteVideo> countryEntry : globalEntry.getValue().entrySet()) {

                ISOCountry country = countryEntry.getKey();
                CompleteVideo completeVideo = countryEntry.getValue();

                if (completeVideo != null) {
                    representativeVideo = preferredCompleteVideo(representativeVideo, completeVideo);
                    availableCountries.add(country);
                    if (completeVideo.data.facetData.videoMetaData.aliases != null)
                        aliases.addAll(completeVideo.data.facetData.videoMetaData.aliases);
                }
            }

            if (representativeVideo == null) return;
            // create GlobalVideo
            GlobalVideo gVideo = new GlobalVideo();
            gVideo.completeVideo = representativeVideo;
            gVideo.aliases = aliases;
            gVideo.availableCountries = availableCountries;
            gVideo.isSupplementalVideo = (representativeVideo.data.facetData.videoCollectionsData.nodeType == cycleConstants.SUPPLEMENTAL);
            gVideo.personCharacters = getPersonCharacters(primaryKeyIndex, representativeVideo);

            mapper.addObject(gVideo);
        }
    }

    private void processUSFallbackArtworks(VideoDataCollection videoDataCollection, HollowObjectMapper objectMapper) {
        Set<Integer> videoIds = videoDataCollection.getVideoIdSetForVideoData();
        for (int videoId : videoIds) {
            VideoImages images = videoDataCollection.getVideoImages(videoId);
            if (images != null) {
                FallbackUSArtwork artwork = new FallbackUSArtwork();
                artwork.id = new Video(videoId);
                artwork.artworksByType = images.artworks;
                artwork.typeFormatIdx = images.artworkFormatsByType;
                objectMapper.addObject(artwork);
            }
        }
    }

    private List<MoviePersonCharacter> getPersonCharacters(HollowPrimaryKeyIndex primaryKeyIndex, CompleteVideo completeVideo) {
        List<MoviePersonCharacter> personCharacters = new ArrayList<>();
        long movieId = completeVideo.id.value;
        int matchingOrdinal = primaryKeyIndex.getMatchingOrdinal(movieId);
        if (matchingOrdinal != -1) {
            MovieCharacterPersonHollow movieCharacterPersonHollow = api.getMovieCharacterPersonHollow(matchingOrdinal);
            CharacterListHollow characterList = movieCharacterPersonHollow._getCharacters();
            Iterator<PersonCharacterHollow> iterator = characterList.iterator();
            while (iterator.hasNext()) {
                PersonCharacterHollow personCharacterHollow = iterator.next();
                MoviePersonCharacter moviePersonCharacter = new MoviePersonCharacter();
                moviePersonCharacter.movieId = movieId;
                moviePersonCharacter.personId = personCharacterHollow._getPersonId();
                moviePersonCharacter.characterId = personCharacterHollow._getCharacterId();
                personCharacters.add(moviePersonCharacter);
            }
        }
        Collections.sort(personCharacters);
        return personCharacters;
    }

    private CompleteVideo preferredCompleteVideo(CompleteVideo current, CompleteVideo candidate) {
        if (current == null
                || isGoLive(candidate)
                || current.data.facetData.videoMetaData.videoSetTypes.contains(VIDEO_SET_TYPE_EXTENDED)) {
            return candidate;
        }

        return current;
    }

    private CompleteVideo getCompleteVideo(int videoId, ISOCountry country, VideoDataCollection videoDataCollection, VideoCollectionsData collectionsData, VideoMiscData miscData) {
        // create complete video
        CompleteVideo completeVideo = new CompleteVideo();
        completeVideo.id = new Video(videoId);
        completeVideo.country = country;

        // create CompleteVideoData
        completeVideo.data = new CompleteVideoData();
        // Facet data
        completeVideo.data.facetData = new CompleteVideoFacetData();
        completeVideo.data.facetData.videoCollectionsData = collectionsData;
        completeVideo.data.facetData.videoMetaData = videoDataCollection.getVideoMetaData(videoId);
        completeVideo.data.facetData.videoMediaData = videoDataCollection.getVideoMediaData(videoId);
        completeVideo.data.facetData.videoImages = videoDataCollection.getVideoImages(videoId) == null ? cycleConstants.EMPTY_VIDEO_IMAGES : videoDataCollection.getVideoImages(videoId);
        // CountrySpecificData
        completeVideo.data.countrySpecificData = videoDataCollection.getCompleteVideoCountrySpecificData(videoId);

        // Misc data
        if (!isExtended(completeVideo))
            completeVideo.data.facetData.videoMiscData = miscData;

        return completeVideo;
    }

    private String getTopNodeIdentifierString(Set<VideoHierarchyGroup> processGroup) {
        StringBuilder builder = new StringBuilder("(");
        boolean first = true;
        for (VideoHierarchyGroup topNodeGroup : processGroup) {
            if (!first)
                builder.append(",");
            builder.append(topNodeGroup);
            first = false;
        }
        builder.append(")");
        return builder.toString();
    }

    private static final VideoSetType VIDEO_SET_TYPE_EXTENDED = new VideoSetType("Extended");

    private static boolean isExtended(CompleteVideo completeVideo) {
        return completeVideo.data.facetData.videoMetaData.videoSetTypes.contains(VIDEO_SET_TYPE_EXTENDED);
    }

    private static boolean isGoLive(CompleteVideo completeVideo) {
        return completeVideo.data.facetData != null && completeVideo.data.facetData.videoMediaData != null && completeVideo.data.facetData.videoMediaData.isGoLive;
    }

    private static Comparator<ISOCountry> countryComparator = new ISOCountryComparator();

    private static class ISOCountryComparator implements Comparator<ISOCountry> {

        @Override
        public int compare(ISOCountry o1, ISOCountry o2) {
            String s1 = new String(o1.id);
            String s2 = new String(o2.id);
            return s1.compareTo(s2);
        }

    }

}
