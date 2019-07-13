package com.netflix.vms.transformer.input.api.gen.videoGeneral;

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
public class VideoGeneralAPIHashIndex extends AbstractHollowHashIndex<VideoGeneralAPI> {

    public VideoGeneralAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public VideoGeneralAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
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

    public Iterable<SetOfString> findSetOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfString>(matches.iterator()) {
            public SetOfString getData(int ordinal) {
                return api.getSetOfString(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralAlias> findVideoGeneralAliasMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralAlias>(matches.iterator()) {
            public VideoGeneralAlias getData(int ordinal) {
                return api.getVideoGeneralAlias(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralAliasList> findVideoGeneralAliasListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralAliasList>(matches.iterator()) {
            public VideoGeneralAliasList getData(int ordinal) {
                return api.getVideoGeneralAliasList(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralEpisodeType> findVideoGeneralEpisodeTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralEpisodeType>(matches.iterator()) {
            public VideoGeneralEpisodeType getData(int ordinal) {
                return api.getVideoGeneralEpisodeType(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralEpisodeTypeList> findVideoGeneralEpisodeTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralEpisodeTypeList>(matches.iterator()) {
            public VideoGeneralEpisodeTypeList getData(int ordinal) {
                return api.getVideoGeneralEpisodeTypeList(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralInteractiveData> findVideoGeneralInteractiveDataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralInteractiveData>(matches.iterator()) {
            public VideoGeneralInteractiveData getData(int ordinal) {
                return api.getVideoGeneralInteractiveData(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralTitleType> findVideoGeneralTitleTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralTitleType>(matches.iterator()) {
            public VideoGeneralTitleType getData(int ordinal) {
                return api.getVideoGeneralTitleType(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralTitleTypeList> findVideoGeneralTitleTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralTitleTypeList>(matches.iterator()) {
            public VideoGeneralTitleTypeList getData(int ordinal) {
                return api.getVideoGeneralTitleTypeList(ordinal);
            }
        };
    }

    public Iterable<VideoGeneral> findVideoGeneralMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneral>(matches.iterator()) {
            public VideoGeneral getData(int ordinal) {
                return api.getVideoGeneral(ordinal);
            }
        };
    }

}