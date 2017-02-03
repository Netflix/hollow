package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.FailedProcessingIndividualHierarchies;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.IndividualTransformFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.NonVideoSpecificTransformDuration;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformInfo;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformProgress;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.VideoHierarchyGrouper.VideoHierarchyGroup;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CharacterListHollow;
import com.netflix.vms.transformer.hollowinput.MovieCharacterPersonHollow;
import com.netflix.vms.transformer.hollowinput.PersonCharacterHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoFacetData;
import com.netflix.vms.transformer.hollowoutput.FallbackUSArtwork;
import com.netflix.vms.transformer.hollowoutput.GlobalPerson;
import com.netflix.vms.transformer.hollowoutput.GlobalVideo;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.MoviePersonCharacter;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoCollectionsData;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.hollowoutput.VideoMiscData;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
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
import com.netflix.vms.transformer.modules.packages.contracts.LanguageRightsModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkImageRecipeModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkTypeModule;
import com.netflix.vms.transformer.modules.passthrough.beehive.RolloutCharacterModule;
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
    private final VMSTransformerWriteStateEngine writeStateEngine;
    private final TransformerContext ctx;
    private final CycleConstants cycleConstants;
    private final VMSTransformerIndexer indexer;

    SimpleTransformer(VMSHollowInputAPI inputAPI, VMSTransformerWriteStateEngine outputStateEngine) {
        this(inputAPI, outputStateEngine, new SimpleTransformerContext());
        //ctx.setNowMillis(1462034581112L);
    }

    public SimpleTransformer(VMSHollowInputAPI inputAPI, VMSTransformerWriteStateEngine outputStateEngine, TransformerContext ctx) {
        this.api = inputAPI;
        this.writeStateEngine = outputStateEngine;
        this.ctx = ctx;
        HollowReadStateEngine inputStateEngine = (HollowReadStateEngine)inputAPI.getDataAccess();
        this.cycleConstants = new CycleConstants(inputStateEngine);
        long startTime = System.currentTimeMillis();
        this.indexer = new VMSTransformerIndexer(inputStateEngine);
        long endTime = System.currentTimeMillis();
        System.out.println("INDEXED IN " + (endTime - startTime) + "ms");
    }

    public void setPublishCycleDataTS(long time) {
        ctx.setNowMillis(time);
    }

    public HollowWriteStateEngine transform() throws Throwable {
        AtomicInteger failedIndividualTransforms = new AtomicInteger(0);

        final VideoHierarchyInitializer hierarchyInitializer = new VideoHierarchyInitializer(api, indexer, ctx);

        final HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.doNotUseDefaultHashKeys();

        SimultaneousExecutor executor = new SimultaneousExecutor();

        this.videoNamedListModule = new VideoNamedListModule(ctx, cycleConstants, objectMapper);

        long startTime = System.currentTimeMillis();
        VideoHierarchyGrouper showGrouper = new VideoHierarchyGrouper(api, ctx);

        final List<Set<VideoHierarchyGroup>> processGroups = showGrouper.getProcessGroups();
        ctx.getLogger().info(TransformInfo, "topNodes={}", processGroups.size());

        AtomicInteger processedCount = new AtomicInteger();
        int progressDivisor = getProgressDivisor(processGroups.size());

        for(int i=0;i<executor.getCorePoolSize();i++) {
            executor.execute(() -> {
                PackageDataModule packageDataModule = new PackageDataModule(api, ctx, objectMapper, cycleConstants, indexer);
                VideoCollectionsModule collectionsModule = new VideoCollectionsModule(api, cycleConstants, indexer);
                VideoMetaDataModule metadataModule = new VideoMetaDataModule(api, ctx, cycleConstants, indexer);
                VideoMediaDataModule mediaDataModule = new VideoMediaDataModule(api, indexer);
                VideoMiscDataModule miscDataModule = new VideoMiscDataModule(api, indexer);
                VideoImagesDataModule imagesDataModule = new VideoImagesDataModule(api, ctx, objectMapper, cycleConstants, indexer);
                CountrySpecificDataModule countrySpecificModule = new CountrySpecificDataModule(api, ctx, objectMapper, cycleConstants, indexer);
                L10NVideoResourcesModule l10nVideoResourcesModule = new L10NVideoResourcesModule(api, ctx, cycleConstants, objectMapper, indexer);

                int idx = processedCount.getAndIncrement();
                while (idx < processGroups.size()) {
                    Set<VideoHierarchyGroup> processGroup = processGroups.get(idx);
                    try {
                        // NOTE: Legacy pipeline seems to propagate data for video that is not considered valid or not valid yet
                        // so need to keep track of them and allow those use cases to be in parity
                        Set<Integer> droppedVideoIds = new HashSet<>();
                        Map<String, Set<VideoHierarchy>> showHierarchiesByCountry = hierarchyInitializer.getShowHierarchiesByCountry(processGroup, droppedVideoIds);
                        Map<Integer, VideoPackageData> transformedPackageData = packageDataModule.transform(showHierarchiesByCountry, droppedVideoIds);
                        l10nVideoResourcesModule.transform(showHierarchiesByCountry, droppedVideoIds);

                        if (showHierarchiesByCountry != null) {
                            Map<String, Set<VideoCollectionsDataHierarchy>> vcdByCountry = collectionsModule.buildVideoCollectionsDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, VideoMetaData>> vmdByCountry = metadataModule.buildVideoMetaDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, VideoMediaData>> mediaDataByCountry = mediaDataModule.buildVideoMediaDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, VideoImages>> imagesDataByCountry = imagesDataModule.buildVideoImagesByCountry(showHierarchiesByCountry);
                            Map<Integer, VideoMiscData> miscData = miscDataModule.buildVideoMiscDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, CompleteVideoCountrySpecificData>> countrySpecificByCountry = countrySpecificModule.buildCountrySpecificDataByCountry(showHierarchiesByCountry, transformedPackageData);

                            if (vcdByCountry != null)
                                writeJustTheCurrentData(vcdByCountry, vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry, objectMapper);
                        }
                    } catch (Throwable th) {
                        ctx.getLogger().error(IndividualTransformFailed, "Transformation failed for hierarchy with top node(s) " + getTopNodeIdentifierString(processGroup), th);
                        failedIndividualTransforms.incrementAndGet();
                    }

                    if (idx % progressDivisor == 0) {
                        ctx.getLogger().info(TransformProgress, new ProgressMessage(idx, progressDivisor));
                    }

                    idx = processedCount.getAndIncrement();
                }
            });

        }

        // @formatter:off
        // Register Transform Modules
        List<TransformModule> moduleList = Arrays.<TransformModule>asList(
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
                new RolloutCharacterModule(api, ctx, cycleConstants, objectMapper),
                new RolloutVideoModule(api, ctx, cycleConstants, objectMapper, indexer),
                new PersonImagesModule(api, ctx, cycleConstants, objectMapper, indexer),
                new CharacterImagesModule(api, ctx, cycleConstants, objectMapper, indexer)
                );

        // @formatter:on
        // Execute Transform Modules
        for(TransformModule m : moduleList) {
            long tStart = System.currentTimeMillis();
            m.transform();
            long tDuration = System.currentTimeMillis() - tStart;
            ctx.getLogger().info(NonVideoSpecificTransformDuration, "Finished Transform for module={}, duration={}", m.getName(), tDuration);
        }

        /// GlobalPersonModule is pulled out separately here because we will use the result in the NamedListCompletionModule
        GlobalPersonModule globalPersonModule = new GlobalPersonModule(api, ctx, cycleConstants, objectMapper, indexer);
        long tStart = System.currentTimeMillis();
        List<GlobalPerson> allGlobalPersonRecords = globalPersonModule.transformPersons();
        long tDuration = System.currentTimeMillis() - tStart;
        ctx.getLogger().info(NonVideoSpecificTransformDuration, "Finished Transform for module={}, duration={}", globalPersonModule.getName(), tDuration);

        executor.awaitSuccessfulCompletion();

        //// NamedListCompletionModule happens after all hierarchies are already processed -- now we have built the ThreadSafeBitSets corresponding
        //// to the NamedLists, and we can build the POJOs using those.
        tStart = System.currentTimeMillis();
        NamedListCompletionModule namedListCompleter = new NamedListCompletionModule(videoNamedListModule, allGlobalPersonRecords, cycleConstants, objectMapper);
        namedListCompleter.transform();
        tDuration = System.currentTimeMillis() - tStart;
        ctx.getLogger().info(NonVideoSpecificTransformDuration, "Finished Transform for module={}, duration={}", namedListCompleter.getName(), tDuration);

        ctx.getLogger().info(TransformProgress, new ProgressMessage(processedCount.get()));
        ctx.getMetricRecorder().recordMetric(FailedProcessingIndividualHierarchies, failedIndividualTransforms.get());
        
        if(failedIndividualTransforms.get() > ctx.getConfig().getMaxTolerableFailedTransformerHierarchies())
            throw new RuntimeException("More than " + ctx.getConfig().getMaxTolerableFailedTransformerHierarchies() + " individual hierarchies failed transformation -- not publishing data");
        
        long endTime = System.currentTimeMillis();
        System.out.println("Processed all videos in " + (endTime - startTime) + "ms");

        return writeStateEngine;
    }

    private int getProgressDivisor(int numProcessGroups) {
        int totalCount = numProcessGroups;
        totalCount = (totalCount / 100) * 100 + 100;
        return totalCount / 100;
    }

    private void writeJustTheCurrentData(Map<String, Set<VideoCollectionsDataHierarchy>> vcdByCountry,
            Map<String, Map<Integer, VideoMetaData>> vmdByCountry,
            Map<Integer, VideoMiscData> miscData,
            Map<String, Map<Integer, VideoMediaData>> mediaDataByCountry,
            Map<String, Map<Integer, VideoImages>> imagesDataByCountry,
            Map<String, Map<Integer, CompleteVideoCountrySpecificData>> countrySpecificByCountry,
            HollowObjectMapper objectMapper) {

        VideoNamedListPopulator namedListPopulator = videoNamedListModule.getPopulator();

        Map<Video, Map<ISOCountry, CompleteVideo>> globalVideoMap = new HashMap<>();

        // ----------------------
        // Process Complete Video
        for(Map.Entry<String, Set<VideoCollectionsDataHierarchy>> countryHierarchyEntry : vcdByCountry.entrySet()) {
            String countryId = countryHierarchyEntry.getKey();
            ISOCountry country = cycleConstants.getISOCountry(countryId);

            for(VideoCollectionsDataHierarchy hierarchy : countryHierarchyEntry.getValue()) {
                VideoCollectionsData videoCollectionsData = hierarchy.getTopNode();

                namedListPopulator.setCountry(countryId);

                // Process TopNode
                CompleteVideo topNode = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                        objectMapper, country, countryId, videoCollectionsData, hierarchy.getTopNode().topNode, globalVideoMap);

                namedListPopulator.addCompleteVideo(topNode, true); // TODO: timt: refactor away duplicate calls (see globalVideoMap)

                // Process Show children
                if(topNode.facetData.videoCollectionsData.nodeType == cycleConstants.SHOW) {
                    int sequenceNumber = 0;
                    // Process Seasons
                    for(Map.Entry<Integer, VideoCollectionsData> showEntry : hierarchy.getOrderedSeasons().entrySet()) {
                        CompleteVideo season = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                                objectMapper, country, countryId, showEntry.getValue(), new Video(showEntry.getKey().intValue()), globalVideoMap);

                        namedListPopulator.addCompleteVideo(season, false); // TODO: timt: refactor away duplicate calls (see globalVideoMap)

                        // Process Episodes
                        for(Map.Entry<Integer, VideoCollectionsData> episodeEntry : hierarchy.getOrderedSeasonEpisodes(++sequenceNumber).entrySet()) {
                            CompleteVideo episode = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                                    objectMapper, country, countryId, episodeEntry.getValue(), new Video(episodeEntry.getKey().intValue()), globalVideoMap);

                            namedListPopulator.addCompleteVideo(episode, false); // TODO: timt: refactor away duplicate calls (see globalVideoMap)
                        }
                    }
                }

                // Process Supplemental
                for(Map.Entry<Integer, VideoCollectionsData> supplementalEntry : hierarchy.getSupplementalVideosCollectionsData().entrySet()) {
                    CompleteVideo supplemental = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                            objectMapper, country, countryId, supplementalEntry.getValue(), new Video(supplementalEntry.getKey().intValue()), globalVideoMap);

                    namedListPopulator.addCompleteVideo(supplemental, false); // TODO: timt: refactor away duplicate calls (see globalVideoMap)
                }
            }
        }

        // ----------------------
        // Process GlobalVideo
        HollowPrimaryKeyIndex primaryKeyIndex = indexer.getPrimaryKeyIndex(IndexSpec.MOVIE_CHARACTER_PERSON);
        for (Map.Entry<Video, Map<ISOCountry, CompleteVideo>> globalEntry : globalVideoMap.entrySet()) {
            Set<ISOCountry> availableCountries = new HashSet<ISOCountry>();
            Set<Strings> aliases = new HashSet<>();


            CompleteVideo representativeVideo = null;
            for (Map.Entry<ISOCountry, CompleteVideo> countryEntry : globalEntry.getValue().entrySet()) {
                ISOCountry country = countryEntry.getKey();
                CompleteVideo completeVideo = countryEntry.getValue();
                if (completeVideo != null) {
                    representativeVideo = preferredCompleteVideo(representativeVideo, completeVideo);
                    availableCountries.add(country);

                    if (completeVideo.facetData.videoMetaData.aliases != null) {
                        aliases.addAll(completeVideo.facetData.videoMetaData.aliases);
                    }
                }
            }
            if (representativeVideo == null) return;
            
            // create GlobalVideo
            GlobalVideo gVideo = new GlobalVideo();
            gVideo.completeVideo = representativeVideo;
            gVideo.aliases = aliases;
            gVideo.availableCountries = availableCountries;
            gVideo.isSupplementalVideo = (representativeVideo.facetData.videoCollectionsData.nodeType == cycleConstants.SUPPLEMENTAL);
            gVideo.personCharacters = getPersonCharacters(primaryKeyIndex, representativeVideo);
            
            objectMapper.addObject(gVideo);
        }

        // ----------------------
        // Process FallbackUSArtwork
        Map<Integer, VideoImages> usArtworkMap = imagesDataByCountry.get("US");
        if (usArtworkMap != null) {
            for (Map.Entry<Integer, VideoImages> usArtwork : usArtworkMap.entrySet()) {
                int videoId = usArtwork.getKey();
                VideoImages images = usArtwork.getValue();

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
        if(matchingOrdinal != -1) {
            MovieCharacterPersonHollow movieCharacterPersonHollow = api.getMovieCharacterPersonHollow(matchingOrdinal);
            CharacterListHollow characterList = movieCharacterPersonHollow._getCharacters();
            Iterator<PersonCharacterHollow> iterator = characterList.iterator();
            while(iterator.hasNext()) {
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
                || current.facetData.videoMetaData.videoSetTypes.contains(VIDEO_SET_TYPE_EXTENDED)) {
            return candidate;
        }

        return current;
    }

    private CompleteVideo addCompleteVideo(
            Map<String, Map<Integer, VideoMetaData>> vmdByCountry,
            Map<Integer, VideoMiscData> miscData,
            Map<String, Map<Integer, VideoMediaData>> mediaDataByCountry,
            Map<String, Map<Integer, VideoImages>> imagesDataByCountry,
            Map<String, Map<Integer, CompleteVideoCountrySpecificData>> countrySpecificByCountry,
            HollowObjectMapper objectMapper, ISOCountry country, String countryId,
            VideoCollectionsData videoCollectionsData, Video video,
            Map<Video, Map<ISOCountry, CompleteVideo>> globalVideoMap) {
        CompleteVideo completeVideo = new CompleteVideo();
        completeVideo.id = video;
        completeVideo.country = country;
        completeVideo.facetData = new CompleteVideoFacetData();
        completeVideo.facetData.videoCollectionsData = videoCollectionsData;
        completeVideo.facetData.videoMetaData = vmdByCountry.get(countryId).get(completeVideo.id.value);
        completeVideo.facetData.videoMediaData = mediaDataByCountry.get(countryId).get(completeVideo.id.value);
        completeVideo.facetData.videoImages = getVideoImages(countryId, completeVideo.id.value, imagesDataByCountry);
        
        if(!isExtended(completeVideo))  /// "Extended" videos have VideoMiscData excluded.
            completeVideo.facetData.videoMiscData = miscData.get(completeVideo.id.value);
        completeVideo.countrySpecificData = countrySpecificByCountry.get(countryId).get(completeVideo.id.value);
        objectMapper.addObject(completeVideo);

        // keep track of created completeVideo for GlobalVideo creation
        addToGlobalVideoMap(completeVideo, country, globalVideoMap);
        return completeVideo;
    }

    private VideoImages getVideoImages(String countryId, Integer videoId, Map<String, Map<Integer, VideoImages>> imagesDataByCountry) {
        Map<Integer, VideoImages> countryDataMap = imagesDataByCountry.get(countryId);
        VideoImages images = null;
        if (countryDataMap != null) 
            images = countryDataMap.get(videoId);
        return images == null ? cycleConstants.EMPTY_VIDEO_IMAGES : images;
    }

    private void addToGlobalVideoMap(CompleteVideo completeVideo, ISOCountry country, Map<Video, Map<ISOCountry, CompleteVideo>> globalVideoMap) {
        Video video = completeVideo.id;
        Map<ISOCountry, CompleteVideo> countryMap = globalVideoMap.get(video);
        if (countryMap == null) {
            countryMap = new TreeMap<>(countryComparator);
            globalVideoMap.put(video, countryMap);
        }

        countryMap.put(country, completeVideo);
    }

    private String getTopNodeIdentifierString(Set<VideoHierarchyGroup> processGroup) {
        StringBuilder builder = new StringBuilder("(");
        boolean first = true;
        for (VideoHierarchyGroup topNodeGroup : processGroup) {
            if(!first)
                builder.append(",");
            builder.append(topNodeGroup);
            first = false;
        }
        builder.append(")");
        return builder.toString();
    }

    private static final VideoSetType VIDEO_SET_TYPE_EXTENDED = new VideoSetType("Extended");

    private static boolean isExtended(CompleteVideo completeVideo) {
        return completeVideo.facetData.videoMetaData.videoSetTypes.contains(VIDEO_SET_TYPE_EXTENDED);
    }

    private static boolean isGoLive(CompleteVideo completeVideo) {
        return completeVideo.facetData != null && completeVideo.facetData.videoMediaData != null && completeVideo.facetData.videoMediaData.isGoLive;
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
