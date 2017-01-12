package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoAwardMappingTypeAPI extends HollowObjectTypeAPI {

    private final VideoAwardMappingDelegateLookupImpl delegateLookupImpl;

    VideoAwardMappingTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "awardId",
            "sequenceNumber",
            "year",
            "personId",
            "winner"
        });
        this.delegateLookupImpl = new VideoAwardMappingDelegateLookupImpl(this);
    }

    public long getAwardId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoAwardMapping", ordinal, "awardId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getAwardIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoAwardMapping", ordinal, "awardId");
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
            return missingDataHandler().handleLong("VideoAwardMapping", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoAwardMapping", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getYear(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoAwardMapping", ordinal, "year");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getYearBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoAwardMapping", ordinal, "year");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPersonId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoAwardMapping", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoAwardMapping", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getWinner(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoAwardMapping", ordinal, "winner") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]) == Boolean.TRUE;
    }

    public Boolean getWinnerBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoAwardMapping", ordinal, "winner");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public VideoAwardMappingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}