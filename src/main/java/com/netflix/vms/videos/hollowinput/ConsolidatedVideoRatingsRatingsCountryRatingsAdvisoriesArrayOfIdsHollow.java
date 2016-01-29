package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow extends HollowList<ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow> {

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow instantiateElement(int ordinal) {
        return (ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow) api().getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesIdsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI typeApi() {
        return (ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsTypeAPI) delegate.getTypeAPI();
    }

}