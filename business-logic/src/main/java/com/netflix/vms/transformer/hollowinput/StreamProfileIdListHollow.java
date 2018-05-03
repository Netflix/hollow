package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class StreamProfileIdListHollow extends HollowList<StreamProfileIdHollow> {

    public StreamProfileIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public StreamProfileIdHollow instantiateElement(int ordinal) {
        return (StreamProfileIdHollow) api().getStreamProfileIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamProfileIdListTypeAPI typeApi() {
        return (StreamProfileIdListTypeAPI) delegate.getTypeAPI();
    }

}