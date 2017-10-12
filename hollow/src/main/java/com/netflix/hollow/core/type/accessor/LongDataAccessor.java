package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HLong;

public class LongDataAccessor extends AbstractHollowDataAccessor<HLong> {

    public static final String TYPE = "HLong";
    private HollowConsumerAPI.LongRetriever api;

    public LongDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (HollowConsumerAPI.LongRetriever)consumer.getAPI();
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.LongRetriever api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.LongRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.LongRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HLong getRecord(int ordinal){
        return api.getHLong(ordinal);
    }

}