package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.IntegerRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowSetTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class SetOfIntegerTypeAPI extends HollowSetTypeAPI {

    private final IntegerRetriever retriever;
    private final HollowSetLookupDelegate delegateLookupImpl;

    public SetOfIntegerTypeAPI(HollowAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (IntegerRetriever) getAPI();
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public IntegerTypeAPI getElementAPI() {
        return retriever.getIntegerTypeAPI();
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    IntegerRetriever getIntegerRetriever() {
        return retriever;
    }
}