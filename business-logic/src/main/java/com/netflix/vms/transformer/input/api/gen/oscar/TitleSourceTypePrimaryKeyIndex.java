package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<TitleSourceType, K> uki = UniqueKeyIndex.from(consumer, TitleSourceType.class)
 *         .usingBean(k);
 *     TitleSourceType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code TitleSourceType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class TitleSourceTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, TitleSourceType> implements HollowUniqueKeyIndex<TitleSourceType> {

    public TitleSourceTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public TitleSourceTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("TitleSourceType")).getPrimaryKey().getFieldPaths());
    }

    public TitleSourceTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TitleSourceTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TitleSourceType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public TitleSourceType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTitleSourceType(ordinal);
    }

}