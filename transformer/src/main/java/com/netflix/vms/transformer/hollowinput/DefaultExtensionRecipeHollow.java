package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class DefaultExtensionRecipeHollow extends HollowObject {

    public DefaultExtensionRecipeHollow(DefaultExtensionRecipeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getRecipeName() {
        int refOrdinal = delegate().getRecipeNameOrdinal(ordinal);
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

    public DefaultExtensionRecipeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DefaultExtensionRecipeDelegate delegate() {
        return (DefaultExtensionRecipeDelegate)delegate;
    }

}