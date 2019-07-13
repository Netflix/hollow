package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<HBoolean, K> uki = HashIndex.from(consumer, HBoolean.class)
 *         .usingBean(k);
 *     Stream<HBoolean> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code HBoolean} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class PackageDealCountryAPIHashIndex extends AbstractHollowHashIndex<PackageDealCountryAPI> {

    public PackageDealCountryAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public PackageDealCountryAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<HBoolean> findBooleanMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<HBoolean>(matches.iterator()) {
            public HBoolean getData(int ordinal) {
                return api.getHBoolean(ordinal);
            }
        };
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

    public Iterable<ListOfPackageTags> findListOfPackageTagsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfPackageTags>(matches.iterator()) {
            public ListOfPackageTags getData(int ordinal) {
                return api.getListOfPackageTags(ordinal);
            }
        };
    }

    public Iterable<MapOfStringToBoolean> findMapOfStringToBooleanMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfStringToBoolean>(matches.iterator()) {
            public MapOfStringToBoolean getData(int ordinal) {
                return api.getMapOfStringToBoolean(ordinal);
            }
        };
    }

    public Iterable<DealCountryGroup> findDealCountryGroupMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DealCountryGroup>(matches.iterator()) {
            public DealCountryGroup getData(int ordinal) {
                return api.getDealCountryGroup(ordinal);
            }
        };
    }

    public Iterable<ListOfDealCountryGroup> findListOfDealCountryGroupMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfDealCountryGroup>(matches.iterator()) {
            public ListOfDealCountryGroup getData(int ordinal) {
                return api.getListOfDealCountryGroup(ordinal);
            }
        };
    }

    public Iterable<PackageMovieDealCountryGroup> findPackageMovieDealCountryGroupMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageMovieDealCountryGroup>(matches.iterator()) {
            public PackageMovieDealCountryGroup getData(int ordinal) {
                return api.getPackageMovieDealCountryGroup(ordinal);
            }
        };
    }

}