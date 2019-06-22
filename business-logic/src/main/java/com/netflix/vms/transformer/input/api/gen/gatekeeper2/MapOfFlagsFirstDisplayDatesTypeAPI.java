package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;

@SuppressWarnings("all")
public class MapOfFlagsFirstDisplayDatesTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public MapOfFlagsFirstDisplayDatesTypeAPI(Gk2StatusAPI api, HollowMapTypeDataAccess dataAccess) {
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

    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI)api;
    }

}