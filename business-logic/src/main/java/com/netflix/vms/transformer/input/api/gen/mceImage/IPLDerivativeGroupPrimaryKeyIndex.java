package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<IPLDerivativeGroup, K> uki = UniqueKeyIndex.from(consumer, IPLDerivativeGroup.class)
 *         .usingBean(k);
 *     IPLDerivativeGroup m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code IPLDerivativeGroup} object.
 */
@Deprecated
@SuppressWarnings("all")
public class IPLDerivativeGroupPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<MceImageV3API, IPLDerivativeGroup> implements HollowUniqueKeyIndex<IPLDerivativeGroup> {

    public IPLDerivativeGroupPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public IPLDerivativeGroupPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("IPLDerivativeGroup")).getPrimaryKey().getFieldPaths());
    }

    public IPLDerivativeGroupPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IPLDerivativeGroupPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "IPLDerivativeGroup", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public IPLDerivativeGroup findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIPLDerivativeGroup(ordinal);
    }

}