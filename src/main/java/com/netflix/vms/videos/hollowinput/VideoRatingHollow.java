package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRatingHollow extends HollowObject {

    public VideoRatingHollow(VideoRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoRatingArrayOfRatingHollow _getRating() {
        int refOrdinal = delegate().getRatingOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRatingArrayOfRatingHollow(refOrdinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRatingDelegate delegate() {
        return (VideoRatingDelegate)delegate;
    }

}