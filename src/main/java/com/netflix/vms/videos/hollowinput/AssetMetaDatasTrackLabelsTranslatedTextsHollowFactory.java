package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class AssetMetaDatasTrackLabelsTranslatedTextsHollowFactory<T extends AssetMetaDatasTrackLabelsTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AssetMetaDatasTrackLabelsTranslatedTextsHollow(((AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AssetMetaDatasTrackLabelsTranslatedTextsHollow(new AssetMetaDatasTrackLabelsTranslatedTextsDelegateCachedImpl((AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}