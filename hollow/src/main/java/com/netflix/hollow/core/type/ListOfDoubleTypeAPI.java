package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.DoubleRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowListTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class ListOfDoubleTypeAPI extends HollowListTypeAPI {

    private final DoubleRetriever retriever;
    private final HollowListLookupDelegate delegateLookupImpl;

    public ListOfDoubleTypeAPI(HollowAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (DoubleRetriever) getAPI();
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public DoubleTypeAPI getElementAPI() {
        return retriever.getDoubleTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    DoubleRetriever getDoubleRetriever() {
        return retriever;
    }

}