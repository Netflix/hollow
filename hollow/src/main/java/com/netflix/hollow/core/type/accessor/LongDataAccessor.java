package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HLong;

public class LongDataAccessor extends AbstractHollowDataAccessor<Long> {

    public static final String TYPE = "Long";
    private HollowConsumerAPI.LongRetriever api;

    public LongDataAccessor(HollowConsumer consumer) {
        this(consumer.getStateEngine(), (HollowConsumerAPI.LongRetriever)consumer.getAPI());
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.LongRetriever api) {
        this(rStateEngine, api, "value");
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.LongRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.LongRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Long getRecord(int ordinal){
        HLong val = api.getHLong(ordinal);
        return val==null ? null : val.getValueBoxed();
    }
}