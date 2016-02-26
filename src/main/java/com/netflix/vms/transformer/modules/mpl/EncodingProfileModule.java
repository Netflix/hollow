package com.netflix.vms.transformer.modules.mpl;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.ProtectionTypesHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.AudioChannelsDescriptor;
import com.netflix.vms.transformer.hollowoutput.EncodingProfile;
import com.netflix.vms.transformer.hollowoutput.ProfileTypeDescriptor;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoDimensionsDescriptor;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class EncodingProfileModule extends AbstractTransformModule {

    private Map<String, ProfileTypeDescriptor> profileTypeMap = new HashMap<>();
    private Map<Integer, AudioChannelsDescriptor> audioChannelsMap = new HashMap<>();
    private Map<Integer, VideoDimensionsDescriptor> videoDimensionsMap = new HashMap<>();
    private Map<String, Strings> stringsMap = new HashMap<>();

    private final HollowPrimaryKeyIndex protectionTypeIndex;


    public EncodingProfileModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, mapper);

        this.protectionTypeIndex = indexer.getPrimaryKeyIndex(IndexSpec.PROTECTION_TYPES);
    }

    @Override
    public void transform() {
        for (StreamProfilesHollow input : api.getAllStreamProfilesHollow()) {
            EncodingProfile output = new EncodingProfile();
            output.id = (int)input._getId();
            output.name26AndBelowStr = toCharArray(input._getName26AndBelow());
            output.name27AndAboveStr = toCharArray(input._getName27AndAbove());
            output.drmKeyGroup = (int)input._getDrmKeyGroup();

            output.profileTypeDescriptor = getProfileType(input._getProfileType()._getValue());
            output.audioChannelsDescriptor = getAudioChannels((int) input._getAudioChannelCount());

            long drmType = input._getDrmType();
            int protectionTypeOrdinal = protectionTypeIndex.getMatchingOrdinal(drmType);
            ProtectionTypesHollow protectionTypes = api.getProtectionTypesHollow(protectionTypeOrdinal);
            output.dRMType = protectionTypes != null ? Collections.singleton(getStrings(protectionTypes._getName()._getValue())) : Collections.emptySet();

            output.fileExtensionStr = toCharArray(input._getFileExtension());
            output.mimeTypeStr = toCharArray(input._getMimeType());
            output.descriptionStr = toCharArray(input._getDescription());

            output.isAdaptiveSwitching = input._getIsAdaptiveSwitching();
            output.videoDimensionsDescriptor = input._getIs3D() ? getVideoDimensions(3) : getVideoDimensions(2);

            mapper.addObject(output);
        }
    }

    private char[] toCharArray(StringHollow str) {
        if (str == null || str._getValue() == null) return null;
        return str._getValue().toCharArray();
    }

    private ProfileTypeDescriptor getProfileType(final String profileTypeName) {
        ProfileTypeDescriptor result = profileTypeMap.get(profileTypeName);
        if (result != null) return result;

        if (StringUtils.equals("AUDIO", profileTypeName)) {
            result = newProfileTypeDescriptor(1, "Audio", "Audio");
        } else if (StringUtils.equals("VIDEO", profileTypeName)) {
            result = newProfileTypeDescriptor(2, "Video", "Video");
        } else if (StringUtils.equals("TEXT", profileTypeName)) {
            result = newProfileTypeDescriptor(3, "Text", "Timed Text");
        } else if (StringUtils.equals("MUXED", profileTypeName)) {
            result = newProfileTypeDescriptor(4, "Muxed", "Muxed");
        } else if (StringUtils.equals("TRICKPLAY", profileTypeName)) {
            result = newProfileTypeDescriptor(5, "Trick Play", "Trick Play");
        } else if (StringUtils.equals("MERCHSTILL", profileTypeName)) {
            result = newProfileTypeDescriptor(6, "MerchStills", "MerchStills");
        } else {
            result = newProfileTypeDescriptor(-1, "unknown", "unknown");
        }

        profileTypeMap.put(profileTypeName, result);
        return result;
    }

    private static ProfileTypeDescriptor newProfileTypeDescriptor(final int id, final String name, final String description) {
        ProfileTypeDescriptor result = new ProfileTypeDescriptor();
        result.id = id;
        result.name = name == null ? null : new Strings(name);
        result.description = description == null ? null : new Strings(description);
        return result;
    }

    private AudioChannelsDescriptor getAudioChannels(final int channels) {
        AudioChannelsDescriptor result = audioChannelsMap.get(channels);
        if (result != null) return result;

        switch(channels) {
            case 0:
                result = newAudioChannelsDescriptor(0, "", "");
                break;
            case 1:
                result = newAudioChannelsDescriptor(1, "1.0", "Mono");
                break;
            case 2:
                result = newAudioChannelsDescriptor(2, "2.0", "Stereo");
                break;
            case 6:
                result = newAudioChannelsDescriptor(6, "5.1", "Dolby Digital Plus");
                break;
            case 8:
                result = newAudioChannelsDescriptor(8, "7.1", "Dolby Digital Plus");
                break;
            default:
                result = newAudioChannelsDescriptor(-1, "UNKNOWN", "");
                break;
        }

        audioChannelsMap.put(channels, result);
        return result;
    }
    private static AudioChannelsDescriptor newAudioChannelsDescriptor(final int numberOfChannels, final String name, final String description) {
        AudioChannelsDescriptor result = new AudioChannelsDescriptor();
        result.numberOfChannels = numberOfChannels;
        result.name = name == null ? null : new Strings(name);
        result.description = description == null ? null : new Strings(description);
        return result;
    }

    private VideoDimensionsDescriptor getVideoDimensions(final int dimensions) {
        VideoDimensionsDescriptor result = videoDimensionsMap.get(dimensions);
        if (result != null) return result;

        switch (dimensions) {
            case 2:
                result = newVideoDimensionsDescriptor(2, "2D", "2D");
                break;
            case 3:
                result = newVideoDimensionsDescriptor(3, "3D", "3D");
                break;
            default:
                result = newVideoDimensionsDescriptor(-1, "UNKNOWN", "");
                break;
        }

        videoDimensionsMap.put(dimensions, result);
        return result;
    }

    private static VideoDimensionsDescriptor newVideoDimensionsDescriptor(final int dimensions, final String name, final String description) {
        VideoDimensionsDescriptor result = new VideoDimensionsDescriptor();
        result.dimensions = dimensions;
        result.name = name == null ? null : new Strings(name);
        result.description = description == null ? null : new Strings(description);
        return result;
    }

    private Strings getStrings(String string) {
        Strings result = stringsMap.get(string);
        if (result == null) result = new Strings(string);

        stringsMap.put(string, result);
        return result;
    }

}