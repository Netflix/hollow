package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsFlagsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsFlagsDelegateLookupImpl delegateLookupImpl;

    VideoRightsFlagsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "searchOnly",
            "localText",
            "languageOverride",
            "localAudio",
            "firstDisplayDates",
            "goLive",
            "autoPlay",
            "firstDisplayDate"
        });
        this.delegateLookupImpl = new VideoRightsFlagsDelegateLookupImpl(this);
    }

    public boolean getSearchOnly(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "searchOnly") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getSearchOnlyBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "searchOnly");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getLocalText(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "localText") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getLocalTextBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "localText");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public boolean getLanguageOverride(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "languageOverride") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]) == Boolean.TRUE;
    }

    public Boolean getLanguageOverrideBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "languageOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public boolean getLocalAudio(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "localAudio") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]) == Boolean.TRUE;
    }

    public Boolean getLocalAudioBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "localAudio");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public int getFirstDisplayDatesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsFlags", ordinal, "firstDisplayDates");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public VideoRightsFlagsMapOfFirstDisplayDatesTypeAPI getFirstDisplayDatesTypeAPI() {
        return getAPI().getVideoRightsFlagsMapOfFirstDisplayDatesTypeAPI();
    }

    public boolean getGoLive(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "goLive") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]) == Boolean.TRUE;
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "goLive");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public boolean getAutoPlay(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "autoPlay") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]) == Boolean.TRUE;
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "autoPlay");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public long getFirstDisplayDate(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleLong("VideoRightsFlags", ordinal, "firstDisplayDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[7]);
    }

    public Long getFirstDisplayDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[7] == -1) {
            l = missingDataHandler().handleLong("VideoRightsFlags", ordinal, "firstDisplayDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[7]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[7]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoRightsFlagsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}