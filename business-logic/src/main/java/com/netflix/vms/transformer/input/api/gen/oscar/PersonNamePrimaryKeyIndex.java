package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PersonName, K> uki = UniqueKeyIndex.from(consumer, PersonName.class)
 *         .usingBean(k);
 *     PersonName m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PersonName} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PersonNamePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PersonName> implements HollowUniqueKeyIndex<PersonName> {

    public PersonNamePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PersonNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PersonName")).getPrimaryKey().getFieldPaths());
    }

    public PersonNamePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonName", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PersonName findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonName(ordinal);
    }

}