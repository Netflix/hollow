package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;
import java.lang.Iterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<Container, K> uki = HashIndex.from(consumer, Container.class)
 *         .usingBean(k);
 *     Stream<Container> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code Container} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class FlexDSAPIHashIndex extends AbstractHollowHashIndex<FlexDSAPI> {

    public FlexDSAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public FlexDSAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<Container> findContainerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Container>(matches.iterator()) {
            public Container getData(int ordinal) {
                return api.getContainer(ordinal);
            }
        };
    }

    public Iterable<SetOfContainer> findSetOfContainerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfContainer>(matches.iterator()) {
            public SetOfContainer getData(int ordinal) {
                return api.getSetOfContainer(ordinal);
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

    public Iterable<AuditGroup> findAuditGroupMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AuditGroup>(matches.iterator()) {
            public AuditGroup getData(int ordinal) {
                return api.getAuditGroup(ordinal);
            }
        };
    }

    public Iterable<ListOfString> findListOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfString>(matches.iterator()) {
            public ListOfString getData(int ordinal) {
                return api.getListOfString(ordinal);
            }
        };
    }

    public Iterable<SetOfString> findSetOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfString>(matches.iterator()) {
            public SetOfString getData(int ordinal) {
                return api.getSetOfString(ordinal);
            }
        };
    }

    public Iterable<DisplaySet> findDisplaySetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DisplaySet>(matches.iterator()) {
            public DisplaySet getData(int ordinal) {
                return api.getDisplaySet(ordinal);
            }
        };
    }

}