package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

@SuppressWarnings("all")
public class ShowMemberTypeListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public ShowMemberTypeListTypeAPI(ShowCountryLabelAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public ShowMemberTypeTypeAPI getElementAPI() {
        return getAPI().getShowMemberTypeTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public ShowCountryLabelAPI getAPI() {
        return (ShowCountryLabelAPI)api;
    }

}