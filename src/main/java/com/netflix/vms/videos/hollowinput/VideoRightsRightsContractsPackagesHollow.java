package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsRightsContractsPackagesHollow extends HollowObject {

    public VideoRightsRightsContractsPackagesHollow(VideoRightsRightsContractsPackagesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long _getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public boolean _getPrimary() {
        return delegate().getPrimary(ordinal);
    }

    public Boolean _getPrimaryBoxed() {
        return delegate().getPrimaryBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsContractsPackagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsRightsContractsPackagesDelegate delegate() {
        return (VideoRightsRightsContractsPackagesDelegate)delegate;
    }

}