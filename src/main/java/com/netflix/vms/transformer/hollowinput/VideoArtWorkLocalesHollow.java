package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkLocalesHollow extends HollowObject {

    public VideoArtWorkLocalesHollow(VideoArtWorkLocalesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoArtWorkLocalesArrayOfTerritoryCodesHollow _getTerritoryCodes() {
        int refOrdinal = delegate().getTerritoryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkLocalesArrayOfTerritoryCodesHollow(refOrdinal);
    }

    public StringHollow _getBcp47Code() {
        int refOrdinal = delegate().getBcp47CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public DateHollow _getEffectiveDate() {
        int refOrdinal = delegate().getEffectiveDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkLocalesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkLocalesDelegate delegate() {
        return (VideoArtWorkLocalesDelegate)delegate;
    }

}