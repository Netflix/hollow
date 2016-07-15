package com.netflix.vms.transformer;

import com.netflix.vms.transformer.hollowoutput.TopNVideoData;

import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageFormatEntry;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageRecipe;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;
import com.netflix.vms.transformer.hollowoutput.CharacterImages;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import com.netflix.vms.transformer.hollowoutput.DefaultExtensionRecipe;
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
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.OriginServer;
import com.netflix.vms.transformer.hollowoutput.PersonImages;
import com.netflix.vms.transformer.hollowoutput.RolloutCharacter;
import com.netflix.vms.transformer.hollowoutput.RolloutVideo;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode_CountryList;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
import com.netflix.vms.transformer.hollowoutput.WmDrmKey;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;

public class VMSTransformerWriteStateEngine extends HollowWriteStateEngine {

    public VMSTransformerWriteStateEngine() {
        super(new VMSTransformerHashCodeFinder());
        initializeTopLevelTypeStates();
    }

    private void initializeTopLevelTypeStates() {
        HollowObjectMapper mapper = new HollowObjectMapper(this);

        ///TODO: When we do the "Unified Primary Key Definitions", then these need to be discovered based on those.
        
        initializeTypeStates(mapper,
                CompleteVideo.class,
                VideoEpisode_CountryList.class,
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
                DefaultExtensionRecipe.class,
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
                TopNVideoData.class
        );
    }
    
    private void initializeTypeStates(HollowObjectMapper mapper, Class<?>... clazzes) {
        for(Class<?> clazz : clazzes)
            mapper.initializeTypeState(clazz);
    }
}