package com.netflix.vms.transformer.input.api.gen.award;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VMSAward, K> uki = UniqueKeyIndex.from(consumer, VMSAward.class)
 *         .usingBean(k);
 *     VMSAward m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VMSAward} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VMSAwardPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<AwardAPI, VMSAward> implements HollowUniqueKeyIndex<VMSAward> {

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VMSAward")).getPrimaryKey().getFieldPaths());
    }

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VMSAward", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VMSAward findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVMSAward(ordinal);
    }

}