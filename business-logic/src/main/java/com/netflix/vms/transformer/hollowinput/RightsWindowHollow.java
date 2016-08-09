package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
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

    public ListOfRightsWindowContractHollow _getContractIdsExt() {
        int refOrdinal = delegate().getContractIdsExtOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsWindowContractHollow(refOrdinal);
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