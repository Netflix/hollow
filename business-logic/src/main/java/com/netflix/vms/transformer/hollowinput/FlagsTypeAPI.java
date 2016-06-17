package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class FlagsTypeAPI extends HollowObjectTypeAPI {

    private final FlagsDelegateLookupImpl delegateLookupImpl;

    FlagsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "searchOnly",
            "localText",
            "languageOverride",
            "localAudio",
            "firstDisplayDates",
            "goLive",
            "contentApproved",
            "autoPlay",
            "firstDisplayDate"
        });
        this.delegateLookupImpl = new FlagsDelegateLookupImpl(this);
    }

    public boolean getSearchOnly(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnly") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getSearchOnlyBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnly");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getLocalText(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localText") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getLocalTextBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localText");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public boolean getLanguageOverride(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "languageOverride") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]) == Boolean.TRUE;
    }

    public Boolean getLanguageOverrideBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "languageOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public boolean getLocalAudio(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localAudio") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]) == Boolean.TRUE;
    }

    public Boolean getLocalAudioBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localAudio");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public int getFirstDisplayDatesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "firstDisplayDates");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public MapOfFlagsFirstDisplayDatesTypeAPI getFirstDisplayDatesTypeAPI() {
        return getAPI().getMapOfFlagsFirstDisplayDatesTypeAPI();
    }

    public boolean getGoLive(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLive") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]) == Boolean.TRUE;
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLive");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public boolean getContentApproved(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "contentApproved") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]) == Boolean.TRUE;
    }

    public Boolean getContentApprovedBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "contentApproved");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public boolean getAutoPlay(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "autoPlay") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]) == Boolean.TRUE;
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "autoPlay");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]);
    }



    public long getFirstDisplayDate(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleLong("Flags", ordinal, "firstDisplayDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
    }

    public Long getFirstDisplayDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[8] == -1) {
            l = missingDataHandler().handleLong("Flags", ordinal, "firstDisplayDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[8]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public FlagsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}