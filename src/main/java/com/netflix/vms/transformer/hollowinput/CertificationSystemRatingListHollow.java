package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class CertificationSystemRatingListHollow extends HollowList<CertificationSystemRatingHollow> {

    public CertificationSystemRatingListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CertificationSystemRatingHollow instantiateElement(int ordinal) {
        return (CertificationSystemRatingHollow) api().getCertificationSystemRatingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationSystemRatingListTypeAPI typeApi() {
        return (CertificationSystemRatingListTypeAPI) delegate.getTypeAPI();
    }

}