package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CinderCupTokenRecordDelegateLookupImpl extends HollowObjectAbstractDelegate implements CinderCupTokenRecordDelegate {

    private final CinderCupTokenRecordTypeAPI typeAPI;

    public CinderCupTokenRecordDelegateLookupImpl(CinderCupTokenRecordTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return typeAPI.getMovieIdOrdinal(ordinal);
    }

    public int getContractIdOrdinal(int ordinal) {
        return typeAPI.getContractIdOrdinal(ordinal);
    }

    public int getCupTokenIdOrdinal(int ordinal) {
        return typeAPI.getCupTokenIdOrdinal(ordinal);
    }

    public CinderCupTokenRecordTypeAPI getTypeAPI() {
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