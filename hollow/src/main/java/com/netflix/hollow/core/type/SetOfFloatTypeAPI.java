package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.FloatRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowSetTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class SetOfFloatTypeAPI extends HollowSetTypeAPI {

    private final FloatRetriever retriever;
    private final HollowSetLookupDelegate delegateLookupImpl;

    public SetOfFloatTypeAPI(HollowAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (FloatRetriever) getAPI();
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public FloatTypeAPI getElementAPI() {
        return retriever.getFloatTypeAPI();
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    FloatRetriever getFloatRetriever() {
        return retriever;
    }
}