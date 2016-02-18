package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoTypeDescriptorDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoTypeDescriptorDelegate {

    private final VideoTypeDescriptorTypeAPI typeAPI;

    public VideoTypeDescriptorDelegateLookupImpl(VideoTypeDescriptorTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public boolean getIsOriginal(int ordinal) {
        return typeAPI.getIsOriginal(ordinal);
    }

    public Boolean getIsOriginalBoxed(int ordinal) {
        return typeAPI.getIsOriginalBoxed(ordinal);
    }

    public long getShowMemberTypeId(int ordinal) {
        return typeAPI.getShowMemberTypeId(ordinal);
    }

    public Long getShowMemberTypeIdBoxed(int ordinal) {
        return typeAPI.getShowMemberTypeIdBoxed(ordinal);
    }

    public int getCopyrightOrdinal(int ordinal) {
        return typeAPI.getCopyrightOrdinal(ordinal);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public boolean getIsContentApproved(int ordinal) {
        return typeAPI.getIsContentApproved(ordinal);
    }

    public Boolean getIsContentApprovedBoxed(int ordinal) {
        return typeAPI.getIsContentApprovedBoxed(ordinal);
    }

    public int getMediaOrdinal(int ordinal) {
        return typeAPI.getMediaOrdinal(ordinal);
    }

    public boolean getIsCanon(int ordinal) {
        return typeAPI.getIsCanon(ordinal);
    }

    public Boolean getIsCanonBoxed(int ordinal) {
        return typeAPI.getIsCanonBoxed(ordinal);
    }

    public boolean getIsExtended(int ordinal) {
        return typeAPI.getIsExtended(ordinal);
    }

    public Boolean getIsExtendedBoxed(int ordinal) {
        return typeAPI.getIsExtendedBoxed(ordinal);
    }

    public VideoTypeDescriptorTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}