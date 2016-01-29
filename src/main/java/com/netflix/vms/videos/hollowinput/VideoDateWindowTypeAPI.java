package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoDateWindowTypeAPI extends HollowObjectTypeAPI {

    private final VideoDateWindowDelegateLookupImpl delegateLookupImpl;

    VideoDateWindowTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "isTheatricalRelease",
            "countryCode",
            "streetDate",
            "theatricalReleaseYear",
            "theatricalReleaseDate"
        });
        this.delegateLookupImpl = new VideoDateWindowDelegateLookupImpl(this);
    }

    public boolean getIsTheatricalRelease(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoDateWindow", ordinal, "isTheatricalRelease") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getIsTheatricalReleaseBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoDateWindow", ordinal, "isTheatricalRelease");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoDateWindow", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getStreetDate(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoDateWindow", ordinal, "streetDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getStreetDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoDateWindow", ordinal, "streetDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getTheatricalReleaseYear(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoDateWindow", ordinal, "theatricalReleaseYear");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getTheatricalReleaseYearBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoDateWindow", ordinal, "theatricalReleaseYear");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getTheatricalReleaseDate(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("VideoDateWindow", ordinal, "theatricalReleaseDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getTheatricalReleaseDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("VideoDateWindow", ordinal, "theatricalReleaseDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoDateWindowDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}