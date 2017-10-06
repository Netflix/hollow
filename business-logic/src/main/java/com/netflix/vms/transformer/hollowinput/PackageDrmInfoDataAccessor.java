package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PackageDrmInfoDataAccessor extends AbstractHollowDataAccessor<PackageDrmInfoHollow> {

    public static final String TYPE = "PackageDrmInfoHollow";
    private VMSHollowInputAPI api;

    public PackageDrmInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PackageDrmInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PackageDrmInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PackageDrmInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PackageDrmInfoHollow getRecord(int ordinal){
        return api.getPackageDrmInfoHollow(ordinal);
    }

}