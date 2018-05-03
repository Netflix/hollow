package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class AltGenresHollow extends HollowObject {

    public AltGenresHollow(AltGenresDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getAltGenreId() {
        return delegate().getAltGenreId(ordinal);
    }

    public Long _getAltGenreIdBoxed() {
        return delegate().getAltGenreIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getDisplayName() {
        int refOrdinal = delegate().getDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getShortName() {
        int refOrdinal = delegate().getShortNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public AltGenresAlternateNamesListHollow _getAlternateNames() {
        int refOrdinal = delegate().getAlternateNamesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAltGenresAlternateNamesListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public AltGenresTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AltGenresDelegate delegate() {
        return (AltGenresDelegate)delegate;
    }

}