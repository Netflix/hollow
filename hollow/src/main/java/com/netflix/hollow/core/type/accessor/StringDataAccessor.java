package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HString;

public class StringDataAccessor extends AbstractHollowDataAccessor<HString> {

    public static final String TYPE = "HString";
    private HollowConsumerAPI.StringRetriever api;

    public StringDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (HollowConsumerAPI.StringRetriever)consumer.getAPI();
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.StringRetriever api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.StringRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.StringRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HString getRecord(int ordinal){
        return api.getHString(ordinal);
    }

}