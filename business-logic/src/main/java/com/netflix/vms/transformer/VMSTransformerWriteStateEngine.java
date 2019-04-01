package com.netflix.vms.transformer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageFormatEntry;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageRecipe;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;
import com.netflix.vms.transformer.hollowoutput.CharacterImages;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import com.netflix.vms.transformer.hollowoutput.DeploymentIntent;
import com.netflix.vms.transformer.hollowoutput.DrmInfoData;
import com.netflix.vms.transformer.hollowoutput.DrmKey;
import com.netflix.vms.transformer.hollowoutput.DrmSystem;
import com.netflix.vms.transformer.hollowoutput.EncodingProfile;
import com.netflix.vms.transformer.hollowoutput.EncodingProfileGroup;
import com.netflix.vms.transformer.hollowoutput.FallbackUSArtwork;
import com.netflix.vms.transformer.hollowoutput.FileEncodingData;
import com.netflix.vms.transformer.hollowoutput.GlobalPerson;
import com.netflix.vms.transformer.hollowoutput.GlobalVideo;
import com.netflix.vms.transformer.hollowoutput.L10NResources;
import com.netflix.vms.transformer.hollowoutput.LanguageRights;
import com.netflix.vms.transformer.hollowoutput.MulticatalogCountryData;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.OriginServer;
import com.netflix.vms.transformer.hollowoutput.PersonImages;
import com.netflix.vms.transformer.hollowoutput.RolloutCharacter;
import com.netflix.vms.transformer.hollowoutput.RolloutVideo;
import com.netflix.vms.transformer.hollowoutput.TopNVideoData;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
import com.netflix.vms.transformer.hollowoutput.WmDrmKey;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;

public class VMSTransformerWriteStateEngine extends HollowWriteStateEngine {

    // @@@ Check if types not annotated with @HollowPrimaryKey need to be
    // declared here.  If not it may be possible to declare the top-level
    // types using the schema selecting all types with a primary key
    private static final Class<?>[] TOP_LEVEL_TYPES = new Class[] {
            ArtWorkImageFormatEntry.class,
            ArtWorkImageRecipe.class,
            ArtWorkImageTypeEntry.class,
            CharacterImages.class,
            CompleteVideo.class,
            DrmSystem.class,
            EncodingProfile.class,
            EncodingProfileGroup.class,
            FallbackUSArtwork.class,
            GlobalPerson.class,
            GlobalVideo.class,
            L10NResources.class,
            LanguageRights.class,
            MulticatalogCountryData.class,
            NamedCollectionHolder.class,  // Not annotated with @HollowPrimaryKey
            OriginServer.class,
            PersonImages.class,
            RolloutVideo.class,
            RolloutCharacter.class,       // Not annotated with @HollowPrimaryKey
            TopNVideoData.class,          // Not annotated with @HollowPrimaryKey
            VideoPackageData.class,       // Not annotated with @HollowPrimaryKey

            // Top-level types filtered in nostreams
            DeploymentIntent.class,
            DrmInfoData.class,
            DrmKey.class,
            FileEncodingData.class,
            WmDrmKey.class
    };

    // Cannot use Class<?>[] since there is no explicit class for the collection types
    // and even if that was the case the class's simple name may not correspond to the hollow
    // type name if annotated with @HollowTypeName.  This distinction is important since
    // it is the type names that are input to the filtering mechanism
    private static final String[] NOSTREAM_IGNORED_TYPE_NAMES = new String[] {
            "ChunkDurationsString",
            "CodecPrivateDataString",
            "DeploymentIntent",
            "DownloadLocationSet",
            "DrmInfo",
            "DrmInfoData",
            "DrmKey",
            "DrmKeyString",
            "DrmHeader",
            "FileEncodingData",
            "ImageSubtitleIndexByteRange",
            "MapOfIntegerToDrmHeader",
            "MapOfDownloadableIdToDrmInfo",
            "QoEInfo",
            "SetOfStreamData",
            "StreamAdditionalData",
            "StreamData",
            "StreamDataDescriptor",
            "StreamDownloadLocationFilename",
            "StreamDrmData",
            "StreamMostlyConstantData",
            "WmDrmKey"

            // This type is not excluded and is present in nostreams blobs
            // but its keys and values are excluded and are not present.
            // This will cause a failure when writing out the map write state if
            // DrmKeyString has a primary key that becomes the hash key
            // ,
            // "MapOfDrmKeyStringToDrmKeyString"
    };

    public VMSTransformerWriteStateEngine() {
        super(new VMSTransformerHashCodeFinder());
        setTargetMaxTypeShardSize(16 * 1024 * 1024);
        initializeTopLevelTypeStates();
    }

    private void initializeTopLevelTypeStates() {
        HollowObjectMapper mapper = new HollowObjectMapper(this);
        mapper.doNotUseDefaultHashKeys();

        ///TODO: When we do the "Unified Primary Key Definitions", then these need to be discovered based on those.

        initializeTypeStates(mapper, TOP_LEVEL_TYPES);
    }

    private void initializeTypeStates(HollowObjectMapper mapper, Class<?>... clazzes) {
        for (Class<?> clazz : clazzes) {
            mapper.initializeTypeState(clazz);
        }
    }


    public static HollowProducer initAndBuildProducer(HollowProducer.Builder b) {
        HollowProducer p = b.withHashCodeFinder(new VMSTransformerHashCodeFinder())
                .build();
        initProducer(p);
        return p;
    }

    private static void initProducer(HollowProducer p) {
        p.initializeDataModel(TOP_LEVEL_TYPES);
    }

    public static HollowProducer initAndBuildNoStreamsProducer(HollowProducer.Builder b) {
        HollowProducer p = b.withHashCodeFinder(new VMSTransformerHashCodeFinder())
                .build();
        initProducerNoStreams(p);
        return p;
    }

    private static void initProducerNoStreams(HollowProducer p) {
        p.initializeDataModel(TOP_LEVEL_TYPES);
    }

    public static HollowFilterConfig getNoStreamsFilterConfig() {
        HollowFilterConfig filterConfig = new HollowFilterConfig(true);

        for (String type : NOSTREAM_IGNORED_TYPE_NAMES) {
            filterConfig.addType(type);
        }

        return filterConfig;
    }

    public static HollowCombiner getNoStreamsCombiner(HollowReadStateEngine input, HollowWriteStateEngine output) {
        HollowCombiner combiner = new HollowCombiner(output, input);
        combiner.addIgnoredTypes(NOSTREAM_IGNORED_TYPE_NAMES);
        return combiner;
    }
}