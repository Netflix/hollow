package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HFloat;

public class FloatDataAccessor extends AbstractHollowDataAccessor<Float> {

    public static final String TYPE = "Float";
    private HollowConsumerAPI.FloatRetriever api;

    public FloatDataAccessor(HollowConsumer consumer) {
        this(consumer.getStateEngine(), (HollowConsumerAPI.FloatRetriever)consumer.getAPI());
    }

    public FloatDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.FloatRetriever api) {
        this(rStateEngine, api, "value");
    }

    public FloatDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.FloatRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public FloatDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.FloatRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Float getRecord(int ordinal){
        HFloat val = api.getHFloat(ordinal);
        return val == null ? null : val.getValueBoxed();
    }
}