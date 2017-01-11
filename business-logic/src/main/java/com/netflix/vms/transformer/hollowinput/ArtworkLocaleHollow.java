package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ArtworkLocaleHollow extends HollowObject {

    public ArtworkLocaleHollow(ArtworkLocaleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public LocaleTerritoryCodeListHollow _getTerritoryCodes() {
        int refOrdinal = delegate().getTerritoryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getLocaleTerritoryCodeListHollow(refOrdinal);
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtworkLocaleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ArtworkLocaleDelegate delegate() {
        return (ArtworkLocaleDelegate)delegate;
    }

}