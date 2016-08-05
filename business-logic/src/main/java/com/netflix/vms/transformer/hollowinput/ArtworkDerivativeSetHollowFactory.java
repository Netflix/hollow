package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowSetCachedDelegate;

@SuppressWarnings("all")
public class ArtworkDerivativeSetHollowFactory<T extends ArtworkDerivativeSetHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ArtworkDerivativeSetHollow(((ArtworkDerivativeSetTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ArtworkDerivativeSetHollow(new HollowSetCachedDelegate((ArtworkDerivativeSetTypeAPI)typeAPI, ordinal), ordinal);
    }

}