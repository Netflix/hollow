package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutHollow extends HollowObject {

    public RolloutHollow(RolloutDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getRolloutName() {
        int refOrdinal = delegate().getRolloutNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public RolloutMapOfLaunchDatesHollow _getLaunchDates() {
        int refOrdinal = delegate().getLaunchDatesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutMapOfLaunchDatesHollow(refOrdinal);
    }

    public long _getRolloutId() {
        return delegate().getRolloutId(ordinal);
    }

    public Long _getRolloutIdBoxed() {
        return delegate().getRolloutIdBoxed(ordinal);
    }

    public StringHollow _getRolloutType() {
        int refOrdinal = delegate().getRolloutTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public RolloutArrayOfPhasesHollow _getPhases() {
        int refOrdinal = delegate().getPhasesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutArrayOfPhasesHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutDelegate delegate() {
        return (RolloutDelegate)delegate;
    }

}