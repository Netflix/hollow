package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RightsContractPackage, K> uki = UniqueKeyIndex.from(consumer, RightsContractPackage.class)
 *         .usingBean(k);
 *     RightsContractPackage m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RightsContractPackage} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RightsContractPackagePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, RightsContractPackage> implements HollowUniqueKeyIndex<RightsContractPackage> {

    public RightsContractPackagePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RightsContractPackagePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RightsContractPackage")).getPrimaryKey().getFieldPaths());
    }

    public RightsContractPackagePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsContractPackagePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RightsContractPackage", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RightsContractPackage findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsContractPackage(ordinal);
    }

}