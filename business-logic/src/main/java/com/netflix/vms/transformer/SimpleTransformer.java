package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.IndividualTransformFailed;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.FailedProcessingIndividualHierarchies;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.ShowGrouper.TopNodeProcessGroup;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoFacetData;
import com.netflix.vms.transformer.hollowoutput.FallbackUSArtwork;
import com.netflix.vms.transformer.hollowoutput.GlobalPerson;
import com.netflix.vms.transformer.hollowoutput.GlobalVideo;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoCollectionsData;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.hollowoutput.VideoMiscData;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.misc.TopNVideoDataModule;
import com.netflix.vms.transformer.misc.VideoEpisodeCountryDecoratorModule;
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
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkFormatModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkImageRecipeModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkTypeModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.DefaultExtensionRecipeModule;
import com.netflix.vms.transformer.modules.passthrough.beehive.RolloutCharacterModule;
import com.netflix.vms.transformer.modules.passthrough.mpl.EncodingProfileGroupModule;
import com.netflix.vms.transformer.modules.person.GlobalPersonModule;
import com.netflix.vms.transformer.modules.rollout.RolloutVideoModule;
import com.netflix.vms.transformer.namedlist.NamedListCompletionModule;
import com.netflix.vms.transformer.namedlist.VideoNamedListModule;
import com.netflix.vms.transformer.namedlist.VideoNamedListModule.VideoNamedListPopulator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
    private VMSTransformerIndexer indexer;

    SimpleTransformer(VMSHollowInputAPI inputAPI, VMSTransformerWriteStateEngine outputStateEngine) {
        this(inputAPI, outputStateEngine, new SimpleTransformerContext());
        //ctx.setNowMillis(1462034581112L);
    }

    public SimpleTransformer(VMSHollowInputAPI inputAPI, VMSTransformerWriteStateEngine outputStateEngine, TransformerContext ctx) {
        this.api = inputAPI;
        this.writeStateEngine = outputStateEngine;
        this.ctx = ctx;
        this.cycleConstants = new CycleConstants();
    }

    public void setPublishCycleDataTS(long time) {
        ctx.setNowMillis(time);
    }

    public HollowWriteStateEngine transform() throws Throwable {
        long startTime = System.currentTimeMillis();
        indexer = new VMSTransformerIndexer((HollowReadStateEngine)api.getDataAccess());
        long endTime = System.currentTimeMillis();
        System.out.println("INDEXED IN " + (endTime - startTime) + "ms");

        AtomicInteger failedIndividualTransforms = new AtomicInteger(0);

        final ShowHierarchyInitializer hierarchyInitializer = new ShowHierarchyInitializer(api, indexer, ctx);

        final HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);

        SimultaneousExecutor executor = new SimultaneousExecutor();

        this.videoNamedListModule = new VideoNamedListModule(ctx, cycleConstants, objectMapper);

        startTime = System.currentTimeMillis();
        ShowGrouper showGrouper = new ShowGrouper(api, ctx);

        final List<Set<TopNodeProcessGroup>> processGroups = showGrouper.getProcessGroups();

        AtomicInteger processedCount = new AtomicInteger();
        int progressDivisor = getProgressDivisor(processGroups.size());

        for(int i=0;i<executor.getCorePoolSize();i++) {
            executor.execute(() -> {
                PackageDataModule packageDataModule = new PackageDataModule(api, objectMapper, indexer);
                VideoCollectionsModule collectionsModule = new VideoCollectionsModule(api, cycleConstants, indexer);
                VideoMetaDataModule metadataModule = new VideoMetaDataModule(api, ctx, cycleConstants, indexer);
                VideoMediaDataModule mediaDataModule = new VideoMediaDataModule(api, indexer);
                VideoMiscDataModule miscDataModule = new VideoMiscDataModule(api, indexer);
                VideoImagesDataModule imagesDataModule = new VideoImagesDataModule(api, ctx, objectMapper, indexer);
                CountrySpecificDataModule countrySpecificModule = new CountrySpecificDataModule(api, ctx, cycleConstants, indexer);
                VideoEpisodeCountryDecoratorModule countryDecoratorModule = new VideoEpisodeCountryDecoratorModule(api, objectMapper);

                int idx = processedCount.getAndIncrement();
                while (idx < processGroups.size()) {
                    Set<TopNodeProcessGroup> processGroup = processGroups.get(idx);
                    try {
                        Set<Integer> droppedIds = new HashSet<>();
                        Map<String, Set<ShowHierarchy>> showHierarchiesByCountry = hierarchyInitializer.getShowHierarchiesByCountry(processGroup, droppedIds);
                        Map<Integer, VideoPackageData> transformedPackageData = packageDataModule.transform(showHierarchiesByCountry, droppedIds);

                        if (showHierarchiesByCountry != null) {
                            Map<String, Set<VideoCollectionsDataHierarchy>> vcdByCountry = collectionsModule.buildVideoCollectionsDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, VideoMetaData>> vmdByCountry = metadataModule.buildVideoMetaDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, VideoMediaData>> mediaDataByCountry = mediaDataModule.buildVideoMediaDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, VideoImages>> imagesDataByCountry = imagesDataModule.buildVideoImagesByCountry(showHierarchiesByCountry);
                            Map<Integer, VideoMiscData> miscData = miscDataModule.buildVideoMiscDataByCountry(showHierarchiesByCountry);
                            Map<String, Map<Integer, CompleteVideoCountrySpecificData>> countrySpecificByCountry = countrySpecificModule.buildCountrySpecificDataByCountry(showHierarchiesByCountry, transformedPackageData);

                            if (vcdByCountry != null) {
                                writeJustTheCurrentData(vcdByCountry, vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry, objectMapper);

                                for (String country : vcdByCountry.keySet()) {
                                    countryDecoratorModule.decorateVideoEpisodes(country, vcdByCountry.get(country));
                                }
                            }

                            // Process Video Related L10N
                            new L10NVideoResourcesModule(api, ctx, objectMapper, indexer).transform(showHierarchiesByCountry);
                        }
                    } catch (Throwable th) {
                        ctx.getLogger().error(IndividualTransformFailed, "Transformation failed for hierarchy with top node(s) " + getTopNodeIdentifierString(processGroup), th);
                        failedIndividualTransforms.incrementAndGet();
                    }

                    if (idx % progressDivisor == 0) {
                        ctx.getLogger().info(LogTag.TransformProgress, ("finished percent=" + (idx / progressDivisor)));
                    }

                    idx = processedCount.getAndIncrement();
                }
            });

        }

        // @formatter:off
        // Register Transform Modules
        List<TransformModule> moduleList = Arrays.<TransformModule>asList(
                new DrmSystemModule(api, ctx, objectMapper),
                new OriginServerModule(api, ctx, objectMapper, indexer),
                new EncodingProfileModule(api, ctx, objectMapper, indexer),
                new ArtworkFormatModule(api, ctx, objectMapper),
                new CacheDeploymentIntentModule(api, ctx, objectMapper),
                new ArtworkTypeModule(api, ctx, objectMapper),
                new ArtworkImageRecipeModule(api, ctx, objectMapper),
                new EncodingProfileGroupModule(api, ctx, objectMapper),
                new DefaultExtensionRecipeModule(api, ctx, objectMapper),

                new L10NMiscResourcesModule(api, ctx, objectMapper, indexer),
                new LanguageRightsModule(api, ctx, objectMapper, indexer),
                new TopNVideoDataModule(api, ctx, objectMapper),
                new RolloutCharacterModule(api, ctx, objectMapper),
                new RolloutVideoModule(api, ctx, objectMapper, indexer),
                new PersonImagesModule(api, ctx, objectMapper, indexer),
                new CharacterImagesModule(api, ctx, objectMapper, indexer)
                );

        // @formatter:on
        // Execute Transform Modules
        for(TransformModule m : moduleList) {
            long tStart = System.currentTimeMillis();
            m.transform();
            long tDuration = System.currentTimeMillis() - tStart;
            ctx.getLogger().info(LogTag.NonVideoSpecificTransformDuration, String.format("Finished Trasform for module=%s, duration=%s", m.getName(), tDuration));
        }

        /// GlobalPersonModule is pulled out separately here because we will use the result in the NamedListCompletionModule
        GlobalPersonModule globalPersonModule = new GlobalPersonModule(api, ctx, objectMapper, indexer);
        long tStart = System.currentTimeMillis();
        List<GlobalPerson> allGlobalPersonRecords = globalPersonModule.transformPersons();
        long tDuration = System.currentTimeMillis() - tStart;
        ctx.getLogger().info(LogTag.NonVideoSpecificTransformDuration, String.format("Finished Trasform for module=%s, duration=%s", globalPersonModule.getName(), tDuration));

        executor.awaitSuccessfulCompletion();

        //// NamedListCompletionModule happens after all hierarchies are already processed -- now we have built the ThreadSafeBitSets corresponding
        //// to the NamedLists, and we can build the POJOs using those.
        tStart = System.currentTimeMillis();
        NamedListCompletionModule namedListCompleter = new NamedListCompletionModule(videoNamedListModule, allGlobalPersonRecords, objectMapper);
        namedListCompleter.transform();
        tDuration = System.currentTimeMillis() - tStart;
        ctx.getLogger().info(LogTag.NonVideoSpecificTransformDuration, String.format("Finished Trasform for module=%s, duration=%s", namedListCompleter.getName(), tDuration));

        ctx.getLogger().info(LogTag.TransformProgress, "finished percent=100");
        ctx.getMetricRecorder().recordMetric(FailedProcessingIndividualHierarchies, failedIndividualTransforms.get());

        endTime = System.currentTimeMillis();
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
            ISOCountry country = getCountry(countryId);

            for(VideoCollectionsDataHierarchy hierarchy : countryHierarchyEntry.getValue()) {
                VideoCollectionsData videoCollectionsData = hierarchy.getTopNode();

                namedListPopulator.setCountry(countryId);

                // Process TopNode
                CompleteVideo topNode = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                        objectMapper, country, countryId, videoCollectionsData, hierarchy.getTopNode().topNode, globalVideoMap);

                namedListPopulator.addCompleteVideo(topNode, true);

                // Process Show children
                if(topNode.facetData.videoCollectionsData.nodeType == cycleConstants.SHOW) {
                    int sequenceNumber = 0;
                    // Process Seasons
                    for(Map.Entry<Integer, VideoCollectionsData> showEntry : hierarchy.getOrderedSeasons().entrySet()) {
                        CompleteVideo season = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                                objectMapper, country, countryId, showEntry.getValue(), new Video(showEntry.getKey().intValue()), globalVideoMap);

                        namedListPopulator.addCompleteVideo(season, false);

                        // Process Episodes
                        for(Map.Entry<Integer, VideoCollectionsData> episodeEntry : hierarchy.getOrderedSeasonEpisodes(++sequenceNumber).entrySet()) {
                            CompleteVideo episode = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                                    objectMapper, country, countryId, episodeEntry.getValue(), new Video(episodeEntry.getKey().intValue()), globalVideoMap);

                            namedListPopulator.addCompleteVideo(episode, false);
                        }
                    }
                }

                // Process Supplemental
                for(Map.Entry<Integer, VideoCollectionsData> supplementalEntry : hierarchy.getSupplementalVideosCollectionsData().entrySet()) {
                    CompleteVideo supplemental = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry, imagesDataByCountry, countrySpecificByCountry,
                            objectMapper, country, countryId, supplementalEntry.getValue(), new Video(supplementalEntry.getKey().intValue()), globalVideoMap);

                    namedListPopulator.addCompleteVideo(supplemental, false);
                }
            }
        }

        // ----------------------
        // Process GlobalVideo
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
        if (countryDataMap == null) return null;
        return countryDataMap.get(videoId);
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

    private String getTopNodeIdentifierString(Set<TopNodeProcessGroup> processGroup) {
        StringBuilder builder = new StringBuilder("(");
        boolean first = true;
        for(TopNodeProcessGroup topNodeGroup : processGroup) {
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

    private Map<String, ISOCountry> countries = new HashMap<String, ISOCountry>();
    private ISOCountry getCountry(String id) {
        ISOCountry country = countries.get(id);
        if(country == null) {
            country = new ISOCountry(id);
            countries.put(id, country);
        }
        return country;
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
