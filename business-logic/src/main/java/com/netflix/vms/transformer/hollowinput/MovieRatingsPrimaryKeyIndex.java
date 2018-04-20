package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieRatingsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, MovieRatingsHollow> {

    public MovieRatingsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public MovieRatingsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("MovieRatings")).getPrimaryKey().getFieldPaths());
    }

    public MovieRatingsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieRatingsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieRatings", isListenToDataRefresh, fieldPaths);
    }

    public MovieRatingsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieRatingsHollow(ordinal);
    }

}