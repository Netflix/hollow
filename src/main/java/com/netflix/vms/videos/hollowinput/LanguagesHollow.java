package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

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

    public LanguagesNameHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getLanguagesNameHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public LanguagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LanguagesDelegate delegate() {
        return (LanguagesDelegate)delegate;
    }

}