package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsContractPackageHollow extends HollowObject {

    public VideoRightsContractPackageHollow(VideoRightsContractPackageDelegate delegate, int ordinal) {
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsContractPackageTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsContractPackageDelegate delegate() {
        return (VideoRightsContractPackageDelegate)delegate;
    }

}