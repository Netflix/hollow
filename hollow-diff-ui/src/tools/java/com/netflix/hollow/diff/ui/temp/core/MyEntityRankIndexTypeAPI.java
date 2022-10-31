package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MyEntityRankIndexTypeAPI extends HollowObjectTypeAPI {

    private final MyEntityRankIndexDelegateLookupImpl delegateLookupImpl;

    public MyEntityRankIndexTypeAPI(MyNamespaceAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "index"
        });
        this.delegateLookupImpl = new MyEntityRankIndexDelegateLookupImpl(this);
    }

    public int getIndexOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MyEntityRankIndex", ordinal, "index");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MapOfMyEntityToIntegerTypeAPI getIndexTypeAPI() {
        return getAPI().getMapOfMyEntityToIntegerTypeAPI();
    }

    public MyEntityRankIndexDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public MyNamespaceAPI getAPI() {
        return (MyNamespaceAPI) api;
    }

}