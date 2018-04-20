package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingListHollow extends HollowList<ConsolidatedCertSystemRatingHollow> {

    public ConsolidatedCertSystemRatingListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ConsolidatedCertSystemRatingHollow instantiateElement(int ordinal) {
        return (ConsolidatedCertSystemRatingHollow) api().getConsolidatedCertSystemRatingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertSystemRatingListTypeAPI typeApi() {
        return (ConsolidatedCertSystemRatingListTypeAPI) delegate.getTypeAPI();
    }

}