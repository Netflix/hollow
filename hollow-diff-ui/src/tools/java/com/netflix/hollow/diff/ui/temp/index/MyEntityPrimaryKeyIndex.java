package com.netflix.hollow.diff.ui.temp.index;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.MyEntity;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MyEntity, K> uki = UniqueKeyIndex.from(consumer, MyEntity.class)
 *         .usingBean(k);
 *     MyEntity m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MyEntity} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MyEntityPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<MyNamespaceAPI, MyEntity> implements HollowUniqueKeyIndex<MyEntity> {

    public MyEntityPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MyEntityPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MyEntity")).getPrimaryKey().getFieldPaths());
    }

    public MyEntityPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MyEntityPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MyEntity", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MyEntity findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMyEntity(ordinal);
    }

}