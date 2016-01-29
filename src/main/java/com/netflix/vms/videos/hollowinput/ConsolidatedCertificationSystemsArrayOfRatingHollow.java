package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ConsolidatedCertificationSystemsArrayOfRatingHollow extends HollowList<ConsolidatedCertificationSystemsRatingHollow> {

    public ConsolidatedCertificationSystemsArrayOfRatingHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConsolidatedCertificationSystemsRatingHollow instantiateElement(int ordinal) {
        return (ConsolidatedCertificationSystemsRatingHollow) api().getConsolidatedCertificationSystemsRatingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsArrayOfRatingTypeAPI typeApi() {
        return (ConsolidatedCertificationSystemsArrayOfRatingTypeAPI) delegate.getTypeAPI();
    }

}