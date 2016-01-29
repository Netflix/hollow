package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class Stories_SynopsesArrayOfHooksHollow extends HollowList<Stories_SynopsesHooksHollow> {

    public Stories_SynopsesArrayOfHooksHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stories_SynopsesHooksHollow instantiateElement(int ordinal) {
        return (Stories_SynopsesHooksHollow) api().getStories_SynopsesHooksHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public Stories_SynopsesArrayOfHooksTypeAPI typeApi() {
        return (Stories_SynopsesArrayOfHooksTypeAPI) delegate.getTypeAPI();
    }

}