package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class Stories_SynopsesNarrativeTextDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, Stories_SynopsesNarrativeTextDelegate {

    private final int translatedTextsOrdinal;
   private Stories_SynopsesNarrativeTextTypeAPI typeAPI;

    public Stories_SynopsesNarrativeTextDelegateCachedImpl(Stories_SynopsesNarrativeTextTypeAPI typeAPI, int ordinal) {
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

    public Stories_SynopsesNarrativeTextTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (Stories_SynopsesNarrativeTextTypeAPI) typeAPI;
    }

}