package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhasesElementsTrailersSupplementalInfoTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhasesElementsTrailersSupplementalInfoDelegateLookupImpl delegateLookupImpl;

    RolloutPhasesElementsTrailersSupplementalInfoTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "imageBackgroundTone",
            "videoLength",
            "subtitleLocale",
            "seasonNumber",
            "video",
            "imageTag",
            "videoValue",
            "priority"
        });
        this.delegateLookupImpl = new RolloutPhasesElementsTrailersSupplementalInfoDelegateLookupImpl(this);
    }

    public int getImageBackgroundToneOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "imageBackgroundTone");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getImageBackgroundToneTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getVideoLength(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "videoLength");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getVideoLengthBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "videoLength");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSubtitleLocaleOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "subtitleLocale");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getSubtitleLocaleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getSeasonNumber(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "seasonNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getSeasonNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "seasonNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getVideoOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "video");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getVideoTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getImageTagOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "imageTag");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getImageTagTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getVideoValueOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "videoValue");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getVideoValueTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getPriority(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleLong("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "priority");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[7]);
    }

    public Long getPriorityBoxed(int ordinal) {
        long l;
        if(fieldIndex[7] == -1) {
            l = missingDataHandler().handleLong("RolloutPhasesElementsTrailersSupplementalInfo", ordinal, "priority");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[7]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[7]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public RolloutPhasesElementsTrailersSupplementalInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}