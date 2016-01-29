package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class LocalizedMetadataHollow extends HollowObject {

    public LocalizedMetadataHollow(LocalizedMetadataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public LocalizedMetadataMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getLocalizedMetadataMapOfTranslatedTextsHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public LocalizedMetadataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LocalizedMetadataDelegate delegate() {
        return (LocalizedMetadataDelegate)delegate;
    }

}