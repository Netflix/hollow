package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class AssetMetaDatasDataAccessor extends AbstractHollowDataAccessor<AssetMetaDatasHollow> {

    public static final String TYPE = "AssetMetaDatasHollow";
    private VMSHollowInputAPI api;

    public AssetMetaDatasDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public AssetMetaDatasDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public AssetMetaDatasDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public AssetMetaDatasDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public AssetMetaDatasHollow getRecord(int ordinal){
        return api.getAssetMetaDatasHollow(ordinal);
    }

}