package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieCharacterPersonPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, MovieCharacterPersonHollow> {

    public MovieCharacterPersonPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public MovieCharacterPersonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("MovieCharacterPerson")).getPrimaryKey().getFieldPaths());
    }

    public MovieCharacterPersonPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieCharacterPersonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieCharacterPerson", isListenToDataRefresh, fieldPaths);
    }

    public MovieCharacterPersonHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieCharacterPersonHollow(ordinal);
    }

}