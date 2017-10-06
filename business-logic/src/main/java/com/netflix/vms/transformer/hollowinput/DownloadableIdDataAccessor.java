package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DownloadableIdDataAccessor extends AbstractHollowDataAccessor<DownloadableIdHollow> {

    public static final String TYPE = "DownloadableIdHollow";
    private VMSHollowInputAPI api;

    public DownloadableIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DownloadableIdDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DownloadableIdDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DownloadableIdDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DownloadableIdHollow getRecord(int ordinal){
        return api.getDownloadableIdHollow(ordinal);
    }

}