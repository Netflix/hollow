package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PersonVideo, K> uki = UniqueKeyIndex.from(consumer, PersonVideo.class)
 *         .usingBean(k);
 *     PersonVideo m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PersonVideo} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PersonVideoPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<PersonVideoAPI, PersonVideo> implements HollowUniqueKeyIndex<PersonVideo> {

    public PersonVideoPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PersonVideoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PersonVideo")).getPrimaryKey().getFieldPaths());
    }

    public PersonVideoPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonVideoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonVideo", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PersonVideo findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonVideo(ordinal);
    }

}