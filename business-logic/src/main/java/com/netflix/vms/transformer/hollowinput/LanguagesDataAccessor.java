package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class LanguagesDataAccessor extends AbstractHollowDataAccessor<LanguagesHollow> {

    public static final String TYPE = "LanguagesHollow";
    private VMSHollowInputAPI api;

    public LanguagesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public LanguagesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public LanguagesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public LanguagesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public LanguagesHollow getRecord(int ordinal){
        return api.getLanguagesHollow(ordinal);
    }

}