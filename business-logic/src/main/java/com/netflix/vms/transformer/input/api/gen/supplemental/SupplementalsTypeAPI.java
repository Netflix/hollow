package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class SupplementalsTypeAPI extends HollowObjectTypeAPI {

    private final SupplementalsDelegateLookupImpl delegateLookupImpl;

    public SupplementalsTypeAPI(SupplementalAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "supplementals"
        });
        this.delegateLookupImpl = new SupplementalsDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Supplementals", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Supplementals", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSupplementalsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Supplementals", ordinal, "supplementals");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public SupplementalsListTypeAPI getSupplementalsTypeAPI() {
        return getAPI().getSupplementalsListTypeAPI();
    }

    public SupplementalsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public SupplementalAPI getAPI() {
        return (SupplementalAPI) api;
    }

}