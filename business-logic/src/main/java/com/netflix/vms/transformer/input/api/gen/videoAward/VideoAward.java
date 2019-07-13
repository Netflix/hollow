package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoAward extends HollowObject {

    public VideoAward(VideoAwardDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoAwardList getAward() {
        int refOrdinal = delegate().getAwardOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoAwardList(refOrdinal);
    }

    public VideoAwardAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoAwardDelegate delegate() {
        return (VideoAwardDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code VideoAward} that has a primary key.
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
    public static UniqueKeyIndex<VideoAward, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, VideoAward.class)
            .bindToPrimaryKey()
            .usingPath("videoId", long.class);
    }

}