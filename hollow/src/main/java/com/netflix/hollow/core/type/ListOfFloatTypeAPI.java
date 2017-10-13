package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.FloatRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowListTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class ListOfFloatTypeAPI extends HollowListTypeAPI {

    private final FloatRetriever retriever;
    private final HollowListLookupDelegate delegateLookupImpl;

    public ListOfFloatTypeAPI(HollowAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.retriever = (FloatRetriever) getAPI();
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public FloatTypeAPI getElementAPI() {
        return retriever.getFloatTypeAPI();
   }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    FloatRetriever getFloatRetriever() {
        return retriever;
    }

}