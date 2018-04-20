package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class PhaseTagHollow extends HollowObject {

    public PhaseTagHollow(PhaseTagDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getPhaseTag() {
        int refOrdinal = delegate().getPhaseTagOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getScheduleId() {
        int refOrdinal = delegate().getScheduleIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PhaseTagTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PhaseTagDelegate delegate() {
        return (PhaseTagDelegate)delegate;
    }

}