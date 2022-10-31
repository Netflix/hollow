package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyEntityRankIndex;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;

@SuppressWarnings("all")
public class MyEntityRankIndexHollowFactory<T extends MyEntityRankIndex> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MyEntityRankIndex(((MyEntityRankIndexTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MyEntityRankIndex(new MyEntityRankIndexDelegateCachedImpl((MyEntityRankIndexTypeAPI)typeAPI, ordinal), ordinal);
    }

}