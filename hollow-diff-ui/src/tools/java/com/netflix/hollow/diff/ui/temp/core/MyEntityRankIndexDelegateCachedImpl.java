package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MyEntityRankIndexDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MyEntityRankIndexDelegate {

    private final int indexOrdinal;
    private MyEntityRankIndexTypeAPI typeAPI;

    public MyEntityRankIndexDelegateCachedImpl(MyEntityRankIndexTypeAPI typeAPI, int ordinal) {
        this.indexOrdinal = typeAPI.getIndexOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getIndexOrdinal(int ordinal) {
        return indexOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MyEntityRankIndexTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MyEntityRankIndexTypeAPI) typeAPI;
    }

}