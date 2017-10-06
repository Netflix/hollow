package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamAssetMetadataDataAccessor extends AbstractHollowDataAccessor<StreamAssetMetadataHollow> {

    public static final String TYPE = "StreamAssetMetadataHollow";
    private VMSHollowInputAPI api;

    public StreamAssetMetadataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamAssetMetadataDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamAssetMetadataDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamAssetMetadataDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamAssetMetadataHollow getRecord(int ordinal){
        return api.getStreamAssetMetadataHollow(ordinal);
    }

}