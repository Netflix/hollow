package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PackageStreamDataAccessor extends AbstractHollowDataAccessor<PackageStreamHollow> {

    public static final String TYPE = "PackageStreamHollow";
    private VMSHollowInputAPI api;

    public PackageStreamDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PackageStreamDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PackageStreamDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PackageStreamDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PackageStreamHollow getRecord(int ordinal){
        return api.getPackageStreamHollow(ordinal);
    }

}