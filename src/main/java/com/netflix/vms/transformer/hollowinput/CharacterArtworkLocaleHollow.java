package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkLocaleHollow extends HollowObject {

    public CharacterArtworkLocaleHollow(CharacterArtworkLocaleDelegate delegate, int ordinal) {
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

    public CharacterArtworkLocaleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterArtworkLocaleDelegate delegate() {
        return (CharacterArtworkLocaleDelegate)delegate;
    }

}