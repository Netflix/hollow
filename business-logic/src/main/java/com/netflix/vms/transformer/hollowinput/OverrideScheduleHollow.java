package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

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

    public OverrideScheduleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected OverrideScheduleDelegate delegate() {
        return (OverrideScheduleDelegate)delegate;
    }

}