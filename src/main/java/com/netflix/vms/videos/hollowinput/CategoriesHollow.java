package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CategoriesHollow extends HollowObject {

    public CategoriesHollow(CategoriesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CategoriesDisplayNameHollow _getDisplayName() {
        int refOrdinal = delegate().getDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCategoriesDisplayNameHollow(refOrdinal);
    }

    public CategoriesShortNameHollow _getShortName() {
        int refOrdinal = delegate().getShortNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCategoriesShortNameHollow(refOrdinal);
    }

    public long _getCategoryId() {
        return delegate().getCategoryId(ordinal);
    }

    public Long _getCategoryIdBoxed() {
        return delegate().getCategoryIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoriesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoriesDelegate delegate() {
        return (CategoriesDelegate)delegate;
    }

}