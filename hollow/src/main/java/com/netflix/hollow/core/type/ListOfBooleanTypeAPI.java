package com.netflix.hollow.core.type;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.consumer.HollowConsumerAPI.BooleanRetriever;
import com.netflix.hollow.api.custom.HollowListTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class ListOfBooleanTypeAPI extends HollowListTypeAPI {

    private final BooleanRetriever retriever;
    private final HollowListLookupDelegate delegateLookupImpl;

    public ListOfBooleanTypeAPI(HollowAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (BooleanRetriever) getAPI();
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public BooleanTypeAPI getElementAPI() {
        return retriever.getBooleanTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    BooleanRetriever getBooleanRetriever() {
        return retriever;
    }
}
