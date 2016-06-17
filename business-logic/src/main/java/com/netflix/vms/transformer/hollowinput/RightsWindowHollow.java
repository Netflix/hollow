package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RightsWindowHollow extends HollowObject {

    public RightsWindowHollow(RightsWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getStartDate() {
        return delegate().getStartDate(ordinal);
    }

    public Long _getStartDateBoxed() {
        return delegate().getStartDateBoxed(ordinal);
    }

    public long _getEndDate() {
        return delegate().getEndDate(ordinal);
    }

    public Long _getEndDateBoxed() {
        return delegate().getEndDateBoxed(ordinal);
    }

    public ListOfContractIdHollow _getContractIds() {
        int refOrdinal = delegate().getContractIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfContractIdHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsWindowDelegate delegate() {
        return (RightsWindowDelegate)delegate;
    }

}