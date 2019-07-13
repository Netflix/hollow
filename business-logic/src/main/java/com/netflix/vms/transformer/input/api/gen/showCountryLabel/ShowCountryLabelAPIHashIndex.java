package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<ISOCountry, K> uki = HashIndex.from(consumer, ISOCountry.class)
 *         .usingBean(k);
 *     Stream<ISOCountry> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code ISOCountry} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class ShowCountryLabelAPIHashIndex extends AbstractHollowHashIndex<ShowCountryLabelAPI> {

    public ShowCountryLabelAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public ShowCountryLabelAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<ISOCountry> findISOCountryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ISOCountry>(matches.iterator()) {
            public ISOCountry getData(int ordinal) {
                return api.getISOCountry(ordinal);
            }
        };
    }

    public Iterable<ISOCountryList> findISOCountryListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ISOCountryList>(matches.iterator()) {
            public ISOCountryList getData(int ordinal) {
                return api.getISOCountryList(ordinal);
            }
        };
    }

    public Iterable<ShowMemberType> findShowMemberTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowMemberType>(matches.iterator()) {
            public ShowMemberType getData(int ordinal) {
                return api.getShowMemberType(ordinal);
            }
        };
    }

    public Iterable<ShowMemberTypeList> findShowMemberTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowMemberTypeList>(matches.iterator()) {
            public ShowMemberTypeList getData(int ordinal) {
                return api.getShowMemberTypeList(ordinal);
            }
        };
    }

    public Iterable<ShowCountryLabel> findShowCountryLabelMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowCountryLabel>(matches.iterator()) {
            public ShowCountryLabel getData(int ordinal) {
                return api.getShowCountryLabel(ordinal);
            }
        };
    }

}