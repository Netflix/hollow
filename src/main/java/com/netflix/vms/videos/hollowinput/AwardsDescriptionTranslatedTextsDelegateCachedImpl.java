package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class AwardsDescriptionTranslatedTextsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AwardsDescriptionTranslatedTextsDelegate {

   private AwardsDescriptionTranslatedTextsTypeAPI typeAPI;

    public AwardsDescriptionTranslatedTextsDelegateCachedImpl(AwardsDescriptionTranslatedTextsTypeAPI typeAPI, int ordinal) {
        this.typeAPI = typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AwardsDescriptionTranslatedTextsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AwardsDescriptionTranslatedTextsTypeAPI) typeAPI;
    }

}