package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RightsContractAsset, K> uki = UniqueKeyIndex.from(consumer, RightsContractAsset.class)
 *         .usingBean(k);
 *     RightsContractAsset m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RightsContractAsset} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RightsContractAssetPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, RightsContractAsset> implements HollowUniqueKeyIndex<RightsContractAsset> {

    public RightsContractAssetPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RightsContractAssetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RightsContractAsset")).getPrimaryKey().getFieldPaths());
    }

    public RightsContractAssetPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsContractAssetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RightsContractAsset", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RightsContractAsset findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsContractAsset(ordinal);
    }

}