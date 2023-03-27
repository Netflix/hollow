package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AwardDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AwardDelegate {

    private final Long id;
    private final int winnerOrdinal;
    private final int nomineesOrdinal;
    private AwardTypeAPI typeAPI;

    public AwardDelegateCachedImpl(AwardTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.winnerOrdinal = typeAPI.getWinnerOrdinal(ordinal);
        this.nomineesOrdinal = typeAPI.getNomineesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        if(id == null)
            return Long.MIN_VALUE;
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public int getWinnerOrdinal(int ordinal) {
        return winnerOrdinal;
    }

    public int getNomineesOrdinal(int ordinal) {
        return nomineesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AwardTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AwardTypeAPI) typeAPI;
    }

}