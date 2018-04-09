package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class CinderCupTokenRecordDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CinderCupTokenRecordDelegate {

    private final int movieIdOrdinal;
    private final int contractIdOrdinal;
    private final int cupTokenIdOrdinal;
    private CinderCupTokenRecordTypeAPI typeAPI;

    public CinderCupTokenRecordDelegateCachedImpl(CinderCupTokenRecordTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        this.contractIdOrdinal = typeAPI.getContractIdOrdinal(ordinal);
        this.cupTokenIdOrdinal = typeAPI.getCupTokenIdOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public int getContractIdOrdinal(int ordinal) {
        return contractIdOrdinal;
    }

    public int getCupTokenIdOrdinal(int ordinal) {
        return cupTokenIdOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CinderCupTokenRecordTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CinderCupTokenRecordTypeAPI) typeAPI;
    }

}