package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieCountriesNotOriginal, K> uki = UniqueKeyIndex.from(consumer, MovieCountriesNotOriginal.class)
 *         .usingBean(k);
 *     MovieCountriesNotOriginal m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieCountriesNotOriginal} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieCountriesNotOriginalPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieCountriesNotOriginal> implements HollowUniqueKeyIndex<MovieCountriesNotOriginal> {

    public MovieCountriesNotOriginalPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieCountriesNotOriginalPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieCountriesNotOriginal")).getPrimaryKey().getFieldPaths());
    }

    public MovieCountriesNotOriginalPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieCountriesNotOriginalPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieCountriesNotOriginal", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieCountriesNotOriginal findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieCountriesNotOriginal(ordinal);
    }

}