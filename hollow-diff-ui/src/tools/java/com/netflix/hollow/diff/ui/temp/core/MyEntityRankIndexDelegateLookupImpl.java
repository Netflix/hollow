package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MyEntityRankIndexDelegateLookupImpl extends HollowObjectAbstractDelegate implements MyEntityRankIndexDelegate {

    private final MyEntityRankIndexTypeAPI typeAPI;

    public MyEntityRankIndexDelegateLookupImpl(MyEntityRankIndexTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getIndexOrdinal(int ordinal) {
        return typeAPI.getIndexOrdinal(ordinal);
    }

    public MyEntityRankIndexTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}