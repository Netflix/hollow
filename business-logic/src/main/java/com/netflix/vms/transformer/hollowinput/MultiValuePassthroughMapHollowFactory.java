package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowMapCachedDelegate;

@SuppressWarnings("all")
public class MultiValuePassthroughMapHollowFactory<T extends MultiValuePassthroughMapHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MultiValuePassthroughMapHollow(((MultiValuePassthroughMapTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MultiValuePassthroughMapHollow(new HollowMapCachedDelegate((MultiValuePassthroughMapTypeAPI)typeAPI, ordinal), ordinal);
    }

}