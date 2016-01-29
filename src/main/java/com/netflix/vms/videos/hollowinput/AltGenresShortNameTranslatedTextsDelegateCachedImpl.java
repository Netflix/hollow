package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class AltGenresShortNameTranslatedTextsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AltGenresShortNameTranslatedTextsDelegate {

    private final int valueOrdinal;
   private AltGenresShortNameTranslatedTextsTypeAPI typeAPI;

    public AltGenresShortNameTranslatedTextsDelegateCachedImpl(AltGenresShortNameTranslatedTextsTypeAPI typeAPI, int ordinal) {
        this.valueOrdinal = typeAPI.getValueOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return valueOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AltGenresShortNameTranslatedTextsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AltGenresShortNameTranslatedTextsTypeAPI) typeAPI;
    }

}