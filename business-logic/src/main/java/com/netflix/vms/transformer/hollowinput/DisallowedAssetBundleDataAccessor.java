package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DisallowedAssetBundleDataAccessor extends AbstractHollowDataAccessor<DisallowedAssetBundleHollow> {

    public static final String TYPE = "DisallowedAssetBundleHollow";
    private VMSHollowInputAPI api;

    public DisallowedAssetBundleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DisallowedAssetBundleDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DisallowedAssetBundleDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DisallowedAssetBundleDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DisallowedAssetBundleHollow getRecord(int ordinal){
        return api.getDisallowedAssetBundleHollow(ordinal);
    }

}