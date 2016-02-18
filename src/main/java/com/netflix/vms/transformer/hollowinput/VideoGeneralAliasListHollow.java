package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoGeneralAliasListHollow extends HollowList<VideoGeneralAliasHollow> {

    public VideoGeneralAliasListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoGeneralAliasHollow instantiateElement(int ordinal) {
        return (VideoGeneralAliasHollow) api().getVideoGeneralAliasHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralAliasListTypeAPI typeApi() {
        return (VideoGeneralAliasListTypeAPI) delegate.getTypeAPI();
    }

}