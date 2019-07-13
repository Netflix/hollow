package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ReleaseDate, K> uki = UniqueKeyIndex.from(consumer, ReleaseDate.class)
 *         .usingBean(k);
 *     ReleaseDate m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ReleaseDate} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ReleaseDatePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoDateAPI, ReleaseDate> implements HollowUniqueKeyIndex<ReleaseDate> {

    public ReleaseDatePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ReleaseDatePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ReleaseDate")).getPrimaryKey().getFieldPaths());
    }

    public ReleaseDatePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ReleaseDatePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ReleaseDate", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ReleaseDate findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getReleaseDate(ordinal);
    }

}