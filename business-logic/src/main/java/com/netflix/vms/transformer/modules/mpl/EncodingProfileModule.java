package com.netflix.vms.transformer.modules.mpl;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ProtectionTypesHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
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

public class EncodingProfileModule extends AbstractTransformModule {

    private final ThreadLocal<Map<String, ProfileTypeDescriptor>> profileTypeMapRef = new ThreadLocal<>();
    private final ThreadLocal<Map<Integer, VideoDimensionsDescriptor>> videoDimensionsMapRef = new ThreadLocal<>();
    private final ThreadLocal<Map<String, Strings>> stringsMapRef = new ThreadLocal<>();

    private final AudioChannelsDescriptorCache audioChannelsDescriptorCache = new AudioChannelsDescriptorCache();

    private final HollowPrimaryKeyIndex protectionTypeIndex;


    public EncodingProfileModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, cycleConstants, mapper);

        this.protectionTypeIndex = indexer.getPrimaryKeyIndex(IndexSpec.PROTECTION_TYPES);
    }

    @Override
    public void transform() {
        for (StreamProfilesHollow input : api.getAllStreamProfilesHollow()) {
            EncodingProfile output = new EncodingProfile();
            output.id = (int) input._getId();
            output.name26AndBelowStr = input._getName26AndBelow()._getValue();
            output.name27AndAboveStr = input._getName27AndAbove()._getValue();
            output.drmKeyGroup = (int) input._getDrmKeyGroup();

            output.profileTypeDescriptor = getProfileType(input._getProfileType()._getValue());
            output.audioChannelsDescriptor = audioChannelsDescriptorCache.getAudioChannels((int) input._getAudioChannelCount());

            long drmType = input._getDrmType();
            int protectionTypeOrdinal = protectionTypeIndex.getMatchingOrdinal(drmType);
            ProtectionTypesHollow protectionTypes = api.getProtectionTypesHollow(protectionTypeOrdinal);
            output.dRMType = protectionTypes != null ? Collections.<Strings>singleton(getStrings(protectionTypes._getName()._getValue())) : Collections.<Strings>emptySet();

            output.fileExtensionStr = input._getFileExtension()._getValue();
            output.mimeTypeStr = input._getMimeType()._getValue();
            output.descriptionStr = input._getDescription()._getValue();

            output.isAdaptiveSwitching = input._getIsAdaptiveSwitching();
            output.videoDimensionsDescriptor = input._getIs3D() ? getVideoDimensions(3) : getVideoDimensions(2);

            output.audioCodec = input._getAudioCodec()._getValue();
            output.videoCodec = input._getVideoCodec()._getValue();
            output.colorAttributes = input._getColorAttributes()._getValue();
            output.bitDepth = (int) input._getBitDepth();
            output.drmKeyType = input._getDrmKeyType()._getValue();
            output.encryptionScheme = input._getEncryptionScheme()._getValue();
            output.playreadyHeaderVersion = input._getPlayreadyHeaderVersion()._getValue();

            mapper.add(output);
        }
    }

    private char[] toCharArray(StringHollow str) {
        if (str == null || str._getValue() == null) return null;
        return str._getValue().toCharArray();
    }

    private <K, V> Map<K, V> getMap(ThreadLocal<Map<K, V>> local) {
        Map<K, V> map = local.get();
        if (map == null) {
            map = new HashMap<>();
            local.set(map);
        }

        return map;
    }

    private ProfileTypeDescriptor getProfileType(final String profileTypeName) {
        Map<String, ProfileTypeDescriptor> profileTypeMap = getMap(profileTypeMapRef);
        ProfileTypeDescriptor result = profileTypeMap.get(profileTypeName);
        if (result != null) return result;

        if ("AUDIO".equals(profileTypeName)) {
            result = newProfileTypeDescriptor(1, "Audio", "Audio");
        } else if ("VIDEO".equals(profileTypeName)) {
            result = newProfileTypeDescriptor(2, "Video", "Video");
        } else if ("TEXT".equals(profileTypeName)) {
            result = newProfileTypeDescriptor(3, "Text", "Timed Text");
        } else if ("MUXED".equals(profileTypeName)) {
            result = newProfileTypeDescriptor(4, "Muxed", "Muxed");
        } else if ("TRICKPLAY".equals(profileTypeName)) {
            result = newProfileTypeDescriptor(5, "Trick Play", "Trick Play");
        } else if ("MERCHSTILL".equals(profileTypeName)) {
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

    private VideoDimensionsDescriptor getVideoDimensions(final int dimensions) {
        Map<Integer, VideoDimensionsDescriptor> videoDimensionsMap = getMap(videoDimensionsMapRef);
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
        Map<String, Strings> stringsMap = getMap(stringsMapRef);
        Strings result = stringsMap.get(string);
        if (result == null) result = new Strings(string);

        stringsMap.put(string, result);
        return result;
    }

}