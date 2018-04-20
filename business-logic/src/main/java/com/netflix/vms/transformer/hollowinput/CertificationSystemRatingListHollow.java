package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class CertificationSystemRatingListHollow extends HollowList<CertificationSystemRatingHollow> {

    public CertificationSystemRatingListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public CertificationSystemRatingHollow instantiateElement(int ordinal) {
        return (CertificationSystemRatingHollow) api().getCertificationSystemRatingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationSystemRatingListTypeAPI typeApi() {
        return (CertificationSystemRatingListTypeAPI) delegate.getTypeAPI();
    }

}