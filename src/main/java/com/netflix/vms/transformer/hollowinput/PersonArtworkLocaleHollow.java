package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkLocaleHollow extends HollowObject {

    public PersonArtworkLocaleHollow(PersonArtworkLocaleDelegate delegate, int ordinal) {
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkLocaleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonArtworkLocaleDelegate delegate() {
        return (PersonArtworkLocaleDelegate)delegate;
    }

}