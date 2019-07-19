package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowListTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class ShowMemberTypeListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public ShowMemberTypeListTypeAPI(OscarAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public ShowMemberTypeTypeAPI getElementAPI() {
        return getAPI().getShowMemberTypeTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public OscarAPI getAPI() {
        return (OscarAPI)api;
    }

}