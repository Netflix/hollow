package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseTrailerTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseTrailerDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseTrailerTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sequenceNumber",
            "trailerMovieId",
            "supplementalInfo"
        });
        this.delegateLookupImpl = new RolloutPhaseTrailerDelegateLookupImpl(this);
    }

    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhaseTrailer", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseTrailer", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getTrailerMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RolloutPhaseTrailer", ordinal, "trailerMovieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getTrailerMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseTrailer", ordinal, "trailerMovieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSupplementalInfoOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseTrailer", ordinal, "supplementalInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI getSupplementalInfoTypeAPI() {
        return getAPI().getRolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI();
    }

    public RolloutPhaseTrailerDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}