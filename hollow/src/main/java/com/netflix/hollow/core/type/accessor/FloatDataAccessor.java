package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HFloat;

public class FloatDataAccessor extends AbstractHollowDataAccessor<HFloat> {

    public static final String TYPE = "HFloat";
    private HollowConsumerAPI.FloatRetriever api;

    public FloatDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (HollowConsumerAPI.FloatRetriever)consumer.getAPI();
    }

    public FloatDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.FloatRetriever api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public FloatDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.FloatRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public FloatDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.FloatRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HFloat getRecord(int ordinal){
        return api.getHFloat(ordinal);
    }
}