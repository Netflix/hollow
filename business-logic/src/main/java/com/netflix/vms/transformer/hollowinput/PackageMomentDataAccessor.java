package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PackageMomentDataAccessor extends AbstractHollowDataAccessor<PackageMomentHollow> {

    public static final String TYPE = "PackageMomentHollow";
    private VMSHollowInputAPI api;

    public PackageMomentDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PackageMomentDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PackageMomentDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PackageMomentDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PackageMomentHollow getRecord(int ordinal){
        return api.getPackageMomentHollow(ordinal);
    }

}