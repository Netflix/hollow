package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsWindowsSetHollow extends HollowSet<VideoRightsWindowHollow> {

    public VideoRightsWindowsSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsWindowHollow instantiateElement(int ordinal) {
        return (VideoRightsWindowHollow) api().getVideoRightsWindowHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsWindowsSetTypeAPI typeApi() {
        return (VideoRightsWindowsSetTypeAPI) delegate.getTypeAPI();
    }

}