package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsHollow extends HollowObject {

    public FestivalsHollow(FestivalsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public FestivalsCopyrightHollow _getCopyright() {
        int refOrdinal = delegate().getCopyrightOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsCopyrightHollow(refOrdinal);
    }

    public long _getFestivalId() {
        return delegate().getFestivalId(ordinal);
    }

    public Long _getFestivalIdBoxed() {
        return delegate().getFestivalIdBoxed(ordinal);
    }

    public FestivalsFestivalNameHollow _getFestivalName() {
        int refOrdinal = delegate().getFestivalNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsFestivalNameHollow(refOrdinal);
    }

    public FestivalsDescriptionHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsDescriptionHollow(refOrdinal);
    }

    public FestivalsShortNameHollow _getShortName() {
        int refOrdinal = delegate().getShortNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsShortNameHollow(refOrdinal);
    }

    public FestivalsSingularNameHollow _getSingularName() {
        int refOrdinal = delegate().getSingularNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsSingularNameHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsDelegate delegate() {
        return (FestivalsDelegate)delegate;
    }

}