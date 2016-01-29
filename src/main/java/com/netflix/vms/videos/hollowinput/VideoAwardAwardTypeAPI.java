package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoAwardAwardTypeAPI extends HollowObjectTypeAPI {

    private final VideoAwardAwardDelegateLookupImpl delegateLookupImpl;

    VideoAwardAwardTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "awardId",
            "sequenceNumber",
            "winner",
            "year",
            "personId"
        });
        this.delegateLookupImpl = new VideoAwardAwardDelegateLookupImpl(this);
    }

    public long getAwardId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoAwardAward", ordinal, "awardId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getAwardIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoAwardAward", ordinal, "awardId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoAwardAward", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoAwardAward", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getWinner(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("VideoAwardAward", ordinal, "winner") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]) == Boolean.TRUE;
    }

    public Boolean getWinnerBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("VideoAwardAward", ordinal, "winner");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public long getYear(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoAwardAward", ordinal, "year");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getYearBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoAwardAward", ordinal, "year");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPersonId(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("VideoAwardAward", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("VideoAwardAward", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoAwardAwardDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}