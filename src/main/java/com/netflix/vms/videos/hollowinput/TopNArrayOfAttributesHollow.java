package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class TopNArrayOfAttributesHollow extends HollowList<TopNAttributesHollow> {

    public TopNArrayOfAttributesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TopNAttributesHollow instantiateElement(int ordinal) {
        return (TopNAttributesHollow) api().getTopNAttributesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TopNArrayOfAttributesTypeAPI typeApi() {
        return (TopNArrayOfAttributesTypeAPI) delegate.getTypeAPI();
    }

}