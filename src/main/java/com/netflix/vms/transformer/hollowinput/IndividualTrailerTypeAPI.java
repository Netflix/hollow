package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class IndividualTrailerTypeAPI extends HollowObjectTypeAPI {

    private final IndividualTrailerDelegateLookupImpl delegateLookupImpl;

    IndividualTrailerTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "identifier",
            "movieId",
            "sequenceNumber",
            "postPlay",
            "subType",
            "aspectRatio",
            "themes",
            "usages"
        });
        this.delegateLookupImpl = new IndividualTrailerDelegateLookupImpl(this);
    }

    public int getIdentifierOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualTrailer", ordinal, "identifier");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getIdentifierTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("IndividualTrailer", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("IndividualTrailer", ordinal, "movieId");
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
            return missingDataHandler().handleLong("IndividualTrailer", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("IndividualTrailer", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getPostPlayOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualTrailer", ordinal, "postPlay");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getPostPlayTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSubTypeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualTrailer", ordinal, "subType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getSubTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAspectRatioOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualTrailer", ordinal, "aspectRatio");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getAspectRatioTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getThemesOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualTrailer", ordinal, "themes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public ListOfStringTypeAPI getThemesTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public int getUsagesOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualTrailer", ordinal, "usages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public ListOfStringTypeAPI getUsagesTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public IndividualTrailerDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}