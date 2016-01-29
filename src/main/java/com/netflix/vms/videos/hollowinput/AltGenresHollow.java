package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AltGenresHollow extends HollowObject {

    public AltGenresHollow(AltGenresDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public AltGenresArrayOfAlternateNamesHollow _getAlternateNames() {
        int refOrdinal = delegate().getAlternateNamesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAltGenresArrayOfAlternateNamesHollow(refOrdinal);
    }

    public AltGenresDisplayNameHollow _getDisplayName() {
        int refOrdinal = delegate().getDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAltGenresDisplayNameHollow(refOrdinal);
    }

    public long _getAltGenreId() {
        return delegate().getAltGenreId(ordinal);
    }

    public Long _getAltGenreIdBoxed() {
        return delegate().getAltGenreIdBoxed(ordinal);
    }

    public AltGenresShortNameHollow _getShortName() {
        int refOrdinal = delegate().getShortNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAltGenresShortNameHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AltGenresTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AltGenresDelegate delegate() {
        return (AltGenresDelegate)delegate;
    }

}