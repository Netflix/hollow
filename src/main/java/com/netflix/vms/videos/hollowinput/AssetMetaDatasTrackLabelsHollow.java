package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AssetMetaDatasTrackLabelsHollow extends HollowObject {

    public AssetMetaDatasTrackLabelsHollow(AssetMetaDatasTrackLabelsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public AssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAssetMetaDatasTrackLabelsMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AssetMetaDatasTrackLabelsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AssetMetaDatasTrackLabelsDelegate delegate() {
        return (AssetMetaDatasTrackLabelsDelegate)delegate;
    }

}