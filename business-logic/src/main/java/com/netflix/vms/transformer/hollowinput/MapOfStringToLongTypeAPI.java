package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;

@SuppressWarnings("all")
public class MapOfStringToLongTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public MapOfStringToLongTypeAPI(VMSHollowInputAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public StringTypeAPI getKeyAPI() {
        return getAPI().getStringTypeAPI();
    }

    public LongTypeAPI getValueAPI() {
        return getAPI().getLongTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}