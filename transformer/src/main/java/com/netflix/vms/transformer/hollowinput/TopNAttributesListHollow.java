package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class TopNAttributesListHollow extends HollowList<TopNAttributeHollow> {

    public TopNAttributesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TopNAttributeHollow instantiateElement(int ordinal) {
        return (TopNAttributeHollow) api().getTopNAttributeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TopNAttributesListTypeAPI typeApi() {
        return (TopNAttributesListTypeAPI) delegate.getTypeAPI();
    }

}