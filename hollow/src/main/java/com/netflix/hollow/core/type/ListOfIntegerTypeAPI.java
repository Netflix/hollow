package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.IntegerRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowListTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class ListOfIntegerTypeAPI extends HollowListTypeAPI {

    private final IntegerRetriever retriever;
    private final HollowListLookupDelegate delegateLookupImpl;

    public ListOfIntegerTypeAPI(HollowAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (IntegerRetriever) getAPI();
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public IntegerTypeAPI getElementAPI() {
        return retriever.getIntegerTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    IntegerRetriever getIntegerRetriever() {
        return retriever;
    }

}