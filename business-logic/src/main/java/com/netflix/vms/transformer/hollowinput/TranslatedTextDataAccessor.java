package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TranslatedTextDataAccessor extends AbstractHollowDataAccessor<TranslatedTextHollow> {

    public static final String TYPE = "TranslatedTextHollow";
    private VMSHollowInputAPI api;

    public TranslatedTextDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TranslatedTextDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TranslatedTextDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TranslatedTextDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TranslatedTextHollow getRecord(int ordinal){
        return api.getTranslatedTextHollow(ordinal);
    }

}