package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowSetTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class SetOfStringTypeAPI extends HollowSetTypeAPI {

    private final HollowSetLookupDelegate delegateLookupImpl;

    SetOfStringTypeAPI(VMSHollowInputAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public StringTypeAPI getElementAPI() {
        return getAPI().getStringTypeAPI();
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}