package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoGeneralDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public boolean getTv(int ordinal);

    public Boolean getTvBoxed(int ordinal);

    public int getAliasesOrdinal(int ordinal);

    public String getVideoType(int ordinal);

    public boolean isVideoTypeEqual(int ordinal, String testValue);

    public int getVideoTypeOrdinal(int ordinal);

    public int getRuntime(int ordinal);

    public Integer getRuntimeBoxed(int ordinal);

    public String getSupplementalSubType(int ordinal);

    public boolean isSupplementalSubTypeEqual(int ordinal, String testValue);

    public int getSupplementalSubTypeOrdinal(int ordinal);

    public int getFirstReleaseYear(int ordinal);

    public Integer getFirstReleaseYearBoxed(int ordinal);

    public boolean getTestTitle(int ordinal);

    public Boolean getTestTitleBoxed(int ordinal);

    public String getOriginalLanguageBcpCode(int ordinal);

    public boolean isOriginalLanguageBcpCodeEqual(int ordinal, String testValue);

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal);

    public int getMetadataReleaseDays(int ordinal);

    public Integer getMetadataReleaseDaysBoxed(int ordinal);

    public String getOriginCountryCode(int ordinal);

    public boolean isOriginCountryCodeEqual(int ordinal, String testValue);

    public int getOriginCountryCodeOrdinal(int ordinal);

    public String getOriginalTitle(int ordinal);

    public boolean isOriginalTitleEqual(int ordinal, String testValue);

    public int getOriginalTitleOrdinal(int ordinal);

    public int getTestTitleTypesOrdinal(int ordinal);

    public String getOriginalTitleBcpCode(int ordinal);

    public boolean isOriginalTitleBcpCodeEqual(int ordinal, String testValue);

    public int getOriginalTitleBcpCodeOrdinal(int ordinal);

    public String getInternalTitle(int ordinal);

    public boolean isInternalTitleEqual(int ordinal, String testValue);

    public int getInternalTitleOrdinal(int ordinal);

    public int getEpisodeTypesOrdinal(int ordinal);

    public int getRegulatoryAdvisoriesOrdinal(int ordinal);

    public boolean getActive(int ordinal);

    public Boolean getActiveBoxed(int ordinal);

    public int getDisplayRuntime(int ordinal);

    public Integer getDisplayRuntimeBoxed(int ordinal);

    public int getInteractiveDataOrdinal(int ordinal);

    public VideoGeneralTypeAPI getTypeAPI();

}