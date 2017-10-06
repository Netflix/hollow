package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ArtworkRecipeDataAccessor extends AbstractHollowDataAccessor<ArtworkRecipeHollow> {

    public static final String TYPE = "ArtworkRecipeHollow";
    private VMSHollowInputAPI api;

    public ArtworkRecipeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ArtworkRecipeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ArtworkRecipeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ArtworkRecipeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ArtworkRecipeHollow getRecord(int ordinal){
        return api.getArtworkRecipeHollow(ordinal);
    }

}