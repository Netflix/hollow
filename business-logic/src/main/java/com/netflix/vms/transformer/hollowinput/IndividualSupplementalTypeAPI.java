package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class IndividualSupplementalTypeAPI extends HollowObjectTypeAPI {

    private final IndividualSupplementalDelegateLookupImpl delegateLookupImpl;

    IndividualSupplementalTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "identifier",
            "movieId",
            "sequenceNumber",
            "passthrough"
        });
        this.delegateLookupImpl = new IndividualSupplementalDelegateLookupImpl(this);
    }

    public int getIdentifierOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualSupplemental", ordinal, "identifier");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getIdentifierTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("IndividualSupplemental", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("IndividualSupplemental", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("IndividualSupplemental", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("IndividualSupplemental", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getPassthroughOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualSupplemental", ordinal, "passthrough");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public PassthroughDataTypeAPI getPassthroughTypeAPI() {
        return getAPI().getPassthroughDataTypeAPI();
    }

    public IndividualSupplementalDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}