package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AltGenresAlternateNamesHollow extends HollowObject {

    public AltGenresAlternateNamesHollow(AltGenresAlternateNamesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getTypeId() {
        return delegate().getTypeId(ordinal);
    }

    public Long _getTypeIdBoxed() {
        return delegate().getTypeIdBoxed(ordinal);
    }

    public StringHollow _getType() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public MapOfTranslatedTextHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public AltGenresAlternateNamesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AltGenresAlternateNamesDelegate delegate() {
        return (AltGenresAlternateNamesDelegate)delegate;
    }

}