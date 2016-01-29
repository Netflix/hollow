package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoTypeTypeArrayOfMediaHollow extends HollowList<VideoTypeTypeMediaHollow> {

    public VideoTypeTypeArrayOfMediaHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoTypeTypeMediaHollow instantiateElement(int ordinal) {
        return (VideoTypeTypeMediaHollow) api().getVideoTypeTypeMediaHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeTypeArrayOfMediaTypeAPI typeApi() {
        return (VideoTypeTypeArrayOfMediaTypeAPI) delegate.getTypeAPI();
    }

}