package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;

@SuppressWarnings("all")
public class ListOfRightsWindowHollowFactory<T extends ListOfRightsWindowHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ListOfRightsWindowHollow(((ListOfRightsWindowTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ListOfRightsWindowHollow(new HollowListCachedDelegate((ListOfRightsWindowTypeAPI)typeAPI, ordinal), ordinal);
    }

}