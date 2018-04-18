package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkLocalePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ArtworkLocaleHollow> {

    public ArtworkLocalePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ArtworkLocalePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ArtworkLocale")).getPrimaryKey().getFieldPaths());
    }

    public ArtworkLocalePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ArtworkLocalePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ArtworkLocale", isListenToDataRefresh, fieldPaths);
    }

    public ArtworkLocaleHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getArtworkLocaleHollow(ordinal);
    }

}