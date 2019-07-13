package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class CinderCupTokenRecordTypeAPI extends HollowObjectTypeAPI {

    private final CinderCupTokenRecordDelegateLookupImpl delegateLookupImpl;

    public CinderCupTokenRecordTypeAPI(CupTokenAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "dealId",
            "cupTokenId"
        });
        this.delegateLookupImpl = new CinderCupTokenRecordDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CinderCupTokenRecord", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public LongTypeAPI getMovieIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getDealIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CinderCupTokenRecord", ordinal, "dealId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public LongTypeAPI getDealIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getCupTokenIdOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CinderCupTokenRecord", ordinal, "cupTokenId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCupTokenIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CinderCupTokenRecordDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public CupTokenAPI getAPI() {
        return (CupTokenAPI) api;
    }

}