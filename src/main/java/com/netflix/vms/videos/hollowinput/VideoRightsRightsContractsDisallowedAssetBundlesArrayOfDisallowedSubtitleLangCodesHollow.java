package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow extends HollowList<VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow> {

    public VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow instantiateElement(int ordinal) {
        return (VideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow) api().getVideoRightsRightsContractsDisallowedAssetBundlesDisallowedSubtitleLangCodesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI typeApi() {
        return (VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI) delegate.getTypeAPI();
    }

}