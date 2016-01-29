package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoDateArrayOfWindowHollow extends HollowList<VideoDateWindowHollow> {

    public VideoDateArrayOfWindowHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoDateWindowHollow instantiateElement(int ordinal) {
        return (VideoDateWindowHollow) api().getVideoDateWindowHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateArrayOfWindowTypeAPI typeApi() {
        return (VideoDateArrayOfWindowTypeAPI) delegate.getTypeAPI();
    }

}