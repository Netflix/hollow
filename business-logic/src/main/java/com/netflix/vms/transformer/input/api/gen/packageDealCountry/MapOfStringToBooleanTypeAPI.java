package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;

@SuppressWarnings("all")
public class MapOfStringToBooleanTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public MapOfStringToBooleanTypeAPI(PackageDealCountryAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public StringTypeAPI getKeyAPI() {
        return getAPI().getStringTypeAPI();
    }

    public BooleanTypeAPI getValueAPI() {
        return getAPI().getBooleanTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public PackageDealCountryAPI getAPI() {
        return (PackageDealCountryAPI)api;
    }

}