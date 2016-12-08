package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class OverrideScheduleHollow extends HollowObject {

    public OverrideScheduleHollow(OverrideScheduleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public String _getPhaseTag() {
        return delegate().getPhaseTag(ordinal);
    }

    public boolean _isPhaseTagEqual(String testValue) {
        return delegate().isPhaseTagEqual(ordinal, testValue);
    }

    public long _getAvailabilityOffset() {
        return delegate().getAvailabilityOffset(ordinal);
    }

    public Long _getAvailabilityOffsetBoxed() {
        return delegate().getAvailabilityOffsetBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public OverrideScheduleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected OverrideScheduleDelegate delegate() {
        return (OverrideScheduleDelegate)delegate;
    }

}