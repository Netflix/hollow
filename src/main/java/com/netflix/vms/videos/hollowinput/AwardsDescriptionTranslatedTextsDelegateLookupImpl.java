package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class AwardsDescriptionTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements AwardsDescriptionTranslatedTextsDelegate {

    private final AwardsDescriptionTranslatedTextsTypeAPI typeAPI;

    public AwardsDescriptionTranslatedTextsDelegateLookupImpl(AwardsDescriptionTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public AwardsDescriptionTranslatedTextsTypeAPI getTypeAPI() {
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