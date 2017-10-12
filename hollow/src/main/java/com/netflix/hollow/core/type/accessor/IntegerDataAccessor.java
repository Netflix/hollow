package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HInteger;

public class IntegerDataAccessor extends AbstractHollowDataAccessor<Integer> {

    public static final String TYPE = "Integer";
    private HollowConsumerAPI.IntegerRetriever api;

    public IntegerDataAccessor(HollowConsumer consumer) {
        this(consumer.getStateEngine(), (HollowConsumerAPI.IntegerRetriever)consumer.getAPI());
    }

    public IntegerDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.IntegerRetriever api) {
        this(rStateEngine, api, "value");
    }

    public IntegerDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.IntegerRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public IntegerDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.IntegerRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Integer getRecord(int ordinal){
        HInteger val = api.getHInteger(ordinal);
        return val == null ? null : val.getValueBoxed();
    }
}