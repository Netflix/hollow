package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HDouble;

public class DoubleDataAccessor extends AbstractHollowDataAccessor<HDouble> {

    public static final String TYPE = "HDouble";
    private HollowConsumerAPI.DoubleRetriever api;

    public DoubleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (HollowConsumerAPI.DoubleRetriever)consumer.getAPI();
    }

    public DoubleDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.DoubleRetriever api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public DoubleDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.DoubleRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public DoubleDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.DoubleRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HDouble getRecord(int ordinal){
        return api.getHDouble(ordinal);
    }
}