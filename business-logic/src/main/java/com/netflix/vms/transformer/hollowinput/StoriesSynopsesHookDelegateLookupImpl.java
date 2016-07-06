package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class StoriesSynopsesHookDelegateLookupImpl extends HollowObjectAbstractDelegate implements StoriesSynopsesHookDelegate {

    private final StoriesSynopsesHookTypeAPI typeAPI;

    public StoriesSynopsesHookDelegateLookupImpl(StoriesSynopsesHookTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public int getRankOrdinal(int ordinal) {
        return typeAPI.getRankOrdinal(ordinal);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public StoriesSynopsesHookTypeAPI getTypeAPI() {
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