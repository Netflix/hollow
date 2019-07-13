package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoTypeDescriptorDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoTypeDescriptorDelegate {

    private final VideoTypeDescriptorTypeAPI typeAPI;

    public VideoTypeDescriptorDelegateLookupImpl(VideoTypeDescriptorTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getCountryCode(int ordinal) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public String getCopyright(int ordinal) {
        ordinal = typeAPI.getCopyrightOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCopyrightEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCopyrightOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCopyrightOrdinal(int ordinal) {
        return typeAPI.getCopyrightOrdinal(ordinal);
    }

    public String getTierType(int ordinal) {
        ordinal = typeAPI.getTierTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isTierTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getTierTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
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