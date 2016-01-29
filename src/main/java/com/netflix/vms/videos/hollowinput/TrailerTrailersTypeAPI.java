package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class TrailerTrailersTypeAPI extends HollowObjectTypeAPI {

    private final TrailerTrailersDelegateLookupImpl delegateLookupImpl;

    TrailerTrailersTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "themes",
            "sequenceNumber",
            "identifier",
            "postPlay",
            "movieId",
            "subType",
            "aspectRatio"
        });
        this.delegateLookupImpl = new TrailerTrailersDelegateLookupImpl(this);
    }

    public int getThemesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TrailerTrailers", ordinal, "themes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public TrailerTrailersArrayOfThemesTypeAPI getThemesTypeAPI() {
        return getAPI().getTrailerTrailersArrayOfThemesTypeAPI();
    }

    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("TrailerTrailers", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("TrailerTrailers", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getIdentifierOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("TrailerTrailers", ordinal, "identifier");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getIdentifierTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPostPlayOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("TrailerTrailers", ordinal, "postPlay");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getPostPlayTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("TrailerTrailers", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("TrailerTrailers", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSubTypeOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("TrailerTrailers", ordinal, "subType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getSubTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAspectRatioOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("TrailerTrailers", ordinal, "aspectRatio");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getAspectRatioTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public TrailerTrailersDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}