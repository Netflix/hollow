package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class DisplaySetDataAccessor extends AbstractHollowDataAccessor<DisplaySet> {

    public static final String TYPE = "DisplaySet";
    private FlexDSAPI api;

    public DisplaySetDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (FlexDSAPI)consumer.getAPI();
    }

    public DisplaySetDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public DisplaySetDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public DisplaySetDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public DisplaySet getRecord(int ordinal){
        return api.getDisplaySet(ordinal);
    }

}