package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CategoriesDataAccessor extends AbstractHollowDataAccessor<CategoriesHollow> {

    public static final String TYPE = "CategoriesHollow";
    private VMSHollowInputAPI api;

    public CategoriesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CategoriesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CategoriesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CategoriesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CategoriesHollow getRecord(int ordinal){
        return api.getCategoriesHollow(ordinal);
    }

}