package com.netflix.sunjeetsonboardingroot.generated.topn;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowSetCachedDelegate;

@SuppressWarnings("all")
public class SetOfTopNAttributeHollowFactory<T extends SetOfTopNAttribute> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new SetOfTopNAttribute(((SetOfTopNAttributeTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new SetOfTopNAttribute(new HollowSetCachedDelegate((SetOfTopNAttributeTypeAPI)typeAPI, ordinal), ordinal);
    }

}