package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CinderCupTokenRecordDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CinderCupTokenRecordDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final Long dealId;
    private final int dealIdOrdinal;
    private final String cupTokenId;
    private final int cupTokenIdOrdinal;
    private CinderCupTokenRecordTypeAPI typeAPI;

    public CinderCupTokenRecordDelegateCachedImpl(CinderCupTokenRecordTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(movieIdTempOrdinal);
        this.dealIdOrdinal = typeAPI.getDealIdOrdinal(ordinal);
        int dealIdTempOrdinal = dealIdOrdinal;
        this.dealId = dealIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(dealIdTempOrdinal);
        this.cupTokenIdOrdinal = typeAPI.getCupTokenIdOrdinal(ordinal);
        int cupTokenIdTempOrdinal = cupTokenIdOrdinal;
        this.cupTokenId = cupTokenIdTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(cupTokenIdTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public long getDealId(int ordinal) {
        if(dealId == null)
            return Long.MIN_VALUE;
        return dealId.longValue();
    }

    public Long getDealIdBoxed(int ordinal) {
        return dealId;
    }

    public int getDealIdOrdinal(int ordinal) {
        return dealIdOrdinal;
    }

    public String getCupTokenId(int ordinal) {
        return cupTokenId;
    }

    public boolean isCupTokenIdEqual(int ordinal, String testValue) {
        if(testValue == null)
            return cupTokenId == null;
        return testValue.equals(cupTokenId);
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