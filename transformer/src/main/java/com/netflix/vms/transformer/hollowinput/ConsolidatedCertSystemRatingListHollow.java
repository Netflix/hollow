package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ConsolidatedCertSystemRatingListHollow extends HollowList<ConsolidatedCertSystemRatingHollow> {

    public ConsolidatedCertSystemRatingListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConsolidatedCertSystemRatingHollow instantiateElement(int ordinal) {
        return (ConsolidatedCertSystemRatingHollow) api().getConsolidatedCertSystemRatingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertSystemRatingListTypeAPI typeApi() {
        return (ConsolidatedCertSystemRatingListTypeAPI) delegate.getTypeAPI();
    }

}