package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class CategoriesHollow extends HollowObject {

    public CategoriesHollow(CategoriesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCategoryId() {
        return delegate().getCategoryId(ordinal);
    }

    public Long _getCategoryIdBoxed() {
        return delegate().getCategoryIdBoxed(ordinal);
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoriesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoriesDelegate delegate() {
        return (CategoriesDelegate)delegate;
    }

}