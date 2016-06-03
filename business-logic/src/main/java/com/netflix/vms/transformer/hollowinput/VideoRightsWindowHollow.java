package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsWindowHollow extends HollowObject {

    public VideoRightsWindowHollow(VideoRightsWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public DateHollow _getContractWindowStartDate() {
        int refOrdinal = delegate().getContractWindowStartDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public boolean _getOnHold() {
        return delegate().getOnHold(ordinal);
    }

    public Boolean _getOnHoldBoxed() {
        return delegate().getOnHoldBoxed(ordinal);
    }

    public DateHollow _getEndDate() {
        int refOrdinal = delegate().getEndDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public DateHollow _getLastUpdateTs() {
        int refOrdinal = delegate().getLastUpdateTsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public VideoRightsWindowContractIdListHollow _getContractIds() {
        int refOrdinal = delegate().getContractIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsWindowContractIdListHollow(refOrdinal);
    }

    public DateHollow _getContractWindowEndDate() {
        int refOrdinal = delegate().getContractWindowEndDateOrdinal(ordinal);
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsWindowDelegate delegate() {
        return (VideoRightsWindowDelegate)delegate;
    }

}