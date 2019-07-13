package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

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
public class ExhibitDealAttributeV1APIHashIndex extends AbstractHollowHashIndex<ExhibitDealAttributeV1API> {

    public ExhibitDealAttributeV1APIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public ExhibitDealAttributeV1APIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
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

    public Iterable<SetOfString> findSetOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfString>(matches.iterator()) {
            public SetOfString getData(int ordinal) {
                return api.getSetOfString(ordinal);
            }
        };
    }

    public Iterable<DisallowedAssetBundleEntry> findDisallowedAssetBundleEntryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DisallowedAssetBundleEntry>(matches.iterator()) {
            public DisallowedAssetBundleEntry getData(int ordinal) {
                return api.getDisallowedAssetBundleEntry(ordinal);
            }
        };
    }

    public Iterable<SetOfDisallowedAssetBundleEntry> findSetOfDisallowedAssetBundleEntryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfDisallowedAssetBundleEntry>(matches.iterator()) {
            public SetOfDisallowedAssetBundleEntry getData(int ordinal) {
                return api.getSetOfDisallowedAssetBundleEntry(ordinal);
            }
        };
    }

    public Iterable<VmsAttributeFeedEntry> findVmsAttributeFeedEntryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VmsAttributeFeedEntry>(matches.iterator()) {
            public VmsAttributeFeedEntry getData(int ordinal) {
                return api.getVmsAttributeFeedEntry(ordinal);
            }
        };
    }

}