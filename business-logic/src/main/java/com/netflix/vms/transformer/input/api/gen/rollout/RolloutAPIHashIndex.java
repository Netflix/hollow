package com.netflix.vms.transformer.input.api.gen.rollout;

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
public class RolloutAPIHashIndex extends AbstractHollowHashIndex<RolloutAPI> {

    public RolloutAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public RolloutAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
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

    public Iterable<RolloutPhaseWindow> findRolloutPhaseWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseWindow>(matches.iterator()) {
            public RolloutPhaseWindow getData(int ordinal) {
                return api.getRolloutPhaseWindow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseWindowMap> findRolloutPhaseWindowMapMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseWindowMap>(matches.iterator()) {
            public RolloutPhaseWindowMap getData(int ordinal) {
                return api.getRolloutPhaseWindowMap(ordinal);
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

    public Iterable<RolloutPhaseArtworkSourceFileId> findRolloutPhaseArtworkSourceFileIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseArtworkSourceFileId>(matches.iterator()) {
            public RolloutPhaseArtworkSourceFileId getData(int ordinal) {
                return api.getRolloutPhaseArtworkSourceFileId(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseArtworkSourceFileIdList> findRolloutPhaseArtworkSourceFileIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseArtworkSourceFileIdList>(matches.iterator()) {
            public RolloutPhaseArtworkSourceFileIdList getData(int ordinal) {
                return api.getRolloutPhaseArtworkSourceFileIdList(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseArtwork> findRolloutPhaseArtworkMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseArtwork>(matches.iterator()) {
            public RolloutPhaseArtwork getData(int ordinal) {
                return api.getRolloutPhaseArtwork(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseLocalizedMetadata> findRolloutPhaseLocalizedMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseLocalizedMetadata>(matches.iterator()) {
            public RolloutPhaseLocalizedMetadata getData(int ordinal) {
                return api.getRolloutPhaseLocalizedMetadata(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseElements> findRolloutPhaseElementsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseElements>(matches.iterator()) {
            public RolloutPhaseElements getData(int ordinal) {
                return api.getRolloutPhaseElements(ordinal);
            }
        };
    }

    public Iterable<RolloutPhase> findRolloutPhaseMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhase>(matches.iterator()) {
            public RolloutPhase getData(int ordinal) {
                return api.getRolloutPhase(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseList> findRolloutPhaseListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseList>(matches.iterator()) {
            public RolloutPhaseList getData(int ordinal) {
                return api.getRolloutPhaseList(ordinal);
            }
        };
    }

    public Iterable<Rollout> findRolloutMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Rollout>(matches.iterator()) {
            public Rollout getData(int ordinal) {
                return api.getRollout(ordinal);
            }
        };
    }

}