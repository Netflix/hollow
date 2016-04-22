package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoIdHollow extends HollowObject {

    public VideoIdHollow(VideoIdDelegate delegate, int ordinal) {
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

    public VideoIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoIdDelegate delegate() {
        return (VideoIdDelegate)delegate;
    }

}