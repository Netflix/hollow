package com.netflix.vms.transformer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
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

    public VMSTransformerWriteStateEngine() {
        super(new VMSTransformerHashCodeFinder());
        setTargetMaxTypeShardSize(16 * 1024 * 1024);
        initializeTopLevelTypeStates();
    }

    private void initializeTopLevelTypeStates() {
        HollowObjectMapper mapper = new HollowObjectMapper(this);
        mapper.doNotUseDefaultHashKeys();

        ///TODO: When we do the "Unified Primary Key Definitions", then these need to be discovered based on those.
        
        initializeTypeStates(mapper,
                CompleteVideo.class,
                VideoPackageData.class,
                NamedCollectionHolder.class,
                EncodingProfile.class,
                OriginServer.class,
                LanguageRights.class,
                DeploymentIntent.class,
                GlobalPerson.class,
                GlobalVideo.class,
                PersonImages.class,
                ArtWorkImageFormatEntry.class,
                ArtWorkImageTypeEntry.class,
                ArtWorkImageRecipe.class,
                DrmKey.class,
                WmDrmKey.class,
                DrmInfoData.class,
                DrmSystem.class,
                L10NResources.class,
                EncodingProfileGroup.class,
                CharacterImages.class,
                FileEncodingData.class,
                RolloutVideo.class,
                RolloutCharacter.class,
                FallbackUSArtwork.class,
                TopNVideoData.class,
                MulticatalogCountryData.class
        );
    }
    
    private void initializeTypeStates(HollowObjectMapper mapper, Class<?>... clazzes) {
        for(Class<?> clazz : clazzes)
            mapper.initializeTypeState(clazz);
    }

    public static HollowProducer initAndBuildProducer(HollowProducer.Builder b) {
        HollowProducer p = b.withHashCodeFinder(new VMSTransformerHashCodeFinder())
                .build();
        initProducer(p);
        return p;
    }

    private static void initProducer(HollowProducer p) {
        p.initializeDataModel(
                CompleteVideo.class,
                VideoPackageData.class,
                NamedCollectionHolder.class,
                EncodingProfile.class,
                OriginServer.class,
                LanguageRights.class,
                DeploymentIntent.class,
                GlobalPerson.class,
                GlobalVideo.class,
                PersonImages.class,
                ArtWorkImageFormatEntry.class,
                ArtWorkImageTypeEntry.class,
                ArtWorkImageRecipe.class,
                DrmKey.class,
                WmDrmKey.class,
                DrmInfoData.class,
                DrmSystem.class,
                L10NResources.class,
                EncodingProfileGroup.class,
                CharacterImages.class,
                FileEncodingData.class,
                RolloutVideo.class,
                RolloutCharacter.class,
                FallbackUSArtwork.class,
                TopNVideoData.class,
                MulticatalogCountryData.class
        );
    }

    public static HollowProducer initAndBuildNoStreamsProducer(HollowProducer.Builder b) {
        HollowProducer p = b.withHashCodeFinder(new VMSTransformerHashCodeFinder())
                .build();
        initProducerNoStreams(p);
        return p;
    }

    private static void initProducerNoStreams(HollowProducer p) {
        p.initializeDataModel(
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
                NamedCollectionHolder.class,
                OriginServer.class,
                PersonImages.class,
                RolloutVideo.class,
                RolloutCharacter.class,
                TopNVideoData.class,
                VideoPackageData.class,

                // Types ignored
                DeploymentIntent.class,
                DrmInfoData.class,
                DrmKey.class,
                FileEncodingData.class,
                WmDrmKey.class
        );
    }
}