package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoDateWindowListHollow extends HollowList<VideoDateWindowHollow> {

    public VideoDateWindowListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoDateWindowHollow instantiateElement(int ordinal) {
        return (VideoDateWindowHollow) api().getVideoDateWindowHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateWindowListTypeAPI typeApi() {
        return (VideoDateWindowListTypeAPI) delegate.getTypeAPI();
    }

}