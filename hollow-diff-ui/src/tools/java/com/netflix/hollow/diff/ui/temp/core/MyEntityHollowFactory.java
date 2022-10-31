package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyEntity;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;

@SuppressWarnings("all")
public class MyEntityHollowFactory<T extends MyEntity> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MyEntity(((MyEntityTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MyEntity(new MyEntityDelegateCachedImpl((MyEntityTypeAPI)typeAPI, ordinal), ordinal);
    }

}