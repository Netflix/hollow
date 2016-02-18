package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoTypeDescriptorTypeAPI extends HollowObjectTypeAPI {

    private final VideoTypeDescriptorDelegateLookupImpl delegateLookupImpl;

    VideoTypeDescriptorTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
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
        this.delegateLookupImpl = new VideoTypeDescriptorDelegateLookupImpl(this);
    }

    public boolean getIsOriginal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isOriginal") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getIsOriginalBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isOriginal");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public long getShowMemberTypeId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoTypeDescriptor", ordinal, "showMemberTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getShowMemberTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoTypeDescriptor", ordinal, "showMemberTypeId");
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
            return missingDataHandler().handleReferencedOrdinal("VideoTypeDescriptor", ordinal, "copyright");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCopyrightTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeDescriptor", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getIsContentApproved(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isContentApproved") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]) == Boolean.TRUE;
    }

    public Boolean getIsContentApprovedBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isContentApproved");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public int getMediaOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoTypeDescriptor", ordinal, "media");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public VideoTypeMediaListTypeAPI getMediaTypeAPI() {
        return getAPI().getVideoTypeMediaListTypeAPI();
    }

    public boolean getIsCanon(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isCanon") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]) == Boolean.TRUE;
    }

    public Boolean getIsCanonBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isCanon");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public boolean getIsExtended(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isExtended") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]) == Boolean.TRUE;
    }

    public Boolean getIsExtendedBoxed(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("VideoTypeDescriptor", ordinal, "isExtended");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]);
    }



    public VideoTypeDescriptorDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}