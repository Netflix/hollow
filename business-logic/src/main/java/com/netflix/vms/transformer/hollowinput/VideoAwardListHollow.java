package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoAwardListHollow extends HollowList<VideoAwardMappingHollow> {

    public VideoAwardListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoAwardMappingHollow instantiateElement(int ordinal) {
        return (VideoAwardMappingHollow) api().getVideoAwardMappingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardListTypeAPI typeApi() {
        return (VideoAwardListTypeAPI) delegate.getTypeAPI();
    }

}