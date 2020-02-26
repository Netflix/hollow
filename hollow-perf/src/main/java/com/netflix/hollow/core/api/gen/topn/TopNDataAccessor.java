package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class TopNDataAccessor extends AbstractHollowDataAccessor<TopN> {

    public static final String TYPE = "TopN";
    private TopNAPI api;

    public TopNDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (TopNAPI)consumer.getAPI();
    }

    public TopNDataAccessor(HollowReadStateEngine rStateEngine, TopNAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public TopNDataAccessor(HollowReadStateEngine rStateEngine, TopNAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public TopNDataAccessor(HollowReadStateEngine rStateEngine, TopNAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public TopN getRecord(int ordinal){
        return api.getTopN(ordinal);
    }

}