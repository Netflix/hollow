package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<VideoAwardMapping, K> uki = HashIndex.from(consumer, VideoAwardMapping.class)
 *         .usingBean(k);
 *     Stream<VideoAwardMapping> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code VideoAwardMapping} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoAwardAPIHashIndex extends AbstractHollowHashIndex<VideoAwardAPI> {

    public VideoAwardAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public VideoAwardAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<VideoAwardMapping> findVideoAwardMappingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoAwardMapping>(matches.iterator()) {
            public VideoAwardMapping getData(int ordinal) {
                return api.getVideoAwardMapping(ordinal);
            }
        };
    }

    public Iterable<VideoAwardList> findVideoAwardListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoAwardList>(matches.iterator()) {
            public VideoAwardList getData(int ordinal) {
                return api.getVideoAwardList(ordinal);
            }
        };
    }

    public Iterable<VideoAward> findVideoAwardMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoAward>(matches.iterator()) {
            public VideoAward getData(int ordinal) {
                return api.getVideoAward(ordinal);
            }
        };
    }

}