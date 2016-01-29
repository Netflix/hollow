package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsRightsWindowsContractIdsHollow extends HollowObject {

    public VideoRightsRightsWindowsContractIdsHollow(VideoRightsRightsWindowsContractIdsDelegate delegate, int ordinal) {
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

    public VideoRightsRightsWindowsContractIdsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsRightsWindowsContractIdsDelegate delegate() {
        return (VideoRightsRightsWindowsContractIdsDelegate)delegate;
    }

}