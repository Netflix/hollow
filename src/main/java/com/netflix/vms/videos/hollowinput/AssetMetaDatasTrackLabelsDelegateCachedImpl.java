package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class AssetMetaDatasTrackLabelsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AssetMetaDatasTrackLabelsDelegate {

    private final int translatedTextsOrdinal;
   private AssetMetaDatasTrackLabelsTypeAPI typeAPI;

    public AssetMetaDatasTrackLabelsDelegateCachedImpl(AssetMetaDatasTrackLabelsTypeAPI typeAPI, int ordinal) {
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return translatedTextsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AssetMetaDatasTrackLabelsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AssetMetaDatasTrackLabelsTypeAPI) typeAPI;
    }

}