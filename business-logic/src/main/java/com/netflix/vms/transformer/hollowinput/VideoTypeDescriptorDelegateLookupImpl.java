package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoTypeDescriptorDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoTypeDescriptorDelegate {

    private final VideoTypeDescriptorTypeAPI typeAPI;

    public VideoTypeDescriptorDelegateLookupImpl(VideoTypeDescriptorTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public int getCopyrightOrdinal(int ordinal) {
        return typeAPI.getCopyrightOrdinal(ordinal);
    }

    public int getTierTypeOrdinal(int ordinal) {
        return typeAPI.getTierTypeOrdinal(ordinal);
    }

    public boolean getOriginal(int ordinal) {
        return typeAPI.getOriginal(ordinal);
    }

    public Boolean getOriginalBoxed(int ordinal) {
        return typeAPI.getOriginalBoxed(ordinal);
    }

    public int getMediaOrdinal(int ordinal) {
        return typeAPI.getMediaOrdinal(ordinal);
    }

    public boolean getExtended(int ordinal) {
        return typeAPI.getExtended(ordinal);
    }

    public Boolean getExtendedBoxed(int ordinal) {
        return typeAPI.getExtendedBoxed(ordinal);
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