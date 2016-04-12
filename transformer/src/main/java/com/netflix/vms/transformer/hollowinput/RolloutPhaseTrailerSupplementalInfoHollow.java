package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseTrailerSupplementalInfoHollow extends HollowObject {

    public RolloutPhaseTrailerSupplementalInfoHollow(RolloutPhaseTrailerSupplementalInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getImageBackgroundTone() {
        int refOrdinal = delegate().getImageBackgroundToneOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getVideoLength() {
        return delegate().getVideoLength(ordinal);
    }

    public Long _getVideoLengthBoxed() {
        return delegate().getVideoLengthBoxed(ordinal);
    }

    public StringHollow _getSubtitleLocale() {
        int refOrdinal = delegate().getSubtitleLocaleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getSeasonNumber() {
        return delegate().getSeasonNumber(ordinal);
    }

    public Long _getSeasonNumberBoxed() {
        return delegate().getSeasonNumberBoxed(ordinal);
    }

    public StringHollow _getVideo() {
        int refOrdinal = delegate().getVideoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getImageTag() {
        int refOrdinal = delegate().getImageTagOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getVideoValue() {
        int refOrdinal = delegate().getVideoValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getPriority() {
        return delegate().getPriority(ordinal);
    }

    public Long _getPriorityBoxed() {
        return delegate().getPriorityBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseTrailerSupplementalInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseTrailerSupplementalInfoDelegate delegate() {
        return (RolloutPhaseTrailerSupplementalInfoDelegate)delegate;
    }

}