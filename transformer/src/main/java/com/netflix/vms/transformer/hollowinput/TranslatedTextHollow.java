package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class TranslatedTextHollow extends HollowObject {

    public TranslatedTextHollow(TranslatedTextDelegate delegate, int ordinal) {
        super(delegate, ordinal);
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

    public TranslatedTextTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TranslatedTextDelegate delegate() {
        return (TranslatedTextDelegate)delegate;
    }

}