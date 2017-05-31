package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsAssetHollow extends HollowObject {

    public RightsAssetHollow(RightsAssetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getBcp47Code() {
        int refOrdinal = delegate().getBcp47CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getAssetType() {
        int refOrdinal = delegate().getAssetTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsAssetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsAssetDelegate delegate() {
        return (RightsAssetDelegate)delegate;
    }

}