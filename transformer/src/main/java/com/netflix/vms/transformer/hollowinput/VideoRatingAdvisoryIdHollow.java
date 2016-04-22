package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRatingAdvisoryIdHollow extends HollowObject {

    public VideoRatingAdvisoryIdHollow(VideoRatingAdvisoryIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getValue() {
        return delegate().getValue(ordinal);
    }

    public Long _getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingAdvisoryIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRatingAdvisoryIdDelegate delegate() {
        return (VideoRatingAdvisoryIdDelegate)delegate;
    }

}