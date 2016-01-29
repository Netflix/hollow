package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsFlagsFirstDisplayDatesHollow extends HollowObject {

    public VideoRightsFlagsFirstDisplayDatesHollow(VideoRightsFlagsFirstDisplayDatesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getValue() {
        return delegate().getValue(ordinal);
    }

    public Long _getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsFlagsFirstDisplayDatesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsFlagsFirstDisplayDatesDelegate delegate() {
        return (VideoRightsFlagsFirstDisplayDatesDelegate)delegate;
    }

}