package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class AssetMetaDatasTrackLabelsTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements AssetMetaDatasTrackLabelsTranslatedTextsDelegate {

    private final AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI typeAPI;

    public AssetMetaDatasTrackLabelsTranslatedTextsDelegateLookupImpl(AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public AssetMetaDatasTrackLabelsTranslatedTextsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}