package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamAssetTypeHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TextStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.HashMap;
import java.util.Map;

public class StreamContractAssetTypeDeterminer {

    public static final String PRIMARYVIDEO_AUDIOMUXED = "Primary Video + Audio Muxed";
    public static final String SUBTITLES = "Subtitles";
    public static final String CLOSEDCAPTIONING = "Closed Captioning";
    public static final String DESCRIPTIVE_AUDIO = "Descriptive Audio";
    public static final String SECONDARY_AUDIO = "Secondary Audio Source";
    /// public static final String NOLANG = "nolanguage";   /// just return null instead

    private final VMSHollowVideoInputAPI api;
    private final HollowPrimaryKeyIndex streamProfileIdx;

    private final Map<Long, String> cachedAssetTypes;

    public StreamContractAssetTypeDeterminer(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);
        this.cachedAssetTypes = new HashMap<Long, String>();
    }

    public String getAssetType(PackageStreamHollow stream) {
        Long downloadableId = stream._getDownloadableIdBoxed();
        String cachedAssetType = cachedAssetTypes.get(downloadableId);
        if(cachedAssetType != null)
            return cachedAssetType;


        int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(stream._getStreamProfileId());
        StreamProfilesHollow streamProfile = api.getStreamProfilesHollow(streamProfileOrdinal);

        String profileType = streamProfile._getProfileType()._getValue();
        if("MERCHSTILL".equals(profileType))
            return null;

        if(streamProfile._getProfileType()._isValueEqual("MUXED")){
            return cache(downloadableId, PRIMARYVIDEO_AUDIOMUXED);
        }else if(streamProfile._getProfileType()._isValueEqual("VIDEO")) {
            TextStreamInfoHollow textInfo = stream._getNonImageInfo()._getTextInfo();
            if(textInfo != null && textInfo._getTextLanguageCode() != null) {
                return cache(downloadableId, SUBTITLES);
            }
            return null;
        } else if(streamProfile._getProfileType()._isValueEqual("AUDIO")) {
            StreamAssetTypeHollow assetType = stream._getAssetType();
            if(assetType != null) {
                StringHollow assetTypeStringHollow = assetType._getAssetType();
                if(assetTypeStringHollow != null && assetTypeStringHollow._isValueEqual("assistive"))
                    return cache(downloadableId, DESCRIPTIVE_AUDIO);
            }
            return cache(downloadableId, PRIMARYVIDEO_AUDIOMUXED);
        } else if(streamProfile._getProfileType()._isValueEqual("TEXT")) {
            TextStreamInfoHollow textInfo = stream._getNonImageInfo()._getTextInfo();
            if(textInfo != null) {
                if(textInfo._getTimedTextType()._isValueEqual("SUBS")) {
                    return cache(downloadableId, SUBTITLES);
                } else if(textInfo._getTimedTextType()._isValueEqual("CC")) {
                    //return cache(downloadableId, CLOSEDCAPTIONING);
                    return cache(downloadableId, SUBTITLES); /// subtitles and closed captioning are treated the same.
                }
            }
        }

        return null;
    }

    private String cache(Long downloadableId, String assetType) {
        cachedAssetTypes.put(downloadableId, assetType);
        return assetType;
    }

    public void clearCache() {
        cachedAssetTypes.clear();
    }

}
