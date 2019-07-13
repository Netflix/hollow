package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoDate extends HollowObject {

    public VideoDate(VideoDateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoDateWindowList getWindow() {
        int refOrdinal = delegate().getWindowOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoDateWindowList(refOrdinal);
    }

    public VideoDateAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDateDelegate delegate() {
        return (VideoDateDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code VideoDate} that has a primary key.
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
    public static UniqueKeyIndex<VideoDate, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, VideoDate.class)
            .bindToPrimaryKey()
            .usingPath("videoId", long.class);
    }

}