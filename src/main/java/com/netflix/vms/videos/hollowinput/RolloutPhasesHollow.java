package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesHollow extends HollowObject {

    public RolloutPhasesHollow(RolloutPhasesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSeasonMovieId() {
        return delegate().getSeasonMovieId(ordinal);
    }

    public Long _getSeasonMovieIdBoxed() {
        return delegate().getSeasonMovieIdBoxed(ordinal);
    }

    public RolloutPhasesElementsHollow _getElements() {
        int refOrdinal = delegate().getElementsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsHollow(refOrdinal);
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

    public RolloutPhasesMapOfWindowsHollow _getWindows() {
        int refOrdinal = delegate().getWindowsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesMapOfWindowsHollow(refOrdinal);
    }

    public StringHollow _getPhaseType() {
        int refOrdinal = delegate().getPhaseTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesDelegate delegate() {
        return (RolloutPhasesDelegate)delegate;
    }

}