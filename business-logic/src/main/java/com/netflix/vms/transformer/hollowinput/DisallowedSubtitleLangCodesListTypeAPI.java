package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowListTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class DisallowedSubtitleLangCodesListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    DisallowedSubtitleLangCodesListTypeAPI(VMSHollowInputAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public DisallowedSubtitleLangCodeTypeAPI getElementAPI() {
        return getAPI().getDisallowedSubtitleLangCodeTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}