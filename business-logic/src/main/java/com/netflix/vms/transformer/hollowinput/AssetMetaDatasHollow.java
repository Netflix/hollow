package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AssetMetaDatasHollow extends HollowObject {

    public AssetMetaDatasHollow(AssetMetaDatasDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getAssetId() {
        int refOrdinal = delegate().getAssetIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public TranslatedTextHollow _getTrackLabels() {
        int refOrdinal = delegate().getTrackLabelsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public AssetMetaDatasTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AssetMetaDatasDelegate delegate() {
        return (AssetMetaDatasDelegate)delegate;
    }

}