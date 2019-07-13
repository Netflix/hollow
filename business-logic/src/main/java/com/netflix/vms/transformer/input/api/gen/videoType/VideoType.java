package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoType extends HollowObject {

    public VideoType(VideoTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoTypeDescriptorSet getCountryInfos() {
        int refOrdinal = delegate().getCountryInfosOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoTypeDescriptorSet(refOrdinal);
    }

    public VideoTypeAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeDelegate delegate() {
        return (VideoTypeDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code VideoType} that has a primary key.
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
    public static UniqueKeyIndex<VideoType, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, VideoType.class)
            .bindToPrimaryKey()
            .usingPath("videoId", long.class);
    }

}