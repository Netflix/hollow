package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CategoryGroupsDataAccessor extends AbstractHollowDataAccessor<CategoryGroupsHollow> {

    public static final String TYPE = "CategoryGroupsHollow";
    private VMSHollowInputAPI api;

    public CategoryGroupsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CategoryGroupsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CategoryGroupsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CategoryGroupsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CategoryGroupsHollow getRecord(int ordinal){
        return api.getCategoryGroupsHollow(ordinal);
    }

}