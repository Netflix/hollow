package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class TimecodedMomentAnnotationHollowFactory<T extends TimecodedMomentAnnotationHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new TimecodedMomentAnnotationHollow(((TimecodedMomentAnnotationTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new TimecodedMomentAnnotationHollow(new TimecodedMomentAnnotationDelegateCachedImpl((TimecodedMomentAnnotationTypeAPI)typeAPI, ordinal), ordinal);
    }

}