package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PersonVideoRole, K> uki = UniqueKeyIndex.from(consumer, PersonVideoRole.class)
 *         .usingBean(k);
 *     PersonVideoRole m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PersonVideoRole} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PersonVideoRolePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<PersonVideoAPI, PersonVideoRole> implements HollowUniqueKeyIndex<PersonVideoRole> {

    public PersonVideoRolePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PersonVideoRolePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PersonVideoRole")).getPrimaryKey().getFieldPaths());
    }

    public PersonVideoRolePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonVideoRolePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonVideoRole", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PersonVideoRole findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonVideoRole(ordinal);
    }

}