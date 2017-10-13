package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.StringRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowSetTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class SetOfStringTypeAPI extends HollowSetTypeAPI {

    private final StringRetriever retriever;
    private final HollowSetLookupDelegate delegateLookupImpl;

    public SetOfStringTypeAPI(HollowAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (StringRetriever) getAPI();
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public StringTypeAPI getElementAPI() {
        return retriever.getStringTypeAPI();
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    StringRetriever getStringRetriever() {
        return retriever;
    }
}