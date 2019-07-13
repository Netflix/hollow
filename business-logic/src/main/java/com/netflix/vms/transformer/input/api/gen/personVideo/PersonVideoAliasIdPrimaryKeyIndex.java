package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PersonVideoAliasId, K> uki = UniqueKeyIndex.from(consumer, PersonVideoAliasId.class)
 *         .usingBean(k);
 *     PersonVideoAliasId m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PersonVideoAliasId} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PersonVideoAliasIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<PersonVideoAPI, PersonVideoAliasId> implements HollowUniqueKeyIndex<PersonVideoAliasId> {

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PersonVideoAliasId")).getPrimaryKey().getFieldPaths());
    }

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonVideoAliasId", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PersonVideoAliasId findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonVideoAliasId(ordinal);
    }

}