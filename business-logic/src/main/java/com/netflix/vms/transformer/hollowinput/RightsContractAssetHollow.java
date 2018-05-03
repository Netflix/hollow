package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class RightsContractAssetHollow extends HollowObject {

    public RightsContractAssetHollow(RightsContractAssetDelegate delegate, int ordinal) {
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

    public RightsContractAssetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsContractAssetDelegate delegate() {
        return (RightsContractAssetDelegate)delegate;
    }

}