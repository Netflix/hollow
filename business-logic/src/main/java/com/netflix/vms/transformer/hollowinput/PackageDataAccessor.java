package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PackageDataAccessor extends AbstractHollowDataAccessor<PackageHollow> {

    public static final String TYPE = "PackageHollow";
    private VMSHollowInputAPI api;

    public PackageDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PackageDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PackageDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PackageDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PackageHollow getRecord(int ordinal){
        return api.getPackageHollow(ordinal);
    }

}