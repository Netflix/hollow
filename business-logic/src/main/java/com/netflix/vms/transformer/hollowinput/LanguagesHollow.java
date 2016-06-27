package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class LanguagesHollow extends HollowObject {

    public LanguagesHollow(LanguagesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getLanguageId() {
        return delegate().getLanguageId(ordinal);
    }

    public Long _getLanguageIdBoxed() {
        return delegate().getLanguageIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public LanguagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LanguagesDelegate delegate() {
        return (LanguagesDelegate)delegate;
    }

}