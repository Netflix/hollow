package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Container, K> uki = UniqueKeyIndex.from(consumer, Container.class)
 *         .usingBean(k);
 *     Container m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Container} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ContainerPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<FlexDSAPI, Container> implements HollowUniqueKeyIndex<Container> {

    public ContainerPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ContainerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Container")).getPrimaryKey().getFieldPaths());
    }

    public ContainerPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ContainerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Container", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Container findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getContainer(ordinal);
    }

}