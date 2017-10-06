package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class MapKeyDataAccessor extends AbstractHollowDataAccessor<MapKeyHollow> {

    public static final String TYPE = "MapKeyHollow";
    private VMSHollowInputAPI api;

    public MapKeyDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public MapKeyDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public MapKeyDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public MapKeyDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public MapKeyHollow getRecord(int ordinal){
        return api.getMapKeyHollow(ordinal);
    }

}