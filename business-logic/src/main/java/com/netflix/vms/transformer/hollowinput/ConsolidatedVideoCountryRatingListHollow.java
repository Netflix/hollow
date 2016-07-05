package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ConsolidatedVideoCountryRatingListHollow extends HollowList<ConsolidatedVideoCountryRatingHollow> {

    public ConsolidatedVideoCountryRatingListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConsolidatedVideoCountryRatingHollow instantiateElement(int ordinal) {
        return (ConsolidatedVideoCountryRatingHollow) api().getConsolidatedVideoCountryRatingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoCountryRatingListTypeAPI typeApi() {
        return (ConsolidatedVideoCountryRatingListTypeAPI) delegate.getTypeAPI();
    }

}