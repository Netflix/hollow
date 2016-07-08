package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoRightsContractSetHollow extends HollowSet<VideoRightsContractHollow> {

    public VideoRightsContractSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoRightsContractHollow instantiateElement(int ordinal) {
        return (VideoRightsContractHollow) api().getVideoRightsContractHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsContractSetTypeAPI typeApi() {
        return (VideoRightsContractSetTypeAPI) delegate.getTypeAPI();
    }

}