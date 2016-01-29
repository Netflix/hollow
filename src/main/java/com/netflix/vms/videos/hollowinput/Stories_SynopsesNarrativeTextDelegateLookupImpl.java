package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesNarrativeTextDelegateLookupImpl extends HollowObjectAbstractDelegate implements Stories_SynopsesNarrativeTextDelegate {

    private final Stories_SynopsesNarrativeTextTypeAPI typeAPI;

    public Stories_SynopsesNarrativeTextDelegateLookupImpl(Stories_SynopsesNarrativeTextTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public Stories_SynopsesNarrativeTextTypeAPI getTypeAPI() {
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