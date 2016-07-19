package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

@SuppressWarnings("all")
public class ListOfRightsWindowContractHollowFactory<T extends ListOfRightsWindowContractHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ListOfRightsWindowContractHollow(((ListOfRightsWindowContractTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ListOfRightsWindowContractHollow(new HollowListCachedDelegate((ListOfRightsWindowContractTypeAPI)typeAPI, ordinal), ordinal);
    }

}