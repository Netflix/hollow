package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.custom.HollowSetTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;

@SuppressWarnings("all")
public class SetOfMovieTypeAPI extends HollowSetTypeAPI {

    private final HollowSetLookupDelegate delegateLookupImpl;

    public SetOfMovieTypeAPI(AwardsAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public MovieTypeAPI getElementAPI() {
        return getAPI().getMovieTypeAPI();
    }

    public AwardsAPI getAPI() {
        return (AwardsAPI)api;
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}