package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtWorkImageTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ArtWorkImageTypeHollow> {

    public ArtWorkImageTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ArtWorkImageTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ArtWorkImageType")).getPrimaryKey().getFieldPaths());
    }

    public ArtWorkImageTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ArtWorkImageTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ArtWorkImageType", isListenToDataRefresh, fieldPaths);
    }

    public ArtWorkImageTypeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getArtWorkImageTypeHollow(ordinal);
    }

}