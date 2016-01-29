package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow extends HollowList<ConsolidatedVideoRatingsRatingsCountryRatingsHollow> {

    public ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConsolidatedVideoRatingsRatingsCountryRatingsHollow instantiateElement(int ordinal) {
        return (ConsolidatedVideoRatingsRatingsCountryRatingsHollow) api().getConsolidatedVideoRatingsRatingsCountryRatingsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI typeApi() {
        return (ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI) delegate.getTypeAPI();
    }

}