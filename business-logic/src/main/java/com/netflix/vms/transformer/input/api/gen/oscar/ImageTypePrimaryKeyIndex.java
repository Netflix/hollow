package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ImageType, K> uki = UniqueKeyIndex.from(consumer, ImageType.class)
 *         .usingBean(k);
 *     ImageType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ImageType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ImageTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, ImageType> implements HollowUniqueKeyIndex<ImageType> {

    public ImageTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ImageTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ImageType")).getPrimaryKey().getFieldPaths());
    }

    public ImageTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ImageTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ImageType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ImageType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getImageType(ordinal);
    }

}