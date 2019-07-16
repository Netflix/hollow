package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<WindowType, K> uki = UniqueKeyIndex.from(consumer, WindowType.class)
 *         .usingBean(k);
 *     WindowType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code WindowType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class WindowTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, WindowType> implements HollowUniqueKeyIndex<WindowType> {

    public WindowTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public WindowTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("WindowType")).getPrimaryKey().getFieldPaths());
    }

    public WindowTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public WindowTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "WindowType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public WindowType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getWindowType(ordinal);
    }

}