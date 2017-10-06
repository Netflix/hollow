package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ArtWorkImageTypeDataAccessor extends AbstractHollowDataAccessor<ArtWorkImageTypeHollow> {

    public static final String TYPE = "ArtWorkImageTypeHollow";
    private VMSHollowInputAPI api;

    public ArtWorkImageTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ArtWorkImageTypeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ArtWorkImageTypeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ArtWorkImageTypeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ArtWorkImageTypeHollow getRecord(int ordinal){
        return api.getArtWorkImageTypeHollow(ordinal);
    }

}