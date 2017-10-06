package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamAssetTypeDataAccessor extends AbstractHollowDataAccessor<StreamAssetTypeHollow> {

    public static final String TYPE = "StreamAssetTypeHollow";
    private VMSHollowInputAPI api;

    public StreamAssetTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamAssetTypeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamAssetTypeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamAssetTypeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamAssetTypeHollow getRecord(int ordinal){
        return api.getStreamAssetTypeHollow(ordinal);
    }

}