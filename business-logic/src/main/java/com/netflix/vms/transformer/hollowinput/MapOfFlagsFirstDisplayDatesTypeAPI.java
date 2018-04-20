package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;

@SuppressWarnings("all")
public class MapOfFlagsFirstDisplayDatesTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public MapOfFlagsFirstDisplayDatesTypeAPI(VMSHollowInputAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public MapKeyTypeAPI getKeyAPI() {
        return getAPI().getMapKeyTypeAPI();
    }

    public DateTypeAPI getValueAPI() {
        return getAPI().getDateTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}