package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.vms.transformer.contract.ContractAssetType;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamAssetTypeHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TextStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashMap;
import java.util.Map;

public class StreamContractAssetTypeDeterminer {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex streamProfileIdx;

    private final Map<Long, ContractAssetType> cachedAssetTypes;

    public StreamContractAssetTypeDeterminer(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);
        this.cachedAssetTypes = new HashMap<>();
    }

    public ContractAssetType getAssetType(PackageStreamHollow stream) {
        Long downloadableId = stream._getDownloadableIdBoxed();
        ContractAssetType cachedAssetType = cachedAssetTypes.get(downloadableId);
        if(cachedAssetType != null)
            return cachedAssetType;


        int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(stream._getStreamProfileId());
        if(streamProfileOrdinal != -1) {
            StreamProfilesHollow streamProfile = api.getStreamProfilesHollow(streamProfileOrdinal);

            String profileType = streamProfile._getProfileType()._getValue();
            if("MERCHSTILL".equals(profileType))
                return null;

            if(streamProfile._getProfileType()._isValueEqual("MUXED")){
                return cache(downloadableId, ContractAssetType.AUDIO);
            }else if(streamProfile._getProfileType()._isValueEqual("VIDEO")) {
                TextStreamInfoHollow textInfo = stream._getNonImageInfo()._getTextInfo();
                if(textInfo != null && textInfo._getTextLanguageCode() != null) {
                    return cache(downloadableId, ContractAssetType.SUBTITLES);
                }
                return null;
            } else if(streamProfile._getProfileType()._isValueEqual("AUDIO")) {
                StreamAssetTypeHollow assetType = stream._getAssetType();
                if(assetType != null) {
                    StringHollow assetTypeStringHollow = assetType._getAssetType();
                    if(assetTypeStringHollow != null && assetTypeStringHollow._isValueEqual("assistive"))
                        return cache(downloadableId, ContractAssetType.DESCRIPTIVE_AUDIO);
                }
                return cache(downloadableId, ContractAssetType.AUDIO);
            } else if(streamProfile._getProfileType()._isValueEqual("TEXT")) {
                TextStreamInfoHollow textInfo = stream._getNonImageInfo()._getTextInfo();
                if(textInfo != null) {
                    if(!textInfo._getTimedTextType()._isValueEqual("FN")) {
                        /// subtitles ("SUBS") and closed captioning ("CC") are treated the same -- not Forced Narrative
                        return cache(downloadableId, ContractAssetType.SUBTITLES);
                    }
                }
            }
        }

        return null;
    }

    private ContractAssetType cache(Long downloadableId, ContractAssetType assetType) {
        cachedAssetTypes.put(downloadableId, assetType);
        return assetType;
    }

    public void clearCache() {
        cachedAssetTypes.clear();
    }

}
