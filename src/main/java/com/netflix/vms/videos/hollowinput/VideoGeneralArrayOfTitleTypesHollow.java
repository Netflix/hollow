package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoGeneralArrayOfTitleTypesHollow extends HollowList<VideoGeneralTitleTypesHollow> {

    public VideoGeneralArrayOfTitleTypesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoGeneralTitleTypesHollow instantiateElement(int ordinal) {
        return (VideoGeneralTitleTypesHollow) api().getVideoGeneralTitleTypesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralArrayOfTitleTypesTypeAPI typeApi() {
        return (VideoGeneralArrayOfTitleTypesTypeAPI) delegate.getTypeAPI();
    }

}