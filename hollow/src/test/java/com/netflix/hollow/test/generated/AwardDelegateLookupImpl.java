package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AwardDelegateLookupImpl extends HollowObjectAbstractDelegate implements AwardDelegate {

    private final AwardTypeAPI typeAPI;

    public AwardDelegateLookupImpl(AwardTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public int getWinnerOrdinal(int ordinal) {
        return typeAPI.getWinnerOrdinal(ordinal);
    }

    public int getNomineesOrdinal(int ordinal) {
        return typeAPI.getNomineesOrdinal(ordinal);
    }

    public AwardTypeAPI getTypeAPI() {
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