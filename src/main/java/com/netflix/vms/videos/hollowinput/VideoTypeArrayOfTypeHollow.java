package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoTypeArrayOfTypeHollow extends HollowList<VideoTypeTypeHollow> {

    public VideoTypeArrayOfTypeHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoTypeTypeHollow instantiateElement(int ordinal) {
        return (VideoTypeTypeHollow) api().getVideoTypeTypeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeArrayOfTypeTypeAPI typeApi() {
        return (VideoTypeArrayOfTypeTypeAPI) delegate.getTypeAPI();
    }

}