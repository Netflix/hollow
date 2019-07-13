package com.netflix.vms.transformer.input.api.gen.videoType;

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
public class VideoTypeAPIHashIndex extends AbstractHollowHashIndex<VideoTypeAPI> {

    public VideoTypeAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public VideoTypeAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
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

    public Iterable<VideoTypeMedia> findVideoTypeMediaMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeMedia>(matches.iterator()) {
            public VideoTypeMedia getData(int ordinal) {
                return api.getVideoTypeMedia(ordinal);
            }
        };
    }

    public Iterable<VideoTypeMediaList> findVideoTypeMediaListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeMediaList>(matches.iterator()) {
            public VideoTypeMediaList getData(int ordinal) {
                return api.getVideoTypeMediaList(ordinal);
            }
        };
    }

    public Iterable<VideoTypeDescriptor> findVideoTypeDescriptorMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeDescriptor>(matches.iterator()) {
            public VideoTypeDescriptor getData(int ordinal) {
                return api.getVideoTypeDescriptor(ordinal);
            }
        };
    }

    public Iterable<VideoTypeDescriptorSet> findVideoTypeDescriptorSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeDescriptorSet>(matches.iterator()) {
            public VideoTypeDescriptorSet getData(int ordinal) {
                return api.getVideoTypeDescriptorSet(ordinal);
            }
        };
    }

    public Iterable<VideoType> findVideoTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoType>(matches.iterator()) {
            public VideoType getData(int ordinal) {
                return api.getVideoType(ordinal);
            }
        };
    }

}