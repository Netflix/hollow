package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowListTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowListLookupDelegate;

public class PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI(VMSHollowVideoInputAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public PersonArtworkLocalesTerritoryCodesTypeAPI getElementAPI() {
        return getAPI().getPersonArtworkLocalesTerritoryCodesTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

}