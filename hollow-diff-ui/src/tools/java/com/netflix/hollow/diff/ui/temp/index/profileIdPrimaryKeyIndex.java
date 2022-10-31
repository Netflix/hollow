package com.netflix.hollow.diff.ui.temp.index;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.ProfileId;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ProfileId, K> uki = UniqueKeyIndex.from(consumer, ProfileId.class)
 *         .usingBean(k);
 *     ProfileId m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ProfileId} object.
 */
@Deprecated
@SuppressWarnings("all")
public class profileIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<MyNamespaceAPI, ProfileId> implements HollowUniqueKeyIndex<ProfileId> {

    public profileIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public profileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("profileId")).getPrimaryKey().getFieldPaths());
    }

    public profileIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public profileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "profileId", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ProfileId findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getProfileId(ordinal);
    }

}