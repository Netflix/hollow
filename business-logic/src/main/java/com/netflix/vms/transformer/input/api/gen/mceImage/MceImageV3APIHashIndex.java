package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<DerivativeTag, K> uki = HashIndex.from(consumer, DerivativeTag.class)
 *         .usingBean(k);
 *     Stream<DerivativeTag> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code DerivativeTag} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class MceImageV3APIHashIndex extends AbstractHollowHashIndex<MceImageV3API> {

    public MceImageV3APIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public MceImageV3APIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<DerivativeTag> findDerivativeTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DerivativeTag>(matches.iterator()) {
            public DerivativeTag getData(int ordinal) {
                return api.getDerivativeTag(ordinal);
            }
        };
    }

    public Iterable<ListOfDerivativeTag> findListOfDerivativeTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfDerivativeTag>(matches.iterator()) {
            public ListOfDerivativeTag getData(int ordinal) {
                return api.getListOfDerivativeTag(ordinal);
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

    public Iterable<IPLArtworkDerivative> findIPLArtworkDerivativeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLArtworkDerivative>(matches.iterator()) {
            public IPLArtworkDerivative getData(int ordinal) {
                return api.getIPLArtworkDerivative(ordinal);
            }
        };
    }

    public Iterable<IPLDerivativeSet> findIPLDerivativeSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLDerivativeSet>(matches.iterator()) {
            public IPLDerivativeSet getData(int ordinal) {
                return api.getIPLDerivativeSet(ordinal);
            }
        };
    }

    public Iterable<IPLDerivativeGroup> findIPLDerivativeGroupMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLDerivativeGroup>(matches.iterator()) {
            public IPLDerivativeGroup getData(int ordinal) {
                return api.getIPLDerivativeGroup(ordinal);
            }
        };
    }

    public Iterable<IPLDerivativeGroupSet> findIPLDerivativeGroupSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLDerivativeGroupSet>(matches.iterator()) {
            public IPLDerivativeGroupSet getData(int ordinal) {
                return api.getIPLDerivativeGroupSet(ordinal);
            }
        };
    }

    public Iterable<IPLArtworkDerivativeSet> findIPLArtworkDerivativeSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLArtworkDerivativeSet>(matches.iterator()) {
            public IPLArtworkDerivativeSet getData(int ordinal) {
                return api.getIPLArtworkDerivativeSet(ordinal);
            }
        };
    }

}