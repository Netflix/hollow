package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<HLong, K> uki = HashIndex.from(consumer, HLong.class)
 *         .usingBean(k);
 *     Stream<HLong> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code HLong} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class MclEarliestDateAPIHashIndex extends AbstractHollowHashIndex<MclEarliestDateAPI> {

    public MclEarliestDateAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public MclEarliestDateAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<HLong> findLongMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<HLong>(matches.iterator()) {
            public HLong getData(int ordinal) {
                return api.getHLong(ordinal);
            }
        };
    }

    public Iterable<HString> findStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<HString>(matches.iterator()) {
            public HString getData(int ordinal) {
                return api.getHString(ordinal);
            }
        };
    }

    public Iterable<MapOfStringToLong> findMapOfStringToLongMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfStringToLong>(matches.iterator()) {
            public MapOfStringToLong getData(int ordinal) {
                return api.getMapOfStringToLong(ordinal);
            }
        };
    }

    public Iterable<FeedMovieCountryLanguages> findFeedMovieCountryLanguagesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<FeedMovieCountryLanguages>(matches.iterator()) {
            public FeedMovieCountryLanguages getData(int ordinal) {
                return api.getFeedMovieCountryLanguages(ordinal);
            }
        };
    }

}