package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AssetMetaDatasTrackLabelsTranslatedTextsHollow extends HollowObject {

    public AssetMetaDatasTrackLabelsTranslatedTextsHollow(AssetMetaDatasTrackLabelsTranslatedTextsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AssetMetaDatasTrackLabelsTranslatedTextsDelegate delegate() {
        return (AssetMetaDatasTrackLabelsTranslatedTextsDelegate)delegate;
    }

}