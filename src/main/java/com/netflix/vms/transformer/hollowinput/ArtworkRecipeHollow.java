package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ArtworkRecipeHollow extends HollowObject {

    public ArtworkRecipeHollow(ArtworkRecipeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getRecipeName() {
        int refOrdinal = delegate().getRecipeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCdnFolder() {
        int refOrdinal = delegate().getCdnFolderOrdinal(ordinal);
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtworkRecipeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ArtworkRecipeDelegate delegate() {
        return (ArtworkRecipeDelegate)delegate;
    }

}