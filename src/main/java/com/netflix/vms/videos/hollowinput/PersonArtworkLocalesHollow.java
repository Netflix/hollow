package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkLocalesHollow extends HollowObject {

    public PersonArtworkLocalesHollow(PersonArtworkLocalesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PersonArtworkLocalesArrayOfTerritoryCodesHollow _getTerritoryCodes() {
        int refOrdinal = delegate().getTerritoryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonArtworkLocalesArrayOfTerritoryCodesHollow(refOrdinal);
    }

    public StringHollow _getBcp47Code() {
        int refOrdinal = delegate().getBcp47CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getEffectiveDate() {
        return delegate().getEffectiveDate(ordinal);
    }

    public Long _getEffectiveDateBoxed() {
        return delegate().getEffectiveDateBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkLocalesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonArtworkLocalesDelegate delegate() {
        return (PersonArtworkLocalesDelegate)delegate;
    }

}