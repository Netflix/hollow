package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsRightsWindowsHollow extends HollowObject {

    public VideoRightsRightsWindowsHollow(VideoRightsRightsWindowsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getContractWindowStartDate() {
        return delegate().getContractWindowStartDate(ordinal);
    }

    public Long _getContractWindowStartDateBoxed() {
        return delegate().getContractWindowStartDateBoxed(ordinal);
    }

    public boolean _getOnHold() {
        return delegate().getOnHold(ordinal);
    }

    public Boolean _getOnHoldBoxed() {
        return delegate().getOnHoldBoxed(ordinal);
    }

    public long _getEndDate() {
        return delegate().getEndDate(ordinal);
    }

    public Long _getEndDateBoxed() {
        return delegate().getEndDateBoxed(ordinal);
    }

    public long _getLastUpdateTs() {
        return delegate().getLastUpdateTs(ordinal);
    }

    public Long _getLastUpdateTsBoxed() {
        return delegate().getLastUpdateTsBoxed(ordinal);
    }

    public VideoRightsRightsWindowsArrayOfContractIdsHollow _getContractIds() {
        int refOrdinal = delegate().getContractIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsRightsWindowsArrayOfContractIdsHollow(refOrdinal);
    }

    public long _getContractWindowEndDate() {
        return delegate().getContractWindowEndDate(ordinal);
    }

    public Long _getContractWindowEndDateBoxed() {
        return delegate().getContractWindowEndDateBoxed(ordinal);
    }

    public long _getStartDate() {
        return delegate().getStartDate(ordinal);
    }

    public Long _getStartDateBoxed() {
        return delegate().getStartDateBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsWindowsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsRightsWindowsDelegate delegate() {
        return (VideoRightsRightsWindowsDelegate)delegate;
    }

}