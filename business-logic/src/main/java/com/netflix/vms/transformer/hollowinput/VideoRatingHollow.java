package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class VideoRatingHollow extends HollowObject {

    public VideoRatingHollow(VideoRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoRatingArrayOfRatingHollow _getRating() {
        int refOrdinal = delegate().getRatingOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRatingArrayOfRatingHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRatingDelegate delegate() {
        return (VideoRatingDelegate)delegate;
    }

}