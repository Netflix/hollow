package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingRatingHollow extends HollowObject {

    public VideoRatingRatingHollow(VideoRatingRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoRatingRatingReasonHollow _getReason() {
        int refOrdinal = delegate().getReasonOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRatingRatingReasonHollow(refOrdinal);
    }

    public long _getRatingId() {
        return delegate().getRatingId(ordinal);
    }

    public Long _getRatingIdBoxed() {
        return delegate().getRatingIdBoxed(ordinal);
    }

    public long _getCertificationSystemId() {
        return delegate().getCertificationSystemId(ordinal);
    }

    public Long _getCertificationSystemIdBoxed() {
        return delegate().getCertificationSystemIdBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRatingRatingDelegate delegate() {
        return (VideoRatingRatingDelegate)delegate;
    }

}