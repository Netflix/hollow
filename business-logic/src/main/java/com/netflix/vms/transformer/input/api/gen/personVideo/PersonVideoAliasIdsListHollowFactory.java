package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class PersonVideoAliasIdsListHollowFactory<T extends PersonVideoAliasIdsList> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new PersonVideoAliasIdsList(((PersonVideoAliasIdsListTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new PersonVideoAliasIdsList(new HollowListCachedDelegate((PersonVideoAliasIdsListTypeAPI)typeAPI, ordinal), ordinal);
    }

}