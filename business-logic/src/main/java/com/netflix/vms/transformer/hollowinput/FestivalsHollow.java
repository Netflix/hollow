package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FestivalsHollow extends HollowObject {

    public FestivalsHollow(FestivalsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getFestivalId() {
        return delegate().getFestivalId(ordinal);
    }

    public Long _getFestivalIdBoxed() {
        return delegate().getFestivalIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getCopyright() {
        int refOrdinal = delegate().getCopyrightOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getFestivalName() {
        int refOrdinal = delegate().getFestivalNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
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

    public TranslatedTextHollow _getSingularName() {
        int refOrdinal = delegate().getSingularNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsDelegate delegate() {
        return (FestivalsDelegate)delegate;
    }

}