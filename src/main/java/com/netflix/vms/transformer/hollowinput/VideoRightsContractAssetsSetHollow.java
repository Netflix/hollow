package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsContractAssetsSetHollow extends HollowSet<VideoRightsContractAssetHollow> {

    public VideoRightsContractAssetsSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsContractAssetHollow instantiateElement(int ordinal) {
        return (VideoRightsContractAssetHollow) api().getVideoRightsContractAssetHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsContractAssetsSetTypeAPI typeApi() {
        return (VideoRightsContractAssetsSetTypeAPI) delegate.getTypeAPI();
    }

}