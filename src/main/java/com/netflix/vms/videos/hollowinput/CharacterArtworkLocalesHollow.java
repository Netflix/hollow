package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkLocalesHollow extends HollowObject {

    public CharacterArtworkLocalesHollow(CharacterArtworkLocalesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CharacterArtworkLocalesArrayOfTerritoryCodesHollow _getTerritoryCodes() {
        int refOrdinal = delegate().getTerritoryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterArtworkLocalesArrayOfTerritoryCodesHollow(refOrdinal);
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

    public CharacterArtworkLocalesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterArtworkLocalesDelegate delegate() {
        return (CharacterArtworkLocalesDelegate)delegate;
    }

}