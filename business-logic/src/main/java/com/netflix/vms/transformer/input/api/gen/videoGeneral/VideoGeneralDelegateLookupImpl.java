package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoGeneralDelegate {

    private final VideoGeneralTypeAPI typeAPI;

    public VideoGeneralDelegateLookupImpl(VideoGeneralTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public boolean getTv(int ordinal) {
        return typeAPI.getTv(ordinal);
    }

    public Boolean getTvBoxed(int ordinal) {
        return typeAPI.getTvBoxed(ordinal);
    }

    public int getAliasesOrdinal(int ordinal) {
        return typeAPI.getAliasesOrdinal(ordinal);
    }

    public String getVideoType(int ordinal) {
        ordinal = typeAPI.getVideoTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isVideoTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getVideoTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getVideoTypeOrdinal(int ordinal) {
        return typeAPI.getVideoTypeOrdinal(ordinal);
    }

    public int getRuntime(int ordinal) {
        return typeAPI.getRuntime(ordinal);
    }

    public Integer getRuntimeBoxed(int ordinal) {
        return typeAPI.getRuntimeBoxed(ordinal);
    }

    public String getSupplementalSubType(int ordinal) {
        ordinal = typeAPI.getSupplementalSubTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isSupplementalSubTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getSupplementalSubTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getSupplementalSubTypeOrdinal(int ordinal) {
        return typeAPI.getSupplementalSubTypeOrdinal(ordinal);
    }

    public int getFirstReleaseYear(int ordinal) {
        return typeAPI.getFirstReleaseYear(ordinal);
    }

    public Integer getFirstReleaseYearBoxed(int ordinal) {
        return typeAPI.getFirstReleaseYearBoxed(ordinal);
    }

    public boolean getTestTitle(int ordinal) {
        return typeAPI.getTestTitle(ordinal);
    }

    public Boolean getTestTitleBoxed(int ordinal) {
        return typeAPI.getTestTitleBoxed(ordinal);
    }

    public String getOriginalLanguageBcpCode(int ordinal) {
        ordinal = typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalLanguageBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal) {
        return typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
    }

    public int getMetadataReleaseDays(int ordinal) {
        return typeAPI.getMetadataReleaseDays(ordinal);
    }

    public Integer getMetadataReleaseDaysBoxed(int ordinal) {
        return typeAPI.getMetadataReleaseDaysBoxed(ordinal);
    }

    public String getOriginCountryCode(int ordinal) {
        ordinal = typeAPI.getOriginCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isOriginCountryCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginCountryCodeOrdinal(int ordinal) {
        return typeAPI.getOriginCountryCodeOrdinal(ordinal);
    }

    public String getOriginalTitle(int ordinal) {
        ordinal = typeAPI.getOriginalTitleOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalTitleEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalTitleOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        return typeAPI.getOriginalTitleOrdinal(ordinal);
    }

    public int getTestTitleTypesOrdinal(int ordinal) {
        return typeAPI.getTestTitleTypesOrdinal(ordinal);
    }

    public String getOriginalTitleBcpCode(int ordinal) {
        ordinal = typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalTitleBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalTitleBcpCodeOrdinal(int ordinal) {
        return typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
    }

    public String getInternalTitle(int ordinal) {
        ordinal = typeAPI.getInternalTitleOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isInternalTitleEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getInternalTitleOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getInternalTitleOrdinal(int ordinal) {
        return typeAPI.getInternalTitleOrdinal(ordinal);
    }

    public int getEpisodeTypesOrdinal(int ordinal) {
        return typeAPI.getEpisodeTypesOrdinal(ordinal);
    }

    public int getRegulatoryAdvisoriesOrdinal(int ordinal) {
        return typeAPI.getRegulatoryAdvisoriesOrdinal(ordinal);
    }

    public boolean getActive(int ordinal) {
        return typeAPI.getActive(ordinal);
    }

    public Boolean getActiveBoxed(int ordinal) {
        return typeAPI.getActiveBoxed(ordinal);
    }

    public int getDisplayRuntime(int ordinal) {
        return typeAPI.getDisplayRuntime(ordinal);
    }

    public Integer getDisplayRuntimeBoxed(int ordinal) {
        return typeAPI.getDisplayRuntimeBoxed(ordinal);
    }

    public int getInteractiveDataOrdinal(int ordinal) {
        return typeAPI.getInteractiveDataOrdinal(ordinal);
    }

    public VideoGeneralTypeAPI getTypeAPI() {
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