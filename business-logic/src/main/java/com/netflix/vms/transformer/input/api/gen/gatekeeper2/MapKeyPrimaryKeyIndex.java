package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MapKey, K> uki = UniqueKeyIndex.from(consumer, MapKey.class)
 *         .usingBean(k);
 *     MapKey m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MapKey} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MapKeyPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, MapKey> implements HollowUniqueKeyIndex<MapKey> {

    public MapKeyPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MapKeyPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MapKey")).getPrimaryKey().getFieldPaths());
    }

    public MapKeyPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MapKeyPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MapKey", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MapKey findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMapKey(ordinal);
    }

}