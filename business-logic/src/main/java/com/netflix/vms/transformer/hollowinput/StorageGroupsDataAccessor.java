package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StorageGroupsDataAccessor extends AbstractHollowDataAccessor<StorageGroupsHollow> {

    public static final String TYPE = "StorageGroupsHollow";
    private VMSHollowInputAPI api;

    public StorageGroupsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StorageGroupsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StorageGroupsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StorageGroupsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StorageGroupsHollow getRecord(int ordinal){
        return api.getStorageGroupsHollow(ordinal);
    }

}