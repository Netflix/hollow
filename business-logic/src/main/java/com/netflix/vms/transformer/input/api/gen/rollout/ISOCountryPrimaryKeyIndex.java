package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ISOCountry, K> uki = UniqueKeyIndex.from(consumer, ISOCountry.class)
 *         .usingBean(k);
 *     ISOCountry m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ISOCountry} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ISOCountryPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<RolloutAPI, ISOCountry> implements HollowUniqueKeyIndex<ISOCountry> {

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ISOCountry")).getPrimaryKey().getFieldPaths());
    }

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ISOCountry", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ISOCountry findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getISOCountry(ordinal);
    }

}