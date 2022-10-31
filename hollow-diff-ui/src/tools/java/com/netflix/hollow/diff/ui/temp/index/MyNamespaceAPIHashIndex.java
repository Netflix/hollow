package com.netflix.hollow.diff.ui.temp.index;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.MyEntity;
import com.netflix.hollow.diff.ui.temp.MyEntityRankIndex;
import com.netflix.hollow.diff.ui.temp.ProfileId;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;
import java.lang.Iterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows
 * if the consumer object is accessible:
 * <pre>{@code
 *     HashIndex<HInteger, K> uki = HashIndex.from(consumer, HInteger.class)
 *         .usingBean(k);
 *     Stream<HInteger> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code HInteger} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class MyNamespaceAPIHashIndex extends AbstractHollowHashIndex<MyNamespaceAPI> {

    public MyNamespaceAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public MyNamespaceAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<HInteger> findIntegerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<HInteger>(matches.iterator()) {
            public HInteger getData(int ordinal) {
                return api.getHInteger(ordinal);
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

    public Iterable<ProfileId> findprofileIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ProfileId>(matches.iterator()) {
            public ProfileId getData(int ordinal) {
                return api.getProfileId(ordinal);
            }
        };
    }

    public Iterable<MyEntity> findMyEntityMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MyEntity>(matches.iterator()) {
            public MyEntity getData(int ordinal) {
                return api.getMyEntity(ordinal);
            }
        };
    }

    public Iterable<MapOfMyEntityToInteger> findMapOfMyEntityToIntegerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfMyEntityToInteger>(matches.iterator()) {
            public MapOfMyEntityToInteger getData(int ordinal) {
                return api.getMapOfMyEntityToInteger(ordinal);
            }
        };
    }

    public Iterable<MyEntityRankIndex> findMyEntityRankIndexMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MyEntityRankIndex>(matches.iterator()) {
            public MyEntityRankIndex getData(int ordinal) {
                return api.getMyEntityRankIndex(ordinal);
            }
        };
    }

}