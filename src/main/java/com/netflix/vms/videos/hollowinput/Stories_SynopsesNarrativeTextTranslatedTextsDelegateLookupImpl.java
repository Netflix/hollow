package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesNarrativeTextTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements Stories_SynopsesNarrativeTextTranslatedTextsDelegate {

    private final Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI typeAPI;

    public Stories_SynopsesNarrativeTextTranslatedTextsDelegateLookupImpl(Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI getTypeAPI() {
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