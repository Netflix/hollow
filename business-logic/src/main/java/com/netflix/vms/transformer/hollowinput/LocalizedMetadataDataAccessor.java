package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class LocalizedMetadataDataAccessor extends AbstractHollowDataAccessor<LocalizedMetadataHollow> {

    public static final String TYPE = "LocalizedMetadataHollow";
    private VMSHollowInputAPI api;

    public LocalizedMetadataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public LocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public LocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public LocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public LocalizedMetadataHollow getRecord(int ordinal){
        return api.getLocalizedMetadataHollow(ordinal);
    }

}