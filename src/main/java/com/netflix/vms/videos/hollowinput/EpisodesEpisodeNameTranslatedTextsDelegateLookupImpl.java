package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class EpisodesEpisodeNameTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements EpisodesEpisodeNameTranslatedTextsDelegate {

    private final EpisodesEpisodeNameTranslatedTextsTypeAPI typeAPI;

    public EpisodesEpisodeNameTranslatedTextsDelegateLookupImpl(EpisodesEpisodeNameTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public EpisodesEpisodeNameTranslatedTextsTypeAPI getTypeAPI() {
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