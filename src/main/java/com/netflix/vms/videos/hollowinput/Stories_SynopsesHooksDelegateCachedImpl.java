package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class Stories_SynopsesHooksDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, Stories_SynopsesHooksDelegate {

    private final int translatedTextsOrdinal;
    private final int rankOrdinal;
    private final int typeOrdinal;
   private Stories_SynopsesHooksTypeAPI typeAPI;

    public Stories_SynopsesHooksDelegateCachedImpl(Stories_SynopsesHooksTypeAPI typeAPI, int ordinal) {
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.rankOrdinal = typeAPI.getRankOrdinal(ordinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return translatedTextsOrdinal;
    }

    public int getRankOrdinal(int ordinal) {
        return rankOrdinal;
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public Stories_SynopsesHooksTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (Stories_SynopsesHooksTypeAPI) typeAPI;
    }

}