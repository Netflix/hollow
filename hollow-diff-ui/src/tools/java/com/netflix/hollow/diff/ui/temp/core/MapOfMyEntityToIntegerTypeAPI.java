package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;

@SuppressWarnings("all")
public class MapOfMyEntityToIntegerTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public MapOfMyEntityToIntegerTypeAPI(MyNamespaceAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public MyEntityTypeAPI getKeyAPI() {
        return getAPI().getMyEntityTypeAPI();
    }

    public IntegerTypeAPI getValueAPI() {
        return getAPI().getIntegerTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public MyNamespaceAPI getAPI() {
        return (MyNamespaceAPI)api;
    }

}