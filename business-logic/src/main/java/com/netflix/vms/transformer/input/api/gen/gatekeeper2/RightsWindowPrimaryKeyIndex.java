package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RightsWindow, K> uki = UniqueKeyIndex.from(consumer, RightsWindow.class)
 *         .usingBean(k);
 *     RightsWindow m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RightsWindow} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RightsWindowPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, RightsWindow> implements HollowUniqueKeyIndex<RightsWindow> {

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RightsWindow")).getPrimaryKey().getFieldPaths());
    }

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RightsWindow", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RightsWindow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsWindow(ordinal);
    }

}