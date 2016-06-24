package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class StoriesSynopsesHookListHollow extends HollowList<StoriesSynopsesHookHollow> {

    public StoriesSynopsesHookListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public StoriesSynopsesHookHollow instantiateElement(int ordinal) {
        return (StoriesSynopsesHookHollow) api().getStoriesSynopsesHookHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StoriesSynopsesHookListTypeAPI typeApi() {
        return (StoriesSynopsesHookListTypeAPI) delegate.getTypeAPI();
    }

}