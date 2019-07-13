package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoTypeDescriptorDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoTypeDescriptorDelegate {

    private final String countryCode;
    private final int countryCodeOrdinal;
    private final String copyright;
    private final int copyrightOrdinal;
    private final String tierType;
    private final int tierTypeOrdinal;
    private final Boolean original;
    private final int mediaOrdinal;
    private final Boolean extended;
    private VideoTypeDescriptorTypeAPI typeAPI;

    public VideoTypeDescriptorDelegateCachedImpl(VideoTypeDescriptorTypeAPI typeAPI, int ordinal) {
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        int countryCodeTempOrdinal = countryCodeOrdinal;
        this.countryCode = countryCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(countryCodeTempOrdinal);
        this.copyrightOrdinal = typeAPI.getCopyrightOrdinal(ordinal);
        int copyrightTempOrdinal = copyrightOrdinal;
        this.copyright = copyrightTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(copyrightTempOrdinal);
        this.tierTypeOrdinal = typeAPI.getTierTypeOrdinal(ordinal);
        int tierTypeTempOrdinal = tierTypeOrdinal;
        this.tierType = tierTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(tierTypeTempOrdinal);
        this.original = typeAPI.getOriginalBoxed(ordinal);
        this.mediaOrdinal = typeAPI.getMediaOrdinal(ordinal);
        this.extended = typeAPI.getExtendedBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getCountryCode(int ordinal) {
        return countryCode;
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return countryCode == null;
        return testValue.equals(countryCode);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public String getCopyright(int ordinal) {
        return copyright;
    }

    public boolean isCopyrightEqual(int ordinal, String testValue) {
        if(testValue == null)
            return copyright == null;
        return testValue.equals(copyright);
    }

    public int getCopyrightOrdinal(int ordinal) {
        return copyrightOrdinal;
    }

    public String getTierType(int ordinal) {
        return tierType;
    }

    public boolean isTierTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return tierType == null;
        return testValue.equals(tierType);
    }

    public int getTierTypeOrdinal(int ordinal) {
        return tierTypeOrdinal;
    }

    public boolean getOriginal(int ordinal) {
        if(original == null)
            return false;
        return original.booleanValue();
    }

    public Boolean getOriginalBoxed(int ordinal) {
        return original;
    }

    public int getMediaOrdinal(int ordinal) {
        return mediaOrdinal;
    }

    public boolean getExtended(int ordinal) {
        if(extended == null)
            return false;
        return extended.booleanValue();
    }

    public Boolean getExtendedBoxed(int ordinal) {
        return extended;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoTypeDescriptorTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoTypeDescriptorTypeAPI) typeAPI;
    }

}