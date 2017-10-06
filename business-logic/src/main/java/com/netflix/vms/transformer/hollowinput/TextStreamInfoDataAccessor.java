package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TextStreamInfoDataAccessor extends AbstractHollowDataAccessor<TextStreamInfoHollow> {

    public static final String TYPE = "TextStreamInfoHollow";
    private VMSHollowInputAPI api;

    public TextStreamInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TextStreamInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TextStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TextStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TextStreamInfoHollow getRecord(int ordinal){
        return api.getTextStreamInfoHollow(ordinal);
    }

}