package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<HString, K> uki = HashIndex.from(consumer, HString.class)
 *         .usingBean(k);
 *     Stream<HString> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code HString} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class SupplementalAPIHashIndex extends AbstractHollowHashIndex<SupplementalAPI> {

    public SupplementalAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public SupplementalAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
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

    public Iterable<IndividualSupplementalIdentifierSet> findIndividualSupplementalIdentifierSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IndividualSupplementalIdentifierSet>(matches.iterator()) {
            public IndividualSupplementalIdentifierSet getData(int ordinal) {
                return api.getIndividualSupplementalIdentifierSet(ordinal);
            }
        };
    }

    public Iterable<IndividualSupplementalThemeSet> findIndividualSupplementalThemeSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IndividualSupplementalThemeSet>(matches.iterator()) {
            public IndividualSupplementalThemeSet getData(int ordinal) {
                return api.getIndividualSupplementalThemeSet(ordinal);
            }
        };
    }

    public Iterable<IndividualSupplementalUsageSet> findIndividualSupplementalUsageSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IndividualSupplementalUsageSet>(matches.iterator()) {
            public IndividualSupplementalUsageSet getData(int ordinal) {
                return api.getIndividualSupplementalUsageSet(ordinal);
            }
        };
    }

    public Iterable<IndividualSupplemental> findIndividualSupplementalMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IndividualSupplemental>(matches.iterator()) {
            public IndividualSupplemental getData(int ordinal) {
                return api.getIndividualSupplemental(ordinal);
            }
        };
    }

    public Iterable<SupplementalsList> findSupplementalsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SupplementalsList>(matches.iterator()) {
            public SupplementalsList getData(int ordinal) {
                return api.getSupplementalsList(ordinal);
            }
        };
    }

    public Iterable<Supplementals> findSupplementalsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Supplementals>(matches.iterator()) {
            public Supplementals getData(int ordinal) {
                return api.getSupplementals(ordinal);
            }
        };
    }

}