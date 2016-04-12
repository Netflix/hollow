package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoPersonAliasListHollow extends HollowList<VideoPersonAliasHollow> {

    public VideoPersonAliasListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoPersonAliasHollow instantiateElement(int ordinal) {
        return (VideoPersonAliasHollow) api().getVideoPersonAliasHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoPersonAliasListTypeAPI typeApi() {
        return (VideoPersonAliasListTypeAPI) delegate.getTypeAPI();
    }

}