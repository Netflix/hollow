package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class TrailersListHollow extends HollowList<IndividualTrailerHollow> {

    public TrailersListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public IndividualTrailerHollow instantiateElement(int ordinal) {
        return (IndividualTrailerHollow) api().getIndividualTrailerHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TrailersListTypeAPI typeApi() {
        return (TrailersListTypeAPI) delegate.getTypeAPI();
    }

}