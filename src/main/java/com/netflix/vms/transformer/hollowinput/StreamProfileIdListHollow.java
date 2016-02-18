package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class StreamProfileIdListHollow extends HollowList<StreamProfileIdHollow> {

    public StreamProfileIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StreamProfileIdHollow instantiateElement(int ordinal) {
        return (StreamProfileIdHollow) api().getStreamProfileIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamProfileIdListTypeAPI typeApi() {
        return (StreamProfileIdListTypeAPI) delegate.getTypeAPI();
    }

}