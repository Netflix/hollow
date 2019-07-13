package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CinderCupTokenRecordDelegateLookupImpl extends HollowObjectAbstractDelegate implements CinderCupTokenRecordDelegate {

    private final CinderCupTokenRecordTypeAPI typeAPI;

    public CinderCupTokenRecordDelegateLookupImpl(CinderCupTokenRecordTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getMovieIdOrdinal(int ordinal) {
        return typeAPI.getMovieIdOrdinal(ordinal);
    }

    public long getDealId(int ordinal) {
        ordinal = typeAPI.getDealIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getDealIdBoxed(int ordinal) {
        ordinal = typeAPI.getDealIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getDealIdOrdinal(int ordinal) {
        return typeAPI.getDealIdOrdinal(ordinal);
    }

    public String getCupTokenId(int ordinal) {
        ordinal = typeAPI.getCupTokenIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCupTokenIdEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCupTokenIdOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
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