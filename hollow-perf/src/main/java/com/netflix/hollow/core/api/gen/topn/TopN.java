package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class TopN extends HollowObject {

    public TopN(TopNDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public SetOfTopNAttribute getAttributes() {
        int refOrdinal = delegate().getAttributesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfTopNAttribute(refOrdinal);
    }

    public TopNAPI api() {
        return typeApi().getAPI();
    }

    public TopNTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TopNDelegate delegate() {
        return (TopNDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code TopN} that has a primary key.
     * The primary key is represented by the type {@code long}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<TopN, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, TopN.class)
            .bindToPrimaryKey()
            .usingPath("videoId", long.class);
    }

}