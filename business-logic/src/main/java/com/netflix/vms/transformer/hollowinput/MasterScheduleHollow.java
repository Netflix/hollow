package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class MasterScheduleHollow extends HollowObject {

    public MasterScheduleHollow(MasterScheduleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getMovieType() {
        int refOrdinal = delegate().getMovieTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getVersionId() {
        return delegate().getVersionId(ordinal);
    }

    public Long _getVersionIdBoxed() {
        return delegate().getVersionIdBoxed(ordinal);
    }

    public StringHollow _getScheduleId() {
        int refOrdinal = delegate().getScheduleIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getPhaseTag() {
        int refOrdinal = delegate().getPhaseTagOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
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