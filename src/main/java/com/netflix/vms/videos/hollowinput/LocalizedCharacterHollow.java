package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class LocalizedCharacterHollow extends HollowObject {

    public LocalizedCharacterHollow(LocalizedCharacterDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Long _getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public LocalizedCharacterMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getLocalizedCharacterMapOfTranslatedTextsHollow(refOrdinal);
    }

    public StringHollow _getAttributeName() {
        int refOrdinal = delegate().getAttributeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getLabel() {
        int refOrdinal = delegate().getLabelOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getCharacterId() {
        return delegate().getCharacterId(ordinal);
    }

    public Long _getCharacterIdBoxed() {
        return delegate().getCharacterIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public LocalizedCharacterTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LocalizedCharacterDelegate delegate() {
        return (LocalizedCharacterDelegate)delegate;
    }

}