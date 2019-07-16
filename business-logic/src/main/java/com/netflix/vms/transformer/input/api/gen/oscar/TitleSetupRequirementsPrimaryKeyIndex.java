package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<TitleSetupRequirements, K> uki = UniqueKeyIndex.from(consumer, TitleSetupRequirements.class)
 *         .usingBean(k);
 *     TitleSetupRequirements m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code TitleSetupRequirements} object.
 */
@Deprecated
@SuppressWarnings("all")
public class TitleSetupRequirementsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, TitleSetupRequirements> implements HollowUniqueKeyIndex<TitleSetupRequirements> {

    public TitleSetupRequirementsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public TitleSetupRequirementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("TitleSetupRequirements")).getPrimaryKey().getFieldPaths());
    }

    public TitleSetupRequirementsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TitleSetupRequirementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TitleSetupRequirements", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public TitleSetupRequirements findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTitleSetupRequirements(ordinal);
    }

}