package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class RolloutPhaseHollow extends HollowObject {

    public RolloutPhaseHollow(RolloutPhaseDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSeasonMovieId() {
        return delegate().getSeasonMovieId(ordinal);
    }

    public Long _getSeasonMovieIdBoxed() {
        return delegate().getSeasonMovieIdBoxed(ordinal);
    }

    public RolloutPhaseElementsHollow _getElements() {
        int refOrdinal = delegate().getElementsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseElementsHollow(refOrdinal);
    }

    public StringHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getShowCoreMetadata() {
        return delegate().getShowCoreMetadata(ordinal);
    }

    public Boolean _getShowCoreMetadataBoxed() {
        return delegate().getShowCoreMetadataBoxed(ordinal);
    }

    public RolloutPhaseWindowMapHollow _getWindows() {
        int refOrdinal = delegate().getWindowsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseWindowMapHollow(refOrdinal);
    }

    public StringHollow _getPhaseType() {
        int refOrdinal = delegate().getPhaseTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getOnHold() {
        return delegate().getOnHold(ordinal);
    }

    public Boolean _getOnHoldBoxed() {
        return delegate().getOnHoldBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseDelegate delegate() {
        return (RolloutPhaseDelegate)delegate;
    }

}