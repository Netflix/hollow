package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

@SuppressWarnings("all")
public class PersonVideoRolesListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public PersonVideoRolesListTypeAPI(PersonVideoAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public PersonVideoRoleTypeAPI getElementAPI() {
        return getAPI().getPersonVideoRoleTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public PersonVideoAPI getAPI() {
        return (PersonVideoAPI)api;
    }

}