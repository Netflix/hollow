package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieCountries, K> uki = UniqueKeyIndex.from(consumer, MovieCountries.class)
 *         .usingBean(k);
 *     MovieCountries m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieCountries} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieCountriesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieCountries> implements HollowUniqueKeyIndex<MovieCountries> {

    public MovieCountriesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieCountriesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieCountries")).getPrimaryKey().getFieldPaths());
    }

    public MovieCountriesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieCountriesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieCountries", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieCountries findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieCountries(ordinal);
    }

}