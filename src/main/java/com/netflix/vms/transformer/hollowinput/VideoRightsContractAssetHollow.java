package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsContractAssetHollow extends HollowObject {

    public VideoRightsContractAssetHollow(VideoRightsContractAssetDelegate delegate, int ordinal) {
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsContractAssetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsContractAssetDelegate delegate() {
        return (VideoRightsContractAssetDelegate)delegate;
    }

}