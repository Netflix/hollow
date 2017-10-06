package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class LocalizedCharacterDataAccessor extends AbstractHollowDataAccessor<LocalizedCharacterHollow> {

    public static final String TYPE = "LocalizedCharacterHollow";
    private VMSHollowInputAPI api;

    public LocalizedCharacterDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public LocalizedCharacterDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public LocalizedCharacterDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public LocalizedCharacterDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public LocalizedCharacterHollow getRecord(int ordinal){
        return api.getLocalizedCharacterHollow(ordinal);
    }

}