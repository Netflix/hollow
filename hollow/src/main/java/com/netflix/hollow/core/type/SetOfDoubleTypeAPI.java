package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.DoubleRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowSetTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class SetOfDoubleTypeAPI extends HollowSetTypeAPI {

    private final DoubleRetriever retriever;
    private final HollowSetLookupDelegate delegateLookupImpl;

    public SetOfDoubleTypeAPI(HollowAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (DoubleRetriever) getAPI();
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public DoubleTypeAPI getElementAPI() {
        return retriever.getDoubleTypeAPI();
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    DoubleRetriever getDoubleRetriever() {
        return retriever;
    }
}