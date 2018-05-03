package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FeedMovieCountryLanguagesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, FeedMovieCountryLanguagesHollow> {

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("FeedMovieCountryLanguages")).getPrimaryKey().getFieldPaths());
    }

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "FeedMovieCountryLanguages", isListenToDataRefresh, fieldPaths);
    }

    public FeedMovieCountryLanguagesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getFeedMovieCountryLanguagesHollow(ordinal);
    }

}