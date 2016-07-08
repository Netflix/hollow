package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoRightsFlagsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsFlagsDelegateLookupImpl delegateLookupImpl;

    VideoRightsFlagsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "searchOnly",
            "localText",
            "languageOverride",
            "localAudio",
            "goLive",
            "autoPlay",
            "firstDisplayDate",
            "firstDisplayDates"
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



    public boolean getGoLive(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "goLive") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]) == Boolean.TRUE;
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "goLive");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public boolean getAutoPlay(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "autoPlay") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]) == Boolean.TRUE;
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("VideoRightsFlags", ordinal, "autoPlay");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public int getFirstDisplayDateOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsFlags", ordinal, "firstDisplayDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DateTypeAPI getFirstDisplayDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsFlags", ordinal, "firstDisplayDates");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public MapOfFirstDisplayDatesTypeAPI getFirstDisplayDatesTypeAPI() {
        return getAPI().getMapOfFirstDisplayDatesTypeAPI();
    }

    public VideoRightsFlagsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}