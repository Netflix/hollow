package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesHooksDelegateLookupImpl extends HollowObjectAbstractDelegate implements Stories_SynopsesHooksDelegate {

    private final Stories_SynopsesHooksTypeAPI typeAPI;

    public Stories_SynopsesHooksDelegateLookupImpl(Stories_SynopsesHooksTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public int getRankOrdinal(int ordinal) {
        return typeAPI.getRankOrdinal(ordinal);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public Stories_SynopsesHooksTypeAPI getTypeAPI() {
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