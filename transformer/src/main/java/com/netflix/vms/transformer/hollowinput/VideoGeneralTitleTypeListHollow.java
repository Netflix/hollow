package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoGeneralTitleTypeListHollow extends HollowList<VideoGeneralTitleTypeHollow> {

    public VideoGeneralTitleTypeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoGeneralTitleTypeHollow instantiateElement(int ordinal) {
        return (VideoGeneralTitleTypeHollow) api().getVideoGeneralTitleTypeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralTitleTypeListTypeAPI typeApi() {
        return (VideoGeneralTitleTypeListTypeAPI) delegate.getTypeAPI();
    }

}