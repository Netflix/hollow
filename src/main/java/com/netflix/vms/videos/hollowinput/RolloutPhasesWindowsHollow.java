package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesWindowsHollow extends HollowObject {

    public RolloutPhasesWindowsHollow(RolloutPhasesWindowsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getEndDate() {
        return delegate().getEndDate(ordinal);
    }

    public Long _getEndDateBoxed() {
        return delegate().getEndDateBoxed(ordinal);
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

    public RolloutPhasesWindowsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesWindowsDelegate delegate() {
        return (RolloutPhasesWindowsDelegate)delegate;
    }

}