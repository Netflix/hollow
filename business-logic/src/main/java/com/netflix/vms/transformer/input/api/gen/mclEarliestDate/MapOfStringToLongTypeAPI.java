package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;

@SuppressWarnings("all")
public class MapOfStringToLongTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public MapOfStringToLongTypeAPI(MclEarliestDateAPI api, HollowMapTypeDataAccess dataAccess) {
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

    public MclEarliestDateAPI getAPI() {
        return (MclEarliestDateAPI)api;
    }

}