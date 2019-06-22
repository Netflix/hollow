package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RightsWindowContract, K> uki = UniqueKeyIndex.from(consumer, RightsWindowContract.class)
 *         .usingBean(k);
 *     RightsWindowContract m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RightsWindowContract} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RightsWindowContractPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, RightsWindowContract> implements HollowUniqueKeyIndex<RightsWindowContract> {

    public RightsWindowContractPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RightsWindowContractPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RightsWindowContract")).getPrimaryKey().getFieldPaths());
    }

    public RightsWindowContractPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsWindowContractPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RightsWindowContract", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RightsWindowContract findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsWindowContract(ordinal);
    }

}