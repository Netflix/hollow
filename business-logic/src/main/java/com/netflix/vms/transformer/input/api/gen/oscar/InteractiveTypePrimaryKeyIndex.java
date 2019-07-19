package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<InteractiveType, K> uki = UniqueKeyIndex.from(consumer, InteractiveType.class)
 *         .usingBean(k);
 *     InteractiveType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code InteractiveType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class InteractiveTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, InteractiveType> implements HollowUniqueKeyIndex<InteractiveType> {

    public InteractiveTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public InteractiveTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("InteractiveType")).getPrimaryKey().getFieldPaths());
    }

    public InteractiveTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public InteractiveTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "InteractiveType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public InteractiveType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getInteractiveType(ordinal);
    }

}