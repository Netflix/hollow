package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow extends HollowList<ConsolidatedVideoRatingsRatingsCountryListHollow> {

    public ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConsolidatedVideoRatingsRatingsCountryListHollow instantiateElement(int ordinal) {
        return (ConsolidatedVideoRatingsRatingsCountryListHollow) api().getConsolidatedVideoRatingsRatingsCountryListHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI typeApi() {
        return (ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI) delegate.getTypeAPI();
    }

}