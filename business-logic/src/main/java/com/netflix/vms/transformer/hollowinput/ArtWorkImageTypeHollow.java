package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ArtWorkImageTypeHollow extends HollowObject {

    public ArtWorkImageTypeHollow(ArtWorkImageTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getImageType() {
        int refOrdinal = delegate().getImageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getExtension() {
        int refOrdinal = delegate().getExtensionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getRecipe() {
        int refOrdinal = delegate().getRecipeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtWorkImageTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ArtWorkImageTypeDelegate delegate() {
        return (ArtWorkImageTypeDelegate)delegate;
    }

}