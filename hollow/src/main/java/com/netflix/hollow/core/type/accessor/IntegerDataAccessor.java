package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HInteger;

public class IntegerDataAccessor extends AbstractHollowDataAccessor<HInteger> {

    public static final String TYPE = "HInteger";
    private HollowConsumerAPI.IntegerRetriever api;

    public IntegerDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (HollowConsumerAPI.IntegerRetriever)consumer.getAPI();
    }

    public IntegerDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.IntegerRetriever api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public IntegerDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.IntegerRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public IntegerDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.IntegerRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HInteger getRecord(int ordinal){
        return api.getHInteger(ordinal);
    }

}