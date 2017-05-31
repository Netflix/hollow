package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsAssetsHollow extends HollowObject {

    public RightsAssetsHollow(RightsAssetsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RightsAssetSetIdHollow _getAssetSetId() {
        int refOrdinal = delegate().getAssetSetIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRightsAssetSetIdHollow(refOrdinal);
    }

    public SetOfRightsAssetHollow _getAssets() {
        int refOrdinal = delegate().getAssetsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfRightsAssetHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsAssetsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsAssetsDelegate delegate() {
        return (RightsAssetsDelegate)delegate;
    }

}