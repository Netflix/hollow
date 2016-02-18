package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseWindowHollow extends HollowObject {

    public RolloutPhaseWindowHollow(RolloutPhaseWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public DateHollow _getEndDate() {
        int refOrdinal = delegate().getEndDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public DateHollow _getStartDate() {
        int refOrdinal = delegate().getStartDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseWindowDelegate delegate() {
        return (RolloutPhaseWindowDelegate)delegate;
    }

}