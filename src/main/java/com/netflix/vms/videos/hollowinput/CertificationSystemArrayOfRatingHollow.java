package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class CertificationSystemArrayOfRatingHollow extends HollowList<CertificationSystemRatingHollow> {

    public CertificationSystemArrayOfRatingHollow(HollowListDelegate delegate, int ordinal) {
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

    public CertificationSystemArrayOfRatingTypeAPI typeApi() {
        return (CertificationSystemArrayOfRatingTypeAPI) delegate.getTypeAPI();
    }

}