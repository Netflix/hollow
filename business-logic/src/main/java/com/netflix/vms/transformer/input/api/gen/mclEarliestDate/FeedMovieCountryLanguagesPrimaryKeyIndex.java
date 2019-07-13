package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<FeedMovieCountryLanguages, K> uki = UniqueKeyIndex.from(consumer, FeedMovieCountryLanguages.class)
 *         .usingBean(k);
 *     FeedMovieCountryLanguages m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code FeedMovieCountryLanguages} object.
 */
@Deprecated
@SuppressWarnings("all")
public class FeedMovieCountryLanguagesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<MclEarliestDateAPI, FeedMovieCountryLanguages> implements HollowUniqueKeyIndex<FeedMovieCountryLanguages> {

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("FeedMovieCountryLanguages")).getPrimaryKey().getFieldPaths());
    }

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public FeedMovieCountryLanguagesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "FeedMovieCountryLanguages", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public FeedMovieCountryLanguages findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getFeedMovieCountryLanguages(ordinal);
    }

}