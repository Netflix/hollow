package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class MasterScheduleHollow extends HollowObject {

    public MasterScheduleHollow(MasterScheduleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getMovieType() {
        return delegate().getMovieType(ordinal);
    }

    public boolean _isMovieTypeEqual(String testValue) {
        return delegate().isMovieTypeEqual(ordinal, testValue);
    }

    public long _getVersionId() {
        return delegate().getVersionId(ordinal);
    }

    public Long _getVersionIdBoxed() {
        return delegate().getVersionIdBoxed(ordinal);
    }

    public String _getScheduleId() {
        return delegate().getScheduleId(ordinal);
    }

    public boolean _isScheduleIdEqual(String testValue) {
        return delegate().isScheduleIdEqual(ordinal, testValue);
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

    public MasterScheduleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MasterScheduleDelegate delegate() {
        return (MasterScheduleDelegate)delegate;
    }

}