package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class StreamProfileGroupsStreamProfileIdsHollow extends HollowObject {

    public StreamProfileGroupsStreamProfileIdsHollow(StreamProfileGroupsStreamProfileIdsDelegate delegate, int ordinal) {
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

    public StreamProfileGroupsStreamProfileIdsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamProfileGroupsStreamProfileIdsDelegate delegate() {
        return (StreamProfileGroupsStreamProfileIdsDelegate)delegate;
    }

}