package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsDelegate {

    private final ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI typeAPI;

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsDelegateLookupImpl(ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI getTypeAPI() {
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