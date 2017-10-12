package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HDouble;

public class DoubleDataAccessor extends AbstractHollowDataAccessor<Double> {

    public static final String TYPE = "Double";
    private HollowConsumerAPI.DoubleRetriever api;

    public DoubleDataAccessor(HollowConsumer consumer) {
        this(consumer.getStateEngine(), (HollowConsumerAPI.DoubleRetriever)consumer.getAPI());
    }

    public DoubleDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.DoubleRetriever api) {
        this(rStateEngine, api, "value");
    }

    public DoubleDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.DoubleRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public DoubleDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.DoubleRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Double getRecord(int ordinal){
        HDouble val = api.getHDouble(ordinal);
        return val == null ? null : val.getValueBoxed();
    }
}