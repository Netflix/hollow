package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.LongRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowSetTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class SetOfLongTypeAPI extends HollowSetTypeAPI {

    private final LongRetriever retriever;
    private final HollowSetLookupDelegate delegateLookupImpl;

    public SetOfLongTypeAPI(HollowAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (LongRetriever) getAPI();
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public LongTypeAPI getElementAPI() {
        return retriever.getLongTypeAPI();
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    LongRetriever getLongRetriever() {
        return retriever;
    }
}