package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoTypeDescriptorListHollow extends HollowList<VideoTypeDescriptorHollow> {

    public VideoTypeDescriptorListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoTypeDescriptorHollow instantiateElement(int ordinal) {
        return (VideoTypeDescriptorHollow) api().getVideoTypeDescriptorHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeDescriptorListTypeAPI typeApi() {
        return (VideoTypeDescriptorListTypeAPI) delegate.getTypeAPI();
    }

}