package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutCountry, K> uki = UniqueKeyIndex.from(consumer, RolloutCountry.class)
 *         .usingBean(k);
 *     RolloutCountry m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutCountry} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutCountryPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, RolloutCountry> implements HollowUniqueKeyIndex<RolloutCountry> {

    public RolloutCountryPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutCountryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutCountry")).getPrimaryKey().getFieldPaths());
    }

    public RolloutCountryPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutCountryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutCountry", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutCountry findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutCountry(ordinal);
    }

}