package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class LongDataAccessor extends AbstractHollowDataAccessor<HLong> {

    public static final String TYPE = "HLong";
    private CupTokenAPI api;

    public LongDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (CupTokenAPI)consumer.getAPI();
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, CupTokenAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, CupTokenAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public LongDataAccessor(HollowReadStateEngine rStateEngine, CupTokenAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HLong getRecord(int ordinal){
        return api.getHLong(ordinal);
    }

}