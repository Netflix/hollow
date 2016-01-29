package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesHooksTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements Stories_SynopsesHooksTranslatedTextsDelegate {

    private final Stories_SynopsesHooksTranslatedTextsTypeAPI typeAPI;

    public Stories_SynopsesHooksTranslatedTextsDelegateLookupImpl(Stories_SynopsesHooksTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public Stories_SynopsesHooksTranslatedTextsTypeAPI getTypeAPI() {
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