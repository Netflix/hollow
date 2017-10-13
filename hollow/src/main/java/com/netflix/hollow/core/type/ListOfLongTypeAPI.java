package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.LongRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowListTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class ListOfLongTypeAPI extends HollowListTypeAPI {

    private final LongRetriever retriever;
    private final HollowListLookupDelegate delegateLookupImpl;

    public ListOfLongTypeAPI(HollowAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (LongRetriever) getAPI();
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public LongTypeAPI getElementAPI() {
        return retriever.getLongTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    LongRetriever getLongRetriever() {
        return retriever;
    }

}