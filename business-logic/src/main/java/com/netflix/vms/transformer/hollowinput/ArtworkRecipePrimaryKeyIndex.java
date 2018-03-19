package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkRecipePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ArtworkRecipeHollow> {

    public ArtworkRecipePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ArtworkRecipePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ArtworkRecipe")).getPrimaryKey().getFieldPaths());
    }

    public ArtworkRecipePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ArtworkRecipePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ArtworkRecipe", isListenToDataRefreah, fieldPaths);
    }

    public ArtworkRecipeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getArtworkRecipeHollow(ordinal);
    }

}