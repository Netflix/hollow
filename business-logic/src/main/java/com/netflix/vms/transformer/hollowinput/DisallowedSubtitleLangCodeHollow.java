package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class DisallowedSubtitleLangCodeHollow extends HollowObject {

    public DisallowedSubtitleLangCodeHollow(DisallowedSubtitleLangCodeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DisallowedSubtitleLangCodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DisallowedSubtitleLangCodeDelegate delegate() {
        return (DisallowedSubtitleLangCodeDelegate)delegate;
    }

}