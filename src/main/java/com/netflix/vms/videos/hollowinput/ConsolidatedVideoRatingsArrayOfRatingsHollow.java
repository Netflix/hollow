package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ConsolidatedVideoRatingsArrayOfRatingsHollow extends HollowList<ConsolidatedVideoRatingsRatingsHollow> {

    public ConsolidatedVideoRatingsArrayOfRatingsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConsolidatedVideoRatingsRatingsHollow instantiateElement(int ordinal) {
        return (ConsolidatedVideoRatingsRatingsHollow) api().getConsolidatedVideoRatingsRatingsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsArrayOfRatingsTypeAPI typeApi() {
        return (ConsolidatedVideoRatingsArrayOfRatingsTypeAPI) delegate.getTypeAPI();
    }

}