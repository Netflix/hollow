package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoTypeTypeTypeAPI extends HollowObjectTypeAPI {

    private final VideoTypeTypeDelegateLookupImpl delegateLookupImpl;

    VideoTypeTypeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "isOriginal",
            "showMemberTypeId",
            "copyright",
            "countryCode",
            "isContentApproved",
            "media",
            "isCanon",
            "isExtended"
        });
        this.delegateLookupImpl = new VideoTypeTypeDelegateLookupImpl(this);
    }

    public boolean getIsOriginal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isOriginal") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getIsOriginalBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isOriginal");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public long getShowMemberTypeId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoTypeType", ordinal, "showMemberTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getShowMemberTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoTypeType", ordinal, "showMemberTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCopyrightOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeType", ordinal, "copyright");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCopyrightTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeType", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getIsContentApproved(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isContentApproved") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]) == Boolean.TRUE;
    }

    public Boolean getIsContentApprovedBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isContentApproved");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public int getMediaOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeType", ordinal, "media");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public VideoTypeTypeArrayOfMediaTypeAPI getMediaTypeAPI() {
        return getAPI().getVideoTypeTypeArrayOfMediaTypeAPI();
    }

    public boolean getIsCanon(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isCanon") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]) == Boolean.TRUE;
    }

    public Boolean getIsCanonBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isCanon");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public boolean getIsExtended(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isExtended") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]) == Boolean.TRUE;
    }

    public Boolean getIsExtendedBoxed(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("VideoTypeType", ordinal, "isExtended");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]);
    }



    public VideoTypeTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}