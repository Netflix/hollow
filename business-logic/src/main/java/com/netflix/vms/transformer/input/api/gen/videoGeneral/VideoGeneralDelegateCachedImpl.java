package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoGeneralDelegate {

    private final Long videoId;
    private final Boolean tv;
    private final int aliasesOrdinal;
    private final String videoType;
    private final int videoTypeOrdinal;
    private final Integer runtime;
    private final String supplementalSubType;
    private final int supplementalSubTypeOrdinal;
    private final Integer firstReleaseYear;
    private final Boolean testTitle;
    private final String originalLanguageBcpCode;
    private final int originalLanguageBcpCodeOrdinal;
    private final Integer metadataReleaseDays;
    private final String originCountryCode;
    private final int originCountryCodeOrdinal;
    private final String originalTitle;
    private final int originalTitleOrdinal;
    private final int testTitleTypesOrdinal;
    private final String originalTitleBcpCode;
    private final int originalTitleBcpCodeOrdinal;
    private final String internalTitle;
    private final int internalTitleOrdinal;
    private final int episodeTypesOrdinal;
    private final int regulatoryAdvisoriesOrdinal;
    private final Boolean active;
    private final Integer displayRuntime;
    private final int interactiveDataOrdinal;
    private VideoGeneralTypeAPI typeAPI;

    public VideoGeneralDelegateCachedImpl(VideoGeneralTypeAPI typeAPI, int ordinal) {
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.tv = typeAPI.getTvBoxed(ordinal);
        this.aliasesOrdinal = typeAPI.getAliasesOrdinal(ordinal);
        this.videoTypeOrdinal = typeAPI.getVideoTypeOrdinal(ordinal);
        int videoTypeTempOrdinal = videoTypeOrdinal;
        this.videoType = videoTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(videoTypeTempOrdinal);
        this.runtime = typeAPI.getRuntimeBoxed(ordinal);
        this.supplementalSubTypeOrdinal = typeAPI.getSupplementalSubTypeOrdinal(ordinal);
        int supplementalSubTypeTempOrdinal = supplementalSubTypeOrdinal;
        this.supplementalSubType = supplementalSubTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(supplementalSubTypeTempOrdinal);
        this.firstReleaseYear = typeAPI.getFirstReleaseYearBoxed(ordinal);
        this.testTitle = typeAPI.getTestTitleBoxed(ordinal);
        this.originalLanguageBcpCodeOrdinal = typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
        int originalLanguageBcpCodeTempOrdinal = originalLanguageBcpCodeOrdinal;
        this.originalLanguageBcpCode = originalLanguageBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(originalLanguageBcpCodeTempOrdinal);
        this.metadataReleaseDays = typeAPI.getMetadataReleaseDaysBoxed(ordinal);
        this.originCountryCodeOrdinal = typeAPI.getOriginCountryCodeOrdinal(ordinal);
        int originCountryCodeTempOrdinal = originCountryCodeOrdinal;
        this.originCountryCode = originCountryCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(originCountryCodeTempOrdinal);
        this.originalTitleOrdinal = typeAPI.getOriginalTitleOrdinal(ordinal);
        int originalTitleTempOrdinal = originalTitleOrdinal;
        this.originalTitle = originalTitleTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(originalTitleTempOrdinal);
        this.testTitleTypesOrdinal = typeAPI.getTestTitleTypesOrdinal(ordinal);
        this.originalTitleBcpCodeOrdinal = typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
        int originalTitleBcpCodeTempOrdinal = originalTitleBcpCodeOrdinal;
        this.originalTitleBcpCode = originalTitleBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(originalTitleBcpCodeTempOrdinal);
        this.internalTitleOrdinal = typeAPI.getInternalTitleOrdinal(ordinal);
        int internalTitleTempOrdinal = internalTitleOrdinal;
        this.internalTitle = internalTitleTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(internalTitleTempOrdinal);
        this.episodeTypesOrdinal = typeAPI.getEpisodeTypesOrdinal(ordinal);
        this.regulatoryAdvisoriesOrdinal = typeAPI.getRegulatoryAdvisoriesOrdinal(ordinal);
        this.active = typeAPI.getActiveBoxed(ordinal);
        this.displayRuntime = typeAPI.getDisplayRuntimeBoxed(ordinal);
        this.interactiveDataOrdinal = typeAPI.getInteractiveDataOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        if(videoId == null)
            return Long.MIN_VALUE;
        return videoId.longValue();
    }

    public Long getVideoIdBoxed(int ordinal) {
        return videoId;
    }

    public boolean getTv(int ordinal) {
        if(tv == null)
            return false;
        return tv.booleanValue();
    }

    public Boolean getTvBoxed(int ordinal) {
        return tv;
    }

    public int getAliasesOrdinal(int ordinal) {
        return aliasesOrdinal;
    }

    public String getVideoType(int ordinal) {
        return videoType;
    }

    public boolean isVideoTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return videoType == null;
        return testValue.equals(videoType);
    }

    public int getVideoTypeOrdinal(int ordinal) {
        return videoTypeOrdinal;
    }

    public int getRuntime(int ordinal) {
        if(runtime == null)
            return Integer.MIN_VALUE;
        return runtime.intValue();
    }

    public Integer getRuntimeBoxed(int ordinal) {
        return runtime;
    }

    public String getSupplementalSubType(int ordinal) {
        return supplementalSubType;
    }

    public boolean isSupplementalSubTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return supplementalSubType == null;
        return testValue.equals(supplementalSubType);
    }

    public int getSupplementalSubTypeOrdinal(int ordinal) {
        return supplementalSubTypeOrdinal;
    }

    public int getFirstReleaseYear(int ordinal) {
        if(firstReleaseYear == null)
            return Integer.MIN_VALUE;
        return firstReleaseYear.intValue();
    }

    public Integer getFirstReleaseYearBoxed(int ordinal) {
        return firstReleaseYear;
    }

    public boolean getTestTitle(int ordinal) {
        if(testTitle == null)
            return false;
        return testTitle.booleanValue();
    }

    public Boolean getTestTitleBoxed(int ordinal) {
        return testTitle;
    }

    public String getOriginalLanguageBcpCode(int ordinal) {
        return originalLanguageBcpCode;
    }

    public boolean isOriginalLanguageBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalLanguageBcpCode == null;
        return testValue.equals(originalLanguageBcpCode);
    }

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal) {
        return originalLanguageBcpCodeOrdinal;
    }

    public int getMetadataReleaseDays(int ordinal) {
        if(metadataReleaseDays == null)
            return Integer.MIN_VALUE;
        return metadataReleaseDays.intValue();
    }

    public Integer getMetadataReleaseDaysBoxed(int ordinal) {
        return metadataReleaseDays;
    }

    public String getOriginCountryCode(int ordinal) {
        return originCountryCode;
    }

    public boolean isOriginCountryCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originCountryCode == null;
        return testValue.equals(originCountryCode);
    }

    public int getOriginCountryCodeOrdinal(int ordinal) {
        return originCountryCodeOrdinal;
    }

    public String getOriginalTitle(int ordinal) {
        return originalTitle;
    }

    public boolean isOriginalTitleEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalTitle == null;
        return testValue.equals(originalTitle);
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        return originalTitleOrdinal;
    }

    public int getTestTitleTypesOrdinal(int ordinal) {
        return testTitleTypesOrdinal;
    }

    public String getOriginalTitleBcpCode(int ordinal) {
        return originalTitleBcpCode;
    }

    public boolean isOriginalTitleBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalTitleBcpCode == null;
        return testValue.equals(originalTitleBcpCode);
    }

    public int getOriginalTitleBcpCodeOrdinal(int ordinal) {
        return originalTitleBcpCodeOrdinal;
    }

    public String getInternalTitle(int ordinal) {
        return internalTitle;
    }

    public boolean isInternalTitleEqual(int ordinal, String testValue) {
        if(testValue == null)
            return internalTitle == null;
        return testValue.equals(internalTitle);
    }

    public int getInternalTitleOrdinal(int ordinal) {
        return internalTitleOrdinal;
    }

    public int getEpisodeTypesOrdinal(int ordinal) {
        return episodeTypesOrdinal;
    }

    public int getRegulatoryAdvisoriesOrdinal(int ordinal) {
        return regulatoryAdvisoriesOrdinal;
    }

    public boolean getActive(int ordinal) {
        if(active == null)
            return false;
        return active.booleanValue();
    }

    public Boolean getActiveBoxed(int ordinal) {
        return active;
    }

    public int getDisplayRuntime(int ordinal) {
        if(displayRuntime == null)
            return Integer.MIN_VALUE;
        return displayRuntime.intValue();
    }

    public Integer getDisplayRuntimeBoxed(int ordinal) {
        return displayRuntime;
    }

    public int getInteractiveDataOrdinal(int ordinal) {
        return interactiveDataOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoGeneralTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoGeneralTypeAPI) typeAPI;
    }

}