package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HString;

public class StringDataAccessor extends AbstractHollowDataAccessor<String> {

    public static final String TYPE = "String";
    private HollowConsumerAPI.StringRetriever api;

    public StringDataAccessor(HollowConsumer consumer) {
        this(consumer.getStateEngine(), (HollowConsumerAPI.StringRetriever)consumer.getAPI());
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.StringRetriever api) {
        this(rStateEngine, api, "value");
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.StringRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.StringRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public String getRecord(int ordinal){
        HString val = api.getHString(ordinal);
        return val == null ? null : val.getValue();
    }
}