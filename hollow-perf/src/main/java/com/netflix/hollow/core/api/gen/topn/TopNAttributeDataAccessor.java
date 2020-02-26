package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class TopNAttributeDataAccessor extends AbstractHollowDataAccessor<TopNAttribute> {

    public static final String TYPE = "TopNAttribute";
    private TopNAPI api;

    public TopNAttributeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (TopNAPI)consumer.getAPI();
    }

    public TopNAttributeDataAccessor(HollowReadStateEngine rStateEngine, TopNAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public TopNAttributeDataAccessor(HollowReadStateEngine rStateEngine, TopNAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public TopNAttributeDataAccessor(HollowReadStateEngine rStateEngine, TopNAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public TopNAttribute getRecord(int ordinal){
        return api.getTopNAttribute(ordinal);
    }

}