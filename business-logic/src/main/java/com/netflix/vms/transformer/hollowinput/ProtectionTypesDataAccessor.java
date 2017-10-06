package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ProtectionTypesDataAccessor extends AbstractHollowDataAccessor<ProtectionTypesHollow> {

    public static final String TYPE = "ProtectionTypesHollow";
    private VMSHollowInputAPI api;

    public ProtectionTypesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ProtectionTypesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ProtectionTypesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ProtectionTypesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ProtectionTypesHollow getRecord(int ordinal){
        return api.getProtectionTypesHollow(ordinal);
    }

}