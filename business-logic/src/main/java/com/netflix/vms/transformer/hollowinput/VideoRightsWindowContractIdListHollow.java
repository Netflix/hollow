package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoRightsWindowContractIdListHollow extends HollowList<VideoRightsContractIdHollow> {

    public VideoRightsWindowContractIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoRightsContractIdHollow instantiateElement(int ordinal) {
        return (VideoRightsContractIdHollow) api().getVideoRightsContractIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsWindowContractIdListTypeAPI typeApi() {
        return (VideoRightsWindowContractIdListTypeAPI) delegate.getTypeAPI();
    }

}