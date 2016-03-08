package com.netflix.vms.transformer;

import com.netflix.vms.transformer.hollowoutput.VideoSetType;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDisplaySetHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoFacetData;
import com.netflix.vms.transformer.hollowoutput.DeploymentIntent;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.RolloutVideo;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoCollectionsData;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.hollowoutput.VideoMiscData;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.misc.TopNVideoDataModule;
import com.netflix.vms.transformer.misc.VideoEpisodeCountryDecoratorModule;
import com.netflix.vms.transformer.modules.TransformModule;
import com.netflix.vms.transformer.modules.artwork.CharacterImagesModule;
import com.netflix.vms.transformer.modules.artwork.PersonImagesModule;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsDataHierarchy;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsModule;
import com.netflix.vms.transformer.modules.deploymentintent.CacheDeploymentIntentModule;
import com.netflix.vms.transformer.modules.media.VideoMediaDataModule;
import com.netflix.vms.transformer.modules.meta.VideoMetaDataModule;
import com.netflix.vms.transformer.modules.meta.VideoMiscDataModule;
import com.netflix.vms.transformer.modules.mpl.DrmSystemModule;
import com.netflix.vms.transformer.modules.mpl.EncodingProfileModule;
import com.netflix.vms.transformer.modules.mpl.OriginServerModule;
import com.netflix.vms.transformer.modules.packages.PackageDataModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkFormatModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkImageRecipeModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.ArtworkTypeModule;
import com.netflix.vms.transformer.modules.passthrough.artwork.DefaultExtensionRecipeModule;
import com.netflix.vms.transformer.modules.passthrough.beehive.RolloutCharacterModule;
import com.netflix.vms.transformer.modules.passthrough.mpl.EncodingProfileGroupModule;
import com.netflix.vms.transformer.modules.person.GlobalPersonModule;
import com.netflix.vms.transformer.modules.rollout.RolloutVideoModule;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleTransformer {

    private final ThreadLocal<VideoCollectionsModule> collectionsModuleRef = new ThreadLocal<VideoCollectionsModule>();
    private final ThreadLocal<VideoMetaDataModule> metadataModuleRef = new ThreadLocal<VideoMetaDataModule>();
    private final ThreadLocal<PackageDataModule> packageDataModuleRef = new ThreadLocal<PackageDataModule>();
    private final ThreadLocal<VideoMediaDataModule> mediadataModuleRef = new ThreadLocal<VideoMediaDataModule>();
    private final ThreadLocal<VideoMiscDataModule> miscdataModuleRef = new ThreadLocal<VideoMiscDataModule>();

    private final VMSHollowVideoInputAPI api;
    private VMSTransformerIndexer indexer;

    public SimpleTransformer(VMSHollowVideoInputAPI api) {
        this.api = api;
    }

    public HollowWriteStateEngine transform() throws Exception {
        indexer = new VMSTransformerIndexer((HollowReadStateEngine)api.getDataAccess(), new SimultaneousExecutor());

        final ShowHierarchyInitializer hierarchyInitializer = new ShowHierarchyInitializer(api, indexer);

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine(new VMSTransformerHashCodeFinder());
        final HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);

        SimultaneousExecutor executor = new SimultaneousExecutor();

        long startTime = System.currentTimeMillis();

        for(final VideoDisplaySetHollow displaySet : api.getAllVideoDisplaySetHollow()) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    VideoCollectionsModule collectionsModule = getVideoCollectionsModule();
                    VideoMetaDataModule metadataModule = getVideoMetaDataModule();
                    PackageDataModule packageDataModule = getPackageDataModule(objectMapper);
                    VideoMediaDataModule mediaDataModule = getVideoMediaDataModule();
                    VideoMiscDataModule miscdataModule = getVideoMiscDataModule();
                    VideoEpisodeCountryDecoratorModule countryDecoratorModule = new VideoEpisodeCountryDecoratorModule(api, objectMapper);

                    Map<String, ShowHierarchy> showHierarchiesByCountry = hierarchyInitializer.getShowHierarchiesByCountry(displaySet);

                    if (showHierarchiesByCountry != null) {

                        Map<String, VideoCollectionsDataHierarchy> vcdByCountry = collectionsModule.buildVideoCollectionsDataByCountry(showHierarchiesByCountry);
                        Map<String, Map<Integer, VideoMetaData>> vmdByCountry = metadataModule.buildVideoMetaDataByCountry(showHierarchiesByCountry);
                        Map<String, Map<Integer, VideoMediaData>> mediaDataByCountry = mediaDataModule.buildVideoMediaDataByCountry(showHierarchiesByCountry);
                        Map<Integer, VideoMiscData> miscData = miscdataModule.buildVideoMiscDataByCountry(showHierarchiesByCountry);

                        if(vcdByCountry != null) {
                            writeJustTheCurrentData(vcdByCountry, vmdByCountry, miscData, mediaDataByCountry, objectMapper);

                            for(String country : vcdByCountry.keySet()) {
                                countryDecoratorModule.decorateVideoEpisodes(country, vcdByCountry.get(country));
                            }
                        }
                        packageDataModule.transform(showHierarchiesByCountry);
                    }
                }
            });
        }

        objectMapper.addObject(new DeploymentIntent());
        RolloutVideo rolloutVideoForSpec = new RolloutVideo();
        rolloutVideoForSpec.video = new Video(-1);
        objectMapper.addObject(rolloutVideoForSpec);
        // @formatter:off

        // Register Transform Modules
        List<TransformModule> moduleList = Arrays.<TransformModule>asList(
                new DrmSystemModule(api, objectMapper),
                new OriginServerModule(api, objectMapper, indexer),
                new EncodingProfileModule(api, objectMapper, indexer),
                new ArtworkFormatModule(api, objectMapper),
                new CacheDeploymentIntentModule(api, objectMapper),
                new ArtworkTypeModule(api, objectMapper),

                new ArtworkImageRecipeModule(api, objectMapper),
                new DefaultExtensionRecipeModule(api, objectMapper),
                new RolloutCharacterModule(api, objectMapper),
                new RolloutVideoModule(api, objectMapper, indexer),
                new EncodingProfileGroupModule(api, objectMapper),
                new GlobalPersonModule(api, objectMapper, indexer),
                new TopNVideoDataModule(api, objectMapper),
                new PersonImagesModule(api, objectMapper, indexer),
                new CharacterImagesModule(api, objectMapper, indexer)
                );

        // @formatter:on
        // Execute Transform Modules
        for(TransformModule m : moduleList) {
            long tStart = System.currentTimeMillis();
            m.transform();
            long tDuration = System.currentTimeMillis() - tStart;
            System.out.println(String.format("Finished Trasform for module=%s, duration=%s", m.getName(), tDuration));
        }


        executor.awaitSuccessfulCompletion();

        long endTime = System.currentTimeMillis();
        System.out.println("Processed all videos in " + (endTime - startTime) + "ms");

        return writeStateEngine;
    }

    private VideoCollectionsModule getVideoCollectionsModule() {
        VideoCollectionsModule module = collectionsModuleRef.get();
        if(module == null) {
            module = new VideoCollectionsModule(api, indexer);
            collectionsModuleRef.set(module);
        }
        return module;
    }

    private VideoMetaDataModule getVideoMetaDataModule() {
        VideoMetaDataModule module = metadataModuleRef.get();
        if(module == null) {
            module = new VideoMetaDataModule(api, indexer);
            metadataModuleRef.set(module);
        }
        return module;
    }

    private PackageDataModule getPackageDataModule(HollowObjectMapper objectMapper) {
        PackageDataModule module = packageDataModuleRef.get();
        if(module == null) {
            module = new PackageDataModule(api, objectMapper, indexer);
            packageDataModuleRef.set(module);
        }
        return module;
    }

    private VideoMediaDataModule getVideoMediaDataModule() {
        VideoMediaDataModule module = mediadataModuleRef.get();
        if (module == null) {
            module = new VideoMediaDataModule(api, indexer);
            mediadataModuleRef.set(module);
        }
        return module;
    }

    private VideoMiscDataModule getVideoMiscDataModule() {
        VideoMiscDataModule module = miscdataModuleRef.get();
        if(module == null) {
            module = new VideoMiscDataModule(api, indexer);
            miscdataModuleRef.set(module);
        }
        return module;
    }

    private void writeJustTheCurrentData(Map<String, VideoCollectionsDataHierarchy> vcdByCountry,
            Map<String, Map<Integer, VideoMetaData>> vmdByCountry,
            Map<Integer, VideoMiscData> miscData,
            Map<String, Map<Integer, VideoMediaData>> mediaDataByCountry,
            HollowObjectMapper objectMapper) {

        for(Map.Entry<String, VideoCollectionsDataHierarchy> countryHierarchyEntry : vcdByCountry.entrySet()) {
            String countryId = countryHierarchyEntry.getKey();
            ISOCountry country = getCountry(countryId);
            VideoCollectionsDataHierarchy hierarchy = countryHierarchyEntry.getValue();
            VideoCollectionsData videoCollectionsData = hierarchy.getTopNode();

            CompleteVideo topNode = addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry,
                    objectMapper, country, countryId, videoCollectionsData, hierarchy.getTopNode().topNode);

            if(topNode.facetData.videoCollectionsData.nodeType == VideoCollectionsDataHierarchy.SHOW) {
                int sequenceNumber = 0;

                for(Map.Entry<Integer, VideoCollectionsData> showEntry : hierarchy.getOrderedSeasons().entrySet()) {
                    addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry,
                            objectMapper, country, countryId, showEntry.getValue(), new Video(showEntry.getKey().intValue()));

                    for(Map.Entry<Integer, VideoCollectionsData> episodeEntry : hierarchy.getOrderedSeasonEpisodes(++sequenceNumber).entrySet()) {
                        addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry,
                                objectMapper, country, countryId, episodeEntry.getValue(), new Video(episodeEntry.getKey().intValue()));
                    }
                }
            }

            for(Map.Entry<Integer, VideoCollectionsData> supplementalEntry : hierarchy.getSupplementalVideosCollectionsData().entrySet()) {
                addCompleteVideo(vmdByCountry, miscData, mediaDataByCountry,
                        objectMapper, country, countryId, supplementalEntry.getValue(), new Video(supplementalEntry.getKey().intValue()));
            }
        }
    }

    private CompleteVideo addCompleteVideo(
            Map<String, Map<Integer, VideoMetaData>> vmdByCountry,
            Map<Integer, VideoMiscData> miscData,
            Map<String, Map<Integer, VideoMediaData>> mediaDataByCountry,
            HollowObjectMapper objectMapper, ISOCountry country, String countryId,
            VideoCollectionsData videoCollectionsData, Video video) {
        CompleteVideo completeVideo = new CompleteVideo();
        completeVideo.id = video;
        completeVideo.country = country;
        completeVideo.facetData = new CompleteVideoFacetData();
        completeVideo.facetData.videoCollectionsData = videoCollectionsData;
        completeVideo.facetData.videoMetaData = vmdByCountry.get(countryId).get(completeVideo.id.value);
        completeVideo.facetData.videoMediaData = mediaDataByCountry.get(countryId).get(completeVideo.id.value);
        if(!isExtended(completeVideo))  /// "Extended" videos have VideoMiscData excluded.
            completeVideo.facetData.videoMiscData = miscData.get(completeVideo.id.value);
        objectMapper.addObject(completeVideo);
        return completeVideo;
    }

    private static final VideoSetType VIDEO_SET_TYPE_EXTENDED = new VideoSetType("Extended");
    private boolean isExtended(CompleteVideo completeVideo) {
        return completeVideo.facetData.videoMetaData.videoSetTypes.contains(VIDEO_SET_TYPE_EXTENDED);
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

}
