package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TranslatedTextValueDataAccessor extends AbstractHollowDataAccessor<TranslatedTextValueHollow> {

    public static final String TYPE = "TranslatedTextValueHollow";
    private VMSHollowInputAPI api;

    public TranslatedTextValueDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TranslatedTextValueDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TranslatedTextValueDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TranslatedTextValueDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TranslatedTextValueHollow getRecord(int ordinal){
        return api.getTranslatedTextValueHollow(ordinal);
    }

}