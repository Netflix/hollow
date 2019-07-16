package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PersonId, K> uki = UniqueKeyIndex.from(consumer, PersonId.class)
 *         .usingBean(k);
 *     PersonId m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PersonId} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PersonIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PersonId> implements HollowUniqueKeyIndex<PersonId> {

    public PersonIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PersonIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PersonId")).getPrimaryKey().getFieldPaths());
    }

    public PersonIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonId", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PersonId findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonId(ordinal);
    }

}