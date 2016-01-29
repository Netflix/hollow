package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class ConsolidatedCertificationSystemsRatingRatingCodesHollowFactory<T extends ConsolidatedCertificationSystemsRatingRatingCodesHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertificationSystemsRatingRatingCodesHollow(((ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertificationSystemsRatingRatingCodesHollow(new ConsolidatedCertificationSystemsRatingRatingCodesDelegateCachedImpl((ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI)typeAPI, ordinal), ordinal);
    }

}