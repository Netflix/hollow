package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ArtworkLocaleDataAccessor extends AbstractHollowDataAccessor<ArtworkLocaleHollow> {

    public static final String TYPE = "ArtworkLocaleHollow";
    private VMSHollowInputAPI api;

    public ArtworkLocaleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ArtworkLocaleDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ArtworkLocaleDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ArtworkLocaleDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ArtworkLocaleHollow getRecord(int ordinal){
        return api.getArtworkLocaleHollow(ordinal);
    }

}