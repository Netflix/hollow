package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingHollowFactory<T extends ConsolidatedCertSystemRatingHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertSystemRatingHollow(((ConsolidatedCertSystemRatingTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertSystemRatingHollow(new ConsolidatedCertSystemRatingDelegateCachedImpl((ConsolidatedCertSystemRatingTypeAPI)typeAPI, ordinal), ordinal);
    }

}