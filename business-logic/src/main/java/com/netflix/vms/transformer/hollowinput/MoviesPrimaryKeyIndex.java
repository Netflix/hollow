package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MoviesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, MoviesHollow> {

    public MoviesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public MoviesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Movies")).getPrimaryKey().getFieldPaths());
    }

    public MoviesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MoviesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Movies", isListenToDataRefresh, fieldPaths);
    }

    public MoviesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMoviesHollow(ordinal);
    }

}