package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamFileIdentificationDataAccessor extends AbstractHollowDataAccessor<StreamFileIdentificationHollow> {

    public static final String TYPE = "StreamFileIdentificationHollow";
    private VMSHollowInputAPI api;

    public StreamFileIdentificationDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamFileIdentificationDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamFileIdentificationDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamFileIdentificationDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamFileIdentificationHollow getRecord(int ordinal){
        return api.getStreamFileIdentificationHollow(ordinal);
    }

}