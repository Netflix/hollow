package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AltGenresPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, AltGenresHollow> {

    public AltGenresPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public AltGenresPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("AltGenres")).getPrimaryKey().getFieldPaths());
    }

    public AltGenresPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AltGenresPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "AltGenres", isListenToDataRefreah, fieldPaths);
    }

    public AltGenresHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAltGenresHollow(ordinal);
    }

}