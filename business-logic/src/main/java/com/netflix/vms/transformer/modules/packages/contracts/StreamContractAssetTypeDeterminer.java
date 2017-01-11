package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.contract.ContractAssetType;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamAssetTypeHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TextStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

public class StreamContractAssetTypeDeterminer {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex streamProfileIdx;

    public StreamContractAssetTypeDeterminer(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);
    }

    public ContractAssetType getAssetType(PackageStreamHollow stream) {
        int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(stream._getStreamProfileId());
        if(streamProfileOrdinal != -1) {
            StreamProfilesHollow streamProfile = api.getStreamProfilesHollow(streamProfileOrdinal);

            String profileType = streamProfile._getProfileType()._getValue();
            if("MERCHSTILL".equals(profileType))
                return null;

            if(profileType.equals("MUXED")){
                return ContractAssetType.AUDIO;
            } else if(profileType.equals("VIDEO")) {
                TextStreamInfoHollow textInfo = stream._getNonImageInfo()._getTextInfo();
                if(textInfo != null && textInfo._getTextLanguageCode() != null) {
                    return ContractAssetType.SUBTITLES;
                }
                return null;
            } else if(profileType.equals("AUDIO")) {
                StreamAssetTypeHollow assetType = stream._getAssetType();
                if(assetType != null) {
                    StringHollow assetTypeStringHollow = assetType._getAssetType();
                    if(assetTypeStringHollow != null && assetTypeStringHollow._isValueEqual("assistive"))
                        return ContractAssetType.DESCRIPTIVE_AUDIO;
                }
                return ContractAssetType.AUDIO;
            } else if(profileType.equals("TEXT")) {
                TextStreamInfoHollow textInfo = stream._getNonImageInfo()._getTextInfo();
                if(textInfo != null) {
                    if(!textInfo._getTimedTextType()._isValueEqual("FN")) {
                        /// subtitles ("SUBS") and closed captioning ("CC") are treated the same -- not Forced Narrative
                        return ContractAssetType.SUBTITLES;
                    }
                }
            }
        }

        return null;
    }

}
